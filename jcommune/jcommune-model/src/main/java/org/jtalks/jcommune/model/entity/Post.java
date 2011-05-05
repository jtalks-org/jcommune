/* 
 * JTalks for uniting people
 * Copyright (C) 2011  JavaTalks Team
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Also add information on how to contact you by electronic and paper mail.
 * 
 * This file creation date: Apr 12, 2011 / 8:05:19 PM
 * The JTalks Project
 * http://www.jtalks.org
 */
package org.jtalks.jcommune.model.entity;

import org.joda.time.DateTime;

/**
 * Represents the simple version of post of the forum
 * with String content.<br>
 * Contains in some {@link Topic}. <br>
 * All fields of this object are required and can't be null.<br>
 * The topic field will be updated automatically when called Topic.addPost(Post). <br>
 * The Post deletes automatically if the parent Topic deleted.
 * Use the static method Post.createNewPost() to create new post with current creationDate.
 * @author Pavel Vervenko
 */
public class Post extends Persistent {
    /**
     * Creation date and time
     */
    private DateTime creationDate;
    private User userCreated;
    private String postContent;
    private Topic topic;

    public Post() {
    }

    public Post(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Creates the new instance with the creationDate initialized with current time.
     * @return 
     */
    public static Post createNewPost() {
        return new Post(new DateTime());
    }

    /**
     * @return the postDate
     */
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @param postDate the postDate to set
     */
    public void setCreationDate(DateTime postDate) {
        this.creationDate = postDate;
    }

    /**
     * @return the userCreated
     */
    public User getUserCreated() {
        return userCreated;
    }

    /**
     * Set the User who create this post.
     * @param userCreated the userCreated to set
     */
    public void setUserCreated(User userCreated) {
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
     * @return the post
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * @param post the post to set
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
