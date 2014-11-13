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
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * 
 * @author Vyacheslav Mishcheryakov
 *
 */
public class CodeReviewDtoTest {
    
    private static final long POST_ID = 1L;
    
    @Test
    public void testDefaultConstructor() {
        CodeReviewDto dto = new CodeReviewDto();

        assertEquals(dto.getPostId(), 0);
        assertNotNull(dto.getComments());
        assertEquals(dto.getComments().size(), 0);
    }

    @Test
    public void testPrototypeConstructor() {
        Post post = getPost();

        CodeReviewDto dto = new CodeReviewDto(post);

        assertEquals(dto.getPostId(), POST_ID);
        assertEquals(dto.getComments().size(), 1);
    }

    private Post getPost() {
        JCUser user = new JCUser("name", "mail@mail.ru","pwd");
        user.setId(1L);
        Post post = new Post(user, "some mad text");
        post.setId(POST_ID);
        PostComment comment = new PostComment();
        comment.setBody("text");
        comment.setAuthor(user);
        comment.putAttribute(CodeReviewCommentDto.LINE_NUMBER_PROPERTY_NAME, "1");
        post.addComment(comment);
        return post;
    }
    
}
