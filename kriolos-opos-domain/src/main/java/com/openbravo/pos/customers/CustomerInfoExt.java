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

package com.openbravo.pos.customers;

import com.openbravo.format.Formats;
import com.openbravo.pos.util.RoundUtils;
import java.awt.image.BufferedImage;
import java.util.Date;

/**
 *
 * @author adrianromero
 * @author JG uniCenta 
 */
public class CustomerInfoExt extends CustomerInfo {

    private static final long serialVersionUID = 1L;

    protected String taxcustomerid;
    protected String taxcustcategoryid;
    protected String card;
    protected Double maxdebt;
    protected String address;
    protected String address2;
    protected String city;
    protected String region;
    protected String country;    
    protected String firstname;
    protected String lastname;
    protected String phone2;
    protected String fax;
    protected String notes;
    protected boolean visible;
    protected Date curdate;
    protected Double accdebt;
    protected boolean isvip;
    protected Double discount;
    protected String prepay;
    protected String memodate;    

    public CustomerInfoExt(String id) {
        super(id);
    }

    public String getTaxCustCategoryID() {
        return taxcustcategoryid;        
    }
    public void setTaxCustCategoryID(String taxcustcategoryid) {
        this.taxcustcategoryid = taxcustcategoryid;
    }
    
    public String getTaxCustomerID() {
        return taxcustomerid;
    }
    public void setTaxCustomerID(String taxcustomerid) {
        this.taxcustomerid = taxcustomerid;
    }
    public String printTaxCustomerID() {       
        return Formats.STRING.formatValue(taxcustomerid);
    }
    
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    

    /**
     *
     * @return Is visible Y/N? boolean
     */
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    

    /**
     * Get Customer hashed member/loyalty card string
     * 
     * @return customer's hashed member/loyalty card string
     */
    public String getCard() {
        return card;
    }
    public void setCard(String card) {
        this.card = card;
    }

    /**
     *
     * @return customer's maximum allowed debt value
     */
    public Double getMaxdebt() {
        return maxdebt;
    }
    public void setMaxdebt(Double maxdebt) {
        this.maxdebt = maxdebt;
    }
    public String printMaxDebt() {       
        return Formats.CURRENCY.formatValue(RoundUtils.getValue(getMaxdebt()));
    }

    
    /**
     *
     * @return customer's last ticket transaction date
     */
    public Date getCurdate() {
        return curdate;
    }
    public void setCurdate(Date curdate) {
        this.curdate = curdate;
    }
    public String printCurDate() {       
        return Formats.DATE.formatValue(getCurdate());
    }

    /**
     *
     * @return customer's current value of account
     */
    public Double getAccdebt() {
        return accdebt;
    }
    public void setAccdebt(Double accdebt) {
        this.accdebt = accdebt;
    }
    public String printCurDebt() {       
        return Formats.CURRENCY.formatValue(RoundUtils.getValue(getAccdebt()));
    }
    
    /**
     *
     * @return prepay string
     */
    public String getPrePay() {
        return prepay;
    }
    public void setPrePay(String prepay) {
        this.prepay = prepay;
    }
    
    
    /**
     *
     * @param amount
     * @param d
     */
    public void updateCurDebt(double amount, Date d) {
        
        accdebt = accdebt == null ? amount : accdebt + amount;
        curdate =  (new Date());

        if (RoundUtils.compare(accdebt, 0.0) > 0) {
            if (curdate == null) {                
                // new date
                curdate = d;
            }
        } else if (RoundUtils.compare(accdebt, 0.0) == 0) {       
            accdebt = null;
            curdate = null;
        } else { // < 0
//            curdate = null;
        }      
    }

    /**
     *
     * @return customer's firstname string
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     *
     * @param firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     *
     * @return customer's lastname string
     */
    public String getLastname() {
        return lastname;
    }

    /**
     *
     * @param lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     *
     * @return customer's Primary telephone string
     */
    public String getPhone1() {
        return phone;
    }

    
    public void setPhone1(String phone1) {
        this.phone = phone1;
    }
    
    /**
     *
     * @return customer's Secondary telephone string
     */
    public String getPhone2() {
        return phone2;
    }

    /**
     *
     * @param phone2
     */
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    /**
     *
     * @return customer's fax number string
     */
    public String getFax() {
        return fax;
    }

    /**
     *
     * @param fax
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     *
     * @return customer's address line 1 string
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
     * @return customer's address line 2 string
     */
    public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }


    /**
     *
     * @return customer's address city string
     */
    public String getCity() {
        return city;
    }
    /**
     *
     * @param city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @return customer's address region/state/county string
     */
    public String getRegion() {
        return region;
    }
    /**
     *
     * @param region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     *
     * @return customer's address country string
     */
    public String getCountry() {
        return country;
    }
    /**
     *
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @return customer's photograph / image
     */
    @Override
    public BufferedImage getImage() {
        return image;
    }
    /**
     *
     * @param img
     */
    @Override
    public void setImage(BufferedImage img) {
        this.image = img;
    }
    
    /**
     *
     * @return Is VIP Y/N? boolean
    */
    public boolean isVIP() {
        return isvip;
    }
    public void setisVIP(boolean isvip) {
        this.isvip = isvip;
    }
    
    
    /**
     *
     * @return customer's discount allowed
     */
    public Double getDiscount() {
        return discount;
    }
    public void setDiscount(Double discount) {
        this.discount = discount;
    }
    /**
     *
     * @return memo date string
     */
    public String getMemoDate() {
        return memodate;
    }
    public void setMemoDate(String memodate) {
        this.memodate = memodate;
    }
    
    
    public String printAddress2() {       
        return Formats.STRING.formatValue(address2);
    } 
    
    public String printDiscount() {       
        return Formats.CURRENCY.formatValue(RoundUtils.getValue(getDiscount()));
    }  

    public String printPostal() {       
        return Formats.STRING.formatValue(postal);
    }  
    
    public String printPhone() {       
        return Formats.STRING.formatValue(phone);
    } 
    public String printMemoDate() {       
        return Formats.STRING.formatValue(memodate);
    }        

}
