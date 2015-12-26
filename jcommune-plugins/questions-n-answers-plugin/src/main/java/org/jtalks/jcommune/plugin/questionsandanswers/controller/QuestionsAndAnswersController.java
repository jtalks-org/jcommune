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
package org.jtalks.jcommune.plugin.questionsandanswers.controller;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.tools.generic.EscapeTool;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.service.*;
import org.jtalks.jcommune.plugin.api.service.nontransactional.BbToHtmlConverter;
import org.jtalks.jcommune.plugin.api.service.nontransactional.PluginLocationServiceImpl;
import org.jtalks.jcommune.plugin.api.service.nontransactional.PropertiesHolder;
import org.jtalks.jcommune.plugin.api.service.transactional.*;
import org.jtalks.jcommune.plugin.api.web.PluginController;
import org.jtalks.jcommune.plugin.api.web.dto.PostDto;
import org.jtalks.jcommune.plugin.api.web.dto.TopicDto;
import org.jtalks.jcommune.plugin.api.web.dto.json.*;
import org.jtalks.jcommune.plugin.api.web.locale.JcLocaleResolver;
import org.jtalks.jcommune.plugin.api.web.util.BreadcrumbBuilder;
import org.jtalks.jcommune.plugin.api.web.velocity.tool.JodaDateTimeTool;
import org.jtalks.jcommune.plugin.api.web.velocity.tool.PermissionTool;
import org.jtalks.jcommune.plugin.questionsandanswers.QuestionsAndAnswersPlugin;
import org.jtalks.jcommune.plugin.questionsandanswers.dto.CommentDto;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static org.jtalks.jcommune.plugin.questionsandanswers.QuestionsAndAnswersPlugin.MESSAGE_PATH;

/**
 * Class for processing question management web requests
 *
 * @author Mikhail Stryzhonok
 */
@Controller
@RequestMapping(QuestionsAndAnswersPlugin.CONTEXT)
public class QuestionsAndAnswersController implements ApplicationContextAware, PluginController {

    private static final String PATH_TO_IMAGES = "/org/jtalks/jcommune/plugin/questionsandanswers/images/";
    private static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";
    private static final String HTTP_HEADER_DATETIME_PATTERN = "E, dd MMM yyyy HH:mm:ss z";
    private static final String TEMPLATE_PATH = "org/jtalks/jcommune/plugin/questionsandanswers/template/";
    private static final String QUESTION_FORM_TEMPLATE_PATH = TEMPLATE_PATH + "questionForm.vm";
    private static final String ANSWER_FORM_TEMPLATE_PATH = TEMPLATE_PATH + "answerForm.vm";
    private static final String QUESTION_TEMPLATE_PATH = TEMPLATE_PATH + "question.vm";
    private static final String BREADCRUMB_LIST = "breadcrumbList";
    private static final String TOPIC_DTO = "topicDto";
    private static final String TOPIC_DRAFT = "topicDraft";
    private static final String POST_DTO = "postDto";
    private static final String EDIT_MODE = "edit";
    private static final String RESULT = "result";
    private static final String QUESTION = "question";
    private static final String POST_PAGE = "postPage";
    private static final String SUBSCRIBED = "subscribed";
    private static final String CONVERTER = "converter";
    private static final String VIEW_LIST = "viewList";
    private static final String LIMIT_OF_POSTS_ATTRIBUTE = "postLimit";
    public static final int LIMIT_OF_POSTS_VALUE = 50;
    public static final String PLUGIN_VIEW_NAME = "plugin/plugin";
    public static final String BRANCH_ID = "branchId";
    public static final String CONTENT = "content";
    private static final String QEUSTION_TITLE = "questionTitle";
    public static final String POST_ID = "postId";
    public static final String COMMENT_ID = "commentId";


    private BreadcrumbBuilder breadcrumbBuilder = new BreadcrumbBuilder();

    private String apiPath;
    private ApplicationContext applicationContext;

