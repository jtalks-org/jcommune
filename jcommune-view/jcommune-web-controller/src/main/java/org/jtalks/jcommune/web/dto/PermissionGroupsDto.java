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

import java.util.List;

/**
 * @author Andrei Alikov
 * DTO for the permission for the branch containing two group lists:
 * one for groups which already have been selected for this permission and
 * another one for groups which are still available for selection
 */
public class PermissionGroupsDto {
    List<GroupDto> selectedGroups;
    List<GroupDto> availableGroups;

    /**
     * Gets the list of already selected groups
     * @return the list of already selected groups
     */
    public List<GroupDto> getSelectedGroups() {
        return selectedGroups;
    }

    /**
     * Sets the list of already selected groups
     * @param selectedGroups the list of already selected groups
     */
    public void setSelectedGroups(List<GroupDto> selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    /**
     * Get the list of still available groups
     * @return the list of still available groups
     */
    public List<GroupDto> getAvailableGroups() {
        return availableGroups;
    }

    /**
     * Sets the list of still available groups
     * @param availableGroups the list of still available groups
     */
    public void setAvailableGroups(List<GroupDto> availableGroups) {
        this.availableGroups = availableGroups;
    }
}
