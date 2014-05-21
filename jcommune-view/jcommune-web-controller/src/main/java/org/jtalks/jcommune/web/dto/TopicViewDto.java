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

import org.jtalks.jcommune.model.entity.*;

/**
 * @author  Mikhail Stryzhonok.
 */
public class TopicViewDto {

    private long id;
    private String title;
    private boolean sticked;
    private boolean announcement;
    private boolean hasPoll;
    private boolean closed;
    private int views;
    private int postCount;
    private boolean hasUpdates;
    private Long firstUnreadPostId;
    private JCUser topicStarter;
    private Post lastPost;
    private Branch branch;
    private CodeReview codeReview;

    public TopicViewDto(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.sticked = topic.isSticked();
        this.announcement = topic.isAnnouncement();
        this.hasPoll = topic.isHasPoll();
        this.closed = topic.isClosed();
        this.views = topic.getViews();
        this.postCount = topic.getPostCount();
        this.hasUpdates = topic.isHasUpdates();
        this.firstUnreadPostId = topic.getFirstUnreadPostId();
        this.topicStarter = topic.getTopicStarter();
        this.lastPost = topic.getLastPost();
        this.branch = topic.getBranch();
        this.codeReview = topic.getCodeReview();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSticked() {
        return sticked;
    }

    public void setSticked(boolean sticked) {
        this.sticked = sticked;
    }

    public boolean isAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(boolean announcement) {
        this.announcement = announcement;
    }

    public boolean isHasPoll() {
        return hasPoll;
    }

    public void setHasPoll(boolean hasPoll) {
        this.hasPoll = hasPoll;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public boolean isHasUpdates() {
        return hasUpdates;
    }

    public void setHasUpdates(boolean hasUpdates) {
        this.hasUpdates = hasUpdates;
    }

    public Long getFirstUnreadPostId() {
        return firstUnreadPostId;
    }

    public void setFirstUnreadPostId(Long firstUnreadPostId) {
        this.firstUnreadPostId = firstUnreadPostId;
    }

    public JCUser getTopicStarter() {
        return topicStarter;
    }

    public void setTopicStarter(JCUser topicStarter) {
        this.topicStarter = topicStarter;
    }

    public Post getLastPost() {
        return lastPost;
    }

    public void setLastPost(Post lastPost) {
        this.lastPost = lastPost;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public CodeReview getCodeReview() {
        return codeReview;
    }

    public void setCodeReview(CodeReview codeReview) {
        this.codeReview = codeReview;
    }
}
