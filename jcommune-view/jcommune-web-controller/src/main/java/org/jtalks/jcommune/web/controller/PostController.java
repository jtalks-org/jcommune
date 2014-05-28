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
import org.jtalks.jcommune.service.*;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.jtalks.jcommune.web.dto.PostDto;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;

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
 * @author Andrey Ivanov
 */
@Controller
public class PostController {

    public static final String TOPIC_ID = "topicId";
    public static final String POST_ID = "postId";
    public static final String POST_DTO = "postDto";
    public static final String TOPIC_TITLE = "topicTitle";
    public static final String BREADCRUMB_LIST = "breadcrumbList";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private PostService postService;
    private LastReadPostService lastReadPostService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private TopicFetchService topicFetchService;
    private TopicModificationService topicModificationService;
    private BBCodeService bbCodeService;
    private UserService userService;
    private LocationService locationService;
    private SessionRegistry sessionRegistry;


    @Autowired(required = true)
    private ViewResolver viewResolver;

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
     * @param postService              {@link PostService} instance to be injected
     * @param breadcrumbBuilder        the object which provides actions on {@link BreadcrumbBuilder} entity
     * @param topicFetchService        to retrieve topics from a database
     * @param topicModificationService to update topics with new posts
     * @param bbCodeService            to create valid quotes
     * @param lastReadPostService      not to track user posts as updates for himself
     * @param userService              to get the current user information
     */
    @Autowired
    public PostController(PostService postService, BreadcrumbBuilder breadcrumbBuilder,
                          TopicFetchService topicFetchService, TopicModificationService topicModificationService,
                          BBCodeService bbCodeService, LastReadPostService lastReadPostService,
                          UserService userService, LocationService locationService, SessionRegistry sessionRegistry) {
        this.postService = postService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.topicFetchService = topicFetchService;
        this.topicModificationService = topicModificationService;
        this.bbCodeService = bbCodeService;
        this.lastReadPostService = lastReadPostService;
        this.userService = userService;
        this.locationService = locationService;
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * Delete post by given id
     * 
     * @param postId post
     * @return redirect to post next to deleted one. Redirects to previous post in case if it's last post in topic.
     * @throws NotFoundException when post was not found
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/posts/{postId}")
    public ModelAndView delete(@PathVariable(POST_ID) Long postId)
            throws NotFoundException {
        Post post = this.postService.get(postId);
        Post nextPost = post.getTopic().getNeighborPost(post);
        deletePostWithLockHandling(postId);
        return new ModelAndView("redirect:/posts/" + nextPost.getId());
    }

    private Topic deletePostWithLockHandling(Long postId) throws NotFoundException {
        for (int i = 0; i < UserController.LOGIN_TRIES_AFTER_LOCK; i++) {
            try {
                Post post = postService.get(postId);
				postService.deletePost(post);
                return post.getTopic();
            } catch (HibernateOptimisticLockingFailureException e) {
            }
        }
        try {
            Post post = postService.get(postId);
            postService.deletePost(post);
            return post.getTopic();
        } catch (HibernateOptimisticLockingFailureException e) {
            LOGGER.error("User has been optimistically locked and can't be reread {} times. Username: {}",
                    UserController.LOGIN_TRIES_AFTER_LOCK, userService.getCurrentUser().getUsername());
            throw e;
        }
    }

    /**
     * Edit post page filled with data from post with given id
     *
     * @param postId post id
     * @return redirect to post form page
     * @throws NotFoundException when topic or post not found
     */
    @RequestMapping(value = "/posts/{postId}/edit", method = RequestMethod.GET)
    public ModelAndView editPage(@PathVariable(POST_ID) Long postId) throws NotFoundException {
        Post post = postService.get(postId);
        return new ModelAndView("topic/editPost")
                .addObject(POST_DTO, PostDto.getDtoFor(post))
                .addObject(TOPIC_ID, post.getTopic().getId())
                .addObject(POST_ID, postId)
                .addObject(TOPIC_TITLE, post.getTopic().getTitle())
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(post.getTopic()));
    }

    /**
     * Update existing post
     *
     * @param postDto Dto populated in form
     * @param result  validation result
     * @param postId  the current postId
     * @return {@code ModelAndView} object which will be redirect to topic page
     *         if saved successfully or show form with error message
     * @throws NotFoundException when topic, branch or post not found
     */
    @RequestMapping(value = "/posts/{postId}/edit", method = RequestMethod.POST)
    public ModelAndView update(@Valid @ModelAttribute PostDto postDto, BindingResult result,
                               @PathVariable(POST_ID) Long postId) throws NotFoundException {
        Post post = postService.get(postId);
        if (result.hasErrors()) {
            return new ModelAndView("topic/editPost")
                    .addObject(TOPIC_ID, post.getTopic().getId())
                    .addObject(POST_ID, postId);
        }
        postService.updatePost(post, postDto.getBodyText());
        return new ModelAndView("redirect:/posts/" + postId);
    }

