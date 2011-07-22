package org.jtalks.poulpe.web.controller.component;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.jtalks.poulpe.model.entity.Component;
import org.mockito.ArgumentMatcher;

/**
 * The class for matching the lists of {@link Component} items.
 * @author Dmitriy Sukharev
 */
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
            isMatch &= comp.getId() == comp2.getId() && comp.getName().equals(comp2.getName())
                    && comp.getDescription().equals(comp2.getDescription())
                    && comp.getComponentType().equals(comp2.getComponentType());
        }
        return isMatch;
    }

}