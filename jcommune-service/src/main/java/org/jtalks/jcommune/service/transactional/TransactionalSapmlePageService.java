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

import org.jtalks.jcommune.model.dao.SamplePageDao;
import org.jtalks.jcommune.model.entity.SamplePage;
import org.jtalks.jcommune.service.SamplePageService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

/**
 * SapmlePage service class. This class contains method needed to manipulate with SapmlePage persistent entity.
 *
 * @author Scherbakov Roman
 */
public class TransactionalSapmlePageService extends AbstractTransactionalEntityService<SamplePage, SamplePageDao> implements SamplePageService {

    public TransactionalSapmlePageService(SamplePageDao dao) {

        super(dao);
    }

    @Override
    public void updatePage(long pageId, String pageName, String pageContent) throws NotFoundException {

        SamplePage samplePage = get(pageId);
        samplePage.setContent(pageContent);
        samplePage.setName(pageName);
        this.getDao().update(samplePage);
    }
}
