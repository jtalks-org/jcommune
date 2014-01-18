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
import org.jtalks.jcommune.service.dto.SimplePageInfoContainer;
import org.jtalks.jcommune.web.validation.annotations.BbCodeNesting;

import javax.validation.constraints.Size;

public class SimplePageDto {

    @NotBlank
    @Size(min = SimplePage.MIN_NAME_LENGTH, max = SimplePage.MAX_NAME_LENGTH)
    private String nameText;

    @NotBlank
    @BbCodeAwareSize(min = SimplePage.MIN_CONTENT_LENGTH, max = SimplePage.MAX_CONTENT_LENGTH)
    @BbCodeNesting
    private String contentText;

    private long id;

    @NotBlank
    @Size(min = SimplePage.MIN_PATH_NAME_LENGTH, max = SimplePage.MAX_PATH_NAME_LENGTH)
    private String pathName;

    public SimplePageDto() {
    }

    /**
     * Create dto for simple page
     *
     * @param simplePage simple page for conversion
     */
    public SimplePageDto(SimplePage simplePage) {
        this.setId(simplePage.getId());
        this.setNameText(simplePage.getName());
        this.setContentText(simplePage.getContent());
        this.setPathName(simplePage.getPathName());
    }

    /**
     * Get page name
     *
     * @return page name
     */
    public String getNameText() {
        return nameText;
    }

    /**
     * Set page name
     *
     * @param nameText page name
     */
    public void setNameText(String nameText) {
        this.nameText = nameText;
    }

    /**
     * Get page content
     *
     * @return page content
     */
    public String getContentText() {
        return contentText;
    }

    /**
     * Set page content
     *
     * @param contentText page content
     */
    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    /**
     * Get page id
     *
     * @return page id
     */
    public long getId() {
        return id;
    }

    /**
     * Set page id
     *
     * @param id page id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get page path name
     *
     * @return page path name
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * Set page path name
     *
     * @param pathName page path name
     */
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    /**
     * Transform SimplePage Dto to container object
     *
     * @return converted container object
     */
    public SimplePageInfoContainer getSimplePageInfoContainer() {
        return new SimplePageInfoContainer(this.getId(), this.getNameText(), this.getContentText());
    }

}

