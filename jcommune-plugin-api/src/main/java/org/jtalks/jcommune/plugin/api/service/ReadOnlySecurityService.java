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
 * This class allows to get information about current user in plugins.
 *
 * @author Mikhail Stryzhonok
 */
public class ReadOnlySecurityService implements UserReader {
    private static final ReadOnlySecurityService INSTANCE = new ReadOnlySecurityService();
    private UserReader userReader;

    public static UserReader getInstance() {
        return INSTANCE;
    }

    /** Use {@link #getInstance()}, this class is singleton. */
    private ReadOnlySecurityService() {
    }

    /**
     * Is set once during spring beans initialization. Because in {@link #getInstance()} the interface is returned,
     * users of this API won't see this setter and won't be able to change this field.
     *
     * @param userReader a service that we wrap that actually has access to security and users
     */
    public void setUserReader(UserReader userReader) {
        this.userReader = userReader;
    }

    /**
     * Gets copy of user currently logged in. This is done so that Plugins won't be able to modify objects and
     * break core forum.
     *
     * @return the copy of user currently logged in or {@code null} if user is anonymous
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
