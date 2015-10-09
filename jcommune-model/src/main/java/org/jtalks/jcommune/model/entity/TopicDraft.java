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

import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.validation.annotations.AtLeastOneFieldIsNotNull;
import org.jtalks.jcommune.model.validation.annotations.TopicDraftNumberOfPollItems;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Represents a draft topic.
 *
 * @author Dmitry S. Dolzhenko
 */
@AtLeastOneFieldIsNotNull(fields = {
        "title", "content", "pollTitle", "pollItemsValue"
}, message = "{topicDraft.fields.not_null}")
public class TopicDraft extends Entity {

    @Size(min = Topic.MIN_NAME_SIZE, max = Topic.MAX_NAME_SIZE,
            message = "{javax.validation.constraints.Size.message}")
    private String title;

    @Size(min = Post.MIN_LENGTH, max = Post.MAX_LENGTH,
            message = "{javax.validation.constraints.Size.message}")
    private String content;

    @NotNull
    private JCUser topicStarter;

    @NotNull
    private DateTime lastSaved;

    @Size(min = Poll.MIN_TITLE_LENGTH, max = Poll.MAX_TITLE_LENGTH,
            message = "{javax.validation.constraints.Size.message}")
    private String pollTitle;

    @TopicDraftNumberOfPollItems
    private String pollItemsValue;

    public TopicDraft() {
    }

    public TopicDraft(JCUser topicStarter, String title, String content) {
        this.topicStarter = topicStarter;
        this.title = title;
        this.content = content;
        this.lastSaved = new DateTime();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public JCUser getTopicStarter() {
        return topicStarter;
    }

    public void setTopicStarter(JCUser topicStarter) {
        this.topicStarter = topicStarter;
    }

    public DateTime getLastSaved() {
        return lastSaved;
    }

    public void setLastSaved(DateTime lastSaved) {
        this.lastSaved = lastSaved;
    }

    public String getPollTitle() {
        return pollTitle;
    }

    public void setPollTitle(String pollTitle) {
        this.pollTitle = pollTitle;
    }

    public String getPollItemsValue() {
        return pollItemsValue;
    }

    public void setPollItemsValue(String pollItemsValue) {
        this.pollItemsValue = pollItemsValue;
    }

    /**
     * Sets current datetime to 'lastSaved' property
     */
    public void updateLastSavedTime() {
        lastSaved = new DateTime();
    }
}
