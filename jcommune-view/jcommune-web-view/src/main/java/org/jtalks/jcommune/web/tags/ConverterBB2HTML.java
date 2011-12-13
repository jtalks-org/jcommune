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

import ru.perm.kefir.bbcode.BBProcessorFactory;
import ru.perm.kefir.bbcode.TextProcessor;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;


/**
 * Converts BB-codes into html representation.
 * Tis tag also replaces newline symbols with html
 * line break tag <br>
 */
public class ConverterBB2HTML extends SimpleTagSupport {

    private String bbCode;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().getOut().print(bbCode);
    }

    /**
     * Sets BB-encoded text to be formatted as HTML.
     *
     * @param bbCode bb-encoded text
     */
    public void setBbCode(String bbCode){
        TextProcessor processor = BBProcessorFactory.getInstance().create();
        this.bbCode = processor.process(bbCode);
    }

    /**
     * Returns text passed formatted with HTML instead of bb-codes
     *
     * @return html representation of bb-encoded text passed
     */
    public String getBbCode() {
        return bbCode;
    }
}