    /**
     * Shows question creation page
     *
     * @param branchId id of the branch in which user want to create question
     * @param model model for transferring to jsp
     * @param request HttpServletRequest
     *
     * @return plugin view name
     * @throws NotFoundException if branch with specified id not found
     */
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String showNewQuestionPage(@RequestParam(BRANCH_ID) Long branchId, Model model, HttpServletRequest request)
            throws NotFoundException {
        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();

        TopicDraft draft = ObjectUtils.defaultIfNull(
                getPluginTopicDraftService().getDraft(), new TopicDraft());

        TopicDto dto = new TopicDto(draft);
        dto.getTopic().setType(QuestionsAndAnswersPlugin.TOPIC_TYPE);

        Branch branch = getPluginBranchService().get(branchId);
        dto.getTopic().setBranch(branch);

        Map<String, Object> data = getDefaultModel(request);
        data.put(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(dto.getTopic()));
        data.put(TOPIC_DTO, dto);
        data.put(TOPIC_DRAFT, draft);
        data.put(EDIT_MODE, false);
        model.addAttribute(CONTENT, getMergedTemplate(engine, QUESTION_FORM_TEMPLATE_PATH, "UTF-8", data));
        return PLUGIN_VIEW_NAME;
    }

    /**
     * Saves question after form submission.
     *
     * @param topicDto Dto populated in form
     * @param result validation result
     * @param model model for transferring to jsp
     * @param branchId branch, where topic will be created
     * @param request HttpServletRequest
     *
     * @return redirect to newly created topic if no validation errors
     *         plugin view name if validation errors occurred
     * @throws NotFoundException if branch with specified id not found
     */
    @RequestMapping(value = "new", method = RequestMethod.POST)
    public String createQuestion(@Valid @ModelAttribute TopicDto topicDto, BindingResult result, Model model,
                                 @RequestParam(BRANCH_ID) Long branchId, HttpServletRequest request)
            throws NotFoundException{
        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();
        Branch  branch = getPluginBranchService().get(branchId);
        Map<String, Object> data = getDefaultModel(request);
        topicDto.getTopic().setBranch(branch);
        topicDto.getTopic().setType(QuestionsAndAnswersPlugin.TOPIC_TYPE);
        if (result.hasErrors()) {
            data.put(BREADCRUMB_LIST, breadcrumbBuilder.getNewTopicBreadcrumb(branch));
            data.put(TOPIC_DTO, topicDto);
            data.put(RESULT, result);
            model.addAttribute(CONTENT, getMergedTemplate(engine, QUESTION_FORM_TEMPLATE_PATH, "UTF-8", data));
            return PLUGIN_VIEW_NAME;
        }
        Topic createdQuestion = getTypeAwarePluginTopicService().createTopic(topicDto.getTopic(),
                topicDto.getBodyText());
        return "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + createdQuestion.getId();
    }

