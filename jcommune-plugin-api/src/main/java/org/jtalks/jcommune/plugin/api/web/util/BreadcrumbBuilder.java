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
package org.jtalks.jcommune.plugin.api.web.util;

import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.web.dto.Breadcrumb;
import org.jtalks.jcommune.plugin.api.web.dto.BreadcrumbLocation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO builder for {@link org.jtalks.jcommune.plugin.api.web.dto.Breadcrumb} objects.
 * Used for preparing breadcrumbs for the different JSP views.
 *
 * @author Alexandre Teterin
 */

@Component
public class BreadcrumbBuilder {

    /**
     * Returns the Forum breadcrumbs.
     *
     * @return the breadcrumb list for the Forum location. Contains one (root) breadcrumb.
     */
    public List<Breadcrumb> getForumBreadcrumb() {
        List<Breadcrumb> breadcrumbList = new ArrayList<>();
        breadcrumbList.add(prepareForumBreadcrumb());

        return breadcrumbList;
    }

    /**
     * Returns the branch breadcrumbs.
     *
     * @param branch {@link org.jtalks.jcommune.model.entity.Branch} the breadcrumbed branch.
     * @return the breadcrumb list for the current <code>Branch</code> location.
     */
    public List<Breadcrumb> getForumBreadcrumb(Branch branch) {
        List<Breadcrumb> breadcrumbList = getForumBreadcrumb();
        breadcrumbList.add(prepareBranchBreadcrumb(branch));

        return breadcrumbList;
    }

    /**
     * Returns the topic breadcrumbs.
     *
     * @param topic {@link org.jtalks.jcommune.model.entity.Topic} the breadcrumbed topic.
     * @return the breadcrumb list for the current <code>Topic</code> location.
     */
    public List<Breadcrumb> getForumBreadcrumb(Topic topic) {
        List<Breadcrumb> breadcrumbList = getForumBreadcrumb(topic.getBranch());
        breadcrumbList.add(prepareTopicBreadcrumb(topic.getBranch()));

        return breadcrumbList;
    }

    /**
     * Returns the post breadcrumbs.
     *
     * @param post {@link org.jtalks.jcommune.model.entity.Post} the breadcrumbed post.
     * @return the breadcrumb list for the current <code>Post</code> location.
     */
    public List<Breadcrumb> getForumBreadcrumb(Post post) {
        List<Breadcrumb> breadcrumbList = getForumBreadcrumb(post.getTopic());
        breadcrumbList.add(preparePostBreadcrumb(post));

        return breadcrumbList;
    }

    /**
     * Returns the topic breadcrumbs.
     *
     * @param branch {@link org.jtalks.jcommune.model.entity.Topic} of the breadcrumbed topic.
     * @return the breadcrumb list for the current <code>Topic</code> location.
     */
    public List<Breadcrumb> getNewTopicBreadcrumb(Branch branch) {
        List<Breadcrumb> breadcrumbList = getForumBreadcrumb(branch);
        breadcrumbList.add(prepareTopicBreadcrumb(branch));

        return breadcrumbList;
    }

    /**
     * Fill the forum breadcrumb.
     *
     * @return {@link Breadcrumb} the filled breadcrumb for the Forum location.
     */
    private Breadcrumb prepareForumBreadcrumb() {
        return new Breadcrumb(
                Breadcrumb.STUB_BREADCRUMB_ID,
                BreadcrumbLocation.FORUM,
                Breadcrumb.ROOT_BREADCRUMB_LOCATION_VALUE);
    }

    /**
     * Fill the branch breadcrumb.
     *
     * @param branch {@link org.jtalks.jcommune.model.entity.Branch} the breadcrumbed branch.
     * @return {@link Breadcrumb} the filled breadcrumb for the Section location.
     */
    private Breadcrumb prepareBranchBreadcrumb(Branch branch) {
        Section section = branch.getSection();
        return new Breadcrumb(
                section.getId(),
                BreadcrumbLocation.SECTION,
                section.getName());
    }

    /**
     * Fill the branch breadcrumb.
     *
     * @param branch {@link org.jtalks.jcommune.model.entity.Branch} of the breadcrumbed topic.
     * @return {@link Breadcrumb} the filled breadcrumb for the Topic location.
     */
    private Breadcrumb prepareTopicBreadcrumb(Branch branch) {
        return new Breadcrumb(
                branch.getId(),
                BreadcrumbLocation.BRANCH,
                branch.getName());
    }

    /**
     * Fill the post breadcrumb.
     *
     * @param post {@link org.jtalks.jcommune.model.entity.Post} the breadcrumbed post.
     * @return {@link Breadcrumb} the filled breadcrumb for the Post location.
     */
    private Breadcrumb preparePostBreadcrumb(Post post) {
        Topic topic = post.getTopic();
        return new Breadcrumb(
                topic.getId(),
                BreadcrumbLocation.TOPIC,
                topic.getTitle());
    }
}