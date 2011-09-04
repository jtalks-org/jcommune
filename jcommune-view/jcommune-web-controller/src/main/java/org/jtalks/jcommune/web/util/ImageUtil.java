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
package org.jtalks.jcommune.web.util;

import java.awt.Image;

/**
 * Class that consists util methods for working with images.
 *
 * @author Eugeny Batov
 */
public class ImageUtil {

    /**
     * Resizes original image to square image with given dimension.
     *
     * @param originalImage      -    image to resizing
     * @param expectedDimension- expected dimension after resizing
     * @return modified image with new dimensions
     */
    public static Image resizeToSquare(Image originalImage, int expectedDimension) {
        int originalWidth = originalImage.getWidth(null);
        int originalHeight = originalImage.getHeight(null);
        int maxDimension = Math.max(originalHeight, originalWidth);
        double scale = (double) maxDimension / expectedDimension;
        int modifiedWidth = (int) (originalWidth / scale);
        int modifiedHeight = (int) (originalHeight / scale);
        return originalImage.getScaledInstance(modifiedWidth, modifiedHeight, Image.SCALE_DEFAULT);
    }

}
