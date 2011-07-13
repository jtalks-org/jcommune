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

import java.util.ArrayList;
import java.util.List;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;

/**
 * The utility class which contains methods for converting model representation
 * of components to view representation and and vice versa.
 * 
 * @author Dmitriy Sukharev
 * 
 */
final class ViewModelConverter {

    /**
     * The empty constructor to prevent creating instances of utility class.
     */
    private ViewModelConverter() {
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
    public static PlainComponentItem model2View(Component model) {
        PlainComponentItem view = new PlainComponentItem();
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
    public static List<PlainComponentItem> model2View(List<Component> model) {
        List<PlainComponentItem> view = new ArrayList<PlainComponentItem>();
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
    public static Component view2Model(PlainComponent view) {
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
    public static List<Component> view2Model(List<? extends PlainComponent> view) {
        List<Component> model = new ArrayList<Component>();
        for (PlainComponent viewItem : view) {
            model.add(view2Model(viewItem));
        }
        return model;
    }
}