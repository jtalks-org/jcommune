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

package org.jtalks.jcommune.model.entity;

import org.jtalks.common.model.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Provides core user information which is later encapsulated. This class is immutable which is very important
 * as it's going to be accessed from many threads. All the fields here are loaded only once into HTTP Session
 * and don't ever change within that session.
 *
 * into {@link Authentication} objects.
 *
 * @author Oleg Tkachenko
 */
public class UserInfo implements UserDetails {
    private final long id;
    private final String uuid;
    private final String username;
    private final String password;
    private final boolean enabled;

    public UserInfo(User user) {
        this.id = user.getId();
        this.uuid = user.getUuid();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
    }

    public long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns user's activation status in forum. After registration user has status "disabled" and receives an e-mail
     * with activation link. When user confirm his e-mail address, status changed to "enabled". Only users with
     * activation status "enabled" can login.
     *
     * @return return true if user is activated in forum and can login, otherwise false.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInfo)) return false;
        UserInfo userInfo = (UserInfo) o;
        return getUuid().equals(userInfo.getUuid());
    }

    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }

    /*
     * Next methods from UserDetails interface, indicating that user can or can't authenticate.
     * We don't need this functionality.
     */

    /**
     * Used for role based access in spring security.
     * Jcommune uses Domain Object Security (ACLs) so we don't have any roles.
     *
     * @return {@link Collections.EmptyList}
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
