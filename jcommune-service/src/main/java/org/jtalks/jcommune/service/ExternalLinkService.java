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
package org.jtalks.jcommune.service;

import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.entity.ExternalLink;

import java.util.List;

/**
 * Provides CRUD API for {@link ExternalLink}.
 *
 * @author Alexandre Teterin
 */
public interface ExternalLinkService extends EntityService<ExternalLink> {

    /**
     * Return list of all existing external link.
     *
     * @return list of all existing external link.
     */
    List<ExternalLink> getLinks();

    /**
     * Persists link to db.
     *
     * @param link link to persist
     */
    void saveLink(ExternalLink link, Component forumComponent);

    /**
     * Deletes link with specified id, does nothing if there is no such link.
     *
     * @param id link id to remove
     * @return {@code true} if entity deleted successfully
     */
    boolean deleteLink(long id, Component forumComponent);
}
