//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta
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

package com.openbravo.pos.suppliers;

import com.openbravo.format.Formats;
import com.openbravo.pos.util.RoundUtils;
import java.util.Date;

/**
 *
 * @author JG uniCenta 
 */
public class SupplierInfoExt extends SupplierInfo {
    
    protected String suppliertaxid;
    protected String suppliervatid;    
    protected String notes;
    protected boolean visible;
    protected String card;
    protected Double maxdebt;
    protected Date curdate;
    protected Double curdebt;
    protected String firstname;
    protected String lastname;
    protected String email;
    protected String phone;
    protected String phone2;
    protected String fax;
    protected String address;
    protected String address2;
    protected String postal;
    protected String city;
    protected String region;
    protected String country;

    /** Creates a new instance of SupplierInfoExt
     * @param id */
    public SupplierInfoExt(String id) {
        super(id);
    }

    public String getSupplierTaxID() {
        return suppliertaxid;
    }
    public void setSupplierTAXID(String suppliertaxid) {
        this.suppliertaxid = suppliertaxid;
    }
    public String printSupplierTaxID() {       
        return Formats.STRING.formatValue(suppliertaxid);
    }    

    public String getSupplierVATID() {
        return suppliervatid;
    }
    public void setSupplierVATID(String suppliervatid) {
        this.suppliervatid = suppliervatid;
    }
    public String printSupplierVATID() {       
        return Formats.STRING.formatValue(suppliervatid);
    }    
    
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Double getMaxdebt() {
        return maxdebt;
    }
    public void setMaxdebt(Double maxdebt) {
        this.maxdebt = maxdebt;
    }
    public String printMaxDebt() {       
        return Formats.CURRENCY.formatValue(RoundUtils.getValue(getMaxdebt()));
    }
    
    public Date getCurdate() {
        return curdate;
    }
    public void setCurdate(Date curdate) {
        this.curdate = curdate;
    }
    public String printCurDate() {       
        return Formats.DATE.formatValue(getCurdate());
    }

    public Double getCurdebt() {
        return curdebt;
    }
    public void setCurdebt(Double curdebt) {
        this.curdebt = curdebt;
    }
    public String printCurDebt() {       
        return Formats.CURRENCY.formatValue(RoundUtils.getValue(getCurdebt()));
    }
    
    
    /**
     *
     * @param amount
     * @param d
     */
    public void updateCurDebt(Double amount, Date d) {
        
        curdebt = curdebt == null ? amount : curdebt + amount;
        curdate =  (new Date());

        if (RoundUtils.compare(curdebt, 0.0) > 0) {
            if (curdate == null) {
                curdate = d;
            }
        } else if (RoundUtils.compare(curdebt, 0.0) == 0) {
            curdebt = null;
            curdate = null;
        } else { // < 0
            curdate = null;
        }
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPhone() {
        return phone;
    }
    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String printPhone() {       
        return Formats.STRING.formatValue(phone);
    } 

    public String getPhone2() {
        return phone2;
    }
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getFax() {
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     *
     * @return supplier's address line 1 string
     */
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String printAddress() {       
        return Formats.STRING.formatValue(address);
    } 

    /**
     *
     * @return supplier's address line 2 string
     */
    public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     *
     * @return supplier's postal/zip code string
     */
    @Override
    public String getPostal() {
        return postal;
    }
    @Override
    public void setPostal(String postal) {
        this.postal = postal;
    }
    public String printPostal() {       
        return Formats.STRING.formatValue(postal);
    }     

    /**
     *
     * @return supplier's address city string
     */
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @return supplier's address region/state/county string
     */
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     *
     * @return supplier's address country string
     */
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
   
}
