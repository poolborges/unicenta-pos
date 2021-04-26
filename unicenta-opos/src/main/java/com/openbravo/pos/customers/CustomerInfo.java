//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
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

package com.openbravo.pos.customers;

import com.openbravo.pos.domain.entity.businesspartner.BusinessPartner;
import java.awt.image.BufferedImage;

/** @author jack gerrard, adrianromero */
public class CustomerInfo extends BusinessPartner {
    
    private static final long serialVersionUID = 9083257536541L;

    protected BufferedImage image; 
    protected Double curdebt;     

    public CustomerInfo(String id) {
        super(id);
    }


    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage img) {
        image = img;
    }

    public Double getCurDebt() {
        return curdebt;
    }

    public void setCurDebt(Double curdebt) {
        this.curdebt = curdebt;
    }
}