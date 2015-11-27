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

import org.jtalks.common.service.security.SecurityContextFacade;
import org.jtalks.jcommune.model.dao.TopicDraftDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.TopicDraft;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.jtalks.jcommune.plugin.api.filters.StateFilter;
import org.jtalks.jcommune.plugin.api.filters.TopicTypeFilter;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.jtalks.jcommune.plugin.api.service.PluginTopicDraftService;
import org.jtalks.jcommune.service.TopicDraftService;
import org.jtalks.jcommune.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * The implementation of TopicDraftService interface.
 *
 * @author Dmitry S. Dolzhenko
 */
public class TransactionalTopicDraftService implements TopicDraftService, PluginTopicDraftService {

    private final UserService userService;
    private final TopicDraftDao topicDraftDao;
    private final SecurityContextFacade securityContextFacade;
    private final PluginLoader pluginLoader;
    private final PermissionEvaluator permissionEvaluator;

    public TransactionalTopicDraftService(UserService userService,
                                          TopicDraftDao topicDraftDao,
                                          SecurityContextFacade securityContextFacade,
                                          PermissionEvaluator permissionEvaluator,
                                          PluginLoader pluginLoader) {
        this.userService = userService;
        this.topicDraftDao = topicDraftDao;
        this.securityContextFacade = securityContextFacade;
        this.permissionEvaluator = permissionEvaluator;
        this.pluginLoader = pluginLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("isAuthenticated()")
    public TopicDraft getDraft() {
        JCUser user = userService.getCurrentUser();
        return topicDraftDao.getForUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("( (not #draft.codeReview) and (not #draft.plugable) " +
            "and hasPermission(#draft.branchId, 'BRANCH', 'BranchPermission.CREATE_POSTS')) " +
            "or (#draft.codeReview " +
            "and hasPermission(#draft.branchId, 'BRANCH', 'BranchPermission.CREATE_CODE_REVIEW')) " +
            " or #draft.plugable")
    public TopicDraft saveOrUpdateDraft(TopicDraft draft) {
        assertCreationAllowedForPlugableTopic(draft);

        JCUser user = userService.getCurrentUser();

        TopicDraft currentDraft = topicDraftDao.getForUser(user);
        if (currentDraft == null) {
            currentDraft = draft;
            currentDraft.setTopicStarter(user);
        } else {
            currentDraft.setContent(draft.getContent());
            currentDraft.setTitle(draft.getTitle());
            currentDraft.setBranchId(draft.getBranchId());
            currentDraft.setTopicType(draft.getTopicType());

            /* When we save draft that does not contain pollTitle and pollItemsValue (e.g. code review),
               we should not overwrite already existing values of these fields. */
            if (draft.getPollTitle() != null || draft.getPollItemsValue() != null) {
                currentDraft.setPollTitle(draft.getPollTitle());
                currentDraft.setPollItemsValue(draft.getPollItemsValue());
            }
        }

        currentDraft.updateLastSavedTime();
        topicDraftDao.saveOrUpdate(currentDraft);

        return currentDraft;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("isAuthenticated()")
    public void deleteDraft() {
        JCUser user = userService.getCurrentUser();
        topicDraftDao.deleteByUser(user);
    }

    /**
     * Checks for draft of plugable topic if current user is granted to create topics with type.
     *
     * @param draft draft topic to be checked
     * @throws AccessDeniedException if user not granted to create current topic type
     *                               or if type of current topic is unknown
     */
    private void assertCreationAllowedForPlugableTopic(TopicDraft draft) {
        if (!draft.isPlugable()) {
            return;
        }

        Authentication auth = securityContextFacade.getContext().getAuthentication();
        List<Plugin> topicPlugins = pluginLoader.getPlugins(new TypeFilter(TopicPlugin.class),
                new StateFilter(Plugin.State.ENABLED), new TopicTypeFilter(draft.getTopicType()));

        if (topicPlugins.size() == 0) {
            throw new AccessDeniedException("Creating of unknown (" + draft.getTopicType() + ") topic type is forbidden");
        } else {
            for (Plugin plugin : topicPlugins) {
                TopicPlugin topicPlugin = (TopicPlugin) plugin;

                if (!permissionEvaluator.hasPermission(auth, draft.getBranchId(),
                        "BRANCH", topicPlugin.getCreateTopicPermission())) {
                    throw new AccessDeniedException("Creating of draft topic with type " + draft.getTopicType() + " is forbidden");
                }
            }
        }
    }
}
