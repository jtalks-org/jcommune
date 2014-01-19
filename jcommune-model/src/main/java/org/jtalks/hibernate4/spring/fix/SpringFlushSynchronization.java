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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

/**
 * Simple synchronization adapter that propagates a {@code flush()} call
 * to the underlying Hibernate Session. Used in combination with JTA.
 *
 * @author Juergen Hoeller
 * @since 3.1
 */
class SpringFlushSynchronization extends TransactionSynchronizationAdapter {

	private final Session session;


	public SpringFlushSynchronization(Session session) {
		this.session = session;
	}


	@Override
	public void flush() {
		try {
			SessionFactoryUtils.logger.debug("Flushing Hibernate Session on explicit request");
			this.session.flush();
		}
		catch (HibernateException ex) {
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}


	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SpringFlushSynchronization &&
				this.session == ((SpringFlushSynchronization) obj).session);
	}

	@Override
	public int hashCode() {
		return this.session.hashCode();
	}

}
