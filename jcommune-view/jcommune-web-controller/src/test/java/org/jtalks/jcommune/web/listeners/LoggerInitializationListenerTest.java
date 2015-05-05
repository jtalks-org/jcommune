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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.mockejb.jndi.MockContextFactory;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

/**
 * @author Evgeny Kapinos
 */
public class LoggerInitializationListenerTest {

    private Context tomcatContext;
    private InputStream emptyDatasourcePropertesFile;
    private LoggerInitializationListener sut;
    private ServletContextEvent servletContextEvent;
    private String testFile;
    private URL testFileURI;

    @BeforeClass
    public void setUpCommonTestData() throws Exception {
        MockContextFactory.setAsInitial();
        tomcatContext = new MockContextFactory().getInitialContext(null);
        new InitialContext().bind("java:/comp/env", tomcatContext);

        emptyDatasourcePropertesFile = new ByteArrayInputStream(new byte[0]);

        ServletContext servletContext = mock(ServletContext.class);
        doNothing().when(servletContext).log(anyString());
        doNothing().when(servletContext).log(anyString(), any(Throwable.class));
        servletContextEvent = mock(ServletContextEvent.class);
        doReturn(servletContext).when(servletContextEvent).getServletContext();

        testFile = "/somepath/log4j.xml";
        testFileURI = new File(testFile).toURI().toURL();
    }

    @BeforeMethod
    public void setUpCurrentTest() throws Exception {
        sut = spy(new LoggerInitializationListener());
        doReturn(true).when(sut).configureLog4j(any(URL.class));
        emptyDatasourcePropertesFile.reset();
        doReturn(emptyDatasourcePropertesFile).when(sut).getPropertiesFileStream();
        tomcatContext.unbind(LoggerInitializationListener.LOG4J_CONFIGURATION_FILE);
        System.clearProperty(LoggerInitializationListener.LOG4J_CONFIGURATION_FILE); // filled after all tests
    }

    @Test
    public void shouldLookForConfigurationInJndi() throws Exception {
        tomcatContext.bind(LoggerInitializationListener.LOG4J_CONFIGURATION_FILE, testFile);
        sut.contextInitialized(servletContextEvent);
        verify(sut).configureLog4j(eq(testFileURI));
        verify(sut, never()).configureLog4j(not(eq(testFileURI)));
    }

    @Test
    public void shouldLookForConfigurationInDataSourceClass() throws Exception {
        byte[] is = (LoggerInitializationListener.LOG4J_CONFIGURATION_FILE+"="+testFile).getBytes("UTF-8");
        InputStream datasourcePropertesFile = new ByteArrayInputStream(is);
        doReturn(datasourcePropertesFile).when(sut).getPropertiesFileStream();
        sut.contextInitialized(servletContextEvent);
        verify(sut).configureLog4j(eq(testFileURI));
        verify(sut, never()).configureLog4j(not(eq(testFileURI)));
    }

    @Test
    public void shouldLookForConfigurationInSystemProperties() throws Exception {
        System.setProperty(LoggerInitializationListener.LOG4J_CONFIGURATION_FILE, testFile);
        sut.contextInitialized(servletContextEvent);
        verify(sut).configureLog4j(eq(testFileURI));
        verify(sut, never()).configureLog4j(not(eq(testFileURI)));
    }

    @Test
    public void shouldLoadDefaultConfiguration() throws Exception {
        sut.contextInitialized(servletContextEvent);
        verify(sut, never()).configureLog4j(eq(testFileURI));
        verify(sut).configureLog4j(not(eq(testFileURI)));
    }

    @Test
    public void shouldReturnLog4jOverrideProperty() throws Exception {
        System.setProperty("log4j.defaultInitOverride", "salt");
        sut.contextInitialized(servletContextEvent);
        assertEquals("salt", System.getProperty("log4j.defaultInitOverride"));
    }
}