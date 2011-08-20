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
package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.Session;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.Section;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;

/**
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Max Malakhov
 */
//TODO: split this class on 2: objects factory and persisted objects factory
public final class ObjectsFactory {
    private ObjectsFactory() {
    }

    public static void setSession(Session session) {
        ObjectsFactory.session = session;
    }

    private static Session session;

    public static User getDefaultUser() {
        return getUser("username", "username@mail.com");
    }

    public static User getUser(String username, String email) {
        User newUser = new User(username, email, "password");
        newUser.setFirstName("first name");
        newUser.setLastName("last name");
        return newUser;
    }

    public static Post getDefaultPost() {
        return new Post(persist(getDefaultUser()), "post content");
    }

    public static Topic getDefaultTopic() {
        User user = persist(getDefaultUser());
        Branch branch = getDefaultBranch();
        Topic newTopic = new Topic(user, "topic title");
        Post post = new Post(user, "post content");
        newTopic.addPost(post);
        branch.addTopic(newTopic);
        persist(branch);
        return newTopic;
    }

    public static Branch getDefaultBranch() {
        Branch newBranch = new Branch();
        newBranch.setName("branch name");
        newBranch.setDescription("branch description");
        return newBranch;
    }

    public static Section getDefaultSection() {
        Section newSection = new Section();
        newSection.setName("section name");
        newSection.setDescription("branch description");
        newSection.setPosition(1L);
        return newSection;
    }

    /**
     * Create the PrivateMessage with filled required fields.
     *
     * @return ready to save instance
     */
    public static PrivateMessage getDefaultPrivateMessage() {
        PrivateMessage pm = PrivateMessage.createNewPrivateMessage();
        pm.setUserFrom(persist(getUser("UserFrom", "mail1")));
        pm.setUserTo(persist(getUser("UserTo", "mail2")));
        pm.setBody("Private message body");
        pm.setTitle("Message title");
        return pm;
    }

    public static PrivateMessage getPrivateMessage(User to, User from) {
        PrivateMessage pm = PrivateMessage.createNewPrivateMessage();
        pm.setUserFrom(from);
        pm.setUserTo(to);
        pm.setBody("Private message body");
        pm.setTitle("Message title");
        return pm;
    }

    private static <T> T persist(T entity) {
        session.save(entity);
        return entity;
    }

}
