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

import java.util.Collection;
import java.util.List;

import org.jtalks.common.model.entity.Group;
import org.springframework.security.core.GrantedAuthority;

/**
 * Represents anonymous user.
 * This is a Null Object pattern implementation, contains default
 * parameter values to be used for users not logged in.
 * <p/>
 * You're likely to get this instance for userService#getCurrentUser if
 * there is no active user session in progress, i.e. current user is an anonymous.
 *
 * @author Vyacheslav Mishcheryakov
 */
public class AnonymousUser extends JCUser {

    /**
     * Creates anonymous user instance with default properties set
     */
    public AnonymousUser() {
        super();
        setPageSize(JCUser.DEFAULT_PAGE_SIZE);
        setLanguage(Language.ENGLISH);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        throw new UnsupportedOperationException("Authorities are not defined for anonymous user");
    }

    @Override
    public String getEmail() {
        throw new UnsupportedOperationException("Mail is not defined for anonymous user");
    }

    @Override
    public String getEncodedUsername() {
        throw new UnsupportedOperationException("Encoded username is not defined for anonymous user");
    }

    @Override
    public List<Group> getGroups() {
        throw new UnsupportedOperationException("Groups are not defined for anonymous user");
    }

    @Override
    public long getId() {
        throw new UnsupportedOperationException("ID is not defined for anonymous user");
    }

    @Override
    public String getPassword() {
        throw new UnsupportedOperationException("Password is not defined for anonymous user");
    }

    @Override
    public String getUsername() {
        throw new UnsupportedOperationException("Username is not defined for anonymous user");
    }

    @Override
    public boolean isAnonymous() {
        return true;
    }
}
