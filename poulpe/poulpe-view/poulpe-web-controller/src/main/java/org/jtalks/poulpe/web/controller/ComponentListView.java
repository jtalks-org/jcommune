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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * The class which manages actions and represents information about component
 * displayed in administrator panel.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ComponentListView extends Window implements AfterCompose {

    private static final long serialVersionUID = 4042325860309593122L;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The path to the web-page for adding / editing component. */
    private static final String EDIT_COMPONENT_URL = "/WEB-INF/pages/edit_component.zul";

    private ListModelList model;
    private ComponentPresenter presenter;

    /** {@inheritDoc} */
    @Override
    public void afterCompose() {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        presenter.initListView(this);
    }

    /**
     * Returns the presenter which is linked with this window.
     * 
     * @return the presenter which is linked with this window
     */
    public ComponentPresenter getPresenter() {
        return presenter;
    }

    /**
     * Sets the presenter which is linked with this window.
     * 
     * @param presenter
     *            new value of the presenter which is linked with this window
     */
    public void setPresenter(ComponentPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Updates the list of the components.
     * 
     * @param list
     *            the new list of the components
     */
    public void updateList(List<ComponentView> list) {
        model.clear();
        model.addAll(list);
    }

    /**
     * Shows the window for adding new / editing selected component.
     * 
     * @param component
     *            the component to be saved (empty or null to add new one)
     * @param types
     *            the list of types {@code component} could be
     */
    public void showEditWindow(ComponentView component, List<String> types) {
        logger.debug("b.showEditWindow(c,t): t.cid = {}", component.getCid());
        if (component == null || component.getName() == null) {
            component = new ComponentViewItem();
            component.setCid(0);
            component.setName("");
            component.setDescription("");
            component.setComponentType("");
        }
        logger.debug("a.showEditWindow(c,t): t.cid = {}", component.getCid());
        Window win = (Window) Executions.createComponents(EDIT_COMPONENT_URL, null, null);
        ((Longbox) win.getFellow("cid")).setValue(component.getCid());
        ((Textbox) win.getFellow("name")).setText(component.getName());
        ((Textbox) win.getFellow("description")).setText(component.getDescription());
        Combobox cb = ((Combobox) win.getFellow("componentType"));
        cb.setValue(component.getComponentType());
        for (String string : types) {
            cb.appendItem(string);
        }
        try {
            win.doModal();
        } catch (Exception e) {
            try {
                Messagebox.show("Something wrong with showing popup window. Try again please.",
                        "Problem with showing popup window", Messagebox.OK, Messagebox.QUESTION);
            } catch (InterruptedException e1) {
                logger.error("Something wrong with showing popup window.", e1);
            }
            logger.error("Something wrong with showing popup window.", e);
        }
    }

    /**
     * Tells to presenter that the window for adding new component must be
     * shown.
     * 
     * @see ComponentPresenter
     */
    public void onClick$addCompButton() {
        presenter.addComponent();
    }

    /**
     * Tells to presenter to delete selected component (it knows which one it
     * is).
     * 
     * @see ComponentPresenter
     */
    public void onClick$delCompButton() {
        String name = presenter.getSelectedComponent().getName();
        try {
            Messagebox.show("Are you sure that you wanna delete " + name + "?",
                    "Delete " + name + "?", Messagebox.YES | Messagebox.CANCEL,
                    Messagebox.QUESTION, Messagebox.CANCEL, new EventListener() {
                        /** {@inheritDoc} */
                        @Override
                        public void onEvent(Event event) {
                            if ((Integer) event.getData() == Messagebox.YES) {
                                presenter.deleteComponent();
                            }
                        }
                    });
        } catch (InterruptedException e) {
            logger.error("Error of showing message box comfirming deleting", e);
        }
    }

    /**
     * Tells to presenter that the window for editing selected component must be
     * shown.
     * 
     * @see ComponentPresenter
     */
    public void onDoubleClick$listItem() {
        presenter.editComponent();
    }

    /**
     * Removes from the displayed component list the {@code component} item. As
     * result user will see updated list.
     * 
     * @param component
     *            item to be deleted.
     */
    public void removeFromModel(ComponentViewItem component) {
        model.remove(component);
    }

    /**
     * Adds to the displayed component list the {@code component} item. As
     * result user will see updated list.
     * 
     * @param component
     *            item to be added.
     */
    public void addToModel(ComponentViewItem component) {
        model.add(component);
    }

    /**
     * Replaces in the displayed component list the old value of
     * {@code component} item by the new one. As result user will see updated
     * list. It searches the component to be replaced by its id.
     * 
     * @param view
     *            the replacing item.
     */
    public void replaceInModel(ComponentViewItem view) {
        for (int i = 0; i < model.size(); i++) {
            ComponentViewItem item = (ComponentViewItem) model.get(i);
            if (item.getCid() == view.getCid()) {
                model.set(i, view);
                break;
            }
        }
    }

    /**
     * Searches the component's id by its name.
     * 
     * @param name
     *            the component's name
     * @return the component's id whose name is {@code name}
     */
    public long getCidByName(String name) {
        for (int i = 0; i < model.size(); i++) {
            ComponentViewItem item = (ComponentViewItem) model.get(i);
            if (name.equals(item.getName())) {
                return item.getCid();
            }
        }
        return -1;
    }

}