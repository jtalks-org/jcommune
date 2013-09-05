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

import org.jtalks.common.model.entity.Branch;
import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of SectionService
 *
 * @author Max Malakhov
 */
public class TransactionalSectionService extends AbstractTransactionalEntityService<Section, SectionDao>
        implements SectionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BranchService branchService;

    private UserService userService;

    private TopicDao topicDao;
    
    /**
     * Create an instance of entity based service
     *
     * @param dao           data access object, which should be able do all CRUD operations.
     * @param branchService autowired object, that represents service for the working with branches
     * @param userService autowired object, that represents service for the working with users
     * @param topicDao autowired object, that represents service for the working with topics
     */
    public TransactionalSectionService(SectionDao dao, BranchService branchService, UserService userService,
                                       TopicDao topicDao) {
        super(dao);
        this.branchService = branchService;
        this.userService = userService;
        this.topicDao = topicDao;
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
    public List<Section> getAllAvailableSections(long currentTopicId) {
        List<Section> result = new ArrayList<>();
        Topic topic = topicDao.get(currentTopicId);
        List<Section> sections = this.getDao().getAll();
        JCUser user = userService.getCurrentUser();
        for (Section section : sections) {
            List<Branch> branches = new ArrayList<>(section.getBranches());
            for (Branch branch : branches) {
                if (branch.equals(topic.getBranch())) {
                    branches.remove(branch);
                    break;
                }
            }
            if (getDao().getCountAvailableBranches(user, branches) > 0) {
                result.add(section);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void prepareSectionsForView(List<Section> sections) {
        for (Section section : sections) {
            List<Branch> branches = section.getBranches();
            branchService.fillStatisticInfo(branches);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Section deleteAllTopicsInSection(long sectionId) throws NotFoundException {
        Section section = get(sectionId);

        //Create tmp list to avoid ConcurrentModificationException
        List<Branch> loopList = new ArrayList<Branch>(section.getBranches());
        for (Branch branch : loopList) {
            branchService.deleteAllTopics(branch.getId());
        }

        logger.info("All branches for sections \"{}\" were deleted. " +
                "Section id: {}", section.getName(), section.getId());
        return section;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllTopicsInForum() throws NotFoundException {
        for (Section section : this.getAll()) {
            this.deleteAllTopicsInSection(section.getId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ifSectionIsVisible(Section section) throws AccessDeniedException {
        List<Branch> branches = section.getBranches();
        if (getDao().getCountAvailableBranches(userService.getCurrentUser(), branches) == 0) {
            throw new AccessDeniedException("Access denied to view for section " + section.getId());
        }
    }
}
