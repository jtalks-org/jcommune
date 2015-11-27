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
