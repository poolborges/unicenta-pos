package com.openbravo.resources;

import com.openbravo.pos.util.ThumbNailBuilder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

/**
 * @author pauloborges
 */
public enum ImageResources {

    ICON_CUSTOMER("com/openbravo/images/customer_sml.png"),
    ICON_FLOORS("com/openbravo/images/floors.png"),
    ICON_SUPPLIER("com/openbravo/images/supplier_sml.png"),;

    private String resourcePath;

    private ImageResources(String resourcePath){
        this.resourcePath = resourcePath;
    }

    public ImageIcon getIcon(){
        return getIcon(resourcePath);
    }

    public static ImageIcon getIcon(String resourcePath){
        ThumbNailBuilder tnbcat = new ThumbNailBuilder(32, 32, resourcePath);
        ImageIcon icon = new ImageIcon(tnbcat.getThumbNail(null));
        return icon;
    }

}
