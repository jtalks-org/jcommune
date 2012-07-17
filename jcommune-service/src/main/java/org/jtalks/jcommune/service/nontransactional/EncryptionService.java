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

import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 * Service for data encryption. Allows us to encrypt the password
 * using the algorithm selected for the application.
 * 
 * @author Anuar Nurmakanov
 */
public class EncryptionService {
    private PasswordEncoder passwordEncoder;
    
    /**
     * Constructs an instance with required fields.
     * 
     * @param passwordEncoder for password encryption
     */
    public EncryptionService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Gets a hash of the password with the use of encryption mechanism.
     * This mechanism is the same  that used in the old forum.
     * 
     * @param password password that the user entered 
     * @return encrypted password
     */
    public String encryptPassword(String password) {
        return passwordEncoder.encodePassword(password, null); //We do not use salt because it is not used phpbb
    }
}
