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

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar Nurmakanov
 *
 */
public class EncryptionServiceTest {
    private static final String PASSWORD = "password";
    private static final String PASSWORD_MD5_HASH = "5f4dcc3b5aa765d61d8327deb882cf99";
    @Mock
    private PasswordEncoder passwordEncoder;
    private EncryptionService encryptionService;
    
    
    @BeforeTest
    public void init() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(passwordEncoder.encodePassword(PASSWORD, null)).thenReturn(PASSWORD_MD5_HASH);
        encryptionService = new EncryptionService(passwordEncoder);
    }
    
    @Test
    public void testEncryptPassword() {
        String encryptedPassword = encryptionService.encryptPassword(PASSWORD);
        Assert.assertEquals(encryptedPassword, PASSWORD_MD5_HASH, "Returned an incorrect hash value");
    }    
}
