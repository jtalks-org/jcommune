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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Evgeniy Naumenko
 */
public class ErrorPageControllerTest {

    private ErrorPageController controller;

    @BeforeMethod
    public void setUp(){
      controller = new ErrorPageController();
    }


    @Test
    public void testGet404Page() throws Exception {
       assertEquals(controller.get404Page(), ErrorPageController.NOT_FOUND_PAGE_VIEW);
    }

    @Test
    public void testRedirect404() throws Exception {
      assertEquals(controller.redirect404(), "redirect:" + ErrorPageController.NOT_FOUND_PAGE_VIEW);
    }
}
