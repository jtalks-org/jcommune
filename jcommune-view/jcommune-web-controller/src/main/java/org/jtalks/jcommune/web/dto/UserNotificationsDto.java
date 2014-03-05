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
package org.jtalks.jcommune.web.dto;

import org.jtalks.jcommune.model.entity.JCUser;

/**
 * This dto used for transferring data in edit {@link org.jtalks.jcommune.model.entity.JCUser} profile operation.
 * To get more info see
 * {@link org.jtalks.jcommune.web.controller.UserProfileController#saveEditedNotifications(EditUserProfileDto,
 * org.springframework.validation.BindingResult, javax.servlet.http.HttpServletResponse)}.
 *
 * @author Andrey Pogorelov
 */
public class UserNotificationsDto {
    private long userId;

    private boolean mentioningNotificationsEnabled;
    private boolean sendPmNotification;
    private boolean autosubscribe;

    /**
     * Form info population only, please do not call it explicitly
     */
    public UserNotificationsDto() {
    }

    /**
     * Constructor which fills dto fields from user.
     *
     * @param user copying source
     */
    public UserNotificationsDto(JCUser user) {
        this.userId = user.getId();
        this.autosubscribe = user.isAutosubscribe();
        this.mentioningNotificationsEnabled = user.isMentioningNotificationsEnabled();
        this.sendPmNotification = user.isSendPmNotification();
    }

    /**
     * Get the primary id of the user.
     *
     * @return the id
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Set the primary id of the user.
     *
     * @param userId the id
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * @see org.jtalks.jcommune.model.entity.JCUser#isMentioningNotificationsEnabled()
     */
    public boolean isMentioningNotificationsEnabled() {
        return mentioningNotificationsEnabled;
    }

    /**
     * @see org.jtalks.jcommune.model.entity.JCUser#setMentioningNotificationsEnabled(boolean)
     */
    public void setMentioningNotificationsEnabled(
            boolean mentioningNotificationsEnabled) {
        this.mentioningNotificationsEnabled = mentioningNotificationsEnabled;
    }

    /**
     * Check - sending notification is allow
     *
     * @return if allow return true else false
     */
    public boolean isSendPmNotification() {
        return sendPmNotification;
    }

    /**
     * Set send notification or not
     *
     * @param sendPmNotification Send notification or not
     */
    public void setSendPmNotification(boolean sendPmNotification) {
        this.sendPmNotification = sendPmNotification;
    }

    /**
     * @see org.jtalks.jcommune.model.entity.JCUser#isAutosubscribe()
     */
    public boolean isAutosubscribe() {
        return autosubscribe;
    }

    /**
     * @see org.jtalks.jcommune.model.entity.JCUser#setAutosubscribe(boolean)
     */
    public void setAutosubscribe(boolean autosubscribe) {
        this.autosubscribe = autosubscribe;
    }
}
