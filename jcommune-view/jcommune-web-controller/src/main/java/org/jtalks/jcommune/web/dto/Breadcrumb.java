package org.jtalks.jcommune.web.dto;

/**
 * Breadcrumb element
 *
 * @author Alexandre Teterin
 */

public class Breadcrumb {

    public static final String ROOT_BREADCRUMB_LOCATION_VALUE = "Forum";
    public static final String PM_BREADCRUMB_LOCATION_VALUE = "PM";
    public static final String INBOX_BREADCRUMB_LOCATION_VALUE = "Inbox";
    public static final String OUTBOX_BREADCRUMB_LOCATION_VALUE = "Outbox";
    public static final String DRAFTS_BREADCRUMB_LOCATION_VALUE = "Drafts";
    public static final String NEW_PM_BREADCRUMB_LOCATION_VALUE = "New Message";
    public static final String DRAFT_PM_BREADCRUMB_LOCATION_VALUE = "Draft";
    public static final Long STUB_BREADCRUMB_ID = 1L;

    /**
     * Enumerates all possible location on the forum
     */
    public enum BreadcrumbLocation {
        FORUM("main"),

        BRANCH("branch"),
        SECTION("section"),
        TOPIC("topic"),
        POST("post"),

        PROFILE("user"),

        PRIVATE_MESSAGE("pm"),
        INBOX("inbox"),
        OUTBOX("outbox"),
        NEW_PM("new"),
        DRAFTS("drafts"),
        DRAFT_PM("draft");


        //Display name for the HTML link
        private String name;

        BreadcrumbLocation(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private Long id;
    private BreadcrumbLocation breadcrumbLocation;
    private String breadcrumbLocationValue;

    public Breadcrumb(Long id, BreadcrumbLocation breadcrumbLocation, String breadcrumbLocationValue) {
        this.id = id;
        this.breadcrumbLocation = breadcrumbLocation;
        this.breadcrumbLocationValue = breadcrumbLocationValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BreadcrumbLocation getBreadcrumbLocation() {
        return breadcrumbLocation;
    }

    public void setBreadcrumbLocation(BreadcrumbLocation breadcrumbLocation) {
        this.breadcrumbLocation = breadcrumbLocation;
    }

    public String getBreadcrumbLocationValue() {
        return breadcrumbLocationValue;
    }

    public void setBreadcrumbLocationValue(String breadcrumbLocationValue) {
        this.breadcrumbLocationValue = breadcrumbLocationValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Breadcrumb that = (Breadcrumb) o;

        if (breadcrumbLocation != that.breadcrumbLocation) return false;
        if (!breadcrumbLocationValue.equals(that.breadcrumbLocationValue)) return false;
        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + breadcrumbLocation.hashCode();
        result = 31 * result + breadcrumbLocationValue.hashCode();
        return result;
    }
}