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

import com.google.common.io.ByteStreams;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.EscapeTool;
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.service.ReadOnlySecurityService;
import org.jtalks.jcommune.plugin.api.service.transactional.TransactionalPluginBranchService;
import org.jtalks.jcommune.plugin.api.service.transactional.TransactionalPluginLastReadPostService;
import org.jtalks.jcommune.plugin.api.service.transactional.TransactionalTypeAwarePluginTopicService;
import org.jtalks.jcommune.plugin.api.web.PluginController;
import org.jtalks.jcommune.plugin.api.web.dto.Breadcrumb;
import org.jtalks.jcommune.plugin.api.web.dto.BreadcrumbLocation;
import org.jtalks.jcommune.plugin.api.web.dto.TopicDto;
import org.jtalks.jcommune.plugin.api.web.util.BreadcrumbBuilder;
import org.jtalks.jcommune.plugin.api.web.velocity.tool.JodaDateTimeTool;
import org.jtalks.jcommune.plugin.api.web.velocity.tool.PermissionTool;
import org.jtalks.jcommune.plugin.questionsandanswers.QuestionsAndAnswersPlugin;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static org.jtalks.jcommune.plugin.questionsandanswers.QuestionsAndAnswersPlugin.MESSAGE_PATH;

/**
 * @author Mikhail Stryzhonok
 */
@Controller
@RequestMapping(QuestionsAndAnswersPlugin.CONTEXT)
public class QuestionsAndAnswersController implements ApplicationContextAware, PluginController {
    private static final String PATH_TO_IMAGES = "/org/jtalks/jcommune/plugin/questionsandanswers/images/";
    private static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";
    public static final String BRANCH_ID = "branchId";
    private static final String HTTP_HEADER_DATETIME_PATTERN = "E, dd MMM yyyy HH:mm:ss z";

    private BreadcrumbBuilder breadcrumbBuilder = new BreadcrumbBuilder();

    private String apiPath;
    private ApplicationContext applicationContext;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showVelocity(Model model, HttpServletRequest request) {
        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();
        model.addAttribute("content", VelocityEngineUtils.mergeTemplateIntoString(engine,
                "org/jtalks/jcommune/plugin/questionsandanswers/template/question.vm", "UTF-8", getModel(request)));
        return "plugin/plugin";
    }

