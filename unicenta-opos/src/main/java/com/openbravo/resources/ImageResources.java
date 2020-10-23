/*
 * Copyright (C) 2020 KiolOS<https://github.com/kriolos>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
