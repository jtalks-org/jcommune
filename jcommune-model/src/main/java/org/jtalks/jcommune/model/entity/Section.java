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

/**
 * Forum section that joins branches related by section theme.
 *
 * @author Max Malakhov
 */
public class Section extends org.jtalks.common.model.entity.Section {


    /**
     * {@inheritDoc}
     */
    protected Section() {
    }
    
    /**
     * {@inheritDoc} 
     */
    public Section(String name) {
        super(name);
    }
    
    /**
     * {@inheritDoc}
     */
    public Section(String name, String description) {
        super(name, description);
    }
    
}
