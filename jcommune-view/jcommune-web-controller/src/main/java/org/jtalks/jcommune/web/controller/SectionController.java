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

import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.jtalks.jcommune.web.dto.SectionDto;
import org.jtalks.jcommune.web.util.ForumStatisticsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;

/**
 * Displays to user page contains section list with related branch lists
 * and page contains branch list from the chosen section.
 * <p/>
 * This is also the main forum page, mapped on url root. Section management is out
 * of scope of forum development, section operations are to be implemented in
 * a admin panel (separate Poulpe project)
 *
 * @author Max Malakhov
 * @author Alexandre Teterin
 * @author Evgeniy Naumenko
 * @author Eugeny Batov
 */

@Controller
public class SectionController {

    private SectionService sectionService;
    private ForumStatisticsProvider forumStaticsProvider;
    private LocationService locationService;

    private static final int RECENT_POST_COUNT = 15;

    /**
     * Constructor creates MVC controller with specified SectionService
     *
     * @param sectionService       for all operations with sections
     * @param locationService      for tracking user's location on the forum
     * @param forumStaticsProvider for getting forum statistic information
     */
    @Autowired
    public SectionController(SectionService sectionService,
                             ForumStatisticsProvider forumStaticsProvider,
                             LocationService locationService) {
        this.sectionService = sectionService;
        this.forumStaticsProvider = forumStaticsProvider;
        this.locationService = locationService;
    }


    /**
     * This method handles GET request and produces JSP page with all branch sections
     *
     * @param session http session that will be initiated
     * @return {@link ModelAndView} with view name as renderAllSection
     */
    @RequestMapping(value = {"/", "/sections"}, method = RequestMethod.GET)
    public ModelAndView sectionList(HttpSession session) {
        /*
        Counting the number of active users based on the number of sessions.
        By default, the session will be initialized after controller's invocation,
        so at the time of request processing, we can miss the session
        if the current request is the first one for a particular user.
        To change a default behavior we call getId() method
        that initializes the session right now.
        If a request from the user is not the first one getId() call will have no effect.
        */
        session.getId();
        List<Section> sections = sectionService.getAll();
        sectionService.prepareSectionsForView(sections);
        return new ModelAndView("sectionList")
                .addObject("sectionList", sections)
                .addObject("messagesCount", forumStaticsProvider.getPostsOnForumCount())
                .addObject("registeredUsersCount", forumStaticsProvider.getUsersCount())
                .addObject("visitors", forumStaticsProvider.getOnlineUsersCount())
                .addObject("usersRegistered", forumStaticsProvider.getOnlineRegisteredUsers())
                .addObject("visitorsRegistered", forumStaticsProvider.getOnlineRegisteredUsersCount())
                .addObject("visitorsGuests", forumStaticsProvider.getOnlineAnonymousUsersCount());
    }

    /**
     * Provides all available for move topic sections as a JSON array.
     *
     * @param currentTopicId id of topic that we want to move
     * @return sections list
     */
    @RequestMapping(value = "/sections/json/{currentTopicId}", method = RequestMethod.GET)
    @ResponseBody
    public SectionDto[] sectionList(@PathVariable("currentTopicId") long currentTopicId) {
        List<Section> sections = sectionService.getAllAvailableSections(currentTopicId);
        List<SectionDto> dtos = project(sections, SectionDto.class,
                on(Section.class).getId(),
                on(Section.class).getName());
        return dtos.toArray(new SectionDto[dtos.size()]);
    }

    /**
     * Displays to user a list of branches from the chosen section.
     *
     * @param sectionId section for display
     * @return {@code ModelAndView} the chosen section
     * @throws NotFoundException when section not found
     * @throws AccessDeniedException when denied access a section
     */
    @RequestMapping(value = "/sections/{sectionId}", method = RequestMethod.GET)
    public ModelAndView branchList(@PathVariable("sectionId") long sectionId) throws AccessDeniedException,
        NotFoundException {
        Section section = sectionService.get(sectionId);
        sectionService.ifSectionIsVisible(section);
        sectionService.prepareSectionsForView(Arrays.asList(section));
        return new ModelAndView("branchList")
                .addObject("viewList", locationService.getUsersViewing(section))
                .addObject("section", section);
    }

    /**
     * Displays last messages for the section.
     *
     * @return {@code ModelAndView} with post list and vars for pagination
     */
    @RequestMapping("/sections/{sectionId}/recent")
    public ModelAndView recentBranchPostsPage(@PathVariable("sectionId") long sectionId) throws NotFoundException {
        Section section = sectionService.get(sectionId);
        sectionService.ifSectionIsVisible(section);

        List<Post> posts = sectionService.getLastPostsForSection(section, RECENT_POST_COUNT);

        return new ModelAndView("sections/recent")
                .addObject("feedTitle", section.getName())
                .addObject("feedDescription", section.getDescription())
                .addObject("urlSuffix", "/sections/" + section.getId())
                .addObject("posts", posts);

    }
}
