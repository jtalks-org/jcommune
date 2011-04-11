package org.jtalks.jcommune.model.entity;

import java.util.Date;

/**
 *
 * @author Pasha V
 */
public class Post extends Persistent {
    private Date postDate;
    private User userCreated;
    private String postContent;

    /**
     * @return the postDate
     */
    public Date getPostDate() {
        return postDate;
    }

    /**
     * @param postDate the postDate to set
     */
    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    /**
     * @return the userCreated
     */
    public User getUserCreated() {
        return userCreated;
    }

    /**
     * @param userCreated the userCreated to set
     */
    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

    /**
     * @return the postContent
     */
    public String getPostContent() {
        return postContent;
    }

    /**
     * @param postContent the postContent to set
     */
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }
}
