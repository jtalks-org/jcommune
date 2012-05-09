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

import org.jtalks.jcommune.model.entity.SimplePage;
import org.jtalks.jcommune.service.SimplePageService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.web.dto.SimplePageDto;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


/**
 * Controller for sapmle pages
 *
 * @author Scherbakov Roman
 */

@Controller
public class SimplePageController {

    public static final String PAGE_DTO = "simplePageDto";
    public static final String PAGE_ID = "pageId";
    public static final String PAGE_PATH_NAME = "pagePathName";

    private SimplePageService simplePageService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private BBCodeService bbCodeService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @Autowired
    public SimplePageController(SimplePageService simplePageService, BBCodeService bbCodeService) {
        this.simplePageService = simplePageService;
        this.bbCodeService = bbCodeService;

    }


    @RequestMapping(value = "/pages/{pagePathName}", method = RequestMethod.GET)
    public ModelAndView showPage(@PathVariable(PAGE_PATH_NAME) String pagePathName) throws NotFoundException {
        SimplePage page = simplePageService.getPageByPathName(pagePathName);

        SimplePageDto pageDto = new SimplePageDto(page);
        pageDto.setContentText(bbCodeService.convertBbToHtml(page.getContent()));
        return new ModelAndView("page")
                .addObject(PAGE_DTO, pageDto);
    }


    @RequestMapping(value = "/pages/{pagePathName}/edit", method = RequestMethod.GET)
    public ModelAndView showEditPage(@PathVariable(PAGE_PATH_NAME) String pagePathName) throws NotFoundException {
        SimplePage page = simplePageService.getPageByPathName(pagePathName);

        return new ModelAndView("faqEditor")
                .addObject(PAGE_DTO, new SimplePageDto(page));
    }

    @RequestMapping(value = "/pages/{pagePathName}/edit", method = RequestMethod.POST)
    public ModelAndView update(@Valid @ModelAttribute SimplePageDto simplePageDto,
                               BindingResult result,
                               @PathVariable(PAGE_PATH_NAME) String pagePathName) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView("faqEdit")
                    .addObject(PAGE_ID, pagePathName);
        }
        simplePageService.updatePage(simplePageDto.getId(), simplePageDto.getNameText(), simplePageDto.getContentText());
        return new ModelAndView("redirect:/pages/" + pagePathName);
    }

}
