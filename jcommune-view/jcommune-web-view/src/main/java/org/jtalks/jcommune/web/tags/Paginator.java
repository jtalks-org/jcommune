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

package org.jtalks.jcommune.web.tags;

import org.jtalks.jcommune.web.util.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.List;

/**
 * Class for custom tag jtalks:display
 *
 * @author Andrey Kluev
 */
public class Paginator extends BodyTagSupport {
    private String uri;
    private int numberLink = DEFAULT_LINK_COUNT;
    private List list;
    private transient Pagination pagination;
    private static final String LINK_PATTERN = "<a href=\"%s?page=%d\">%d</a>      ";

    private static final Logger LOGGER = LoggerFactory.getLogger(Paginator.class);

    public static final int DEFAULT_LINK_COUNT = 3;

    private static final long serialVersionUID = 1L;

    /**
     * @param uri uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @param list list of elements
     */
    public void setList(List list) {
        this.list = list;
    }

    /**
     * @return list
     */
    public List getList() {
        return this.list;
    }

    @Override
    public int doStartTag() {

        if (!pagination.isPagingEnabled()) {
            pagination.setPageSize(list.size());
        }

        if (!list.isEmpty()) {
            if (pagination.isLastPages() && !pagination.isRounded()) {
                pageContext.setAttribute("list", pagination.notIntegerNumberOfPages(list));
            } else {
                pageContext.setAttribute("list", pagination.integerNumberOfPages(list));
            }
        }
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() {
        JspWriter out = pageContext.getOut();
        try {
            out.write(pagination.createPagingLink(numberLink, LINK_PATTERN, uri));
        } catch (IOException e) {
            LOGGER.error("There was an error writing formed links for paging!", e);
        }
        return EVAL_PAGE;
    }

    /**
     * @param pagination called by JSP engine
     */
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    /**
     * @return numberLink number link of paging
     */
    public int getNumberLink() {
        return numberLink;
    }

    /**
     * @param numberLink number link of paging
     */
    public void setNumberLink(int numberLink) {
        this.numberLink = numberLink;
    }
}
