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

import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import javax.servlet.Filter

@CompileStatic
class MockMvcFactoryBean implements FactoryBean<MockMvc> {
    @Autowired WebApplicationContext applicationContext
    List<Filter> filters

    MockMvcFactoryBean(List<Filter> filters) {
        this.filters = filters
    }

    @Override
    MockMvc getObject() throws Exception {
        return MockMvcBuilders.webAppContextSetup(applicationContext)
                .addFilters(filters.toArray(new Filter[filters.size()])).build()
    }

    @Override
    Class<?> getObjectType() {
        return MockMvc
    }

    @Override
    boolean isSingleton() {
        return true
    }
}
