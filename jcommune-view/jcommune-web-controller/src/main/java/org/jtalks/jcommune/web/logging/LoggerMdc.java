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
package org.jtalks.jcommune.web.logging;

import org.slf4j.MDC;

/**
 * Provides an ability to call methods from MDC without calling static methods. Allows to register a username in MDC so
 * that in each line of code we see a user that caused that action.
 *
 * @author Anuar_Nurmakanov
 */
public class LoggerMdc {
    //be careful, value must be the same as in logging pattern
    private static final String USER_NAME_KEY = "userName";

    /**
     * Registers user's name in MDC.
     *
     * @param userName user's name
     */
    public void registerUser(String userName) {
        MDC.put(USER_NAME_KEY, userName);
    }

    /** Unregister user from current thread. Does nothing if it wasn't registered */
    public void unregisterUser() {
        MDC.remove(USER_NAME_KEY);
    }
}
