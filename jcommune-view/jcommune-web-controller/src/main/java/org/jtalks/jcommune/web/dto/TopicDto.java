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

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollItem;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.validation.annotations.BbCodeAwareSize;
import org.jtalks.jcommune.web.validation.annotations.ValidPoll;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link Topic} objects. Used for validation and binding to form.
 *
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 */
@ValidPoll(pollTitle = "pollTitle", pollItems = "pollItems", endingDate = "endingDate")
public class TopicDto {

    @Valid
    private Topic topic;

    @NotBlank
    @BbCodeAwareSize(min = Post.MIN_LENGTH, max = Post.MAX_LENGTH)
    private String bodyText;

    private boolean notifyOnAnswers;

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
        this.topic = topic;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
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
     * @return flag that indicates notification state(enabled or disabled)
     */
    public boolean isNotifyOnAnswers() {
        return notifyOnAnswers;
    }

    /**
     * Set flag that indicates notification state(enabled or disabled).
     *
     * @param notifyOnAnswers flag of notifications state
     */
    public void setNotifyOnAnswers(boolean notifyOnAnswers) {
        this.notifyOnAnswers = notifyOnAnswers;
    }

    public String getTopicTitle() {
        return topic.getTitle();
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    @Size(min = Poll.MIN_TITLE_LENGTH, max = Poll.MAX_TITLE_LENGTH)
    private String pollTitle;

    private String pollItems;

    private boolean multiple;

    private String endingDate;

    private Poll poll;


    public String getPollTitle() {
        return pollTitle;
    }

    public String getPollItems() {
        return pollItems;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setPollTitle(String pollTitle) {
        this.pollTitle = pollTitle;
    }

    public void setPollItems(String pollItems) {
        this.pollItems = pollItems;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }


    public Poll preparePollFromTopicDto() {
        Poll poll = new Poll(pollTitle);
        poll.setMultipleAnswer(multiple);
        if (endingDate != null) {
            DateTimeFormatter format = DateTimeFormat.forPattern(PollDto.DATE_FORMAT);
            poll.setEndingDate(format.parseDateTime(endingDate));
        }
        poll.addPollOptions(parseItems(pollItems));

        return poll;
    }

    /**
     * Prepare poll items list from string. Removes empty lines from.
     *
     * @param pollItems user input
     * @return processed poll items list
     */
    public static List<PollItem> parseItems(String pollItems) {
        List<PollItem> result = new ArrayList<PollItem>();
        String[] items = StringUtils.split(pollItems, LINE_SEPARATOR);
        for (String item : items) {
            //If user entered empty lines these lines are ignoring from validation.
            // Only meaningful lines are processed and user get processed output
            if (StringUtils.isNotBlank(item)) {
                PollItem pollItem = new PollItem(item);
                result.add(pollItem);
            }
        }

        return result;
    }

    public boolean hasPoll() {
        return StringUtils.isNotBlank(pollTitle) && StringUtils.isNotBlank(pollItems);
    }


}