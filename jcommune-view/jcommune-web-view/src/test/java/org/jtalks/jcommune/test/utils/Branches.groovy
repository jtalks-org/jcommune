package org.jtalks.jcommune.test.utils

import org.jtalks.jcommune.test.model.Branch
import org.jtalks.jcommune.test.model.Section
import org.jtalks.jcommune.test.service.ComponentService
import org.jtalks.jcommune.test.service.SectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc

import javax.servlet.http.HttpSession

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
        componentService.createForumComponent()
        def section = sectionService.createSection(new Section())
        mockMvc.perform(post("/branch/new").session(session as MockHttpSession)
                .param("branchDto.name", branch.name)
                .param("branchDto.description", branch.description)
                .param("branchDto.sectionId", section.id.toString())).andExpect(status().isOk())
    }
}
