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
import org.jtalks.jcommune.test.model.GroupDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc
import javax.servlet.http.HttpSession

import static io.qala.datagen.RandomShortApi.alphanumeric
import static org.jtalks.common.model.entity.Group.GROUP_DESCRIPTION_MAX_LENGTH
import static org.jtalks.common.model.entity.Group.GROUP_NAME_MAX_LENGTH
import static org.jtalks.jcommune.test.utils.assertions.Assert.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

/**
 * @author Oleg Tkachenko
 */
class Groups {

    @Autowired
    GroupDao groupDao
    @Autowired
    MockMvc mockMvc

    boolean isExist(String groupName) {
        groupDao.getByName(groupName)
    }

    def showGroupAdministrationPage(HttpSession session) {
        def result = mockMvc.perform(get('/group/list')
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        isAccessGranted(result)
        assertView(result, "groupAdministration")
    }

    void create(GroupDto group, HttpSession session) {
        def result = mockMvc.perform(post("/group")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session as MockHttpSession)
                .content(JsonResponseUtils.pojoToJsonString(group)))
                .andReturn()
        isAccessGranted(result)
        assertJsonResponseResult(result)
    }

    void edit(GroupDto group, HttpSession session) {
        def result = mockMvc.perform(put("/group/" + group.id)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session as MockHttpSession)
                .content(JsonResponseUtils.pojoToJsonString(group)))
                .andReturn()
        isAccessGranted(result)
        assertJsonResponseResult(result)
    }

    void delete(long groupId, HttpSession session) {
        def result = mockMvc.perform(delete("/group/${groupId}")
                .session(session as MockHttpSession))
                .andReturn()
        isAccessGranted(result)
        assertJsonResponseResult(result)
    }

    void assertDoesNotExist(String groupName) {
        def groups = groupDao.getAll()
        assert groups.find { it.name == groupName } == null,
                "Found a group with name ${groupName} while it wasn't expected it exists"
    }

    static Group random() {
        return new Group(alphanumeric(GROUP_NAME_MAX_LENGTH), alphanumeric(GROUP_DESCRIPTION_MAX_LENGTH))
    }

    static GroupDto randomDto(Map<String, Object> overrideDefaults = [:]) {
        Map<String, Object> defaults = [
                name        : alphanumeric(GROUP_NAME_MAX_LENGTH).toString(),
                description : alphanumeric(GROUP_DESCRIPTION_MAX_LENGTH).toString()]
        defaults.putAll(overrideDefaults)
        return new GroupDto(defaults)
    }
}
