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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;


/**
 * This service provides send email notifications to all mentioned users
 * in some components of forum:topics, posts. Also it provides an ability
 * to extract users mentioning from text.
 * 
 * @author Anuar_Nurmakanov
 * @author Andrei Alikov
 *
 */
public class MentionedUsers {
    private static final Logger LOGGER = LoggerFactory.getLogger(MentionedUsers.class);

    private static final Pattern ALL_MENTIONED_USERS_PATTERN = 
            Pattern.compile("\\[user\\].*?\\[/user\\]|\\[user notified=true\\].*?\\[/user\\]");
    private static final Pattern MENTIONED_AND_NOT_NOTIFIED_USERS_PATTERN = 
            Pattern.compile("\\[user\\].*?\\[/user\\]");
    public static final String MENTIONED_NOT_NOTIFIED_USER_TEMPLATE = "[user]%s[/user]";
    public static final String MENTIONED_AND_NOTIFIED_USER_TEMPLATE = "[user notified=true]%s[/user]";
    public static final String USER_WITH_LINK_TO_PROFILE_TEMPLATE = "[user=%s]%s[/user]";

    /**
     * Content of the post
     */
    private String postContent;

    /**
     * Post with mentioned users
     */
    private Post post;

    private MentionedUsers(String postContent) {
        this.postContent = postContent;
    }

    private MentionedUsers(Post post) {
        this.post = post;
        this.postContent = post.getPostContent();
    }

    /**
     * Creates new instance of MentionedUsers based on the Post data
     *
     * @param postContent content of the post where user was mentioned
     */
    public static MentionedUsers parse(String postContent) {
        return new MentionedUsers(postContent);
    }

    /**
     * Creates new instance of MentionedUsers based on the Post data
     *
     * @param post the post where user was mentioned
     */
    public static MentionedUsers parse(Post post) {
        return new MentionedUsers(post);
    }

    /**
     * Sends notifications to all notified users
     * @param mailService service for sending e-mails to users
     * @param userDao service for user related operations
     */
    public void notifyNewlyMentionedUsers(MailService mailService, UserDao userDao) {
        if (post == null) {
            throw new IllegalStateException();
        }

        Set<String> mentionedUsersNames = extractNotNotifiedMentionedUsers(postContent);
        if (!CollectionUtils.isEmpty(mentionedUsersNames)) {
            sendNotificationToMentionedUsers(mentionedUsersNames, mailService, userDao);
        }
    }

    /**
     * Marks all users in user BB codes as already notified
     * @param postDao service for post related operations
     */
    public void markUsersAsAlreadyNotified(PostDao postDao) {
        if (post == null) {
            throw new IllegalStateException();
        }

        Set<String> mentionedUsersNames = extractNotNotifiedMentionedUsers(postContent);
        if (!CollectionUtils.isEmpty(mentionedUsersNames)) {
            markUsersAsAlreadyNotified(mentionedUsersNames, postDao);
        }
    }

    /**
     * Returns post text with BB codes replaced by user profile links
     * @param userDao service for working with user objects
     * @return text with BB codes replaced by user profile links
     */
    public String getTextWithProcessedUserTags(UserDao userDao) {
        Set<String> mentionedUsers = extractAllMentionedUsers(postContent);
        Map<String, String> userToUserProfileLinkMap = new HashMap<String, String>();
        for (String mentionedUser: mentionedUsers) {
            String mentionedUserProfileLink = getLinkToUserProfile(mentionedUser, userDao);
            if (mentionedUserProfileLink != null) {
                userToUserProfileLinkMap.put(mentionedUser, mentionedUserProfileLink);
            }
        }
        return addLinksToUserProfileForMentionedUsers(postContent, userToUserProfileLinkMap);
    }



    /**
     * Extract names of all users that were mentioned in passed text.
     * 
     * @return extracted users' names
     */
    public Set<String> extractAllMentionedUsers(String canContainMentionedUsers) {
        return extractMentionedUsers(canContainMentionedUsers, ALL_MENTIONED_USERS_PATTERN);
    }

    
    /**
     * Extract names of users that were mentioned but not notified yet
     * 
     * @return names of users that were mentioned but not notified yet
     */
    private Set<String> extractNotNotifiedMentionedUsers(String canContainMentionedUsers) {
        return extractMentionedUsers(canContainMentionedUsers, MENTIONED_AND_NOT_NOTIFIED_USERS_PATTERN);
    }
    
