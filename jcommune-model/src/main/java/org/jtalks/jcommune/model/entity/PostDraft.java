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
 * @author Mikhail Stryzhonok
 */
public class PostDraft extends Entity {

    private String content;
    private DateTime modificationDate;
    private JCUser author;
    private Topic topic;

    public PostDraft() {
    }

    public PostDraft(String content, JCUser author, Topic topic) {
        this.content = content;
        this.author = author;
        this.topic = topic;
        this.modificationDate = new DateTime();
    }

    /**
     * Gets content of current draft
     *
     * @return content of current draft
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets specified string as content of draft
     *
     * @param content content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets modification date of current draft.
     * Here modification date means date of draft creation, in case if draft was never modified
     * and date of last modification in case if draft was updated
     *
     * @return modification date of current draft.
     */
    public DateTime getModificationDate() {
        return modificationDate;
    }

    /**
     * Sets specified date as modification date of draft
     *
     * @param modificationDate modification date to set
     */
    public void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * Gets user who created draft
     *
     * @return user who created draft
     */
    public JCUser getAuthor() {
        return author;
    }

    /**
     * Sets specified user as author of draft
     *
     * @param author user to set
     */
    public void setAuthor(JCUser author) {
        this.author = author;
    }

    /**
     * Gets topic in which draft was created
     *
     * @return topic in which draft was created
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * Sets specified topic as topic in which draft was created
     *
     * @param topic topic to set
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
