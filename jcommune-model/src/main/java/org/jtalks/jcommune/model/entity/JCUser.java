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
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores information about the forum user.
 * Used as {@code UserDetails} in spring security for user authentication, authorization.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Andrey Kluev
 */
public class JCUser extends User {

    private int postCount;
    private Language language = Language.ENGLISH;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private String location;
    private String signature;
    private DateTime registrationDate;
    private boolean enabled;
    private boolean autosubscribe;
    private boolean mentioningNotificationsEnabled;
    private boolean sendPmNotification;

    public static final int MAX_SIGNATURE_SIZE = 255;
    public static final int MAX_LOCATION_SIZE = 30;

    public static final int DEFAULT_PAGE_SIZE = 15;
    public static final int[] PAGE_SIZES_AVAILABLE = new int[]{15, 25, 50};

    private static final long serialVersionUID = 19981017L;
    private Set<UserContact> contacts = new HashSet<>();

    private DateTime avatarLastModificationTime = new DateTime(System.currentTimeMillis());

    private DateTime allForumMarkedAsReadTime;

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
     * Updates login time to current time
     */
    public void updateLastLoginTime() {
        this.setLastLogin(new DateTime());
    }

    /**
     * @param contact user contact
     */
    public void addContact(UserContact contact) {
        contact.setOwner(this);
        this.getContacts().add(contact);
    }

    /**
     * @param contact user contact
     */
    public void removeContact(UserContact contact) {
        this.getContacts().remove(contact);
    }

    /**
     * @return user signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param content user signature
     */
    public void setSignature(String content) {
        this.signature = content;
    }

    /**
     * @return count post this user
     */
    public int getPostCount() {
        return this.postCount;

    }

    /**
     * @param postCount count posts this user to set
     */
    public void setPostCount(int postCount) {
        this.postCount = postCount;
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

    /**
     * @return set contacts of user
     */
    public Set<UserContact> getContacts() {
        return contacts;
    }

    /**
     * @param contacts contacts of user
     */
    protected void setContacts(Set<UserContact> contacts) {
        this.contacts = contacts;
    }

    /**
     * After registration user account is disabled by default.
     * If not enabled in 24 hours after registration account will be deleted.
     * <p/>
     * User can activate his account by following the link in email.
     *
     * @return true, if user account is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Disables or enables the user account. If it's disabled, user won't be able to log in. Usually user is enabled
     * during account activation.
     *
     * @param enabled if set to false, it will prevent user from log in
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Determines whether user is automatically subscribed to the topic while posting there.
     *
     * @return true if user automatically subscribed to the topic while posting there, or false if user switched this
     *         off in his settings
     * @see <a href="http://jira.jtalks.org/browse/JC-1361">Related JIRA Ticket</a>
     */
    public boolean isAutosubscribe() {
        return autosubscribe;
    }

    /**
     * Set whether after creating a new post user subscribes on update of the topic.
     *
     * @param autosubscribe set true if you'd like user to subscribe to the topic when she creates it, otherwise set
     *                      false and user won't get automatically subscribed to the topic
     * @see <a href="http://jira.jtalks.org/browse/JC-1361">Related JIRA Ticket</a>
     */
    public void setAutosubscribe(boolean autosubscribe) {
        this.autosubscribe = autosubscribe;
    }

    /**
     * Determines whether email notifications are send to user when he has been mentioned in forum.
     *
     * @return true user receives email notifications, otherwise false
     */
    public boolean isMentioningNotificationsEnabled() {
        return mentioningNotificationsEnabled;
    }

    /**
     * Set whether email notifications are send to user when he has been mentioned in forum.
     *
     * @param mentioningNotificationsEnabled true user receives email notifications, otherwise false
     */
    public void setMentioningNotificationsEnabled(boolean mentioningNotificationsEnabled) {
        this.mentioningNotificationsEnabled = mentioningNotificationsEnabled;
    }

    /**
     * Returns whether current user is logged in or not. Vast majority of user
     * properties is available for logged in users only, anonymous user object
     * holds only default settings
     *
     * @return whether this user is anonymous
     */
    public boolean isAnonymous() {
        return false;
    }

    /**
     * Determines whether email notifications are send to user when he has new private message.
     *
     * @return true user receives email notifications, otherwise false
     */
    public boolean isSendPmNotification() {
        return sendPmNotification;
    }

    /**
     * Set whether email notifications are send to user when he has new private message.
     *
     * @param sendPmNotification
     */
    public void setSendPmNotification(boolean sendPmNotification) {
        this.sendPmNotification = sendPmNotification;
    }

    /**
     * @return last modification time of avatar
     */
    public DateTime getAvatarLastModificationTime() {
        return avatarLastModificationTime;
    }

    /**
     * @param avatarLastModificationTime time when avatar was last modified
     */
    public void setAvatarLastModificationTime(DateTime avatarLastModificationTime) {
        this.avatarLastModificationTime = avatarLastModificationTime;
    }

    /**
     * Get the time when forum was marked as all read for this user.
     *
     * @return if forum was marked as all read for this user it returns time of this action,
     *         if forum was never marked as all read it returns null
     */
    public DateTime getAllForumMarkedAsReadTime() {
        return allForumMarkedAsReadTime;
    }

    /**
     * Set the time when forum was marked as all read for this user.
     *
     * @param forumMarkedAsAllReadTime the time when forum was marked as all read for this user
     */
    public void setAllForumMarkedAsReadTime(DateTime forumMarkedAsAllReadTime) {
        this.allForumMarkedAsReadTime = forumMarkedAsAllReadTime;
    }

    /**
     * Adds a user to the group and adds group to the user. No checks whether there are such records present here,
     * that's what Hibernate will do for us anyway.
     *
     * @param group a new group to be added to the list of groups this user is in
     * @return this
     */
    public JCUser addGroup(Group group) {
        getGroups().add(group);
        group.getUsers().add(this);
        return this;
    }

    /**
     * Creates copy of user needed in plugins.
     * @param user user to be copied
     */
    public static JCUser copyUser(JCUser user) {
        if (user == null) {
            throw new IllegalArgumentException("User should not be null");
        }
        JCUser copy = new JCUser(user.getUsername(), user.getEmail(), user.getPassword());
        copy.setSalt(user.getSalt());
        copy.setPostCount(user.getPostCount());
        copy.setLanguage(user.getLanguage());
        copy.setPageSize(user.getPageSize());
        copy.setLocation(user.getLocation());
        copy.setSignature(user.getSignature());
        copy.setRegistrationDate(user.getRegistrationDate());
        copy.setEnabled(user.isEnabled());
        copy.setAutosubscribe(user.isAutosubscribe());
        copy.setMentioningNotificationsEnabled(user.isMentioningNotificationsEnabled());
        copy.setSendPmNotification(user.isSendPmNotification());
        copy.getContacts().addAll(user.getContacts());
        copy.setAvatarLastModificationTime(user.getAvatarLastModificationTime());
        copy.setAllForumMarkedAsReadTime(user.getAllForumMarkedAsReadTime());
        return copy;
    }
}
