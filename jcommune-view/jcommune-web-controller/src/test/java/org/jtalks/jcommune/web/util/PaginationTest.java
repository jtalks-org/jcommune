package org.jtalks.jcommune.web.util;

import org.jtalks.jcommune.model.entity.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class PaginationTest {
    private Pagination pagination;
    private String link;
    private String uri;
    private User user;
    private List list;

    @BeforeMethod
    protected void setUp(){
        user = mock(User.class);
        when(user.getPageSize()).thenReturn("FIVE");
        uri = "1";
        link = "<a href=\"%s?page=%d\">%d</a>";
    }

    @Test
    public void testCreatePagingLink(){
        pagination = new Pagination(1, user,10,true);

        String comletedLinks = pagination.createPagingLink(5, link,uri);

        assertEquals(comletedLinks,"1      <a href=\"1?page=2\">2</a>");

        pagination = new Pagination(1, user,10,false);

        comletedLinks = pagination.createPagingLink(5, link,uri);

        assertEquals(comletedLinks,"");

        pagination = new Pagination(2, user,15,true);

        comletedLinks = pagination.createPagingLink(5, link,uri);

        assertEquals(comletedLinks,"<a href=\"1?page=1\">1</a>2      <a href=\"1?page=3\">3</a>");
    }

    @Test
    public void testNumberOfPages(){
        pagination = new Pagination(1, user,10,true);

        list = Collections.nCopies(10, 1);

        List lists = pagination.integerNumberOfPages(list);

        assertEquals(lists,list.subList(0,5));

        pagination = new Pagination(2, user,10,true);

        list = Collections.nCopies(7, 1);

        lists = pagination.notIntegerNumberOfPages(list);

        assertEquals(lists,list.subList(5,7));

    }
}
