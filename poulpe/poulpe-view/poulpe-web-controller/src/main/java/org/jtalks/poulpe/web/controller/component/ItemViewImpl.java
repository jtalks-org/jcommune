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
import java.util.Set;

import org.jtalks.poulpe.model.entity.ComponentType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Longbox;
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

    private static final long serialVersionUID = -3927090308078350369L;

    private Longbox cid;
    private Textbox name;
    private Textbox description;
    private Combobox componentType;
    /* Important! If we are going to serialize/deserialize this class, this
     * field must be initialised explicitly during deserialization */
    private transient ItemPresenter presenter;

    /** {@inheritDoc} */
    @Override
    public void afterCompose() {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        presenter.initView(this);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getArgs() {
        return Executions.getCurrent().getArg();
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
        return name.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String compName) {
        this.name.setText(compName);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(String description) {
        this.description.setText(description);
    }

    /** {@inheritDoc} */
    @Override
    public String getComponentType() {
        return componentType.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setComponentType(String type) {
        this.componentType.setText(type);
    }

    /**
     * Returns the presenter which is linked with this window.
     * 
     * @return the presenter which is linked with this window
     */
    public ItemPresenter getPresenter() {
        return presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void setComponentTypes(Set<ComponentType> types) {
        componentType.appendItem(componentType.getText());
        for (ComponentType type : types) {
            componentType.appendItem(type.toString());
        }
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
     * @see ListPresenter
     */
    public void onClick$saveCompButton() {
        componentType.setConstraint("no empty");
        name.setConstraint("no empty");
        presenter.saveComponent();
    }
    
    /**
     * Tells to presenter to check if name of created or edited component is a duplicate.
     * 
     * @see ListPresenter
     */
    public void onBlur$name() {
        name.setConstraint("no empty");
        presenter.checkComponent();
    }

    /** {@inheritDoc} */
    @Override
    public void wrongName(String error) {
        throw new WrongValueException(name, Labels.getLabel(error));
    }

}