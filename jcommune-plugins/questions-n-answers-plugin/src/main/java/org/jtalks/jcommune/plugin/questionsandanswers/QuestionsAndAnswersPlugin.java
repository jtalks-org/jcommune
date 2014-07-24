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

import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.plugin.api.core.StatefullPlugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.jtalks.jcommune.plugin.api.dto.CreateTopicBtnDto;

import java.util.*;

/**
 * Plugin for question and answer type of topic.
 *
 * @author Evgeniy Myslovets
 */
public class QuestionsAndAnswersPlugin extends StatefullPlugin implements TopicPlugin {
    private static final String ORDER_PROPERTY = "label.Order";
    private int order = 102;

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
        return Arrays.asList(new PluginProperty(ORDER_PROPERTY, PluginProperty.Type.INT, String.valueOf(order)));
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
    protected Map<PluginProperty, String> applyConfiguration(List<PluginProperty> properties) {
        if (properties.size() == 1 && ORDER_PROPERTY.equalsIgnoreCase(properties.get(0).getName())) {
            order = properties.get(0).getValue() == null ? 102 : Integer.parseInt(properties.get(0).getValue());
            return Collections.EMPTY_MAP;
        } else {
            throw new RuntimeException("Can't apply configuration: incorrect parameters count or order not found");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PluginProperty> getDefaultConfiguration() {
        PluginProperty order = new PluginProperty(ORDER_PROPERTY, PluginProperty.Type.INT, "102");
        return Arrays.asList(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String translateLabel(String code, Locale locale) {
        ResourceBundle messages = ResourceBundle.getBundle("org.jtalks.jcommune.plugin.questionsandanswers.messages",
                locale);
        return messages.containsKey(code) ? messages.getString(code) : code;
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
    public CreateTopicBtnDto getCreateTopicBtnDto(long branchId, Locale locale) {
        return new CreateTopicBtnDto("new-question-btn", translateLabel("label.addQuestion", locale),
                translateLabel("label.addQuestion.tip", locale), "/questions/new?branchId=" + branchId, order);
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
        return QuestionsPluginBranchPermission.valueOf(name);
    }
}
