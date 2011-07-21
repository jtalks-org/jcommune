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
import static org.mockito.Mockito.inOrder;
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
import org.jtalks.poulpe.web.controller.WindowManager;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


/**
 * The test for {@link ListPresenter} class.
 * @author Dmitriy Sukharev
 *
 */
public class ListPresenterTest {
    
    private ListPresenter presenter;

    @BeforeTest
    protected void setUp() {
        presenter = new ListPresenter();
    }
    
    @Test public void initListViewTest() {        
        ComponentService componentService = mock(ComponentService.class);
        ListView view = mock(ListView.class);
        presenter.setComponentService(componentService);
        List<Component> fake = getFakeComponents();
        
        when(componentService.getAll()).thenReturn(fake);
        presenter.initView(view);        
        verify(view).createModel(argThat(new ComponentListMatcher(fake)));
    }
    
    @Test (enabled=false)
    public void addComponentTest() {
        ListView view = mock(ListView.class);
        WindowManager wm = mock(WindowManager.class);
        ComponentService componentService = mock(ComponentService.class);
        presenter.setComponentService(componentService);
        presenter.initView(view);        
        presenter.setWindowManager(wm);
        ArgumentCaptor<Long> argument1 = ArgumentCaptor.forClass(Long.class);
        // damn, it's implementation dependent.
        // upd: now it's independent, but too general.
        ArgumentCaptor<Object> argument2 =
            ArgumentCaptor.forClass(Object.class);        
        
        presenter.addComponent();
        verify(wm).showEditComponentWindow(
                argument1.capture(), argument2.capture());
        assertEquals(argument1.getValue(), new Long(-1L));
        assertNotNull(argument2.getValue());
    }
    
    @Test (enabled=false)
    public void editComponentTest() {
        ListView view = mock(ListView.class);
        WindowManager wm = mock(WindowManager.class);
        presenter.initView(view);
        presenter.setWindowManager(wm);
        ArgumentCaptor<Long> argument1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Object> argument2 = ArgumentCaptor.forClass(Object.class);
        Component fake = getFakeComponent(10L, "Fake1", "Desc1", ComponentType.ARTICLE);

        when(view.getSelectedItem()).thenReturn(fake);
        presenter.editComponent();
        verify(wm).showEditComponentWindow(argument1.capture(), argument2.capture());
        assertEquals(argument1.getValue(), new Long(fake.getId()));
        assertNotNull(argument2.getValue());
    }
    
    @Test (enabled=false)
    public void deleteComponentTest() {
        ComponentService componentService = mock(ComponentService.class);
        ListView view = mock(ListView.class);
        presenter.setComponentService(componentService);
        presenter.initView(view);        
        Component fake = getFakeComponent(1L, "Fake1", "Desc1", ComponentType.ARTICLE);
        
        when(view.getSelectedItem()).thenReturn(fake);
        when(componentService.getAll()).thenReturn(new ArrayList<Component>());
        
        presenter.deleteComponent();
        
        InOrder inOrder = inOrder(componentService, view);
        inOrder.verify(componentService).deleteComponent(argThat(new ComponentMatcher(fake)));
        inOrder.verify(view).updateList(presenter.getComponents());
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

class ComponentListMatcher extends ArgumentMatcher<List<Component>> {
    private List<Component> list;
    
    public ComponentListMatcher(List<Component> list) {
        this.list = list;
    }

    @Override
    public boolean matches(Object item) {
        List<?> list2 = (List<?>) item;
        assertEquals(list.size(), list2.size());
        boolean isMatch = true;
        for (int i = 0; i < list.size(); i++) {
            Component comp = list.get(i);
            Component comp2 = (Component) list2.get(i);
            isMatch &= comp.getId() == comp2.getId()
                    && comp.getName().equals(comp2.getName())
                    && comp.getDescription().equals(comp2.getDescription())
                    && comp.getComponentType().equals(comp2.getComponentType());
        }
        return isMatch;
    }

}