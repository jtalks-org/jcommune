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

import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * The interface to manipulate with sections
 *
 * @author Max Malakhov
 */
public interface SectionService extends EntityService<Section> {
    /**
     * Get list of all sections.
     *
     * @return - list of the sections.
     */
    List<Section> getAll();
    
    /**
     * Prepares sections for the main forum page.Fills the necessary information
     * for the branches of each section.
     * Calling this method avoids the use of counters, which reduce the response
     * time of the main page.
     * 
     * @param sections the list of sections
     */
    void prepareSectionsForView(List<Section> sections);

    /**
     * Deletes all topics in the session given, causing post count updates.
     *
     * @param sectionId section id
     * @return section without branches
     * @throws NotFoundException when section not found
     */
    Section deleteAllTopicsInSection(long sectionId) throws NotFoundException;

    /**
     *  Deletes all topics in all the sections, causing post count updates.
     *
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException if object for deletion has not been found
     */
    void deleteAllTopicsInForum() throws NotFoundException;

    /**
     * Checks permission VIEW_TOPICS for access
     * @param section section
     * @throws AccessDeniedException throw if there is no permission for access
     */
    void ifSectionIsVisible(Section section) throws AccessDeniedException;

    /**
     * Get all available for move topic sections.
     *
     * @param currentTopicId topic id that we want to move
     */
    List<Section> getAllAvailableSections(long currentTopicId);

    /**
     * Gets last posts created in the section
     * @param section the section to get latest posts
     * @param postsCount maximum posts count to retrieve
     * @return list of the latest posts in the given section
     */
    List<Post> getLastPostsForSection(Section section, int postsCount);
}
