package org.jtalks.jcommune.web.util;


/**
 * Created by IntelliJ IDEA.
 * User: Mr_Green
 * Date: 17.10.11
 * Time: 20:15
 * To change this template use File | Settings | File Templates.
 */
public enum Languages {
    ENGLISH,
    RUSSIAN;

    private String asText;
    private Languages languages;

    public String getAsText() {
        switch (this) {
            case ENGLISH:
                return "label.english";
            case RUSSIAN:
                return "label.russian";
            default:
                return super.toString();
        }
    }

    public Languages getLanguages() {
        return languages;
    }

    public void setLanguages(Languages languages) {
        this.languages = languages;
    }
}
