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

import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.ImageService;

import java.beans.PropertyEditorSupport;

/**
 * Editor sets up default avatar if user set incorrect avatar.
 * See {@link http://jira.jtalks.org/browse/JC-1830}
 *
 * @author Andrey Ivanov
 */
public class DefaultAvatarEditor extends PropertyEditorSupport {

    private final ImageService imageService;
    private final Base64Wrapper base64Wrapper = new Base64Wrapper();

    /**
     * @param imageService for image validation
     */
    public DefaultAvatarEditor(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public void setAsText(String text) {
        try {
            byte[] avatarAsByte = base64Wrapper.decodeB64Bytes(text);
            imageService.validateImageSize(avatarAsByte);
            imageService.validateImageFormat(avatarAsByte);
            setValue(text);
        } catch (ImageProcessException e) {
            setValue(base64Wrapper.encodeB64Bytes(imageService.getDefaultImage()));
        }
    }
}
