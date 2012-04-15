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
package org.jtalks.jcommune.service.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;

/**
 * Allows you to perform security operations with the addition of the temporary authorities.
 * Try not to use this implementation, which is needed as a temporary solution until the
 * introduction of new security.
 * 
 * @author Anuar Nurmakanov
 */
public class TemporaryAuthorityManager {
    private SecurityContextFacade securityContextFacade;

    /**
     * Constructs an instance of manager.
     * 
     * @param securityContextFacade {@link org.jtalks.jcommune.service.security.SecurityContextFacade}
     *                              to be injected
     */
    public TemporaryAuthorityManager(SecurityContextFacade securityContextFacade) {
        this.securityContextFacade = securityContextFacade;
    }
    
    /**
     * Performs security operation with the addition of temporary authority.
     * The authority is temporary and it will not be saved.
     * 
     * @param operation the security operation
     * @param authorityName a name of the authority
     * @see SecurityOperation
     */
    public void runWithTemporaryAuthority(SecurityOperation operation, String authorityName) {
        SecurityContext securityContext = securityContextFacade.getContext();
        Authentication realAuthentication = securityContext.getAuthentication();
        Collection<GrantedAuthority> realAuthorities = new ArrayList<GrantedAuthority>(
                realAuthentication.getAuthorities());
        realAuthorities.add(new GrantedAuthorityImpl(authorityName));
        Authentication tempAuthentication = new UsernamePasswordAuthenticationToken(
                realAuthentication.getPrincipal(),
                realAuthentication.getCredentials(),
                realAuthorities);
        securityContext.setAuthentication(tempAuthentication);
        operation.doOperation();
        securityContext.setAuthentication(realAuthentication);
    }
    
    /**
     * The operation of security that must be done with a temporary authority.
     * 
     * @author Anuar Nurmakanov
     */
    public interface SecurityOperation {
        /**
         * The implementation of this method should contain a list of actions
         * that must be met with additional authority.
         */
        void doOperation();
    }
}
