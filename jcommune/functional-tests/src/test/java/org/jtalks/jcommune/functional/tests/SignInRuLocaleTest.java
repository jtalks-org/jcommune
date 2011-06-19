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


import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugeny Batov
 */
public class SignInRuLocaleTest extends SignInTest {

    private final String RU_LOCALE_LINK = "Ru";

    @Test(description = "Entered empty login data.")
    public void signInWithEmptyData() throws Exception {
        runSignInWithEmptyDataTest();
        assertEquals(mainPage.getTitleText(), "Войти");
        assertTrue(mainPage.asText().contains("Ваша попытка войти не удалась, попробуйте снова"));
    }


    @Test(description = "Entered valid login data.")
    public void signInSuccess() throws Exception {
        runSignInSuccessTest();
    }


    @Override
    public void switchLocale() throws Exception {
        HtmlAnchor ruLocaleLink = mainPage.getAnchorByText(RU_LOCALE_LINK);
        mainPage = (HtmlPage) ruLocaleLink.click();
    }

}
