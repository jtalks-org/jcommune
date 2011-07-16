/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.poulpe.web.controller.component;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.web.controller.WindowManager;
import org.testng.annotations.Test;

/**
 * The test for {@link ItemPresenter} class.
 * @author Dmitriy Sukharev
 * 
 */
public class ItemPresenterTest {

    private ItemPresenter presenter = new ItemPresenter();

    private Component getFakeComponent(long id, String name, String description, ComponentType type) {
        Component comp = new Component();
        comp.setId(id);
        comp.setName(name);
        comp.setDescription(description);
        comp.setComponentType(type);
        return comp;
    }

    @Test
    public void saveComponentTest() throws Exception {
        ComponentService componentService = mock(ComponentService.class);
        ItemViewImpl view = mock(ItemViewImpl.class);
        WindowManager wm = mock(WindowManager.class);
        presenter.setComponentService(componentService);
        presenter.initView(view);
        presenter.setWindowManager(wm);
        Component fake = getFakeComponent(2L, "comp", "abc", ComponentType.ARTICLE);

        when(view.getCid()).thenReturn(fake.getId());
        when(view.getName()).thenReturn(fake.getName());
        when(view.getDescription()).thenReturn(fake.getDescription());
        when(view.getComponentType()).thenReturn(fake.getComponentType().toString());

        presenter.saveComponent();
        verify(componentService).saveComponent(argThat(new ComponentMatcher(fake)));
        verify(wm).closeWindow(view);
    }

    @Test
    public void getCidByNameTest() {
        ComponentService componentService = mock(ComponentService.class);
        presenter.setComponentService(componentService);
        List<Component> fakeList = new ArrayList<Component>();
        fakeList.add(getFakeComponent(0, "abc", "desc1", ComponentType.ARTICLE));
        fakeList.add(getFakeComponent(1, "abe", null, ComponentType.FORUM));

        when(componentService.getAll()).thenReturn(fakeList);

        assertEquals(presenter.getCidByName("abc"), 0);
        assertEquals(presenter.getCidByName("abe"), 1);
        assertEquals(presenter.getCidByName("abd"), -1);
    }
    
    @Test (expectedExceptions=IllegalArgumentException.class)
    public void getCidByNameException1Test() {
        presenter.getCidByName(null);
    }
    
    @Test (expectedExceptions=IllegalArgumentException.class)
    public void getCidByNameException2Test() {
        presenter.getCidByName("");
    }

    @Test
    public void getTypesTest() {
        ComponentService componentService = mock(ComponentService.class);
        presenter.setComponentService(componentService);
        Set<ComponentType> origTypes = new HashSet<ComponentType>(Arrays.asList(ComponentType
                .values()));

        when(componentService.getAvailableTypes()).thenReturn(origTypes);

        List<String> strTypes = presenter.getTypes();
        assertEquals(origTypes.size(), strTypes.size());
        for (ComponentType orig : origTypes) {
            assertTrue(strTypes.contains(orig.toString()));
        }
    }
}