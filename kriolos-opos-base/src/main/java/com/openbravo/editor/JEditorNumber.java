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
package com.openbravo.editor;

import com.openbravo.basic.BasicException;
import com.openbravo.format.Formats;
import java.awt.Toolkit;

/**
 *
 * @author JG uniCenta
 * @param <T>
 */
public abstract class JEditorNumber<T extends Number> extends JEditorAbstract {

    // Variable numerica
    protected final static int NUMBER_ZERONULL = 0;
    protected final static int NUMBER_INT = 1;
    protected final static int NUMBER_DEC = 2;
    protected final static char DECIMAL_SEPARATOR = '.';
    private static final long serialVersionUID = 1L;

    private int m_iNumberStatus;
    private String m_sNumber;
    private boolean m_bNegative;

    private final Formats m_fmt;

    //private final Boolean priceWith00;

    public JEditorNumber() {
        m_fmt = getFormat();
        //numberWithDecimalZero
        //priceWith00 = ("true".equals(AppConfig.getInstance().getProperty("till.pricewith00")));
        reset();
    }

    protected abstract Formats getFormat();
    protected abstract T getCurrentValue();
    protected abstract void setCurrentValue(T value);

    public T getValue() {
        return getCurrentValue();
    }
    
    public void setValue(T value) {
        String sOldText = getText();
        
        setCurrentValue(value);
        
        
        reprintText();
        firePropertyChange("Text", sOldText, getText());
    }
    
    public void reset() {

        String sOldText = getText();

        m_sNumber = "";
        m_bNegative = false;
        m_iNumberStatus = NUMBER_ZERONULL;

        reprintText();

        firePropertyChange("Text", sOldText, getText());
    }

    protected void setINumberStatus(int m_iNumberStatus) {
        this.m_iNumberStatus = m_iNumberStatus;
    }

    protected void setSNumber(String m_sNumber) {
        this.m_sNumber = m_sNumber;
    }

    protected void setBNegative(boolean m_bNegative) {
        this.m_bNegative = m_bNegative;
    }


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
     * @return @throws BasicException
     */
    @Override
    protected String getTextFormat() throws BasicException {
        return m_fmt.formatValue(getValue());
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
        } else if (cTrans == DECIMAL_SEPARATOR && m_iNumberStatus == NUMBER_ZERONULL) {
            m_iNumberStatus = NUMBER_DEC;
            m_sNumber = "0" + DECIMAL_SEPARATOR;
        } else if (cTrans == DECIMAL_SEPARATOR && m_iNumberStatus == NUMBER_ZERONULL) {
            m_iNumberStatus = NUMBER_INT;
            m_sNumber = "0";

        } else if ((cTrans == '0' || cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
                && (m_iNumberStatus == NUMBER_INT)) {
            //m_iNumberStatus = NUMBER_INT;
            m_sNumber += cTrans;
//         } else if (cTrans == DEC_SEP &&  m_iNumberStatus == NUMBER_INT && !priceWith00) {
        } else if (cTrans == DECIMAL_SEPARATOR && m_iNumberStatus == NUMBER_INT) {
            m_iNumberStatus = NUMBER_DEC;
            m_sNumber += DECIMAL_SEPARATOR;
        } else if (cTrans == DECIMAL_SEPARATOR && m_iNumberStatus == NUMBER_INT) {
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

    @Override
    protected abstract int getMode();
}
