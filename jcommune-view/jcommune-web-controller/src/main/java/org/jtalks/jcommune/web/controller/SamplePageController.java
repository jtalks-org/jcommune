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

import org.jtalks.jcommune.model.entity.SamplePage;
import org.jtalks.jcommune.service.SamplePageService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.web.dto.PostDto;
import org.jtalks.jcommune.web.dto.SamplePageDto;
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
public class SamplePageController {

    public static final String PAGE_DTO = "samplePageDto";
    public static final String PAGE_ID = "pageId";

    private SamplePageService samplePageService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private BBCodeService bbCodeService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @Autowired
    public SamplePageController(SamplePageService samplePageService) {
        this.samplePageService = samplePageService;

    }


    @RequestMapping(value = "/pages/{pageId}", method = RequestMethod.GET)
    public ModelAndView showPage(@PathVariable(PAGE_ID) Long pageId) throws NotFoundException {
        SamplePage page = samplePageService.get(pageId);

        return new ModelAndView("page")
                .addObject(PAGE_DTO, new SamplePageDto(page));
    }


    @RequestMapping(value = "/pages/{pageId}/edit", method = RequestMethod.GET)
    public ModelAndView showEditPage(@PathVariable(PAGE_ID) Long pageId) throws NotFoundException {
        SamplePage page = samplePageService.get(pageId);

        return new ModelAndView("faqEditor")
                .addObject(PAGE_DTO, new SamplePageDto(page));
    }

    @RequestMapping(value = "/pages/{pageId}/edit", method = RequestMethod.POST)
    public ModelAndView update(@Valid @ModelAttribute SamplePageDto samplePageDto,
                               BindingResult result,
                               @PathVariable(PAGE_ID) Long pageId) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView("faqEdit")
                    .addObject(PAGE_ID, pageId);
        }
        samplePageService.updatePage(samplePageDto.getId(), samplePageDto.getNameText(), samplePageDto.getContentText());
        return new ModelAndView("redirect:/pages/" + pageId);
    }

}
