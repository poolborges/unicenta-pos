package com.openbravo.pos.cash;

import com.openbravo.basic.BasicException;
import java.util.Date;

/**
 * Service for cash management operations (opening/closing cash).
 */
public interface CashManagementService {

    void addCloseCash(CashRegister cash) throws BasicException;
    
    void closeCash(String host, int sequence, String money, Date dateEnd, int noSales) throws BasicException;
    
    boolean isCashActive(String id) throws BasicException;

    CashRegister getCloseCashBySequence(String host, int sequence) throws BasicException;
    
    CashRegister getCloseCashByMoney(String moneyToken) throws BasicException;
    
    int getCloseCashSequenceByHost(String host) throws BasicException;

    int getNumOfNoSales(Date startDate) throws BasicException;

    int getNumOfRemovedLines(Date startDate) throws BasicException;

    int getNumOfVoidLines(Date startDate) throws BasicException;
}
