package org.jtalks.jcommune.web.validation.editors;

import org.jtalks.jcommune.model.entity.Language;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class LanguageEditorTest {
    @Test
    public void testWhenUserSetIncorrectLanguage() {
        Language defaultLanguage = Language.ENGLISH;
        LanguageEditor languageEditor = new LanguageEditor(Language.ENGLISH);
        languageEditor.setAsText("Incorrect language");
        assertEquals(defaultLanguage, languageEditor.getValue());
    }

    @Test
    public void testWhenUserSetCorrectLanguage() {
        String language = Language.RUSSIAN.toString();
        LanguageEditor languageEditor = new LanguageEditor(null);
        languageEditor.setAsText(language);
        assertEquals(language, languageEditor.getValue());
    }
}
