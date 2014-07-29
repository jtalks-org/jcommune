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

import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;


/**
 * @author Mikhail Stryzhonok
 */
public class QuestionAndAnswersPluginTest {
    private static final String ORDER_PROPERTY = "label.order";

    @Test
    public void testConfigure() throws Exception {
        PluginProperty property = new PluginProperty(ORDER_PROPERTY, PluginProperty.Type.INT, "102");
        PluginConfiguration config = new PluginConfiguration("Questions and Answers plugin", true, Arrays.asList(property));
        QuestionsAndAnswersPlugin plugin = new QuestionsAndAnswersPlugin();

        plugin.configure(config);

        assertEquals(plugin.getState(), Plugin.State.ENABLED);
    }

    @Test(expectedExceptions = UnexpectedErrorException.class)
    public void configurationWithIncorrectParameterShouldThrowUnexpectedErrorException() throws Exception {
        PluginProperty property = new PluginProperty("anyProperty", PluginProperty.Type.STRING, "string");
        PluginConfiguration config = new PluginConfiguration("Questions and Answers plugin", true, Arrays.asList(property));
        QuestionsAndAnswersPlugin plugin = new QuestionsAndAnswersPlugin();

        plugin.configure(config);
    }

    @Test(expectedExceptions = UnexpectedErrorException.class)
    public void configurationWithIncorrectParametersNumberShouldThrowUnexpectedErrorException() throws Exception {
        PluginProperty correctProperty = new PluginProperty(ORDER_PROPERTY, PluginProperty.Type.INT, "102");
        PluginProperty incorrectProperty = new PluginProperty("anyProperty", PluginProperty.Type.STRING, "string");
        PluginConfiguration config = new PluginConfiguration("Questions and Answers plugin", true,
                Arrays.asList(correctProperty, incorrectProperty));
        QuestionsAndAnswersPlugin plugin = new QuestionsAndAnswersPlugin();

        plugin.configure(config);
    }

    @Test(expectedExceptions = UnexpectedErrorException.class)
    public void configurationWithIncorrectParameterTypeShouldThrowUnexpectedErrorException() throws Exception {
        PluginProperty property = new PluginProperty(ORDER_PROPERTY, PluginProperty.Type.INT, "string");
        PluginConfiguration config = new PluginConfiguration("Questions and Answers plugin", true, Arrays.asList(property));
        QuestionsAndAnswersPlugin plugin = new QuestionsAndAnswersPlugin();

        plugin.configure(config);
    }

    @Test
    public void defaultConfigurationShouldBeAppliedIfConfigureWithNullOrderValue() throws Exception {
        PluginProperty property = new PluginProperty(ORDER_PROPERTY, PluginProperty.Type.INT, null);
        PluginConfiguration config = new PluginConfiguration("Questions and Answers plugin", true, Arrays.asList(property));
        QuestionsAndAnswersPlugin plugin = new QuestionsAndAnswersPlugin();

        plugin.configure(config);
        List<PluginProperty> actualConfiguration = plugin.getConfiguration();

        assertEquals(actualConfiguration.size(), 1);
        assertEquals(actualConfiguration.get(0).getValue(), "102");
    }
}
