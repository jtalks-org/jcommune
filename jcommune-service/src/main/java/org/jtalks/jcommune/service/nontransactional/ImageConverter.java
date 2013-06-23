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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.*;

/**
 * Class for converting image and saving it in the target format in the byte array.
 * Subclasses should define how to save image and what type target image will have
 * Some methods were taken from JForum: http://jforum.net/
 *
 * @author Eugeny Batov
 * @author Alexandre Teterin
 * @author Andrei Alikov
 */
@Component
public abstract class ImageConverter {

    /**
     * This prefix is used when specifying image as a byte array in SRC attribute
     * of IMG HTML tag. Used in AJAX avatar preview.
     */
    protected static final String HTML_SRC_TAG_PREFIX = "data:image/{0};base64,";
    private static final int ALPHA_CHANNEL_MASK = 0xFF000000;
    private static final int RED_CHANNEL_MASK = 0x00FF0000;
    private static final int GREEN_CHANNEL_MASK = 0x0000FF00;
    private static final int BLUE_CHANNEL_MASK = 0x000000FF;
    private static final int BIT = 8;
    private static final int TWO_BITS = 16;
    private static final int THREE_BITS = 24;

    private Base64Wrapper base64Wrapper;

    private final int maxImageWidth;
    private final int maxImageHeight;

    /**
     * @param base64Wrapper to perform image data encoding, essential for embedding an image into HTML page
     * @param maxImageHeight maximum image height after pre processing
     * @param maxImageWidth  maximum image width after pre processing
     */
    public ImageConverter(Base64Wrapper base64Wrapper, int maxImageWidth, int maxImageHeight) {
        this.base64Wrapper = base64Wrapper;
        this.maxImageWidth = maxImageWidth;
        this.maxImageHeight = maxImageHeight;
    }

    /**
     * Gets prefix for "src" attribute of the "img" tag representing the image format
     * @return prefix for "src" attribute of the "img" tag representing the image format
     */
    public abstract String getHtmlSrcImagePrefix();

    /**
     * Converts image to byte array.
     *
     * @param image input image, not null
     * @return byte array obtained from image
     * @throws ImageProcessException if an I/O error occurs
     */
    public byte[] convertImageToByteArray(BufferedImage image) throws ImageProcessException {
        Validate.notNull(image, "Incoming image cannot be null");
        byte[] result;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            saveImageToStream(image, baos);
            baos.flush();
            result = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            throw new ImageProcessException(e);
        }

