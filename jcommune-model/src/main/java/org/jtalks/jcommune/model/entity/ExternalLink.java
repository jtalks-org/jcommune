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

import org.hibernate.validator.constraints.NotBlank;
import org.jtalks.common.model.entity.Entity;

/**
 * Stores information about external link. Such links are shown on every page of forum at the top of it.
 *
 * @author Alexandre Teterin
 *         Date: 03.02.13
 */
public class ExternalLink extends Entity {
    @NotBlank
    private String url;
    @NotBlank
    private String title;
    @NotBlank
    private String hint;

    /**
     * Only for hibernate usage.
     */
    public ExternalLink() {
    }

    /**
     * @param url   target URL, e.g., jtalks.org the link will lead to
     * @param title URL title, e.g. 'JTalks' that's going to be shown to user
     * @param hint  URL hint or description, e.g. 'The most powerful forum engine', this hint is shown
     */
    public ExternalLink(String url, String title, String hint) {
        this.url = url;
        this.title = title;
        this.hint = hint;
    }

    /**
     * @return url target URL, e.g., jtalks.org the link will lead to
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url target URL, e.g., jtalks.org the link will lead to
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return URL URL title, e.g. 'JTalks' that's going to be shown to user
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title URL title, e.g. 'JTalks' that's going to be shown to user
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return URL hint or description, e.g. 'The most powerful forum engine', this hint is shown
     */
    public String getHint() {
        return hint;
    }

    /**
     * @param hint hint or description, e.g. 'The most powerful forum engine', this hint is shown
     */
    public void setHint(String hint) {
        this.hint = hint;
    }


}
