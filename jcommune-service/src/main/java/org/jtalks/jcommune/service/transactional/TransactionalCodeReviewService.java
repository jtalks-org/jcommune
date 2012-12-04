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
package org.jtalks.jcommune.service.transactional;

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.dao.CodeReviewDao;
import org.jtalks.jcommune.model.entity.CodeReview;
import org.jtalks.jcommune.model.entity.CodeReviewComment;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.CodeReviewService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

/**
 * The implementation of (@link {@link CodeReviewService}
 * 
 * @author Vyacheslav Mishcheryakov
 */
public class TransactionalCodeReviewService extends AbstractTransactionalEntityService<CodeReview, CodeReviewDao> 
        implements CodeReviewService {

    private UserService userService;
    
    TransactionalCodeReviewService(CodeReviewDao dao,
                                   UserService userService) {
        super(dao);
        this.userService = userService;
    }
    
    @Override
    public CodeReviewComment addComment(Long reviewId, int lineNumber, String body) throws NotFoundException {
        CodeReview review = get(reviewId);
        JCUser currentUser = userService.getCurrentUser(); 
        
        CodeReviewComment comment = new CodeReviewComment();
        comment.setLineNumber(lineNumber);
        comment.setBody(body);
        comment.setCreationDate(new DateTime(System.currentTimeMillis()));
        comment.setAuthor(currentUser);
        
        review.addComment(comment);
        getDao().update(review);
        
        return comment;
    }

}
