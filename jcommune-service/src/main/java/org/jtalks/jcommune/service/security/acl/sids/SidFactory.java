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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Typical application that uses Spring ACL won't need anything but {@link org.springframework.security.acls.domain.PrincipalSid}
 * or {@link org.springframework.security.acls.domain.GrantedAuthoritySid},
 * but sometimes we need to extend this list of implementations or replace it for more complicated scenarios when
 * default capabilities of Spring ACL is not enough. You can implement this factory and inject it into different classes
 * that work with {@link Sid}s in order them to create <i>your</i> SIDs.
 *
 * @author stanislav bashkirtsev
 */
public interface SidFactory {
    /**
     * The Factory Method that creates a particular implementation of {@link Sid} depending on the arguments.
     *
     * @param sidName   the name of the sid representing its unique identifier. In typical ACL database schema it's
     *                  located in table {@code acl_sid} table, {@code sid} column.
     * @param principal whether it's a user or granted authority like role
     * @return the instance of Sid with the {@code sidName} as an identifier
     */
    Sid create(String sidName, boolean principal);

    /**
     * Creates a principal-like sid from the authentication information.
     *
     * @param authentication the authentication information that can provide principal and thus the sid's id will be
     *                       dependant on the value inside
     * @return a sid with the ID taken from the authentication information
     */
    Sid createPrincipal(Authentication authentication);

    List<? extends Sid> createGrantedAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities);
}
