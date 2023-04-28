//    KriolOS POS
//    Copyright (c) 2019-2023 KriolOS
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.data.loader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JG uniCenta
 */
public class ImageUtils {

    private final static Logger LOGGER = Logger.getLogger(ImageUtils.class.getName());
    private final static char[] HEXCHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private ImageUtils() {
    }

    private static byte[] getBytesFromInputStream(InputStream inputstream) throws IOException {
        byte[] buffer = new byte[1024];
        byte[] resource = new byte[0];
        int n;

        if (inputstream != null) {
            while ((n = inputstream.read(buffer)) != -1) {
                byte[] b = new byte[resource.length + n];
                System.arraycopy(resource, 0, b, 0, resource.length);
                System.arraycopy(buffer, 0, b, resource.length, n);
                resource = b;
            }
        }
        return resource;
    }

    private static BufferedImage getImageFromInputStream(InputStream inputstream) {
        BufferedImage image = null;
        try {
            if (inputstream != null) {
                image = ImageIO.read(inputstream);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Exception on get image: " + inputstream, ex);
        }
        return image;
    }

    public static byte[] getBytesFromClasspath(String classPath) {

        byte[] image = new byte[0];
        if (classPath != null) {
            InputStream in = ImageUtils.class.getResourceAsStream(classPath);
            try {
                image = ImageUtils.getBytesFromInputStream(in);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Exception on get image: " + classPath, ex);
            }
        }
        return image;

    }

    public static BufferedImage getImageFromClasspath(String classpathFile) {

        BufferedImage image = null;

        if (classpathFile == null) {
            try {
                InputStream is = ImageUtils.class.getResourceAsStream(classpathFile);
                if (is != null) {
                    image = ImageIO.read(is);
                }
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Exception on get image: " + classpathFile, ex);
            }
        }

        return image;
    }

    public static BufferedImage getImageFromUrl(String urlString) {
        BufferedImage image = generateDefaultImage();;
        try {
            image = getImageFromUrl(new URL(urlString));
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, "ReadImage from string url: " + urlString, ex);
        }
        return image;
    }

    public static BufferedImage getImageFromUrl(URL url) {
        //TODO improve null checking Objects.requireNonNull(url, "url should not be null");
        BufferedImage image = null; //generateDefaultImage();
        if (url != null) {
            try {
                URLConnection urlConnection = url.openConnection();
                try ( InputStream in = urlConnection.getInputStream()) {
                    image = readImage(getBytesFromInputStream(in));
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "ReadImage from url: " + url, ex);
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "ReadImage from url: " + url, ex);
            }
        }
        return image;
    }

    public static BufferedImage readImage(byte[] imageByteArray) {
        //TODO improve null checking Objects.requireNonNull(imageByteArray, "imageByteArray should not be null");
        BufferedImage image = null;//generateDefaultImage();
        if (imageByteArray != null) {
            try ( ByteArrayInputStream input = new ByteArrayInputStream(imageByteArray)) {
                image = ImageIO.read(input);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "ReadImage from byte array", ex);
            }
        }
        return image;
    }

    /**
     *
     * @param img
     * @return
     */
    public static byte[] writeImage(BufferedImage img) {
        //TODO improve null checking Objects.requireNonNull(img, "img should not be null");
        byte[] imageByte = new byte[0];
        if (img != null) {
            try ( ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();) {
                ImageIO.write(img, "png", byteOutputStream);
                byteOutputStream.flush();
                imageByte = byteOutputStream.toByteArray();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Write Image(Supported: PNG) to byte array", ex);
            }
        }
        return imageByte;
    }

    /**
     *
     * @param objectByteArrary
     * @return
     */
    public static Object readSerializable(byte[] objectByteArrary) {
        //TODO improve null checking Objects.requireNonNull(objectByteArrary, "objectByteArrary should not be null");
        Object obj = null;
        if (objectByteArrary != null) {
            try ( ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(objectByteArrary))) {
                obj = in.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                LOGGER.log(Level.SEVERE, "Exception on readSerializable", ex);
            }
        }
        return obj;
    }

    /**
     *
     * @param obj
     * @return
     */
    public static byte[] writeSerializable(Object obj) {
        //TODO improve null checking Objects.requireNonNull(obj, "obj should not be null");
        byte[] objectSerialize = new byte[0];
        if (obj != null) {
            try {
                ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
                try ( ObjectOutputStream out = new ObjectOutputStream(bOutput)) {
                    out.writeObject(obj);
                    out.flush();
                    objectSerialize = bOutput.toByteArray();
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Exception on writeSerializable", ex);
            }
        }
        return objectSerialize;
    }

    /**
     *
     * @param propByte
     * @return
     */
    public static Properties readProperties(byte propByte[]) {
        //TODO improve null checking Objects.requireNonNull(propByte, "propByte should not be null");
        Properties prop = new Properties();
        if (propByte != null) {
            try {
                prop.loadFromXML(new ByteArrayInputStream(propByte));
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "readProperties", ex);
            }
        }
        return prop;
    }

    /**
     *
     * @param binput
     * @return
     */
    public static String bytes2hex(byte[] binput) {

        String result = "";
        if (binput != null) {
            StringBuilder s = new StringBuilder(binput.length * 2);
            for (int i = 0; i < binput.length; i++) {
                byte b = binput[i];
                s.append(HEXCHARS[(b & 0xF0) >> 4]);
                s.append(HEXCHARS[b & 0x0F]);
            }

            result = s.toString();
        }
        return result;
    }

    public static BufferedImage generateColorImage(Color color, int width, int heigth) {
        BufferedImage bImage = new BufferedImage(width, heigth, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = bImage.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, bImage.getWidth(), bImage.getHeight());
        // draw other things on g
        g.dispose();

        return bImage;

    }

    private static BufferedImage generateDefaultImage() {
        return generateColorImage(Color.WHITE, 128, 128);
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered defaultImage with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the defaultImage on to the buffered defaultImage
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered defaultImage
        return bimage;
    }
}
