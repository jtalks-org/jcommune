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
package org.jtalks.jcommune.model.dao.security;

import org.jtalks.common.model.dao.Crud;
import org.jtalks.common.model.entity.Group;

import java.util.List;

/**
 * Dao interface for accessing {@link org.jtalks.common.model.entity.Group} objects.
 *
 * @author Konstantin Akimov
 */
public interface GroupDao extends Crud<Group> {

    /**
     * Get the list of all groups.
     *
     * @return list of groups
     */
    List<Group> getAll();

    /**
     * Get the list of all groups which names contains the specified name.
     *
     * @param name group name
     * @return list of groups
     * @throws IllegalArgumentException if name is null
     */
    List<Group> getByNameContains(String name);

    /**
     * Get the list of all groups which name matches ignoring case the specified name.
     *
     * @param name group name
     * @return list of groups
     * @throws IllegalArgumentException if name is null
     */
    Group getByName(String name);

}
