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

/**
 * Represents draft of a post
 *
 * @author Mikhail Stryzhonok
 */
public class PostDraft extends Entity {

    private String content;
    private Topic topic;
    private JCUser author;
    private DateTime lastSaved;

    public PostDraft() {
    }

    public PostDraft(String content, JCUser author) {
        this.content = content;
        this.author = author;
        lastSaved = new DateTime();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public JCUser getAuthor() {
        return author;
    }

    public void setAuthor(JCUser author) {
        this.author = author;
    }

    public DateTime getLastSaved() {
        return lastSaved;
    }

    protected void setLastSaved(DateTime lastSaved) {
        this.lastSaved = lastSaved;
    }

    /**
     * Sets current datetime to last saved property
     */
    public void updateLastSavedTime() {
        lastSaved = new DateTime();
    }
}
