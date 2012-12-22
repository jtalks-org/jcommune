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
package org.jtalks.jcommune.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class ExceptionHandlerControllerTest {
    @Mock
    private HttpServletRequest request;
    private ExceptionHandlerController controller = new ExceptionHandlerController();
    
    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void rememberMeExceptionHandlerShouldRedirectToLoginPage(){
        String actualRedirectUrl = controller.handleRememberMeException(request);
        
        Assert.assertEquals(actualRedirectUrl, ExceptionHandlerController.REDIRECT_TO_LOGIN, 
                "After exception handling controller must redirect to login page");
    }
}
