package com.openbravo.resources;

import com.openbravo.pos.util.ThumbNailBuilder;

import javax.swing.*;
import java.net.URL;

/**
 * @author pauloborges
 */
public enum ImageResources {

    ICON_CUSTOMER("com/openbravo/images/customer_sml.png"), //TODO Add suffix SMALL
    ICON_FLOORS("com/openbravo/images/floors.png"),
    ICON_SUPPLIER("com/openbravo/images/supplier_sml.png"), //TODO Add suffix SMALL
    ICON_USER_SMALL("com/openbravo/images/user_sml.png"),   //TODO Add suffix SMALL
    ICON_USER("com/openbravo/images/user.png"),
    ICON_CATEGORY("com/openbravo/images/category.png"),
    ICON_SUBCATEGORY("com/openbravo/images/subcategory.png"),
    ICON_NULL("com/openbravo/images/null.png"),
    ICON_CASH("com/openbravo/images/cash.png"),
    ICON_RUN_SCRIPT("com/openbravo/images/run_script.png"),
    ICON_PAY("com/openbravo/images/pay.png"),
    ICON_REFUNDIT("com/openbravo/images/refundit.png"),
    ICON_CANCEL("com/openbravo/images/cancel.png"),
    ICON_PACKAGE("com/openbravo/images/package.png");

    private final String resourcePath;

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
    
    public static ImageIcon getIcon(String resourcePath, int width, int height){
        ThumbNailBuilder tnbcat = new ThumbNailBuilder(width, height, resourcePath);
        ImageIcon icon = new ImageIcon(tnbcat.getThumbNail(null));
        return icon;
    }
    
    public ImageIcon getIcon(int width, int height){
        return ImageResources.getIcon(resourcePath,width, height);
    }
    
    public URL getRsourceURL(){
        return getClass().getResource(resourcePath);
    }

}
