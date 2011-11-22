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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.exceptions.DuplicateEmailException;
import org.jtalks.jcommune.service.exceptions.DuplicateUserException;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.exceptions.WrongPasswordException;

/**
 * This interface should have methods which give us more abilities in manipulating User persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 */
public interface UserService extends EntityService<User> {
    /**
     * Get {@link User} by username.
     *
     * @param username username of User
     * @return {@link User} with given username
     * @throws NotFoundException if user not found
     * @see User
     */
    User getByUsername(String username) throws NotFoundException;

    /**
     * Get {@link User} by encodedUsername.
     *
     * @param encodedUsername encodedUsername of User
     * @return {@link User} with given encodedUsername
     * @throws NotFoundException if user not found
     * @see User
     */
    User getByEncodedUsername(String encodedUsername) throws NotFoundException;

    /**
     * Try to register {@link User} with given features.
     *
     * @param user user for register
     * @return registered {@link User}
     * @throws DuplicateUserException  if user with username already exist
     * @throws DuplicateEmailException when user with given email already exist
     * @see User
     */
    User registerUser(User user) throws DuplicateUserException, DuplicateEmailException;


    /**
     * Updates user last login time to current time.
     *
     * @param user user which must be updated
     * @see User
     */
    void updateLastLoginTime(User user);

    /**
     * Update user entity.
     *
     * @param email           email
     * @param firstName       first name
     * @param lastName        last name
     * @param currentPassword current user password, could be NULL
     * @param newPassword     new user password, could be NULL
     * @param avatar          user avatar
     * @param signature       user signature
     * @param language        user language
     * @param pageSize        user pageSize
     * @return edited user
     * @throws DuplicateEmailException when user with given email already exist
     * @throws WrongPasswordException  when user enter wrong currentPassword
     */
    User editUserProfile(String email, String firstName, String lastName, String currentPassword,
                         String newPassword, byte[] avatar, String signature, String language, String pageSize)
            throws DuplicateEmailException, WrongPasswordException;

    /**
     * Remove current user's avatar.
     */
    void removeAvatarFromCurrentUser();

    /**
     * Checks if email has been registered.
     *
     * @param email email for check existence
     * @return {@code true} if email exist
     */
    boolean isEmailExist(String email);

    /**
     * Performs the following:
     * 2. Checks, checks if this parameter represents known e-mail
     * 3. If no user is found by e-mail exception is thrown
     * 4. Alters the password for this user to the random string
     * 5. Sends an e-mail with new password to this address to notify user
     *
     * @param email address to identify user
     * @throws NotFoundException if there is no user for the email given
     * @throws org.jtalks.jcommune.service.exceptions.MailingFailedException if mailing failed
     */
    void restorePassword(String email) throws NotFoundException, MailingFailedException;
}
