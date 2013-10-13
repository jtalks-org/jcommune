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

import org.jtalks.jcommune.model.entity.Language;

import java.beans.PropertyEditorSupport;

/**
 * Default property editor for Language.
 * <p/>
 *
 * @author Andrey Ivanov
 */
public class LanguageEditor extends PropertyEditorSupport {

    /**
     * Default language which we should use when user sets incorrect language.
     *
     * @param defaultLanguage
     */
    Language defaultLanguage;

    public LanguageEditor(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    @Override
    public void setAsText(String text) {
        for (Language lang : Language.values()) {
            if (text.equals(lang.toString())) {
                setValue(text);
                return;
            }
        }
        setValue(this.defaultLanguage);
    }
}
