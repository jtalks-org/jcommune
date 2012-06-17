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

import java.util.List;

import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

/**
 * The implementation of BranchService
 *
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 */

public class TransactionalBranchService extends AbstractTransactionalEntityService<Branch, BranchDao>
        implements BranchService {

    private SectionDao sectionDao;
    private TopicDao topicDao;
    private PostDao postDao;

    /**
     * Create an instance of entity based service
     *
     * @param branchDao data access object, which should be able do all CRUD operations.
     * @param sectionDao used for checking branch existance.
     * @param topicDao data access object for operations with topics  
     * @param postDao data access object for operations with posts 
     */
    public TransactionalBranchService(
            BranchDao branchDao,
            SectionDao sectionDao,
            TopicDao topicDao,
            PostDao postDao) {
        super(branchDao);
        this.sectionDao = sectionDao;
        this.topicDao = topicDao;
        this.postDao = postDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Branch> getAllBranches() {
        return this.getDao().getAllBranches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Branch> getBranchesInSection(long sectionId) throws NotFoundException {
        if (!sectionDao.isExist(sectionId)) {
            throw new NotFoundException("Section with id: " + sectionId + " not found");
        }

        return this.getDao().getBranchesInSection(sectionId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void fillStatisticInfo(List<org.jtalks.common.model.entity.Branch> branches) {
        for(org.jtalks.common.model.entity.Branch commonBranch: branches) {
            Branch jcommuneBranch = (Branch) commonBranch;
            int postsCount = getDao().getCountPostsInBranch(jcommuneBranch);
            jcommuneBranch.setPostsCount(postsCount);
            int topicsCount = topicDao.getCountTopicsInBranch(jcommuneBranch);
            jcommuneBranch.setTopicsCount(topicsCount);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void fillLastPostInLastUpdatedTopic(List<org.jtalks.common.model.entity.Branch> branches) {
        for(org.jtalks.common.model.entity.Branch commonBranch: branches) {
            Branch jcommuneBranch = (Branch) commonBranch;
            Topic lastUpdatedTopic = topicDao.getLastUpdatedTopicInBranch(jcommuneBranch);
            if (lastUpdatedTopic != null) {
                Post lastPost = postDao.getLastPostInTopic(lastUpdatedTopic);
                jcommuneBranch.setLastPostInLastUpdatedTopic(lastPost);
            }
        }
    }
}