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

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Formatter;
import java.util.List;

/**
 * Class for custom tag.
 *
 * @author Andrey Kluev
 */
public class Paginator extends BodyTagSupport {
    private String uri;
    private int currentPage;
    private int numberElement;
    private List list;
    private int maxPages;

    /**
     * @param itemCount     total number of elements
     * @param numberElement number of pages
     * @return maxPage
     */
    public int getMaxPage(int itemCount, int numberElement) {
        if (itemCount % numberElement == 0) {
            this.maxPages = itemCount / numberElement;
        } else {
            this.maxPages = (itemCount / numberElement) + 1;
        }
        return this.maxPages;
    }

    /**
     * @param maxPages maximum number of pages
     */
    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }

    /**
     * @return uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @param currentPage current page number
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * @return currentPage
     */
    public int getCurrentPage() {
        return this.currentPage;
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

    /**
     * @return numberElement
     */
    public int getNumberElement() {
        return numberElement;
    }

    /**
     * @param numberElement number of elements
     */
    public void setNumberElement(int numberElement) {
        this.numberElement = numberElement;
    }

    @Override
    public int doStartTag() {
        pageContext.setAttribute("currentPage", currentPage);
        getMaxPage(list.size(), numberElement);
        if (list.size() >= 1) {

            if (currentPage == maxPages && list.size() % numberElement != 0) {
                int x = list.size() - (list.size() / numberElement) * numberElement;
                list = list.subList((currentPage - 1) * numberElement, (currentPage - 1) * numberElement + x);
            } else {
                list = list.subList((currentPage - 1) * numberElement, currentPage * numberElement);
            }
        }
        pageContext.setAttribute("list", list);
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() {
        JspWriter out = pageContext.getOut();
        String spanOpen = new Formatter().format("<div class=\"forum_misc_info\">").toString();
        String strPrevus = new Formatter().format("<c:url var=\"url\" value=\"\"><c:param name=\"page\" value=\"2\"/>" +
                "</c:url><a href=\"%s?page=%d\">%d</a>", uri, currentPage - 1, currentPage - 1).toString();
        String str = new Formatter().format("%d", currentPage).toString();
        String strNext = new Formatter().format("<c:url var=\"url\" value=\"\"><c:param name=\"page\" value=\"2\"/>" +
                "</c:url><a href=\"%s?page=%d\">%d</a>", uri, currentPage + 1, currentPage + 1).toString();
        String spanClose = new Formatter().format("</div>").toString();

        pageContext.setAttribute("maxPage", maxPages);
        try {
            out.write(spanOpen);
            if (currentPage > 1 && maxPages > 0) {
                out.write(strPrevus);
            }

            out.write("   ");
            if (maxPages != 1 && maxPages > 0) {
                out.write(str);
                out.write("   ");
            }
            if (currentPage != maxPages && maxPages > 0) {
                out.write(strNext);
            }
            out.write(spanClose);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EVAL_PAGE;
    }
}