    @RequestMapping(value = "new", method = RequestMethod.POST)
    public String createQuestion(@Valid @ModelAttribute TopicDto topicDto, BindingResult result, Model model,
                                 @RequestParam(BRANCH_ID) Long branchId, HttpServletRequest request)
            throws NotFoundException{
        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();
        Branch  branch = TransactionalPluginBranchService.getInstance().get(branchId);
        Map<String, Object> data = getDefaultModel(request);
        if (result.hasErrors()) {
            topicDto.getTopic().setBranch(branch);
            data.put("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(topicDto.getTopic()));
            data.put("topicDto", topicDto);
            data.put("result", result);
            model.addAttribute("content", VelocityEngineUtils.mergeTemplateIntoString(engine,
                    "org/jtalks/jcommune/plugin/questionsandanswers/template/questionForm.vm", "UTF-8", data));
            return "plugin/plugin";
        }
        topicDto.getTopic().setBranch(branch);
        topicDto.getTopic().setType("Question");
        Topic createdQuestion = TransactionalTypeAwarePluginTopicService.getInstance().createTopic(topicDto.getTopic(),
                topicDto.getBodyText());
        return "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + createdQuestion.getId();
    }

    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String showNewQuestionPage(@RequestParam(BRANCH_ID) Long branchId, Model model, HttpServletRequest request)
            throws NotFoundException {
        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();
        Branch branch = TransactionalPluginBranchService.getInstance().get(branchId);
        Topic topic = new Topic();
        topic.setBranch(branch);
        TopicDto dto = new TopicDto(topic);
        Map<String, Object> data = getDefaultModel(request);
        data.put("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(topic));
        data.put("topicDto", dto);
        data.put("edit", false);
        model.addAttribute("content", VelocityEngineUtils.mergeTemplateIntoString(engine,
                "org/jtalks/jcommune/plugin/questionsandanswers/template/questionForm.vm", "UTF-8", data));
        return "plugin/plugin";
    }

    @RequestMapping(value = "icon/{name}", method = RequestMethod.GET)
    public void getIcon(HttpServletRequest request, HttpServletResponse response, @PathVariable("name") String name) {
        try {
            processIconRequest(request, response, PATH_TO_IMAGES + name);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String showQuestion(HttpServletRequest request, Model model, @PathVariable("id") Long id)
            throws NotFoundException {
        Topic topic = TransactionalTypeAwarePluginTopicService.getInstance().get(id, "Question");
        Map<String, Object> data = getDefaultModel(request);
        data.put("question", topic);
        data.put("postPage", new PageImpl<>(topic.getPosts()));
        data.put("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(topic));
        data.put("subscribed", false);
        TransactionalPluginLastReadPostService.getInstance().markTopicPageAsRead(topic, 1);
        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();
        model.addAttribute("content", VelocityEngineUtils.mergeTemplateIntoString(engine,
                "org/jtalks/jcommune/plugin/questionsandanswers/template/question.vm", "UTF-8", data));
        return "plugin/plugin";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public String deleteQuestion(@PathVariable("id") Long id) throws NotFoundException {
        Topic topic = TransactionalTypeAwarePluginTopicService.getInstance().get(id, "Question");
        TransactionalTypeAwarePluginTopicService.getInstance().deleteTopic(topic);
        return "redirect:/branches/" + topic.getBranch().getId();
    }

    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    public String editQuestionPage(HttpServletRequest request, Model model, @PathVariable("id") Long id)
            throws NotFoundException{
        Topic topic = TransactionalTypeAwarePluginTopicService.getInstance().get(id, "Question");
        TopicDto topicDto = new TopicDto(topic);
        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();
        Map<String, Object> data = getDefaultModel(request);
        data.put("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(topic));
        data.put("topicDto", topicDto);
        data.put("edit", true);
        model.addAttribute("content", VelocityEngineUtils.mergeTemplateIntoString(engine,
                "org/jtalks/jcommune/plugin/questionsandanswers/template/questionForm.vm", "UTF-8", data));
        return "plugin/plugin";
    }

    @RequestMapping(value = "{id}/edit", method = RequestMethod.POST)
    public String updateQuestion(@Valid @ModelAttribute TopicDto topicDto, BindingResult result, Model model,
                                 @PathVariable("id") Long id, HttpServletRequest request)
            throws NotFoundException {
        Topic topic = TransactionalTypeAwarePluginTopicService.getInstance().get(id, "Question");
        Map<String, Object> data = getDefaultModel(request);
        if (result.hasErrors()) {
            topicDto.getTopic().setId(topic.getId());
            topicDto.getTopic().setBranch(topic.getBranch());
            VelocityEngine engine = new VelocityEngine(getProperties());
            engine.init();
            data.put("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(topic));
            data.put("topicDto", topicDto);
            data.put("edit", true);
            data.put("result", result);
            model.addAttribute("content", VelocityEngineUtils.mergeTemplateIntoString(engine,
                    "org/jtalks/jcommune/plugin/questionsandanswers/template/questionForm.vm", "UTF-8", data));
            return "plugin/plugin";

        }
        topicDto.fillTopic(topic);
        TransactionalTypeAwarePluginTopicService.getInstance().updateTopic(topic);
        return "redirect:" + QuestionsAndAnswersPlugin.CONTEXT + "/" + topic.getId();
    }

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
        String formattedDateExpires = DateFormatUtils.format(
                new Date(System.currentTimeMillis()),
                HTTP_HEADER_DATETIME_PATTERN, Locale.US);
        response.setHeader("Expires", formattedDateExpires);
        Date lastModificationDate = new Date(0);
        response.setHeader("Last-Modified", DateFormatUtils.format(lastModificationDate, HTTP_HEADER_DATETIME_PATTERN,
                Locale.US));
    }

    private ResourceBundle getLocalizedMessagesBundle(JCUser currentUser) {
        return ResourceBundle.getBundle(MESSAGE_PATH, currentUser.getLanguage().getLocale());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

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

    private Map<String, Object> getModel(HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();
        List<Breadcrumb> breadcrumbList = new ArrayList<>();
        Breadcrumb forum = new Breadcrumb(1L, BreadcrumbLocation.FORUM, "Forum");
        breadcrumbList.add(forum);
        Breadcrumb section = new Breadcrumb(1L, BreadcrumbLocation.SECTION, "Sport");
        breadcrumbList.add(section);
        Breadcrumb branch = new Breadcrumb(1L, BreadcrumbLocation.BRANCH, "Russian Hockey");
        breadcrumbList.add(branch);
        List<Post> posts = new ArrayList<>();
        JCUser mrVasiliy = new JCUser("Mr. Vasiliy", "", "");
        PostComment comment = new PostComment();
        comment.setAuthor(mrVasiliy);
        comment.setBody("Lorem ipsum <strong>dolor</strong> sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        comment.setCreationDate(new DateTime());
        Post p1 = new Post(mrVasiliy, "Lorem ipsum <strong>dolor</strong> sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        p1.getComments().add(comment);
        p1.getComments().add(comment);
        p1.getComments().add(comment);
        p1.getComments().add(comment);
        p1.getComments().add(comment);
        p1.getComments().add(comment);
        posts.add(p1);
        JCUser mrKakashka = new JCUser("Mr. Kakashka", "", "");
        Post p2 = new Post(mrKakashka, "Lorem ipsum <strong>dolor</strong> sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        p2.getComments().add(comment);
        p2.getComments().add(comment);
        p2.getComments().add(comment);
        p2.getComments().add(comment);
        posts.add(p2);
        JCUser mrTolik = new JCUser("Mr. Tolik", "", "");
        Post p3 = new Post(mrTolik, "Lorem ipsum <strong>dolor</strong> sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        p3.getComments().add(comment);
        p3.getComments().add(comment);
        posts.add(p3);
        Page<Post> postPage = new PageImpl<>(posts);
        data.putAll(getDefaultModel(request));
        data.put("postPage", postPage);
        data.put("question", new Topic(mrVasiliy, "Test title"));
        data.put("subscribed", false);
        data.put("breadcrumbList", breadcrumbList);
        return data;
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
        JCUser currentUser = ReadOnlySecurityService.getInstance().getCurrentUser();
        PermissionTool tool = new PermissionTool(applicationContext);
        model.put("currentUser", currentUser);
        model.put("messages", getLocalizedMessagesBundle(currentUser));
        model.put("permissionTool", tool);
        return model;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }
}
