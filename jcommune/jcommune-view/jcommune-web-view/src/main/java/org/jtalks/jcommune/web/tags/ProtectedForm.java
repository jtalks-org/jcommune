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
package org.jtalks.jcommune.web.tags;

import org.springframework.web.servlet.tags.form.FormTag;

/**
 * Extension of Spring form tag. This tag automatically put necessary JavaScript to prevent form's multiposting.
 *
 * @author Pavel Vervenko
 */
public class ProtectedForm extends FormTag {

    private static final String mULTIPOST_BLOCKING_JS = "if (this.getAttribute('submitted')) return false; "
            + "this.setAttribute('submitted','true');";

    /**
     * Default tag constructor.
     */
    public ProtectedForm() {
        setOnsubmit(MULTIPOST_BLOCKING_JS);
    }

    private static final long serialVersionUID = 34588L;
}
