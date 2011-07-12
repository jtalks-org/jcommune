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

import org.jtalks.poulpe.service.TopicTypeService;

/**
 * Presenter of TopicType list page.
 * @author Pavel Vervenko
 */
public class TopicTypePresenter {

    private TopicTypeService topicTypeService;
    private TopicTypeView view;

    /**
     * Save and init view.
     * @param view view
     */
    public void initView(TopicTypeView view) {
        this.view = view;
    }

    /**
     * Set the TopicTypeService implementation.
     * @param topicTypeService impl of TopicTypeService
     */
    public void setTopicTypeService(TopicTypeService topicTypeService) {
        this.topicTypeService = topicTypeService;
    }
}
