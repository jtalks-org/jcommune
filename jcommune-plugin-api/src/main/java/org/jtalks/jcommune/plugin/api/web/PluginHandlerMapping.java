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

import org.jtalks.jcommune.plugin.api.dto.HandlerStateDto;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Stryzhonok
 */
public class PluginHandlerMapping extends RequestMappingHandlerMapping {

    private static final PluginHandlerMapping INSTANCE = new PluginHandlerMapping();
    private List<HandlerStateDto> handlerStateDtos = new ArrayList<>();

    private PluginHandlerMapping() {

    }

    public static PluginHandlerMapping getInstance() {
        return INSTANCE;
    }

    // need to run tests without context
    @Override
    protected boolean isContextRequired() {
        return false;
    }

    /**
     * Adds handlers from controller to handler mapping
     * Note: class should be annotated with {@link org.springframework.stereotype.Controller} annotation and all
     * handler method should be annotated with {@link org.springframework.web.bind.annotation.RequestMapping} annotation
     *
     * @param controller controller object to map
     */
    public void addController(Object controller) {
        HandlerStateDto handlerStateDto = getHandlerStateDto(controller.getClass().getName());
        if (handlerStateDto == null) {
            handlerStateDtos.add(new HandlerStateDto(controller.getClass().getName(), true));
            INSTANCE.detectHandlerMethods(controller);
            if (controller instanceof ApplicationContextAware) {
                ((ApplicationContextAware) controller).setApplicationContext(getApplicationContext());
            }
        } else if (!handlerStateDto.isEnabled()) {
            handlerStateDto.setEnabled(true);
        }
    }

    /**
     * Disables handlers from specified controller
     *
     * @param controller controller bean to disable handlers
     */
    public void deactivateController(Object controller) {
        HandlerStateDto handlerStateDto = getHandlerStateDto(controller.getClass().getName());
        if (handlerStateDto != null) {
            handlerStateDto.setEnabled(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        HandlerMethod handlerMethod = super.getHandlerInternal(request);
        HandlerStateDto handlerStateDto = getHandlerStateDto(handlerMethod.getBean().getClass().getName());
        if (handlerStateDto != null && !handlerStateDto.isEnabled()) {
            return null;
        } else {
            return handlerMethod;
        }
    }

    /**
     * Search in handlerStateDtos for {@link HandlerStateDto} with specified name
     *
     * @param beanClassName name to search
     * @return {@link HandlerStateDto} with specified name or <code>null</code> if not found
     */
    private HandlerStateDto getHandlerStateDto(String beanClassName) {
        for (HandlerStateDto handlerStateDto : handlerStateDtos) {
            if (handlerStateDto.getBeanClassName().equals(beanClassName)) {
                return handlerStateDto;
            }
        }
        return null;
    }

}
