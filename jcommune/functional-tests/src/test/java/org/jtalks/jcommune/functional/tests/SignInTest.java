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

import static org.testng.Assert.assertEquals;

/**
 * @author Eugeny Batov
 */
public abstract class SignInTest {

    protected WebClient webClient;
    protected HtmlPage mainPage;
    protected HtmlAnchor signInLink;


    private HtmlPage loginPage;
    private HtmlForm loginForm;
    private HtmlSubmitInput submitButton;
    private HtmlTextInput usernameTextField;
    private HtmlPasswordInput passwordTextField;

    @Parameters({"mainPageUrl"})
    @BeforeClass
    public void init(String mainPageUrl) throws Exception {
        webClient = new WebClient();
        mainPage = webClient.getPage(mainPageUrl);
        switchLocale();
        loginPageInit();
    }

    public abstract void switchLocale() throws Exception;


    public final void runSignInWithEmptyDataTest() throws Exception {
        submitEmptyData();
    }

    public final void runSignInSuccessTest() throws Exception {
        submitValidData();
        assertEquals(mainPage.getTitleText(), "JCommune");
    }

    @AfterClass
    public void closeAllWindows() {
        webClient.closeAllWindows();
    }

    public void loginPageInit() throws Exception {
        signInLink = mainPage.getAnchorByName("signIn");
        loginPage = (HtmlPage) signInLink.click();
        loginForm = loginPage.getFormByName("login_form");
        submitButton = loginForm.getInputByName("submit_button");
        usernameTextField = loginForm.getInputByName("j_username");
        passwordTextField = loginForm.getInputByName("j_password");
    }

    public void submitEmptyData() throws Exception {
        usernameTextField.setValueAttribute("");
        passwordTextField.setValueAttribute("");
        mainPage = submitButton.click();
    }

    public void submitValidData() throws Exception {
        usernameTextField.setValueAttribute("testuser");
        passwordTextField.setValueAttribute("userpass");
        mainPage = submitButton.click();
    }


}

