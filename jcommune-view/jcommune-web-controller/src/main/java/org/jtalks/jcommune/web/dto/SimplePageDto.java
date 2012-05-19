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

import org.hibernate.validator.constraints.NotBlank;
import org.jtalks.jcommune.model.entity.SimplePage;
import org.jtalks.jcommune.web.validation.annotations.BbCodeAwareSize;

public class SimplePageDto {

    @NotBlank
    @BbCodeAwareSize(min = SimplePage.MIN_NAME_LENGTH, max = SimplePage.MAX_NAME_LENGTH)
    private String nameText;

    @NotBlank
    @BbCodeAwareSize(min = SimplePage.MIN_CONTENT_LENGTH, max = SimplePage.MAX_CONTENT_LENGTH)
    private String contentText;
    
    private long id;

    @NotBlank
    @BbCodeAwareSize(min = SimplePage.MIN_PATH_NAME_LENGTH, max = SimplePage.MAX_PATH_NAME_LENGTH)
    private String pathName;

    public SimplePageDto() {
    }

    public SimplePageDto(SimplePage simplePage) {
        this.setId(simplePage.getId());
        this.setNameText(simplePage.getName());
        this.setContentText(simplePage.getContent());
        this.setPathName(simplePage.getPathName());
    }

    public String getNameText() {
        return nameText;
    }

    public void setNameText(String nameText) {
        this.nameText = nameText;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

}

