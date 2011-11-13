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
package org.jtalks.jcommune.model;

import org.hibernate.Session;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.Section;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;

import java.util.ArrayList;
import java.util.List;

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
        Branch newBranch = new Branch("branch name");
        newBranch.setDescription("branch description");
        return newBranch;
    }

    public static Section getDefaultSection() {
        Section newSection = new Section("section name");
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
        User userTo = persist(getUser("UserTo", "mail2"));
        User userFrom = persist(getUser("UserFrom", "mail1"));
        return new PrivateMessage(userTo, userFrom,
                "Message title", "Private message body");
    }

    public static PrivateMessage getPrivateMessage(User userTo, User userFrom) {
        return new PrivateMessage(userTo, userFrom,
                "Message title", "Private message body");
    }

    private static <T> T persist(T entity) {
        session.save(entity);
        return entity;
    }

    public static List<Post> createAndSavePostList(int size) {
        List<Post> posts = new ArrayList<Post>();
        Topic topic = ObjectsFactory.getDefaultTopic();
        User author = topic.getTopicStarter();
        for (int i = 0; i < size - 1; i++) {
            Post newPost = new Post(author, "content " + i);
            topic.addPost(newPost);
            posts.add(newPost);
        }
        session.save(topic);
        return posts;
    }

}
