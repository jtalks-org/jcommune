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
package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.entity.JCUser;

/**
 * This service provides functionality, that is needed for pagination.
 * For the most part, it needed to get the size of a user's page. 
 * After we have add a fake user for anonymous users, this service
 * will not be needed.
 * 
 * @author Anuar Nurmakanov
 */
public class PaginationService {
    private SecurityService securityService;
    
    /**
     * Constructs an instance with required fields.
     * 
     * @param securityService security service, it needed for information
     *                        about current user
     */
    public PaginationService(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Returns page size applicable for the current user.
     * 
     * @return page size applicable for the current user
     */
    public int getPageSizeForCurrentUser() {
        JCUser currentUser = securityService.getCurrentUser();
        return getPageSizeFor(currentUser);
    }
    
    /**
     * Returns page size applicable for the user. If for some reasons
     * this implementation is unable to determaine this parameter the default
     * value will be used.
     *
     * @param user current user representation, may be null
     * @return page size for the current user or default if there is no user
     */
    public int getPageSizeFor(JCUser user) {
        return (user == null) ? JCUser.DEFAULT_PAGE_SIZE : user.getPageSize();
    }
}
