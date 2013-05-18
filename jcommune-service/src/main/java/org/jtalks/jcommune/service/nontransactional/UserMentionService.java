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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;


/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class UserMentionService {
    private static final Pattern USER_BB_CODE_PATTERN = Pattern.compile("\\[user\\].*?\\[/user\\]");
    private MailService mailService;
    private UserDao userDao;
    
    
    /**
     * @param mailService
     * @param userDao
     */
    public UserMentionService(MailService mailService, UserDao userDao) {
        this.mailService = mailService;
        this.userDao = userDao;
    }

    /**
     * Extract all mentioned users and notify them.
     * 
     * @param canContainMentionedUsers can contains mentioned users
     * @param post where user was mentioned
     */
    public void notifyAllMentionedUsers(String canContainMentionedUsers, Post mentioningPost) {
        List<String> mentionedUsersNames = extractMentionedUsers(canContainMentionedUsers);
        if (!CollectionUtils.isEmpty(mentionedUsersNames)) {
            sendNotificationToMentionedUsers(mentionedUsersNames, mentioningPost);
        }
    }
    
    /**
     * Extract all user names that were mentioned in passed text.
     * 
     * @return extracted user names
     */
    public List<String> extractMentionedUsers(String canContainMentionedUsers) {
        if (!StringUtils.isEmpty(canContainMentionedUsers)) {
            Matcher matcher = USER_BB_CODE_PATTERN.matcher(canContainMentionedUsers);
            List<String> mentionedUsernames = new ArrayList<String>();
            while (matcher.find()) {
                String userBBCode = matcher.group();
                String mentionedUser = userBBCode.replaceAll("\\[.*?\\]", StringUtils.EMPTY);
                mentionedUsernames.add(mentionedUser);
            }
            return mentionedUsernames;
        } 
        return Collections.emptyList();
    }
    
    /**
     * Send notification for all mentioned users.
     * 
     * @param mentionedUsernames the list of names of mentioned users
     * @param mentioningPost post where users where mentioned
     */
    private void sendNotificationToMentionedUsers(List<String> mentionedUsernames, Post mentioningPost) {
        List<JCUser> mentionedUsers = userDao.getByUsernames(mentionedUsernames);
        for (JCUser mentionedUser: mentionedUsers) {
            boolean isNotificationAlreadySent = mentioningPost.getTopicSubscribers().contains(mentionedUser);
            if (!isNotificationAlreadySent && mentionedUser.isMentioningNotificationsEnabled()) {
                mailService.sendUserMentionedNotification(mentionedUser, mentioningPost.getId());
            }
        }
    }
}
