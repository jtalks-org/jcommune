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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Components;
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
public class ComponentItemView extends Window implements ComponentView, AfterCompose {

    private static final long serialVersionUID = -3927090308078350369L;
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Longbox cid;
    private Textbox name;
    private Textbox description;
    private Combobox componentType;
    private ComponentPresenter presenter;

    /** {@inheritDoc} */
    @Override
    public void afterCompose() {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        presenter.initItemView(this);
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

    /**
     * Tells to presenter to save created or edited component in component list.
     * 
     * @see ComponentPresenter
     */
    public void onClick$saveCompButton() {
        componentType.setConstraint("no empty");
        name.setConstraint("no empty");
        long pos = presenter.getCidByName(name.getValue());
        if (pos == -1 || pos == cid.longValue()) {
            presenter.saveComponent();
            onClose();
        } else {
            try {
                Messagebox.show("The component with such name already exists.", "Warning", Messagebox.OK,
                        Messagebox.EXCLAMATION);
            } catch (InterruptedException e) {
                logger.error("Error of showing message box comfirming deleting", e);
            }
        }
    }

}