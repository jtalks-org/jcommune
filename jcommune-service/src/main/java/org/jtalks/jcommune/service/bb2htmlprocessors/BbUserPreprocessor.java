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

package org.jtalks.jcommune.service.bb2htmlprocessors;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.UserMentionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ru.perm.kefir.bbcode.TextProcessorAdapter;

/**
 * Process for [user][/user] code. It adds link to user mentioned in tag before starting
 * converting to HTML tags.  
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class BbUserPreprocessor extends TextProcessorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BbUserPreprocessor.class);
    private static final String MENTIONED_AND_NOT_NOTIFIED_USER_TEMPLATE = "[user]%s[/user]";
    private static final String MENTIONED_AND_NOTIFIED_USER_TEMPLATE = "[user notified=true]%s[/user]";
    private static final String USER_WITH_LINK_TO_PROFILE_TEMPLATE = "[user=%s]%s[/user]";
    private UserService userService;
    private UserMentionService userMentionService;

    /**
     * @param userService to check users' existence
     * @param userMentionService to extract mentioned users
     */
    public BbUserPreprocessor(UserService userService, UserMentionService userMentionService) {
        this.userService = userService;
        this.userMentionService = userMentionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence process(CharSequence source) {
        String notProcessedSource = source.toString();
        List<String> mentionedUsers = userMentionService.extractAllMentionedUsers(notProcessedSource);
        Map<String, String> userToUserProfileLinkMap = new HashMap<String, String>();
        for (String mentionedUser: mentionedUsers) {
            String mentionedUserProfileLink = getLinkToUserProfile(mentionedUser);
            if (mentionedUserProfileLink != null) {
                userToUserProfileLinkMap.put(mentionedUser, mentionedUserProfileLink);
            }
        }
        return addLinksToUserProfileForMentionedUsers(notProcessedSource, userToUserProfileLinkMap);
    }
    
    /**
     * Get link to user's profile.
     * 
     * @param username user's name
     * @return null when user doesn't exist, otherwise link to user's profile
     */
    private String getLinkToUserProfile(String username) {
        String userPofileLink = null;
        try {
            JCUser user = userService.getByUsername(username);
            userPofileLink = getDeploymentRootUrlWithoutPort() + "/users/" + user.getId();
            LOGGER.debug(username + " has the following url of profile -" + userPofileLink);
        } catch (NotFoundException e) {
            LOGGER.debug("Mentioned user wasn't find", e);
        }
        return userPofileLink;
    }
    
    /**
     * Returns current deployment root without port for using as label link, for example.
     *
     * @return current deployment root without port, e.g. "http://myhost.com/mycoolforum"
     */
    private String getDeploymentRootUrlWithoutPort() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        return request.getScheme()
                + "://" + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
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
            String userNotNotifiedBBCode = format(MENTIONED_AND_NOT_NOTIFIED_USER_TEMPLATE, username);
            String userNotifiedBBCode = format(MENTIONED_AND_NOTIFIED_USER_TEMPLATE, username);
            String userBBCodeWithLink = format(
                    USER_WITH_LINK_TO_PROFILE_TEMPLATE, userToLinkMap.getValue(), username);
            changedSource = changedSource.replace(userNotNotifiedBBCode, userBBCodeWithLink);
            changedSource = changedSource.replace(userNotifiedBBCode, userBBCodeWithLink);
        }
        return changedSource;
    }
}