    /**
     * Extract names of users that were mentioned in passed text.
     * 
     * @param canContainMentionedUsers can contain users mentioning
     * @param mentionedUserPattern pattern to extract mentioned user in given text
     * @return extracted users' names
     */
    private Set<String> extractMentionedUsers(String canContainMentionedUsers, Pattern mentionedUserPattern) {
        if (!StringUtils.isEmpty(canContainMentionedUsers)) {
            Matcher matcher = mentionedUserPattern.matcher(canContainMentionedUsers);
            Set<String> mentionedUsernames = new HashSet<String>();
            while (matcher.find()) {
                String userBBCode = matcher.group();
                String mentionedUser = userBBCode.replaceAll("\\[.*?\\]", StringUtils.EMPTY);
                mentionedUsernames.add(mentionedUser);
            }
            return mentionedUsernames;
        } 
        return Collections.emptySet();
    }

    /**
     * Send notification for passed list of users.
     *
     * @param mentionedUsernames the set of names of mentioned users
     * @param mailService service for sending e-mails
     * @param userDao service for working with JCUser objects
     */
    private void sendNotificationToMentionedUsers(Set<String> mentionedUsernames,
                                                  MailService mailService, UserDao userDao) {
        List<JCUser> mentionedUsers = userDao.getByUsernames(mentionedUsernames);

        for (JCUser mentionedUser: mentionedUsers) {
            sendNotificationToMentionedUser(mentionedUser, mailService);
        }
    }
    
    /**
     * Send notification for passed list of users.
     * 
     * @param mentionedUsernames the set of names of mentioned users
     * @param postDao service for working with Post objects
     */
    private void markUsersAsAlreadyNotified(Set<String> mentionedUsernames, PostDao postDao) {
        for (String user: mentionedUsernames) {
            markUserAsAlreadyNotified(user, postDao);
        }
    }

    /**
     * Send notification for passed user
     *
     * @param mentionedUser this user was mentioned, so he should receive notification(if notifications are enabled)
     * @param sendMailService service for sending e-mails
     */
    private void sendNotificationToMentionedUser(JCUser mentionedUser,
                                                 MailService sendMailService) {
        boolean isOtherNotificationAlreadySent = post.getTopicSubscribers().contains(mentionedUser);
        if (!isOtherNotificationAlreadySent && mentionedUser.isMentioningNotificationsEnabled()) {
            sendMailService.sendUserMentionedNotification(mentionedUser, post.getId());
        }
    }
    
    /**
     * Send notification for passed user
     * 
     * @param username this user was mentioned, so he should receive notification(if notifications are enabled)
     * @param postDao service for working with Post objects
     */
    private void markUserAsAlreadyNotified(String username, PostDao postDao) {
            String initialUserMentioning = format(MENTIONED_NOT_NOTIFIED_USER_TEMPLATE, username);
            String notifiedUserMentioning = format(MENTIONED_AND_NOTIFIED_USER_TEMPLATE, username);

            String newPostContent =
                    post.getPostContent().replace(initialUserMentioning, notifiedUserMentioning);
            post.setPostContent(newPostContent);
            postDao.saveOrUpdate(post);
    }

    /**
     * Get link to user's profile.
     *
     * @param username user's name
     * @return null when user doesn't exist, otherwise link to user's profile
     */
    private String getLinkToUserProfile(String username, UserDao userDao) {
        String userPofileLink = null;

        JCUser user = userDao.getByUsername(username);
        if (user != null) {
            userPofileLink = getApplicationNameAsContextPath() + "/users/" + user.getId();
            LOGGER.debug(username + " has the following url of profile -" + userPofileLink);
        }
        else {
            LOGGER.debug("Mentioned user wasn't find: " + username);
        }

        return userPofileLink;
    }

    /**
     * Get the name of application as context path.
     *
     * @return forum application name
     */
    private String getApplicationNameAsContextPath() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        return request.getContextPath();
    }

    /**
     * Add links to users' profiles for mentioned users.
     *
     * @param source will be changed and all mentioned users in it will contain links to their profiles
     * @param userToUserProfileLinkMap user to it links of profile map
     * @return source with users with attached links to profiles
     */
    private String addLinksToUserProfileForMentionedUsers(
            String source, Map<String, String> userToUserProfileLinkMap) {
        String changedSource = source;
        for (Map.Entry<String, String> userToLinkMap: userToUserProfileLinkMap.entrySet()) {
            String username = userToLinkMap.getKey();
            String userNotNotifiedBBCode = format(MENTIONED_NOT_NOTIFIED_USER_TEMPLATE, username);
            String userNotifiedBBCode = format(MENTIONED_AND_NOTIFIED_USER_TEMPLATE, username);
            String userBBCodeWithLink = format(
                    USER_WITH_LINK_TO_PROFILE_TEMPLATE, userToLinkMap.getValue(), username);
            changedSource = changedSource.replace(userNotNotifiedBBCode, userBBCodeWithLink);
            changedSource = changedSource.replace(userNotifiedBBCode, userBBCodeWithLink);
        }
        return changedSource;
    }
}
