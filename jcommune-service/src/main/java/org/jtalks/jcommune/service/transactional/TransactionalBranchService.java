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

import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.Section;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dto.GroupsPermissions;
import org.jtalks.jcommune.model.dto.PermissionChanges;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.service.PluginBranchService;
import org.jtalks.jcommune.service.BranchLastPostService;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.TopicModificationService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.AdministrationGroup;
import org.jtalks.jcommune.service.security.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.*;

/**
 * The implementation of BranchService
 *
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 * @author masyan
 */

public class TransactionalBranchService extends AbstractTransactionalEntityService<Branch, BranchDao>
        implements BranchService, PluginBranchService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SectionDao sectionDao;
    private GroupDao groupDao;
    private TopicDao topicDao;
    private TopicModificationService topicService;
    private PermissionService permissionService;
    private BranchLastPostService lastPostService;

    /**
     * Create an instance of entity based service
     *
     * @param branchDao         data access object, which should be able do all CRUD operations.
     * @param sectionDao        used for checking branch existence.
     * @param topicDao          data access object for operations with topics
     * @param topicService      service to perform complex operations with topics
     * @param permissionService service to perform permissions operations
     */
    public TransactionalBranchService(
            BranchDao branchDao,
            SectionDao sectionDao,
            TopicDao topicDao,
            GroupDao groupDao,
            TopicModificationService topicService,
            PermissionService permissionService,
            BranchLastPostService lastPostService) {
        super(branchDao);
        this.sectionDao = sectionDao;
        this.topicDao = topicDao;
        this.topicService = topicService;
        this.permissionService = permissionService;
        this.groupDao = groupDao;
        this.lastPostService = lastPostService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Branch> getAllAvailableBranches(long currentTopicId) {
        List<Section> allSections = sectionDao.getAll();
        List<Branch> allBranches = new ArrayList<>();
        for (Section section : allSections) {
            allBranches.addAll((List) section.getBranches());
        }
        return getBranchesWithViewPermission(currentTopicId, allBranches);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Branch> getAvailableBranchesInSection(long sectionId, long currentTopicId) throws NotFoundException {
        if (!sectionDao.isExist(sectionId)) {
            throw new NotFoundException("Section with id: " + sectionId + " not found");
        }

        Section section = sectionDao.get(sectionId);
        List<Branch> branches = (List) section.getBranches();
        return getBranchesWithViewPermission(currentTopicId, branches);
    }

    private List<Branch> getBranchesWithViewPermission(Long topicId, List<Branch> branches) {
        Topic topic = topicDao.get(topicId);
        List<Branch> result = new ArrayList<>();
        for (Branch branch : branches) {
            if (permissionService.hasBranchPermission(branch.getId(), BranchPermission.VIEW_TOPICS)) {
                result.add(branch);
            }
        }
        result.remove(topic.getBranch());
        return result;
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
            if (jcommuneBranch.getLastPost() == null) {
                lastPostService.refreshLastPostInBranch(jcommuneBranch);
            }
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
        List<Topic> loopList = new ArrayList<>(branch.getTopics());
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

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void createNewBranch(long componentId, long sectionId, String title, String description) {
        Section section = sectionDao.get(sectionId);
        Branch branch = new Branch(title, description);
        branch.setSection(section);
        section.addOrUpdateBranch(branch);
        sectionDao.saveOrUpdate(section);
        //add default permission to view topics (for group Registered users)
        Group registeredUsersGroup = groupDao.getGroupByName(AdministrationGroup.USER.getName());
        Collection<Group> groups = Arrays.asList(registeredUsersGroup);
        PermissionChanges permissionChanges = new PermissionChanges(BranchPermission.VIEW_TOPICS, groups,
                Collections.<Group>emptyList());
        permissionService.changeGrants(branch, permissionChanges);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkIfBranchExists(long branchId) throws NotFoundException {
        super.get(branchId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public GroupsPermissions getPermissionsFor(long componentId, long branchId)
            throws NotFoundException {
        return permissionService.getPermissionsFor(get(branchId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public <T extends JtalksPermission> List<Group> getPermissionGroupsFor(long componentId, long branchId, boolean allowed, T permission)
            throws NotFoundException {
        GroupsPermissions allPermissions = permissionService.getPermissionsFor(get(branchId));
        if (allowed) {
            return allPermissions.getAllowed(permission);
        }

        return allPermissions.getRestricted(permission);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void changeBranchPermissions(long componentId, long branchId, boolean allowed, PermissionChanges changes)
            throws NotFoundException {
        Branch branch = get(branchId);
        if (allowed) {
            permissionService.changeGrants(branch, changes);
        } else {
            permissionService.changeRestrictions(branch, changes);
        }
    }
}
