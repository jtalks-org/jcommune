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
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollItem;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.Topic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Max Malakhov
 * @author Eugeny Batov
 */
public final class PersistedObjectFactory {

    private static Session session;

    private PersistedObjectFactory() {
    }

    public static void setSession(Session session) {
        PersistedObjectFactory.session = session;
    }

    public static Post getDefaultPost() {
        return new Post(persist(ObjectsFactory.getDefaultUser()), "post content");
    }

    public static Topic getDefaultTopic() {
        JCUser user = persist(ObjectsFactory.getDefaultUser());
        Branch branch = ObjectsFactory.getDefaultBranch();
        Topic newTopic = new Topic(user, "topic title");
        Post post = new Post(user, "post content");
        newTopic.addPost(post);
        branch.addTopic(newTopic);
        persist(branch);
        return newTopic;
    }

    /**
     * Create the PrivateMessage with filled required fields.
     *
     * @return ready to save instance
     */
    public static PrivateMessage getDefaultPrivateMessage() {
        JCUser userTo = persist(ObjectsFactory.getUser("UserTo", "mail2@mail.com"));
        JCUser userFrom = persist(ObjectsFactory.getUser("UserFrom", "mail1@mail.com"));
        return new PrivateMessage(userTo, userFrom,
                "Message title", "Private message body");
    }

    public static List<Post> createAndSavePostList(int size) {
        List<Post> posts = new ArrayList<Post>();
        Topic topic = PersistedObjectFactory.getDefaultTopic();
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

    private static <T> T persist(T entity) {
        session.save(entity);
        return entity;
    }

}
