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
package org.jtalks.jcommune.web.dto;

import org.jtalks.jcommune.model.entity.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author Alexandre Teterin
 */
public class BreadcrumbBuilderTest {
    private final Long ID = 1L;
    private final String USER_NAME = "user";
    private final String EMAIL = "mail@mail.com";
    private final String PASSWORD = "password";
    private final String SECTION_NAME = "Section Name";
    private final String BRANCH_NAME = "Branch Name";
    private final String TOPIC_NAME = "Topic Name";
    private BreadcrumbBuilder breadcrumbBuilder;
    private User user;
    private Section section;
    private Branch branch;
    private Topic topic;
    private Post post;


    @BeforeMethod
    public void setUp() throws Exception {
        breadcrumbBuilder = new BreadcrumbBuilder();

        user = new User(USER_NAME, EMAIL, PASSWORD);

        section = new Section(SECTION_NAME);
        section.setId(ID);

        branch = new Branch(BRANCH_NAME);
        branch.setId(ID);
        section.addBranch(branch);

        topic = new Topic(user, TOPIC_NAME);
        topic.setId(ID);
        branch.addTopic(topic);

        post = new Post(user, "");
        topic.addPost(post);
        post.setId(ID);
    }

    @Test
    public void testGetForumBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(Breadcrumb.STUB_BREADCRUMB_ID,
                BreadcrumbLocation.FORUM,  Breadcrumb.ROOT_BREADCRUMB_LOCATION_VALUE);
        List<Breadcrumb> expectedResult = new ArrayList<Breadcrumb>();
        expectedResult.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualResult = breadcrumbBuilder.getForumBreadcrumb();

        //check result
        assertBreadcrumbs(actualResult, expectedResult);

    }

    @Test
    public void testGetBranchBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(ID, BreadcrumbLocation.SECTION, section.getName());
        List<Breadcrumb> expectedList = breadcrumbBuilder.getForumBreadcrumb();
        expectedList.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualList = breadcrumbBuilder.getForumBreadcrumb(branch);

        //check result
        assertBreadcrumbs(actualList, expectedList);
    }

    @Test
    public void testGetTopicBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(ID, BreadcrumbLocation.BRANCH, branch.getName());
        List<Breadcrumb> expectedList = breadcrumbBuilder.getForumBreadcrumb(branch);
        expectedList.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualList = breadcrumbBuilder.getForumBreadcrumb(topic);

        //check result
        assertBreadcrumbs(actualList, expectedList);
    }

    @Test
    public void testGetNewTopicBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(ID, BreadcrumbLocation.BRANCH, branch.getName());
        List<Breadcrumb> expectedList = breadcrumbBuilder.getForumBreadcrumb(branch);
        expectedList.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualList = breadcrumbBuilder.getNewTopicBreadcrumb(branch);

        //check result
        assertBreadcrumbs(actualList, expectedList);
    }

    @Test
    public void testGetPostBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(ID, BreadcrumbLocation.TOPIC, topic.getTitle());
        List<Breadcrumb> expectedList = breadcrumbBuilder.getForumBreadcrumb(topic);
        expectedList.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualList = breadcrumbBuilder.getForumBreadcrumb(post);

        //check result
        assertBreadcrumbs(actualList, expectedList);
    }

    private void assertBreadcrumbs(List<Breadcrumb> actualList, List<Breadcrumb> expectedList) {
        if (actualList.size() == expectedList.size()) {
            for (int i = 0; i < actualList.size(); i++) {
                Breadcrumb actual = actualList.get(i);
                Breadcrumb expected = expectedList.get(i);
                assertEquals(actual.getId(), expected.getId());
                assertEquals(actual.getBreadcrumbLocation(), expected.getBreadcrumbLocation());
                assertEquals(actual.getBreadcrumbLocationValue(), expected.getBreadcrumbLocationValue());
            }
        } else {
            fail("Actual and expect breadcrumbs must be of the same length");
        }
    }
}
