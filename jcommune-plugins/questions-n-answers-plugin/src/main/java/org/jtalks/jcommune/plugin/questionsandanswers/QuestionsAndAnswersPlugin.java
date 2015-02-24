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
package org.jtalks.jcommune.plugin.questionsandanswers;

import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.plugin.api.core.SubscribersFilter;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.jtalks.jcommune.plugin.api.core.WebControllerPlugin;
import org.jtalks.jcommune.plugin.api.web.dto.CreateTopicBtnDto;
import org.jtalks.jcommune.plugin.api.exceptions.PluginConfigurationException;
import org.jtalks.jcommune.plugin.api.service.ReadOnlySecurityService;
import org.jtalks.jcommune.plugin.api.web.PluginController;
import org.jtalks.jcommune.plugin.questionsandanswers.controller.QuestionsAndAnswersController;

import java.util.*;

/**
 * Plugin for question and answer type of topic.
 *
 * @author Evgeniy Myslovets
 */
public class QuestionsAndAnswersPlugin extends WebControllerPlugin implements TopicPlugin {
    public static final String TOPIC_TYPE = "Question";
    private static final String ORDER_PROPERTY = "label.order";
    private static final String ORDER_HINT = "label.order.hint";
    private static final int DEFAULT_ORDER_VALUE = 102;
    private static final String DEFAULT_LOCALE_CODE = "en";
    public static final String CONTEXT = "/topics/question";

    public static final String MESSAGE_PATH = "org.jtalks.jcommune.plugin.questionsandanswers.messages";
    /**
     * Default value, thus it will show lower in the list of topics than Discussion and Code Review which are 100 & 101.
     */
    private int order = DEFAULT_ORDER_VALUE;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Questions and Answers plugin";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PluginProperty> getConfiguration() {
        PluginProperty orderProperty = new PluginProperty(ORDER_PROPERTY, PluginProperty.Type.INT,
                String.valueOf(order));
        orderProperty.setHint(ORDER_HINT);
        return Arrays.asList(orderProperty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsJCommuneVersion(String version) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<PluginProperty, String> applyConfiguration(List<PluginProperty> properties)
            throws PluginConfigurationException {
        if (properties.size() == 1 && ORDER_PROPERTY.equalsIgnoreCase(properties.get(0).getName())) {
            order = properties.get(0).getValue() == null ? DEFAULT_ORDER_VALUE
                    : Integer.parseInt(properties.get(0).getValue());
            properties.get(0).setHint(ORDER_HINT);
            return new HashMap<>();
        } else {
            throw new PluginConfigurationException(
                    "Can't apply configuration: incorrect parameters count or order not found");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PluginProperty> getDefaultConfiguration() {
        PluginProperty orderProperty = new PluginProperty(ORDER_PROPERTY, PluginProperty.Type.INT,
                String.valueOf(DEFAULT_ORDER_VALUE));
        orderProperty.setHint(ORDER_HINT);
        return Arrays.asList(orderProperty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String translateLabel(String code, Locale locale) {
        ResourceBundle messages = ResourceBundle.getBundle(MESSAGE_PATH, locale);
        if (messages.containsKey(code)) {
            return  messages.getString(code);
        } else {
            ResourceBundle defaultMessages = ResourceBundle.getBundle(MESSAGE_PATH, new Locale(DEFAULT_LOCALE_CODE));
            return defaultMessages.containsKey(code) ? defaultMessages.getString(code) : code;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends JtalksPermission> List<T> getBranchPermissions() {
        return (List<T>) QuestionsPluginBranchPermission.getAllAsList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreateTopicBtnDto getCreateTopicBtnDto(long branchId) {
        Locale locale = ReadOnlySecurityService.getInstance().getCurrentUser().getLanguage().getLocale();
        return new CreateTopicBtnDto("new-question-btn", translateLabel("label.addQuestion", locale),
                translateLabel("label.addQuestion.tip", locale), CONTEXT + "/new?branchId=" + branchId, order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JtalksPermission getCreateTopicPermission() {
        return QuestionsPluginBranchPermission.CREATE_QUESTIONS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JtalksPermission getBranchPermissionByMask(int mask) {
        return QuestionsPluginBranchPermission.findByMask(mask);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JtalksPermission getBranchPermissionByName(String name) {
        try {
            return QuestionsPluginBranchPermission.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PluginController getController() {
        return new QuestionsAndAnswersController();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTopicType() {
        return TOPIC_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JtalksPermission getCommentPermission() {
        return BranchPermission.CREATE_POSTS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SubscribersFilter getSubscribersFilter() {
        return new QuestionSubscribersFilter();
    }
}
