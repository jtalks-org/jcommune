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

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * 
 * @author Anuar Nurmakanov
 *
 * @param <T>
 */
public class JcommunePageImpl<T> extends PageImpl<T> implements JcommunePage<T> {
    private static final long serialVersionUID = 1741947011630545443L;
    private boolean pagingEnabled = true;

    /**
     * Constructs an instance of {@link JcommunePageImpl}.
     * 
     * @param content the content of this page
     * @param pageable the paging information
     * @param total the total amount of items available
     */
    public JcommunePageImpl(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPagingEnabled() {
        return pagingEnabled;
    }

    /**
     * Determines whether the pagination enabled.
     * 
     * @param pagingEnabled if true then pagination enabled,
     *                      otherwise disabled
     */
    public void setPagingEnabled(boolean pagingEnabled) {
        this.pagingEnabled = pagingEnabled;
    }
}