    /**
     * Get quote text.
     * If user select nothing JS will substitute whole post contents here
     * <p/>
     * Supports post method to pass large quotations.
     *
     * @param postId    identifier os the post we're quoting
     * @param selection text selected by user for the quotation
     * @throws NotFoundException when topic was not found
     */
    @RequestMapping(method = RequestMethod.POST, value = "/posts/{postId}/quote")
    @ResponseBody
    public JsonResponse getQuote(@PathVariable(POST_ID) Long postId,
                                 @RequestParam("selection") String selection) throws NotFoundException {
        Post source = postService.get(postId);
        String content = StringUtils.defaultString(selection, source.getPostContent());
        return new JsonResponse(JsonResponseStatus.SUCCESS, bbCodeService.quote(content, source.getUserCreated()));
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
    @RequestMapping(method = RequestMethod.POST, value = "/topics/{topicId}")
    public ModelAndView create(@RequestParam(value = "page", defaultValue = "1", required = false) String page,
                               @PathVariable(TOPIC_ID) Long topicId,
                               @Valid @ModelAttribute PostDto postDto,
                               BindingResult result) throws NotFoundException {
        postDto.setTopicId(topicId);
        if (result.hasErrors()) {
            JCUser currentUser = userService.getCurrentUser();
            Topic topic = topicFetchService.get(topicId);
            postDto.setTopicId(topicId);
            Page<Post> postsPage = postService.getPosts(topic, page);

            return new ModelAndView("topic/postList")
                    .addObject("viewList", locationService.getUsersViewing(topic))
                    .addObject("usersOnline", sessionRegistry.getAllPrincipals())
                    .addObject("postsPage", postsPage)
                    .addObject("topic", topic)
                    .addObject(POST_DTO, postDto)
                    .addObject("subscribed", topic.getSubscribers().contains(currentUser))
                    .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(topic));
        }

        Post newbie = replyToTopicWithLockHandling(postDto, topicId);
        lastReadPostService.markTopicAsRead(newbie.getTopic());
        return new ModelAndView(this.redirectToPageWithPost(newbie.getId()));
    }

    private Post replyToTopicWithLockHandling(PostDto postDto, Long topicId) throws NotFoundException {
        Topic topic = topicFetchService.get(topicId);
        long branchId = topic.getBranch().getId();
        for (int i = 0; i < UserController.LOGIN_TRIES_AFTER_LOCK; i++) {
            try {
                return topicModificationService.replyToTopic(
                        postDto.getTopicId(), postDto.getBodyText(), branchId);
            } catch (HibernateOptimisticLockingFailureException e) {
            }
        }
        try {
            return topicModificationService.replyToTopic(
                    postDto.getTopicId(), postDto.getBodyText(), branchId);
        } catch (HibernateOptimisticLockingFailureException e) {
            LOGGER.error("User has been optimistically locked and can't be reread {} times. Username: {}",
                    UserController.LOGIN_TRIES_AFTER_LOCK, userService.getCurrentUser().getUsername());
            throw e;
        }
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
     * Converts post with bb codes to HTML for client-side
     * preview in bbEditor
     *
     * @param postDto  Current post dto
     * @param result Spring MVC binding result
     * @return HTML content for post
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST, value = "/posts/bbToHtml")
    public ModelAndView preview(@Valid @ModelAttribute PostDto postDto, BindingResult result) throws Exception {
        return getPreviewModelAndView(result).addObject("content", postDto.getBodyText());
    }

    /**
     * Converts topic with bb codes to HTML for client-side
     * preview in bbEditor
     *
     * @param topicDto Current topic dto
     * @param result Spring MVC binding result
     * @return HTML content for topic
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST, value = "/topics/bbToHtml")
    public ModelAndView preview(@Valid @ModelAttribute TopicDto topicDto, BindingResult result) throws Exception {
        return getPreviewModelAndView(result).addObject("content", topicDto.getBodyText());
    }

    /**
     * Prepare ModelAndView for showing preview
     *
     * @return prepared ModelAndView for preview
     */
    private ModelAndView getPreviewModelAndView(BindingResult result) {
        String signature = userService.getCurrentUser().getSignature();
        return new ModelAndView("ajax/postPreview").addObject("signature", signature)
                .addObject("isInvalid", result.hasFieldErrors("bodyText"))
                .addObject("errors", result.getFieldErrors("bodyText"));
    }
}
