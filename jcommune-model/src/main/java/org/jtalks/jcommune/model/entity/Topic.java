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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.joda.time.DateTime;

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
public class Topic extends SubscriptionAwareEntity {
    private DateTime creationDate;
    private DateTime modificationDate;
    private JCUser topicStarter;
    private String title;
    private int topicWeight;
    private boolean sticked;
    private boolean announcement;
    private List<Post> posts = new ArrayList<Post>();
    private Branch branch;
    private int views;

    // transient, makes sense for current user only if set explicitly
    private boolean hasUpdates = true;

    public static final int MIN_NAME_SIZE = 5;
    public static final int MAX_NAME_SIZE = 120;

    /**
     * Used only by hibernate.
     */
    protected Topic() {
    }

    /**
     * Creates the Topic instance with required fields.
     * Creation and modification date is set to now.
     *
     * @param topicStarter user who create the topic
     * @param title        topic title
     */
    public Topic(JCUser topicStarter, String title) {
        this.topicStarter = topicStarter;
        this.title = title;
        this.creationDate = new DateTime();
        this.modificationDate = new DateTime();
        this.topicWeight = 0;
        this.sticked = false;
        this.announcement = false;
    }

    /**
     * Add new {@link Post} to the topic.
     * The method sets Posts.topic field to this Topic.
     *
     * @param post post to add
     */
    public void addPost(Post post) {
        post.setTopic(this);
        updateModificationDate();
        this.posts.add(post);
    }

    /**
     * Remove the post from the topic.
     *
     * @param postToRemove post to remove
     */
    public void removePost(Post postToRemove) {
        posts.remove(postToRemove);
        updateModificationDate();
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
    protected void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Get the user who created the post.
     *
     * @return the userCreated
     */
    public JCUser getTopicStarter() {
        return topicStarter;
    }

    /**
     * The the author of the post.
     *
     * @param userCreated the user who create the post
     */
    protected void setTopicStarter(JCUser userCreated) {
        this.topicStarter = userCreated;
    }

    /**
     * Gets the topic name.
     *
     * @return the topicName
     */
    @Field
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
    protected void setPosts(List<Post> posts) {
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
    void setBranch(Branch branch) {
        this.branch = branch;
    }

    /**
     * Get the topic first post.
     *
     * @return the firstPost
     */
    public Post getFirstPost() {
        return posts.get(0);
    }

    /**
     * Get the topic last post.
     *
     * @return last post
     */
    public Post getLastPost() {
        return posts.get(posts.size() - 1);
    }

    /**
     * @return date and time when theme was changed last time
     */
    public DateTime getModificationDate() {
        return modificationDate;
    }

    /**
     * @param modificationDate date and time when theme was changed last time
     */
    protected void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * Set modification date to now.
     *
     * @return new modification date
     */
    public DateTime updateModificationDate() {
        this.modificationDate = new DateTime();
        return this.modificationDate;
    }

    /**
     * @return priority of a sticked topic
     */
    public int getTopicWeight() {
        return this.topicWeight;
    }

    /**
     * @param topicWeight a priority for a sticked topic
     */
    public void setTopicWeight(int topicWeight) {
        this.topicWeight = topicWeight;
    }

    /**
     * @return flag og stickedness
     */
    public boolean isSticked() {
        return this.sticked;
    }

    /**
     * @param sticked a flag of stickedness for a topic
     */
    public void setSticked(boolean sticked) {
        this.sticked = sticked;
        if (!sticked) {
            topicWeight = 0;
        }
    }

    /**
     * @return flag og announcement
     */
    public boolean isAnnouncement() {
        return this.announcement;
    }

    /**
     * @param announcement a flag of announcement for a topic
     */
    public void setAnnouncement(boolean announcement) {
        this.announcement = announcement;
    }

    /**
     * Get count of post in topic.
     *
     * @return count of post
     */
    public int getPostCount() {
        return posts.size();
    }

    /**
     * @return topic page views
     */
    public int getViews() {
        return views;
    }

    /**
     * @param views topic page views
     */
    public void setViews(int views) {
        this.views = views;
    }

    /**
     * @param index last read post index in this topic for current user
     * (0 means first post is the last read one)
     */
    public void setLastReadPostIndex(int index){
       hasUpdates = (index + 1 < posts.size());
    }

    /**
     * This method will return true if there are unread posts in that topic
     * for the current user. This state is NOT persisted and must be
     * explicitly set by calling  Topic.setLastReadPostIndex().
     *
     * If setter has not been called this method will always return no updates
     *
     * @return if current topic has posts still unread by the current user
     */
    public boolean isHasUpdates(){
       return hasUpdates;
    }
    
	/**
     * {@inheritDoc}
     */
    @DocumentId
	@Override
	public long getId() {
		return super.getId();
	}
}
