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
package org.jtalks.jcommune.web.validation.editors;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;

/**
 * Editor transform DateTime according to format
 *
 * @author Andrey Ivanov
 */
public class DateTimeEditor extends PropertyEditorSupport {

    private final String format;

    public DateTimeEditor(String format) {
        this.format = format;
    }

    @Override
    public String getAsText() {
        if (getValue() != null) {
            return new SimpleDateFormat(this.format).format(((DateTime) getValue()).toDate());
        }
        return super.getAsText();
    }

    @Override
    public void setAsText(String text) {
        if (text != null) {
            DateTimeFormatter format = DateTimeFormat.forPattern(this.format);
            setValue(format.parseDateTime(text));
        }
    }
}
