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

/**
 * Represents the poll of the topic. Contains the list of related {@link PollOption}.
 * Poll may be either "single type" or "multiple type" also topic may have an end date.
 * Poll is tied to the life cycle of the topic({@link Topic}).
 *
 * @author Anuar Nurmakanov
 */
public class Poll extends Entity {
    private String title;
    private Boolean single;
    private DateTime endingDate;
    private List<PollOption> pollOptions = new ArrayList<PollOption>();
    private Topic topic;

    /**
     * Used only by Hibernate.
     */
    protected Poll() {
    }

    /**
     * Creates the Voting instance with required fields.
     * Voting is "single type" by default.
     *
     * @param title the voting title
     */
    public Poll(String title) {
        this.title = title;
        this.single = true;
    }

    /**
     * Get the voting title.
     *
     * @return the voting title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the the voting title.
     *
     * @param title the voting title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Voting may be either "single type" or "multiple type".
     *
     * @return <tt>true</tt> if the voting is "single type",
     *         <tt>false</tt> if the voting is "multiple type"
     */
    public Boolean isSingle() {
        return single;
    }

    /**
     * Set the voting type. Voting may be either "single type" or "multiple type".
     *
     * @param single <tt>true</tt> if the voting is "single type",
     *               <tt>false</tt> if the voting is "multiple type"
     */
    public void setSingle(boolean single) {
        this.single = single;
    }

    /**
     * Get the voting ending date.
     *
     * @return the voting ending date
     */
    public DateTime getEndingDate() {
        return endingDate;
    }

    /**
     * Set the voting ending date.
     *
     * @param endingDate the voting ending date
     */
    public void setEndingDate(DateTime endingDate) {
        this.endingDate = endingDate;
    }

    /**
     * Get the list of voting options.
     *
     * @return the list of voting options
     */
    public List<PollOption> getPollOptions() {
        return pollOptions;
    }

    /**
     * Set the list of voting options.
     *
     * @param pollOptions the list of voting options
     */
    protected void setPollOptions(List<PollOption> pollOptions) {
        this.pollOptions = pollOptions;
    }

    /**
     * Get the topic that contains this voting.
     *
     * @return the topic that contains this voting
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * Get the topic that contains this voting.
     *
     * @param topic the topic that contains this voting
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }


    /**
     * Add the voting option to this voting.
     *
     * @param options the voting option
     */
    public void addPollOptions(PollOption... options) {
        addPollOptions(Arrays.asList(options));
    }

    public void addPollOptions(List<PollOption> options) {
        for (PollOption option : options) {
            option.setPoll(this);
            this.pollOptions.add(option);
        }
    }

    /**
     * Counts the total number of votes in the poll.
     *
     * @return the total number of votes in the poll
     */
    public int getTotalVoteCount() {
        int totalVoteCount = 0;
        for (PollOption option : pollOptions) {
            totalVoteCount += option.getVoteCount();
        }
        return totalVoteCount;
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
