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

package org.jtalks.jcommune.web.controller.integration;

import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

/**
 * Controller that handles notifications from Poulpe
 * @author Vyacheslav Mishcheryakov
 *
 */
@Controller
public class PoulpeNotificationHandler {
    
    private static final String ERROR_MESSAGE_PARAMETER = "errorMessage";
    
    private BranchService branchService;
    private SectionService sectionService;
    
    
    @Autowired
    public PoulpeNotificationHandler(BranchService branchService,
                            SectionService sectionService) {
        this.branchService = branchService;
        this.sectionService = sectionService;
    }


    /**
     * Handles notification about branch deletion. This method deletes all 
     * topics in branch
     * @param branchId branch id
     * @throws NotFoundException is thrown if branch not found
     */
    @RequestMapping(value="/branches/{branchId}", method=RequestMethod.DELETE)
    @ResponseBody
    public void deleteBranch(@PathVariable("branchId") long branchId) throws NotFoundException {
        branchService.deleteAllTopics(branchId);
    }
    
    /**
     * Handles notification about section deletion. Removes all branches from
     * section. 
     * @param sectionId section id
     * @throws NotFoundException is thrown if section not found
     */
    @RequestMapping(value="/sections/{sectionId}", method=RequestMethod.DELETE)
    @ResponseBody
    public void deleteSection(@PathVariable("sectionId") long sectionId) throws NotFoundException {
        sectionService.deleteAllBranches(sectionId);
    }
    
    /**
     * Catches all exceptions threw by any method in this controller and 
     * returns HTTP status 500 with error message in response body instead.  
     * @param exception exception that was thrown
     * @return {@link ModelAndView} object with JSON view and error message as model 
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllExceptions(Exception exception) {
        MappingJacksonJsonView jsonView = new MappingJacksonJsonView();
        ModelAndView mav = new ModelAndView(jsonView);
        mav.addObject(ERROR_MESSAGE_PARAMETER, exception.getMessage());
        return mav;
    }

}
