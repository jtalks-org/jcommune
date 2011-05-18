/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 */


package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.TopicBranch;
import org.jtalks.jcommune.service.TopicBranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
public class TopicsBranchesController {

    private TopicBranchService topicBranchService;

    @Autowired
    public TopicsBranchesController(TopicBranchService topicBranchService) {
        this.topicBranchService = topicBranchService;
    }

    @ModelAttribute("topicsBranchList")
    public List<TopicBranch> populateFormWithBranches() {
        return topicBranchService.getAll();
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public ModelAndView displayAllTopicsBranches() {
        return new ModelAndView("renderAllBranches");
    }

}