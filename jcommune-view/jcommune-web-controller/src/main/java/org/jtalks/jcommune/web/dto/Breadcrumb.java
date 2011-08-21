package org.jtalks.jcommune.web.dto;

/**
 * Breadcrumb element
 *
 * @author Alexandre Teterin
 */

public class Breadcrumb {

    public static final String ROOT_BREADCRUMB_NAME_VALUE = "Forum";
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
        INBOX("pm/inbox"),
        OUTBOX("pm/outbox"),
        NEW_MESSAGE("new"),
        DRAFTS("pm/drafts");

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
}