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
package org.jtalks.jcommune.web.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mikhail Stryzhonok
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
        "classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml",
        "classpath:/org/jtalks/jcommune/model/entity/applicationContext-properties.xml",
        "classpath:/org/jtalks/jcommune/service/applicationContext-service.xml",
        "classpath:/org/jtalks/jcommune/service/security-service-context.xml",
        "classpath:/org/jtalks/jcommune/service/email-context.xml",
        "classpath:/org/jtalks/jcommune/web/applicationContext-controller.xml",
        "classpath:security-context.xml",
        "classpath:spring-dispatcher-servlet.xml",
        "classpath:/org/jtalks/jcommune/web/view/test-filter-configuration.xml"
})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class SectionControllerMockMvcTest extends AbstractTransactionalTestNGSpringContextTests {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @Resource(name = "testFilters")
    List<Filter> filters;

    @BeforeMethod
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(filters.toArray(new Filter[0])).build();

    }

    @Test
    public void test() throws Exception{
        mockMvc.perform(get("/").session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("sectionList", Collections.EMPTY_LIST))
                .andExpect(model().attribute("messagesCount", 0))
                .andExpect(model().attributeExists("registeredUsersCount", "visitors", "usersRegistered",
                        "visitorsRegistered", "visitorsGuests"));
    }
}

