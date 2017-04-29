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

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.service.PostCommentService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.web.dto.CodeReviewCommentDto;
import org.jtalks.jcommune.web.dto.CodeReviewDto;
import org.jtalks.jcommune.plugin.api.web.dto.json.FailJsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.FailValidationJsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseReason;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Serves code review comment management web requests
 *
 * @author Vyachesla Mishcheryakov
 * @see org.jtalks.jcommune.model.entity.Topic
 */
@Controller
public class CodeReviewCommentController {

    public static final String POST_ID = "postId";
    public static final String COMMENT_ID = "commentId";

    private PostCommentService postCommentService;
    private PostService postService;
    private BBCodeService bbCodeService;

    @Autowired
    public CodeReviewCommentController(PostCommentService postCommentService,
                                       PostService postService,
                                       BBCodeService bbCodeService) {
        this.postCommentService = postCommentService;
        this.postService = postService;
        this.bbCodeService = bbCodeService;
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
     * Returns code review by its ID as JSON data
     *
     * @param postId ID of post
     * @return JSON response object containing string status and review DTO as
     *         result field
     * @throws NotFoundException if code review was not found
     */
    @RequestMapping(value = "/reviews/{postId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse getCodeReview(@PathVariable("postId") Long postId) throws NotFoundException {
        Post post = postService.get(postId);
        CodeReviewDto postDto = new CodeReviewDto(post);
        for (CodeReviewCommentDto postComment : postDto.getComments()) {
            postComment.setBody(bbCodeService.convertBbToHtml(postComment.getBody()));
        }
        return new JsonResponse(JsonResponseStatus.SUCCESS, postDto);
    }

    @RequestMapping(value = "/review/comment/edit/{commentId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse getCodeReviewCommentForEdit(@PathVariable("commentId") Long commentId) throws NotFoundException {
        PostComment postComment = postCommentService.get(commentId);
        return new JsonResponse(JsonResponseStatus.SUCCESS, new CodeReviewCommentDto(postComment));
    }

    @RequestMapping(value = "/review/comment/render/{commentId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse getCodeReviewCommentForRender(@PathVariable("commentId") Long commentId) throws NotFoundException {
        PostComment postComment = postCommentService.get(commentId);
        postComment.setBody(bbCodeService.convertBbToHtml(postComment.getBody()));
        return new JsonResponse(JsonResponseStatus.SUCCESS, new CodeReviewCommentDto(postComment));
    }

    /**
     * Adds CR comment to review
     * @param commentDto incoming DTO object from client
     * @param bindingResult object contains validation information
     * @param postId ID of post where add comment to
     * @return response with status 'success' and comment DTO object if comment 
     *          was added or 'fail' with no objects if there were some errors
     * @throws NotFoundException when no review with <code>reviewId</code>was found
     */
    @RequestMapping(value="/reviewcomments/new", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse addComment(
            @Valid @ModelAttribute CodeReviewCommentDto commentDto,
            BindingResult bindingResult,
            @RequestParam("postId") Long postId) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return new FailValidationJsonResponse(bindingResult.getAllErrors());
        }
        PostComment addedComment = postService.addComment(postId, commentDto.getCommentAttributes(),
                commentDto.getBody());
        addedComment.setBody(bbCodeService.convertBbToHtml(addedComment.getBody()));
        CodeReviewCommentDto addedCommentDto = new CodeReviewCommentDto(addedComment);
        return new JsonResponse(JsonResponseStatus.SUCCESS, addedCommentDto);
    }

    /**
     * Deletes CR comment from review
     *
     * @param commentId comment ID
     * @param postId  ID of post where delete comment to
     * @return response with status 'success' if comment
     *         was deleted or 'fail' with no objects if there were some errors
     * @throws NotFoundException when no review with <code>reviewId</code>
     *                           or comment with <code>commentId</code> was found
     */
    @RequestMapping(value = "/reviewcomments/delete", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse deleteComment(
            @RequestParam(COMMENT_ID) Long commentId,
            @RequestParam(POST_ID) Long postId) throws NotFoundException {
        postService.deleteComment(postService.get(postId), postCommentService.get(commentId));
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
