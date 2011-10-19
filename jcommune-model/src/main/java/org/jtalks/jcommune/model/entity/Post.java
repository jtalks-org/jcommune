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
 * Represents the simple version of post of the forum
 * with String content.<br/>
 * Contains in some {@link Topic}. <br/>
 * All fields of this object are required and can't be null.<br/>
 * The topic field will be updated automatically when called Topic.addPost(Post). <br/>
 * The Post deletes automatically if the parent Topic deleted.
 * Use the static method Post.createNewPost() to create new post with current creationDate.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 */
public class Post extends Entity {

    /**
     * Creation date and time
     */
    private DateTime creationDate;
    private DateTime modificationDate;
    private User userCreated;
    private String postContent;
    private Topic topic;
    
    /**
     * Constructs the instance with initialized fields.
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
    public Post(User userCreated, String postContent) {
        this.creationDate = new DateTime();
        this.userCreated = userCreated;
        this.postContent = postContent;
    }

    /**
     * Constructor used only for the input data in the testGetPostBreadcrumb method
     * org.jtalks.jcommune.web.dto.BreadcrumbBuilderTest class.
     *
     * @param topic to be used as input data
     */
    public Post(Topic topic) {
        this.topic = topic;
    }

    /**
     * Creates the new instance with the creationDate initialized with current time.***
     *
     * @param userCreated author of the post
     * @param postContent the post content itself
     * @return new Post instance
     */
    public static Post createNewPost(User userCreated, String postContent) {
        return new Post(userCreated,postContent);
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
     * Set modification date to now.
     *
     * @return new modification date
     */
    public DateTime updateModificationDate()  {
        this.modificationDate = new DateTime();
        return this.modificationDate;
    }

    /**
     * @return the userCreated
     */
    public User getUserCreated() {
        return userCreated;
    }

    /**
     * Set the User who create this post.
     *
     * @param userCreated the userCreated to set
     */
    protected void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

    /**
     * @return the postContent
     */
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
    public Topic getTopic() {
        return topic;
    }

    /**
     * @param topic the Topic to set
     */
    protected void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * Get a short version of topic content for preview in recent messages (max 200 character).
     *
     * @return shortContent
     */
    public String getShortContent(){
        String content = this.getPostContent();
        String shortContent = "";
        // todo: replace with lastIndexOf call
        if (content.length() > 200){
            for (int i = 197; i > 1; i--){
                if (content.charAt(i) == ' '){
                    shortContent = content.substring(0,i);
                    shortContent = shortContent + "...";
                    break;
                }
            }
        } else {
            shortContent = content;
        }
        return shortContent;
    }
}
