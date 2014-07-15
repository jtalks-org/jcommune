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

package org.jtalks.jcommune.web.dto;

/**
 * Class providing the information about UI element: id, localization keys for the
 * displaying text and tooltip and the http address which will be used by the element
 * @author Andrei Alikov
 */
public class UiElementDto {
    private String id;
    private String displayNameKey;
    private String toolTipKey;
    private String link;

    public UiElementDto(String id, String displayNameKey, String toolTipKey, String link) {
        this.displayNameKey = displayNameKey;
        this.toolTipKey = toolTipKey;
        this.link = link;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayNameKey() {
        return displayNameKey;
    }

    public void setDisplayNameKey(String displayNameKey) {
        this.displayNameKey = displayNameKey;
    }

    public String getToolTipKey() {
        return toolTipKey;
    }

    public void setToolTipKey(String toolTipKey) {
        this.toolTipKey = toolTipKey;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
