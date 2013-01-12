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
package org.jtalks.jcommune.service.jmx;

import com.google.common.collect.Lists;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/** @author stanislav bashkirtsev */
public class Log4jConfiguratorTest {
    private Log4jConfigurator sut;

    @BeforeMethod
    public void initSutAndDocs() throws Exception {
        sut = spy(new Log4jConfigurator());
    }

    @Test
    public void getLoggersShouldReturnAllAvailableNames() {
        List<Logger> allLoggers = Lists.newArrayList(logger("a", "DEBUG"), logger("b", "INFO"));
        doReturn(allLoggers).when(sut).getAllLoggers();
        assertEquals(sut.getLoggers(), Lists.newArrayList("a = DEBUG", "b = INFO"));
    }

    @Test
    public void getLoggersShouldReturnEmptyListIfNoLoggersConfigured() {
        doReturn(Lists.newArrayList()).when(sut).getAllLoggers();
        assertTrue(sut.getLoggers().isEmpty());
    }

    @Test
    public void testGetLogLevel() throws Exception {
        doReturn(logger("a", "INFO")).when(sut).getLogger("a");
        assertEquals(sut.getLogLevel("a"), "INFO");
    }

    @Test
    public void getLogLevelReturnsUnavailableIfNoSuchLogger(){
        doReturn(null).when(sut).getLogger("a");
        assertEquals(sut.getLogLevel("a"), "unavailable");
    }

    @Test
    public void testSetLogLevel() throws Exception {
        doReturn(logger("a", "INFO")).when(sut).getLogger("a");
        sut.setLogLevel("a", "DEBUG");
        assertEquals(sut.getLogLevel("a"), "DEBUG");
    }

    private Logger logger(String name, String level){
        Logger logger = Logger.getLogger(name);
        logger = spy(logger);
        logger.setLevel(Level.toLevel(level));
        return logger;
    }
}
