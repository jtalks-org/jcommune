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

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.util.Pagination;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Vitaliy kravchenko
 * @author Kirill Afonin
 * @author Alexandre Teterin
 */

@Controller
public final class BranchController {

    public static final String PAGE = "page";
    private BranchService branchService;
    private TopicService topicService;
    private BreadcrumbBuilder breadcrumbBuilder;

    /**
     * Constructor creates MVC controller with specified BranchService
     *
     * @param branchService     autowired object from Spring Context
     * @param topicService      autowired object from Spring Context
     * @param breadcrumbBuilder the object which provides actions on
     *                          {@link org.jtalks.jcommune.web.dto.BreadcrumbBuilder} entity
     */
    @Autowired
    public BranchController(BranchService branchService,
                            TopicService topicService,
                            BreadcrumbBuilder breadcrumbBuilder) {
        this.branchService = branchService;
        this.topicService = topicService;
        this.breadcrumbBuilder = breadcrumbBuilder;
    }

    /**
     * Displays to user a list of topic from the chosen branch with pagination.
     *
     * @param branchId branch for display
     * @param page     page
     * @param size     number of posts on the page
     * @return {@code ModelAndView} with topics list and vars for pagination
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    @RequestMapping(value = "/branches/{branchId}", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable("branchId") long branchId,
                             @RequestParam(value = PAGE, required = false) Integer page,
                             @RequestParam(value = "size", required = false) Integer size) throws NotFoundException {
        int topicsCount = topicService.getTopicsInBranchCount(branchId);
        Pagination pag = new Pagination(page, size, topicsCount);

        List<Topic> topics = topicService.getTopicRangeInBranch(branchId, pag.getStart(), pag.getPageSize());

        Branch branch = branchService.get(branchId);

        return new ModelAndView("topicList")
                .addObject("branch", branch)
                .addObject("branchId", branchId)
                .addObject("topics", topics)
                .addObject("maxPages", pag.getMaxPages())
                .addObject(PAGE, pag.getPage())
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(branchService.get(branchId)));
    }

    /**
     * Displays to user a list of topic past last 24 hour.
     *
     * @param page page
     * @param size number of posts on the page
     * @return {@code ModelAndView} with topics list and vars for pagination
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    @RequestMapping(value = "/topics/recent", method = RequestMethod.GET)
    public ModelAndView show(@RequestParam(value = PAGE, required = false) Integer page,
                             @RequestParam(value = "size", required = false) Integer size, HttpSession session) throws NotFoundException {

        DateTime lastLogin = (DateTime) session.getAttribute("lastlogin");
        if (lastLogin == null)
            lastLogin = new DateTime().minusDays(1);
        int topicsCount = topicService.getTopicsPastLastDayCount(lastLogin);
        Pagination pag = new Pagination(page, size, topicsCount);

        List<Topic> topics = topicService.getAllTopicsPastLastDay(pag.getStart(), pag.getPageSize(), lastLogin);

        return new ModelAndView("recent")
                .addObject("topics", topics)
                .addObject("maxPages", pag.getMaxPages())
                .addObject(PAGE, pag.getPage())
                .addObject("breadcrumbList", breadcrumbBuilder.getRecentBreadcrumb());
    }

}