    /**
     * Writes question icon into response
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param name name of icon
     */
    @RequestMapping(value = "/resources/icon/{name}", method = RequestMethod.GET)
    public void getIcon(HttpServletRequest request, HttpServletResponse response, @PathVariable("name") String name) {
        try {
            processIconRequest(request, response, PATH_TO_IMAGES + name);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Shows question page
     *
     * @param request HttpServletRequest
     * @param model model for transferring to jsp
     * @param id id of question
     *
     * @return plugin view name
     * @throws NotFoundException if question with specified id not found
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String showQuestion(HttpServletRequest request, Model model, @PathVariable("id") Long id)
            throws NotFoundException {
        Topic topic = getTypeAwarePluginTopicService().get(id, QuestionsAndAnswersPlugin.TOPIC_TYPE);
        getTypeAwarePluginTopicService().checkViewTopicPermission(topic.getBranch().getId());

        JCUser currentUser = getUserReader().getCurrentUser();
        PostDto postDto = new PostDto();
        PostDraft draft = topic.getDraftForUser(currentUser);
        if (draft != null) {
            postDto = PostDto.getDtoFor(draft);
        }

        Map<String, Object> data = getDefaultModel(request);
        data.put(QUESTION, topic);
        data.put(POST_PAGE, new PageImpl<>(getSortedPosts(topic.getPosts())));
        data.put(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(topic));
        data.put(SUBSCRIBED, false);
        data.put(CONVERTER, BbToHtmlConverter.getInstance());
        data.put(VIEW_LIST, getLocationService().getUsersViewing(topic));
        data.put(POST_DTO, postDto);
        data.put(LIMIT_OF_POSTS_ATTRIBUTE, LIMIT_OF_POSTS_VALUE);
        getPluginLastReadPostService().markTopicAsRead(topic);
        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();
        model.addAttribute(CONTENT, getMergedTemplate(engine, QUESTION_TEMPLATE_PATH, "UTF-8", data));
        return PLUGIN_VIEW_NAME;
    }

    /**
     * Check if user can answer for question with given id. Yer can answer if question has less than
     * {@link #LIMIT_OF_POSTS_VALUE} answers
     *
     * @param questionId id of question to be checked
     *
     * @return Success response in JSON form if user can leave answer and fail response in JSON form otherwise
     * @throws NotFoundException if question with given id not found
     */
    @RequestMapping(value = "{id}/canpost", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse canPost(@PathVariable("id") Long questionId) throws NotFoundException {
        Topic topic = getTypeAwarePluginTopicService().get(questionId, QuestionsAndAnswersPlugin.TOPIC_TYPE);
        if (topic.getPostCount() - 1 >= LIMIT_OF_POSTS_VALUE) {
            return new JsonResponse(JsonResponseStatus.FAIL);
        }
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }

    /**
     * Shows answer
     *
     * @param questionId id of question
     * @param answerId id of the answer
     *
     * @return redirect to the answer url
     */
    @RequestMapping(value = "{questionId}/post/{answerId}", method = RequestMethod.GET)
    public String showAnswer(@PathVariable Long questionId, @PathVariable Long answerId) {
        return "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + questionId + "#" + answerId;
    }

    /**
     * Show edit question page
     *
     * @param request HttpServletRequest
     * @param model model for transferring to jsp
     * @param id id of question to edit
     *
     * @return plugin view name
     * @throws NotFoundException if question with specified id not found
     */
    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    public String editQuestionPage(HttpServletRequest request, Model model, @PathVariable("id") Long id)
            throws NotFoundException{
        Topic topic = getTypeAwarePluginTopicService().get(id, QuestionsAndAnswersPlugin.TOPIC_TYPE);
        TopicDto topicDto = new TopicDto(topic);
        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();
        Map<String, Object> data = getDefaultModel(request);
        data.put(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(topic));
        data.put(TOPIC_DTO, topicDto);
        data.put(EDIT_MODE, true);
        model.addAttribute(CONTENT, getMergedTemplate(engine, QUESTION_FORM_TEMPLATE_PATH, "UTF-8", data));
        return PLUGIN_VIEW_NAME;
    }

    /**
     * Updates question
     *
     * @param topicDto Dto populated in form
     * @param result validation result
     * @param model model for transferring to jsp
     * @param id id of question to edit
     * @param request HttpServletRequest
     *
     * @return redirect to newly created topic if no validation errors
     *         plugin view name if validation errors occurred
     * @throws NotFoundException if question with specified id not found
     */
    @RequestMapping(value = "{id}/edit", method = RequestMethod.POST)
    public String updateQuestion(@Valid @ModelAttribute TopicDto topicDto, BindingResult result, Model model,
                                 @PathVariable("id") Long id, HttpServletRequest request)
            throws NotFoundException {
        Topic topic = getTypeAwarePluginTopicService().get(id, QuestionsAndAnswersPlugin.TOPIC_TYPE);
        Map<String, Object> data = getDefaultModel(request);
        if (result.hasErrors()) {
            topicDto.getTopic().setId(topic.getId());
            topicDto.getTopic().setBranch(topic.getBranch());
            VelocityEngine engine = new VelocityEngine(getProperties());
            engine.init();
            data.put(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(topic));
            data.put(TOPIC_DTO, topicDto);
            data.put(EDIT_MODE, true);
            data.put(RESULT, result);
            model.addAttribute(CONTENT, getMergedTemplate(engine, QUESTION_FORM_TEMPLATE_PATH, "UTF-8", data));
            return PLUGIN_VIEW_NAME;

        }
        topicDto.fillTopic(topic);
        getTypeAwarePluginTopicService().updateTopic(topic);
        return "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + topic.getId();
    }

    /**
     * Show edit answer page
     *
     * @param request HttpServletRequest
     * @param model model for transferring to jsp
     * @param id id of answer to edit
     *
     * @return plugin view name
     * @throws NotFoundException if answer with specified id not found
     */
    @RequestMapping(value = "post/{id}/edit", method = RequestMethod.GET)
    public String editAnswerPage(HttpServletRequest request, Model model, @PathVariable("id") Long id)
            throws NotFoundException{
        Post answer = getPluginPostService().get(id);
        PostDto answerDto = PostDto.getDtoFor(answer);
        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();
        Map<String, Object> data = getDefaultModel(request);
        data.put(QEUSTION_TITLE, answer.getTopic().getTitle());
        data.put(POST_DTO, answerDto);
        model.addAttribute(CONTENT, getMergedTemplate(engine, ANSWER_FORM_TEMPLATE_PATH, "UTF-8", data));
        return PLUGIN_VIEW_NAME;
    }

    /**
     * updates answer
     *
     * @param postDto Dto populated in form
     * @param result validation result
     * @param model model for transferring to template
     * @param id id of answer to edit
     * @param request HttpServletRequest
     *
     * @return redirect to updated answer if no validation errors
     *         plugin view name if validation errors occurred
     * @throws NotFoundException if answer with specified id not found
     */
    @RequestMapping(value = "post/{id}/edit", method = RequestMethod.POST)
    public String updateAnswer(@Valid @ModelAttribute PostDto postDto, BindingResult result, Model model,
                                 @PathVariable("id") Long id, HttpServletRequest request) throws NotFoundException {
        Post answer = getPluginPostService().get(id);
        Map<String, Object> data = getDefaultModel(request);
        if (result.hasErrors()) {
            VelocityEngine engine = new VelocityEngine(getProperties());
            engine.init();
            data.put(QEUSTION_TITLE, answer.getTopic().getTitle());
            data.put(POST_DTO, postDto);
            data.put(RESULT, result);
            model.addAttribute(CONTENT, getMergedTemplate(engine, ANSWER_FORM_TEMPLATE_PATH, "UTF-8", data));
            return PLUGIN_VIEW_NAME;

        }
        getPluginPostService().updatePost(answer, postDto.getBodyText());
        return "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + answer.getTopic().getId() + "#" + id;
    }

    /**
     * Process the answer form. Adds new answer to the specified question and redirects to the
     * question view page.
     *
     * @param questionId id of question to which answer will be added
     * @param postDto dto that contains data entered in form
     * @param result  validation result
     * @param model model for transferring to template
     * @param request HttpServletRequest
     *
     * @return redirect to the answer or back to answer page if validation failed
     * @throws NotFoundException when question or branch not found
     */
    @RequestMapping(value = "{questionId}", method = RequestMethod.POST)
    public String create(@PathVariable("questionId") Long questionId, @Valid @ModelAttribute PostDto postDto,
                               BindingResult result, Model model, HttpServletRequest request) throws NotFoundException {
        postDto.setTopicId(questionId);
        Topic topic = getTypeAwarePluginTopicService().get(questionId, QuestionsAndAnswersPlugin.TOPIC_TYPE);
        //We can't provide limitation properly without database-level locking
        if (result.hasErrors() || LIMIT_OF_POSTS_VALUE <= topic.getPostCount() - 1) {
            JCUser currentUser = getUserReader().getCurrentUser();
            PostDraft draft = topic.getDraftForUser(currentUser);
            if (draft != null) {
                // If we create new dto object instead of using already existing
                // we lose error messages linked with it
                postDto.fillFrom(draft);
            }

            Map<String, Object> data = getDefaultModel(request);
            VelocityEngine engine = new VelocityEngine(getProperties());
            engine.init();
            data.put(QUESTION, topic);
            data.put(POST_PAGE, new PageImpl<>(getSortedPosts(topic.getPosts())));
            data.put(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(topic));
            data.put(SUBSCRIBED, false);
            data.put(RESULT, result);
            data.put(CONVERTER, BbToHtmlConverter.getInstance());
            data.put(VIEW_LIST, getLocationService().getUsersViewing(topic));
            data.put(POST_DTO, postDto);
            data.put(LIMIT_OF_POSTS_ATTRIBUTE, LIMIT_OF_POSTS_VALUE);
            model.addAttribute(CONTENT, getMergedTemplate(engine, QUESTION_TEMPLATE_PATH, "UTF-8", data));
            return PLUGIN_VIEW_NAME;
        }

        Post newbie = getTypeAwarePluginTopicService().replyToTopic(questionId,
                postDto.getBodyText(), topic.getBranch().getId());
        getPluginLastReadPostService().markTopicAsRead(newbie.getTopic());
        return "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + questionId + "#" + newbie.getId();
    }

    /**
     * Closes question with specified id.
     *
     * @param id id of question to close
     *
     * @return redirect to closed topic
     * @throws NotFoundException if question with specified id not found
     */
    @RequestMapping(value = "{id}/close", method = RequestMethod.GET)
    public String closeQuestion(@PathVariable("id") Long id) throws NotFoundException {
        Topic topic = getTypeAwarePluginTopicService().get(id, QuestionsAndAnswersPlugin.TOPIC_TYPE);
        getTypeAwarePluginTopicService().closeTopic(topic);
        return "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + topic.getId();
    }

    /**
     * Opens question with specified id.
     *
     * @param id of question to open
     *
     * @return redirect to opened question
     * @throws NotFoundException if question with specified if not found
     */
    @RequestMapping(value = "{id}/open", method = RequestMethod.GET)
    public String openQuestion(@PathVariable("id") Long id) throws NotFoundException {
        Topic topic = getTypeAwarePluginTopicService().get(id, QuestionsAndAnswersPlugin.TOPIC_TYPE);
        getTypeAwarePluginTopicService().openTopic(topic);
        return "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + topic.getId();
    }

    /**
     * Deletes answer by given id
     *
     * @param answerId id of the answer
     * @return redirect to question page
     * @throws NotFoundException when answer was not found
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "post/{answerId}")
    public String deleteAnswer(@PathVariable Long answerId)
            throws NotFoundException {
        Post answer = getPluginPostService().get(answerId);
        Post neighborAnswer = answer.getTopic().getNeighborPost(answer);
        getPluginPostService().deletePost(answer);
        return "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + answer.getTopic().getId()
                + "#" + neighborAnswer.getId();
    }

    /**
     * Adds new comment to post
     *
     * @param dto dto populated in form
     * @param result validation result
     * @param request http servlet request
     *
     * @return result in JSON format
     */
    @RequestMapping(method = RequestMethod.POST, value = "newcomment")
    @ResponseBody
    JsonResponse addComment(@Valid @RequestBody CommentDto dto, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            return new FailValidationJsonResponse(result.getAllErrors());
        }
        PostComment comment;
        try {
            Post targetPost = getPluginPostService().get(dto.getPostId());
            //We can't provide limitation properly without database-level locking
            if (targetPost.getNotRemovedComments().size() >= LIMIT_OF_POSTS_VALUE) {
                return new JsonResponse(JsonResponseStatus.FAIL);
            }
            comment = getPluginPostService().addComment(dto.getPostId(), Collections.EMPTY_MAP, dto.getBody());
        } catch (NotFoundException ex) {
            return new FailJsonResponse(JsonResponseReason.ENTITY_NOT_FOUND);
        }
        JodaDateTimeTool dateTimeTool = new JodaDateTimeTool(request);
        return new JsonResponse(JsonResponseStatus.SUCCESS, new CommentDto(comment, dateTimeTool));
    }

    /**
     * Edits existence comment
     *
     * @param dto dto populated in form
     * @param result validation result
     * @param branchId id of a branch to check permission
     *
     * @return result in JSON format
     */
    @RequestMapping(method = RequestMethod.POST, value = "editcomment")
    @ResponseBody
    JsonResponse editComment(@Valid @RequestBody CommentDto dto, BindingResult result,
                             @RequestParam("branchId") long branchId) {
        if (result.hasErrors()) {
            return new FailValidationJsonResponse(result.getAllErrors());
        }
        PostComment updatedComment;
        try {
            updatedComment = getCommentService().updateComment(dto.getId(), dto.getBody(), branchId);
        } catch (NotFoundException ex) {
            return new FailJsonResponse(JsonResponseReason.ENTITY_NOT_FOUND);
        }
        return new JsonResponse(JsonResponseStatus.SUCCESS, updatedComment.getBody());
    }

    /**
     * Deletes comment
     *
     * @param commentId id of comment to delete
     * @param postId id of a post to which this comment belong
     *
     * @return result in JSON format
     */
    @RequestMapping(method = RequestMethod.GET, value = "deletecomment")
    @ResponseBody
    JsonResponse deleteComment(@RequestParam(COMMENT_ID) Long commentId, @RequestParam(POST_ID) Long postId) {
        Post post;
        PostComment postComment;
        try {
            post = getPluginPostService().get(postId);
            postComment = getCommentService().getComment(commentId);
        } catch (NotFoundException ex) {
            return new FailJsonResponse(JsonResponseReason.ENTITY_NOT_FOUND);
        }
        getCommentService().markCommentAsDeleted(post, postComment);
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }

    /**
     * Gets copy of specified collection of posts sorted by rating and creation date
     *
     * @param posts collection of posts to sort
     *
     * @return collection of posts sorted by rating and creation date
     */
    @VisibleForTesting
    List<Post> getSortedPosts(List<Post> posts) {
        List<Post> result = new ArrayList<>(posts);
        Post question = result.remove(0);
        Collections.sort(result, new PostComparator());
        result.add(0, question);
        return result;
    }

    /**
     * Writes icon to response and set apropriate response headers
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param iconPath path to icon to be writed
     *
     * @throws IOException if icon not found
     */
    private void processIconRequest(HttpServletRequest request, HttpServletResponse response, String iconPath)
            throws IOException {
        if(request.getHeader(IF_MODIFIED_SINCE_HEADER) != null) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }
        byte[] icon = ByteStreams.toByteArray(getClass().getResourceAsStream(iconPath));
        response.setContentType("image/png");
        response.setContentLength(icon.length);
        response.getOutputStream().write(icon);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "public");
        response.addHeader("Cache-Control", "must-revalidate");
        response.addHeader("Cache-Control", "max-age=0");
        String formattedDateExpires = DateFormatUtils.format(new Date(), HTTP_HEADER_DATETIME_PATTERN, Locale.US);
        response.setHeader("Expires", formattedDateExpires);
        Date lastModificationDate = new Date(0);
        response.setHeader("Last-Modified", DateFormatUtils.format(lastModificationDate, HTTP_HEADER_DATETIME_PATTERN,
                Locale.US));
    }

