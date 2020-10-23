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

package com.openbravo.pos.payment;

public class PaymentInfoMagcard extends PaymentInfo {
     
    protected double m_dTotal;
    protected double m_dTip;
    protected String m_sHolderName;
    protected String m_sCardNumber;
    protected String m_sExpirationDate;
    protected String track1;
    protected String track2;
    protected String track3;
    protected String m_sTransactionID;
    protected String m_sAuthorization;    
    protected String m_sErrorMessage;
    protected String m_sReturnMessage;
    //JG Jan 2018 
    protected String encryptedTrack;
    protected String encryptionKey;
    protected String m_dCardName =null;

    protected Boolean chipAndPin = Boolean.FALSE;
    protected String verification;

    /**
     * Creates a new instance of PaymentInfoMagcard
     * @param sHolderName
     * @param sCardNumber
     * @param sExpirationDate
     * @param track1
     * @param track2
     * @param track3
     * @param encryptedCard
     * @param encryptKey
     * @param sTransactionID
     * @param dTotal
     */
    public PaymentInfoMagcard(String sHolderName, String sCardNumber,
            String sExpirationDate, String track1, String track2, String track3, String encryptedCard, String encryptKey,
            String sTransactionID, double dTotal) {
        m_sHolderName = sHolderName;
        m_sCardNumber = sCardNumber;
        m_sExpirationDate = sExpirationDate;
        this.track1 = track1;
        this.track2 = track2;
        this.track3 = track3;
        encryptedTrack = encryptedCard;
        encryptionKey = encryptKey;
        m_sTransactionID = sTransactionID;
        m_dTotal = dTotal;
        

        m_sAuthorization = null;
        m_sErrorMessage = null;
        m_sReturnMessage = null;
    }
    
    /**
     * Creates a new instance of PaymentInfoMagcard
     * @param sHolderName
     * @param sCardNumber
     * @param sExpirationDate
     * @param sTransactionID
     * @param dTotal
     */
    public PaymentInfoMagcard(String sHolderName, String sCardNumber, 
            String sExpirationDate, String sTransactionID, double dTotal) {
        this(sHolderName, sCardNumber, sExpirationDate, 
                null, null, null, null, null, sTransactionID, dTotal);
    }
    
    @Override
    public PaymentInfo copyPayment() {
        PaymentInfoMagcard p = new PaymentInfoMagcard(m_sHolderName, 
                m_sCardNumber, m_sExpirationDate, track1, track2,
                track3, encryptedTrack, encryptionKey, m_sTransactionID, m_dTotal);
        p.m_sAuthorization = m_sAuthorization;
        p.m_sErrorMessage = m_sErrorMessage;
        return p;
    }

    public String getName() {
//        return "magcard";
        return "ccard";
    }

    @Override
    public double getTotal() {
        return m_dTotal;
    }

    public double getTip() {
        return m_dTip;
    }

    public boolean isPaymentOK() {
        return m_sAuthorization != null;
    }    

    public String getHolderName() {
        return m_sHolderName;
    }

    @Override
    public String getCardName() {
        if (chipAndPin){
            return m_dCardName;
        }
        return getCardType(m_sCardNumber);
    }

  public String getCardNumber() {
        return m_sCardNumber;
    }

    public String getExpirationDate() {
        return m_sExpirationDate;
    }    

    @Override
    public String getTransactionID() {
        return m_sTransactionID;
    }

    /**
     * Get tracks of magnetic card. Framing characters: - start sentinel (SS) -
     * end sentinel (ES) - LRC
     *
     * @return tracks of the magnetic card
    */
    public String getEncryptedCardData() {
        return encryptedTrack;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }
    
    /** @param sCardNumber
     * @return
     */
    public String getCardType(String sCardNumber){
        String c = "UNKNOWN";
        
       if (sCardNumber.startsWith("4")) {
           c = "VISA";
       } else if (sCardNumber.startsWith("6")) {
           c = "DISC";
       } else if (sCardNumber.startsWith("5")) {
           c = "MAST";
       } else if (sCardNumber.startsWith("34") || sCardNumber.startsWith("37")) {
           c = "AMEX";
       } else if (sCardNumber.startsWith("3528") || sCardNumber.startsWith("3589")) {
           c = "JCB";
       } else if (sCardNumber.startsWith("3")) {
           c = "DINE";
       }
       m_sCardNumber = c;
       return c;
   }
   
    public String getTrack1(boolean framingChar) {
        return (framingChar)
            ? track1
                : track1.substring(1, track1.length() - 2);
    }

    public String getTrack2(boolean framingChar) {
        return (framingChar)
            ? track2
                : track2.substring(1, track2.length() - 2);
    }

    public String getTrack3(boolean framingChar) {
        return (framingChar)
            ? track3
                : track3.substring(1, track3.length() - 2);
    }
    
    public String getAuthorization() {
        return m_sAuthorization;
    }

    public String getMessage() {
        return m_sErrorMessage;
    }
    
    public void paymentError(String sMessage, String moreInfo) {
        m_sAuthorization = null;
        m_sErrorMessage = sMessage + "\n" + moreInfo;
    }

    public void setReturnMessage(String returnMessage) {
        m_sReturnMessage = returnMessage;
    }
    
    public String getReturnMessage() {
        return m_sReturnMessage;
    }
    
    public void paymentOK(String sAuthorization, String sTransactionId, String sReturnMessage) {
        m_sAuthorization = sAuthorization;
        m_sTransactionID = sTransactionId;
        m_sReturnMessage = sReturnMessage;
        m_sErrorMessage = null;
    }  

    public String printCardNumber() {
        
System.out.println("Full Card Number : " + m_sCardNumber);

        if (m_sCardNumber.length() > 4) {
            return m_sCardNumber.substring(0, m_sCardNumber.length() - 4).replaceAll("\\.", "*")
                    + m_sCardNumber.substring(m_sCardNumber.length() - 4);
        } else {
            return "****";
        }
    }

    public String printExpirationDate() {
        return m_sExpirationDate;
    }

    public String printAuthorization() {
        return m_sAuthorization;
    }

    public String printTransactionID() {
        return m_sTransactionID;
    }

    public boolean getIsProcessed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public void setIsProcessed(boolean value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public double getPaid() {
        return (0.0); 
    }

    @Override
    public double getChange(){
       return 0.00;
   }   

    @Override
    public double getTendered() {
        return 0.00;
    }
    
    @Override
    public String getVoucher() {
        return null;
    }

    public void setCardName(String m_dCardName){
        this.m_dCardName = m_dCardName;
    }

    public Boolean isChipAndPin() {
        return chipAndPin;
    }

    public void setChipAndPin(Boolean chipAndPin) {
        this.chipAndPin = chipAndPin;
    }

    public String printVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }
}