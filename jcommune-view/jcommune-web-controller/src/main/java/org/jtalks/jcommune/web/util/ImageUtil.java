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
package org.jtalks.jcommune.web.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Class that consists util methods for working with images.
 *
 * @author Eugeny Batov
 */
public final class ImageUtil {

    public static final int IMAGE_JPEG = 0;
    public static final int IMAGE_PNG = 1;

    private static final int ALPHA_CHANNEL_MASK = 0xFF000000;
    private static final int RED_CHANNEL_MASK = 0x00FF0000;
    private static final int GREEN_CHANNEL_MASK = 0x0000FF00;
    private static final int BLUE_CHANNEL_MASK = 0x000000FF;
    private static final int BIT = 8;
    private static final int TWO_BITS = 16;
    private static final int THREE_BITS = 24;


    /**
     * Empty constructor.
     */
    private ImageUtil() {
        //Utility classes should not have a public or default constructor
    }

    /**
     * Converts multipart file to image.
     *
     * @param multipartFile input multipart file
     * @return image obtained from multipart file
     * @throws IOException throws if an error occurs during reading
     */
    public static Image convertMultipartFileToImage(MultipartFile multipartFile) throws IOException {
        return ImageIO.read(multipartFile.getInputStream());
    }

    /**
     * Converts image to byte array.
     *
     * @param image input image
     * @return byte array obtained from image
     * @throws IOException if an I/O error occurs
     */
    public static byte[] convertImageToByteArray(Image image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) image, "jpeg", baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }


    /**
     * Resizes an image.
     *
     * @param image     The image to resize
     * @param maxWidth  The image's max width
     * @param maxHeight The image's max height
     * @param type      int code jpeg, png or gif
     * @return A resized <code>BufferedImage</code>
     */
    public static BufferedImage resizeImage(BufferedImage image, int type, int maxWidth, int maxHeight) {
        Dimension largestDimension = new Dimension(maxWidth, maxHeight);

        // Original size
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        float aspectRatio = (float) imageWidth / imageHeight;

        if (imageWidth > maxWidth || imageHeight > maxHeight) {
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
     * Creates a <code>BufferedImage</code> from an <code>Image</code>. This method can
     * function on a completely headless system. This especially includes Linux and Unix systems
     * that do not have the X11 libraries installed, which are required for the AWT subsystem to
     * operate. The resulting image will be smoothly scaled using bilinear filtering.
     *
     * @param source The image to convert
     * @param width  The desired image width
     * @param height The desired image height
     * @param type   int code jpeg, png or gif
     * @return bufferedImage The resized image
     */
    private static BufferedImage createBufferedImage(BufferedImage source, int type, int width, int height) {
        int imageType = BufferedImage.TYPE_INT_RGB;
        if (type == ImageUtil.IMAGE_PNG && hasAlpha(source)) {
            imageType = BufferedImage.TYPE_INT_ARGB;
        }

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
            yDiff = scale(y, scaleY) - sourceY;

            for (int x = 0; x < width; x++) {
                sourceX = x * source.getWidth() / bufferedImage.getWidth();
                xDiff = scale(x, scaleX) - sourceX;

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
     * Scales point.
     *
     * @param point point
     * @param scale scale
     * @return scaled point
     */
    private static double scale(int point, double scale) {
        return point / scale;
    }

    /**
     * Makes rgb interpolation.
     *
     * @param value1   first known value
     * @param value2   second known value
     * @param distance distance between values
     * @return rgb an integer pixel in the ARGB color model
     */
    private static int getRGBInterpolation(int value1, int value2, double distance) {
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
    private static boolean hasAlpha(Image image) {
        try {
            PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
            pg.grabPixels();

            return pg.getColorModel().hasAlpha();
        } catch (InterruptedException e) {
            return false;
        }
    }
}
