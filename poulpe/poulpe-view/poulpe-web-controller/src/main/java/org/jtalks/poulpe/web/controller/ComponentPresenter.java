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
 * Creation date: July 10, 2011
 * The jtalks.org Project
 */
package org.jtalks.poulpe.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.service.ComponentService;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

/**
 * The class for mediating between model and view representation of components.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ComponentPresenter {

    private static final String EDIT_COMPONENT_URL = "/edit_component.zul";

    private ComponentView view;

    private ComponentService componentService;

    private ComponentView currentComponent;

    public void initView(ComponentView view) {
        this.view = view;
    }

    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }

//    /**
//     * Returns a fake list of components, DON'T USE IT, it's only for testing.
//     * Once more DO NOT USE IT.
//     * 
//     * @deprecated
//     * @return a fake list of components
//     */
//    public List<ComponentView> getFakeComponents() {
//        List<Component> list = new ArrayList<Component>();
//        for (int i = 0; i < 20; i++) {
//            Component c = new Component();
//            c.setName("Component Name #" + i);
//            c.setDescription("Component Description #" + i);
//            c.setComponentType(((i & 1) == 0) ? ComponentType.ARTICLE : ComponentType.FORUM);
//            list.add(c);
//        }
//        return ComponentViewModelConverter.model2View(list);
//    }

    /**
     * Get all components.
     * 
     * @return the list of the components
     */
    public List<ComponentView> getComponents() {
        return ComponentViewModelConverter.model2View(componentService.getAll());
    }

    public ComponentView getCurrentComponent() {
        return currentComponent;
    }

    public void setCurrentComponent(ComponentView currentComponent) {
        this.currentComponent = currentComponent;
    }

    public void addComponent() throws InterruptedException {
        Window win = (Window) Executions.createComponents(EDIT_COMPONENT_URL, null, null);
        win.doModal();
    }

    public void deleteComponent() {
        Component victim = ComponentViewModelConverter.view2Model(currentComponent);
        componentService.deleteComponent(victim);
        view.getModel().clear();
        view.getModel().addAll(getComponents());
    }

    public void editComponent() throws InterruptedException {
        // ComponentView comp = currentComponent;
        Window win = (Window) Executions.createComponents(EDIT_COMPONENT_URL, null, null);
        win.doModal();
    }
}

/**
 * The utility class which contains methods for converting model representation
 * of components to view representation and and vice versa.
 * 
 * @author Dmitriy Sukharev
 * 
 */
final class ComponentViewModelConverter {
    private ComponentViewModelConverter() {
    }

    public static ComponentView model2View(Component model) {
        ComponentView view = new org.jtalks.poulpe.web.controller.ComponentViewImpl();
        view.setCid(model.getId());
        view.setName(model.getName());
        view.setDescription(model.getDescription());
        view.setComponentType(model.getComponentType());
        return view;
    }

    public static List<ComponentView> model2View(List<Component> model) {
        List<ComponentView> view = new ArrayList<ComponentView>();
        for (Component modelItem : model) {
            view.add(model2View(modelItem));
        }
        return view;
    }

    public static Component view2Model(ComponentView view) {
        Component model = new Component();
        model.setId(view.getCid());
        model.setName(view.getName());
        model.setDescription(view.getDescription());
        model.setComponentType(view.getComponentType());
        return model;
    }

    public static List<Component> view2Model(List<ComponentView> view) {
        List<Component> model = new ArrayList<Component>();
        for (ComponentView viewItem : view) {
            model.add(view2Model(viewItem));
        }
        return model;
    }
}
