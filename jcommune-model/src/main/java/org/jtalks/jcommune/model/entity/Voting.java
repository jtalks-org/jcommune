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

import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Entity;

/**
 * Represents the voting of the topic.
 * Contains the list of related {@link VotingOption}.
 * 
 * @author Anuar Nurmakanov
 */
public class Voting extends Entity {
    private String title;
    private boolean single;
    private DateTime endingDate;
    private List<VotingOption> votingOptions = new ArrayList<VotingOption>();
    private Topic topic;
    
    /**
     * Used only by Hibernate.
     */
    protected Voting() {
    }
    
    /**
     * Creates the Voting instance with required fields.
     * Voting is "single type" by default.
     * 
     * @param title the voting title
     */
    public Voting(String title) {
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
     * 
     * @return
     */
    public boolean isSingle() {
        return single;
    }

    /**
     * 
     * @param single
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
    public List<VotingOption> getVotingOptions() {
        return votingOptions;
    }

    /**
     * Set the list of voting options.
     * 
     * @param votingOptions the list of voting options
     */
    protected void setVotingOptions(List<VotingOption> votingOptions) {
        this.votingOptions = votingOptions;
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
     * 
     * @param option
     */
    public void addVotingOption(VotingOption option) {
        option.setVoting(this);
        this.votingOptions.add(option);
    }
}
