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


    public static HtmlPage createTopic(HtmlPage topicList, String topicTitle, String post) throws Exception {
        HtmlForm topicListForm = topicList.getFormByName("topicListForm");
        HtmlSubmitInput newTopicButton = topicListForm.getInputByName("newTopicButton");
        HtmlPage createTopicPage = newTopicButton.click();
        HtmlForm createTopicForm = createTopicPage.getFormByName("newTopicForm");
        HtmlInput topicNameInput = createTopicForm.getInputByName("topicName");
        HtmlTextArea bodyTextArea = createTopicForm.getTextAreaByName("bodyText");
        newTopicButton = createTopicForm.getInputByName("newTopicButton");
        topicNameInput.setValueAttribute(topicTitle);
        bodyTextArea.setText(post);
        HtmlPage postPage = newTopicButton.click();
        return postPage;
    }

    public static HtmlPage backToTopicList(HtmlPage postPage) throws Exception {
        HtmlForm backButtonForm = postPage.getFormByName("backButtonForm");
        HtmlSubmitInput backButton = backButtonForm.getInputByName("backButton");
        HtmlPage topicPage = backButton.click();
        return topicPage;
    }


    public static HtmlPage addPost(HtmlPage answerPage, String post) throws Exception {
        HtmlForm answerForm = answerPage.getFormByName("answerForm");
        HtmlSubmitInput answerButton = answerForm.getInputByName("answerButton");
        HtmlTextArea postArea = answerForm.getTextAreaByName("bodytext");
        postArea.setText(post);
        return answerButton.click();
    }


    public static HtmlPage logOut(HtmlPage main) throws Exception {
        HtmlAnchor logOutLink = main.getAnchorByName("logout");
        return logOutLink.click();
    }

    public static void closeAllWindows(){
        webClient.closeAllWindows();
    }

}
