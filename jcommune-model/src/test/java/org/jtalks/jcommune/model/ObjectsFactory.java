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
import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.entity.*;

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

    public static JCUser getDefaultUser() {
        return getUser("username", "username@mail.com");
    }

    public static JCUser getUser(String username, String email) {
        JCUser newUser = new JCUser(username, email, "password");
        newUser.setFirstName("first name");
        newUser.setLastName("last name");
        return newUser;
    }

    public static Post getDefaultPost() {
        return new Post(persist(getDefaultUser()), "post content");
    }

    public static Topic getDefaultTopic() {
        JCUser user = persist(getDefaultUser());
        Branch branch = getDefaultBranch();
        Topic newTopic = new Topic(user, "topic title");
        Post post = new Post(user, "post content");
        newTopic.addPost(post);
        branch.addTopic(newTopic);
        persist(branch);
        return newTopic;
    }

    public static Branch getDefaultBranch() {
        Branch newBranch = new Branch("branch name", "branch description");
        return newBranch;
    }

    public static Section getDefaultSection() {
        Section newSection = new Section("section name");
        newSection.setDescription("branch description");
        newSection.setPosition(1);
        return newSection;
    }

    /**
     * Create the PrivateMessage with filled required fields.
     *
     * @return ready to save instance
     */
    public static PrivateMessage getDefaultPrivateMessage() {
        JCUser userTo = persist(getUser("UserTo", "mail2@mail.com"));
        JCUser userFrom = persist(getUser("UserFrom", "mail1@mail.com"));
        return new PrivateMessage(userTo, userFrom,
                "Message title", "Private message body");
    }

    public static PrivateMessage getPrivateMessage(JCUser userTo, JCUser userFrom) {
        return new PrivateMessage(userTo, userFrom,
                "Message title", "Private message body");
    }

    public static UserContactType getDefaultUserContactType() {
        UserContactType type = new UserContactType();
        type.setTypeName("Some type");
        type.setIcon("/some/icon");
        type.setMask("12345");
        type.setDisplayPattern("protocol://" + UserContactType.CONTACT_MASK_PLACEHOLDER);
        type.setValidationPattern("\\d+");
        return type;
    }

    public static UserContact getDefaultUserContact() {
        UserContactType type = new UserContactType();
        UserContact contact = new UserContact("value", type);
        contact.setOwner(ObjectsFactory.getDefaultUser());
        return contact;
    }

    private static <T> T persist(T entity) {
        session.save(entity);
        return entity;
    }

    public static List<Post> createAndSavePostList(int size) {
        List<Post> posts = new ArrayList<Post>();
        Topic topic = ObjectsFactory.getDefaultTopic();
        JCUser author = topic.getTopicStarter();
        for (int i = 0; i < size - 1; i++) {
            Post newPost = new Post(author, "content " + i);
            topic.addPost(newPost);
            posts.add(newPost);
            session.save(newPost);
        }
        session.save(topic);
        return posts;
    }

    public static LastReadPost getDefaultLastReadPost() {
        Topic topic = getDefaultTopic();
        JCUser user = topic.getTopicStarter();
        return new LastReadPost(user, topic, 0);
    }

    public static Poll createDefaultVoting() {
        Topic topic = getDefaultTopic();
        Poll voting = new Poll("New voting");
        topic.setPoll(voting);
        voting.setTopic(topic);
        persist(topic);
        return voting;
    }

    public static PollItem createDefaultVotingOption() {
        Poll voting = createDefaultVoting();
        persist(voting);
        PollItem option = new PollItem("First voting option");
        voting.addPollOptions(option);
        return option;
    }

    public static SimplePage getDefaultSimplePage() {
        return new SimplePage("name", "content", "pathName");
    }
}
