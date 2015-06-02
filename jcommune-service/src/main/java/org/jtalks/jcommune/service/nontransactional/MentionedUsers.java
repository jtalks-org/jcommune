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
 */
public class MentionedUsers {
    public static final String MENTIONED_NOT_NOTIFIED_USER_TEMPLATE = "[user]%s[/user]";
    public static final String MENTIONED_AND_NOTIFIED_USER_TEMPLATE = "[user notified=true]%s[/user]";
    public static final String USER_WITH_LINK_TO_PROFILE_TEMPLATE = "[user=%s]%s[/user]";
    private static final Logger LOGGER = LoggerFactory.getLogger(MentionedUsers.class);
    private static final Pattern ALL_MENTIONED_USERS_PATTERN =
            Pattern.compile("\\[user\\].*?\\[/user\\]|\\[user notified=true\\].*?\\[/user\\]");
    private static final Pattern MENTIONED_AND_NOT_NOTIFIED_USERS_PATTERN =
            Pattern.compile("\\[user\\].*?\\[/user\\]");
    private static final String CLOSE_BRACKET_CODE_PLACEHOLDER = "@w0956756wo@";
    private static final String OPEN_BRACKET_CODE_PLACEHOLDER = "@ywdffgg434y@";
    private static final String SLASH_CODE_PLACEHOLDER = "14@123435vggv4f";
    private static final String LOWER_THEN_PLACEHOLDER = "gertfgertgf@@@@@#4324234";
    private static final Map<String, String> CHARS_PLACEHOLDERS = new HashMap<>();

    static {
        CHARS_PLACEHOLDERS.put("[", OPEN_BRACKET_CODE_PLACEHOLDER);
        CHARS_PLACEHOLDERS.put("]", CLOSE_BRACKET_CODE_PLACEHOLDER);
        CHARS_PLACEHOLDERS.put("\\", SLASH_CODE_PLACEHOLDER);
        CHARS_PLACEHOLDERS.put("<", LOWER_THEN_PLACEHOLDER);
    }

    private final Map<String, String> encodedUserNames = new HashMap<>();
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
     * Get list of the users which have to receive notification
     *
     * @param userDao service for user related operations
     * @return list of the users which should be notified that they were mentioned
     * @throws IllegalStateException when instance was not created based on Post object
     */
    public List<JCUser> getNewUsersToNotify(UserDao userDao) {
        if (post == null) {
            throw new IllegalStateException("To call this method you should create class with Post type parameter");
        }

        Set<String> mentionedUsersNames = extractNotNotifiedMentionedUsers(postContent);
        if (!CollectionUtils.isEmpty(mentionedUsersNames)) {
            return getNewUsersToNotify(mentionedUsersNames, userDao);
        }

        return new ArrayList<>();
    }

    /**
     * Marks all users in user BB codes as already notified
     *
     * @param postDao service for post related operations
     * @throws IllegalStateException when instance was not created based on Post object
     */
    public void markUsersAsAlreadyNotified(PostDao postDao) {
        if (post == null) {
            throw new IllegalStateException("To call this method you should create class with Post type parameter");
        }

        Set<String> mentionedUsersNames = extractNotNotifiedMentionedUsers(postContent);
        if (!CollectionUtils.isEmpty(mentionedUsersNames)) {
            markUsersAsAlreadyNotified(mentionedUsersNames, postDao);
        }
    }

