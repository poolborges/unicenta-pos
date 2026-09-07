package com.openbravo.pos.cash;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializableRead;
import java.io.Serializable;
import java.util.Date;

public class CashRegister implements SerializableRead, Serializable {

    private String money;
    private String host;
    private Integer hostSequence;
    private Date startDate;
    private Date endDate;
    private Integer noSales;

    @Override
    public void readValues(DataRead dr) throws BasicException {
        money = dr.getString(1);
        host = dr.getString(2);
        hostSequence = dr.getInt(3);
        startDate = dr.getTimestamp(4);
        endDate = dr.getTimestamp(5);
        noSales = dr.getInt(6);
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getHostsequence() {
        return hostSequence;
    }

    public void setHostsequence(Integer hostsequence) {
        this.hostSequence = hostsequence;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date dateend) {
        this.endDate = dateend;
    }

    public Integer getNoSales() {
        return noSales;
    }

    public void setNoSales(Integer noSales) {
        this.noSales = noSales;
    }
    
    
}
