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
 * @author Andrei Alikov
 * DTO for the request from the client side to get information about branch permission.
 * Contains Branch ID for which information is requested, permission mask and
 * information about if information was requested for Restricted or Allowed part
 */
public class PermissionGroupRequestDto {
    private boolean allowed;
    private long branchId;
    private int permissionMask;

    /**
     * @return true if information was requested about groups for which permission is allowed
     * and false otherwise
     */
    public boolean isAllowed() {
        return allowed;
    }

    /**
     * Set the information about Restricted/Allowed
     * @param allowed true if information was requested about groups for which permission is allowed
     * and false otherwise
     */
    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    /**
     * Gets ID of the branch for which permission information is requested
     * @return ID of the branch for which permission information is requested
     */
    public long getBranchId() {
        return branchId;
    }

    /**
     * Sets ID of the branch for which permission information is requested
     * @param branchId ID of the branch for which permission information is requested
     */
    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    /**
     * Gets the mask for the permission for which information is requested
     * @return the mask for the permission for which information is requested
     */
    public int getPermissionMask() {
        return permissionMask;
    }

    /**
     * Sets the mask for the permission for which information is requested
     * @param permissionMask the mask for the permission for which information is requested
     */
    public void setPermissionMask(int permissionMask) {
        this.permissionMask = permissionMask;
    }
}
