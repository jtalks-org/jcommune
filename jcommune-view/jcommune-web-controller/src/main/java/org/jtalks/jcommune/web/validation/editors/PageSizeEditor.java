package org.jtalks.jcommune.web.validation.editors;

import org.apache.commons.lang.ArrayUtils;
import org.jtalks.jcommune.model.entity.JCUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditorSupport;

/**
 * Editor for using when user set page size in profile.
 * We need this one because user can set invalid value via firebug or other web-plugin in browser
 *
 * @author Andrey Ivanov
 */
public class PageSizeEditor extends PropertyEditorSupport {

    /**
     * @param text current value
     */
    @Override
    public void setAsText(String text) {
        for (int pageSize : JCUser.PAGE_SIZES_AVAILABLE) {
            if (String.valueOf(pageSize).equals(text)) {
                setValue(pageSize);
                return;
            }
        }
    }
}