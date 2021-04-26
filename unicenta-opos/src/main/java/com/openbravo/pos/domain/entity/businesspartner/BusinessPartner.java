/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.domain.entity.businesspartner;

import com.openbravo.data.loader.IKeyed;
import com.openbravo.pos.util.StringUtils;
import java.io.Serializable;

/**
 *
 * @author pauloborges
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
    public Object getKey() {
        return (Object)id;
    }

    public String printTaxid() {
        return StringUtils.encodeXML(getTaxid());
    }    

    public String printName() {
        return StringUtils.encodeXML(getName());
    }
    
}
