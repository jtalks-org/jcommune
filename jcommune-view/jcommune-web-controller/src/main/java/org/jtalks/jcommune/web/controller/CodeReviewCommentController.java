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

import javax.validation.Valid;

import org.jtalks.jcommune.model.entity.CodeReview;
import org.jtalks.jcommune.model.entity.CodeReviewComment;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.CodeReviewCommentService;
import org.jtalks.jcommune.service.CodeReviewService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.CodeReviewCommentDto;
import org.jtalks.jcommune.web.dto.json.FailValidationJsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Serves code review comment management web requests
 *
 * @author Vyachesla Mishcheryakov
 * @see Topic
 */
@Controller
public class CodeReviewCommentController {

    public static final String BRANCH_ID = "branchId";
    public static final String BREADCRUMB_LIST = "breadcrumbList";
    
    private CodeReviewService codeReviewService;
    private CodeReviewCommentService codeReviewCommentService;
    
    /**
     * @param codeReviewService        to operate with {@link CodeReview} entities
     * @param codeReviewCommentService to operate with (@link {@link CodeReviewComment} entities
     */
    @Autowired
    public CodeReviewCommentController(CodeReviewService codeReviewService,
                                CodeReviewCommentService codeReviewCommentService) {
        this.codeReviewService = codeReviewService;
        this.codeReviewCommentService = codeReviewCommentService;
    }
    
    /**
     * This method turns the trim binder on. Trim binder
     * removes leading and trailing spaces from the submitted fields.
     * So, it ensures, that all validations will be applied to
     * trimmed field values only.
     *
     * @param binder Binder object to be injected
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Adds CR comment to review
     * @param commentDto incoming DTO object from client
     * @param bindingResult object contains validation information
     * @param reviewId ID of review where add comment to
     * @return response with status 'success' and comment DTO object if comment 
     *          was added or 'fail' with no objects if there were some errors
     * @throws NotFoundException when no review with <code>reviewId</code>was found
     */
    @RequestMapping(value="/reviewcomments/new", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse addComment(
            @Valid @ModelAttribute CodeReviewCommentDto commentDto,
            BindingResult bindingResult,
            @RequestParam("reviewId") Long reviewId) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return new FailValidationJsonResponse(bindingResult.getAllErrors());
        }
        CodeReviewComment addedComment = codeReviewService.addComment(
                reviewId, commentDto.getLineNumber(), commentDto.getBody());
        CodeReviewCommentDto addedCommentDto = new CodeReviewCommentDto(addedComment);
        return new JsonResponse(JsonResponseStatus.Success, addedCommentDto);
    }
    
    /**
     * Save CR comment
     * @param commentDto incoming DTO object from client
     * @param bindingResult object contains validation information
     * @return response with status 'success' and comment DTO object if comment 
     *          was added or 'fail' with no objects if there were some errors
     * @throws NotFoundException when no CR comment with <code>commentDto.id</code> 
     *          was found
     */
    @RequestMapping(value="/reviewcomments/edit", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse editComment(
            @Valid @ModelAttribute CodeReviewCommentDto commentDto,
            BindingResult bindingResult) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return new FailValidationJsonResponse(bindingResult.getAllErrors());
        }
        CodeReviewComment editedComment = codeReviewCommentService.updateComment(
                commentDto.getId(), commentDto.getBody());
        CodeReviewCommentDto addedCommentDto = new CodeReviewCommentDto(editedComment);
        return new JsonResponse(JsonResponseStatus.Success, addedCommentDto);
    }
    
}
