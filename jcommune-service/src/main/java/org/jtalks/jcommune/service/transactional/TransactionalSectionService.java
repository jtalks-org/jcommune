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

import org.jtalks.common.model.entity.Branch;
import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.SectionService;

/**
 * The implementation of SectionService
 *
 * @author Max Malakhov
 * @author Anuar Nurmakanov
 */

public class TransactionalSectionService extends AbstractTransactionalEntityService<Section, SectionDao>
        implements SectionService {
    private BranchDao branchDao;
    private TopicDao topicDao;
    private PostDao postDao;
    
    /**
     * Create an instance of entity based service
     *
     * @param dao data access object, which should be able do all CRUD operations.
     * @param branchDao data access object for branches
     */
    public TransactionalSectionService(
            SectionDao dao,
            BranchDao branchDao,
            TopicDao topicDao,
            PostDao postDao) {
        super(dao);
        this.branchDao = branchDao;
        this.topicDao = topicDao;
        this.postDao = postDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Section> getAll() {
        return this.getDao().getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fetchBranchesAndFillStatistic(List<Section> sections) {
        for (Section section : sections) {
            List<Branch> branches = section.getBranches();
            if(branches != null) {
                fillCountInformation(branches);
            }
        }
    }
    
    /**
     * Fills an information about count of topics, count of posts for each branch.
     * 
     * @param branches the list of branches
     */
    private void fillCountInformation(List<Branch> branches) {
        for(Branch branch: branches) {
            org.jtalks.jcommune.model.entity.Branch jcommuneBranch = 
                    (org.jtalks.jcommune.model.entity.Branch) branch;
            int postsCount = branchDao.getCountPostsInBranch(jcommuneBranch);
            jcommuneBranch.setPostsCount(postsCount);
            int topicsCount = branchDao.getCountTopicsInBranch(jcommuneBranch);
            jcommuneBranch.setTopicsCount(topicsCount);
            
            Topic lastUpdatedTopic = topicDao.getLastUpdatedTopicInBranch(jcommuneBranch);
            if (lastUpdatedTopic != null) {
                Post lastPost = postDao.getLastPostInTopic(lastUpdatedTopic);
                jcommuneBranch.setLastPostInLastUpdatedTopic(lastPost);
            }
        }
    }
}
