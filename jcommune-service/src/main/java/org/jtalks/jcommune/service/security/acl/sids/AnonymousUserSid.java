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

import javax.annotation.Nullable;

/**
 * An implementation of the sid particularly for anonymous users. This class is package-private, use {@link
 * UserSid#isAnonymous(String)} and {@link UserSid#createAnonymous()} to work with
 * this class outside of the package.
 *
 * @author stanislav bashkirtsev
 */
class AnonymousUserSid extends UserSid {
    /**
     * This is the string representation of all the anonymous users in Spring Security.
     */
    public static final String ANONYMOUS_USER_NAME = "anonymousUser";
    /**
     * The sid id of all of the anonymous users is the constant.
     *
     * @see UniversalSid#getSidId()
     */
    public static final String ANONYMOUS_USER_SID_ID = SID_PREFIX + SID_NAME_SEPARATOR + ANONYMOUS_USER_NAME;
    /**
     * We don't need to create each time a new anonymous user since it always will be the same, so we can always work
     * with this instance.
     */
    private static final AnonymousUserSid ANONYMOUS_USER = new AnonymousUserSid();

    /**
     * Creates a sid with the sid id equals to {@link #ANONYMOUS_USER_SID_ID}.
     */
    private AnonymousUserSid() {
        super(ANONYMOUS_USER_SID_ID);
    }

    /**
     * Defines whether the specified string is the name of the anonymous user which is equal to {@link
     * #ANONYMOUS_USER_NAME}.
     *
     * @param principal the principal to understand whether it's an anonymous user or not
     * @return {@code true} if the specified principal is an anonymous user, {@code false} if it's {@code null} or
     *         doesn't equal to {@link #ANONYMOUS_USER_NAME}
     */
    public static boolean isAnonymous(@Nullable String principal) {
        return ANONYMOUS_USER_NAME.equals(principal);
    }

    /**
     * Returns all the time the same instance of the anonymous user. Since they shouldn't ever differ and this instance
     * is immutable, this is pretty enough.
     *
     * @return the instance of anonymous user
     */
    public static AnonymousUserSid create() {
        return ANONYMOUS_USER;
    }
}
