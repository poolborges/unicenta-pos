package com.openbravo.pos.printer.screen;

import com.openbravo.pos.printer.DeviceDisplay;

import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public interface DeviceDisplayAdvance extends DeviceDisplay {

    public static final int PRODUCT_IMAGE = 1;
    public static final int TICKETLINES = 2;
    public static final int AD_IMAGE = 4;

    boolean hasFeature(int paramInt);

    boolean setProductImage(BufferedImage paramBufferedImage);

    boolean setTicketLines(JPanel paramJTicketLines);
    
    public void setTitle(String title);
    
    public void setCustomerImage(BufferedImage customerImage);
}
