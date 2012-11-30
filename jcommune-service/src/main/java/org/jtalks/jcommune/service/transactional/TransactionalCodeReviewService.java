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

import org.jtalks.jcommune.model.dao.CodeReviewDao;
import org.jtalks.jcommune.model.entity.CodeReview;
import org.jtalks.jcommune.service.CodeReviewService;

/**
 * The implementation of (@link {@link CodeReviewService}
 * 
 * @author Vyacheslav Mishcheryakov
 */
public class TransactionalCodeReviewService extends AbstractTransactionalEntityService<CodeReview, CodeReviewDao> 
        implements CodeReviewService {

    TransactionalCodeReviewService(CodeReviewDao dao) {
        super(dao);
    }

}
