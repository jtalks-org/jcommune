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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.service.exceptions.NotFoundException;
import org.jtalks.poulpe.web.controller.DialogManager;
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
    
    private List<Component> getFakeComponents() {
        List<Component> list = new ArrayList<Component>();
        ComponentType[] types = ComponentType.values();
        for (int i = 0; i < types.length; i++) {
            list.add(getFakeComponent(i, "Fake" + i, "Desc" + i, types[i]));
        }
        return list;
    }
    
    /**
     * Tests if the componentId is correct id.
     * @throws NotFoundException
     */
    @Test
    public void initViewTest() throws NotFoundException {
        ComponentService componentService = mock(ComponentService.class);
        ItemViewImpl view = mock(ItemViewImpl.class);
        presenter.setComponentService(componentService);
        
        final int id = 0;
        List<Component> fake = getFakeComponents();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("componentId", (long)id);
        
        when(componentService.getAll()).thenReturn(fake);
        when(componentService.get((long) id)).thenReturn(fake.get(id));
        when(view.getArgs()).thenReturn(map);
        
        presenter.initView(view);
        
        verify(view).setCid(fake.get(id).getId());
        verify(view).setName(fake.get(id).getName());
        verify(view).setDescription(fake.get(id).getDescription());
        verify(view).setComponentType(fake.get(id).getComponentType().toString());
    }
    
    /**
     * Tests if componentId is -1L (new element)
     * @throws NotFoundException
     */
    @Test
    public void initViewTest2() {
        ComponentService componentService = mock(ComponentService.class);
        ItemViewImpl view = mock(ItemViewImpl.class);
        WindowManager wm = mock(WindowManager.class);
        DialogManager dm = mock(DialogManager.class);
        presenter.setComponentService(componentService);
        presenter.setWindowManager(wm);
        presenter.setDialogManager(dm);
        
        final int id = -1;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("componentId", (long)id);

        when(view.getArgs()).thenReturn(map);
        
        presenter.initView(view);
        
        verify(view).setCid(0);
        verify(view).setName(null);
        verify(view).setDescription(null);
        verify(view).setComponentType(null);
    }
    
    /**
     * Tests if there is no such object
     * @throws NotFoundException
     */
    @Test
    public void initViewTest3() throws NotFoundException {
        ComponentService componentService = mock(ComponentService.class);
        ItemViewImpl view = mock(ItemViewImpl.class);
        WindowManager wm = mock(WindowManager.class);
        DialogManager dm = mock(DialogManager.class);
        presenter.setComponentService(componentService);
        presenter.setWindowManager(wm);
        presenter.setDialogManager(dm);
        
        final int id = 9999;
        List<Component> fake = getFakeComponents();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("componentId", (long)id);
        
        when(componentService.getAll()).thenReturn(fake);
        when(componentService.get((long) id)).thenThrow(new NotFoundException());
        when(view.getArgs()).thenReturn(map);
        
        presenter.initView(view);
        
        verify(dm).notify("item.doesnt.exist");
        verify(wm).closeWindow(view);
    }

//    @Test
//    public void saveComponentTest() throws NotFoundException {
//        ComponentService componentService = mock(ComponentService.class);
////        ItemViewImpl view = mock(ItemViewImpl.class);
////        WindowManager wm = mock(WindowManager.class);
//        presenter.setComponentService(componentService);
////        presenter.initView(view);
////        presenter.setWindowManager(wm);
//        Component fake = getFakeComponent(2L, "comp", "abc", ComponentType.ARTICLE);
//        
//        
////
////        when(view.getCid()).thenReturn(fake.getId());
////        when(view.getName()).thenReturn(fake.getName());
////        when(view.getDescription()).thenReturn(fake.getDescription());
////        when(view.getComponentType()).thenReturn(fake.getComponentType().toString());
//        initViewTest();
//
//        presenter.saveComponent();
//        verify(componentService).saveComponent(argThat(new ComponentMatcher(fake)));
////        verify(wm).closeWindow(view);
//    }
    
    @Test
    public void saveComponentTest0() throws NotFoundException {
        ComponentService componentService = mock(ComponentService.class);
        ItemViewImpl view = mock(ItemViewImpl.class);
        WindowManager wm = mock(WindowManager.class);
        presenter.setComponentService(componentService);
        presenter.setWindowManager(wm);
        
        Component fake = getFakeComponent(0, "extraordinary", "new", ComponentType.ARTICLE);
        
        final int ID = 1;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("componentId", (long)ID);
        
        List<Component> fakeList = getFakeComponents();
        when(componentService.getAll()).thenReturn(fakeList);
        when(componentService.get((long) ID)).thenReturn(fakeList.get(ID));
        when(view.getArgs()).thenReturn(map);
        
        presenter.initView(view);
        
        when(view.getName()).thenReturn(fake.getName());
        when(view.getCid()).thenReturn(fake.getId());
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