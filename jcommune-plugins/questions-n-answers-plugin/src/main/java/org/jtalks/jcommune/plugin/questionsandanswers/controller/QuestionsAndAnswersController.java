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

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.EscapeTool;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.service.ReadOnlySecurityService;
import org.jtalks.jcommune.plugin.api.web.velocity.tool.JodaDateTimeTool;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.jtalks.jcommune.plugin.questionsandanswers.QuestionsAndAnswersPlugin.MESSAGE_PATH;

/**
 * @author Mikhail Stryzhonok
 */
@Controller
public class QuestionsAndAnswersController implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/question/new", method = RequestMethod.GET)
    public View show(Model model, HttpServletRequest request) {
        List<Post> posts = new ArrayList<>();
        JCUser user = new JCUser("user", "", "");
        posts.add(new Post(user, "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        Page<Post> postPage = new PageImpl<>(posts);

        model.addAttribute("content", "Coming soon");
        model.addAttribute("viewList", new ArrayList<JCUser>());
        model.addAttribute("usersOnline", new ArrayList<>());
        model.addAttribute("postsPage", postPage);
        model.addAttribute("question", new Topic(user, "Test"));
        model.addAttribute("postDto", null);
        model.addAttribute("subscribed", false);
        //model.addAttribute("breadcrumbList", );
        //JstlView view = new JstlView("/org/jtalks/jcommune/plugin/questionsandanswers/views/questionForm.jsp");
        JstlView view = new JstlView("/WEB-INF/jsp/plugin/question.jsp");
        view.setPreventDispatchLoop(true);
        String contentType = request.getContentType();
        view.setContentType(contentType);
        view.setApplicationContext(applicationContext);
        return view;
    }

    @RequestMapping(value = "/question", method = RequestMethod.GET)
    public String showVelocity(Model model, HttpServletRequest request) {
        VelocityEngine engine = new VelocityEngine(getProperties());
        engine.init();
        Map<String, Object> data = new HashMap<>();
        List<Post> posts = new ArrayList<>();
        JCUser user = new JCUser("user", "", "");
        posts.add(new Post(user, "Lorem ipsum <strong>dolor</strong> sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        Page<Post> postPage = new PageImpl<>(posts);
        JCUser currentUser = ReadOnlySecurityService.getInstance().getCurrentUser();
        data.putAll(getDefaultModel(request));
        data.put("postPage", postPage);
        data.put("question", new Topic(user, "Test title"));
        data.put("subscribed", false);
        model.addAttribute("content", VelocityEngineUtils.mergeTemplateIntoString(engine,
                "org/jtalks/jcommune/plugin/questionsandanswers/template/question.vm", "UTF-8", data));
        return "plugin/plugin";
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
        String jarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        properties.put("jar.resource.loader.path", "jar:file:" + jarPath);
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
        JCUser currentUser = ReadOnlySecurityService.getInstance().getCurrentUser();
        model.put("currentUser", currentUser);
        model.put("messages", getLocalizedMessagesBundle(currentUser));
        return model;
    }
}
