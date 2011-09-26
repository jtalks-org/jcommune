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
package org.jtalks.jcommune.functional.tests;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugeny Batov
 */
public class SignInTest {

    private WebClient webClient;
    private HtmlPage loginPage;
    private HtmlForm loginForm;
    private HtmlSubmitInput submitButton;
    private HtmlTextInput usernameTextField;
    private HtmlPasswordInput passwordTextField;


    @BeforeClass
    public void init() throws IOException {
        webClient = new WebClient();
        loginPage = webClient.getPage("http://deploy.jtalks.org/jcommune/login.html");
        loginForm = loginPage.getFormByName("");
        submitButton = loginForm.getInputByName("");
        usernameTextField = loginForm.getInputByName("j_username");
        passwordTextField = loginForm.getInputByName("j_password");
    }

    @Test(enabled=false)
    public void signInWithEmptyDataEn() throws IOException {
        usernameTextField.setValueAttribute("");
        passwordTextField.setValueAttribute("");
        HtmlPage homePage = submitButton.click();
        assertEquals(homePage.getTitleText(), "Sign in");
        assertTrue(homePage.asText().contains("Your login attempt was not successful, try again."));
    }


    @Test(enabled=false)
    public void signInSuccess() throws IOException {
        usernameTextField.setValueAttribute("testuser");
        passwordTextField.setValueAttribute("userpass");
        HtmlPage homePage = submitButton.click();
        assertEquals(homePage.getTitleText(), "JCommune");
    }


    @AfterTest
    public void closeAllWindows() {
        //webClient.closeAllWindows();
    }
}

