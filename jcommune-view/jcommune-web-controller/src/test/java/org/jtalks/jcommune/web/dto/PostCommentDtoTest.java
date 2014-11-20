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

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.CommentProperty;
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PropertyType;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PostCommentDtoTest {

    @Test
    public void testConstructor() {
        PostComment comment = createComment();
        comment.addCustomProperty(new CommentProperty(CodeReviewCommentDto.LINE_NUMBER_PROPERTY_NAME,
                PropertyType.INT, "1"));
        
        CodeReviewCommentDto dto = new CodeReviewCommentDto(comment);
        
        assertEquals(dto.getId(), comment.getId());
        assertEquals(dto.getLineNumber(), 1);
        assertEquals(dto.getBody(), comment.getBody());
        assertEquals(dto.getAuthorId(), comment.getAuthor().getId());
        assertEquals(dto.getAuthorUsername(), comment.getAuthor().getEncodedUsername());
    }
    
    private PostComment createComment() {
        PostComment comment = new PostComment();
        comment.setId(1L);
        comment.setAuthor(new JCUser("username1", "mail1", "password1" ));
        comment.setBody("Comment1 body");
        comment.setCreationDate(new DateTime(1));
        
        return comment;
    }
}
