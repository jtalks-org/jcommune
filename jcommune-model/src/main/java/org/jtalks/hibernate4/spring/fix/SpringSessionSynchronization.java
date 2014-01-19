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

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronization;
import static org.springframework.transaction.support.TransactionSynchronization.STATUS_COMMITTED;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Callback for resource cleanup at the end of a Spring-managed transaction
 * for a pre-bound Hibernate Session.
 *
 * @author Juergen Hoeller
 * @since 3.1
 */
class SpringSessionSynchronization implements TransactionSynchronization, Ordered {

	private final SessionHolder sessionHolder;

	private final SessionFactory sessionFactory;

	private boolean holderActive = true;


	public SpringSessionSynchronization(SessionHolder sessionHolder, SessionFactory sessionFactory) {
		this.sessionHolder = sessionHolder;
		this.sessionFactory = sessionFactory;
	}

	private Session getCurrentSession() {
		return this.sessionHolder.getSession();
	}


	public int getOrder() {
		return SessionFactoryUtils.SESSION_SYNCHRONIZATION_ORDER;
	}

	public void suspend() {
		if (this.holderActive) {
			TransactionSynchronizationManager.unbindResource(this.sessionFactory);
			// Eagerly disconnect the Session here, to make release mode "on_close" work on JBoss.
			getCurrentSession().disconnect();
		}
	}

	public void resume() {
		if (this.holderActive) {
			TransactionSynchronizationManager.bindResource(this.sessionFactory, this.sessionHolder);
		}
	}

	public void flush() {
		try {
			SessionFactoryUtils.logger.debug("Flushing Hibernate Session on explicit request");
			getCurrentSession().flush();
		}
		catch (HibernateException ex) {
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void beforeCommit(boolean readOnly) throws DataAccessException {
		if (!readOnly) {
			Session session = getCurrentSession();
			// Read-write transaction -> flush the Hibernate Session.
			// Further check: only flush when not FlushMode.MANUAL.
			if (!FlushMode.isManualFlushMode(session.getFlushMode())) {
				try {
					SessionFactoryUtils.logger.debug("Flushing Hibernate Session on transaction synchronization");
					session.flush();
				}
				catch (HibernateException ex) {
					throw SessionFactoryUtils.convertHibernateAccessException(ex);
				}
			}
		}
	}

	public void beforeCompletion() {
		Session session = this.sessionHolder.getSession();
		if (this.sessionHolder.getPreviousFlushMode() != null) {
			// In case of pre-bound Session, restore previous flush mode.
			session.setFlushMode(this.sessionHolder.getPreviousFlushMode());
		}
		// Eagerly disconnect the Session here, to make release mode "on_close" work nicely.
		session.disconnect();
	}

	public void afterCommit() {
	}

	public void afterCompletion(int status) {
		try {
			if (status != STATUS_COMMITTED) {
				// Clear all pending inserts/updates/deletes in the Session.
				// Necessary for pre-bound Sessions, to avoid inconsistent state.
				this.sessionHolder.getSession().clear();
			}
		}
		finally {
			this.sessionHolder.setSynchronizedWithTransaction(false);
		}
	}

}
