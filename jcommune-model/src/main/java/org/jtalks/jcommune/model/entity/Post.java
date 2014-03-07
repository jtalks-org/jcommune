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

import org.hibernate.search.annotations.*;
import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.search.BbCodeFilterBridge;

import java.util.Set;

/**
 * Represents the simple version of post of the forum
 * with String content.<br/>
 * Always included in the {@link Topic}. Topic itself should contain at lest one Post <br/>
 * All fields of this object are required and can't be null.<br/>
 * The topic field will be updated automatically when called Topic.addPost(Post). <br/>
 * The Post deletes automatically if the parent Topic deleted.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Anuar Nurmakanov
 */

public class Post extends Entity {

    private DateTime creationDate;
    private DateTime modificationDate;
    private JCUser userCreated;
    private String postContent;
    private Topic topic;

    public static final int MAX_LENGTH = 20000;
    public static final int MIN_LENGTH = 2;
    
    /**
     * Name of the field in the index for Russian.
     */
    public static final String POST_CONTENT_FIELD_RU = "postContentRu";
    /**
     * Name of the field in the index for default language(English).
     */
    public static final String POST_CONTENT_FIELD_DEF = "postContent";

    /**
     * For Hibernate use only
     */
    protected Post() {
    }

    /**
     * Creates the Post instance with required fields.
     * Creation date is set to now.
     *
     * @param userCreated user who create the post
     * @param postContent content of the post
     */
    public Post(JCUser userCreated, String postContent) {
        this.creationDate = new DateTime();
        this.userCreated = userCreated;
        this.postContent = postContent;
    }

    /**
     * Used to find out the current post index on JSP page.
     * We can't invoke a method there so use an explicit getter.
     *
     * @return index of this post ina  topic, starting from 0
     */
    public int getPostIndexInTopic(){
        return topic.getPosts().indexOf(this);
    }
    
    /**
     * @return the postDate
     */
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @return date and time when the post was changed last time
     */
    public DateTime getModificationDate() {
        return modificationDate;
    }

    /**
     * @param postDate the postDate to set
     */
    protected void setCreationDate(DateTime postDate) {
        this.creationDate = postDate;
    }

    /**
     * @param modificationDate date and time when the post was changed last time
     */
    protected void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * Set modification date to now. The post's topic's
     * modification date will be also set to now
     *
     * @return new modification date
     */
    public DateTime updateModificationDate() {
        this.modificationDate = new DateTime();
        return this.modificationDate;
    }
    
    /**
     * @return date and time where post what last time modified or created otherwise
     */
    public DateTime getLastTouchedDate() {
        return modificationDate == null ? creationDate : modificationDate;
    }

    /**
     * @return the userCreated
     */
    public JCUser getUserCreated() {
        return userCreated;
    }

    /**
     * Set the User who create this post.
     *
     * @param userCreated the userCreated to set
     */
    protected void setUserCreated(JCUser userCreated) {
        this.userCreated = userCreated;
    }

    /**
     * @return the postContent
     */
    @Fields({
        @Field(name = POST_CONTENT_FIELD_RU,
            analyzer = @Analyzer(definition = "russianJtalksAnalyzer")),
        @Field(name = POST_CONTENT_FIELD_DEF,
            analyzer = @Analyzer(definition = "defaultJtalksAnalyzer"))
    })
    @FieldBridge(impl = BbCodeFilterBridge.class)
    public String getPostContent() {
        return postContent;
    }

    /**
     * @param postContent the postContent to set
     */
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    /**
     * @return the topic
     */
    @ContainedIn
    public Topic getTopic() {
        return topic;
    }

    /**
     * @param topic the Topic to set
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * @return subscribers of topic of this post
     */
    public Set<JCUser> getTopicSubscribers() {
        return getTopic().getSubscribers();
    }
}
