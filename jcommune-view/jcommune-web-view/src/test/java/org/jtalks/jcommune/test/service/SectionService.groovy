package org.jtalks.jcommune.test.service

import org.jtalks.jcommune.model.dao.SectionDao
import org.jtalks.jcommune.test.model.Section
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author Mikhail Stryzhonok
 */
class SectionService {

    @Autowired
    private SectionDao sectionDao;

    def createSection(Section section) {
        org.jtalks.common.model.entity.Section commonSection =
                new org.jtalks.common.model.entity.Section(section.name, section.description)
        sectionDao.saveOrUpdate(commonSection);
        sectionDao.flush();
        return commonSection;
    }
}
