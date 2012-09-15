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

package org.jtalks.jcommune.service.dto;

/**
 * This class is used when transferring simple page profile updates
 * from web tier to the service layer. For various reasons
 * we can't use domain model class and MVC command object.
 *
 * @author Alexander Gavrikov
 */

public class SimplePageInfoContainer {

    private long id;
    private String name;
    private String content;

    /**
     * Create instance with required fields.
     *
     * @param id        id of simple page
     * @param name      name of simple page
     * @param content   content of simple page
     */
    public SimplePageInfoContainer(long id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
