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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

public class Post extends Entity implements SubscriptionAwareEntity {
    public static final String URL_SUFFIX = "/posts/";
    private DateTime creationDate;
    private DateTime modificationDate;
    private JCUser userCreated;
    private String postContent;
    private Topic topic;
    private int rating;

    private List<PostComment> comments = new ArrayList<>();
    private Set<PostVote> votes = new HashSet<>();

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
     * Gets list of comments of the post
     *
     * @return list of comments
     */
    public List<PostComment> getComments() {
        return comments;
    }

    /**
     * Sets specified list of comments to the post
     *
     * @param comments list of comments to set
     */
    public void setComments(List<PostComment> comments) {
        this.comments = comments;
    }

    /**
     * Adds specified comment to the post
     *
     * @param comment comment to add
     */
    public void addComment(PostComment comment) {
        comment.setPost(this);
        comments.add(comment);
    }

    /**
     * Gets rating of the post.
     * Rating introduced to provide ability to vote for posts.
     * In some topic types post may be ordered by rating.
     *
     * @return rating of the post
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets specified rating to current post
     *
     * @param rating rating to set
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Gets set of votes for this post
     *
     * @return set of votes for this post
     */
    public Set<PostVote> getVotes() {
        return votes;
    }

    /**
     * Sets set of votes as votes for this post. Needed only for Hibernate
     * use {@link #putVote(PostVote)} to add or update vote of post
     *
     * @param votes set of votes to set
     */
    public void setVotes(Set<PostVote> votes) {
        this.votes = votes;
    }

    /**
     * Adds new vote to the post or overrides existent
     *
     * @param vote vote to add or override
     */
    public void putVote(PostVote vote) {
        vote.setPost(this);
        for (PostVote storedVote : votes) {
            if (storedVote.getUser().equals(vote.getUser()) && storedVote.getPost().equals(vote.getPost())) {
                storedVote.setVotedUp(vote.isVotedUp());
                storedVote.setVoteDate(vote.getVoteDate());
                return;
            }
        }
        votes.add(vote);
    }

    /**
     * Determines if user voted up for the post
     *
     * @param user user to check
     *
     * @return true if user voted up for the post
     *         false if user voted down for the post
     *         false if user not voted for the post
     */
    public boolean isVotedUpBy(JCUser user) {
        for (PostVote vote : votes) {
            if (vote.getUser().equals(user)) {
                return vote.isVotedUp();
            }
        }
        return false;
    }

    /**
     * Determines if user voted down for the post
     *
     * @param user user to check
     *
     * @return true if user voted down for the post
     *         false if user voted up for the post
     *         false if user not voted for the post
     */
    public boolean isVotedDownBy(JCUser user) {
        for (PostVote vote : votes) {
            if (vote.getUser().equals(user)) {
                return !vote.isVotedUp();
            }
        }
        return false;
    }

    /**
     * Determines if user can vote for the post in specified direction.
     * If direction is true possibility to vote up will be checked.
     * If direction if false possibility to vote down will be checked.
     *
     * @param user user to check
     * @param direction direction of vote
     *
     * @return true if user can vote in specified direction otherwise false
     */
    public boolean canBeVotedBy(JCUser user, boolean direction) {
        return direction ? !isVotedUpBy(user) : !isVotedDownBy(user);
    }

    /**
     * Calculates changes in rating of the posh which will be made by specified vote
     *
     * @param vote vote for calculating changes;
     *
     * @return 0 if user can't vote in direction specified by vote
     *         +/- 1 if user votes first time in up/down direction
     *         +/- 2 if user changes his vote from negative to positive/from positive to negative
     */
    public int calculateRatingChanges(PostVote vote) {
        if (canBeVotedBy(vote.getUser(), vote.isVotedUp())) {
            if (isVotedDownBy(vote.getUser())) {
                return 2;
            } else if (isVotedUpBy(vote.getUser())) {
                return -2;
            } else {
                return vote.isVotedUp() ? 1 : -1;
            }
        }
        return 0;
    }

    /**
     * Creates and returns new list of comments of the current post which is not marked as removed. We cant use
     * hibernate "WHERE" clause due caching issue. Additionally we may need to retrieve comments which is marked as
     * removed in future. To manipulate with list of comments use {@link #getComments()} and
     * {@link #setComments(java.util.List)} methods.
     *
     * @return newly created list of comments which not marked as deleted
     */
    public List<PostComment> getNotRemovedComments() {
        List<PostComment> notRemovedComments = new ArrayList<>();
        for (PostComment comment : getComments()) {
            if (comment.getDeletionDate() == null) {
                notRemovedComments.add(comment);
            }
        }
        return notRemovedComments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<JCUser> getSubscribers() {
        return getTopic().getSubscribers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prepareUrlSuffix() {
        return URL_SUFFIX + getId();
    }
}
