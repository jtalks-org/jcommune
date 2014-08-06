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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Andrey Ivanov
 */
public class DateTimeEditorTest {
    DateTimeEditor editor;

    @BeforeMethod
    public void init() {
        editor = new DateTimeEditor("dd-MM-yyyy");
    }

    @Test
    public void valueIsInvalid() {
        editor.setAsText(null);
        assertEquals(null, editor.getValue());
    }

    @Test
    public void setValueAsText() {
        editor.setAsText("02-08-2014");
        assertEquals("02-08-2014", editor.getAsText());
    }

    @Test
    public void setAsTextNotMatchingFormatStingShouldNotSetValue() {
        try {
            editor.setAsText("99999-08-2014");
        } catch (Exception ex) {
        }

        assertEquals(null, editor.getValue());
    }
}
