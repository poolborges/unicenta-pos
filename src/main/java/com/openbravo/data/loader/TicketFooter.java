//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2013 uniCenta & previous Openbravo POS works
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;
import com.openbravo.pos.forms.AppViewConnection;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.data.loader.Session;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import java.io.*;

public class TicketFooter {
    private String footer_line1;
    private String footer_line2;       
    private String footer_line3;
    private String footer_line4;
    private String footer_line5;
    private String footer_line6;
    private File m_config;
    private Session session;

    public TicketFooter() {          
        AppConfig config = new AppConfig(m_config);
        // AppViewConnection Session = new AppViewConnection();

    }

    public void loadProperties(AppConfig config) {
        footer_line1=(config.getProperty("till.footer1"));
        footer_line2=(config.getProperty("till.footer2"));
        footer_line3=(config.getProperty("till.footer3"));         
        footer_line4=(config.getProperty("till.footer4"));         
        footer_line5=(config.getProperty("till.footer5"));         
        footer_line6=(config.getProperty("till.footer6"));         
    }
    
    public String getTicketFooterLine1() {
        return footer_line1;
    }
    
    public String getTicketFooterLine2() {
        return footer_line2;
    }
    
    public String getTicketFooterLine3() {
        return footer_line3;
    }
    
    public String getTicketFooterLine4() {
        return footer_line4;
    }
    
    public String getTicketFooterLine5() {
        return footer_line5;
    }
    
    public String getTicketFooterLine6() {
        return footer_line6;
    }
}





