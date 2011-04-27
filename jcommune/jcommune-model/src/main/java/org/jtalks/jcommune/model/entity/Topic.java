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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Represents the topic of the forum.
 * Contains the list of related {@link Post}.
 * @author Pavel Vervenko
 */
public class Topic extends Persistent {

    /**
     * The creation date of the topic.
     */
    private Date creationDate;
    /**
     * The user who create the topic
     */
    private User userCreated;
    private String topicName;
    /**
     * The list of topic's posts
     */
    private List<Post> posts;
    
    public Topic() {
        posts = new ArrayList<Post>();
        creationDate = new Date();
    }

    /**
     * Add new {@link Post} to the topic.
     * @param newPost 
     */
    public void addPost(Post newPost) {
        posts.add(newPost);
        newPost.setTopic(this);
    }

    /**
     * Remove the post from the topic.
     * @param postToRemove 
     */
    public void removePost(Post postToRemove) {
        posts.remove(postToRemove);
    }

    /**
     * Get the post creation date.
     * @return the creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Set the post creation date.
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Get the user who created the post.
     * @return the userCreated
     */
    public User getUserCreated() {
        return userCreated;
    }

    /**
     * The the author of the post.
     * @param userCreated the user who create the post
     */
    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

    /**
     * Get the topic name.
     * @return the topicName
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * Set the topic name.
     * @param topicName the topicName to set
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    /**
     * Get the list of the posts.
     * @return the list of posts
     */
    public Collection<Post> getPosts() {
        return posts;
    }

    /**
     * Set the list of posts
     * @param posts the posts to set
     */
    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
 }
