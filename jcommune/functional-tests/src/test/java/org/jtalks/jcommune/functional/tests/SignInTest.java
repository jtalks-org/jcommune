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
import org.testng.annotations.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugeny Batov
 */
public class SignInTest {

    private WebClient webClient;
    private HtmlPage mainPage;
    private HtmlAnchor signInLink;
    private HtmlPage loginPage;
    private HtmlForm loginForm;
    private HtmlSubmitInput submitButton;
    private HtmlTextInput usernameTextField;
    private HtmlPasswordInput passwordTextField;

    @Parameters({"mainPageUrl"})
    @BeforeTest
    public void init(String mainPageUrl) throws Exception {
        webClient = new WebClient();
        mainPage = webClient.getPage(mainPageUrl);
        signInLink = mainPage.getAnchorByName("signIn");
        loginPage = (HtmlPage) signInLink.click();
        loginForm = loginPage.getFormByName("login_form");
        submitButton = loginForm.getInputByName("submit_button");
        usernameTextField = loginForm.getInputByName("j_username");
        passwordTextField = loginForm.getInputByName("j_password");
    }

    @Test(description = "Entered empty login data", dataProvider = "localeData")
    public void signInWithEmptyData(String locale, String validationMessage, String pageTitle) throws Exception {
        HtmlAnchor localeLink = mainPage.getAnchorByText(locale);
        mainPage = (HtmlPage) localeLink.click();
        submitEmptyData();
        assertEquals(mainPage.getTitleText(), pageTitle);
        assertTrue(mainPage.asText().contains(validationMessage));
    }


    @Test(description = "Entered username in wrong case", dataProvider = "localeData")
    public void signInWithWrongUsernameCase(String locale, String validationMessage, String pageTitle) throws Exception {
        HtmlAnchor localeLink = mainPage.getAnchorByText(locale);
        mainPage = (HtmlPage) localeLink.click();
        submitWrongUsernameCaseData();
        assertEquals(mainPage.getTitleText(), pageTitle);
        assertTrue(mainPage.asText().contains(validationMessage));
    }

    @Test(description = "Entered password in wrong case", dataProvider = "localeData")
    public void signInWithWrongPasswordCase(String locale, String validationMessage, String pageTitle) throws Exception {
        HtmlAnchor localeLink = mainPage.getAnchorByText(locale);
        mainPage = (HtmlPage) localeLink.click();
        submitWrongPasswordCaseData();
        assertEquals(mainPage.getTitleText(), pageTitle);
        assertTrue(mainPage.asText().contains(validationMessage));
    }

    @Test(description = "Entered the non-registered username", dataProvider = "localeData")
    public void signInWithWrongUsername(String locale, String validationMessage, String pageTitle) throws Exception {
        HtmlAnchor localeLink = mainPage.getAnchorByText(locale);
        mainPage = (HtmlPage) localeLink.click();
        submitWrongUsernameData();
        assertEquals(mainPage.getTitleText(), pageTitle);
        assertTrue(mainPage.asText().contains(validationMessage));
    }

    @Test(description = "Entered valid login data", dataProvider = "localeData")
    public void signInSuccess(String locale, String validationMessage, String pageTitle) throws Exception {
        HtmlAnchor localeLink = mainPage.getAnchorByText(locale);
        mainPage = (HtmlPage) localeLink.click();
        submitValidData();
        assertEquals(mainPage.getTitleText(), "JCommune");
    }

    @AfterTest
    public void closeAllWindows() {
        webClient.closeAllWindows();
    }

    @DataProvider
    public Object[][] localeData() {
        return new Object[][]{
                {"En", "Your login attempt was not successful, try again.", "Sign in"},
                {"Ru", "Ваша попытка войти не удалась, попробуйте снова", "Войти"}
        };
    }

    public void submitEmptyData() throws Exception {
        usernameTextField.setValueAttribute("");
        passwordTextField.setValueAttribute("");
        mainPage = submitButton.click();
    }

    public void submitWrongUsernameCaseData() throws Exception {
        usernameTextField.setValueAttribute("TestUser");
        passwordTextField.setValueAttribute("userpass");
        mainPage = submitButton.click();
    }

    public void submitWrongPasswordCaseData() throws Exception {
        usernameTextField.setValueAttribute("testuser");
        passwordTextField.setValueAttribute("UserPass");
        mainPage = submitButton.click();
    }

    public void submitWrongUsernameData() throws Exception {
        usernameTextField.setValueAttribute("incorrectuser");
        passwordTextField.setValueAttribute("userpass");
        mainPage = submitButton.click();
    }

    public void submitValidData() throws Exception {
        usernameTextField.setValueAttribute("testuser");
        passwordTextField.setValueAttribute("userpass");
        mainPage = submitButton.click();
    }


}

