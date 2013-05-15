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
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class BBUserPreprocessor extends TextProcessorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BBUserPreprocessor.class);
    private UserService userService;
    private UserMentionService userMentionService;

    /**
     * @param userService to check users' existence
     * @param userMentionService to extract mentioned users
     */
    public BBUserPreprocessor(UserService userService, UserMentionService userMentionService) {
        this.userService = userService;
        this.userMentionService = userMentionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence process(CharSequence source) {
        String notProcessedSource = source.toString();
        List<String> mentionedUsers = userMentionService.extractMentionedUsers(notProcessedSource);
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
     * @param source will be changed and all mentioned users in it will contain link to theirs profiles
     * @param userToUserProfileLinkMap user to its profile map
     * @return source with users with attached links to profiles
     */
    private String addLinksToUserProfileForMentionedUsers(
            String source, Map<String, String> userToUserProfileLinkMap) {
        String changedSource = source;
        for (Map.Entry<String, String> userToLinkMap: userToUserProfileLinkMap.entrySet()) {
            String userBBCode = "[user]" + userToLinkMap.getKey() + "[/user]";
            String userBBCodeWithLink = "[user=" + userToLinkMap.getValue() + "]" + userToLinkMap.getKey() + "[/user]";
            changedSource = changedSource.replace(userBBCode, userBBCodeWithLink);
        }
        return changedSource;
    }
}
