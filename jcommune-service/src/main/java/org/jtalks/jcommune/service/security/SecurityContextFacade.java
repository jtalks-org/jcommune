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

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Wraps static security context holder into a Spring bean for the sake of IoC
 *
 * @author Kirill Afonin
 */
public class SecurityContextFacade {

    /**
     * @return {@code SecurityContext} from {@code SecurityContextHolder}
     */
    public SecurityContext getContext() {
        return SecurityContextHolder.getContext();
    }

    /**
     * Set {@code SecurityContext} to  {@code SecurityContextHolder}
     *
     * @param securityContext {@code SecurityContext} to set.
     */
    public void setContext(SecurityContext securityContext) {
        SecurityContextHolder.setContext(securityContext);
    }
}
