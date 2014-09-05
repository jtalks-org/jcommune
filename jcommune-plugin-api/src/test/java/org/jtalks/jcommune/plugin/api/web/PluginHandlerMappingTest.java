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
package org.jtalks.jcommune.plugin.api.web;

import org.mockito.Mock;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Mikhail Stryzhonok
 */
public class PluginHandlerMappingTest {

    @Mock
    private PluginHandlerMapping handlerMapping;


    @BeforeMethod
    public void init() {
        initMocks(this);
    }

    @Test
    public void registerHandlerMethodShouldCallRegisterPluginHandlerMethodIfMethodIsPluginHandler() {
        doCallRealMethod().when(handlerMapping).registerHandlerMethod(any(), any(Method.class),
                any(RequestMappingInfo.class));

        TestPluginController controller = new TestPluginController();
        RequestMappingInfo mappingInfo = new RequestMappingInfo(null, null, null, null, null, null, null);
        handlerMapping.registerHandlerMethod(controller, TestPluginController.class.getMethods()[0], mappingInfo);

        verify(handlerMapping).registerPluginHandlerMethod(controller, TestPluginController.class.getMethods()[0],
                mappingInfo);
    }

    private class TestPluginController implements PluginController {

        public void testMethod() {

        }
    }

}
