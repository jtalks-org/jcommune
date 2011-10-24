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
    private final String POST_NAME = "Post Name";

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
        branch.setSection(section);

        topic = new Topic(user, branch, TOPIC_NAME);
        topic.setId(ID);

        post = new Post(topic);
        post.setId(ID);
    }

    @Test
    public void testGetForumBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(
                Breadcrumb.STUB_BREADCRUMB_ID,
                Breadcrumb.BreadcrumbLocation.FORUM,
                Breadcrumb.ROOT_BREADCRUMB_LOCATION_VALUE);
        List<Breadcrumb> expectedResult = new ArrayList<Breadcrumb>();
        expectedResult.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualResult = breadcrumbBuilder.getForumBreadcrumb();

        //check result
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testGetBranchBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(
                ID,
                Breadcrumb.BreadcrumbLocation.SECTION,
                section.getName());
        List<Breadcrumb> expectedList = breadcrumbBuilder.getForumBreadcrumb();
        expectedList.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualList = breadcrumbBuilder.getForumBreadcrumb(branch);

        //check result
        assertEquals(actualList, expectedList);
    }

    @Test
    public void testGetTopicBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(
                ID,
                Breadcrumb.BreadcrumbLocation.BRANCH,
                branch.getName());
        List<Breadcrumb> expectedList = breadcrumbBuilder.getForumBreadcrumb(branch);
        expectedList.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualList = breadcrumbBuilder.getForumBreadcrumb(topic);

        //check result
        assertEquals(actualList, expectedList);
    }

    @Test
    public void testGetNewTopicBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(
                ID,
                Breadcrumb.BreadcrumbLocation.BRANCH,
                branch.getName());
        List<Breadcrumb> expectedList = breadcrumbBuilder.getForumBreadcrumb(branch);
        expectedList.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualList = breadcrumbBuilder.getNewTopicBreadcrumb(branch);

        //check result
        assertEquals(actualList, expectedList);
    }

    @Test
    public void testGetPostBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(
                ID,
                Breadcrumb.BreadcrumbLocation.TOPIC,
                topic.getTitle());
        List<Breadcrumb> expectedList = breadcrumbBuilder.getForumBreadcrumb(topic);
        expectedList.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualList = breadcrumbBuilder.getForumBreadcrumb(post);

        //check result
        assertEquals(actualList, expectedList);
    }

    @Test
    public void testGetInboxBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(
                Breadcrumb.STUB_BREADCRUMB_ID,
                Breadcrumb.BreadcrumbLocation.INBOX,
                Breadcrumb.INBOX_BREADCRUMB_LOCATION_VALUE);
        List<Breadcrumb> expectedList = breadcrumbBuilder.getForumBreadcrumb();
        expectedList.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualList = breadcrumbBuilder.getInboxBreadcrumb();

        //check result
        assertEquals(actualList, expectedList);
    }

    @Test
    public void testGetOutboxBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(
                Breadcrumb.STUB_BREADCRUMB_ID,
                Breadcrumb.BreadcrumbLocation.OUTBOX,
                Breadcrumb.OUTBOX_BREADCRUMB_LOCATION_VALUE);
        List<Breadcrumb> expectedList = breadcrumbBuilder.getForumBreadcrumb();
        expectedList.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualList = breadcrumbBuilder.getOutboxBreadcrumb();

        //check result
        assertEquals(actualList, expectedList);
    }

    @Test
    public void testGetDraftsBreadcrumb() throws Exception {
        //init
        Breadcrumb expectedBreadcrumb = new Breadcrumb(
                Breadcrumb.STUB_BREADCRUMB_ID,
                Breadcrumb.BreadcrumbLocation.DRAFTS,
                Breadcrumb.DRAFTS_BREADCRUMB_LOCATION_VALUE);
        List<Breadcrumb> expectedList = breadcrumbBuilder.getForumBreadcrumb();
        expectedList.add(expectedBreadcrumb);

        //invoke the object under test
        List<Breadcrumb> actualList = breadcrumbBuilder.getDraftsBreadcrumb();

        //check result
        assertEquals(actualList, expectedList);
    }
}
