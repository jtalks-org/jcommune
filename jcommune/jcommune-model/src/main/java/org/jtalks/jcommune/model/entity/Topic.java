/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.model.entity;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the topic of the forum.
 * Contains the list of related {@link Post}.
 * All Posts will be cascade deleted with the associated Topic.
 * The fields creationDate, topicStarter and Title are required and can't be <code>null</code>
 *
 * @author Pavel Vervenko
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
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

    private Branch branch;

    /**
     * The last modification date of the topic.
     */
    private DateTime lastModificationDate;

    /**
     * Creates the Topic instance. All fields values are null.
     */
    public Topic() {
    }

    /**
     * Creates the Topic instance with required fields.
     * Creation and modification date is set to now.
     *
     * @param branch       branch that contains topic
     * @param topicStarter user who create the topic
     * @param title        topic title
     */
    public Topic(Branch branch, User topicStarter, String title) {
        this.branch = branch;
        this.topicStarter = topicStarter;
        this.title = title;
        this.creationDate = new DateTime();

        updateLastModificationDate();
    }

    /**
     * Creates the Topic with the specified creation date.
     *
     * @param creationDate the topic's creation date
     */
    public Topic(DateTime creationDate) {
        this.creationDate = creationDate;

        updateLastModificationDate();
    }

    /**
     * Creates a new Topic with the creationDate initialized with current time.
     *
     * @return newly created Topic
     */
    public static Topic createNewTopic() {
        return new Topic(new DateTime());
    }

    /**
     * Add new {@link Post} to the topic.
     * The method sets Posts.topic field to this Topic.
     *
     * @param newPost post to add
     */
    public void addPost(Post newPost) {
        posts.add(newPost);
        newPost.setTopic(this);

        updateLastModificationDate();
    }

    /**
     * Remove the post from the topic.
     *
     * @param postToRemove post to remove
     */
    public void removePost(Post postToRemove) {
        posts.remove(postToRemove);

        updateLastModificationDate();
    }

    /**
     * Get the post creation date.
     *
     * @return the creationDate
     */
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Set the post creation date.
     *
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Get the user who created the post.
     *
     * @return the userCreated
     */
    public User getTopicStarter() {
        return topicStarter;
    }

    /**
     * The the author of the post.
     *
     * @param userCreated the user who create the post
     */
    public void setTopicStarter(User userCreated) {
        this.topicStarter = userCreated;
    }

    /**
     * Gets the topic name.
     *
     * @return the topicName
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the topic title.
     *
     * @param newTitle the title to set
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * Get the list of the posts.
     *
     * @return the list of posts
     */
    public List<Post> getPosts() {
        return posts;
    }

    /**
     * Set the list of posts
     *
     * @param posts the posts to set
     */
    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    /**
     * Get branch that contains topic
     *
     * @return branch that contains the topic
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * Set branch that contains topic
     *
     * @param branch branch that contains the topic
     */
    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    /**
     * Set the topic last modification date.
     *
     * @param lastModificationDate the lastModificationDate to set
     */
    public void setLastModificationDate(DateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    /**
     * Get the topic last modification date.
     *
     * @return the lastModificationDate
     */
    public DateTime getLastModificationDate() {
        return lastModificationDate;
    }

    /**
     * Set the topic last modification date for current DateTime.
     */
    private void updateLastModificationDate() {
        this.lastModificationDate = new DateTime();
    }
}
