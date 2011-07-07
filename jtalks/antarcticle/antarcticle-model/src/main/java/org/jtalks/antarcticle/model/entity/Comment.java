package org.jtalks.antarcticle.model.entity;

import org.joda.time.DateTime;

/**
 *
 * @author Dmitry
 */
public class Comment extends Persistent {
    
    private User userCommented;
    private DateTime creationDate;
    private String commentContent;
    private Article article;

    public Comment() {
    }

    public Comment(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Comment(User userCommented, DateTime creationDate, String commentContent, Article article) {
        this.userCommented = userCommented;
        this.creationDate = creationDate;
        this.commentContent = commentContent;
        this.article = article;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User getUserCommented() {
        return userCommented;
    }

    public void setUserCommented(User userCommented) {
        this.userCommented = userCommented;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
    
}
