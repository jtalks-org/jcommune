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
