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
import org.springframework.util.ReflectionUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Vitaliy Kravchenko
 */
public class PrettyLogExceptionResolverTest {

    private PrettyLogExceptionResolver prettyLogExceptionResolver = new PrettyLogExceptionResolver();

    private Log originalLog;

    private Log mockLog;

    @BeforeMethod
    public void setUp() throws Exception {
        mockLog = mock(Log.class);
        Field loggerField = getLoggerField();
        originalLog = (Log) loggerField.get(prettyLogExceptionResolver);
        ReflectionUtils.setField(loggerField, prettyLogExceptionResolver, mockLog);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        Field field = getLoggerField();
        ReflectionUtils.setField(field, prettyLogExceptionResolver, originalLog);
    }

    @Test
    public void testLogExceptionWithIncomingNotFoundException() throws Exception {
        final String exceptionMessage = "Entity not found";
        NotFoundException notFoundException = new NotFoundException(exceptionMessage);
        prettyLogExceptionResolver.logException(notFoundException, mock(HttpServletRequest.class));

        verify(mockLog).info(exceptionMessage);
    }

    @Test
    public void testLogExceptionWithoutNotFoundException() throws Exception {
        Exception exception = new Exception();
        prettyLogExceptionResolver.logException(exception, mock(HttpServletRequest.class));

        verify(mockLog, times(0)).info(anyString());
    }

    private Field getLoggerField() {
        Field loggerField = ReflectionUtils.findField(PrettyLogExceptionResolver.class, "logger");
        loggerField.setAccessible(true);
        return loggerField;
    }
}
