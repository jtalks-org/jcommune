package org.jtalks.jcommune.test.utils

import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import javax.servlet.Filter

class MockMvcFactoryBean implements FactoryBean<MockMvc> {
    @Autowired WebApplicationContext applicationContext
    @Autowired List<Filter> filters

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
