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
package org.jtalks.jcommune.model.entity;

import org.joda.time.DateTime;
import org.jtalks.common.model.entity.User;

/**
 * Stores information about the forum user.
 * Used as {@code UserDetails} in spring security for user authentication, authorization.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Alexandre Teterin
 */
public class JCUser extends User {

    private String signature;
    private int userPostCount;
    private Language language = Language.ENGLISH;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private String location;
    private DateTime registrationDate;

    public static final int MIN_NAME_SIZE = 4;
    public static final int MAX_NAME_SIZE = 20;
    public static final int MAX_LAST_NAME_SIZE = 255;
    public static final int MIN_PASS_SIZE = 4;
    public static final int MAX_PASS_SIZE = 20;
    public static final int MAX_LOCATION_SIZE = 30;

    public static final int DEFAULT_PAGE_SIZE = 50;

    private static final long serialVersionUID = 19981017L;

    /**
     * Only for hibernate usage.
     */
    protected JCUser() {
    }

    /**
     * Create instance with required fields.
     *
     * @param username username
     * @param email    email
     * @param password password
     */
    public JCUser(String username, String email, String password) {
        // passing salt as null until we're not using encrypted passwords
        super(username, email, password, null);
    }

    /**
     * Too complex only for compatibility sake: common-model User
     * assumes byte[0] means empty avatar while we're using "null"
     * value for that
     *
     * @return user avatar
     */
    public byte[] getAvatar() {
        byte[] avatar = super.getAvatar();
        if (avatar == null || avatar.length == 0){
            return null;
        } else {
            return avatar.clone();
        }
    }

    /**
     * Updates login time to current time
     */
    public void updateLastLoginTime() {
        this.setLastLogin( new DateTime());
    }

    /**
     * @return user signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature user signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @return count post this user
     */
    public int getUserPostCount() {
        return this.userPostCount;

    }

    /**
     * @param userPostCount count posts this user to set
     */
    public void setUserPostCount(int userPostCount) {
        this.userPostCount = userPostCount;
    }

    /**
     * @return user language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * @param language of user
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * @return user page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize user page size
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return user location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location user location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return user registration date
     */
    public DateTime getRegistrationDate() {
        return registrationDate;
    }

    /**
     * @param registrationDate user registration date
     */
    public void setRegistrationDate(DateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
