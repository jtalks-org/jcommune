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
 * Throws if image has not allowable size
 *
 * @author Alexandre Teterin
 */
public class ImageSizeException extends ImageProcessException {
    static final long serialVersionUID = 2012017L;
    private int maxSize;

    /**
     * Constructs a new exception with <code>null</code> as its detail message.
     */
    public ImageSizeException(int maxSize) {
        this.maxSize = maxSize;
    }
    
    /**
     * Gets the max size of avatar, that is available in the forum.
     * 
     * @return the max size of avatar
     */
    public int getMaxSize() {
        return maxSize;
    }
}
