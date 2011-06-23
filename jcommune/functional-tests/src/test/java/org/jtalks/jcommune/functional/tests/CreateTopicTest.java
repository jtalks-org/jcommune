/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.functional.tests;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugeny Batov
 */
public class CreateTopicTest {

    private WebClient webClient;
    private HtmlPage mainPage;
    private HtmlPage loginPage;
    private HtmlForm loginForm;
    private HtmlAnchor signInLink;
    private HtmlSubmitInput submitButton;
    private HtmlTextInput usernameTextField;
    private HtmlPasswordInput passwordTextField;
    private HtmlPage branchesPage;
    private HtmlForm branchesForm;
    private HtmlPage topicListPage;
    private HtmlForm topicListForm;
    private HtmlForm createTopicForm;
    private HtmlPage createTopicPage;
    private HtmlAnchor topicLink;
    private final String USERNAME = "testuser";
    private final String PASSWORD = "userpass";
    private final String TOPIC_TITLE = "topic_title";
    private final String MESSAGE = "message";
    private final String EN_LOCALE_TIME_PATTERN="\\d{2}\\s[a-zA-Z]{3}\\s\\d{4}\\s\\d{2}:\\d{2}";

    @Parameters({"mainPageUrl"})
    @BeforeClass
    public void logIn(String mainPageUrl) throws IOException {
        webClient = new WebClient();
        mainPage = webClient.getPage(mainPageUrl);
        signInLink = mainPage.getAnchorByName("signIn");
        loginPage = (HtmlPage) signInLink.click();
        loginForm = loginPage.getFormByName("login_form");
        submitButton = loginForm.getInputByName("submit_button");
        usernameTextField = loginForm.getInputByName("j_username");
        passwordTextField = loginForm.getInputByName("j_password");
        usernameTextField.setValueAttribute(USERNAME);
        passwordTextField.setValueAttribute(PASSWORD);
        mainPage = submitButton.click();
    }

    @Test(priority = 0)
    public void forumLinkClick() throws Exception {
        HtmlAnchor forumLink = mainPage.getAnchorByName("forumLink");
        mainPage = (HtmlPage) forumLink.click();
        branchesForm = mainPage.getFormByName("branchesForm");
    }

    @Test(priority = 1)
    public void branchLinkClick() throws Exception {
        HtmlAnchor branchLink = mainPage.getAnchorByHref("/jcommune/branch/1.html");
        topicListPage = (HtmlPage) branchLink.click();
        topicListForm = topicListPage.getFormByName("topicListForm");
    }

    @Test(priority = 2)
    public void newTopicButtonClick() throws Exception {
        HtmlSubmitInput newTopicButton = topicListForm.getInputByName("newTopicButton");
        createTopicPage = newTopicButton.click();
        createTopicForm = createTopicPage.getFormByName("newTopicForm");
    }

    @Test(priority = 3)
    public void fillCreateTopicForm() throws Exception {
        HtmlInput topicNameInput = createTopicForm.getInputByName("topicName");
        HtmlTextArea bodyTextArea = createTopicForm.getTextAreaByName("bodyText");
        HtmlSubmitInput newTopicButton = createTopicForm.getInputByName("newTopicButton");
        topicNameInput.setValueAttribute(TOPIC_TITLE);
        bodyTextArea.setText(MESSAGE);
        createTopicPage = newTopicButton.click();

        createTopicForm = createTopicPage.getFormByName("topicListForm");
        topicLink = createTopicPage.getAnchorByText(TOPIC_TITLE);
        HtmlTable topicsTable = createTopicPage.getElementByName("topicsTable");
        assertEquals(topicsTable.getCellAt(1, 0).asText(), TOPIC_TITLE);
        assertEquals(topicsTable.getCellAt(1, 1).asText(), USERNAME);
        assertTrue(topicsTable.getCellAt(1, 2).asText().matches(EN_LOCALE_TIME_PATTERN));

    }


    @Test(priority = 4)
    public void clickOnCreatedTopicTitle() throws Exception {
        HtmlPage messagesPage = topicLink.click();
        HtmlTable messagesTable = messagesPage.getElementByName("messagesTable");
        //assertTrue(messagesPage.asText().contains("Topic : "+TOPIC_TITLE));
        assertEquals(messagesTable.getCellAt(0, 0).asText(), "Author: " + USERNAME);
        assertEquals(messagesTable.getCellAt(0, 1).asText(), "Message: " + MESSAGE);


    }

    @AfterClass
    public void closeAllWindows() {
        webClient.closeAllWindows();
    }
}
































