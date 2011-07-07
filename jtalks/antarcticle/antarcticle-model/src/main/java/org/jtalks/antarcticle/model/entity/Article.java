package org.jtalks.antarcticle.model.entity;

import org.joda.time.DateTime;

/**
 *
 * @author Dmitry
 */
public class Article extends Persistent {
    
    private DateTime creationDate;
    private User userCreated;
    private String articleTopic;
    private String articleContent;

    public Article() {
    }

    public Article(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getArticleTopic() {
        return articleTopic;
    }

    public void setArticleTopic(String articleTopic) {
        this.articleTopic = articleTopic;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }
    
    
    
}
