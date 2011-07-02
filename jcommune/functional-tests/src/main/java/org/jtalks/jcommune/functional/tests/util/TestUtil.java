package org.jtalks.jcommune.functional.tests.util;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

public class TestUtil {

    private static WebClient webClient;
    private static HtmlPage mainPage;
    private static HtmlPage topicListPage;


    public static HtmlPage logIn(String mainPageUrl, String username, String password) throws Exception {
        webClient = new WebClient();
        mainPage = webClient.getPage(mainPageUrl);
        HtmlAnchor signInLink = mainPage.getAnchorByName("signIn");
        HtmlPage loginPage = (HtmlPage) signInLink.click();
        HtmlForm loginForm = loginPage.getFormByName("login_form");
        HtmlSubmitInput submitButton = loginForm.getInputByName("submit_button");
        HtmlTextInput usernameTextField = loginForm.getInputByName("j_username");
        HtmlPasswordInput passwordTextField = loginForm.getInputByName("j_password");
        usernameTextField.setValueAttribute(username);
        passwordTextField.setValueAttribute(password);
        mainPage = submitButton.click();
        return mainPage;
    }

    public static HtmlPage forumLinkClick() throws Exception {
        HtmlAnchor forumLink = mainPage.getAnchorByName("forumLink");
        mainPage = (HtmlPage) forumLink.click();
        return mainPage;
    }

    public static HtmlPage branchLinkClick(String branchId) throws Exception {
        HtmlAnchor branchLink = mainPage.getAnchorByHref("/jcommune/branch/" + branchId + ".html");
        topicListPage = (HtmlPage) branchLink.click();
        return topicListPage;
    }

    public static HtmlPage topicLinkClick(HtmlPage topicList) throws Exception {
        HtmlTable topicsTable = topicList.getElementByName("topicsTable");
        DomNodeList linksList = topicsTable.getCellAt(1, 0).getElementsByTagName("a");
        HtmlAnchor topicLink = (HtmlAnchor) linksList.get(0);
        HtmlPage postListPage = topicLink.click();
        return postListPage;
    }


    public static HtmlPage createTopic(HtmlPage topicList, String topicTitle, String message) throws Exception {
        HtmlForm topicListForm = topicList.getFormByName("topicListForm");
        HtmlSubmitInput newTopicButton = topicListForm.getInputByName("newTopicButton");
        HtmlPage createTopicPage = newTopicButton.click();
        HtmlForm createTopicForm = createTopicPage.getFormByName("newTopicForm");
        HtmlInput topicNameInput = createTopicForm.getInputByName("topicName");
        HtmlTextArea bodyTextArea = createTopicForm.getTextAreaByName("bodyText");
        newTopicButton = createTopicForm.getInputByName("newTopicButton");
        topicNameInput.setValueAttribute(topicTitle);
        bodyTextArea.setText(message);
        createTopicPage = newTopicButton.click();
        return createTopicPage;
    }

    public static HtmlPage addPost(HtmlPage answerPage, String message) throws Exception {
        HtmlForm answerForm = answerPage.getFormByName("answerForm");
        HtmlSubmitInput answerButton = answerForm.getInputByName("answerButton");
        HtmlTextArea messageArea = answerForm.getTextAreaByName("bodytext");
        messageArea.setText(message);
        return answerButton.click();
    }


    public static HtmlPage logOut(HtmlPage main) throws Exception {
        HtmlAnchor logOutLink = main.getAnchorByName("logout");
        return logOutLink.click();
    }

}
