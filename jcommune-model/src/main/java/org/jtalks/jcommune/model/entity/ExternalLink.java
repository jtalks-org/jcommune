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

import java.net.URL;

/**
 * Stores information about external link.
 * @author Alexandre Teterin
 *         Date: 03.02.13
 */

public class ExternalLink extends Entity {

    private URL url;
    private String title;
    private String hint;

    /**
     * Only for hibernate usage.
     */
    public ExternalLink() {
    }

    /**
     *
     * @param url target URL, e.g., jtalks.org.
     * @param title  URL title, e.g. 'JTalks'.
     * @param hint URL hint or description, e.g. 'The most powerful forum engine'.
     */
    public ExternalLink(URL url, String title, String hint) {
        this.url = url;
        this.title = title;
        this.hint = hint;
    }

    /**
     *
     * @return url
     */
    public URL getUrl() {
        return url;
    }

    /**
     *
     * @param url url
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     *
     * @return URL title.
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title URL title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return URL hint.
     */
    public String getHint() {
        return hint;
    }

    /**
     *
     * @param hint URL hint.
     */
    public void setHint(String hint) {
        this.hint = hint;
    }
}
