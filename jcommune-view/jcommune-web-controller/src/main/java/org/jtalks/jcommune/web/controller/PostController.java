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

import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.LastReadPostService;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.web.dto.PostDto;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


/**
 * Controller for post-related actions
 *
 * @author Pavel Vervenko
 * @author Osadchuck Eugeny
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Evgeniy Naumenko
 */
@Controller
public class PostController {

    public static final String TOPIC_ID = "topicId";
    public static final String POST_ID = "postId";
    public static final String POST_DTO = "postDto";
    public static final String POST_BB_CONTENT = "bbContent";
    public static final String TOPIC_TITLE = "topicTitle";

    private PostService postService;
    private LastReadPostService lastReadPostService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private TopicService topicService;
    private BBCodeService bbCodeService;
    private SecurityService securityService;

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
     * @param postService       {@link PostService} instance to be injected
     * @param breadcrumbBuilder the object which provides actions on {@link BreadcrumbBuilder} entity
     * @param topicService      {@link TopicService} to be injected
     * @param bbCodeService     to create valid quotes
     * @param lastReadPostService not to track user posts as updates for himself
     * @param securityService to get the current user information
     */
    @Autowired
    public PostController(PostService postService, BreadcrumbBuilder breadcrumbBuilder,
                          TopicService topicService, BBCodeService bbCodeService,
                          LastReadPostService lastReadPostService, SecurityService securityService) {
        this.postService = postService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.topicService = topicService;
        this.bbCodeService = bbCodeService;
        this.lastReadPostService = lastReadPostService;
        this.securityService = securityService;
    }

    /**
     * Delete post by given id
     *
     * @param postId post
     * @return redirect to topic page
     * @throws NotFoundException when topic or post not found
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/posts/{postId}")
    public String delete(@PathVariable(POST_ID) Long postId) throws NotFoundException {
        Post post = postService.get(postId);
        postService.deletePost(postId);
        return "redirect:/topics/" + post.getTopic().getId();
    }

    /**
     * Edit post page filled with data from post with given id
     *
     * @param topicId topic id
     * @param postId  post id
     * @return redirect to post form page
     * @throws NotFoundException when topic or post not found
     */
    @RequestMapping(value = "/posts/{postId}/edit", method = RequestMethod.GET)
    public ModelAndView editPage(@RequestParam(TOPIC_ID) Long topicId,
                                 @PathVariable(POST_ID) Long postId) throws NotFoundException {
        Post post = postService.get(postId);

        return new ModelAndView("editForm")
                .addObject(POST_DTO, PostDto.getDtoFor(post))
                .addObject(TOPIC_ID, topicId)
                .addObject(POST_ID, postId)
                .addObject(TOPIC_TITLE, post.getTopic().getTitle())
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(post.getTopic()));
    }

    /**
     * Update existing post
     *
     * @param postDto Dto populated in form
     * @param result  validation result
     * @param topicId the current topicId
     * @param postId  the current postId
     * @return {@code ModelAndView} object which will be redirect to topic page
     *         if saved successfully or show form with error message
     * @throws NotFoundException when topic, branch or post not found
     */
    @RequestMapping(value = "/posts/{postId}/edit", method = RequestMethod.POST)
    public ModelAndView update(@Valid @ModelAttribute PostDto postDto,
                               BindingResult result,
                               @RequestParam(TOPIC_ID) Long topicId,
                               @PathVariable(POST_ID) Long postId) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView("editForm")
                    .addObject(TOPIC_ID, topicId)
                    .addObject(POST_ID, postId);
        }
        postService.updatePost(postDto.getId(), postDto.getBodyText());
        return new ModelAndView("redirect:/posts/" + postId);
    }

    /**
     * Creates the answering page with empty answer form
     *
     * @param topicId the id of the topic for the answer
     * @return answering {@code ModelAndView} or redirect to the login page
     * @throws NotFoundException when topic not found
     */
    @RequestMapping(method = RequestMethod.GET, value = "/posts/new")
    public ModelAndView addPost(@RequestParam(TOPIC_ID) Long topicId) throws NotFoundException {
        Topic answeringTopic = topicService.get(topicId);
        return new ModelAndView("answer")
                .addObject("topic", answeringTopic)
                .addObject(TOPIC_ID, topicId)
                .addObject(POST_DTO, new PostDto())
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(answeringTopic));
    }


    /**
     * Creates the answering page with quotation.
     * If user select nothing JS will substitute whole post contents here
     * <p/>
     * Supports post method to pass large quotations.
     * Supports get method as language switching always use get requests.
     *
     * @param postId    identifier os the post we're quoting
     * @param selection text selected by user for the quotation.
     * @return the same view as topic answering page with textarea prefilled with quoted text
     * @throws NotFoundException when topic was not found
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "/posts/{postId}/quote")
    public ModelAndView addPostWithQuote(@PathVariable(POST_ID) Long postId,
                                         @RequestParam("selection") String selection) throws NotFoundException {
        Post source = postService.get(postId);
        ModelAndView mav = addPost(source.getTopic().getId());
        PostDto dto = (PostDto) mav.getModel().get(POST_DTO);
        String content = StringUtils.defaultString(selection, source.getPostContent());
        dto.setBodyText(bbCodeService.quote(content, source.getUserCreated()));
        return mav;
    }

    /**
     * Process the reply form. Adds new post to the specified topic and redirects to the
     * topic view page.
     *
     * @param postDto dto that contains data entered in form
     * @param result  validation result
     * @return redirect to the topic or back to answer pae if validation failed
     * @throws NotFoundException when topic or branch not found
     */
    @RequestMapping(method = RequestMethod.POST, value = "/posts/new")
    public ModelAndView create(@Valid @ModelAttribute PostDto postDto, BindingResult result) throws NotFoundException {
        if (result.hasErrors()) {
            // refill the form fields
            ModelAndView mav = this.addPost(postDto.getTopicId());
            mav.addObject(POST_DTO, postDto);
            return mav;
        }
        Post newbie = topicService.replyToTopic(postDto.getTopicId(), postDto.getBodyText());
        lastReadPostService.markTopicAsRead(newbie.getTopic());
        return new ModelAndView(this.redirectToPageWithPost(newbie.getId()));
    }

    /**
     * Redirects user to the topic view with the appropriate page selected.
     * Method clients should not wary about paging at all, post id
     * is enough to be transferred to the proper page.
     *
     * @param postId unique post identifier
     * @return redirect view to the certain topic page
     * @throws NotFoundException is the is no post for the identifier given
     */
    @RequestMapping(method = RequestMethod.GET, value = "/posts/{postId}")
    public String redirectToPageWithPost(@PathVariable Long postId) throws NotFoundException {
        Post post = postService.get(postId);
        int page = postService.calculatePageForPost(post);
        return new StringBuilder("redirect:/topics/")
                .append(post.getTopic().getId())
                .append("?page=")
                .append(page)
                .append("#")
                .append(postId)
                .toString();
    }

    /**
     * Converted post with bb codes to HTML
     *
     * @param bbContent post with bb codes
     * @return HTML content for post
     */
    @RequestMapping(method = RequestMethod.POST, value = "/posts/bbToHtml")
    public ResponseEntity<String> preview(@RequestParam(POST_BB_CONTENT) String bbContent) {
        JCUser user = securityService.getCurrentUser();
        String post = bbCodeService.convertBbToHtml(bbContent);
        post += user.getRenderedSignature();
        return new ResponseEntity<String>(post, HttpStatus.OK);
    }
}
