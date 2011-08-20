package org.jtalks.jcommune.web.dto;

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Section;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO builder for {@link org.jtalks.jcommune.web.dto.Breadcrumb} objects.
 * Used for preparing breadcrumbs for the different JSP views.
 *
 * @author Alexandre Teterin
 */

public class BreadcrumbBuilder {

    //represent the location breadcrumb list
    private List<Breadcrumb> breadcrumbList;

    /**
     * Returns the Forum breadcrumbs
     *
     * @return the breadcrumb list for the Forum location. Contains one (root) breadcrumb.
     */
    public List<Breadcrumb> getForumBreadcrumb() {
        breadcrumbList = new ArrayList<Breadcrumb>();
        breadcrumbList.add(prepareForumBreadcrumb());

        return breadcrumbList;
    }

    /**
     * Returns the section breadcrumbs
     *
     * @param section {@link org.jtalks.jcommune.model.entity.Section} the breadcrumbed section
     * @return the breadcrumb list for the current <code>Section</code> location
     */
    public List<Breadcrumb> getSectionBreadcrumb(Section section) {
        breadcrumbList = getForumBreadcrumb();
        breadcrumbList.add(prepareSectionBreadcrumb(section));

        return breadcrumbList;
    }

    /**
     * Returns the branch breadcrumbs
     *
     * @param branch {@link org.jtalks.jcommune.model.entity.Branch} the breadcrumbed branch
     * @return the breadcrumb list for the current <code>Branch</code> location
     */
    public List<Breadcrumb> getBranchBreadcrumb(Branch branch) {
        breadcrumbList = getSectionBreadcrumb(branch.getSection());
        breadcrumbList.add(prepareBranchBreadcrumb(branch));

        return breadcrumbList;
    }

    /**
     *Fill the forum breadcrumb.
     *
     * @return {@link Breadcrumb} the filled breadcrumb fot the Forum location
     */
    private Breadcrumb prepareForumBreadcrumb() {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setId(Breadcrumb.ROOT_BREADCRUMB_ID);
        breadcrumb.setBreadcrumbLocation(Breadcrumb.BreadcrumbLocation.FORUM);
        breadcrumb.setBreadcrumbLocationValue(Breadcrumb.ROOT_BREADCRUMB_NAME_VALUE);

        return breadcrumb;
    }


    /**
     *Fill the section breadcrumb.
     *
     * @param section {@link org.jtalks.jcommune.model.entity.Section} the breadcrumbed section
     * @return {@link Breadcrumb} the filled breadcrumb fot the Forum location
     */
    private Breadcrumb prepareSectionBreadcrumb(Section section) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setId(section.getId());
        breadcrumb.setBreadcrumbLocation(Breadcrumb.BreadcrumbLocation.SECTION);
        breadcrumb.setBreadcrumbLocationValue(section.getName());

        return breadcrumb;
    }

    /**
     *Fill the branch breadcrumb.
     *
     * @param branch {@link org.jtalks.jcommune.model.entity.Branch} the breadcrumbed branch
     * @return {@link Breadcrumb} the filled breadcrumb fot the Section location
     */
    private Breadcrumb prepareBranchBreadcrumb(Branch branch) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setId(branch.getId());
        breadcrumb.setBreadcrumbLocation(Breadcrumb.BreadcrumbLocation.BRANCH);
        breadcrumb.setBreadcrumbLocationValue(branch.getName());

        return breadcrumb;
    }
}