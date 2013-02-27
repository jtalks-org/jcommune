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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.ReflectionUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sun.security.acl.PrincipalImpl;

import javax.servlet.http.HttpServletRequest;
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
        prettyLogExceptionResolver.logException(notFoundException, mock(HttpServletRequest.class));

        verify(mockLog).info("Entity not found");
    }

    @Test
    public void testLogExceptionWithIncomingAccessDeniedException() throws Exception {
        Log mockLog = replaceLoggerWithMock(prettyLogExceptionResolver);
        AccessDeniedException accessDeniedException = new AccessDeniedException("Access denied");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getUserPrincipal()).thenReturn(new PrincipalImpl("test user"));
        String url = "http://testserver.com/testing/url/42";
        when(request.getRequestURL()).thenReturn(new StringBuffer(url));

        prettyLogExceptionResolver.logException(accessDeniedException, request);

        verify(mockLog).info("Access was denied for user [test user] trying to POST http://testserver.com/testing/url/42");
    }

    @Test
    public void testLogExceptionWithoutNotFoundException() throws Exception {
        Log mockLog = replaceLoggerWithMock(prettyLogExceptionResolver);
        Exception exception = new Exception();
        prettyLogExceptionResolver.logException(exception, mock(HttpServletRequest.class));

        verify(mockLog, times(0)).info(anyString());
    }

    private Log replaceLoggerWithMock(PrettyLogExceptionResolver resolver) throws Exception {
        Log mockLog = mock(Log.class);
        Field loggerField = ReflectionUtils.findField(PrettyLogExceptionResolver.class, "logger");
        loggerField.setAccessible(true);
        loggerField.set(resolver, mockLog);
        return mockLog;
    }
}
