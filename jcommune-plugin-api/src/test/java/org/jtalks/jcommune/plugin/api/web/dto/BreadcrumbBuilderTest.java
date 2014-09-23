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
package org.jtalks.jcommune.plugin.api.web.dto;

import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.web.util.BreadcrumbBuilder;
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
    private BreadcrumbBuilder breadcrumbBuilder;
    private Section section;
    private Branch branch;
    private Topic topic;
    private Post post;


    @BeforeMethod
    public void setUp() throws Exception {
        breadcrumbBuilder = new BreadcrumbBuilder();

        JCUser user = new JCUser("user", "mail@mail.com", "password");

        section = new Section("Section Name");
        section.setId(ID);

        branch = new Branch("Branch Name", "Branch description");
        branch.setId(ID);
        section.addOrUpdateBranch(branch);
        branch.setSection(section);

        topic = new Topic(user, "Topic Name");
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
                BreadcrumbLocation.FORUM, Breadcrumb.ROOT_BREADCRUMB_LOCATION_VALUE);
        List<Breadcrumb> expectedResult = new ArrayList<>();
        expectedResult.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualResult = breadcrumbBuilder.getForumBreadcrumb();

        //check result
        assertBreadcrumbsAreEqual(actualResult, expectedResult);

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
        assertBreadcrumbsAreEqual(actualList, expectedList);
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
        assertBreadcrumbsAreEqual(actualList, expectedList);
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
        assertBreadcrumbsAreEqual(actualList, expectedList);
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
        assertBreadcrumbsAreEqual(actualList, expectedList);
    }

    @Test
    public void testLongBreadcrumbValue() {
        String longOne = "this is a long string to be shortened by finely trained code chunk";
        Breadcrumb breadcrumb = new Breadcrumb(ID, BreadcrumbLocation.BRANCH, longOne);

        assertEquals(breadcrumb.getValue(), "this is a long string to be shortened...");
    }

    private void assertBreadcrumbsAreEqual(List<Breadcrumb> actualList, List<Breadcrumb> expectedList) {
        if (actualList.size() == expectedList.size()) {
            for (int i = 0; i < actualList.size(); i++) {
                Breadcrumb actual = actualList.get(i);
                Breadcrumb expected = expectedList.get(i);
                assertEquals(actual.getId(), expected.getId());
                assertEquals(actual.getBreadcrumbLocation(), expected.getBreadcrumbLocation());
                assertEquals(actual.getValue(), expected.getValue());
            }
        } else {
            fail("Actual and expect breadcrumbs must be of the same length");
        }
    }
}
