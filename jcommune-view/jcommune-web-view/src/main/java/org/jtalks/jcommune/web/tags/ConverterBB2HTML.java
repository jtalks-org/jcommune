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

import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * Converts BB-codes into html representation. This tag also replaces newline symbols with html line break tag <br>.
 * Take a look at {@code kefirbb.xml} file that configures what are the bb codes and how they should be replaced with
 * HTML tags.
 *
 * @author Evgeny Kapinos
 * @see BBCodeService
 */
public class ConverterBB2HTML extends TagSupport {
    private String bbCode;
    /** We get this from Spring App Context in {@link #setPageContext(PageContext)}. */
    private BBCodeService service;


    /** {@inheritDoc} */
    @Override
    public int doStartTag() throws JspException {
        try {
            String html = service.convertBbToHtml(bbCode);
            pageContext.getOut().print(html);
            return SKIP_BODY;
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    /**
     * Feeds BB-encoded text from the JSP page to be formatted as HTML.
     *
     * @param bbCode bb-encoded text
     */
    public void setBbCode(String bbCode) {
        this.bbCode = bbCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
        service = ac.getBean(BBCodeService.class);
    }
}
