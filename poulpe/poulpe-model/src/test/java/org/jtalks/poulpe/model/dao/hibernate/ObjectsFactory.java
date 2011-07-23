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
package org.jtalks.poulpe.model.dao.hibernate;

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.Session;
import org.jtalks.poulpe.model.entity.Branch;
import org.jtalks.poulpe.model.entity.TopicType;

/**
 * @author Kirill Afonin
 */
// TODO: split this class on 2: objects factory and persisted objects factory
public final class ObjectsFactory {
    private ObjectsFactory() {
    }

    public static void setSession(Session session) {
        ObjectsFactory.session = session;
    }

    // XXX: why is it injected if it is not used? 
    // someone forgot to add something into this class?
    private static Session session;

    public static Branch getDefaultBranch() {
        Branch newBranch = new Branch();
        newBranch.setName("branch name");
        newBranch.setDescription("branch description");
        return newBranch;
    }

    /**
     * Create type of topic with random title, it may be usefull when need to
     * persist mаny object in testing.
     * 
     * @return type of topic
     */
    public static TopicType createTopicTypeWithRandomTitle() {
        TopicType topicType = new TopicType();
        String randomTitle = "topic type title" + RandomStringUtils.random(10);
        topicType.setTitle(randomTitle);
        topicType.setDescription("topic type description");
        return topicType;
    }
}
