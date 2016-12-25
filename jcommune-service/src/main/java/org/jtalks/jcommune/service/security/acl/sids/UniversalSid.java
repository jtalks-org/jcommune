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

package org.jtalks.jcommune.service.security.acl.sids;

import org.springframework.security.acls.model.Sid;

/**
 * This interface is dedicated to join all the custom {@link Sid}s into one group that can be accessed with unified
 * method to obtain their identifier (it will be saved into the ACL table).
 * IMPORTANT: Every custom Sid should implement this interface
 * @author stanislav bashkirtsev
 */
public interface UniversalSid extends Sid {

    /**
     * Gets the unique identifier of the SID (usually a database ID of the entity). It is string since the ACL tables
     * require this.
     *
     * @return the unique identifier of the SID (usually a database ID of the entity)
     */
    boolean isPrincipal();

    public String getSidId();
    /**
     * All the custom Sids, when they implement the {@link UniversalSid} should obey some pattern since they are
     * saved as string to the DB. This pattern usually will be: some string identifier of the Sid implementation + ":" +
     * the database id of the entity which is a Sid. Like in case of user groups: {@code usergroup:2123}.
     */
    static final String SID_NAME_SEPARATOR = ":";

    /**
     * Since some of Sid implementations accept different arguments while creation, like String, and these arguments
     * have to be of some particular pattern, we need such exception that is thrown when the passed argument is of wrong
     * format.
     *
     * @author stanislav bashkirtsev
     */
    public static class WrongFormatException extends RuntimeException {
        /**
         * @param sidName the name of the sid (actually the value which is of wrong format)
         */
        public WrongFormatException(String sidName) {
            super("Sid name is of incorrect format: " + sidName);
        }
    }
}