        return result;
    }

    /**
     * Saves image to the stream
     * @param image image to be saved
     * @param stream output stream
     * @throws IOException
     */
    protected abstract void saveImageToStream(BufferedImage image, OutputStream stream) throws IOException;

    /**
     * Perform byte data conversion to BufferedImage.
     *
     * @param bytes for conversion.
     * @return image result.
     * @throws ImageProcessException image conversion problem.
     */
    public BufferedImage convertByteArrayToImage(byte[] bytes) throws ImageProcessException {
        BufferedImage result;
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(bytes));
        try {
            result = ImageIO.read(bis);
        } catch (IOException e) {
            throw new ImageProcessException(e);
        } finally {
            IOUtils.closeQuietly(bis);
        }
        return result;
    }

    /**
     * Resizes an image if its width or height is bigger than maximum value specified in the constructor.
     *
     * @param image     The image to resize
     * @param type      int code jpeg, png or gif
     * @return A <code>BufferedImage</code> having width and height less or equal then maximum
     */
    public BufferedImage resizeImage(BufferedImage image, int type) {
        Dimension largestDimension = new Dimension(maxImageWidth, maxImageHeight);

        // Original size
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        float aspectRatio = (float) imageWidth / imageHeight;

        if (imageWidth > largestDimension.width || imageHeight > largestDimension.height) {
            if ((float) largestDimension.width / largestDimension.height > aspectRatio) {
                largestDimension.width = (int) Math.ceil(largestDimension.height * aspectRatio);
            } else {
                largestDimension.height = (int) Math.ceil(largestDimension.width / aspectRatio);
            }

            //Modified size
            imageWidth = largestDimension.width;
            imageHeight = largestDimension.height;
        }
        return createBufferedImage(image, type, imageWidth, imageHeight);
    }

    /**
     * Perform image resizing and processing
     *
     * @param image for processing
     * @return processed image bytes
     * @throws ImageProcessException image processing problem
     */
    public byte[] preprocessImage(BufferedImage image) throws ImageProcessException {
        byte[] result;

        BufferedImage outputImage = resizeImage(image, getImageType());
        result = convertImageToByteArray(outputImage);
        return result;
    }

    /**
     * Gets the type of the result image (see {@link BufferedImage} documentation)
     * @return the type of the result image
     */
    protected abstract int getImageType();

    /**
     * Perform preparing content for SRC attribute of the IMG HTML tag
     *
     * @param avatar image payload
     * @return SRC attribute content
     */
    public String prepareHtmlImgSrc(byte[] avatar) {
        return base64Wrapper.encodeB64Bytes(avatar);
    }

    /**
     * Creates a <code>BufferedImage</code> from an <code>Image</code>. This method can
     * function on a completely headless system. This especially includes Linux and Unix systems
     * that do not have the X11 libraries installed, which are required for the AWT subsystem to
     * operate. The resulting image will be smoothly scaled using bilinear filtering.
     *
     * @param source The image to convert
     * @param width  The desired image width
     * @param height The desired image height
     * @param imageType   int code RGB or ARGB
     * @return bufferedImage The resized image
     */
    private BufferedImage createBufferedImage(BufferedImage source, int imageType, int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, imageType);

        int sourceX;
        int sourceY;

        double scaleX = (double) width / source.getWidth();
        double scaleY = (double) height / source.getHeight();

        int x1;
        int y1;

        double xDiff;
        double yDiff;

        int rgb;
        int rgb1;
        int rgb2;

        for (int y = 0; y < height; y++) {
            sourceY = y * source.getHeight() / bufferedImage.getHeight();
            yDiff = y / scaleY - sourceY;

            for (int x = 0; x < width; x++) {
                sourceX = x * source.getWidth() / bufferedImage.getWidth();
                xDiff = x / scaleX - sourceX;

                x1 = Math.min(source.getWidth() - 1, sourceX + 1);
                y1 = Math.min(source.getHeight() - 1, sourceY + 1);

                rgb1 = getRGBInterpolation(source.getRGB(sourceX, sourceY), source.getRGB(x1, sourceY), xDiff);
                rgb2 = getRGBInterpolation(source.getRGB(sourceX, y1), source.getRGB(x1, y1), xDiff);

                rgb = getRGBInterpolation(rgb1, rgb2, yDiff);

                bufferedImage.setRGB(x, y, rgb);
            }
        }

        return bufferedImage;
    }

    /**
     * Makes rgb interpolation.
     *
     * @param value1   first known value
     * @param value2   second known value
     * @param distance distance between values
     * @return rgb an integer pixel in the ARGB color model
     */
    private int getRGBInterpolation(int value1, int value2, double distance) {
        int alpha1 = (value1 & ALPHA_CHANNEL_MASK) >>> THREE_BITS;
        int red1 = (value1 & RED_CHANNEL_MASK) >> TWO_BITS;
        int green1 = (value1 & GREEN_CHANNEL_MASK) >> BIT;
        int blue1 = (value1 & BLUE_CHANNEL_MASK);

        int alpha2 = (value2 & ALPHA_CHANNEL_MASK) >>> THREE_BITS;
        int red2 = (value2 & RED_CHANNEL_MASK) >> TWO_BITS;
        int green2 = (value2 & GREEN_CHANNEL_MASK) >> BIT;
        int blue2 = (value2 & BLUE_CHANNEL_MASK);

        return ((int) (alpha1 * (1.0 - distance) + alpha2 * distance) << THREE_BITS)
                | ((int) (red1 * (1.0 - distance) + red2 * distance) << TWO_BITS)
                | ((int) (green1 * (1.0 - distance) + green2 * distance) << BIT)
                | (int) (blue1 * (1.0 - distance) + blue2 * distance);
    }

    /**
     * Determines if the image has transparent pixels.
     *
     * @param image The image to check for transparent pixel.s
     * @return <code>true</code> of <code>false</code>, according to the result
     */
    private boolean hasAlpha(Image image) {
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            return false;
        }
        return pg.getColorModel().hasAlpha();
    }


}
