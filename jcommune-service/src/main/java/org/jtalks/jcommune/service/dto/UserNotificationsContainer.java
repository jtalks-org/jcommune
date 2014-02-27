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
package org.jtalks.jcommune.service.dto;

/**
 * This class is used when transferring user notifications updates
 * from web tier to the service layer. For various reasons
 * we can't use domain model class and MVC command object.
 *
 * @author Andrey Pogorelov
 */
public class UserNotificationsContainer {

    private boolean mentioningNotificationsEnabled;
    private boolean sendPmNotification;

    /**
     * Create instance with required fields.
     *
     * @param mentioningNotificationsEnabled whether email notifications are send when user was mentioned in forum
     * @param sendPmNotification send notification when get PM
     */
    public UserNotificationsContainer(boolean mentioningNotificationsEnabled, boolean sendPmNotification) {
        this.mentioningNotificationsEnabled = mentioningNotificationsEnabled;
        this.sendPmNotification = sendPmNotification;
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
     * @return send pm notification or not
     */
    public boolean isSendPmNotification() {
        return sendPmNotification;
    }
}
