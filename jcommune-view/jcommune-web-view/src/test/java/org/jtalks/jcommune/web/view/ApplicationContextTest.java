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
package org.jtalks.jcommune.web.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

/**
 * Is created in order to find out if we changed the classes or Spring contexts per se so that it won't start up. Usual
 * tests won't show this kind of problems because they don't instantiate app contexts, thus the only way to figure out
 * that the contexts are being created correctly is this test.
 *
 * @author Evgeny Surovtsev
 * @author Vitaliy Kravchenko
 */
public class ApplicationContextTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextTest.class);
    @Test
    public void applicationContextShouldConstructAllBeans() {
        try
        {
            new ClassPathXmlApplicationContext(
                    "/org/jtalks/jcommune/model/entity/applicationContext-dao.xml",
                    "/org/jtalks/jcommune/model/entity/applicationContext-properties.xml",
                    "/org/jtalks/jcommune/service/applicationContext-service.xml",
                    "/org/jtalks/jcommune/service/security-service-context.xml",
                    "/org/jtalks/jcommune/service/email-context.xml",
                    "/org/jtalks/jcommune/web/applicationContext-controller.xml",
                    "/org/jtalks/jcommune/web/view/security-mock-context.xml"
            );  
        }
        catch (Exception ex) {
            LOGGER.error("Application initialization is failed", ex);
            fail();
        }
    }
}



