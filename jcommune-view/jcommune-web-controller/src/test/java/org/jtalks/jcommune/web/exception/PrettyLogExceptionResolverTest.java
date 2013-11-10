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
package org.jtalks.jcommune.web.exception;


import org.apache.commons.logging.Log;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.ReflectionUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Vitaliy Kravchenko
 */
public class PrettyLogExceptionResolverTest {
    private PrettyLogExceptionResolver prettyLogExceptionResolver;

    @BeforeMethod
    public void setUp() throws Exception {
        prettyLogExceptionResolver = new PrettyLogExceptionResolver();
    }

    @Test
    public void testLogExceptionWithIncomingNotFoundException() throws Exception {
        Log mockLog = replaceLoggerWithMock(prettyLogExceptionResolver);
        NotFoundException notFoundException = new NotFoundException("Entity not found");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.setContent("".getBytes());
        prettyLogExceptionResolver.logException(notFoundException, request);

        verify(mockLog).info("Entity not found");
    }

    @Test
    public void testLogExceptionWithIncomingAccessDeniedException() throws Exception {
        Log mockLog = replaceLoggerWithMock(prettyLogExceptionResolver);
        AccessDeniedException accessDeniedException = new AccessDeniedException("Access denied");

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/testing/url/42");
        request.setServerName("testserver.com");
        request.setServerPort(8080);
        request.setContent("12345".getBytes());
        request.setUserPrincipal(new UsernamePasswordAuthenticationToken("username", "password"));

        prettyLogExceptionResolver.logException(accessDeniedException, request);

        verify(mockLog).info(
                "Access was denied for user [username] trying to POST http://testserver.com:8080/testing/url/42");
    }

    @Test
    public void testLogExceptionWithoutNotFoundException() throws Exception {
        Log mockLog = replaceLoggerWithMock(prettyLogExceptionResolver);
        Exception exception = new Exception();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.setContent("".getBytes());
        prettyLogExceptionResolver.logException(exception, request);

        verify(mockLog, times(1)).info(anyString());
    }

    private Log replaceLoggerWithMock(PrettyLogExceptionResolver resolver) throws Exception {
        Log mockLog = mock(Log.class);
        Field loggerField = ReflectionUtils.findField(PrettyLogExceptionResolver.class, "logger");
        loggerField.setAccessible(true);
        loggerField.set(resolver, mockLog);
        return mockLog;
    }
}
