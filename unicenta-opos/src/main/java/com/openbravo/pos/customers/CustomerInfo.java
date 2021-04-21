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

import com.openbravo.pos.util.StringUtils;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/** @author jack gerrard, adrianromero */
public class CustomerInfo implements Serializable {
    
    private static final long serialVersionUID = 9083257536541L;

    protected String id;
    protected String searchkey;
    protected String taxid;
    protected String name;
    protected String postal;
    protected String phone;
    protected String email;
    protected BufferedImage image; 
    protected Double curdebt;     

    public CustomerInfo(String id) {
        this.id = id;
        this.searchkey = null;
        this.taxid = null;
        this.name = null;
        this.postal = null;
        this.phone = null;
        this.email = null;
        this.image = null;
        this.curdebt = null;
    }

    public String getId() {
        return id;
    }

    public String getSearchkey() {
        return searchkey;
    }

    public void setSearchkey(String searchkey) {
        this.searchkey = searchkey;
    }

    public String getTaxid() {
        return taxid;
    }

    public void setTaxid(String taxid) {
        this.taxid = taxid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPcode() {
        return postal;
    }

    public void setPcode(String postal) {
        this.postal = postal;
    }

    public String getPhone1() {
        return phone;
    }

    public void setPhone1(String phone) {
        this.phone = phone;
    }

    public String getCemail() {
        return email;
    }

    public void setCemail(String email) {
        this.email = email;
    }

    public String printName() {
        return StringUtils.encodeXML(name);
    }
    
    @Override
    public String toString() {
        return getName();        
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