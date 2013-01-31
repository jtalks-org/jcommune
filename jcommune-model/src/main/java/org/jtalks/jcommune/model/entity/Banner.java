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
 * Represent banner that is showed in every page of forum. Banner contains
 * HTML/JS value.
 * 
 * @author Anuar_Nurmakanov
 * 
 */
public class Banner extends Entity {
    private int positionOnPage;
    private String content;
    
    /**
     * Default constructor.
     */
    public Banner() {
    }
    
    /**
     * Constructs an instance with predefined position on page
     * and content.
     * 
     * @param positionOnPage position on page
     * @param content content
     */
    public Banner(int positionOnPage, String content) {
        this.positionOnPage = positionOnPage;
        this.content = content;
    }

    /**
     * Get position of banner on forum page.
     * 
     * @return position of banner on forum page
     */
    public int getPositionOnPage() {
        return positionOnPage;
    }

    /**
     * Set position of banner on forum page.
     * 
     * @param positionOnPage position of banner on forum page
     */
    public void setPositionOnPage(int positionOnPage) {
        this.positionOnPage = positionOnPage;
    }

    /**
     * Get content.
     * 
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * Set content.
     * 
     * @param content content
     */
    public void setContent(String content) {
        this.content = content;
    }
}
