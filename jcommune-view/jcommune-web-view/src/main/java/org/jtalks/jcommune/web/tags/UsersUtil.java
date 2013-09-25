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
package org.jtalks.jcommune.web.tags;

import org.slf4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.jtalks.jcommune.model.entity.JCUser;
import org.slf4j.LoggerFactory;

/**
 * The class contains JSTL functions for the work with users.
 */
public class UsersUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersUtil.class);

    /**
     * The function checks - there is a user or not.
     *
     * @param user a user,
     * @return there is a user or not.
     */
    public static boolean isExists(JCUser user) {
        if (user != null) {
            try {
                return (user.getUsername() != null);
            } catch (ObjectNotFoundException ex) {
                LOGGER.warn("User does not exist.", ex);
                return false;
            }
        }
        return false;
    }

}
