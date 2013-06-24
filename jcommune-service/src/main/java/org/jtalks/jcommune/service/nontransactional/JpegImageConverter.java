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
package org.jtalks.jcommune.service.nontransactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class for converting source image to the JPEG format
 * @author Andrei Alikov
 */
public class JpegImageConverter extends ImageConverter {
    private static final String format = "jpeg";

    /**
     * @param base64Wrapper to perform image data encoding, essential for embedding an image into HTML page
     * @param maxImageHeight maximum image height after pre processing
     * @param maxImageWidth  maximum image width after pre processing
     */
    public JpegImageConverter(Base64Wrapper base64Wrapper, int maxImageWidth, int maxImageHeight) {
        super(base64Wrapper, maxImageWidth, maxImageHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveImageToStream(BufferedImage image, OutputStream stream) throws IOException {
        ImageIO.write(image, format, stream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getImageType() {
        return BufferedImage.TYPE_INT_RGB;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlSrcImagePrefix() {
        return  String.format(HTML_SRC_TAG_PREFIX, format);
    }
}
