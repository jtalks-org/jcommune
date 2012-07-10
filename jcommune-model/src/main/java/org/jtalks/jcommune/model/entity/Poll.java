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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.*;

/**
 * Represents the poll of the topic. Contains the list of related {@link PollItem}.
 * Poll may be either "single type" or "multiple type" also topic may have an end date.
 * Poll is tied to the life cycle of the topic({@link Topic}).
 *
 * @author Anuar Nurmakanov
 */
public class Poll extends Entity {
    private String title;
    private boolean multipleAnswer;
    private DateTime endingDate;
    private List<PollItem> pollItems = new ArrayList<PollItem>();
    private Topic topic;

    public static final int MIN_TITLE_LENGTH = 3;
    public static final int MAX_TITLE_LENGTH = 120;
    public static final int MIN_ITEMS_NUMBER = 2;
    public static final int MAX_ITEMS_NUMBER = 50;

    /**
     * Used only by Hibernate.
     */
    protected Poll() {
    }

    /**
     * Creates the Poll instance with required fields.
     * Poll is "single answer type" by default.
     *
     * @param title the poll title
     */
    public Poll(String title) {
        this.title = title;
    }

    /**
     * Get the poll title.
     *
     * @return the poll title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the the poll title.
     *
     * @param title the poll title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Poll may be either "single answer type" or "multiple answer type".
     *
     * @return <tt>false</tt> if the poll is "single answer type",
     *         <tt>true</tt> if the poll is "multiple answer type"
     */
    public boolean isMultipleAnswer() {
        return multipleAnswer;
    }

    /**
     * Set the poll type. Poll may be either "single answer type"
     * or "multiple answer type".
     *
     * @param multipleAnswer <tt>false</tt> if the poll is "single answer type",
     *                     <tt>true</tt> if the poll is "multiple answer type"
     */
    public void setMultipleAnswer(boolean multipleAnswer) {
        this.multipleAnswer = multipleAnswer;
    }

    /**
     * Get the poll ending date.
     *
     * @return the poll ending date
     */
    public DateTime getEndingDate() {
        return endingDate;
    }

    /**
     * Set the poll ending date.
     *
     * @param endingDate the poll ending date
     */
    public void setEndingDate(DateTime endingDate) {
        this.endingDate = endingDate;
    }

    /**
     * Get the list of poll options.
     *
     * @return the list of poll options
     */
    public List<PollItem> getPollItems() {
        return pollItems;
    }

    /**
     * Set the list of poll options.
     *
     * @param pollItems the list of poll options
     */
    protected void setPollItems(List<PollItem> pollItems) {
        this.pollItems = pollItems;
    }

    /**
     * Get the topic that contains this poll.
     *
     * @return the topic that contains this poll
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * Get the topic that contains this poll.
     *
     * @param topic the topic that contains this poll
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }


    /**
     * Add the poll options to this poll.
     *
     * @param options the poll option
     */
    public void addPollOptions(PollItem... options) {
        addPollOptions(Arrays.asList(options));
    }

    /**
     * Add the list of poll options to this poll.
     *
     * @param options the list of poll options to this poll
     */
    public void addPollOptions(List<PollItem> options) {
        pollItems.addAll(options);
        forEach(options).setPoll(this);
    }

    /**
     * Counts the total count of votes in the poll.
     *
     * @return the total count of votes in the poll
     */
    public int getTotalVotesCount() {
        return sumFrom(pollItems, PollItem.class).getVotesCount();
    }

    /**
     * Evaluates the current activity of poll.
     *
     * @return <tt>true</tt>  if the poll is active
     *         <tt>false</tt>  if the poll is inactive
     */
    public boolean isActive() {
        return endingDate == null || endingDate.isAfterNow();
    }
}
