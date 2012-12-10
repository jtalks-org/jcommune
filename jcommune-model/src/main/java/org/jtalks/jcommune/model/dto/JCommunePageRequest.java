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
package org.jtalks.jcommune.model.dto;

import org.springframework.data.domain.PageRequest;

/**
 * Data transfer object that needed for pagination in JCommune.
 * It contains additional help methods for calculation of
 * pagination.
 * 
 * @author Anuar Nurmakanov
 */
public class JCommunePageRequest extends PageRequest {
    private static final long serialVersionUID = -9054794147449741044L;
    private boolean pagingEnabled;
    
    /**
     * Creates a new {@link JCommunePageRequest}. 
     * 
     * @param size size of page
     * @param page page number
     * @param pagingEnabled true if pagination is enabled, false if pagination is disabled
     */
    public JCommunePageRequest(int page, int size, boolean pagingEnabled) {
        super(page, size);
        this.pagingEnabled = pagingEnabled;
    }
    
    /**
     * Create an instance of {@link JCommunePageRequest} with enabled paging.
     *
     * @param page page number
     * @param size size of page
     * @return an instance of {@link JCommunePageRequest} with enabled paging
     */
    public static JCommunePageRequest createWithPagingEnabled(int page, int size) {
        return new JCommunePageRequest(page, size, true);
    }
    
    /**
     * Create an instance of {@link JCommunePageRequest} with disabled paging.
     * 
     * @param size size of page
     * @param page page number
     * @return an instance of {@link JCommunePageRequest} with disabled paging.
     */
    public static JCommunePageRequest createWithPagingDisabled(int page, int size) {
        return new JCommunePageRequest(page, size, false);
    }
    
    /**
     * Get an index for first element in the page.
     * 
     * @return an index for first element in the page
     */
    public int getIndexOfFirstItem() {
        return(getPageNumber() - 1) * getPageSize();
    }

    /**
     * Get the flag, which is an indicator of enabling/disabling pagination.
     * 
     * @return true if pagination is enabled, false if pagination is disabled
     */
    public boolean isPagingEnabled() {
        return pagingEnabled;
    }
}
