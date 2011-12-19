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
package org.jtalks.jcommune.web.util;

import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.nontransactional.LocationServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for pagination.
 *
 * @author Kirill Afonin
 * @author Andrey Kluev
 */
public class Pagination {
    private int page;
    private int pageSize = User.DEFAULT_PAGE_SIZE;
    private int itemsCount;
    private boolean pagingEnabled;

    /**
     * Create instance.
     *
     * @param page          page (default 1)
     * @param currentUser   current user
     * @param itemsCount    total number of items
     * @param pagingEnabled paging status
     */
    public Pagination(Integer page, User currentUser, int itemsCount, boolean pagingEnabled) {
        this.page = page;
        this.pageSize = Pagination.getPageSizeFor(currentUser);
        this.itemsCount = itemsCount;
        this.pagingEnabled = pagingEnabled;
    }


    /**
     * @return page number of current page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * todo: looks odd, do we necessary need it?
     *
     * @param pageSize number of items on the page
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return number of items on the page
     */
    public int getPageSize() {
        return pageSize;
    }


    /**
     * Returns page count.
     *
     * @return page count
     */
    private int getPageCount() {
        return itemsCount / pageSize;
    }

    /**
     * @return total number of pages
     */
    public int getMaxPages() {
        int maxPages = isRounded() ? getPageCount() : getPageCount() + 1;
        if (maxPages == 0) {
            maxPages = 1;
        }
        return maxPages;

    }

    /**
     * @return {@code true} if number of pages rounded else {@code false}
     */
    public boolean isRounded() {
        return (itemsCount % pageSize) == 0;
    }

    /**
     * @return true if current page is last
     */
    public boolean isLastPages() {
        return page == getMaxPages();
    }

    /**
     * @return pagingEnabled is flag for the button Show all/Show pages
     */
    public boolean isPagingEnabled() {
        return pagingEnabled;
    }

    /**
     * @param numberLink number of links on pages
     * @param link       pattern of building links
     * @param uri        uri
     * @return completed links
     */
    public String createPagingLink(int numberLink, String link, String uri) {
        StringBuffer buffer = new StringBuffer();
        if (isPagingEnabled()) {
            for (int i = numberLink; i > 0; i--) {
                if (getPage() > i) {
                    buffer.append(String.format(link, uri, getPage() - i, getPage() - i));
                }
            }
            if (getMaxPages() > 1) {
                buffer.append(getPage());
                buffer.append("      ");
            }
            for (int i = 0; i < numberLink; i++) {
                if (getPage() + i < getMaxPages()) {
                    buffer.append(String.format(link, uri, getPage() + i + 1, getPage() + i + 1));
                }
            }
        }
        return buffer.toString();
    }

    /**
     * used if the total number of items
     * divided by the number of elements on a page without a trace
     *
     * @param list list of items
     * @return list new list of items
     */
    public List integerNumberOfPages(List list) {
        return list.subList((getPage() - 1) * pageSize, getPage() * pageSize);
    }

    /**
     * used if the total number of items
     * divided by the number of elements on the page with the remainder
     *
     * @param list list of items
     * @return list new list of items
     */
    public List notIntegerNumberOfPages(List list) {
        return list.subList((getPage() - 1) * pageSize,
                (getPage() - 1) * pageSize + list.size() % pageSize);
    }

    /**
     * Returns page size applicable for the current user. If for some reasons
     * this implementation is unable to determaine this parameter the default
     * value will be used.
     *
     * @param user current user representation, may be null
     * @return page size for the current user or default if there is no user
     */
    public static int getPageSizeFor(User user) {
        return (user == null) ? User.DEFAULT_PAGE_SIZE : user.getPageSize();
    }

    /**
     * Specifies the page number on which the post
     *
     * @param post post
     * @return page number on which the post
     */
    public int definitionPostInTopic(Post post) {
        int number = post.getTopic().getPosts().indexOf(post);
        int res;
        for (res = 1; res * pageSize <= number; res++) {
        }
        return res;
    }

    /**
     *
     * @param locationServiceImpl locationService
     * @param currentUser current user
     * @param entity entity
     * @param forumStatisticsProvider forumStatisticsProvider
     * @return lis name user active these page
     */
    public List<String> activeRegistryUserList(LocationServiceImpl locationServiceImpl,
                                               User currentUser, Entity entity,
                                               ForumStatisticsProvider forumStatisticsProvider) {
        Map globalUserMap = locationServiceImpl.getRegisterUserMap();
        globalUserMap.put(currentUser, entity.getUuid());

        Map<User, String> innerMap = new HashMap<User, String>();
        List<String> viewList = new ArrayList<String>();
        for (Object o : forumStatisticsProvider.getOnlineRegisteredUsers()) {
            User user = (User) o;
            if (globalUserMap.containsKey(user) && globalUserMap.get(user).equals(entity.getUuid())) {
                innerMap.put(user, entity.getUuid());
                viewList.add(user.getEncodedUsername());
            }
        }
        locationServiceImpl.setRegisterUserMap(innerMap);
        return viewList;
    }
}
