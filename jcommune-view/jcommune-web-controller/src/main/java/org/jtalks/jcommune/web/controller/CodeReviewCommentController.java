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

import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.service.PostCommentService;
import org.jtalks.jcommune.service.CodeReviewService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.CodeReviewCommentDto;
import org.jtalks.jcommune.web.dto.json.FailJsonResponse;
import org.jtalks.jcommune.web.dto.json.FailValidationJsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseReason;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * Serves code review comment management web requests
 *
 * @author Vyachesla Mishcheryakov
 * @see org.jtalks.jcommune.model.entity.Topic
 */
@Controller
public class CodeReviewCommentController {

    public static final String BRANCH_ID = "branchId";
    public static final String REVIEW_ID = "reviewId";
    public static final String COMMENT_ID = "commentId";
    public static final String BREADCRUMB_LIST = "breadcrumbList";
    
    private CodeReviewService codeReviewService;
    private PostCommentService postCommentService;
    
    /**
     * @param codeReviewService        to operate with {@link org.jtalks.jcommune.model.entity.CodeReview} entities
     * @param postCommentService to operate with (@link {@link org.jtalks.jcommune.model.entity.PostComment} entities
     */
    @Autowired
    public CodeReviewCommentController(CodeReviewService codeReviewService,
                                PostCommentService postCommentService) {
        this.codeReviewService = codeReviewService;
        this.postCommentService = postCommentService;
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
        PostComment addedComment = codeReviewService.addComment(
                reviewId, commentDto.getLineNumber(), commentDto.getBody());
        CodeReviewCommentDto addedCommentDto = new CodeReviewCommentDto(addedComment);
        return new JsonResponse(JsonResponseStatus.SUCCESS, addedCommentDto);
    }

    /**
     * Deletes CR comment from review
     *
     * @param commentId comment ID
     * @param reviewId  ID of review where delete comment to
     * @return response with status 'success' if comment
     *         was deleted or 'fail' with no objects if there were some errors
     * @throws NotFoundException when no review with <code>reviewId</code>
     *                           or comment with <code>commentId</code> was found
     */
    @RequestMapping(value = "/reviewcomments/delete", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse deleteComment(
            @RequestParam(COMMENT_ID) Long commentId,
            @RequestParam(REVIEW_ID) Long reviewId) throws NotFoundException {
        codeReviewService.deleteComment(postCommentService.get(commentId), codeReviewService.get(reviewId));
        return new JsonResponse(JsonResponseStatus.SUCCESS);
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
            BindingResult bindingResult,
            @RequestParam("branchId") long branchId) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return new FailValidationJsonResponse(bindingResult.getAllErrors());
        }
        PostComment editedComment = postCommentService.updateComment(
                commentDto.getId(), commentDto.getBody(), branchId);
        CodeReviewCommentDto editedCommentDto = new CodeReviewCommentDto(editedComment);
        return new JsonResponse(JsonResponseStatus.SUCCESS, editedCommentDto);
    }
    
    /**
     * Returns fail response  when security exception is through
     * @return fail response with status 'Fail' and reason 'security'
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public FailJsonResponse securityError() {
        return new FailJsonResponse(JsonResponseReason.SECURITY);
    }
    
    /**
     * Returns fail response  when entity with given ID was not found
     * @return fail response with status 'Fail' and reason 'entity-not-found'
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public FailJsonResponse entityNotFoundError() {
        return new FailJsonResponse(JsonResponseReason.ENTITY_NOT_FOUND);
    }
}
