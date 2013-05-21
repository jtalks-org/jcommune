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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Alexandre Teterin
 */
public class AvatarServiceTest {
    private static final String PROPERTY_NAME = "property";
    private static final int AVATAR_MAX_SIZE = 1000;
    private JCommuneProperty avatarSizeProperty = JCommuneProperty.AVATAR_MAX_SIZE;
    @Mock
    private ImageUtils imageUtils;
    @Mock
    private Base64Wrapper base64Wrapper;
    @Mock
    private PropertyDao propertyDao;
    //
    private AvatarService avatarService;
    

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        avatarSizeProperty.setName(PROPERTY_NAME);
        avatarSizeProperty.setPropertyDao(propertyDao);
        when(propertyDao.getByName(PROPERTY_NAME))
            .thenReturn(new Property(PROPERTY_NAME, String.valueOf(AVATAR_MAX_SIZE)));
        avatarService = new AvatarService(
                imageUtils,
                base64Wrapper,
                "org/jtalks/jcommune/service/avatar.gif",
                avatarSizeProperty);
    }



    @Test
    public void getDefaultAvatarShouldReturnNotEmptyAvatar() {
        byte[] avatar = avatarService.getDefaultAvatar();

        assertTrue(avatar.length > 0);
    }
    
    @Test
    public void testGetIfModifiedSinceDate() {
        long currentMillis = System.currentTimeMillis();
        long currentTimeIgnoreMillis = (currentMillis / 1000) * 1000; 
        Date date = new Date(currentTimeIgnoreMillis);
        String dateAsString = DateFormatUtils.format(date, 
                AvatarService.HTTP_HEADER_DATETIME_PATTERN,
                Locale.US);
        
        Date result = avatarService.getIfModifiedSineDate(dateAsString);
        
        assertEquals(result.getTime(), date.getTime());
    }
    
    @Test
    public void testGetIfModifiedSinceDateNullHeader() {
        Date result = avatarService.getIfModifiedSineDate(null);
        
        assertEquals(result, new Date(0));
    }
}
