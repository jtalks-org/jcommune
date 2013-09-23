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

package org.jtalks.jcommune.web.listeners;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletContextEvent;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

public class AppPropertySetupListenerTest {

    AppPropertySetupListener listener;

    @Mock
    ServletContextEvent sce;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        listener = new AppPropertySetupListener();
    }

    @Test
    public void testContextInitialized() throws Exception {
        listener.contextInitialized(sce);
        assertEquals(System.getProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize"), "true");
    }

    @Test
    public void testContextDestroyed() throws Exception {
        listener.contextDestroyed(sce);
        assertEquals(System.getProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize"), null);
    }
}
