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

import org.jtalks.jcommune.test.model.Branch
import org.jtalks.jcommune.test.model.Section
import org.jtalks.jcommune.test.service.ComponentService
import org.jtalks.jcommune.test.service.SectionService
import org.jtalks.jcommune.test.utils.assertions.Assert
import org.jtalks.jcommune.web.dto.BranchDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc

import javax.servlet.http.HttpSession

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

/**
 * @author Mikhail Stryzhonok
 */
class Branches {

    private MockMvc mockMvc

    @Autowired
    private ComponentService componentService
    @Autowired
    private SectionService sectionService

    def create(Branch branch, HttpSession session) {
        def section = sectionService.createSection(new Section())
        def branchDto = new BranchDto()
        branchDto.name = branch.name
        branchDto.description = branch.description
        branchDto.sectionId = section.id
        def result = mockMvc.perform(post("/branch/new")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session as MockHttpSession)
                .content(JsonResponseUtils.pojoToJsonString(branchDto)))
                .andReturn()

        Assert.assertJsonResponseResult(result)
    }
}
