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

import java.util.Arrays;

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
        PostComment postComment = createCommentAttachedTo(expectedOwnerPost);
        
        Post actualOwnerPost = postComment.getOwnerPost();
        
        assertEquals("It should return first post of owner topic, cause it contains code review.",
                expectedOwnerPost, actualOwnerPost);
    }

    @Test
    public void testAddCustomProperty() {
        PostComment comment = new PostComment();
        CommentProperty property = new CommentProperty();

        comment.addCustomProperty(property);

        assertTrue(comment.getCustomProperties().contains(property));
        assertEquals(comment, property.getComment());
    }

    @Test
    public void testAddCustomProperties() {
        PostComment comment = new PostComment();
        CommentProperty property1 = new CommentProperty();
        CommentProperty property2 = new CommentProperty();

        comment.addCustomProperties(Arrays.asList(property1, property2));

        assertEquals(2, comment.getCustomProperties().size());
        assertTrue(comment.getCustomProperties().contains(property1));
        assertTrue(comment.getCustomProperties().contains(property2));
        assertEquals(comment, property1.getComment());
        assertEquals(comment, property2.getComment());
    }
    
    
    private PostComment createCommentAttachedTo(Post ownerPost) {
        Topic topic = new Topic();
        topic.addPost(ownerPost);
        PostComment postComment = new PostComment();
        ownerPost.addComment(postComment);
        
        return postComment;
    }
}
