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
import org.zkoss.zul.ListModelList;

/**
 * Interface which represents information about component displayed in admin panel.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public interface ComponentView {

    long getCid();

    void setCid(long cid);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    ComponentType getComponentType();

    void setComponentType(ComponentType type);

    ListModelList getModel();

    void setModel(ListModelList model);
}
