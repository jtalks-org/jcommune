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

import com.gargoylesoftware.htmlunit.html.*;
import org.jtalks.jcommune.functional.tests.util.DateUtil;
import org.jtalks.jcommune.functional.tests.util.TestUtil;
import org.jtalks.jcommune.functional.tests.util.UserEnum;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Locale;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugeny Batov
 */
public class CreateTopicTest {

    private HtmlPage mainPage;
    private HtmlForm topicListForm;
    private HtmlForm createTopicForm;
    private HtmlPage createTopicPage;
    private HtmlPage postListPage;
    private HtmlAnchor topicLink;
    private static final String USERNAME = UserEnum.MAIN_USER.getUsername();
    private static final String PASSWORD = UserEnum.MAIN_USER.getPassword();
    private static final String TOPIC_TITLE = "CreateTopicTest";
    private static final String POST = "message";
    private static final String UPPER_BRANCH = "1";


    @Parameters({"mainPageUrl"})
    @BeforeClass
    public void init(String mainPageUrl) throws Exception {
        mainPage = TestUtil.logIn(mainPageUrl, USERNAME, PASSWORD);
        mainPage = TestUtil.forumLinkClick();
        HtmlForm branchesForm = mainPage.getFormByName("branchesForm");
        HtmlPage topicListPage = TestUtil.branchLinkClick(UPPER_BRANCH);
        topicListForm = topicListPage.getFormByName("topicListForm");
    }

    @Test(priority = 0)
    public void newTopicButtonClick() throws Exception {
        HtmlSubmitInput newTopicButton = topicListForm.getInputByName("newTopicButton");
        createTopicPage = newTopicButton.click();
        createTopicForm = createTopicPage.getFormByName("newTopicForm");
    }

    @Test(priority = 1)
    public void fillCreatedTopicForm() throws Exception {
        HtmlInput topicNameInput = createTopicForm.getInputByName("topicName");
        HtmlTextArea bodyTextArea = createTopicForm.getTextAreaByName("bodyText");
        HtmlSubmitInput newTopicButton = createTopicForm.getInputByName("newTopicButton");
        topicNameInput.setValueAttribute(TOPIC_TITLE);
        bodyTextArea.setText(POST);
        postListPage = newTopicButton.click();
        HtmlPage topicPage = TestUtil.backToTopicList(postListPage);
        topicListForm = topicPage.getFormByName("topicListForm");
        topicLink = topicPage.getAnchorByText(TOPIC_TITLE);
        HtmlTable topicsTable = topicPage.getElementByName("topicsTable");
        List<HtmlTableRow> tableRowList = topicsTable.getRows();
        boolean flag = false;
        for (HtmlTableRow row : tableRowList) {
            if (row.getCell(0).asText().equals(TOPIC_TITLE)) {
                assertEquals(row.getCell(0).asText(), TOPIC_TITLE);
                assertEquals(row.getCell(1).asText(), USERNAME);
                assertTrue(DateUtil.stringToMillis(row.getCell(2).asText(), Locale.ENGLISH)
                        - DateUtil.getCurrentTimeMillis() < 1000 * 60);
                flag = true;
                break;
            }
        }
        assertTrue(flag, "Topic not found");
    }


    @Test(priority = 2)
    public void clickOnCreatedTopicTitle() throws Exception {
        HtmlPage messagesPage = topicLink.click();
        HtmlTable postsTable = messagesPage.getElementByName("postsTable");
        //assertTrue(messagesPage.asText().contains(TOPIC_TITLE));
        assertTrue(postsTable.getCellAt(0, 0).asText().contains(USERNAME));
        assertTrue(postsTable.getCellAt(0, 1).asText().contains(POST));
    }

    @AfterClass
    public void closeAllWindows() {
        TestUtil.closeAllWindows();
    }
}
































