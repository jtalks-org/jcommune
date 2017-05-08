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

import org.codehaus.jackson.map.ObjectMapper
import org.jtalks.common.model.entity.Group
import org.jtalks.jcommune.model.dao.GroupDao
import org.jtalks.jcommune.model.dto.UserDto
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse
import org.jtalks.jcommune.test.model.GroupDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult

import javax.servlet.http.HttpSession

import static io.qala.datagen.RandomShortApi.alphanumeric
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
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

    long create(GroupDto group, HttpSession session) {
        def result = mockMvc.perform(post("/group")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session as MockHttpSession)
                .content(JsonResponseUtils.pojoToJsonString(group)))
                .andReturn()
        isAccessGranted(result)
        assertJsonResponseResult(result)
        groupDao.getByName(group.name).get(0).id
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

    MvcResult getPagedGroupUsers(long groupId, long page, HttpSession session) {
        def result = mockMvc.perform(get("/group/${groupId}?page=${page}")
                .session(session as MockHttpSession))
                .andReturn()
        return result
    }

    JsonResponse getUserNotInGroupByPattern(long groupId, String pattern, HttpSession session) {
        MvcResult result = mockMvc.perform(get("/user/?notInGroupId=${groupId}&pattern=${pattern}")
                .session(session as MockHttpSession))
                .andReturn()
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result.response.contentAsString, JsonResponse.class)
    }

    void assertGroupUserPage(page, groupUserList) {
        Page<UserDto> pagedGroupUsers = page.modelAndView.getModel().get("groupUsersPage")
        assertThat(pagedGroupUsers.content.size(), is(groupUserList.size()));
        for (int i=0; i<pagedGroupUsers.content.size(); i++) {
            assertThat(pagedGroupUsers.content.get(i).username, is(groupUserList.get(i).username))
        }
    }

    void assertUserDtoList(List expected, List actual) {
        assertThat(expected.size(), is(actual.size()));
        for (int i=0; i<expected.size(); i++) {
            assertThat(expected.get(i).username, is(actual.get(i).username))
        }
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
