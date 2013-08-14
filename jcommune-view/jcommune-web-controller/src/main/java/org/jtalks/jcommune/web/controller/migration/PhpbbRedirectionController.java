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
package org.jtalks.jcommune.web.controller.migration;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;

/**
 * Performs redirection from old phpbb-style URLs to support
 * javatalks.ru forum migration.
 *
 * @author Evgeniy Naumenko
 */
@Controller
public class PhpbbRedirectionController {

    /**
     * http://www.javatalks.ru/ftopic2036-0.php
     * http://www.javatalks.ru/ftopic2036-0.php/topicname
     * @param topicParams example ftopic2036-0, where the last number is page, which is ignored in our case
     * @param response http response object to set headers on
     * @param request http request to figure out the context path
     */
    @RequestMapping({"/ftopic{topicParams}", "/ftopic{topicParams}.php",
                        "/ftopic{topicParams}/**", "/ftopic{topicParams}.php/**"})
    public void showFtopicWithAdditionalParams(@PathVariable String topicParams, HttpServletResponse response,
                                               WebRequest request) {
        String id = StringUtils.substringBefore(topicParams, "-");
        String redirectUrl = request.getContextPath() +  "/topics/" + id;
        setHttp301Headers(response, redirectUrl);
    }

    /**
     * Redirect topic URLs to, assumes that topic ids
     * haven't been changed during migration. 
     * 
     * @param topicParams contains topic id and additional info(it will be ignored)
     * @param response http response object to set headers on
     * @param request http request to figure out the context path
     */
    @RequestMapping("/topics/{topicParams}.php")
    public void showTopicWithAdditionalParams(
            @PathVariable String topicParams,
            HttpServletResponse response,
            WebRequest request) {
        String id = StringUtils.substringBefore(topicParams, "-");
        String redirectUrl = request.getContextPath() +  "/topics/" + id;
        setHttp301Headers(response, redirectUrl);
    }

    /**
     * Redirects post URLs, assumes that post ids
     * haven't been changed during data migration
     *
     * @param id post identifier from url
     * @param response http response object to set headers on
     * @param request http request to figure out the context path
     */
    @RequestMapping({"/sutra{id}.php", "/sutra{id}"})
    public void showPost(@PathVariable String id, HttpServletResponse response, WebRequest request) {
        String redirectUrl = request.getContextPath() +  "/posts/" + id;
        setHttp301Headers(response, redirectUrl);
    }

    /**
     * Redirects branch URL's, assumes that bramch ids
     * haven't been changed during data migration
     *
     * @param id branch identifier from url
     * @param response http response object to set headers on
     * @param request http request to figure out the context path
     */
    @RequestMapping("/forum{id}.php")
    public void showBranch(@PathVariable String id, HttpServletResponse response, WebRequest request) {
        String redirectUrl = request.getContextPath() +  "/branches/" + id;
        setHttp301Headers(response, redirectUrl);
    }

    /**
     * Redirects branch URL's, assumes that branch ids haven't been changed during data migration. Redirects always to
     * the first page of the branch even if page was specified in params.
     *
     * @param id branch identifier from url, but also may contain the page number which will be ignored
     * @param response http response object to set headers on
     * @param request http request to figure out the context path
     */
    @RequestMapping("/viewforum{branchParams}.php")
    public void showBranchWithViewPrefix(@PathVariable String branchParams, HttpServletResponse response,
                                         WebRequest request) {
        String id = StringUtils.substringBefore(branchParams, "-");
        String redirectUrl = request.getContextPath() +  "/branches/" + id;
        setHttp301Headers(response, redirectUrl);
    }

    /**
     * Redirects search URL's to default search page
     *
     * @param response http response object to set headers on
     * @param request http request to figure out the context path
     */
    @RequestMapping("/search.php")
    public void showSearch(HttpServletResponse response, WebRequest request) {
        String redirectUrl = request.getContextPath() +  "/search/?searchText=";
        setHttp301Headers(response, redirectUrl);
    }

    /**
     * Method sets Http 301 Moved permanently http headers to
     * indicate this URL should be changed if indexed elsewhere.
     * When used instead of plain redirect it allows graceful
     * search engine index modification. Browser is expected to
     * behave the same way as if redirect response has been sent.
     *
     * @param response http response object to be filled with headers
     * @param newUrl redirect url with context path
     */
    private void setHttp301Headers(HttpServletResponse response, String newUrl) {
        response.setStatus(301);
        response.setHeader("Location", newUrl);
        response.setHeader("Connection", "close");
    }
}
