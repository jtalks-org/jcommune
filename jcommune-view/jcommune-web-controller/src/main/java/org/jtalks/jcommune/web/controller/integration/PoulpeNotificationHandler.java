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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller that handles notifications from Poulpe
 * @author Vyacheslav Mishcheryakov
 *
 */
@Controller
public class PoulpeNotificationHandler {
    
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

}
