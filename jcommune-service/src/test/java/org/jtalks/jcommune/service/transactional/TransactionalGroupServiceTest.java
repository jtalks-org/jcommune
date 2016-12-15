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

import org.jtalks.common.model.entity.Group;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.common.validation.ValidationException;
import org.jtalks.jcommune.model.dao.GroupDao;
import org.jtalks.jcommune.model.dto.GroupAdministrationDto;
import org.jtalks.jcommune.service.GroupService;
import org.jtalks.jcommune.service.exceptions.OperationIsNotAllowedException;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;
import static io.qala.datagen.RandomShortApi.sample;
import static java.util.Collections.singletonList;
import static org.jtalks.jcommune.service.security.AdministrationGroup.PREDEFINED_GROUP_NAMES;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;

/**
 * @author Pavel Vervenko
 */
public class TransactionalGroupServiceTest {

    @Mock
    private GroupDao groupDao;
    private GroupService groupService;

    @BeforeMethod
    public void init() {
        initMocks(this);
        groupService = new TransactionalGroupService(groupDao, null, null);
    }

    @Test
    public void predefinedGroupMarkedAsNotEditable() throws Exception {
        when(groupDao.getGroupNamesWithCountOfUsers()).thenReturn(singletonList(new GroupAdministrationDto(sample(PREDEFINED_GROUP_NAMES), 1)));
        List<GroupAdministrationDto> groupNamesWithCountOfUsers = groupService.getGroupNamesWithCountOfUsers();
        GroupAdministrationDto groupDto = getOnlyElement(groupNamesWithCountOfUsers);
        assertFalse(groupDto.isEditable());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void throwsIfEditingNonExistingGroup() throws NotFoundException {
        when(groupDao.get(anyLong())).thenReturn(null);
        groupService.saveOrUpdate(new GroupAdministrationDto(100500L, "not exist", "not exists", 0));
    }

    @Test(expectedExceptions = ValidationException.class)
    public void throwsIfSavingNewGroupWithNotUniqueName() throws NotFoundException {
        when(groupDao.getGroupByName("existing name")).thenReturn(new Group("existing name"));
        groupService.saveOrUpdate(new GroupAdministrationDto(null, "existing name", "some description", 100));
    }

    @Test(expectedExceptions = OperationIsNotAllowedException.class)
    public void throwsIfEditingPredefinedGroup() throws NotFoundException {
        when(groupDao.get(1L)).thenReturn(new Group(sample(PREDEFINED_GROUP_NAMES)));
        groupService.saveOrUpdate(new GroupAdministrationDto(1L, "new group name", "some description", 100));
    }
}