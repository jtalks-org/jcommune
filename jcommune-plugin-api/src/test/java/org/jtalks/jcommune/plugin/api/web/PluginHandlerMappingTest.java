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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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

    @AfterMethod
    public void clearMapping() {
        PluginHandlerMapping.getInstance().getPluginHandlerMethods().clear();
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

    @Test
    public void pluginHandlerMethodsShouldNotContainMappingsIfNoHandlersMapped() {
        PluginHandlerMapping mapping = PluginHandlerMapping.getInstance();

        Map<PluginHandlerMapping.MethodAwareKey, HandlerMethod> result = mapping.getPluginHandlerMethods();

        assertTrue(result.isEmpty());
    }

    @Test
    public void pluginHandlerMethodsShouldNotContainMappingsIfOnlyApplicationControllersWereMapped() {
        PluginHandlerMapping mapping = PluginHandlerMapping.getInstance();
        TestController controller = new TestController();
        RequestMappingInfo mappingInfo = new RequestMappingInfo(null, null, null, null, null, null, null);

        mapping.registerHandlerMethod(controller, TestController.class.getMethods()[0], mappingInfo);
        Map<PluginHandlerMapping.MethodAwareKey, HandlerMethod> result = mapping.getPluginHandlerMethods();

        assertTrue(result.isEmpty());
    }

    @Test
    public void addControllerShouldMapAllControllerMethods() throws Exception {
        PluginHandlerMapping mapping = PluginHandlerMapping.getInstance();
        TestPluginController controller = new TestPluginController();
        mapping.addController(controller);

        Map<PluginHandlerMapping.MethodAwareKey, HandlerMethod> result = mapping.getPluginHandlerMethods();

        assertEquals(result.size(), 1);

        assertEquals(mapping.findHandlerMethod(new PluginHandlerMapping.MethodAwareKey(RequestMethod.GET, "/test/"))
                        .getMethod(),
                controller.getClass().getMethod("testMethod"));
    }

    @Test
    public void deactivateControllerShouldRemoveAllControllerMethods() {
        PluginHandlerMapping mapping = PluginHandlerMapping.getInstance();
        TestPluginController controller = new TestPluginController();
        mapping.addController(controller);
        mapping.deactivateController(controller);

        Map<PluginHandlerMapping.MethodAwareKey, HandlerMethod> result = mapping.getPluginHandlerMethods();

        assertTrue(result.isEmpty());
    }

    @Test
    public void allMappedUrlsShouldEndsWithSlash() {
        PluginHandlerMapping mapping = PluginHandlerMapping.getInstance();
        TestPluginController controller = new TestPluginController();
        mapping.addController(controller);

        Set<PluginHandlerMapping.MethodAwareKey> keys = mapping.getPluginHandlerMethods().keySet();

        for (PluginHandlerMapping.MethodAwareKey key : keys) {
            assertTrue(key.getUrlRegExp().endsWith("/"));
        }
    }

    @Test
    public void handlerMapperShouldDifferHandlersWithSameUrlAndDifferentRequestMethods() throws Exception{
        PluginHandlerMapping mapping = PluginHandlerMapping.getInstance();
        ControllerWithDifferentRequestMethodsOnSameUrl controller = new ControllerWithDifferentRequestMethodsOnSameUrl();
        mapping.addController(controller);

        Map<PluginHandlerMapping.MethodAwareKey, HandlerMethod> result = mapping.getPluginHandlerMethods();

        assertEquals(result.size(), 2);
        assertEquals(result.get(new PluginHandlerMapping.MethodAwareKey(RequestMethod.GET, "/test/")).getMethod(),
                controller.getClass().getMethod("testGet"));
        assertEquals(result.get(new PluginHandlerMapping.MethodAwareKey(RequestMethod.POST, "/test/")).getMethod(),
                controller.getClass().getMethod("testPost"));
    }


    @Controller
    private class TestPluginController implements PluginController {

        @RequestMapping(value = "/test", method = RequestMethod.GET)
        public void testMethod() {

        }

        @Override
        public void setApiPath(String apiPath) {

        }
    }

    @Controller
    private class TestController {

        @RequestMapping(value = "/test", method = RequestMethod.GET)
        public void testMethod() {

        }
    }

    @Controller
    private class ControllerWithDifferentRequestMethodsOnSameUrl implements PluginController {

        @RequestMapping(value = "/test", method = RequestMethod.GET)
        public void testGet() {

        }

        @RequestMapping(value = "/test", method = RequestMethod.POST)
        public void testPost() {

        }

        @Override
        public void setApiPath(String apiPath) {

        }
    }

}
