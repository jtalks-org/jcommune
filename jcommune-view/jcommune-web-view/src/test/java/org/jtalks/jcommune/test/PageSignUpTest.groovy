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

import org.jtalks.jcommune.test.utils.Users
import org.jtalks.jcommune.test.utils.exceptions.WrongResponseException
import org.jtalks.jcommune.web.controller.UserController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

import static org.junit.Assert.assertEquals;

/**
 * @author Mikhail Stryzhonok
 */
class PageSignUpTest extends SignUpTest {

    @Override
    void initNonDefaultFailParameters() {
        honeypotErrorResponse = UserController.REG_SERVICE_HONEYPOT_FILLED_ERROR_URL;
    }

    @Autowired
    @Qualifier('modelAndViewUsers')
    @Override
    void setUsers(Users users) {
        super.setUsers(users)
    }

    @Override
    boolean isNonDefaultFailParametersEquals(Object expected, WrongResponseException exception) {
        if (expected instanceof String) {
            return expected.equals(exception.actual);
        } else {
            throw new IllegalArgumentException('For page signup test expected should be String')
        }
    }
}
