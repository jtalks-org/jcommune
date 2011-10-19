package org.jtalks.jcommune.web.util;

/**
 * Holds a list of the languages available
 */
public enum Languages {
    ENGLISH,
    RUSSIAN;

    private String asText;
    private Languages languages;

    /**
     * Transforms the current Language instance into resource bundle label.
     * This method shoulb be used when you need a localized representation of the current
     * instance
     *
     * @return string resource bundle label
     */
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
