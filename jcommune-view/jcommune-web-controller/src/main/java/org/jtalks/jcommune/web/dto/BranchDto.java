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
 * Dto for transferring branches to client side.
 * Dto does not contains topics in branch, just id and name.
 *
 * @author Eugeny Batov
 */
public class BranchDto {

    private long id;
    private String name;

    /**
     * @param id   unique branch identifier
     * @param name branch display name
     */
    public BranchDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @return branch id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets branch id.
     *
     * @param id id of branch
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return branch name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets branch name.
     *
     * @param name name of branch
     */
    public void setName(String name) {
        this.name = name;
    }

}
