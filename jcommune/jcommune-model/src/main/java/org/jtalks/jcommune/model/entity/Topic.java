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
import java.util.List;
import org.joda.time.DateTime;

/**
 * Represents the topic of the forum.
 * Contains the list of related {@link Post}.
 * All Posts will be cascade deleted with the associated Topic.
 * The fields creationDate, topicStarter and Title are required and can't be <code>null</code>
 * @author Pavel Vervenko
 */
public class Topic extends Persistent {

    /**
     * The creation date of the topic. 
     */
    private DateTime creationDate;
    /**
     * The user who create the topic
     */
    private User topicStarter;
    private String title;
    /**
     * The list of topic's posts
     */
    private List<Post> posts = new ArrayList<Post>();
    
    public Topic() {
    }
    
    public Topic(DateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    /**
     * Creates a new Topic with the creationDate initialized with current time.
     * @return 
     */
    public static Topic createNewTopic() {
        return new Topic(new DateTime());
    }
    /**
     * Add new {@link Post} to the topic.
     * The method sets Posts.topic field to this Topic.
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
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Set the post creation date.
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Get the user who created the post.
     * @return the userCreated
     */
    public User getTopicStarter() {
        return topicStarter;
    }

    /**
     * The the author of the post.
     * @param userCreated the user who create the post
     */
    public void setTopicStarter(User userCreated) {
        this.topicStarter = userCreated;
    }

    /**
     * Gets the topic name.
     * @return the topicName
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the topic title.
     * @param newTitle the title to set
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * Get the list of the posts.
     * @return the list of posts
     */
    public List<Post> getPosts() {
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
