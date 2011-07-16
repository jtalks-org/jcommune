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
import org.jtalks.jcommune.model.entity.*;

import java.io.UnsupportedEncodingException;

/**
 * @author Kirill Afonin
 * @author Alexandre Teterin
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
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFirstName("first name");
        newUser.setLastName("last name");
        newUser.setUsername(username);
        newUser.setPassword("password");
        try {
            newUser.setEncodedUsername(username);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return newUser;
    }
    
    /**
     * @return {@link User} with deference from default user values.
     * @see {@link ObjectsFactory#getDefaultUser()}. 
     */
    public static User getAnotherUser(){
        User newUser = new User();
        newUser.setUsername("anotherUsername");
        newUser.setFirstName("another first name");
        newUser.setLastName("another last name");
        newUser.setEmail("another@mail.com");
        newUser.setPassword("anotherPassword");
        try {
            newUser.setEncodedUsername("anotherEncodedUsername");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return newUser;
    }

    public static Post getDefaultPost() {
        return getPost(persist(getDefaultUser()));
    }

    public static Post getPost(User author) {
        Post newPost = Post.createNewPost();
        newPost.setPostContent("post content");
        newPost.setUserCreated(author);
        return newPost;
    }

    public static Topic getDefaultTopic() {
        Topic newTopic = Topic.createNewTopic();
        newTopic.setTitle("topic title");
        User user = persist(getDefaultUser());
        newTopic.setTopicStarter(user);
        newTopic.setBranch(persist(getDefaultBranch()));
        newTopic.setLastPost(getPost(user));
        return newTopic;
    }

    public static Topic getTopic(User author) {
        Topic newTopic = Topic.createNewTopic();
        newTopic.setTitle("topic title");
        newTopic.setTopicStarter(author);
        newTopic.setBranch(persist(getDefaultBranch()));
        return newTopic;
    }

    public static Branch getDefaultBranch() {
        Branch newBranch = new Branch();
        newBranch.setName("branch name");
        newBranch.setDescription("branch description");
        return newBranch;
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
