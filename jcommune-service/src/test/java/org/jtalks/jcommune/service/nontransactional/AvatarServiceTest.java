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

package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertTrue;

/**
 * @author Alexandre Teterin
 */
public class AvatarServiceTest {
    private static final String PROPERTY_NAME = "property";
    private static final int AVATAR_MAX_SIZE = 1000;
    private JCommuneProperty avatarSizeProperty = JCommuneProperty.AVATAR_MAX_SIZE;
    @Mock
    private ImageConverter imageConverter;
    @Mock
    private Base64Wrapper base64Wrapper;
    @Mock
    private PropertyDao propertyDao;
    //
    private ImageService avatarService;
    

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        avatarSizeProperty.setName(PROPERTY_NAME);
        avatarSizeProperty.setPropertyDao(propertyDao);
        when(propertyDao.getByName(PROPERTY_NAME))
            .thenReturn(new Property(PROPERTY_NAME, String.valueOf(AVATAR_MAX_SIZE)));
        avatarService = new ImageService(
                imageConverter,
                base64Wrapper,
                "org/jtalks/jcommune/service/avatar.gif",
                avatarSizeProperty);
    }



    @Test
    public void getDefaultAvatarShouldReturnNotEmptyAvatar() {
        byte[] avatar = avatarService.getDefaultImage();

        assertTrue(avatar.length > 0);
    }
}
