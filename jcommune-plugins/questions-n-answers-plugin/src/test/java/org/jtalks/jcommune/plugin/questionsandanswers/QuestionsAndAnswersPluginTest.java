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
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.entity.PluginProperty;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.web.dto.CreateTopicBtnDto;
import org.jtalks.jcommune.plugin.api.exceptions.PluginConfigurationException;
import org.jtalks.jcommune.plugin.api.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.plugin.api.service.ReadOnlySecurityService;
import org.jtalks.jcommune.plugin.api.service.UserReader;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;


/**
 * @author Mikhail Stryzhonok
 */
public class QuestionsAndAnswersPluginTest {

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

    @Test
    public void testApplyConfiguration() throws PluginConfigurationException {
        PluginProperty property = new PluginProperty(ORDER_PROPERTY, PluginProperty.Type.INT, "103");
        QuestionsAndAnswersPlugin plugin = new QuestionsAndAnswersPlugin();
        plugin.applyConfiguration(Arrays.asList(property));

        assertEquals(plugin.getConfiguration().size(), 1);
        assertEquals(plugin.getConfiguration().get(0).getValue(), "103");
    }

    @Test(expectedExceptions = PluginConfigurationException.class)
    public void applyConfigurationShouldThrowRuntimeExceptionIfPassedEmptyProperties() throws PluginConfigurationException {
        new QuestionsAndAnswersPlugin().applyConfiguration(Collections.<PluginProperty>emptyList());
    }

    @Test
    public void testGetDefaultConfiguration() {
        List<PluginProperty> defaultConfiguration = new QuestionsAndAnswersPlugin().getDefaultConfiguration();
        assertEquals(defaultConfiguration.size(), 1);
        assertEquals(defaultConfiguration.get(0).getValue(), "102");
    }

    @Test
    public void translateLabelWithExistingTranslation() {
        assertEquals("Ask Question", new QuestionsAndAnswersPlugin().translateLabel("label.addQuestion", Locale.forLanguageTag("en")));
    }

    @Test
    public void translateLabelWithoutExistingTranslation() {
        assertEquals("label.unexisted", new QuestionsAndAnswersPlugin().translateLabel("label.unexisted", Locale.forLanguageTag("en")));
    }

    @Test
    public void testGetBranchPermissions() {
        assertEquals(new QuestionsAndAnswersPlugin().getBranchPermissions(), QuestionsPluginBranchPermission.getAllAsList());
    }

    @Test
    public void testCreateTopicPermission() {
        assertEquals(new QuestionsAndAnswersPlugin().getCreateTopicPermission(), QuestionsPluginBranchPermission.CREATE_QUESTIONS);
    }

    @Test
    public void getBranchPermissionByMaskShouldReturnCorrectPermission() {
        QuestionsPluginBranchPermission expectedPermission = QuestionsPluginBranchPermission.CREATE_QUESTIONS;
        JtalksPermission actualPermission = new QuestionsAndAnswersPlugin().getBranchPermissionByMask(31);
        assertEquals(actualPermission, expectedPermission);
    }

    @Test
    public void getBranchPermissionByMaskShouldReturnNullIfPermissionNotFound() {
        JtalksPermission actualPermission = new QuestionsAndAnswersPlugin().getBranchPermissionByMask(30);
        assertNull(actualPermission);
    }

    @Test
    public void getBranchPermissionByNameShouldReturnCorrectPermission() {
        QuestionsPluginBranchPermission expectedPermission = QuestionsPluginBranchPermission.CREATE_QUESTIONS;
        JtalksPermission actualPermission = new QuestionsAndAnswersPlugin().getBranchPermissionByName("CREATE_QUESTIONS");
        assertEquals(actualPermission, expectedPermission);
    }

    @Test
    public void getBranchPermissionByNameShouldReturnNullIfPermissionNotFound() {
        JtalksPermission actualPermission = new QuestionsAndAnswersPlugin().getBranchPermissionByName("ASK_QUESTIONS");
        assertNull(actualPermission);
    }

    @Test
    public void supportsJCommuneVersionShouldReturnTrue() {
        assertTrue(new QuestionsAndAnswersPlugin().supportsJCommuneVersion(""));
    }

    @Test
    public void testGetCreateTopicBtnDto() {
        JCUser currentUser = new JCUser("name", "email@example.com", "password");
        currentUser.setLanguage(Language.ENGLISH);
        UserReader userReader = mock(UserReader.class);
        when(userReader.getCurrentUser()).thenReturn(currentUser);
        ReadOnlySecurityService service = (ReadOnlySecurityService) ReadOnlySecurityService.getInstance();
        service.setUserReader(userReader);

        CreateTopicBtnDto createTopicBtnDto = new QuestionsAndAnswersPlugin().getCreateTopicBtnDto(1);
        assertEquals("Ask Question", createTopicBtnDto.getDisplayNameKey());
    }

    @Test
    public void getTopicTypeTest() {
        QuestionsAndAnswersPlugin plugin = new QuestionsAndAnswersPlugin();

        assertEquals(plugin.getTopicType(), QuestionsAndAnswersPlugin.TOPIC_TYPE);
    }
}
