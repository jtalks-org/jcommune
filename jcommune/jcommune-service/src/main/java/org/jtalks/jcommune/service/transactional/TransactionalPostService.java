/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Post service class. This class contains method needed to manipulate with Post persistent entity.
 *
 * @author Osadchuck Eugeny
 */
public class TransactionalPostService extends AbstractTransactionlaEntityService<Post> implements PostService {

    private final Logger logger = LoggerFactory.getLogger(TransactionalPostService.class);
    private PostDao postDao;

    /**
     * Create an instance of Post entity based service
     *
     * @param dao - data access object, which should be able do all CRUD operations with post entity.
     */
    public TransactionalPostService(PostDao dao) {
        super(dao);
        this.postDao = dao;
    }
}
