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
package org.jtalks.jcommune.model.entity;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.jtalks.common.model.entity.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Max Malakhov
 * @author Eugeny Batov
 * @author masyan
 */
public final class PersistedObjectsFactory {
    //todo: refactor this class without using static
    //because static will affect our tests if we will want run it in some threads
    private static Session session;

    private PersistedObjectsFactory() {
    }

    public static void setSession(Session session) {
        PersistedObjectsFactory.session = session;
    }

    public static Branch getDefaultBranch() {
        Branch branch = ObjectsFactory.getDefaultBranch();
        return persist(branch);
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

    public static void createAndSaveViewTopicsBranchesEntity(Long branchId, String sid, Boolean granting) {
        ViewTopicsBranches viewTopicsBranches = new ViewTopicsBranches();
        viewTopicsBranches.setBranchId(branchId);
        viewTopicsBranches.setSid(sid);
        viewTopicsBranches.setGranting(granting);

        session.save(viewTopicsBranches);
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

    /**
     * Create and persist one single message.
     *
     * @param status message status.
     * @return saved pm.
     */
    public static PrivateMessage createAndSaveMessage(PrivateMessageStatus status, JCUser userTo,
                                                      JCUser userFrom) {
        PrivateMessage pm = new PrivateMessage(userTo, userFrom,
                "Message title", "Private message body");
        pm.setStatus(status);
        persist(pm);
        return pm;
    }

    /**
     * Create and persist list of private messages with all possible statuses.
     *
     * @param size message number for DRAFT status. For other statuses message number is size / 2.
     * @return saved pm list.
     */
    public static List<PrivateMessage> preparePrivateMessages(int size, JCUser userTo,
                                                              JCUser userFrom) {
        List<PrivateMessage> messages = new ArrayList<PrivateMessage>(size);
        for (int i = 0; i < size; i++) {
            PrivateMessage pm = new PrivateMessage(userTo, userFrom,
                    "Message title", "Private message body");
            if (i % 2 == 0) {
                pm.setStatus(PrivateMessageStatus.SENT);
            } else {
                pm.setStatus(PrivateMessageStatus.NEW);
            }
            messages.add(pm);
            persist(pm);
        }
        for (int i = 0; i < size; i++) {
            PrivateMessage pm = new PrivateMessage(userTo, userFrom,
                    "Message title", "Private message body");
            if (i % 2 == 0) {
                pm.setStatus(PrivateMessageStatus.DELETED_FROM_OUTBOX);
            } else {
                pm.setStatus(PrivateMessageStatus.DELETED_FROM_INBOX);
            }
            messages.add(pm);
            persist(pm);
        }
        for (int i = 0; i < size; i++) {
            PrivateMessage pm = new PrivateMessage(userTo, userFrom,
                    "Message title", "Private message body");
            pm.setStatus(PrivateMessageStatus.DRAFT);
            messages.add(pm);
            persist(pm);
        }
        return messages;
    }

    public static List<Topic> createAndSaveTopicList(int size) {
        org.jtalks.jcommune.model.entity.Branch branch = ObjectsFactory.getDefaultBranch();
        JCUser user = persist(ObjectsFactory.getDefaultUser());
        for (int i = 0; i < size; i++) {
            Topic topic = new Topic(user, "title" + i);
            topic.addPost(new Post(user, "post_context" + i));
            branch.addTopic(topic);
        }
        persist(branch);
        return branch.getTopics();
    }

    /**
     * Create the Topics with posts.
     *
     * @return saved topics
     */
    public static List<Topic> createAndSaveTopicListWithPosts(int size) {
        org.jtalks.jcommune.model.entity.Branch branch = ObjectsFactory.getDefaultBranch();
        JCUser user = persist(ObjectsFactory.getRandomUser());
        for (int i = 0; i < size; i++) {
            Topic topic = new Topic(user, "title" + i);
            topic.addPost(new Post(topic.getTopicStarter(), "content"));
            branch.addTopic(topic);
        }
        persist(branch);
        return branch.getTopics();
    }

    public static List<Post> createAndSavePostList(int size) {
        List<Post> posts = new ArrayList<Post>();
        Topic topic = PersistedObjectsFactory.getDefaultTopic();
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
        return new LastReadPost(user, topic, new DateTime());
    }

    public static Poll createDefaultVoting() {
        Topic topic = getDefaultTopic();
        Poll poll = new Poll("New voting");
        List<PollItem> pollItems = new ArrayList<PollItem>();
        pollItems.add(new PollItem("item1"));
        pollItems.add(new PollItem("item2"));
        pollItems.add(new PollItem("item3"));
        poll.setPollItems(pollItems);
        topic.setPoll(poll);
        poll.setTopic(topic);
        persist(topic);
        return poll;
    }

    public static PollItem createDefaultVotingOption() {
        Poll voting = createDefaultVoting();
        persist(voting);
        PollItem option = new PollItem("First voting option");
        voting.addPollOptions(option);
        return option;
    }

    public static JCUser getDefaultUser() {
        return getUser("user", "email@user.org");
    }

    public static JCUser getUser(String username, String mail) {
        JCUser user = new JCUser(username, mail, "user");
        persist(user);
        return user;
    }

    public static Group group(String name) {
        Group group = new Group(name, "default-group-description");
        session.save(group);
        session.flush();
        session.evict(group);
        return group;
    }

    public static Group groupWithUsers(String name) {
        Group group = new Group(name, "default-group-description");
        group.getUsers().add(getDefaultUser());
        session.save(group);
        session.flush();
        session.evict(group);
        return group;
    }

    public static JCUser getDefaultUserWithGroups() {
        List<Group> groups = ObjectsFactory.getDefaultGroupList();
        for (Group group : groups) {
            persist(group);
        }
        JCUser user = ObjectsFactory.getDefaultUser();
        user.setGroups(groups);
        persist(user);

        return user;
    }

    public static Topic getCodeReviewTopic() {
        Topic topic = getDefaultTopic();
        CodeReview review = new CodeReview();
        topic.setCodeReview(review);
        review.setTopic(topic);
        persist(topic);
        return topic;
    }

    /**
     * Create code review with two comments and persist it to session
     *
     * @return persisted code review entity
     */
    public static CodeReview getDefaultCodeReview() {
        CodeReview review = new CodeReview();
        persist(review);

        CodeReviewComment comment1 = new CodeReviewComment();
        comment1.setAuthor(getUser("user1", "mail1@mail.ru"));
        comment1.setBody("Comment1 body");
        comment1.setLineNumber(1);
        comment1.setCreationDate(new DateTime(1));
        review.addComment(comment1);
        persist(comment1);

        CodeReviewComment comment2 = new CodeReviewComment();
        comment2.setAuthor(getUser("user2", "mail2@mail.ru"));
        comment2.setBody("Comment2 body");
        comment2.setLineNumber(2);
        comment2.setCreationDate(new DateTime(2));
        review.addComment(comment2);
        persist(comment2);


        return review;
    }

    /**
     * Return first code review comment from persisted code review.
     *
     * @return first code review comment from persisted code review.
     */
    public static CodeReviewComment getDefaultCodeReviewComment() {
        return getDefaultCodeReview().getComments().get(0);
    }

    public static Component getDefaultComponent() {
        Component component = new Component();
        component.setName("component.name");
        component.setDescription("component.description");
        component.setComponentType(ComponentType.FORUM);

        List<Property> properties = new ArrayList<Property>();
        Property property1 = new Property("name1", "value1");
        property1.setValidationRule("validationRule1");
        properties.add(property1);

        Property property2 = new Property("name2", "value2");
        property2.setValidationRule("validationRule2");
        properties.add(property2);

        component.setProperties(properties);

        persist(component);
        return component;
    }

    public static PluginConfiguration getDefaultPluginConfiguration() {
        PluginConfiguration configuration = new PluginConfiguration("Default name", true, Collections.<PluginProperty>emptyList());
        persist(configuration);
        return configuration;
    }

    public static PluginProperty getDefaultPluginConfigurationProperty() {
        PluginProperty property = new PluginProperty("Property", PluginProperty.Type.BOOLEAN, "true");
        PluginConfiguration configuration = new PluginConfiguration("Default name", true, Arrays.asList(property));
        property.setPluginConfiguration(configuration);
        persist(configuration);
        return property;
    }

    public static void createViewUnreadPostsInBranch() {
        session.createSQLQuery("CREATE VIEW COUNT_POSTS_TOPICS_VIEW AS SELECT tp.TOPIC_ID, tp.BRANCH_ID," +
                " MAX(POST_DATE) as LAST_POST_DATE FROM TOPIC tp join POST p ON p.TOPIC_ID=tp.TOPIC_ID group by tp.TOPIC_ID")
                .executeUpdate();
    }

    /**
     * Used in manual rollback
     */
    public static void deleteViewUnreadPostsInBranch() {
        session.createSQLQuery("DROP VIEW COUNT_POSTS_TOPICS_VIEW")
                .executeUpdate();
    }

    private static <T> T persist(T entity) {
        session.save(entity);
        return entity;
    }

    /**
     * @return group with randoms users
     */
    public static Group groupWithUsers(int count) {
        List<JCUser> users = usersListOf(count);
        Group group = ObjectsFactory.getRandomGroup();
        group.setUsers((List<User>) (Object) users);
        return persist(group);
    }

    public static List<JCUser> usersListOf(int n) {
        List<JCUser> result = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            result.add(persist(ObjectsFactory.getRandomUser()));
        }
        return result;
    }

}
