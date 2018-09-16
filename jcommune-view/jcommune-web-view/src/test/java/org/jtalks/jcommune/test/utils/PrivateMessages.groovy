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
package org.jtalks.jcommune.test.utils

import org.jtalks.common.model.entity.User
import org.jtalks.jcommune.model.entity.PrivateMessage
import org.jtalks.jcommune.test.model.PrivateMessageDto

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic

/**
 * @author Evgeniy Cheban
 */
class PrivateMessages {

    static PrivateMessageDto randomDto(Map<String, Object> overrideDefaults = [:]) {
        Map<String, Object> defaults = [
                'title': randomAlphabetic(PrivateMessage.MAX_TITLE_LENGTH).toString(),
                'body':  randomAlphabetic(PrivateMessage.MAX_MESSAGE_LENGTH).toString(),
                'recipient': randomAlphabetic(User.USERNAME_MAX_LENGTH).toString()
        ]
        defaults.putAll(overrideDefaults)
        return new PrivateMessageDto(defaults)
    }
}
