/*
 * Copyright (C) 2022 KriolOS
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
package com.openbravo.pos.domain.entity.businesspartner;

import com.openbravo.data.loader.IKeyed;
import com.openbravo.format.Formats;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 *
 * @author poolborges
 */
//@RequiredArgsConstructor
//@ToString( doNotUseGetters = true )
//@EqualsAndHashCode( doNotUseGetters = true )
public class BusinessPartner implements Serializable, IKeyed {

    private static final long serialVersionUID = 1L;
    
    protected String id;
    protected String searchkey;
    protected String taxid;
    protected String name;
    protected String postal;
    protected String phone;
    protected String email;
    protected BufferedImage image; 
    
    public BusinessPartner(String id) {

        this.id = id;
        searchkey = null;
        taxid = null;
        name = null;
        postal = null;
        phone = null;
        email = null;
    }
    
    public BusinessPartner(String id, String searchkey, String name) {
        this(id);
        this.id = id;
        this.searchkey = searchkey;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getKey() {
        return id;
    }

    public String printTaxid() {
        return Formats.STRING.formatValue(taxid);
    }    

    public String printName() {
        return Formats.STRING.formatValue(getName());
    }
    
    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage img) {
        image = img;
    }
}
