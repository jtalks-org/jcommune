/**
 * Copyright (C) 2011  JTalks.org Team
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
 */

package org.jtalks.jcommune.service.exceptions;

/**
 * Throws if error occurred while image processing
 *
 * @author Alexandre Teterin
 */
public class ImageProcessException extends Exception {
    private static final long serialVersionUID = 20120115L;

    /**
     * Default constructor.
     * <p/>
     * {@link Exception}
     */
    public ImageProcessException() {
        super();
    }

    /**
     * Create exception with specific message.
     *
     * @param message exception message
     */
    public ImageProcessException(String message) {
        super(message);
    }

    /**
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).
     */
    public ImageProcessException(Throwable cause) {
        super(cause);
    }
}
