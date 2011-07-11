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
public class ComponentViewImpl extends Window implements ComponentView, AfterCompose {

    private static final long serialVersionUID = -3927090308078350369L;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    /** The path to the web-page for adding / editing component. */
    private static final String EDIT_COMPONENT_URL = "/WEB-INF/pages/edit_component.zul";

    private Longbox cid;
    private Textbox name;
    private Textbox description;
    private Combobox componentType;
    private ListModelList model;
    private ComponentPresenter presenter;

    /** {@inheritDoc} */
    @Override
    public void afterCompose() {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        if (!presenter.hasView()) {
            presenter.initView(this);
        }
    }

    /** {@inheritDoc} */
    @Override
    public long getCid() {
        return cid.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setCid(long cid) {
        this.cid.setValue(cid);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String compName) {
        this.name.setValue(compName);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(String description) {
        this.description.setValue(description);
    }

    /** {@inheritDoc} */
    @Override
    public String getComponentType() {
        return componentType.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setComponentType(String type) {
        this.componentType.setValue(type);
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

    /** {@inheritDoc} */
    @Override
    public void updateList(List<ComponentView> list) {
        model.clear();
        model.addAll(list);
    }

    /** {@inheritDoc} */
    @Override
    public void showEditWindow(ComponentView component) {
        if (component.getName() == null) {
            component.setCid(0);
            component.setName("");
            component.setDescription("");
            component.setComponentType("");
        }
        Window win = (Window) Executions.createComponents(EDIT_COMPONENT_URL, null, null);
        ((Longbox) win.getFellow("cid")).setValue(component.getCid());
        ((Textbox) win.getFellow("name")).setText(component.getName());
        ((Textbox) win.getFellow("description")).setText(component.getDescription());
        ((Textbox) win.getFellow("componentType")).setText(component.getComponentType());
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
        presenter.deleteComponent();
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
     * Tells to presenter to save created or edited component in component list.
     * 
     * @see ComponentPresenter
     */
    public void onClick$saveCompButton() {
        presenter.saveComponent(this);
        onClose();
    }

}