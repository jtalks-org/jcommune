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
package org.jtalks.jcommune.plugin.api.web.dto;

/**
 * Breadcrumb element
 *
 * @author Alexandre Teterin
 */

public class Breadcrumb {

    public static final String ROOT_BREADCRUMB_LOCATION_VALUE = "Forum";
    public static final Long STUB_BREADCRUMB_ID = 1L; // used when node have no ID, e.g. folder

    private static final int SIZE_LIMIT = 40;
    private static final String ABBREVIATION_SIGN = "...";

    private Long id;
    //Displayed URL breadcrumb value
    private BreadcrumbLocation breadcrumbLocation;
    //Displayed breadcrumb value
    private String value;

    /**
     * Create instance {@code }Breadcrumb{@code } and set it fields.
     *
     * @param id                      location id
     * @param breadcrumbLocation      used for constructing location URL
     * @param value used for constructing location display name
     */
    public Breadcrumb(Long id, BreadcrumbLocation breadcrumbLocation, String value) {
        this.id = id;
        this.breadcrumbLocation = breadcrumbLocation;
        this.value = value;
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
     * Get the display breadcrumb name.
     *
     * @return value the display breadcrumb name.
     */
    public String getValue() {
        if (value.length() < SIZE_LIMIT) {
            return value;
        } else {
            int barrier = SIZE_LIMIT - ABBREVIATION_SIGN.length();
            return value.substring(0, barrier) + ABBREVIATION_SIGN;
        }
    }
}