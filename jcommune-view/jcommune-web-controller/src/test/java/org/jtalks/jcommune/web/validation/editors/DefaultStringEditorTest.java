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

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 *
 * @author Andrey Pogorelov
 */
public class DefaultStringEditorTest {

    DefaultStringEditor editor;

    @Test
    public void emptyValueWithOptionEmptyAsNullShouldBeTransformedToNull() {
        editor = new DefaultStringEditor(true);
        editor.setAsText("");

        assertNull(editor.getValue(), "String value should be correctly transformed to null.");
    }

    @Test
    public void notEmptyValueWithOptionEmptyAsNullShouldNotBeChanged() {
        editor = new DefaultStringEditor(true);
        editor.setAsText("123");

        assertEquals("123", editor.getValue(), "String value shouldn't be transformed.");
    }

    @Test
    public void emptyValueWithoutOptionEmptyAsNullShouldNotBeChanged() {
        editor = new DefaultStringEditor(false);
        editor.setAsText("");

        assertEquals("", editor.getValue(), "String value shouldn't be transformed.");
    }

    @Test
    public void notEmptyValueWithoutOptionEmptyAsNullShouldNotBeChanged() {
        editor = new DefaultStringEditor(false);
        editor.setAsText("123");

        assertEquals("123", editor.getValue(), "String value shouldn't be transformed.");
    }

    @Test
    public void nullValueShouldBeReturnedAsEmptyString() {
        editor = new DefaultStringEditor(false);
        editor.setValue(null);

        assertEquals("", editor.getAsText(), "Null value should be returned as empty string.");
    }

    @Test
    public void notNullValueShouldBeReturnedWithoutTransforms() {
        editor = new DefaultStringEditor(false);
        editor.setValue(null);

        assertEquals("", editor.getAsText(), "Not null value should be returned without any transforms.");
    }
}
