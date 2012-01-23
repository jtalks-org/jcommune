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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Evgeniy Naumenko
 */
public class ContainsSupport extends SimpleTagSupport {

    private Collection collection;
    private Object object;
    private String successMessage;
    private String failMessage;

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        if (collection.contains(object)){
            this.getJspContext().getOut().print(successMessage);
        } else {
            this.getJspContext().getOut().print(failMessage);
        }
    }
}
