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
package org.jtalks.jcommune.plugin.questionsandanswers;

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Mikhail Stryzhonok
 */
public class QuestionSubscribersFilterTest {

    @Test
    public void questionSubscribersFilterShouldNotChangeRecepientsIfBranchSubscribersFilters() {
        Branch branch = new Branch("name", "description");
        JCUser user1 = new JCUser("name1", "email1@example.com", "pwd1");
        JCUser user2 = new JCUser("name2", "email2@example.com", "pwd2");
        QuestionSubscribersFilter filter = new QuestionSubscribersFilter();
        List<JCUser> users = new ArrayList<>(Arrays.asList(user1, user2));

        filter.filter(users, branch);

        assertEquals(users.size(), 2);
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    public void questionSubscribersFilterShouldNotChangeRecepientsIfTopicSubscribersFilters() {
        Topic topic = new Topic();
        JCUser user1 = new JCUser("name1", "email1@example.com", "pwd1");
        JCUser user2 = new JCUser("name2", "email2@example.com", "pwd2");
        QuestionSubscribersFilter filter = new QuestionSubscribersFilter();
        List<JCUser> users = new ArrayList<>(Arrays.asList(user1, user2));


        filter.filter(users, topic);

        assertEquals(users.size(), 2);
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    public void questionSubscribersFilterShouldNotChangeRecepientsIfPostFomTopicWithOtherType() {
        Post post = getInTopicWithType("Some amazing topic");
        JCUser user1 = new JCUser("name1", "email1@example.com", "pwd1");
        JCUser user2 = new JCUser("name2", "email2@example.com", "pwd2");
        QuestionSubscribersFilter filter = new QuestionSubscribersFilter();
        List<JCUser> users = new ArrayList<>(Arrays.asList(user1, user2));

        filter.filter(users, post);

        assertEquals(users.size(), 2);
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    public void questionSubscribersFilterShouldLeaveOnlyPostAuthorInIfPostSubscribersFilters() {
        Post post = getInTopicWithType(QuestionsAndAnswersPlugin.TOPIC_TYPE);
        JCUser user1 = new JCUser("name1", "email1@example.com", "pwd1");
        JCUser user2 = new JCUser("name2", "email2@example.com", "pwd2");
        QuestionSubscribersFilter filter = new QuestionSubscribersFilter();
        List<JCUser> users = new ArrayList<>(Arrays.asList(user1, user2, post.getUserCreated()));

        filter.filter(users, post);

        assertEquals(users.size(), 1);
        assertTrue(users.contains(post.getUserCreated()));
    }

    private Post getInTopicWithType(String type) {
        JCUser author = new JCUser("authorName", "authoremail@example.com", "authorpwd");
        Post post = new Post(author, "text");
        Topic topic = new Topic();
        topic.setType(type);
        post.setTopic(topic);
        return post;
    }
}
