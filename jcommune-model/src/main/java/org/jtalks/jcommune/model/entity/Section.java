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

package org.jtalks.jcommune.model.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Forum branch that contains topics related to branch theme.
 *
 * @author Max Malakhov
 */
public class Section extends Entity {

    private String name;
    private String description;
    private Long index;
    private List<Branch> branches = new ArrayList<Branch>();

    /**
     * Set section name which briefly describes the topics contained in it.
     *
     * @return section name
     */
    public String getName() {
        return name;
    }

    /**
     * Get section name.
     *
     * @param name section name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get section description.
     *
     * @return section description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set section description which contains additional information about the section.
     *
     * @param description section description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get section index.
     *
     * @param index section index
     */
    public void setIndex(Long index) {
        this.index = index;
    }

    /**
     * Get section index.
     *
     * @return section index
     */
    public Long getIndex() {
        return index;
    }

    /**
     * @return list of branches
     */
    protected List<Branch> getBranches() {
        return branches;
    }

    /**
     * @param branches list of branches
     */
    protected void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    /**
     * Add branch to section.
     *
     * @param branch branch
     */
    public void addBranch(Branch branch) {
        branch.setSection(this);
        this.branches.add(branch);
    }

    /**
     * Delete branch from section.
     *
     * @param topic topic
     */
    public void deleteTopic(Branch branch) {
        this.branches.remove(branch);
    }

    /**
     * @return number of branches in section
     */
    public int topicCount() {
        return branches.size();
    }
}
