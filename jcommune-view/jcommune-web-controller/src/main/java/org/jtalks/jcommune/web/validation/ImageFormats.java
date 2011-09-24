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
package org.jtalks.jcommune.web.validation;

/**
 * Stores all allowable formats and their content types.
 *
 * @author Eugeny Batov
 */
public enum ImageFormats {

    JPG("image/jpeg"), GIF("image/gif"), PNG("image/png");

    private String contentType;

    /**
     * Enum constructor.
     *
     * @param contentType - content type
     */
    ImageFormats(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return contentType - content type of chosen format
     */
    public String getContentType() {
        return contentType;
    }
}
