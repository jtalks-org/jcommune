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
package org.jtalks.jcommune.web.dto;

import org.hibernate.validator.constraints.NotBlank;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollOption;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.web.util.PollUtil;
import org.jtalks.jcommune.web.validation.annotations.BbCodeAwareSize;
import org.jtalks.jcommune.web.validation.annotations.DateInStringFormat;
import org.jtalks.jcommune.web.validation.annotations.FutureDateInStringFormat;

import javax.validation.constraints.Size;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link Topic} objects. Used for validation and binding to form.
 *
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 */
public class TopicDto {
    @NotBlank
    @Size(min = Topic.MIN_NAME_SIZE, max = Topic.MAX_NAME_SIZE)
    private String topicName;

    @NotBlank
    @BbCodeAwareSize(min = Post.MIN_LENGTH, max = Post.MAX_LENGTH)
    private String bodyText;

    private int topicWeight;

    private boolean sticked;
    private boolean announcement;

    private long id;

    @Size(min = Poll.MIN_TITLE_LENGTH, max = Poll.MAX_TITLE_LENGTH)
    private String pollTitle;
    private String pollOptions;
    private String single;

    @DateInStringFormat
    @FutureDateInStringFormat
    private String endingDate;

    private Poll poll;

    /**
     * Plain object for topic creation
     */
    public TopicDto() {
    }

    /**
     * Create dto from {@link Topic}
     *
     * @param topic topic for conversion
     */
    public TopicDto(Topic topic) {
        topicName = topic.getTitle();
        bodyText = topic.getFirstPost().getPostContent();
        id = topic.getId();
        topicWeight = topic.getTopicWeight();
        sticked = topic.isSticked();
        announcement = topic.isAnnouncement();
        if (topic.getPoll() != null) {
            pollTitle = topic.getPoll().getTitle();
            pollOptions = topic.getPoll().getPollOptions().toString();
            single = String.valueOf(topic.getPoll().isSingleAnswer());
            endingDate = topic.getPoll().getEndingDate().toString();
        }
    }

    /**
     * @return topic id
     */
    public long getId() {
        return id;
    }

    /**
     * Set topic id.
     *
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get topic title.
     *
     * @return topic title
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * Set topic title.
     *
     * @param topicName name of topic
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    /**
     * Get first post content.
     *
     * @return first post content
     */
    public String getBodyText() {
        return bodyText;
    }

    /**
     * Set first post content.
     *
     * @param bodyText content of first post in topic
     */
    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    /**
     * @return priority of sticked topic
     */
    public int getTopicWeight() {
        return this.topicWeight;
    }

    /**
     * Set priority for a sticked topic.
     *
     * @param topicWeight priority(weight) of sticked topic
     */
    public void setTopicWeight(int topicWeight) {
        this.topicWeight = topicWeight;
    }

    /**
     * @return stickedness flag of topic
     */
    public boolean isSticked() {
        return this.sticked;
    }

    /**
     * Set flag of stickedness.
     *
     * @param sticked flag of stickedness
     */
    public void setSticked(boolean sticked) {
        this.sticked = sticked;
    }

    /**
     * @return announcement flag of topic
     */
    public boolean isAnnouncement() {
        return this.announcement;
    }

    /**
     * Set flag of announcement for a topic
     *
     * @param announcement flag of announcement
     */
    public void setAnnouncement(boolean announcement) {
        this.announcement = announcement;
    }

    public String getPollTitle() {
        return pollTitle;
    }

    public String getPollOptions() {
        return pollOptions;
    }

    public String getSingle() {
        return single;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setPollTitle(String pollTitle) {
        this.pollTitle = pollTitle;
    }

    public void setPollOptions(String pollOptions) {
        this.pollOptions = pollOptions;
    }

    public void setSingle(String single) {
        this.single = single;
    }


    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public Poll createPoll() {
        Poll poll = new Poll(pollTitle);
        //TODO is need to handle broken string here?
        poll.setSingleAnswer(Boolean.parseBoolean(single));
        if (endingDate != null) {
            poll.setEndingDate(PollUtil.parseDate(endingDate, Poll.DATE_FORMAT));
        }
        try {
            poll.addPollOptions(parseOptions(pollOptions));
        } catch (IOException e) {
            poll = null;
        }

        return poll;
    }

    private List<PollOption> parseOptions(String pollOptions) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(pollOptions));
        String line;
        List<PollOption> result = new ArrayList<PollOption>();
        while ((line = reader.readLine()) != null) {

            if (!line.equals("")) {
                PollOption option = new PollOption(line);
                result.add(option);
            }
        }
        return result;
    }
}