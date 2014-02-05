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

package org.jtalks.jcommune.service.exceptions;

/**
 * Throws if user tries to activate account second time following the same activation link. At first it doesn't seem
 * important to deny second activation, but then
 * <a href="http://jira.jtalks.org/browse/JC-1184">auto-login during activation</a> was implemented which would allow
 * bad guys to sign in if they figure out the activation link of the user.
 *
 * @author Andrey Ivanov
 */
public class UserTriesActivatingAccountAgainException extends Exception {
    public UserTriesActivatingAccountAgainException() {
        super("User tried to activate his account for the second time which is not allowed.");
    }
}
