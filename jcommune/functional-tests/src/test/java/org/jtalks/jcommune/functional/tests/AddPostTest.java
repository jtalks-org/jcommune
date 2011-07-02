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

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import org.jtalks.jcommune.functional.tests.util.TestUtil;
import org.jtalks.jcommune.functional.tests.util.UserEnum;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Eugeny Batov
 */
public class AddPostTest {

    private HtmlPage topicListPage;
    private HtmlPage postListPage;
    private HtmlPage answerPage;
    private HtmlPage mainPage;
    private static final String TOPIC_TITLE = "AddPostTest";
    private static final String POST = "Topic for add post functional testing";
    private static final String FIRST_ANSWER = "First answer";
    private static final String SECOND_ANSWER = "Second answer";
    private static final String UPPER_BRANCH = "1";


    @BeforeClass
    @Parameters({"mainPageUrl"})
    public void init(String mainPageUrl) throws Exception {
        mainPage = TestUtil.logIn(mainPageUrl, UserEnum.MAIN_USER.getUsername(), UserEnum.MAIN_USER.getPassword());
        mainPage = TestUtil.forumLinkClick();
        topicListPage = TestUtil.branchLinkClick(UPPER_BRANCH);
        postListPage = TestUtil.createTopic(topicListPage, TOPIC_TITLE, POST);
    }

    @Test(priority = 0)
    public void answerButtonClick() throws Exception {
        HtmlForm answerButtonForm = postListPage.getFormByName("answerButtonForm");
        HtmlSubmitInput addAnswerButton = answerButtonForm.getInputByName("addAnswerButton");
        answerPage = addAnswerButton.click();
    }

    @Test(priority = 1)
    public void addPost() throws Exception {
        postListPage = TestUtil.addPost(answerPage, FIRST_ANSWER);
        System.out.println(postListPage.asText());
        HtmlTable postsTable = postListPage.getElementByName("postsTable");
        assertTrue(postsTable.getCellAt(1, 0).asText().contains(UserEnum.MAIN_USER.getUsername()));
        assertTrue(postsTable.getCellAt(1, 1).asText().contains(FIRST_ANSWER));
    }

    @Test(priority = 2)
    @Parameters({"mainPageUrl"})
    public void reLogInAndAddPost(String mainPageUrl) throws Exception {
        mainPage = TestUtil.logOut(mainPage);
        mainPage = TestUtil.logIn(mainPageUrl, UserEnum.ALTERNATIVE_USER.getUsername(),
                UserEnum.ALTERNATIVE_USER.getPassword());
        mainPage = TestUtil.forumLinkClick();
        topicListPage = TestUtil.branchLinkClick(UPPER_BRANCH);
        postListPage = TestUtil.topicLinkClick(topicListPage);
        HtmlForm answerButtonForm = postListPage.getFormByName("answerButtonForm");
        HtmlSubmitInput addAnswerButton = answerButtonForm.getInputByName("addAnswerButton");
        answerPage = addAnswerButton.click();
        postListPage = TestUtil.addPost(answerPage, SECOND_ANSWER);
        HtmlTable postsTable = postListPage.getElementByName("postsTable");

        assertTrue(postsTable.getCellAt(0, 0).asText().contains(UserEnum.MAIN_USER.getUsername()));
        assertTrue(postsTable.getCellAt(0, 1).asText().contains(POST));
        assertTrue(postsTable.getCellAt(1, 0).asText().contains(UserEnum.MAIN_USER.getUsername()));
        assertTrue(postsTable.getCellAt(1, 1).asText().contains(FIRST_ANSWER));
        assertTrue(postsTable.getCellAt(2, 0).asText().contains(UserEnum.ALTERNATIVE_USER.getUsername()));
        assertTrue(postsTable.getCellAt(2, 1).asText().contains(SECOND_ANSWER));
    }

    @Test(priority = 3, expectedExceptions = ElementNotFoundException.class)
    public void logOut() throws Exception {
        mainPage = TestUtil.logOut(mainPage);
        mainPage = TestUtil.forumLinkClick();
        topicListPage = TestUtil.branchLinkClick(UPPER_BRANCH);
        postListPage = TestUtil.topicLinkClick(topicListPage);
        HtmlForm answerButtonForm = postListPage.getFormByName("answerButtonForm");
    }

    @AfterClass
    public void closeAllWindows() {
        TestUtil.closeAllWindows();
    }
}






















