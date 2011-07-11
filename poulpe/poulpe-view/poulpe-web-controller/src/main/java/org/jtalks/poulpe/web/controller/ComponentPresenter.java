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
package org.jtalks.poulpe.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.jtalks.poulpe.service.ComponentService;

//TODO: tasks which are going to be done from the bottom
//1) i18n support;
//2) unit-test;
//3) control for componentType and componentName. Component.Name is unique in DB???
//4) javadoc;
//5) edit_component.zul : items of combo-box
//6) view in presenter is transient???

/**
 * The class for mediating between model and view representation of components.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ComponentPresenter {

    /** The object that is responsible for updating view (content of web-pages). */
    private ComponentView view;

    /** The current (selected) component from the list of components. */
    private ComponentViewListItem currentComponent;

    /** The service instance to manipulate with stored components. */
    private ComponentService componentService;

    /**
     * Initialises the object that is responsible for updating view (content of
     * web-pages).
     * 
     * @param view
     *            the object that is responsible for updating view (content of
     *            web-pages)
     */
    public void initView(ComponentView view) {
        // try {
        // Messagebox.show(Integer.toString(view.hashCode()));
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        this.view = view;
    }

    /**
     * Checks if this presenter has the object that is responsible for updating
     * view (content of web-pages).
     * 
     * @return true if this presenter has the object that is responsible for
     *         updating view (content of web-pages), false otherwise
     */
    public boolean hasView() {
        return view != null;
    }

//    /**
//     * Returns a fake list of components, DON'T USE IT, it's only for testing.
//     * Once more DO NOT USE IT. It ought to be deleted soon.
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
     * Returns view representation of all components.
     * 
     * @return the list of the components
     */
    public List<ComponentView> getComponents() {
        return ComponentViewModelConverter.model2View(componentService.getAll());
    }

    /**
     * Sets the service instance which is used for manipulating with stored
     * components.
     * 
     * @param componentService
     *            the new value of the service instance
     */
    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }

    /**
     * Returns the current component from the list of components.
     * 
     * @return the current component from the list of components
     */
    public ComponentView getCurrentComponent() {
        return currentComponent;
    }

    /**
     * Sets the current component from the list of components.
     * 
     * @param currentComponent
     *            the new value of the current component from the list of
     *            components
     */
    public void setCurrentComponent(ComponentViewListItem currentComponent) {
        this.currentComponent = currentComponent;
    }

    /**
     * Shows the window for adding new component to component list.
     * 
     * @throws InterruptedException
     */
    // TODO maybe it's too complicated: view delegates showing window to
    // presenter which delegates it to view %-) 
    public void addComponent() {
        currentComponent = new ComponentViewListItem();
        view.showEditWindow(currentComponent);
    }

    /**
     * Removes selected component from the component list.
     */
    public void deleteComponent() {
        Component victim = ComponentViewModelConverter.view2Model(currentComponent);
        componentService.deleteComponent(victim);
        view.updateList(getComponents());
    }

    /**
     * Shows the window for editing selected component from component list.
     * 
     * @throws InterruptedException
     */
    public void editComponent() {
        view.showEditWindow(currentComponent);
    }

    /**
     * Saves the created or edited component in component list.
     * 
     * @param viewComponent
     *            the created or edited component in view representation
     */
    public void saveComponent(ComponentView viewComponent) {
        Component newbie = ComponentViewModelConverter.view2Model(viewComponent);
        componentService.saveComponent(newbie);
        viewComponent.updateList(getComponents());
        // TODO try to do it without additional request to database, using model
        // as list.
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

    /**
     * The empty constructor to prevent creating instances of utility class.
     */
    private ComponentViewModelConverter() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts the component from the model representation to the view
     * representation.
     * 
     * @param model
     *            the model representation of the component
     * @return the component in view representation
     */
    public static ComponentView model2View(Component model) {
        ComponentViewListItem view = new ComponentViewListItem();
        view.setCid(model.getId());
        view.setName(model.getName());
        view.setDescription(model.getDescription());
        view.setComponentType(model.getComponentType().toString());
        return view;
    }

    /**
     * Converts the components from the model representation to the view
     * representation.
     * 
     * @param model
     *            the list of the model representations of the components
     * @return the list of the components in view representation
     */
    public static List<ComponentView> model2View(List<Component> model) {
        List<ComponentView> view = new ArrayList<ComponentView>();
        for (Component modelItem : model) {
            view.add(model2View(modelItem));
        }
        return view;
    }

    /**
     * Converts the component from the view representation to the model
     * representation.
     * 
     * @param view
     *            the view representation of the component
     * @return the component in model representation
     */
    public static Component view2Model(ComponentView view) {
        Component model = new Component();
        model.setId(view.getCid());
        model.setName(view.getName());
        model.setDescription(view.getDescription());
        model.setComponentType(ComponentType.valueOf(view.getComponentType()));
        return model;
    }

    /**
     * Converts the components from the view representation to the model
     * representation.
     * 
     * @param view
     *            the list of the view representations of the components
     * @return the list of the components in model representation
     */
    public static List<Component> view2Model(List<ComponentView> view) {
        List<Component> model = new ArrayList<Component>();
        for (ComponentView viewItem : view) {
            model.add(view2Model(viewItem));
        }
        return model;
    }
}
