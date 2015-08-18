package org.jtalks.jcommune.model.entity;

import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Entity;

/**
 * Represents draft of a post
 *
 * @author Mikhail Stryzhonok
 */
public class PostDraft extends Entity {

    private String content;
    private Topic topic;
    private JCUser author;
    private DateTime lastSaved;

    public PostDraft() {
    }

    public PostDraft(String content, JCUser author) {
        this.content = content;
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public JCUser getAuthor() {
        return author;
    }

    public void setAuthor(JCUser author) {
        this.author = author;
    }

    public DateTime getLastSaved() {
        return lastSaved;
    }

    protected void setLastSaved(DateTime lastSaved) {
        this.lastSaved = lastSaved;
    }

    /**
     * Sets current datetime to last saved property
     */
    public void updateLastSavedTime() {
        lastSaved = new DateTime();
    }
}
