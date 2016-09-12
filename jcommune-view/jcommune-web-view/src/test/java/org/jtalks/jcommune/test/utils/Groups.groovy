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

import org.jtalks.common.model.entity.Group
import org.jtalks.jcommune.model.dao.GroupDao
import org.jtalks.jcommune.model.dto.GroupAdministrationDto
import org.jtalks.jcommune.test.utils.assertions.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc

import javax.servlet.http.HttpSession

import static io.qala.datagen.RandomShortApi.alphanumeric
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

/**
 * @author Oleg Tkachenko
 */
class Groups {

    @Autowired
    GroupDao groupDao
    @Autowired
    MockMvc mockMvc

    boolean isExist(String username) {
        return groupDao.getByName(username) != null
    }

    def getGroupsWithCountOfUsers(HttpSession session) {
        def result = mockMvc.perform(get('/group/list')
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        Assert.assertView(result, "groupAdministration")
    }

    void create(Group group, HttpSession session) {
        def groupDto = new GroupAdministrationDto()
        groupDto.name = group.name
        groupDto.description = group.description
        def result = mockMvc.perform(post("/group/new")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session as MockHttpSession)
                .content(JsonResponseUtils.pojoToJsonString(groupDto)))
                .andReturn()

        Assert.assertJsonResponseResult(result)
    }

    public static Group random() {
        return new Group(alphanumeric(1, Group.GROUP_NAME_MAX_LENGTH), alphanumeric(0, Group.GROUP_DESCRIPTION_MAX_LENGTH))
    }

    void assertDoesNotExist(Group group) {
        def groups = groupDao.getAll()
        assert groups.find { group.name == group.name } != null,
                "Found a group with name ${group.name} while it wasn't expected it exists"
    }
}
