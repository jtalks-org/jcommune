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
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.*;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.dto.EntityToDtoConverter;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.jtalks.jcommune.plugin.api.web.dto.PostDto;
import org.jtalks.jcommune.plugin.api.web.dto.PostDraftDto;
import org.jtalks.jcommune.plugin.api.web.dto.TopicDto;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.plugin.api.web.util.BreadcrumbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    private PostService postService;
    private LastReadPostService lastReadPostService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private TopicFetchService topicFetchService;
    private TopicModificationService topicModificationService;
    private BBCodeService bbCodeService;
    private UserService userService;
    private LocationService locationService;
    private EntityToDtoConverter converter;

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
     * @param converter                instance of {@link EntityToDtoConverter} needed to
     *                                 obtain link to the topic
     */
    @Autowired
    public PostController(PostService postService, BreadcrumbBuilder breadcrumbBuilder,
                          TopicFetchService topicFetchService, TopicModificationService topicModificationService,
                          BBCodeService bbCodeService, LastReadPostService lastReadPostService,
                          UserService userService, LocationService locationService, EntityToDtoConverter converter) {
        this.postService = postService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.topicFetchService = topicFetchService;
        this.topicModificationService = topicModificationService;
        this.bbCodeService = bbCodeService;
        this.lastReadPostService = lastReadPostService;
        this.userService = userService;
        this.locationService = locationService;
        this.converter = converter;
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
    @RequestMapping(method = RequestMethod.POST, value = "/topics/{topicId}") //
    public ModelAndView create(@RequestParam(value = "page", defaultValue = "1", required = false) String page,
                               @PathVariable(TOPIC_ID) Long topicId,
                               @Valid @ModelAttribute PostDto postDto,
                               BindingResult result, RedirectAttributes attr) throws NotFoundException {
        postDto.setTopicId(topicId);
        if (result.hasErrors()) {
            attr.addFlashAttribute("postDto", postDto);
            return new ModelAndView("redirect:/topics/error/" + topicId + "?page=" + page);
        }

        Post newbie = replyToTopicWithLockHandling(postDto, topicId);
        lastReadPostService.markTopicAsRead(newbie.getTopic());
        return new ModelAndView(this.redirectToPageWithPost(newbie.getId()));
    }

    /**
     * Gets validation errors from 'create' methods to redirect them to the view. We need it
     * to implement POST/redirect/GET pattern, which leads to preventing of repeating POST request
     * on browser refresh.
     *
     * @param page  page of the current post
     * @param topicId ID of a topic
     * @param postDto Dto with failed validation
     * @param result  validation result
     *
     * @return {@code ModelAndView} object which shows form with an error message
     * @throws NotFoundException when topic, branch or post not found
     */
    @RequestMapping(method = RequestMethod.GET, value = "/topics/error/{topicId}")
    public ModelAndView errorRedirect(@RequestParam(value = "page", required = false) String page,
                                      @PathVariable(TOPIC_ID) Long topicId, @ModelAttribute @Valid PostDto postDto,
                                      BindingResult result) throws NotFoundException {
        JCUser currentUser = userService.getCurrentUser();
        Topic topic = topicFetchService.get(topicId);

        PostDraft draft = topic.getDraftForUser(currentUser);
        if (draft != null) {
            postDto = PostDto.getDtoFor(draft);
        }
        postDto.setTopicId(topicId);
        Page<Post> postsPage = postService.getPosts(topic, page);

        return new ModelAndView("topic/postList")
                .addObject("viewList", locationService.getUsersViewing(topic))
                .addObject("postsPage", postsPage)
                .addObject("topic", topic)
                .addObject(POST_DTO, postDto)
                .addObject("subscribed", topic.getSubscribers().contains(currentUser))
                .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(topic));
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
     * If post belongs to plugable topic and  appropriated plugin is enabled redirects
     * to plugable topic view.
     *
     * @param postId unique post identifier
     * @return redirect view to the certain topic page
     * @throws NotFoundException is the is no post for the identifier given
     */
    @RequestMapping(method = RequestMethod.GET, value = "/posts/{postId}")
    public String redirectToPageWithPost(@PathVariable Long postId) throws NotFoundException {
        Post post = postService.get(postId);
        int page = postService.calculatePageForPost(post);
        String topicUrl = converter.convertTopicToDto(post.getTopic()).getTopicUrl();
        return "redirect:" + topicUrl + "?page=" + page + "#" + postId;
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
     * Votes up for post with specified id
     *
     * @param postId id of a post to vote up
     * @param request HttpServletRequest
     *
     * @return response in JSON format
     *
     * @throws NotFoundException if post with specified id not found
     */
    @RequestMapping(method = RequestMethod.GET, value = "/posts/{postId}/voteup")
    @ResponseBody
    public JsonResponse voteUp(@PathVariable Long postId, HttpServletRequest request) throws NotFoundException {
        PostVote vote = new PostVote(true);
        voteWithSessionLocking(postId, vote, request);
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }

    /**
     * Votes down for post with specified id
     *
     * @param postId id of a post to vote down
     * @param request HttpServletRequest
     *
     * @return response in JSON format
     *
     * @throws NotFoundException if post with specified id not found
     */
    @RequestMapping(method = RequestMethod.GET, value = "/posts/{postId}/votedown")
    @ResponseBody
    public JsonResponse voteDown(@PathVariable Long postId, HttpServletRequest request) throws NotFoundException {
        PostVote vote = new PostVote(false);
        voteWithSessionLocking(postId, vote, request);
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }

    /**
     * Saves new draft or update if it already exist
     *
     * @param postDraftDto post draft dto populated in form
     * @param result validation result
     *
     * @return response in JSON format
     *
     * @throws NotFoundException if topic to store draft not exist
     */
    @RequestMapping(value = "/posts/savedraft", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse saveDraft(@Valid @RequestBody PostDraftDto postDraftDto, BindingResult result) throws NotFoundException {
        if (result.hasErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL);
        }
        Topic topic = topicFetchService.getTopicSilently(postDraftDto.getTopicId());
        PostDraft saved = postService.saveOrUpdateDraft(topic, postDraftDto.getBodyText());
        return new JsonResponse(JsonResponseStatus.SUCCESS, saved.getId());
    }

    /**
     * Deletes draft
     *
     * @param draftId id of draft to delete
     *
     * @return response in JSON format
     *
     * @throws NotFoundException if post with specified id not exist
     */
    @RequestMapping(value = "drafts/{draftId}/delete", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse deleteDraft(@PathVariable Long draftId) throws NotFoundException {
        postService.deleteDraft(draftId);
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }

    /**
     * Prepare ModelAndView for showing preview
     *
     * @return prepared ModelAndView for preview
     */
    private ModelAndView getPreviewModelAndView(BindingResult result) {
        return new ModelAndView("ajax/postPreview")
                .addObject("isInvalid", result.hasFieldErrors("bodyText"))
                .addObject("errors", result.getFieldErrors("bodyText"));
    }

    /**
     * Performs vote with session locking to prevent handling of concurrent requests from same user
     *
     * @param postId id of a post to vote
     * @param vote {@link PostVote} object
     * @param request HttpServletRequest
     *
     * @throws NotFoundException if post with specified id not found
     */
    private void voteWithSessionLocking(Long postId, PostVote vote, HttpServletRequest request) throws NotFoundException {
        /**
         * We should not create session here to prevent possibility of creating multiplier sessions for same user in
         * concurrent requests
         */
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object mutex = WebUtils.getSessionMutex(session);
            /**
             *  Next operations performed in synchronized block to prevent handling of concurrent requests from same
             *  user. We use session mutex as the lock object. In many cases, the HttpSession reference itself is a safe
             *  mutex as well, since it will always be the same object reference for the same active logical session.
             *  However, this is not guaranteed across different servlet containers; the only 100% safe way is a session
             *  mutex.
            */
            synchronized (mutex) {
                Post post = postService.get(postId);
                postService.vote(post, vote);
            }
        } else {
            /**
             * If <code>HttpSession</code> is <code>null</code> we have no mutex object, so we perform operations
             * without synchronization
             */
            Post post = postService.get(postId);
            postService.vote(post, vote);
        }
    }
}