    /**
     * Returns post text with BB codes replaced by user profile links
     *
     * @param userDao service for working with user objects
     * @return text with BB codes replaced by user profile links
     */
    public String getTextWithProcessedUserTags(UserDao userDao) {
        Set<String> mentionedUsers = extractAllMentionedUsers(postContent);
        Map<String, String> userToUserProfileLinkMap = new HashMap<>();
        for (String mentionedUser : mentionedUsers) {
            String mentionedUserProfileLink = getLinkToUserProfile(mentionedUser, userDao);
            userToUserProfileLinkMap.put(mentionedUser, mentionedUserProfileLink);
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
     * @param mentionedUserPattern     pattern to extract mentioned user in given text
     * @return extracted users' names
     */
    private Set<String> extractMentionedUsers(String canContainMentionedUsers, Pattern mentionedUserPattern) {
        if (!StringUtils.isEmpty(canContainMentionedUsers)) {
            Matcher matcher = mentionedUserPattern.matcher(canContainMentionedUsers);
            Set<String> mentionedUsernames = new HashSet<>();
            while (matcher.find()) {
                String userBBCode = matcher.group();
                String mentionedUser = userBBCode.replaceAll("\\[.*?\\]", StringUtils.EMPTY);
                mentionedUsernames.add(replacePlaceholdersWithChars(mentionedUser));
            }
            return mentionedUsernames;
        }
        return Collections.emptySet();
    }

    private String replacePlaceholdersWithChars(String userNameWithPlaceholders) {
        String formattedUserName = userNameWithPlaceholders;
        for (Map.Entry<String, String> decodeEntry : CHARS_PLACEHOLDERS.entrySet()) {
            formattedUserName = formattedUserName.replace(decodeEntry.getValue(), decodeEntry.getKey());
        }
        encodedUserNames.put(formattedUserName, userNameWithPlaceholders);
        return formattedUserName;
    }

    private String encodeUsername(String decodedUsername) {
        return encodedUserNames.get(decodedUsername);
    }

    /**
     * Gets list of users which should be notified
     *
     * @param mentionedUsernames the set of names of mentioned users
     * @param userDao            service for working with JCUser objects
     * @return list of users which should be notified
     */
    private List<JCUser> getNewUsersToNotify(Set<String> mentionedUsernames, UserDao userDao) {
        List<JCUser> mentionedUsers = userDao.getByUsernames(mentionedUsernames);
        List<JCUser> usersToNotify = new ArrayList<>();

        for (JCUser mentionedUser : mentionedUsers) {
            if (shouldNotificationBeSent(mentionedUser)) {
                usersToNotify.add(mentionedUser);
            }
        }

        return usersToNotify;
    }

    /**
     * Determines if it is needed to send notification to the user
     *
     * @param mentionedUser this user was mentioned
     * @return true if we need to send notification and false otherwise
     */
    private boolean shouldNotificationBeSent(JCUser mentionedUser) {
        boolean isOtherNotificationAlreadySent = post.getSubscribers().contains(mentionedUser);
        return !isOtherNotificationAlreadySent && mentionedUser.isMentioningNotificationsEnabled();
    }

    /**
     * Mark user tags as already notified
     *
     * @param mentionedUsernames the set of names of mentioned users
     * @param postDao            service for working with Post objects
     */
    private void markUsersAsAlreadyNotified(Set<String> mentionedUsernames, PostDao postDao) {
        for (String user : mentionedUsernames) {
            markUserAsAlreadyNotified(user, postDao);
        }
    }

    /**
     * Change BB user tag to mark user as already notified
     *
     * @param username this user was mentioned
     * @param postDao  service for working with Post objects
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
        if (user != null && user.getUsername().equals(username)) {
            userPofileLink = getApplicationNameAsContextPath() + "/users/" + user.getId();
            LOGGER.trace("{} has the following url of profile - {}", username, userPofileLink);
        } else {
            LOGGER.trace("Mentioned user wasn't find: {}", username);
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
     * @param source                   will be changed and all mentioned users in it will contain links to their
     *                                 profiles
     * @param userToUserProfileLinkMap user to it links of profile map
     * @return source with users with attached links to profiles
     */
    private String addLinksToUserProfileForMentionedUsers(
            String source, Map<String, String> userToUserProfileLinkMap) {
        String changedSource = source;
        for (Map.Entry<String, String> userToLinkMap : userToUserProfileLinkMap.entrySet()) {
            String username = encodeUsername(userToLinkMap.getKey());
            String userNotNotifiedBBCode = format(MENTIONED_NOT_NOTIFIED_USER_TEMPLATE, username);
            String userNotifiedBBCode = format(MENTIONED_AND_NOTIFIED_USER_TEMPLATE, username);
            String userBBCodeWithLink = username;
            if (userToLinkMap.getValue() != null) {
                userBBCodeWithLink = format(USER_WITH_LINK_TO_PROFILE_TEMPLATE, userToLinkMap.getValue(), username);
            }
            changedSource = changedSource.replace(userNotNotifiedBBCode, userBBCodeWithLink);
            changedSource = changedSource.replace(userNotifiedBBCode, userBBCodeWithLink);
        }
        return changedSource;
    }
}