    /**
     * Gets resource bundle with locale of current user
     *
     * @param request http servlet request
     *
     * @return resource bundle with locale of current user
     */
    private ResourceBundle getLocalizedMessagesBundle(HttpServletRequest request) {
        return ResourceBundle.getBundle(MESSAGE_PATH, getLocaleResolver().resolveLocale(request));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Gets properties of velocity engine
     *
     * @return properties of velocity engine
     */
    protected Properties getProperties() {
        Properties properties = new Properties();
        properties.put("resource.loader", "jar");
        properties.put("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
        List<String> jars = new ArrayList<>();
        jars.add("jar:file:" + this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        jars.add("jar:file:" + apiPath);
        properties.put("jar.resource.loader.path", jars);
        properties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        return properties;
    }

    /**
     * Create map with default objects needed in velocity template (e.g. request, current user, messages bundle etc.)
     *
     * @param request HttpServletRequest
     *
     * @return Map which will be passed into velocity template
     */
    private Map<String, Object> getDefaultModel(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        model.put("request", request);
        model.put("dateTool", new JodaDateTimeTool(request));
        model.put("esc", new EscapeTool());
        JCUser currentUser = getUserReader().getCurrentUser();
        PermissionTool tool = new PermissionTool(applicationContext);
        model.put("currentUser", currentUser);
        model.put("messages", getLocalizedMessagesBundle(request));
        model.put("permissionTool", tool);
        model.put("propertiesHolder", PropertiesHolder.getInstance());
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    /**
     * Needed for mocking
     *
     * @return locale resolver used in plugin
     */
    LocaleResolver getLocaleResolver() {
        return  JcLocaleResolver.getInstance();
    }

    /**
     * Needed for mocking
     *
     * @return service for manipulating with posts
     */
    PluginPostService getPluginPostService() {
        return TransactionalPluginPostService.getInstance();
    }

    /**
     * Needed for mocking
     *
     * @return service for manipulating draft topics
     */
    PluginTopicDraftService getPluginTopicDraftService() {
        return TransactionalPluginTopicDraftService.getInstance();
    }

    /**
     * Needed for mocking
     *
     * @return service for manipulating with branches
     */
    PluginBranchService getPluginBranchService() {
        return TransactionalPluginBranchService.getInstance();
    }

    /**
     * Needed for mocking
     *
     * @return service for marking topic as read
     */
    PluginLastReadPostService getPluginLastReadPostService() {
        return TransactionalPluginLastReadPostService.getInstance();
    }

    /**
     * Needed for mocking
     *
     * @return service for manipulating with topics
     */
    TypeAwarePluginTopicService getTypeAwarePluginTopicService() {
        return TransactionalTypeAwarePluginTopicService.getInstance();
    }

    /**
     * Needed for mocking
     *
     * @return service for fetching information about users
     */
    UserReader getUserReader() {
        return ReadOnlySecurityService.getInstance();
    }

    /**
     *
     * Merge the specified Velocity template with the given model into a String.
     * Needed for mocking.
     *
     * @param velocityEngine VelocityEngine to work with
     * @param templateLocation the location of template, relative to Velocity's resource loader path
     * @param encoding the encoding of the template file
     * @param model the Map that contains model names as keys and model objects as values
     *
     * @return the result as String
     */
    String getMergedTemplate(VelocityEngine velocityEngine, String templateLocation, String encoding,
                             Map<String, Object> model) throws VelocityException {
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, encoding, model);
    }

    /**
     * Needed for mocking
     *
     * @return service for manipulating with user location
     */
    PluginLocationService getLocationService() {
        return PluginLocationServiceImpl.getInstance();
    }

    /**
     * Needed for mocking
     *
     * @return service for manipulating with comments
     */
    PluginCommentService getCommentService() {
        return TransactionalPluginCommentService.getInstance();
    }


    /**
     * Sets specified {@link BreadcrumbBuilder}
     * Needed for tests
     *
     * @param breadcrumbBuilder {@link BreadcrumbBuilder} to set
     */
    void setBreadcrumbBuilder(BreadcrumbBuilder breadcrumbBuilder) {
        this.breadcrumbBuilder = breadcrumbBuilder;
    }

    private static class PostComparator implements Comparator<Post> {
        @Override
        public int compare(Post o1, Post o2) {
            return o1.getRating() == o2.getRating() ? o2.getCreationDate().compareTo(o1.getCreationDate()) :
                    o2.getRating() - o1.getRating();
        }
    }
}
