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

package com.openbravo.pos.voucher;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.customers.CustomerInfo;
import com.openbravo.pos.customers.DataLogicCustomers;
import com.openbravo.pos.customers.JCustomerFinder;
import com.openbravo.pos.customers.JDialogNewCustomer;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.util.ValidateBuilder;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public final class VoucherEditor extends javax.swing.JPanel implements EditorRecord {

    private static final DateFormat m_simpledate = new SimpleDateFormat("MM-yy");
    private Object id;
    private final DataLogicCustomers dlCustomers;
    private final  DataLogicSystem dlSystem;
    private CustomerInfo customerInfo;
    private final AppView m_app;
    
    private final ComboBoxValModel m_ReasonModel;    

     public VoucherEditor(DirtyManager dirty,AppView app) {
        m_app = app;

        initComponents();
  
        dlCustomers = (DataLogicCustomers) 
                app.getBean("com.openbravo.pos.customers.DataLogicCustomers");
        dlSystem= (DataLogicSystem) 
                app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        m_jNumber.getDocument().addDocumentListener(dirty);
        m_jCustomer.getDocument().addDocumentListener(dirty);
        m_jAmount.getDocument().addDocumentListener(dirty);
        m_jStatus.getDocument().addDocumentListener(dirty);
        
        jButtonPrint.setVisible(false);       
        
        m_ReasonModel = new ComboBoxValModel();
        m_ReasonModel.add(AppLocal.getIntString("cboption.find"));
        m_ReasonModel.add(AppLocal.getIntString("cboption.create"));              
        jCBCustomer.setModel(m_ReasonModel);   
        jLblStatus.setIcon(null);
                
        writeValueEOF();
    }
     
    @Override
    public void writeValueEOF() {
        id = null;
        m_jNumber.setText(null);
        m_jNumber.setEnabled(false);
        m_jCustomer.setText(null);
        m_jCustomer.setEnabled(false);
        m_jAmount.setText(null);
        m_jAmount.setEnabled(false);
        m_jStatus.setText(null);
        m_jStatus.setEnabled(false);        
        
        jButtonPrint.setEnabled(false);
    }
    
    @Override
    public void writeValueInsert() {
        id = UUID.randomUUID().toString();
        m_jNumber.setText(generateVoucherNumber());
        m_jNumber.setEnabled(true);
        m_jCustomer.setText(null);
        m_jCustomer.setEnabled(true);
        m_jAmount.setText(null);
        m_jAmount.setEnabled(true);
        m_jStatus.setText(null);
        m_jStatus.setText("A");
        
        jButtonPrint.setEnabled(false);
        jButtonPrint.setEnabled(true);        
    }
    
    @Override
    public void writeValueDelete(Object value) {
    if ("A".equals(m_jStatus.getText())) {
        try {
            Object[] attr = (Object[]) value;
            id = attr[0];
            m_jNumber.setText(Formats.STRING.formatValue(attr[1]));
            m_jNumber.setEnabled(false);
            customerInfo = dlCustomers.getCustomerInfo(attr[2].toString());
            m_jCustomer.setText(customerInfo.getName());
            m_jCustomer.setEnabled(false);
            m_jAmount.setText(Formats.DOUBLE.formatValue(attr[3]));
            m_jAmount.setEnabled(false);
            m_jStatus.setText(Formats.STRING.formatValue(attr[4]));
            m_jStatus.setEnabled(false);
            
                jButtonPrint.setEnabled(false);
        } catch (BasicException ex) {
            Logger.getLogger(VoucherEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    } else {
        JOptionPane.showMessageDialog(this, 
            AppLocal.getIntString("message.voucherdelete"),
            AppLocal.getIntString("Check"),
            JOptionPane.WARNING_MESSAGE);
    }
    }    
    
    @Override
    public void writeValueEdit(Object value) {

        try {
            Object[] attr = (Object[]) value;
            id = attr[0];
            m_jNumber.setText(Formats.STRING.formatValue(attr[1]));
            m_jNumber.setEnabled(true);
            customerInfo = dlCustomers.getCustomerInfo(attr[2].toString());
            m_jCustomer.setText(customerInfo.getName());
            m_jCustomer.setEnabled(true);
            m_jAmount.setText(Formats.DOUBLE.formatValue(attr[3]));
            m_jAmount.setEnabled(true);
            m_jStatus.setText(Formats.STRING.formatValue(attr[4]));
            
            jButtonPrint.setEnabled(true);
            
            if (null == m_jStatus.getText()) {
                jLblStatus.setIcon(null);
            } else switch (m_jStatus.getText()) {
                case "A":
                    jLblStatus.setIcon(new javax.swing.ImageIcon(getClass()
                            .getResource("/com/openbravo/images/OK.png")));
                    m_jNumber.setEnabled(true);
                    m_jAmount.setEnabled(true); 
                    jCBCustomer.setEnabled(true);
                    m_jStatus.setText("A");
                    break;
                case "D":
                    jLblStatus.setIcon(new javax.swing.ImageIcon(getClass()
                            .getResource("/com/openbravo/images/refundit.png")));
                    m_jNumber.setEnabled(false);
                    m_jAmount.setEnabled(false);
                    jCBCustomer.setEnabled(false);                    
                    m_jStatus.setText("D");                    
                    
                    break;
                default:
                    jLblStatus.setIcon(null);
                    break;
            }             
            
        } catch (BasicException ex) {
            Logger.getLogger(VoucherEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object createValue() throws BasicException {
        
        Object[] attr = new Object[5];

        attr[0] = id;
        attr[1] = m_jNumber.getText();
        attr[2] = customerInfo.getId();
        attr[3] = Formats.DOUBLE.parseValue(m_jAmount.getText());
        attr[4] = m_jStatus.getText();
        
        return attr;
    }    
     
    @Override
    public Component getComponent() {
        return this;
    }
    
    @Override
    public void refresh() {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        m_jNumber = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_jCustomer = new javax.swing.JTextField();
        m_jAmount = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButtonPrint = new javax.swing.JButton();
        jLblStatus = new javax.swing.JLabel();
        m_jStatus = new javax.swing.JTextField();
        jCBCustomer = new javax.swing.JComboBox<>();

        setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.Number")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(100, 30));

        m_jNumber.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jNumber.setPreferredSize(new java.awt.Dimension(240, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.customer")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 30));

        m_jCustomer.setEditable(false);
        m_jCustomer.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCustomer.setPreferredSize(new java.awt.Dimension(240, 30));

        m_jAmount.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jAmount.setPreferredSize(new java.awt.Dimension(240, 30));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.paymenttotal")); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(100, 30));

        jButtonPrint.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/printer24.png"))); // NOI18N
        jButtonPrint.setToolTipText(AppLocal.getIntString("button.print")); // NOI18N
        jButtonPrint.setFocusPainted(false);
        jButtonPrint.setFocusable(false);
        jButtonPrint.setMargin(new java.awt.Insets(8, 14, 8, 14));
        jButtonPrint.setPreferredSize(new java.awt.Dimension(80, 45));
        jButtonPrint.setRequestFocusEnabled(false);
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });

        jLblStatus.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/ok.png"))); // NOI18N
        jLblStatus.setText(AppLocal.getIntString("label.Status")); // NOI18N
        jLblStatus.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLblStatus.setPreferredSize(new java.awt.Dimension(100, 30));

        m_jStatus.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jStatus.setPreferredSize(new java.awt.Dimension(240, 30));

        jCBCustomer.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jCBCustomer.setMaximumRowCount(2);
        jCBCustomer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Find", "Create" }));
        jCBCustomer.setToolTipText(AppLocal.getIntString("label.voucherCustomer")); // NOI18N
        jCBCustomer.setPreferredSize(new java.awt.Dimension(110, 30));
        jCBCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCustomerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(m_jCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCBCustomer, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(m_jNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(m_jAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(m_jStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                        .addComponent(jButtonPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_jCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCBCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed

    try {
        VoucherInfo  voucherInfo = dlCustomers.getVoucherInfoAll(id.toString());
        BufferedImage image = dlSystem.getResourceAsImage("Window.Logo");
        if (voucherInfo!=null){
            JDialogReportPanel dialog = JDialogReportPanel
                    .getDialog(this,m_app,voucherInfo,image);
            dialog.setVisible(true);
        }
                
    } catch (BasicException ex) {

    }    
}//GEN-LAST:event_jButtonPrintActionPerformed

    private void jCBCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCustomerActionPerformed
        if(jCBCustomer.getSelectedIndex() == 0){

            JCustomerFinder finder = JCustomerFinder.getCustomerFinder(this, dlCustomers);
            finder.setVisible(true);
            customerInfo = finder.getSelectedCustomer() ;

            if (finder.getSelectedCustomer()!=null){
                m_jCustomer.setText(customerInfo.getName()); 
            }
        } else {

         JDialogNewCustomer dialog = JDialogNewCustomer.getDialog(this,m_app);
         dialog.setVisible(true);
       
           customerInfo=dialog.getSelectedCustomer();
            if (dialog.getSelectedCustomer()!=null){
                 m_jCustomer.setText(customerInfo.getName());  
            }
        }

    }//GEN-LAST:event_jCBCustomerActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JComboBox<String> jCBCustomer;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLblStatus;
    private javax.swing.JTextField m_jAmount;
    private javax.swing.JTextField m_jCustomer;
    private javax.swing.JTextField m_jNumber;
    private javax.swing.JTextField m_jStatus;
    // End of variables declaration//GEN-END:variables

    public boolean isDataValid() {
        ValidateBuilder validate = new ValidateBuilder(this);
        validate.setValidate(m_jNumber.getText(),ValidateBuilder.IS_NOT_EMPTY,
                AppLocal.getIntString("message.message.emptynumber"));
        validate.setValidate(m_jCustomer.getText(),ValidateBuilder.IS_NOT_EMPTY,
                AppLocal.getIntString("message.emptycustomer"));
        validate.setValidate(m_jAmount.getText(),ValidateBuilder.IS_DOUBLE,
                AppLocal.getIntString("message.numericamount"));
        validate.setValidate(m_jStatus.getText(),ValidateBuilder.IS_NOT_EMPTY,
                AppLocal.getIntString("message.emptystatus"));        
        return validate.getValid();
    }
    
    
    public String generateVoucherNumber(){
        String result="";

        try {
            result = "VO-";
            String date = m_simpledate.format(new Date());
            result = result + date;
            String lastNumber= (String)dlCustomers.getVoucherNumber().find(result);
            int newNumber = 1 ;

            if (lastNumber!=null){
               newNumber = Integer.parseInt(lastNumber) +1;
           }
            result = result + "-" + getNewNumber(newNumber);
            
            return result;
            
        } catch (BasicException ex) {
        }
        return result;
    }
    
    
    private String getNewNumber(int newNumber){
        String newNo = newNumber + "";
        String zero = "";
        for (int i=0;i< 3 - newNo.length();i++){
            zero = zero + "0";
        }
        return zero+newNo;
    }
}
