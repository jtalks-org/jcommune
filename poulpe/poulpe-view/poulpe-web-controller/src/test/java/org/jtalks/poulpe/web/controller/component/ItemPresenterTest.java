/**
 * 
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
import org.mockito.ArgumentMatcher;
import org.testng.annotations.Test;


/**
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

    @Test public void saveComponentTest() {
        ComponentService componentService = mock(ComponentService.class);
        ItemView view = mock(ItemView.class);
        presenter.setComponentService(componentService);
        presenter.initView(view);
        
        Component fake = getFakeComponent(2L, "comp", "abc", ComponentType.ARTICLE);
        
        when(view.getCid()).thenReturn(fake.getId());
        when(view.getName()).thenReturn(fake.getName());
        when(view.getDescription()).thenReturn(fake.getDescription());
        when(view.getComponentType()).thenReturn(fake.getComponentType().toString());
        presenter.saveComponent();
        
        verify(componentService).saveComponent(argThat(new ComponentMatcher(fake)));
    }
    
    @Test public void getCidByNameTest() {
        ComponentService componentService = mock(ComponentService.class);
        presenter.setComponentService(componentService);

        List<Component> fakeList = new ArrayList<Component>();
        fakeList.add(getFakeComponent(0, "abc", "desc1", ComponentType.ARTICLE));
        fakeList.add(getFakeComponent(1, "abe", null, ComponentType.FORUM));
        
        when(componentService.getAll()).thenReturn(fakeList);

        assertEquals(presenter.getCidByName("abc"), 0);
        assertEquals(presenter.getCidByName("abe"), 1);
        assertEquals(presenter.getCidByName("abd"), -1);
        //assert(presenter.getCidByName(null), -1);
    }
    
    @Test public void getTypesTest() {
        ComponentService componentService = mock(ComponentService.class);
        presenter.setComponentService(componentService);
        
        Set<ComponentType> origTypes = new HashSet<ComponentType>(Arrays.asList(ComponentType.values()));
        
        when(componentService.getAvailableTypes()).thenReturn(origTypes);
        
        List<String> strTypes = presenter.getTypes();
        assertEquals(origTypes.size(), strTypes.size());
        for (ComponentType orig : origTypes) {
            assertTrue(strTypes.contains(orig.toString()));
        }
    }
}

class ComponentMatcher extends ArgumentMatcher<Component> {
    private Component comp;
    
    public ComponentMatcher(Component comp) {
        this.comp = comp;
    }

    @Override
    public boolean matches(Object item) {
        Component comp2 = (Component) item;
        return comp.getId() == comp2.getId()
                && comp.getName().equals(comp2.getName())
                && comp.getDescription().equals(comp2.getDescription())
                && comp.getComponentType().equals(comp2.getComponentType());
    }

}