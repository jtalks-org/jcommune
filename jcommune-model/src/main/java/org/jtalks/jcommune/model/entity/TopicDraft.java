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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Represents a draft topic.
 *
 * @author Dmitry S. Dolzhenko
 */
@AtLeastOneFieldIsNotNull(fields = {
        "title", "content", "pollTitle", "pollItemsValue"
}, message = "{topicDraft.fields.not_null}")
public class TopicDraft extends Entity {

    @Size(max = Topic.MAX_NAME_SIZE,
            message = "{javax.validation.constraints.Size.message}")
    private String title;

    @Size(max = Post.MAX_LENGTH,
            message = "{javax.validation.constraints.Size.message}")
    private String content;

    @Size(max = Poll.MAX_TITLE_LENGTH,
            message = "{javax.validation.constraints.Size.message}")
    private String pollTitle;

    @TopicDraftNumberOfPollItems(max = Poll.MAX_ITEMS_NUMBER)
    private String pollItemsValue;

    private JCUser topicStarter;
    private DateTime lastSaved;

    /**
     * These fields are transient, since we do not need to save them in DB
     * and and we use them only for permissions check during topic creation.
     * Later on the user may close the page and start creating it in another
     * branch - the permissions may be different and we're ok with that.
     * Draft is not bound to the branch and can be started in one branch
     * and finished in another one.
     *
     * Here we check that branchId is greater than 0, to be sure in that
     * user passed it to check permissions
     */
    @Min(value = 1)
    private transient long branchId;
    @NotNull
    private transient String topicType;

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

    public long getBranchId() {
        return branchId;
    }

    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    /**
     * Sets current datetime to 'lastSaved' property
     */
    public void updateLastSavedTime() {
        lastSaved = new DateTime();
    }

    /**
     * Determines if this draft is draft for code review
     *
     * @return true if code review, otherwise false
     */
    public boolean isCodeReview() {
        return Objects.equals(topicType, TopicTypeName.CODE_REVIEW.getName());
    }

    /**
     * Determines if this draft is draft for provided by plugin topic.
     * NOTE: currently jcommune provides two topic types: "Code review" and "Discussion" all other
     * topic types are provided by plugins
     *
     * @return true if topic is provided by plugin otherwise false
     */
    public boolean isPlugable() {
        return topicType != null && !(this.isCodeReview() || topicType.equals(TopicTypeName.DISCUSSION.getName()));
    }
}
