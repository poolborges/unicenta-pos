/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openbravo.pos.ticket;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.data.loader.SerializerRead;

public class HostInfo implements IKeyed {

    //MONEY     HOST    HOSTSEQUENCE    DATESTART       DATEEND
    //private static final long serialVersionUID = 8612449444103L;
    private String m_sMoney;
    private String m_sHost;
    private String m_Hostsequence;

    /** Creates new CategoryInfo
     * @param money
     * @param host
     * @param hostsequence */
    public HostInfo(String money, String host, String hostsequence) {
       
        m_sMoney = host; // hack to search by hostname
        m_sHost = host;
        m_Hostsequence = hostsequence;
    }

    @Override
    public Object getKey() {
        return m_sMoney;
    }
   
    public String getHostsequence() {
        return m_Hostsequence;
    }

    public void setHostsequence(String m_Hostsequence) {
        this.m_Hostsequence = m_Hostsequence;
    }

    public String getHost() {
        return m_sHost;
    }

    public void setHost(String m_sHost) {
        this.m_sHost = m_sHost;
    }

    public String getMoney() {
        return m_sMoney;
    }

    public void setMoney(String m_sMoney) {
        this.m_sMoney = m_sMoney;
    }

    @Override
    public String toString() {
        return m_sHost;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
    @Override
    public Object readValues(DataRead dr) throws BasicException {
            return new HostInfo(dr.getString(1), dr.getString(2), dr.getString(3));
        }
        };
    }
}

