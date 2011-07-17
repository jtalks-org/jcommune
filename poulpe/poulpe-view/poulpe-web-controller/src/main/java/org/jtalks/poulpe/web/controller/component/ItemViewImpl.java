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

import java.util.Map;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.service.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Combobox;
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
public class ItemViewImpl extends Window implements ItemView, AfterCompose {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(ItemViewImpl.class);
    private static final long serialVersionUID = -3927090308078350369L;

    private Longbox cid;
    private Textbox name;
    private Textbox description;
    private Combobox componentType;
    private ItemPresenter presenter;

    /** {@inheritDoc} */
    @Override
    public void afterCompose() {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        initArgs();
        presenter.initView(this);
    }

    /** Initialises this window using received arguments. */
    private void initArgs() {
        Map<?, ?> args = Executions.getCurrent().getArg();
        long id = (Long) args.get("componentId");
        Component component = obtainComponent(id);
        if (component != null) {
            cid.setValue(component.getId());
            name.setText(component.getName());
            description.setText(component.getDescription());
            initTypes(component);
        }
//        EventListener el = (EventListener) args.get("listener");
//        saveCompButton.addEventListener(Events.ON_CLICK, el);
    }

    /**
     * Obtains the component by its ID.
     * 
     * @param id
     *            the ID of the component, {@code -1L} to return new instance
     * @return the component by its ID, new instance if {@code id} is equal to
     *         {@code -1L}, and null if there is no component with such ID.
     */
    private Component obtainComponent(long id) {
        Component component = null;
        if (id == -1L) {
            component = new Component();
        } else {
            try {
                component = presenter.getComponent(id);
            } catch (NotFoundException e) {
                try {
                    Messagebox.show(Labels.getLabel("item.doesnt.exist"));
                } catch (InterruptedException e1) {
                    // TODO: what to do here???? I can't throw it up.
                    LOGGER.error("Problem with showing messagebox.", e1);
                }
                LOGGER.warn("Attempt to change non-existing item.", e);
                detach();
                return null;
            }
        }
        return component;
    }

    /**
     * Initialises the list of possible types for the specified component.
     * 
     * @param component
     *            the component whose types are being determined.
     */
    private void initTypes(Component component) {
        if (component.getComponentType() == null) {
            componentType.setValue(null);
        } else {
            componentType.setValue(component.getComponentType().toString());
        }
        componentType.appendItem(componentType.getValue());
        for (Object obj : presenter.getTypes()) {
            componentType.appendItem(obj.toString());
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
    public ItemPresenter getPresenter() {
        return presenter;
    }

    /**
     * Sets the presenter which is linked with this window.
     * 
     * @param presenter
     *            new value of the presenter which is linked with this window
     */
    public void setPresenter(ItemPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Tells to presenter to save created or edited component in component list.
     * 
     * @throws Exception
     *             when error of closing window including problems with
     *             execution of the "onDetach" listener action
     * 
     * @see ListPresenter
     */
    public void onClick$saveCompButton() throws Exception {
        componentType.setConstraint("no empty");
        name.setConstraint("no empty");
        long pos = presenter.getCidByName(name.getValue());
        if (pos == -1 || pos == cid.longValue()) {
            presenter.saveComponent();
        } else {
            Messagebox.show(Labels.getLabel("item.already.exist"), Labels.getLabel("window.warning"),
                    Messagebox.OK, Messagebox.EXCLAMATION);
        }
    }

}