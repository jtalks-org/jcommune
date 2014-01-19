/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jtalks.hibernate4.spring.fix;

import javax.transaction.TransactionManager;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.orm.hibernate4.SpringJtaSessionContext;

import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Implementation of Hibernate 3.1's CurrentSessionContext interface
 * that delegates to Spring's SessionFactoryUtils for providing a
 * Spring-managed current Session.
 *
 * <p>This CurrentSessionContext implementation can also be specified in custom
 * SessionFactory setup through the "hibernate.current_session_context_class"
 * property, with the fully qualified name of this class as value.
 *
 * @author Juergen Hoeller
 * @since 3.1
 */
@SuppressWarnings("serial")
public class SpringSessionContext implements CurrentSessionContext {

	private final SessionFactoryImplementor sessionFactory;

	private final CurrentSessionContext jtaSessionContext;


	/**
	 * Create a new SpringSessionContext for the given Hibernate SessionFactory.
	 * @param sessionFactory the SessionFactory to provide current Sessions for
	 */
	public SpringSessionContext(SessionFactoryImplementor sessionFactory) {
		this.sessionFactory = sessionFactory;
		JtaPlatform jtaPlatform = sessionFactory.getServiceRegistry().getService(JtaPlatform.class);
		TransactionManager transactionManager = jtaPlatform.retrieveTransactionManager();
		this.jtaSessionContext = (transactionManager != null ? new SpringJtaSessionContext(sessionFactory) : null);
	}


	/**
	 * Retrieve the Spring-managed Session for the current thread, if any.
	 */
	public Session currentSession() throws HibernateException {
		Object value = TransactionSynchronizationManager.getResource(this.sessionFactory);
		if (value instanceof Session) {
			return (Session) value;
		}
		else if (value instanceof SessionHolder) {
			SessionHolder sessionHolder = (SessionHolder) value;
			Session session = sessionHolder.getSession();
			if (TransactionSynchronizationManager.isSynchronizationActive() &&
					!sessionHolder.isSynchronizedWithTransaction()) {
				TransactionSynchronizationManager.registerSynchronization(
						new SpringSessionSynchronization(sessionHolder, this.sessionFactory));
				sessionHolder.setSynchronizedWithTransaction(true);
				// Switch to FlushMode.AUTO, as we have to assume a thread-bound Session
				// with FlushMode.MANUAL, which needs to allow flushing within the transaction.
				FlushMode flushMode = session.getFlushMode();
				if (FlushMode.isManualFlushMode(flushMode) &&
						!TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
					session.setFlushMode(FlushMode.AUTO);
					sessionHolder.setPreviousFlushMode(flushMode);
				}
			}
			return session;
		}
		else if (this.jtaSessionContext != null) {
			Session session = this.jtaSessionContext.currentSession();
			if (TransactionSynchronizationManager.isSynchronizationActive()) {
				TransactionSynchronizationManager.registerSynchronization(new SpringFlushSynchronization(session));
			}
			return session;
		}
		else {
			throw new HibernateException("No Session found for current thread");
		}
	}

}
