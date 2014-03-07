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

import org.jtalks.common.model.entity.Entity;

/**
 * Any simple page entity. <br/>
 * It's used for display and edit static information. <br/>
 *
 * Includes not null fields:
 *  name - name or title of page,
 *  content - formatted text of page,
 *  pathName - specific name of page which identifies it in browser address.
 *
 * @author Alexander Gavrikov
 * @author Scherbakov Roman
 */

public class SimplePage extends Entity {

    public static final int MIN_NAME_LENGTH = 5;
    public static final int MAX_NAME_LENGTH = 50;
    public static final int MIN_CONTENT_LENGTH = 5;
    public static final int MAX_CONTENT_LENGTH = 20000;
    public static final int MIN_PATH_NAME_LENGTH = 5;
    public static final int MAX_PATH_NAME_LENGTH = 50;

    private String name;
    private String content;

    private String pathName;

    public SimplePage() {
    }
    
    public SimplePage(String name, String content, String pathName) {
        
        this.name = name;
        this.content = content;
        this.pathName = pathName;
    }
    
    public String getName() {

        return name;
    }

    public void setName(String name) {
        
        this.name = name;     
    }
    
    public String getContent() {
        
        return content;
    }
    
    public void setContent(String content) {

        this.content = content;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }
}
