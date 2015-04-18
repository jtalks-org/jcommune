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
package org.jtalks.jcommune.model.dao;

import org.jtalks.common.model.dao.Crud;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PostDraft;
import org.jtalks.jcommune.model.entity.Topic;

/**
 * @author Mikhail Stryzhonok
 */
public interface PostDraftDao extends Crud<PostDraft> {

    /**
     * Gets post draft by author and topic
     *
     * @param author author of interested draft
     * @param topic topic in which interested draft stored
     *
     * @return post draft by author and topic
     */
    PostDraft getByAuthorAndTopic(JCUser author, Topic topic);
}
