/*
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

/**
 * Creates a link = current location + 'lang' URL parameter.
 * When submitted this link will force temporary (session scoped)
 * locale change for the current user.
 * To change locale instantly user should alter the corresponding user profile settings.
 *
 * @param lang ISO_639-1 language code
 */
function getLanguageLink(lang) {
    var href = window.location.toString().split("language", 1)[0];
    if (href.indexOf("?") == -1) {
        href = href + "?lang=";
    } else {
        if (href.indexOf("lang=") == -1) {
            href = href + "&lang=";
        } else {
            href = href.substring(0, href.length - 2);
        }
    }
    return href + lang;
}