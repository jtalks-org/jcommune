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
package org.jtalks.jcommune.model.search;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.search.bridge.StringBridge;

/**
 * Eliminates all the bbcodes before indexing the data.
 * To enable the filter you need to declare the bridge annotation
 * {@link org.hibernate.search.annotations.FieldBridge} above property/field.
 * 
 * @author Anuar Nurmakanov
 * @see StringBridge
 * @see org.hibernate.search.annotations.FieldBridge
 */
public class BbCodeFilterBridge implements StringBridge {
    //TODO: BBCodeService.stripBBCodes should be used instead of this regexp
    private static final String BB_CODE_REGEXP_TEMPLATE = "\\[.*?\\]";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String objectToString(Object object) {
        if (object instanceof String) {
            String value = (String) object;
            return value.replaceAll(BB_CODE_REGEXP_TEMPLATE, " ");
        }
        return ObjectUtils.toString(object);
    }
}
