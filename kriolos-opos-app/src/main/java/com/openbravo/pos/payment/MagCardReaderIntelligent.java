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

package com.openbravo.pos.payment;

public final class MagCardReaderIntelligent implements MagCardReader {
    
    private String cardHolderName;
    private String cardNumber;
    private String cardExpirationDate;
    
    private StringBuffer m_sField;
    
    
    /**
     * Character with code 0x0009 (corresponds to ASCII code Tabulator '\t')
     */
    private static final char CHAR_TAB = 0x0009;
    
    /**
     * Character with code 0x000A (corresponds to ASCII code Newline '\n')
     */
    private static final char CHAR_NEWLINE = 0x000A;
    
    private static final int READING_HOLDER = 0;
    private static final int READING_NUMBER = 1;
    private static final int READING_DATE = 2;
    private static final int READING_FINISHED = 3;
    private int readingState;
            
    /** Creates a new instance of BasicMagCardReader */
    public MagCardReaderIntelligent() {
        reset();
    }
 
    @Override
    public String getReaderName() {
        return "Basic magnetic card reader";
    }
    
    @Override
    public void reset() {
        cardHolderName = null;
        cardNumber = null;
        cardExpirationDate = null;
        m_sField = new StringBuffer();
        readingState = READING_HOLDER;
    }
    
    @Override
    public void appendChar(char c) {
       
        switch (readingState) {
            case READING_HOLDER:
            case READING_FINISHED:
            switch (c) {
                case 0x0009:
                    cardHolderName = m_sField.toString();
                    m_sField = new StringBuffer();
                    readingState = READING_NUMBER;
                    break;
                case 0x000A:
                    cardHolderName = null;
                    cardNumber = null;
                    cardExpirationDate = null;
                    m_sField = new StringBuffer();
                    readingState = READING_HOLDER;
                    break;
                default:
                    m_sField.append(c);
                    readingState = READING_HOLDER;
                    break;
            }
                break;

            case READING_NUMBER:
            switch (c) {
                case 0x0009:
                    cardNumber = m_sField.toString();
                    m_sField = new StringBuffer();
                    readingState = READING_DATE;
                    break;
                case 0x000A:
                    cardHolderName = null;
                    cardNumber = null;
                    cardExpirationDate = null;
                    m_sField = new StringBuffer();
                    readingState = READING_HOLDER;
                    break;
                default:
                    m_sField.append(c);
                    break;
            }
                break;                
                
            case READING_DATE:
            switch (c) {
                case 0x0009:
                    cardHolderName = cardNumber;
                    cardNumber = cardExpirationDate;
                    cardExpirationDate = null;
                    m_sField = new StringBuffer();
                    break;
                case 0x000A:
                    cardExpirationDate = m_sField.toString();
                    m_sField = new StringBuffer();
                    readingState = READING_FINISHED;
                    break;
                default:
                    m_sField.append(c);
                    break;
            }
                break;  
  
        }
    }
    
    @Override
    public boolean isComplete() {
        return readingState == READING_FINISHED;
    }
    
    @Override
    public String getHolderName() {
        return cardHolderName;
    }
    @Override
    public String getCardNumber() {
        return cardNumber;
    }
    @Override
    public String getExpirationDate() {
        return cardExpirationDate;
    }
    @Override
    public String getTrack1() {
        return null;
    }
    @Override
    public String getTrack2() {
        return null;
    }    
    @Override
    public String getTrack3() {
        return null;
    }       

    @Override
    public String getEncryptedCardData() {
        return null;
    }

    @Override
    public String getEncryptionKey() {
        return null;
    }
}
