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

package com.openbravo.editor;

import com.openbravo.basic.BasicException;
import com.openbravo.format.DoubleUtils;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import java.awt.Toolkit;
import java.io.File;

/**
 *
 * @author JG uniCenta
 */
public abstract class JEditorNumber extends JEditorAbstract {
    
    // Variable numerica
    private final static int NUMBER_ZERONULL = 0;
    private final static int NUMBER_INT = 1;
    private final static int NUMBER_DEC = 2;

    private char DEC_SEP = '.';
    private int m_iNumberStatus;
    private String m_sNumber;
    private boolean m_bNegative;
    
    private Formats m_fmt;
    
    private Boolean priceWith00;
    
    /** Creates a new instance of JEditorNumber */
    public JEditorNumber() {
        m_fmt = getFormat();
        
// added JDL 11.05.13        
        AppConfig m_config =  AppConfig.getInstance();        
        m_config.load();        
        priceWith00 =("true".equals(m_config.getProperty("till.pricewith00")));
        m_config=null;         
        reset();
    }
    
    /**
     *
     * @return
     */
    protected abstract Formats getFormat();
    
    /**
     *
     */
    public void reset() {
        
        String sOldText = getText();
        
        m_sNumber = "";
        m_bNegative = false;
        m_iNumberStatus = NUMBER_ZERONULL;
        
        reprintText();
        
        firePropertyChange("Text", sOldText, getText());
    }

    /**
     *
     * @param dvalue
     */
    public void setDoubleValue(Double dvalue) {
        
        String sOldText = getText();
        
        if (dvalue == null) {
            m_sNumber = "";
            m_bNegative = false;
            m_iNumberStatus = NUMBER_ZERONULL;                 
        } else if (dvalue >= 0.0) {
            m_sNumber = formatDouble(dvalue);
            m_bNegative = false;
            m_iNumberStatus = NUMBER_ZERONULL;            
        } else {
            m_sNumber = formatDouble(-dvalue);
            m_bNegative = true;
            m_iNumberStatus = NUMBER_ZERONULL;            
        }
        reprintText();
        
        firePropertyChange("Text", sOldText, getText());
    }

    /**
     *
     * @return
     */
    public Double getDoubleValue() {  
        String text = getText();   
        if (text == null || text.equals("")) {
            return null; 
        } else {
            try {
                //return priceWith00? Double.parseDouble(text)/100 : Double.parseDouble(text);
                return Double.parseDouble(text);
               // return  Double.parseDouble(text);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
    
    /**
     *
     * @param ivalue
     */
    public void setValueInteger(int ivalue) {
        
        String sOldText = getText();
        
        if (ivalue >= 0) {
            m_sNumber = Integer.toString(ivalue);
            m_bNegative = false;
            m_iNumberStatus = NUMBER_ZERONULL;            
        } else {
            m_sNumber = Integer.toString(-ivalue);
            m_bNegative = true;
            m_iNumberStatus = NUMBER_ZERONULL;            
        }
        reprintText();
        
        firePropertyChange("Text", sOldText, getText());
    }

    /**
     *
     * @return
     * @throws BasicException
     */
    public int getValueInteger() throws BasicException {  
        try {
            return Integer.parseInt(getText());
        } catch (NumberFormatException e) {
            throw new BasicException(e);
        }
    }    
    
    private String formatDouble(Double value) {
        String sNumber = Double.toString(DoubleUtils.fixDecimals(value));
        if (sNumber.endsWith(".0")) {
            sNumber = sNumber.substring(0,  sNumber.length() - 2);
        }
        return sNumber;
    }
    
    /**
     *
     * @return
     */
    @Override
    protected String getEditMode() {
        return "-1.23";
    }

    /**
     *
     * @return
     */
    public String getText() {
        return (m_bNegative ? "-" : "") + m_sNumber;
    }   

    /**
     *
     * @return
     */
    @Override
    protected int getAlignment() {
        return javax.swing.SwingConstants.RIGHT;
    }
    
    /**
     *
     * @return
     */
    @Override
    protected String getTextEdit() {
        return getText();
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    protected String getTextFormat() throws BasicException {
        return m_fmt.formatValue(getDoubleValue());
    }
    
    /**
     *
     * @param cTrans
     */
    @Override
    protected void typeCharInternal(char cTrans) {
        transChar(cTrans);
    }
    
    /**
     *
     * @param cTrans
     */
    @Override
    protected void transCharInternal(char cTrans) {
        
        String sOldText = getText();

        if (cTrans == '\u007f') {
            reset();
        } else if (cTrans == '-') {
            m_bNegative = !m_bNegative;
        } else if ((cTrans == '0')
        && (m_iNumberStatus == NUMBER_ZERONULL)) {
            // m_iNumberStatus = NUMBER_ZERO;
            m_sNumber = "0";
        } else if ((cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
        && (m_iNumberStatus == NUMBER_ZERONULL)) {
            m_iNumberStatus = NUMBER_INT;
            m_sNumber = Character.toString(cTrans);
//       } else if (cTrans == DEC_SEP &&  m_iNumberStatus == NUMBER_ZERONULL && !priceWith00) {
        } else if (cTrans == DEC_SEP &&  m_iNumberStatus == NUMBER_ZERONULL) {
            m_iNumberStatus = NUMBER_DEC;
            m_sNumber = "0"+DEC_SEP;
        } else if (cTrans == DEC_SEP &&  m_iNumberStatus == NUMBER_ZERONULL) {
            m_iNumberStatus = NUMBER_INT;
            m_sNumber = "0";

        } else if ((cTrans == '0' || cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
        && (m_iNumberStatus == NUMBER_INT)) {
            //m_iNumberStatus = NUMBER_INT;
            m_sNumber += cTrans;
//         } else if (cTrans == DEC_SEP &&  m_iNumberStatus == NUMBER_INT && !priceWith00) {
        } else if (cTrans == DEC_SEP &&  m_iNumberStatus == NUMBER_INT) {
            m_iNumberStatus = NUMBER_DEC;
            m_sNumber += DEC_SEP;
        } else if (cTrans == DEC_SEP &&  m_iNumberStatus == NUMBER_INT) {
//            m_iNumberStatus = NUMBER_DEC;
            m_sNumber += "00";

        } else if ((cTrans == '0' || cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
        && (m_iNumberStatus == NUMBER_DEC)) {
            m_sNumber += cTrans;

        } else {
            Toolkit.getDefaultToolkit().beep(); 
        }       
        
        firePropertyChange("Text", sOldText, getText());
    } 
 
    
 /* Added JDL 13.04.13 routine
 * routine to set the amount appearance to show '.'
 */ 
    private String setTempjPrice(String jPrice){
        jPrice = jPrice.replace(".","");
        return (jPrice.length()<= 2)? jPrice : (new StringBuffer(jPrice).insert(jPrice.length()-2,".").toString());
    }
}
