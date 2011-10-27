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
package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for Topic answer related actions.
 *
 * @author Pavel Vervenko
 * @author Kravchenko Vitaliy
 * @author Alexandre Teterin
 */
@Controller
public class TopicAnswerController {

    public static final int MIN_ANSWER_LENGTH = 1;
    private TopicService topicService;
    private BreadcrumbBuilder breadcrumbBuilder;

    /**
     * Constructor creates MVC controller with specifying TopicService, SecurityService.
     *
     * @param topicService {@link TopicService} to be injected
     * @param breadcrumbBuilder the object which provides actions on
     * {@link org.jtalks.jcommune.web.dto.BreadcrumbBuilder} entity
     */
    @Autowired
    public TopicAnswerController(TopicService topicService, BreadcrumbBuilder breadcrumbBuilder) {
        this.topicService = topicService;
        this.breadcrumbBuilder = breadcrumbBuilder;
    }



}
