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

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * The class which manages actions and represents information about components
 * displayed in administrator panel.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ListViewImpl extends Window implements ListView, AfterCompose {

    private static final long serialVersionUID = -5891403019261284676L;

    private ListModelList model;
    private Listbox listbox;
    private ListPresenter presenter;

    /** {@inheritDoc} */
    @Override
    public void afterCompose() {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        model = new BindingListModelList(new ArrayList<Object>(), true);
        listbox.setModel(model);
        presenter.initListView(this);
        listbox.setItemRenderer(new Renderer());
    }

    /**
     * Returns the presenter which is linked with this window.
     * 
     * @return the presenter which is linked with this window
     */
    public ListPresenter getPresenter() {
        return presenter;
    }

    /**
     * Sets the presenter which is linked with this window.
     * 
     * @param presenter
     *            new value of the presenter which is linked with this window
     */
    public void setPresenter(ListPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Tells to presenter that the window for adding new component must be
     * shown.
     * @throws InterruptedException 
     * 
     * @see ListPresenter
     */
    public void onClick$addCompButton() throws InterruptedException {
        presenter.addComponent();
    }

    /**
     * Tells to presenter to delete selected component (it knows which one it
     * is).
     * 
     * @throws InterruptedException
     *             when a thread is waiting, sleeping, or otherwise occupied,
     *             and the thread is interrupted, either before or during the
     *             activity
     * 
     * @see ListPresenter
     */
    public void onClick$delCompButton() throws InterruptedException {
        if (listbox.getSelectedIndex() == -1) { // if there is no selected item
            Messagebox.show("There is no selected item to delete", "Warning", Messagebox.OK,
                    Messagebox.EXCLAMATION);
            return;
        }
        String name = getSelectedItem().getName();
        Messagebox.show("Are you sure that you wanna delete " + name + "?", "Delete " + name + "?",
                Messagebox.YES | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL,
                new EventListener() {
                    /** {@inheritDoc} */
                    @Override
                    public void onEvent(Event event) {
                        if ((Integer) event.getData() == Messagebox.YES) {
                            presenter.deleteComponent();
                        }
                    }
                });
    }

//    /**
//     * Tells to presenter that the window for editing selected component must be
//     * shown.
//     * @throws InterruptedException 
//     * 
//     * @see ListPresenter
//     */
//    public void onDoubleClick$listItem() throws InterruptedException {
//        presenter.editComponent();
//    }

//    /**
//     * Removes from the displayed component list the {@code component} item. As
//     * result user will see updated list.
//     * 
//     * @param componentView
//     *            item to be deleted.
//     */
//    public void removeFromModel(ComponentView componentView) {
//        model.remove(componentView);
//    }
    
    /** {@inheritDoc} */
    @Override
    public void updateList(List<PlainComponentItem> list) {
        model.clear();
        model.addAll(list);
    }
    
//    /** {@inheritDoc} */
//    @Override
//    public boolean hasSelectedItem() {
//        return listbox.getSelectedIndex() >= 0;
//    }

    /** {@inheritDoc} */
    @Override
    public PlainComponent getSelectedItem() {
        return (PlainComponent) model.get(listbox.getSelectedIndex());
    }

//    /** {@inheritDoc} */
//    @Override
//    public void addToList(PlainComponentItem component) {
//        model.add(component);
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void updateInList(PlainComponentItem component) {
//        for (int i = 0; i < model.size(); i++) {
//            PlainComponentItem item = (PlainComponentItem) model.get(i);
//            if (item.getCid() == component.getCid()) {
//                model.set(i, component);
//                break;
//            }
//        }
//    }

//    /**
//     * Searches the component's id by its name.
//     * 
//     * @param name
//     *            the component's name
//     * @return the component's id whose name is {@code name}
//     */
//    public long getCidByName(String name) {
//        for (int i = 0; i < model.size(); i++) {
//            ComponentViewItem item = (ComponentViewItem) model.get(i);
//            if (name.equals(item.getName())) {
//                return item.getCid();
//            }
//        }
//        return -1;
//    }

}