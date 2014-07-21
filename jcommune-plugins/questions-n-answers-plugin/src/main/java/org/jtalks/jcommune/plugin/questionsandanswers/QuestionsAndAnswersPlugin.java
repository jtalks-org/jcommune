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
import org.jtalks.jcommune.plugin.api.plugins.PluginWithPermissions;
import org.jtalks.jcommune.plugin.api.plugins.StatefullPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Plugin for question and answer type of topic.
 *
 * @author Evgeniy Myslovets
 */
public class QuestionsAndAnswersPlugin extends StatefullPlugin implements PluginWithPermissions {

    @Override
    public String getName() {
        return "Questions and Answers plugin";
    }

    @Override
    public List<PluginProperty> getConfiguration() {
        return Collections.emptyList();
    }

    public boolean supportsJCommuneVersion(String version) {
        return true;
    }

    @Override
    protected Map<PluginProperty, String> applyConfiguration(List<PluginProperty> properties) {
        return Collections.emptyMap();
    }

    @Override
    public List<PluginProperty> getDefaultConfiguration() {
        return Collections.emptyList();
    }

    @Override
    public String translateLabel(String code, Locale locale) {
        return null;
    }

    @Override
    public <T extends JtalksPermission> List<T> getBranchPermissions() {
        return (List<T>) QuestionPluginBranchPermission.getAllAsList();
    }

    @Override
    public <T extends JtalksPermission> List<T> getGeneralPermissions() {
        return Collections.emptyList();
    }

    @Override
    public <T extends JtalksPermission> List<T> getProfilePermissions() {
        return Collections.emptyList();
    }

}
