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
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Collection;

/**
 * Supporting class for ifContains tag. This tag checks if certain object is contained
 * in the collection passed and prints messages depending on the result.
 * <p/>
 * This tag does not support localization or message codes.
 *
 * @author Evgeniy Naumenko
 */
public class ContainsSupport extends BodyTagSupport {

    /**
     * Serializable class should define it
     */
    private static final long serialVersionUID = 34588L;

    private transient Collection collection;
    private Object object;
    private String successMessage;
    private String failMessage;


    /**
     * @param collection collection to search specified object into
     */
    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    /**
     * @param object object to be searched in collection
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * @param successMessage message to display if collection passed contains the target element
     */
    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    /**
     * @param failMessage message to display if element is not in the collection passed
     */
    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        try {
            String message = collection.contains(object) ? successMessage : failMessage;
            pageContext.getOut().write(message);
        } catch (IOException e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }
}
