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
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

/**
 * The class represents the ZK implementation of the manager for showing
 * different types of dialog messages.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class DialogManagerImpl implements DialogManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DialogManagerImpl.class);

    /** {@inheritDoc} */
    @Override
    public void notify(String str) {
        try {
            Messagebox.show(Labels.getLabel(str), Labels.getLabel("dialogmanager.warning"), Messagebox.OK,
                    Messagebox.EXCLAMATION);
        } catch (InterruptedException e) {
            LOGGER.error("Problem with showing messagebox.", e);
            throw new AssertionError(e);    // it's unlikely to happen
        }
    }

    /** {@inheritDoc} */
    @Override
    public void confirmDeletion(final String victim, final DialogManager.Performable confirmable) {
        final String title = String.format(Labels.getLabel("dialogmanager.delete.title"), victim);
        final String text = String.format(Labels.getLabel("dialogmanager.delete.question"), victim);
        try {
            Messagebox.show(text, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                    Messagebox.NO, new DialogDeleteListener(confirmable));
        } catch (InterruptedException e) {
            LOGGER.error("Problem with showing deleting messagebox.", e);
            throw new AssertionError(e);    // it's unlikely to happen
        }
    }

    /**
     * The class for deciding if the item ought to be deleted in accordance with
     * the message button user clicked on.
     * 
     * @author Dmitriy Sukharev
     * 
     */
    private static class DialogDeleteListener implements EventListener {

        private Performable confirmable;

        /**
         * Constructor of the listener which initialises the object whose
         * {@code execute} method will be invoked.
         * 
         * @param confirmable
         *            the object whose {@code execute} method will be invoked
         */
        public DialogDeleteListener(Performable confirmable) {
            this.confirmable = confirmable;
        }

        /** {@inheritDoc} */
        @Override
        public void onEvent(Event event) {
            if ((Integer) event.getData() == Messagebox.YES) {
                confirmable.execute();
            }
        }
    }

}
