package org.jtalks.jcommune.test.service

import org.jtalks.common.model.entity.Component
import org.jtalks.common.model.entity.ComponentType
import org.jtalks.jcommune.model.dao.ComponentDao
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author Mikhail Stryzhonok
 */
class ComponentService {

    @Autowired
    private ComponentDao componentDao

    def createForumComponent() {
        def component = new Component("Forum", "Jtalks forum", ComponentType.FORUM);
        componentDao.saveOrUpdate(component);
        componentDao.flush();
    }
}
