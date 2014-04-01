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

import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.ImageService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Andrey Ivanov
 */
public class DefaultAvatarEditorTest {

    @Mock
    private ImageService imageService;

    DefaultAvatarEditor defaultAvatarEditor;

    @BeforeMethod
    public void init() {
        initMocks(this);
        defaultAvatarEditor = new DefaultAvatarEditor(imageService);
    }

    @Test
    public void userSetIncorrectAvatarShouldGetDefaultAvatar() throws ImageProcessException, NotFoundException {
        //ARRANGE
        byte[] avatar = new byte[10];
        doThrow(new ImageFormatException("test")).when(imageService).validateImageFormat(any(byte[].class));
        doThrow(new ImageSizeException(1)).when(imageService).validateImageSize(any(byte[].class));
        //ACT
        defaultAvatarEditor.setAsText(String.valueOf(avatar));
        //ASSERT
        verify(imageService, times(1)).getDefaultImage();
    }

    @Test
    public void userSetValidAvatarShouldGetSettedAvatar() throws ImageProcessException, NotFoundException {
        //ARRANGE
        byte[] avatar = new byte[10];
        //ACT
        defaultAvatarEditor.setAsText(String.valueOf(avatar));
        //ASSERT
        verify(imageService, never()).getDefaultImage();
    }
}
