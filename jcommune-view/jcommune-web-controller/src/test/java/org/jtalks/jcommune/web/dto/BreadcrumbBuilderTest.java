package org.jtalks.jcommune.web.dto;

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
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

}
