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

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * The class for rendering components as items of {@link Listbox}.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class Renderer implements ListitemRenderer {

    /** {@inheritDoc} */
    @Override
    public void render(Listitem arg0, Object arg1) {
        PlainComponentItem item = (PlainComponentItem) arg1;
        Listcell cell = new Listcell();
        Label nameLabel = new Label(item.getName());
        cell.appendChild(nameLabel);
        nameLabel.setSclass("boldstyle");
        arg0.appendChild(cell);
        arg0.appendChild(new Listcell(item.getDescription()));
        arg0.appendChild(new Listcell(item.getComponentType()));
        arg0.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener() {
            /** {@inheritDoc} */
            @Override
            public void onEvent(Event event) throws InterruptedException {
                ListPresenter presenter = (ListPresenter) SpringUtil
                        .getBean("componentListPresenter");
                presenter.editComponent();
            }
        });
    }

}
