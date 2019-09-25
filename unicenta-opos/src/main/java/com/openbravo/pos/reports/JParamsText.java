//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.reports;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.QBFCompareEnum;
import com.openbravo.data.loader.SerializerWrite;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import java.awt.Component;

/**
 *
 * @author  adrianromero
 */
public final class JParamsText extends javax.swing.JPanel implements ReportEditorCreator {
    
    private Datas datasvalue;
    private Formats formatsvalue;
    private QBFCompareEnum comparevalue;
    
    /** Creates new form JParamsText */
    public JParamsText() {
        initComponents();
        
        setLabel(AppLocal.getIntString("label.value"));
        setType(Formats.STRING);
    }
    
    /**
     *
     * @param label
     */
    public JParamsText(String label) {
        
        initComponents();
        
        setLabel(label);    
        setType(Formats.STRING);
    }
    
    /**
     *
     * @param label
     * @param format
     */
    public JParamsText(String label, Formats format) {
        
        initComponents();
        
        setLabel(label);    
        setType(format);
    }
    
    /**
     *
     * @param label
     * @param format
     * @param data
     */
    public JParamsText(String label, Formats format, Datas data) {
        
        initComponents();
        
        setLabel(label);    
        setType(format, data);
    }
    
    /**
     *
     * @param label
     */
    public void setLabel(String label) {
        lblField.setText(label);
    }
    
    /**
     *
     * @param format
     * @param data
     */
    public void setType(Formats format, Datas data) {
        formatsvalue = format;
        datasvalue = data;
        setDefaultCompare();
    }
    
    /**
     *
     * @param format
     */
    public void setType(Formats format) {
        
        if (Formats.INT == format) {
             setType(format, Datas.INT);
        } else if (Formats.DOUBLE == format || Formats.CURRENCY == format || Formats.PERCENT == format) {
             setType(format, Datas.DOUBLE);
        } else if (Formats.DATE == format || Formats.TIME == format || Formats.TIMESTAMP == format) {
             setType(format, Datas.TIMESTAMP);
        } else if (Formats.BOOLEAN == format) {
             setType(format, Datas.BOOLEAN);
        } else { // if (Formats.STRING == format) {
            setType(format, Datas.STRING);
        }
    }
    
    /**
     *
     * @param compare
     */
    public void setCompare(QBFCompareEnum compare) {
        comparevalue = compare;
    }
    
    private void setDefaultCompare() {
        if (Formats.INT == formatsvalue) {
             comparevalue = QBFCompareEnum.COMP_LESSOREQUALS;
        } else if (Formats.DOUBLE == formatsvalue || Formats.CURRENCY == formatsvalue || Formats.PERCENT == formatsvalue) {
             comparevalue = QBFCompareEnum.COMP_LESSOREQUALS;
        } else if (Formats.DATE == formatsvalue || Formats.TIME == formatsvalue || Formats.TIMESTAMP == formatsvalue) {
             comparevalue = QBFCompareEnum.COMP_GREATEROREQUALS;
        } else if (Formats.BOOLEAN == formatsvalue) {
             comparevalue = QBFCompareEnum.COMP_EQUALS;
        } else { // if (Formats.STRING == formatsvalue) {
             comparevalue = QBFCompareEnum.COMP_RE;
        }
    }
    
    /**
     *
     * @param app
     */
    @Override
    public void init(AppView app) {
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {
        txtField.setText(null);
    }
    
    /**
     *
     * @return
     */
    @Override
    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(new Datas[] {Datas.OBJECT, datasvalue});
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        
        Object value = formatsvalue.parseValue(txtField.getText());
        txtField.setText(formatsvalue.formatValue(value));
        
        if (value == null) {        
            return new Object[] {QBFCompareEnum.COMP_NONE, null};
        } else {
            return new Object[] {comparevalue, value};
        }
    }      
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblField = new javax.swing.JLabel();
        txtField = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(0, 30));
        setLayout(null);

        lblField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblField.setText("***");
        lblField.setPreferredSize(new java.awt.Dimension(0, 30));
        add(lblField);
        lblField.setBounds(20, 10, 120, 30);

        txtField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtField.setPreferredSize(new java.awt.Dimension(0, 30));
        add(txtField);
        txtField.setBounds(140, 10, 200, 30);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblField;
    private javax.swing.JTextField txtField;
    // End of variables declaration//GEN-END:variables
    
}
