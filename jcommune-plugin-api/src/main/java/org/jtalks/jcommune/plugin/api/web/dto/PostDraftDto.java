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
package org.jtalks.jcommune.plugin.api.web.dto;

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.plugin.api.web.validation.annotations.BbCodeAwareSize;
import org.jtalks.jcommune.plugin.api.web.validation.annotations.BbCodeNesting;

/**
 * @author Dmitry S. Dolzhenko
 */
public class PostDraftDto {
    /**
     * Unlike post, draft may contain only one symbol to be saved.
     */
    @BbCodeAwareSize(min = 1, max = Post.MAX_LENGTH)
    @BbCodeNesting
    private String bodyText;
    private long topicId;

    public PostDraftDto() {
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }
}
