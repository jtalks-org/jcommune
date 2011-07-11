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

import org.jtalks.poulpe.model.entity.ComponentType;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * The class which manages actions and represents information about component
 * displayed in administrator panel.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ComponentViewImpl extends Window implements ComponentView, AfterCompose {

    private static final long serialVersionUID = 481006835514635561L;

    private long cid;
    private String name;
    private String description;
    private ComponentType componentType;
    private ListModelList model;
    private ComponentPresenter presenter;

    public void afterCompose() {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        presenter.initView(this);
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public long getCid() {
        return cid;
    }

    public ListModelList getModel() {
        return model;
    }

    public ComponentPresenter getPresenter() {
        return presenter;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String compName) {
        this.name = compName;
    }

    public void setComponentType(ComponentType type) {
        this.componentType = type;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public void setModel(ListModelList model) {
        this.model = model;
    }

    public void setPresenter(ComponentPresenter presenter) {
        this.presenter = presenter;
    }

    public void onClick$addCompButton() throws InterruptedException {
        Messagebox.show("add" + (presenter == null));
    }

    public void onClick$delCompButton() throws InterruptedException {
        presenter.deleteComponent();

    }

    public void onClick$editCompButton() throws InterruptedException {
        presenter.editComponent();
    }

    public void onDoubleClick$listItem() throws InterruptedException {
        presenter.editComponent();
    }

}