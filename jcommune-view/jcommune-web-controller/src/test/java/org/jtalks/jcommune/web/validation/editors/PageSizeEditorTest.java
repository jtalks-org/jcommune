package org.jtalks.jcommune.web.validation.editors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Andrey Ivanov
 */
public class PageSizeEditorTest {
    PageSizeEditor editor;

    @BeforeMethod
    public void init() {
        editor = new PageSizeEditor();
    }

    @Test
    public void userSetInvalidValue() {
        editor.setAsText("bla");
        assertEquals(null, editor.getValue());
    }

    @Test
    public void userSetValueWhichOutOfRange() {
        editor.setAsText(String.valueOf(1));
        assertEquals(null, editor.getValue());
    }

    @Test
    public void userSetValidValue() {
        int expected = 25;
        editor.setAsText(String.valueOf(expected));
        assertEquals(expected, editor.getValue());
    }
}
