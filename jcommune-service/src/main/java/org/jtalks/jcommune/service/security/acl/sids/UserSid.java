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

import org.jtalks.jcommune.model.entity.UserInfo;
import org.springframework.security.acls.domain.PrincipalSid;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;

/**
 * @author stanislav bashkirstev
 */
public class UserSid extends PrincipalSid implements UniversalSid {
    public final static String SID_PREFIX = "user";
    private final String userId;

    public UserSid(@Nonnull String sidId) {
        super(sidId);
        this.userId = parseUserId(sidId);
    }

    public UserSid(@Nonnegative long userId) {
        super(SID_PREFIX + String.valueOf(userId));
        this.userId = String.valueOf(userId);
    }

    public UserSid(@Nonnull UserInfo user) {
        this(user.getId());
    }

    private String parseUserId(String sidId) {
        String[] splitted = sidId.split(Pattern.quote(":"));
        if (splitted.length != 2) {
            throw new WrongFormatException(sidId);
        }
        return splitted[1];
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String getPrincipal() {
        return getSidId();
    }

    /**
     * Defines whether the specified principal is an anonymous user (the one that's name is {@code anonymousUser}).
     *
     * @param principal the principal to decide whether it's an anonymous user
     * @return {@code true} if the specified principal is an anonymous user
     */
    public static boolean isAnonymous(@Nullable String principal) {
        return AnonymousUserSid.isAnonymous(principal);
    }

    /**
     * Creates an anonymous user which actually is an instance of {@link AnonymousUserSid} and has a sid id equal to
     * {@link AnonymousUserSid#ANONYMOUS_USER_SID_ID}.
     *
     * @return the instance of the user sid that represents an anonymous user
     */
    public static UserSid createAnonymous() {
        return AnonymousUserSid.create();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSidId() {
        return SID_PREFIX + UniversalSid.SID_NAME_SEPARATOR + userId;
    }

    @Override
    public boolean isPrincipal() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof PrincipalSid)) {
            return false;
        }
        PrincipalSid that = (PrincipalSid) o;
        if (!getPrincipal().equals(that.getPrincipal())) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getSidId();
    }
}
