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
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */


package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author Vitaliy kravchenko
 * @author Kirill Afonin
 */

@Controller
public final class BranchController {

    private BranchService branchService;
    private TopicService topicService;

    /**
     * Constructor creates MVC controller with specified BranchService
     *
     * @param branchService autowired object from Spring Context
     * @param topicService  autowired object from Spring Context
     */
    @Autowired
    public BranchController(BranchService branchService, TopicService topicService) {
        this.branchService = branchService;
        this.topicService = topicService;
    }


    /**
     * This method handles GET request and produces JSP page with all topic branches
     *
     * @return {@link ModelAndView} with view name as renderAllBranches
     */
    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public ModelAndView branchesList() {
        return new ModelAndView("branchesList", "topicsBranchList", branchService.getAll());
    }

    /**
     * Displays to user a list of topic from the chosen branch with pagination.
     *
     * @param branchId branch for display
     * @param page     page
     * @param size     number of posts on the page
     * @return {@code ModelAndView} with topics list and vars for pagination
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException when branch not found
     */
    @RequestMapping(value = "/branch/{branchId}", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable("branchId") long branchId,
                             @RequestParam(value = "page", required = false) Integer page,
                             @RequestParam(value = "size", required = false) Integer size) throws NotFoundException {
        int topicsCount = topicService.getTopicsInBranchCount(branchId);
        Pagination pag = new Pagination(page, size, topicsCount);

        List<Topic> topics = topicService.getTopicRangeInBranch(branchId, pag.getStart(), pag.getPageSize());

        return new ModelAndView("topicList")
                .addObject("branchId", branchId)
                .addObject("topics", topics)
                .addObject("maxPages", pag.getMaxPages())
                .addObject("page", pag.getPage());
    }

}