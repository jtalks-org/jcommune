/* 
 * JTalks for uniting people
 * Copyright (C) 2011  JavaTalks Team
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Also add information on how to contact you by electronic and paper mail.
 * 
 * This file creation date: Apr 12, 2011 / 8:05:19 PM
 * The JTalks Project
 * http://www.jtalks.org
 */
package org.jtalks.jcommune.model.entity;

import java.util.Date;

/**
 * Represents the post of the forum
 * 
 * @author Temdegon
 */
public class Post extends Persistent {

    private Date postDate;
    private User userCreated;
    private String postContent;
    private Topic post;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Post other = (Post) obj;
        if (this.postDate != other.postDate && (this.postDate == null || !this.postDate.equals(other.postDate))) {
            return false;
        }
        if (this.userCreated != other.userCreated && (this.userCreated == null || !this.userCreated.equals(other.userCreated))) {
            return false;
        }
        if ((this.postContent == null) ? (other.postContent != null) : !this.postContent.equals(other.postContent)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.postDate != null ? this.postDate.hashCode() : 0);
        hash = 17 * hash + (this.userCreated != null ? this.userCreated.hashCode() : 0);
        hash = 17 * hash + (this.postContent != null ? this.postContent.hashCode() : 0);
        return hash;
    }

    /**
     * @return the post
     */
    public Topic getPost() {
        return post;
    }

    /**
     * @param post the post to set
     */
    public void setPost(Topic post) {
        this.post = post;
    }
}
