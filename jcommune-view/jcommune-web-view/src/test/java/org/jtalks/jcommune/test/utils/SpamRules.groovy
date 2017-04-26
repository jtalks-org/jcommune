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
import org.codehaus.jackson.type.TypeReference
import org.jtalks.jcommune.model.dto.SpamRuleDto
import org.jtalks.jcommune.test.utils.exceptions.ProcessingException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult

import javax.servlet.http.HttpSession

import static io.qala.datagen.RandomShortApi.alphanumeric
import static io.qala.datagen.RandomShortApi.numeric
import static org.jtalks.jcommune.test.utils.assertions.Assert.isAccessGranted
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
/**
 * @author Oleg Tkachenko
 */
class SpamRules {
    private final @Autowired MockMvc mockMvc
    private final @Autowired Users transactionalUsers


    SpamRuleDto createNewRule(SpamRuleDto ruleDto = randomRule()) {
        this.post(transactionalUsers.signInAsAdmin(), ruleDto)
    }

    SpamRuleDto createNewRule(String regex){
        def dto = randomRule()
        dto.regex = regex
        createNewRule(dto)
    }

    SpamRuleDto get(HttpSession session, long id){
        def result = mockMvc.perform(get("/api/spam-rules/${id}")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)).andReturn()
        isAccessGranted(result)
        getDtoFromJson(result)
    }

    List<SpamRuleDto> getAll(HttpSession session){
        def result = mockMvc.perform(get("/api/spam-rules/")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)).andReturn()
        isAccessGranted(result)
        def mapper = new ObjectMapper()
        def json = mapper.readTree(result.response.contentAsString).get("result")
        mapper.readValue(json, new TypeReference<List<SpamRuleDto>>(){})
    }

    SpamRuleDto post(HttpSession session, SpamRuleDto rule){
        def result = mockMvc.perform(post('/api/spam-rules/')
                .session(session as MockHttpSession)
                .content(JsonResponseUtils.pojoToJsonString(rule))
                .contentType(MediaType.APPLICATION_JSON)).andReturn()
        isAccessGranted(result)
        getDtoFromJson(result)
    }

    def put(HttpSession session, SpamRuleDto rule){
        def result = mockMvc.perform(put("/api/spam-rules/${rule.id}")
                .session(session as MockHttpSession)
                .content(JsonResponseUtils.pojoToJsonString(rule))
                .contentType(MediaType.APPLICATION_JSON)).andReturn()
        isAccessGranted(result)
        getDtoFromJson(result)
    }

    def delete(HttpSession session, long id){
        def result = mockMvc.perform(delete("/api/spam-rules/${id}")
                .session(session as MockHttpSession)).andReturn()
        isAccessGranted(result)
    }

    static SpamRuleDto randomRule(){
        new SpamRuleDto(numeric(3) as Long, alphanumeric(255), alphanumeric(255), true)
    }

    private SpamRuleDto getDtoFromJson(MvcResult result) {
        try {
            def mapper = new ObjectMapper()
            def json = mapper.readTree(result.response.contentAsString).get("result")
            mapper.readValue(json, SpamRuleDto)
        }catch (IOException e){
            throw new ProcessingException("Cannot parse SpamRuleDto from JsonResponse, response = [" + result.response.contentAsString + "]");
        }
    }

    void assertExists(String regex){
        def rules = getAll(transactionalUsers.signInAsAdmin())
        assert rules.find { it.regex == regex } != null,
                "Spam rule with regex ${regex} is not found while it was expected it exists"
    }

    void assertNotExists(String regex){
        def rules = getAll(transactionalUsers.signInAsAdmin())
        assert rules.find { it.regex == regex } == null,
                "Spam rule with regex ${regex} is found while it wasn't expected it exists"
    }
}
