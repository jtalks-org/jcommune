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
package org.jtalks.jcommune.model.dto;

import org.jtalks.common.model.entity.Group;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Access list for {@link org.jtalks.common.model.entity.Group}
 *
 * @author stanislav bashkirtsev
 * @author Vyacheslav Zhivaev
 */
public class GroupAccessList {
    private final List<Group> allowed = new ArrayList<Group>();
    private final List<Group> restricted = new ArrayList<Group>();

    /**
     * Adds group to the allowed access list.
     *
     * @param group to be allowed
     * @return {@code this} instance for providing fluent interface
     */
    public GroupAccessList addAllowed(@Nullable Group group) {
        if (group != null) {
            allowed.add(group);
        }
        return this;
    }

    /**
     * Restricts a given group.
     *
     * @param group to be restricted
     * @return {@code this} instance for providing fluent interface
     */
    public GroupAccessList addRestricted(@Nullable Group group) {
        if (group != null) {
            restricted.add(group);
        }
        return this;
    }

    /**
     * Sets new list of allowed groups (the elements from the old are removed).
     *
     * @param allowed allowed groups
     * @return {@code this} instance for providing fluent interface
     */
    public GroupAccessList setAllowed(List<Group> allowed) {
        this.allowed.clear();
        this.allowed.addAll(allowed);
        return this;
    }

    /**
     * Sets new list of restricted groups (the elements from the old are removed).
     *
     * @param restricted restricted groups
     * @return {@code this} instance for providing fluent interface
     */
    public GroupAccessList setRestricted(List<Group> restricted) {
        this.restricted.clear();
        this.restricted.addAll(restricted);
        return this;
    }

    /**
     * Gets list of restricted groups.
     *
     * @return unmodifiable list of restricted groups
     */
    public List<Group> getRestricted() {
        return Collections.unmodifiableList(restricted);
    }

    /**
     * Gets list of allowed groups.
     *
     * @return unmodifiable list of allowed groups
     */
    public List<Group> getAllowed() {
        return Collections.unmodifiableList(allowed);
    }
}
