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
package org.jtalks.jcommune.test

import com.google.common.collect.ImmutableMap
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus
import org.jtalks.jcommune.test.utils.Users
import org.jtalks.jcommune.test.utils.exceptions.WrongResponseException
import org.jtalks.jcommune.web.controller.UserController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

import static junit.framework.Assert.assertEquals
import static org.jtalks.jcommune.test.utils.JsonResponseUtils.jsonResponseToString

/**
 * @author Mikhail Stryzhonok
 */
class PopUpSignUpTest extends SignUpTest {

    @Override
    void initNonDefaultFailParameters() {
        honeypotErrorResponse = new JsonResponse(JsonResponseStatus.FAIL,
                new ImmutableMap.Builder<String, String>()
                        .put(UserController.CUSTOM_ERROR, UserController.HONEYPOT_CAPTCHA_ERROR).build())
    }

    @Override
    boolean isNonDefaultFailParametersEquals(def Object expected, WrongResponseException exception) {
        if (expected instanceof JsonResponse) {
            return jsonResponseToString(expected as JsonResponse).equals(exception.actual)
        } else {
            throw new IllegalArgumentException('For popup signup test non-default expected should be JsonResponse')
        }
    }

    @Autowired
    @Qualifier('jsonResponseUsers')
    @Override
    void setUsers(Users users) {
        super.setUsers(users)
    }
}
