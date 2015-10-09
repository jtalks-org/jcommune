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

import org.jtalks.jcommune.model.entity.TopicDraft;

/**
 * The interface to manipulate with draft topic of current user
 *
 * @author Dmitry S. Dolzhenko
 */
public interface TopicDraftService {
    /**
     * Returns the draft topic for current user.
     *
     * @return the draft topic or null
     */
    TopicDraft getDraft();

    /**
     * Save or update the draft topic.
     *
     * @param draft the draft topic
     */
    TopicDraft saveOrUpdateDraft(TopicDraft draft);

    /**
     * Delete the draft topic.
     */
    void deleteDraft();
}
