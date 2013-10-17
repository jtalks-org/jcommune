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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.TopicModificationService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The implementation of BranchService
 *
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 * @author masyan
 */

public class TransactionalBranchService extends AbstractTransactionalEntityService<Branch, BranchDao>
        implements BranchService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SectionDao sectionDao;
    private TopicDao topicDao;
    private TopicModificationService topicService;
    private UserService userService;

    /**
     * Create an instance of entity based service
     *
     * @param branchDao    data access object, which should be able do all CRUD operations.
     * @param sectionDao   used for checking branch existence.
     * @param topicDao     data access object for operations with topics
     * @param topicService service to perform complex operations with topics
     * @param userService  service to perform complex operations with users
     */
    public TransactionalBranchService(
            BranchDao branchDao,
            SectionDao sectionDao,
            TopicDao topicDao,
            TopicModificationService topicService,
            UserService userService) {
        super(branchDao);
        this.sectionDao = sectionDao;
        this.topicDao = topicDao;
        this.topicService = topicService;
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Branch> getAllAvailableBranches(long currentTopicId) {
        JCUser user = userService.getCurrentUser();
        if (user.getGroups().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<Branch> branches = new ArrayList<Branch>(this.getDao().getAllAvailableBranches(user));
        Topic topic = topicDao.get(currentTopicId);
        branches.remove(topic.getBranch());
        return branches;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Branch> getAvailableBranchesInSection(long sectionId, long currentTopicId) throws NotFoundException {
        if (!sectionDao.isExist(sectionId)) {
            throw new NotFoundException("Section with id: " + sectionId + " not found");
        }

        JCUser user = userService.getCurrentUser();
        if (user.getGroups().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Section section = sectionDao.get(sectionId);
        List<Branch> branches = new ArrayList<Branch>(this.getDao().getAllAvailableBranchesInSection(user, section));
        Topic topic = topicDao.get(currentTopicId);
        branches.remove(topic.getBranch());
        return branches;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillStatisticInfo(List<org.jtalks.common.model.entity.Branch> branches) {

        for (org.jtalks.common.model.entity.Branch commonBranch : branches) {
            Branch jcommuneBranch = (Branch) commonBranch;
            int postsCount = getDao().getCountPostsInBranch(jcommuneBranch);
            jcommuneBranch.setPostsCount(postsCount);
            int topicsCount = topicDao.countTopics(jcommuneBranch);
            jcommuneBranch.setTopicsCount(topicsCount);
            //TODO Was removed till milestone 2 due to performance issues
//            JCUser user = userService.getCurrentUser();
//            if (!user.isAnonymous()) {
//                boolean isUnreadPosts = getDao().isUnreadPostsInBranch(jcommuneBranch, user);
//                jcommuneBranch.setUnreadPosts(isUnreadPosts);
//            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#id, 'BRANCH', 'BranchPermission.VIEW_TOPICS')")
    public Branch get(Long id) throws NotFoundException {
        return super.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Branch deleteAllTopics(long branchId) throws NotFoundException {
        Branch branch = get(branchId);

        // Create tmp list to avoid ConcurrentModificationException
        List<Topic> loopList = new ArrayList<Topic>(branch.getTopics());
        for (Topic topic : loopList) {
            topicService.deleteTopicSilent(topic.getId());
        }

        logger.info("All topics for branch \"{}\" were deleted. " +
                "Branch id: {}", branch.getName(), branch.getId());
        return branch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void changeBranchInfo(long componentId, long branchId, String title, String description)
            throws NotFoundException {
        Branch branch = get(branchId);
        branch.setName(title);
        branch.setDescription(description);
        getDao().saveOrUpdate(branch);
    }
}
