package org.jtalks.jcommune.model.entity;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Pasha V
 */
public class Topic implements Persistent {

    private long id;
    private Date creationDate;
    private User userCreated;
    private String topicName;
    private List<Post> posts;

    public void addPost(Post newPost) {
        posts.add(newPost);
    }
    
    public void removePost(Post postToRemove) {
        posts.remove(postToRemove);
    }
    
    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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
     * @return the topicName
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * @param topicName the topicName to set
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    /**
     * @return the posts
     */
    public List<Post> getPosts() {
        return posts;
    }

    /**
     * @param posts the posts to set
     */
    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
