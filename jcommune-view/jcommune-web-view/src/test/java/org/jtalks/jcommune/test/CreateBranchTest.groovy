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
package org.jtalks.jcommune.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.annotation.Resource
import javax.servlet.Filter

/**
 * @author Mikhail Stryzhonak
 */
@WebAppConfiguration
@ContextConfiguration(locations = [
        'classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml',
        'classpath:/org/jtalks/jcommune/model/entity/applicationContext-properties.xml',
        'classpath:/org/jtalks/jcommune/service/applicationContext-service.xml',
        'classpath:/org/jtalks/jcommune/service/security-service-context.xml',
        'classpath:/org/jtalks/jcommune/service/email-context.xml',
        'classpath:/org/jtalks/jcommune/web/applicationContext-controller.xml',
        'classpath:security-context.xml',
        'classpath:spring-dispatcher-servlet.xml',
        'classpath:/org/jtalks/jcommune/web/view/test-configuration.xml'
])
@TransactionConfiguration(transactionManager = 'transactionManager', defaultRollback = true)
@Transactional
class CreateBranchTest extends Specification {

//    @Autowired
//    private WebApplicationContext ctx
//    @Resource(name = 'testFilters')
//    List<Filter> filters
//
//    def setUp() {
//
//    }
}
