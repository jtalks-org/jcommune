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
 * Represents user's vote for post. If {@link #votedUp} is true that means what user voted up for
 * the post if {@link #votedUp} is false that means that user voted down for the post.
 *
 * @author Mikhail Stryzhonok
 */
public class PostVote extends Entity {
    private JCUser user;
    private Post post;
    private DateTime voteDate = new DateTime();
    private boolean votedUp;

    /**
     * Needed for Hibernate usage
     */
    public PostVote() {
    }

    public PostVote(JCUser user) {
        this.user = user;
    }

    /**
     * Gets the voted user
     *
     * @return voted user
     */
    public JCUser getUser() {
        return user;
    }

    /**
     * Sets specified user as voter
     *
     * @param user user to be set
     */
    public void setUser(JCUser user) {
        this.user = user;
    }

    /**
     * Gets the post to vote
     *
     * @return post to vote
     */
    public Post getPost() {
        return post;
    }

    /**
     * Sets specified post as post to vote
     *
     * @param post post to be set
     */
    public void setPost(Post post) {
        this.post = post;
    }

    /**
     * Gets vote date
     *
     * @return vote date
     */
    public DateTime getVoteDate() {
        return voteDate;
    }

    /**
     * Sets specified date as vote date
     *
     * @param voteDate date to set
     */
    public void setVoteDate(DateTime voteDate) {
        this.voteDate = voteDate;
    }

    /**
     * Checks if {@link #user} voted up for {@link #post}
     *
     * @return true if user voted up or false if voted down
     */
    public boolean isVotedUp() {
        return votedUp;
    }

    /**
     * Sets boolean value that specifies if user voted up or down
     *
     * @param votedUp boolean value to set
     */
    public void setVotedUp(boolean votedUp) {
        this.votedUp = votedUp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        PostVote postVote = (PostVote) o;

        if (!post.equals(postVote.post)) {
            return false;
        }
        return user.equals(postVote.user);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + post.hashCode();
        return result;
    }
}
