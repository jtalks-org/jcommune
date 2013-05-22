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
public class CodeReviewCommentTest {
    @Test
    public void isCreatedBy_successfulPath() {
        CodeReviewComment comment = commentBy(userWithUuid("11"));
        assertTrue(comment.isCreatedBy(userWithUuid("11")));
    }

    @Test
    public void isCreatedByIsFalseIfUsersNotEqual() {
        CodeReviewComment comment = commentBy(userWithUuid("11"));
        assertFalse(comment.isCreatedBy(userWithUuid("not-equal")));
    }

    @Test
    public void isCreatedByShouldFailIfNullPassed() {
        CodeReviewComment comment = commentBy(userWithUuid("11"));
        assertFalse(comment.isCreatedBy(null));
    }

    @Test
    public void isCreatedByShouldFailIfAuthorIsNull() {
        CodeReviewComment comment = commentBy(null);
        assertFalse(comment.isCreatedBy(userWithUuid("11")));
    }

    @Test
    public void isCreatedByShouldFailIfAuthorIsNullAndNullPassed() {
        CodeReviewComment comment = commentBy(null);
        assertFalse(comment.isCreatedBy(null));
    }

    private JCUser userWithUuid(String uuid) {
        JCUser user = new JCUser();
        user.setUuid(uuid);
        return user;
    }

    private CodeReviewComment commentBy(JCUser user) {
        CodeReviewComment comment = new CodeReviewComment();
        comment.setAuthor(user);
        return comment;
    }
    
    @Test
    public void getOwnerPostShouldReturnFirstPostOfCodeReviewTopic() {
        Post expectedOwnerPost = new Post();
        CodeReviewComment codeReviewComment = createCodeReviewCommentAttachedTo(expectedOwnerPost);
        
        Post actualOwnerPost = codeReviewComment.getOwnerPost();
        
        assertEquals("It should return first post of owner topic, cause it contains code review.",
                expectedOwnerPost, actualOwnerPost);
    }
    
    
    private CodeReviewComment createCodeReviewCommentAttachedTo(Post ownerPost) {
        Topic topic = new Topic();
        topic.addPost(ownerPost);
        CodeReview codeReview = new CodeReview();
        CodeReviewComment codeReviewComment = new CodeReviewComment();
        codeReview.addComment(codeReviewComment);
        codeReviewComment.setCodeReview(codeReview);
        codeReview.setTopic(topic);
        
        return codeReviewComment;
    }
}
