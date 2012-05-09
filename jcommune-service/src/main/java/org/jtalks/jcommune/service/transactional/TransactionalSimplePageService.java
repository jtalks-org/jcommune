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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.SimplePageDao;
import org.jtalks.jcommune.model.entity.SimplePage;
import org.jtalks.jcommune.service.SimplePageService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SapmlePage service class. This class contains method needed to manipulate with SapmlePage persistent entity.
 *
 * @author Scherbakov Roman
 */
public class TransactionalSimplePageService extends AbstractTransactionalEntityService<SimplePage, SimplePageDao> implements SimplePageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public TransactionalSimplePageService(SimplePageDao simplePageDao) {
        super(simplePageDao);
    }

    /**
     * update pageName and pageContent by current SimplePage
     * @param pageId page_id
     * @param pageName name
     * @param pageContent post_content
     */

    public void updatePage(long pageId, String pageName, String pageContent) throws NotFoundException {

        SimplePage simplePage = null;
        try{
            simplePage = get(pageId);
        }catch (NotFoundException e){
            e.printStackTrace();
        }
        assert simplePage != null;
        simplePage.setContent(pageContent);
        simplePage.setName(pageName);
        this.getDao().update(simplePage);
    }

    /**
     * get SimplePage by name
     * @param pathName path name
     * @return SimplePage with current name
     */

    public SimplePage getPageByPathName(String pathName) throws NotFoundException {
        SimplePage simplePage = this.getDao().getPageByPathName(pathName);
        if (simplePage == null) {
            String msg = "SimplePage " + pathName + " not found.";
            logger.info(msg);
            throw new NotFoundException(msg);
        }
        return simplePage;
    }


    /**
     * create SimplePage
     */

    public SimplePage createPage(SimplePage simplePage) {
        this.getDao().update(simplePage);
        logger.info("SimplePage registered: {}", simplePage.getName());
        return simplePage;
    }

}