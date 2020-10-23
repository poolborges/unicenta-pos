//    KrOS POS
//    Copyright (c) 2009-2018 uniCenta
//    
//
//     
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
//    along with KrOS POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.suppliers;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.pos.util.StringUtils;
import java.io.Serializable;

/** @author Jack Gerrard */


public class SupplierInfo implements Serializable, IKeyed {
    
    private static final long serialVersionUID = 9093257536541L;

    protected String m_sID;
    protected String m_sSearchkey;
    protected String m_sTaxid;
    protected String m_sName;
    protected String m_sPostal;
    protected String m_sPhone;
    protected String m_sEmail; 
    
    /** Creates a new instance of SupplierInfo
     * @param id */
    public SupplierInfo(String id) {

        m_sID = id;
        m_sSearchkey = null;
        m_sTaxid = null;
        m_sName = null;
        m_sPostal = null;
        m_sPhone = null;
        m_sEmail = null;
    }

    public SupplierInfo(String id, String searchkey, String name) {
        m_sID = id;
        m_sSearchkey = searchkey;
        m_sName = name;
    }
    
    /**
     *
     * @return
     */
    @Override
    public Object getKey() {
        return m_sID;
    }
    
    /**
     *
     * @param dr
     * @throws BasicException
     */
    public void readValues(DataRead dr) throws BasicException {
        m_sID = dr.getString(1);
        m_sName = dr.getString(2);
    }    
    
    /**
     *
     * @return id string
     */
    public String getID() {
        return m_sID;
    }
    public void setID(String sID) {
        m_sID = sID;
    }
    

  /**
     *
     * @return taxid string
   */
    
    public String getTaxid() {
        return m_sTaxid;
    }    
    public void setTaxid(String sTaxid) {
        m_sTaxid = sTaxid;
    }
    public String printTaxid() {
        return StringUtils.encodeXML(m_sTaxid);
    }    

    
    /**
     *
     * @return searchkey string
     */
    public String getSearchkey() {
        return m_sSearchkey;
    }
    public void setSearchkey(String sSearchkey) {
        m_sSearchkey = sSearchkey;
    }
    
    /**
     *
     * @return name string
     */
    public String getName() {
        return m_sName;
    }   
    public void setName(String sName) {
        m_sName = sName;
    }

    /**
     *
     * @return postal/zip code string
     */
    public String getPostal() {
        return m_sPostal;
    }
        public void setPostal(String sPostal) {
        m_sPostal = sPostal;
    }
    
    /**
     *
     * @return Primary Telephone string
     */
    public String getPhone() {
        return m_sPhone;
    }
    public void setPhone(String sPhone) {
        m_sPhone = sPhone;
    }

    /**
     *
     * @return email string
     */
    public String getEmail() {
        return m_sEmail;
    }

    public void setEmail(String sEmail) {
        m_sEmail = sEmail;
    }

    /**
     *
     * @return
     */
    public String printName() {
        return StringUtils.encodeXML(m_sName);
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
}