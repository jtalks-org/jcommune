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

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.springframework.data.domain.Page;

/**
 * Class for custom tag jtalks:pagination
 * todo: refactor it
 *
 * @author Andrey Kluev
 */
public class Paginator extends BodyTagSupport {
    private static final long serialVersionUID = 1L;
    private static final String LINK_PATTERN = "<li><a href='%s?page=%d'>%d</a></li>";
    private static final String CURRENT_LINK_PATTERN = "<li class='active'><a href='#'>%d</a></li>";
    public static final int DEFAULT_LINK_COUNT = 7;
    
    private String uri;
    private int numberLink = DEFAULT_LINK_COUNT;
    private Page<?> page;
    private boolean pagingEnabled;
    
    /**
     * @param uri uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() {
        pageContext.setAttribute("list", page.getContent());
        return EVAL_BODY_INCLUDE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.write(this.createPagingLink(numberLink, uri));
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    /**
     * @param numberLink number of links on pages
     * @param uri        uri
     * @return completed links
     */
    public String createPagingLink(int numberLink,  String uri) {
        int page = this.page.getNumber();
        StringBuffer buffer = new StringBuffer();
        if (pagingEnabled) {
            for (int i = numberLink; i > 0; i--) {
                if (page > i) {
                    buffer.append(String.format(LINK_PATTERN, uri, page - i, page - i));
                }
            }
            if (this.page.getTotalPages() > 1) {
                buffer.append(String.format(CURRENT_LINK_PATTERN, page));
            }
            for (int i = 0; i < numberLink; i++) {
                if (page + i < this.page.getTotalPages()) {
                    buffer.append(String.format(LINK_PATTERN, uri, page + i + 1, page + i + 1));
                }
            }
        }
        return buffer.toString();
    }
    
    /**
     * 
     * @param page
     */
    public void setPage(Page<?> page) {
        this.page = page;
    }
    
    /**
     * 
     * @param pagingEnabled
     */
    public void setPagingEnabled(boolean pagingEnabled) {
        this.pagingEnabled = pagingEnabled;
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
