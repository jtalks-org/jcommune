package org.jtalks.jcommune.model.entity;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Evgeniy Naumenko
 */
public class PostTest {

    private Post post;

    @BeforeMethod
    public void init() {
        Topic topic = new Topic(null, "header");
        post = new Post(null, "content");
        topic.addPost(post);
    }

    @Test
    public void testGetPostIndex() {
        assertEquals(0, post.getPostIndexInTopic());
    }

    @Test
    public void testUpdatePostModificationDate() throws InterruptedException {
        post.updateModificationDate();
        DateTime prevDate = post.getModificationDate();
        Thread.sleep(25); // to catch the date difference

        post.updateModificationDate();

        assertTrue(post.getModificationDate().isAfter(prevDate));
    }
}
