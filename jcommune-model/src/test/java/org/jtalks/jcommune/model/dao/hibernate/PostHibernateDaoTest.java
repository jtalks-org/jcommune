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
package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Kirill Afonin
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class PostHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    private static final String PAGE_NUMBER_TOO_LOW = "0";
    private static final String PAGE_NUMBER_TOO_BIG = "1000";
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PostDao dao;
    private Session session;

    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void testGet() {
        Post post = PersistedObjectsFactory.getDefaultPost();
        session.save(post);

        Post result = dao.get(post.getId());

        assertNotNull(result);
        assertEquals(result.getId(), post.getId());
    }

    @Test
    public void testGetInvalidId() {
        Post result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newContent = "new content";
        Post post = PersistedObjectsFactory.getDefaultPost();
        session.save(post);
        post.setPostContent(newContent);

        dao.saveOrUpdate(post);
        session.flush();
        session.evict(post);
        Post result = (Post) session.get(Post.class, post.getId());

        assertEquals(result.getPostContent(), newContent);
    }

    @Test(expectedExceptions = org.hibernate.exception.ConstraintViolationException.class)
    public void testUpdateNotNullViolation() {
        Post post = PersistedObjectsFactory.getDefaultPost();
        session.save(post);
        post.setPostContent(null);
        dao.saveOrUpdate(post);
        session.flush();
    }

    /* PostDao specific methods */

    @Test
    public void testPostOfUser() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;

        PageRequest pageRequest = new PageRequest("1", pageSize);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(totalSize);
        JCUser author = posts.get(0).getUserCreated();
        List<Long> allowedBranchesIds = new ArrayList<>();
        allowedBranchesIds.add(posts.get(0).getTopic().getBranch().getId());

        Page<Post> postsPage = dao.getUserPosts(author, pageRequest, allowedBranchesIds);

        assertEquals(postsPage.getContent().size(), pageSize, "Incorrect count of posts in one page.");
        assertEquals(postsPage.getTotalElements(), totalSize, "Incorrect total count.");
        assertEquals(postsPage.getTotalPages(), pageCount, "Incorrect count of pages.");
    }

    @Test
    public void testPostOfUserWithForbiddenBranchesIds() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;

        PageRequest pageRequest = new PageRequest("1", pageSize);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(totalSize);
        JCUser author = posts.get(0).getUserCreated();
        List<Long> allowedBranchesIds = new ArrayList<>();
        allowedBranchesIds.add(-1L);

        Page<Post> postsPage = dao.getUserPosts(author, pageRequest, allowedBranchesIds);

        assertEquals(postsPage.getContent().size(), 0, "Incorrect count of posts in one page.");
        assertEquals(postsPage.getTotalElements(), 0, "Incorrect total count.");
        assertEquals(postsPage.getTotalPages(), 0, "Incorrect count of pages.");
    }

    @Test
    public void testReturnedUserPostListIsSortedInDescOrderByDate() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;

        PageRequest pageRequest = new PageRequest("1", pageSize);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(totalSize);
        JCUser author = posts.get(0).getUserCreated();
        List<Long> allowedBranchesIds = new ArrayList<>();
        allowedBranchesIds.add(posts.get(0).getTopic().getBranch().getId());

        Page<Post> postsPage = dao.getUserPosts(author, pageRequest, allowedBranchesIds);

        boolean asc = false;
        assertTrue(isPostListSortedByDate(postsPage.getContent(), asc));
    }


    private boolean isPostListSortedByDate(List<Post> postList, boolean asc) {
        boolean result = false;
        for (int i = 1; i < postList.size(); i++) {
            DateTime creationDatePrevious = postList.get(i - 1).getCreationDate();
            DateTime creationDate = postList.get(i).getCreationDate();
            if (asc) {
                result = creationDatePrevious.compareTo(creationDate) <= 0;
                if (!result) break;
            } else {
                result = creationDatePrevious.compareTo(creationDate) >= 0;
                if (!result) break;
            }
        }
        return result;
    }

    @Test
    public void testPostOfUserWithEnabledPagingPageLessTooLow() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;

        PageRequest pageRequest = new PageRequest(
                PAGE_NUMBER_TOO_LOW, pageSize);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(totalSize);
        JCUser author = posts.get(0).getUserCreated();
        List<Long> allowedBranchesIds = new ArrayList<>();
        allowedBranchesIds.add(posts.get(0).getTopic().getBranch().getId());

        Page<Post> postsPage = dao.getUserPosts(author, pageRequest, allowedBranchesIds);
        List<Post> postList = postsPage.getContent();
        for (int i = 1; i < postList.size(); i++) {
            DateTime creationDatePrevious = postList.get(i - 1).getCreationDate();
            DateTime creationDate = postList.get(i).getCreationDate();
            assertTrue(creationDatePrevious.compareTo(creationDate) >= 0);
        }

        assertEquals(postsPage.getContent().size(), pageSize, "Incorrect count of posts in one page.");
        assertEquals(postsPage.getTotalElements(), totalSize, "Incorrect total count.");
        assertEquals(postsPage.getTotalPages(), pageCount, "Incorrect count of pages.");
        assertEquals(postsPage.getNumber(), 1, "Incorrect page number");
    }

    @Test
    public void testPostOfUserWithEnabledPagingPageTooBig() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;

        PageRequest pageRequest = new PageRequest(
                PAGE_NUMBER_TOO_BIG, pageSize);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(totalSize);
        JCUser author = posts.get(0).getUserCreated();
        List<Long> allowedBranchesIds = new ArrayList<>();
        allowedBranchesIds.add(posts.get(0).getTopic().getBranch().getId());

        Page<Post> postsPage = dao.getUserPosts(author, pageRequest, allowedBranchesIds);

        assertEquals(postsPage.getContent().size(), pageSize, "Incorrect count of posts in one page.");
        assertEquals(postsPage.getTotalElements(), totalSize, "Incorrect total count.");
        assertEquals(postsPage.getTotalPages(), pageCount, "Incorrect count of pages.");
        assertEquals(postsPage.getNumber(), pageCount, "Incorrect page number");
    }

    @Test
    public void testNullPostOfUser() {
        PageRequest pageRequest = new PageRequest("1", 50);
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);
        List<Long> allowedBranchesIds = new ArrayList<>();
        allowedBranchesIds.add(1L);

        Page<Post> postsPage = dao.getUserPosts(user, pageRequest, allowedBranchesIds);

        assertFalse(postsPage.hasContent());
    }

    @Test
    public void testGetPostsWithEnabledPaging() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;
        PageRequest pageRequest = new PageRequest("1", pageSize);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(totalSize);
        Topic topic = posts.get(0).getTopic();

        Page<Post> postsPage = dao.getPosts(topic, pageRequest);

        assertEquals(postsPage.getContent().size(), pageSize, "Incorrect count of posts in one page.");
        assertEquals(postsPage.getTotalElements(), totalSize, "Incorrect total count.");
        assertEquals(postsPage.getTotalPages(), pageCount, "Incorrect count of pages.");
    }

    @Test
    public void testGetPostsWithEnabledPagingPageTooLow() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;
        PageRequest pageRequest = new PageRequest(PAGE_NUMBER_TOO_LOW, pageSize);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(totalSize);
        Topic topic = posts.get(0).getTopic();

        Page<Post> postsPage = dao.getPosts(topic, pageRequest);

        assertEquals(postsPage.getContent().size(), pageSize, "Incorrect count of posts in one page.");
        assertEquals(postsPage.getTotalElements(), totalSize, "Incorrect total count.");
        assertEquals(postsPage.getTotalPages(), pageCount, "Incorrect count of pages.");
        assertEquals(postsPage.getNumber(), 1, "Incorrect number of page");
    }

    @Test
    public void testGetPostsWithEnabledPagingPageTooBig() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize / pageCount;
        PageRequest pageRequest = new PageRequest(PAGE_NUMBER_TOO_BIG, pageSize);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(totalSize);
        Topic topic = posts.get(0).getTopic();

        Page<Post> postsPage = dao.getPosts(topic, pageRequest);

        assertEquals(postsPage.getContent().size(), pageSize, "Incorrect count of posts in one page.");
        assertEquals(postsPage.getTotalElements(), totalSize, "Incorrect total count.");
        assertEquals(postsPage.getTotalPages(), pageCount, "Incorrect count of pages.");
        assertEquals(postsPage.getNumber(), pageCount, "Incorrect number of page");
    }

    @Test
    public void testGetLastPostForBranch() {
        int size = 2;
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(size);
        Topic postsTopic = posts.get(0).getTopic();
        Branch postsBranch = postsTopic.getBranch();
        Post expectedLastPost = posts.get(0);
        ReflectionTestUtils.setField(
                expectedLastPost,
                "creationDate",
                new DateTime(2100, 12, 25, 0, 0, 0, 0));
        session.save(expectedLastPost);

        Post actualLastPost = dao.getLastPostFor(postsBranch);

        assertNotNull(actualLastPost, "Last post in the branch is not found.");
        assertEquals(actualLastPost.getId(), expectedLastPost.getId(),
                "The last post in the branch is the wrong.");
    }

    @Test
    public void getLastPostsForBranchShouldReturnLatestCreatedPosts() {
        int size = 42;
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(size);
        Topic postsTopic = posts.get(0).getTopic();
        Branch postsBranch = postsTopic.getBranch();

        ReflectionTestUtils.setField(
                posts.get(0),
                "creationDate",
                new DateTime(2101, 12, 25, 0, 0, 0, 0));
        session.save(posts.get(0));

        ReflectionTestUtils.setField(
                posts.get(1),
                "creationDate",
                new DateTime(2100, 12, 25, 0, 0, 0, 0));
        session.save(posts.get(1));

        List<Post> actualLastPosts = dao.getLastPostsFor(Arrays.asList(postsBranch.getId()), 2);

        assertEquals(actualLastPosts.size(), 2);
        assertEquals(actualLastPosts.get(0).getId(), posts.get(0).getId());
        assertEquals(actualLastPosts.get(1).getId(), posts.get(1).getId());
    }

    @Test
    public void testGetLastPostInEmptyBranch() {
        Topic topic = PersistedObjectsFactory.getDefaultTopic();
        Branch branch = topic.getBranch();
        branch.deleteTopic(topic);

        session.save(branch);

        assertNull(dao.getLastPostFor(branch),
                "The branch is empty, so last post mustn't be found");
    }

    @Test
    public void getLastPostsInEmptyBranchShouldReturnEmptyList() {
        Topic topic = PersistedObjectsFactory.getDefaultTopic();
        Branch branch = topic.getBranch();
        branch.deleteTopic(topic);

        session.save(branch);

        assertTrue(dao.getLastPostsFor(Arrays.asList(branch.getId()), 42).isEmpty(),
                "The branch is empty, so last posts mustn't be found");
    }
}
