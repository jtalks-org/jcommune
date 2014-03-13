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

import org.springframework.web.servlet.tags.Param;
import org.springframework.web.servlet.tags.UrlTag;

import javax.servlet.jsp.JspException;
import java.util.Map;

/**
 * JSP tag for creating URLs to the specified page in the pagination control.
 * Modeled after the JSTL spring:url tag with backwards
 * compatibility in mind. Also has two additional attributes - "page" to specify page and
 * "params" to get the parameters for the link directly from map rather than use multiple
 * spring:param tags.
 * @author Andrei Alikov
 */
public class PaginationLinkUrl extends UrlTag {
    private Map<String, String> params;
    private String page;
    private static final String PAGE_PARAM = "page";

    /** {@inheritDoc} */
    @Override
    public int doEndTag() throws JspException {
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                Param param = new Param();
                param.setName(entry.getKey());
                param.setValue(entry.getValue());
                addParam(param);
            }
        }

        if (page != null) {
            Param param = new Param();
            param.setName(PAGE_PARAM);
            param.setValue(page);
            addParam(param);
        }

        return super.doEndTag();
    }

    /**
     * Sets the parameters for the link
     * @param params Map containing: key - parameter name and value - parameter value
     */
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    /**
     * Set the page parameter for the url
     * @param value value of the page parameter
     */
    public void setPage(String value) {
        this.page = value;
    }
}
