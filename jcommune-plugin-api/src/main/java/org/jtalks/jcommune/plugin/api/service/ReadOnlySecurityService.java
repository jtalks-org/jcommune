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
package org.jtalks.jcommune.plugin.api.service;

import org.jtalks.jcommune.model.entity.JCUser;

/**
 * This class allows get information about current user in plugins.
 *
 * @author Mikhail Stryzhonok
 */
public class ReadOnlySecurityService implements UserReader {
    private static ReadOnlySecurityService instance = new ReadOnlySecurityService();

    private UserReader userReader;

    public static UserReader getInstance() {
        return instance;
    }

    private ReadOnlySecurityService() {
    }

    public void setUserReader(UserReader userReader) {
        this.userReader = userReader;
    }

    /**
     * Gets copy of user currently logged in.
     * @return the copy of user currently logged in
     *         or <b>null</b> if user is anonymous
     */
    @Override
    public JCUser getCurrentUser() {
        JCUser currentUser = userReader.getCurrentUser();
        if (currentUser == null) {
            return null;
        } else {
            return JCUser.copyUser(currentUser);
        }
    }
}
