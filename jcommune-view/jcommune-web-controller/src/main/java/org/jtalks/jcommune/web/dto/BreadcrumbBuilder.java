package org.jtalks.jcommune.web.dto;

import org.jtalks.jcommune.model.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO builder for {@link org.jtalks.jcommune.web.dto.Breadcrumb} objects.
 * Used for preparing breadcrumbs for the different JSP views.
 *
 * @author Alexandre Teterin
 */

public class BreadcrumbBuilder {

    /**
     * Returns the Forum breadcrumbs.
     *
     * @return the breadcrumb list for the Forum location. Contains one (root) breadcrumb.
     */
    public List<Breadcrumb> getForumBreadcrumb() {
        List<Breadcrumb> breadcrumbList = new ArrayList<Breadcrumb>();
        breadcrumbList.add(prepareForumBreadcrumb());

        return breadcrumbList;
    }

    /**
     * Returns the section breadcrumbs.
     *
     * @param section {@link org.jtalks.jcommune.model.entity.Section} the breadcrumbed section.
     * @return the breadcrumb list for the current <code>Section</code> location.
     */
    public List<Breadcrumb> getForumBreadcrumb(Section section) {
        List<Breadcrumb> breadcrumbList = getForumBreadcrumb();
        breadcrumbList.add(prepareSectionBreadcrumb(section));

        return breadcrumbList;
    }

    /**
     * Returns the branch breadcrumbs.
     *
     * @param branch {@link org.jtalks.jcommune.model.entity.Branch} the breadcrumbed branch.
     * @return the breadcrumb list for the current <code>Branch</code> location.
     */
    public List<Breadcrumb> getForumBreadcrumb(Branch branch) {
        List<Breadcrumb> breadcrumbList = getForumBreadcrumb(branch.getSection());
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
        breadcrumbList.add(prepareTopicBreadcrumb(topic));

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
     * Return the Inbox breadcrumbs.
     *
     * @return the breadcrumb list for the Inbox location.
     */
    public List<Breadcrumb> getInboxBreadcrumb() {
        List<Breadcrumb> breadcrumbList = getForumBreadcrumb();
        breadcrumbList.add(prepareInboxBreadcrumb());

        return breadcrumbList;
    }

    /**
     * Returns the Outbox breadcrumbs.
     *
     * @return the breadcrumb list for the Outbox location.
     */
    public List<Breadcrumb> getOutboxBreadcrumb() {
        List<Breadcrumb> breadcrumbList = getForumBreadcrumb();
        breadcrumbList.add(prepareOutboxBreadcrumb());

        return breadcrumbList;
    }

    /**
     * Returns the Drafts breadcrumbs.
     *
     * @return the breadcrumb list for the Drafts location.
     */
    public List<Breadcrumb> getDraftsBreadcrumbs() {
        List<Breadcrumb> breadcrumbList = getForumBreadcrumb();
        breadcrumbList.add(prepareDraftsBreadcrumb());

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
                Breadcrumb.BreadcrumbLocation.FORUM,
                Breadcrumb.ROOT_BREADCRUMB_LOCATION_VALUE);
    }

    /**
     * Fill the section breadcrumb.
     *
     * @param section {@link org.jtalks.jcommune.model.entity.Section} the breadcrumbed section.
     * @return {@link Breadcrumb} the filled breadcrumb for the Forum location.
     */
    private Breadcrumb prepareSectionBreadcrumb(Section section) {
        return new Breadcrumb(
                section.getId(),
                Breadcrumb.BreadcrumbLocation.SECTION,
                section.getName());
    }

    /**
     * Fill the branch breadcrumb.
     *
     * @param branch {@link org.jtalks.jcommune.model.entity.Branch} the breadcrumbed branch.
     * @return {@link Breadcrumb} the filled breadcrumb for the Section location.
     */
    private Breadcrumb prepareBranchBreadcrumb(Branch branch) {
        return new Breadcrumb(
                branch.getId(),
                Breadcrumb.BreadcrumbLocation.BRANCH,
                branch.getName());
    }

    /**
     * Fill the branch breadcrumb.
     *
     * @param topic {@link org.jtalks.jcommune.model.entity.Topic} the breadcrumbed topic.
     * @return {@link Breadcrumb} the filled breadcrumb for the Topic location.
     */
    private Breadcrumb prepareTopicBreadcrumb(Topic topic) {
        return new Breadcrumb(
                topic.getId(),
                Breadcrumb.BreadcrumbLocation.TOPIC,
                topic.getTitle());
    }

    /**
     * Fill the post breadcrumb.
     *
     * @param post {@link org.jtalks.jcommune.model.entity.Post} the breadcrumbed post.
     * @return {@link Breadcrumb} the filled breadcrumb for the Post location.
     */
    private Breadcrumb preparePostBreadcrumb(Post post) {
        return new Breadcrumb(
                post.getId(),
                Breadcrumb.BreadcrumbLocation.POST,
                //TODO Need additional info about display post breadcrumb
                "Post ID " + post.getId());
    }

    /**
     * Fill the inbox breadcrumb.
     *
     * @return {@link Breadcrumb} the filled breadcrumb for the inbox location.
     */
    private Breadcrumb prepareInboxBreadcrumb() {
        return new Breadcrumb(
                Breadcrumb.STUB_BREADCRUMB_ID,
                Breadcrumb.BreadcrumbLocation.INBOX,
                Breadcrumb.INBOX_BREADCRUMB_LOCATION_VALUE);
    }

    /**
     * Fill the outbox breadcrumb.
     *
     * @return {@link Breadcrumb} the filled breadcrumb for the outbox location.
     */
    private Breadcrumb prepareOutboxBreadcrumb() {
        return new Breadcrumb(
                Breadcrumb.STUB_BREADCRUMB_ID,
                Breadcrumb.BreadcrumbLocation.OUTBOX,
                Breadcrumb.OUTBOX_BREADCRUMB_LOCATION_VALUE);
    }

    /**
     * Fill the drafts breadcrumb.
     *
     * @return {@link Breadcrumb} the filled breadcrumb for the drafts location.
     */
    private Breadcrumb prepareDraftsBreadcrumb() {
        return new Breadcrumb(
                Breadcrumb.STUB_BREADCRUMB_ID,
                Breadcrumb.BreadcrumbLocation.DRAFTS,
                Breadcrumb.DRAFTS_BREADCRUMB_LOCATION_VALUE);
    }

    /**
     * Fill the new PM breadcrumb.
     *
     * @return {@link Breadcrumb} the filled breadcrumb for the new PM location.
     */
    private Breadcrumb prepareNewPMBreadcrumb() {
        return new Breadcrumb(
                Breadcrumb.STUB_BREADCRUMB_ID,
                Breadcrumb.BreadcrumbLocation.NEW_PM,
                Breadcrumb.NEW_PM_BREADCRUMB_LOCATION_VALUE);
    }

    /**
     * Fill the draft PM breadcrumb.
     *
     * @param pm {@link org.jtalks.jcommune.model.entity.PrivateMessage} the breadcrumbed draft pm.
     * @return {@link Breadcrumb} the filled breadcrumb for the draft location.
     */
    private Breadcrumb prepareDraftPmBreadcrumb(PrivateMessage pm) {
        //TODO Need to define standard URI for most location - ${Entity type}/${Entity ID}.html
        //TODO Need additional info regarding to display draft breadcrumb
        return new Breadcrumb(
                pm.getId(),
                Breadcrumb.BreadcrumbLocation.DRAFT_PM,
                Breadcrumb.DRAFT_PM_BREADCRUMB_LOCATION_VALUE + ": " + pm.getTitle());
    }
}