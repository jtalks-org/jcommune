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

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

/** @author stanislav bashkirtsev */
public class PostCommentTest {
    @Test
    public void isCreatedBy_successfulPath() {
        PostComment comment = commentBy(userWithUuid("11"));
        assertTrue(comment.isCreatedBy(userWithUuid("11")));
    }

    @Test
    public void isCreatedByIsFalseIfUsersNotEqual() {
        PostComment comment = commentBy(userWithUuid("11"));
        assertFalse(comment.isCreatedBy(userWithUuid("not-equal")));
    }

    @Test
    public void isCreatedByShouldFailIfNullPassed() {
        PostComment comment = commentBy(userWithUuid("11"));
        assertFalse(comment.isCreatedBy(null));
    }

    @Test
    public void isCreatedByShouldFailIfAuthorIsNull() {
        PostComment comment = commentBy(null);
        assertFalse(comment.isCreatedBy(userWithUuid("11")));
    }

    @Test
    public void isCreatedByShouldFailIfAuthorIsNullAndNullPassed() {
        PostComment comment = commentBy(null);
        assertFalse(comment.isCreatedBy(null));
    }

    private JCUser userWithUuid(String uuid) {
        JCUser user = new JCUser();
        user.setUuid(uuid);
        return user;
    }

    private PostComment commentBy(JCUser user) {
        PostComment comment = new PostComment();
        comment.setAuthor(user);
        return comment;
    }
    
    @Test
    public void getOwnerPostShouldReturnFirstPostOfCodeReviewTopic() {
        Post expectedOwnerPost = new Post();
        PostComment postComment = createCodeReviewCommentAttachedTo(expectedOwnerPost);
        
        Post actualOwnerPost = postComment.getOwnerPost();
        
        assertEquals("It should return first post of owner topic, cause it contains code review.",
                expectedOwnerPost, actualOwnerPost);
    }
    
    
    private PostComment createCodeReviewCommentAttachedTo(Post ownerPost) {
        Topic topic = new Topic();
        topic.addPost(ownerPost);
        CodeReview codeReview = new CodeReview();
        PostComment postComment = new PostComment();
        ownerPost.addComment(postComment);
        codeReview.setTopic(topic);
        
        return postComment;
    }
}
