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

import org.jtalks.jcommune.model.dao.TopicBranchDao;
import org.jtalks.jcommune.model.entity.TopicBranch;
import org.jtalks.jcommune.service.TopicBranchService;

/**
 * @author Vitaliy Kravchenko
 */

public class TransactionalTopicBranchService extends AbstractTransactionalEntityService<TopicBranch, TopicBranchDao> implements TopicBranchService {


    /**
     * Create an instance of entity based service
     *
     * @param topicBranchDao - data access object, which should be able do all CRUD operations.
     */
    public TransactionalTopicBranchService(TopicBranchDao topicBranchDao) {
        this.dao = topicBranchDao;
    }
}