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
package org.jtalks.jcommune.web.dto;

/**
 * Breadcrumb element
 *
 * @author Alexandre Teterin
 */

public class Breadcrumb {

    public static final String ROOT_BREADCRUMB_LOCATION_VALUE = "Forum";
    public static final String INBOX_BREADCRUMB_LOCATION_VALUE = "Inbox";
    public static final String OUTBOX_BREADCRUMB_LOCATION_VALUE = "Outbox";
    public static final String DRAFTS_BREADCRUMB_LOCATION_VALUE = "Drafts";
    public static final String RECENT_BREADCRUMB_LOCATION_VALUE = "Recent";
    public static final Long STUB_BREADCRUMB_ID = 1L; // used when node have no ID, e.g. folder

    private static final int SIZE_LIMIT = 40;
    private static final String ABBREVIATION_SIGN = "...";

    /**
     * Enumerates all possible location on the forum
     */
    public enum BreadcrumbLocation {
        FORUM("sections"),

        BRANCH("branches"),
        SECTION("sections"),
        TOPIC("topics"),
        POST("posts"),

        INBOX("/inbox"),
        OUTBOX("/outbox"),
        DRAFTS("/drafts"),

        RECENT("/topics/recent");


        //Displayed value for the HTML link
        private String name;

        /**
         * Set the Breadcrumb URL location
         *
         * @param name Breadcrumb URL location
         */
        BreadcrumbLocation(String name) {
            this.name = name;
        }

        /**
         * Return the Breadcrumb URL location
         *
         * @return name Breadcrumb URL location
         */
        public String getName() {
            return name;
        }

        /**
         * Return the display value for the breadcrumb URL location
         *
         * @return the display value for the breadcrumb URL location
         */

        @Override
        public String toString() {
            return name;
        }
    }

    private Long id;
    //Displayed URL breadcrumb value
    private BreadcrumbLocation breadcrumbLocation;
    //Displayed breadcrumb value
    private String breadcrumbLocationValue;

    /**
     * Create instance {@code }Breadcrumb{@code } and set it fields.
     *
     * @param id                      location id
     * @param breadcrumbLocation      used for constructing location URL
     * @param breadcrumbLocationValue used for constructing location display name
     */
    public Breadcrumb(Long id, BreadcrumbLocation breadcrumbLocation, String breadcrumbLocationValue) {
        this.id = id;
        this.breadcrumbLocation = breadcrumbLocation;
        this.breadcrumbLocationValue = breadcrumbLocationValue;
    }

    /**
     * Get the breadcrumb location id.
     *
     * @return id breadcrumb location id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the location element breadcrumb URL.
     *
     * @return breadcrumbLocation  the location element breadcrumb URL.
     */
    public BreadcrumbLocation getBreadcrumbLocation() {
        return breadcrumbLocation;
    }

    /**
     * Set the location element breadcrumb URL.
     *
     * @param breadcrumbLocation the location element breadcrumb URL.
     */
    public void setBreadcrumbLocation(BreadcrumbLocation breadcrumbLocation) {
        this.breadcrumbLocation = breadcrumbLocation;
    }

    /**
     * Get the display breadcrumb name.
     *
     * @return breadcrumbLocationValue the display breadcrumb name.
     */
    public String getBreadcrumbLocationValue() {
        if (breadcrumbLocationValue.length() < SIZE_LIMIT) {
            return breadcrumbLocationValue;
        } else {
            int barrier = SIZE_LIMIT - ABBREVIATION_SIGN.length();
            return breadcrumbLocationValue.substring(0, barrier) + ABBREVIATION_SIGN;
        }
    }

    /**
     * Get the display breadcrumb name.
     *
     * @param breadcrumbLocationValue the display breadcrumb name.
     */
    public void setBreadcrumbLocationValue(String breadcrumbLocationValue) {
        this.breadcrumbLocationValue = breadcrumbLocationValue;
    }
}