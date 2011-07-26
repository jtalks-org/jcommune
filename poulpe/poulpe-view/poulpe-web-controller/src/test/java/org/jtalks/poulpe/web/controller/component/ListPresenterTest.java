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
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.web.controller.DialogManager;
import org.jtalks.poulpe.web.controller.WindowManager;
import org.jtalks.poulpe.web.controller.component.ListPresenter.DeletePerformable;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * The test for {@link ListPresenter} class.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ListPresenterTest {

    private ListPresenter presenter;

    @BeforeTest public void setUp() {
        presenter = new ListPresenter();
    }

    /** Tests view initialisation. */
    @Test public void initListViewTest() {
        ComponentService componentService = mock(ComponentService.class);
        ListView view = mock(ListView.class);
        presenter.setComponentService(componentService);
        List<Component> fake = getFakeComponents();

        when(componentService.getAll()).thenReturn(fake);
        presenter.initView(view);
        verify(view).createModel(argThat(new ComponentListMatcher(fake)));
    }

    @Test public void addComponentTest() {
        ListView view = mock(ListView.class);
        WindowManager wm = mock(WindowManager.class);
        ComponentService componentService = mock(ComponentService.class);
        presenter.setComponentService(componentService);
        presenter.initView(view);
        presenter.setWindowManager(wm);
        ArgumentCaptor<Long> argument1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Object> argument2 = ArgumentCaptor.forClass(Object.class);

        presenter.addComponent();
        verify(wm).showEditComponentWindow(argument1.capture(), argument2.capture());
        assertEquals(argument1.getValue(), new Long(-1L));
    }

    @Test public void editComponentTest() {
        ListView view = mock(ListView.class);
        WindowManager wm = mock(WindowManager.class);
        presenter.initView(view);
        presenter.setWindowManager(wm);
        ArgumentCaptor<Long> argument1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Object> argument2 = ArgumentCaptor.forClass(Object.class);
        Component fake = getFakeComponent(10L, "Fake1", "Desc1", ComponentType.ARTICLE);

        presenter.editComponent(fake);
        verify(wm).showEditComponentWindow(argument1.capture(), argument2.capture());
        assertEquals(argument1.getValue(), new Long(fake.getId()));
    }

    @Test public void deleteComponentTest() {
        ComponentService componentService = mock(ComponentService.class);
        ListView view = mock(ListView.class);
        DialogManager dm = mock(DialogManager.class);
        presenter.setDialogManager(dm);
        presenter.setComponentService(componentService);
        presenter.initView(view);
        Component fake = getFakeComponent(1L, "Fake1", "Desc1", ComponentType.ARTICLE);

        ArgumentCaptor<String> argument1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<DialogManager.Performable> argument2 = ArgumentCaptor
                .forClass(DialogManager.Performable.class);

        when(view.getSelectedItem()).thenReturn(fake);
        when(view.hasSelectedItem()).thenReturn(true);

        presenter.deleteComponent();

        verify(dm).confirmDeletion(argument1.capture(), argument2.capture());
        assertEquals(argument1.getValue(), fake.getName());
        assertNotNull(argument2.getValue());

        // and another check
        when(view.hasSelectedItem()).thenReturn(false);
        presenter.deleteComponent();
        verify(dm).notify("item.no.selected.item");
    }

    /** Tests executed method of the {@link DeletePerformable} inner class. */
    @SuppressWarnings("unchecked")
    @Test public void executeDcTest() {
        ComponentService componentService = mock(ComponentService.class);
        ListView view = mock(ListView.class);
        presenter.setComponentService(componentService);
        presenter.initView(view);

        final int id = 1;
        List<Component> fakeList = getFakeComponents();
        Component fake = fakeList.get(id);

        when(componentService.getAll()).thenReturn(fakeList);
        when(view.getSelectedItem()).thenReturn(fake);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        presenter.new DeletePerformable().execute();

        verify(componentService).deleteComponent(fake);
        verify(view).updateList(argument.capture());
    }

    private Component getFakeComponent(long id, String name, String description, ComponentType type) {
        Component comp = new Component();
        comp.setId(id);
        comp.setName(name);
        comp.setDescription(description);
        comp.setComponentType(type);
        return comp;
    }

    private List<Component> getFakeComponents() {
        List<Component> list = new ArrayList<Component>();
        ComponentType[] types = ComponentType.values();
        for (int i = 0; i < types.length; i++) {
            list.add(getFakeComponent(i, "Fake" + i, "Desc" + i, types[i]));
        }
        return list;
    }
}