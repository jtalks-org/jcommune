/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
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
    public static final Long STUB_BREADCRUMB_ID = 1L;

    /**
     * Enumerates all possible location on the forum
     */
    public enum BreadcrumbLocation {
        FORUM("sections"),

        BRANCH("branch"),
        SECTION("section"),
        TOPIC("topic"),
        POST("post"),

        INBOX("/pm/inbox"),
        OUTBOX("/pm/outbox"),
        DRAFTS("/pm/drafts"),

        RECENT("/recent");


        //Display name for the HTML link
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
    private BreadcrumbLocation breadcrumbLocation;
    private String breadcrumbLocationValue;

    /**
     * Create instance <code>Breadcrumb</code> and set it fields.
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
     * @param breadcrumbLocation  the location element breadcrumb URL.
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
        return breadcrumbLocationValue;
    }

    /**
     * Get the display breadcrumb name.
     *
     * @param breadcrumbLocationValue the display breadcrumb name.
     */
    public void setBreadcrumbLocationValue(String breadcrumbLocationValue) {
        this.breadcrumbLocationValue = breadcrumbLocationValue;
    }


    /**
     * Check the equality <code>Breadcrumb</code> instances
     *
     * @param o the checked for equality object.
     * @return true if this <code>Breadcrumb</code> instance is equal to the specified object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Breadcrumb that = (Breadcrumb) o;

        if (breadcrumbLocation != that.breadcrumbLocation) {
            return false;
        }
        if (!breadcrumbLocationValue.equals(that.breadcrumbLocationValue)) {
            return false;
        }
        if (!id.equals(that.id)) {
            return false;
        }

        return true;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return an integer hash code for this object.
     */
    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + breadcrumbLocation.hashCode();
        result = 31 * result + breadcrumbLocationValue.hashCode();
        return result;
    }
}