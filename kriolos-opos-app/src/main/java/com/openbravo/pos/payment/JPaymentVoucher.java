//    KriolOS POS
//    Copyright (c) 2019-2023 KriolOS.
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

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.format.Formats;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.customers.DataLogicCustomers;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.util.RoundUtils;
import com.openbravo.pos.voucher.VoucherInfo;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 *
 * @author JG uniCenta
 */
public class JPaymentVoucher extends javax.swing.JPanel implements JPaymentInterface {

    private final JPaymentNotifier m_notifier;

    private DataLogicSales dlSales;
    private DataLogicCustomers dlCustomers;
    private ComboBoxValModel m_VoucherModel;
    private SentenceList<VoucherInfo> m_sentvouch;

    private double voucherAmount;
    private double m_dTotalToPay;

    private final String m_sVoucherType; // "voucherin", "voucherout"
    private String m_sVoucherNumber;

    /**
     * Creates new form JPaymentTicket
     *
     * @param app
     * @param notifier
     * @param sVoucher should be "voucherin", "voucherout"
     */
    public JPaymentVoucher(AppView app, JPaymentNotifier notifier, String sVoucher) {

        m_notifier = notifier;
        m_sVoucherType = sVoucher;
        voucherAmount = 0.0;
        m_dTotalToPay = 0.0;

        init(app);

        m_jTendered.addPropertyChangeListener("Edition", new RecalculateState());
    }

    private void init(AppView app) {

        try {
            dlSales = (DataLogicSales) app.getBean("com.openbravo.pos.forms.DataLogicSales");
            dlCustomers = (DataLogicCustomers) app.getBean("com.openbravo.pos.customers.DataLogicCustomers");
            m_sentvouch = dlSales.getVoucherList();

            initComponents();

            m_VoucherModel = new ComboBoxValModel();
            List<VoucherInfo> a = m_sentvouch.list();

            m_VoucherModel = new ComboBoxValModel(a);
            m_jVoucher.setModel(m_VoucherModel);

            webLblcustomerName.setText(null);

        } catch (BasicException ex) {
        }
    }

    /**
     *
     * @param customerext
     * @param dTotal
     * @param transID
     */
    @Override
    public void activate(CustomerInfoExt customerext, double dTotal, String transID) {

        m_dTotalToPay = dTotal;
        m_jTendered.reset();
        m_jKeys.setEnabled(false);
        m_jTendered.setEnabled(false);

        setStates(null);
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
     */
    @Override
    public PaymentInfo executePayment() {
        return new PaymentInfoTicket(voucherAmount, m_sVoucherType, m_sVoucherNumber);

    }

    private class RecalculateState implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            int iCompare = RoundUtils.compare(voucherAmount, m_dTotalToPay);
            m_notifier.setStatus(voucherAmount > 0.0, iCompare >= 0);
        }
    }

    private void setStates(VoucherInfo m_voucherInfo) {

        cleanStates();
        if (m_voucherInfo != null) {
            if ("A".equals(m_voucherInfo.getStatus())) {

                voucherAmount = m_voucherInfo.getAmount();
                m_jTendered.setDoubleValue(voucherAmount);
                m_jMoneyEuros.setText(Formats.CURRENCY.formatValue(voucherAmount));
                webLblcustomerName.setText(m_voucherInfo.getCustomerName());
                m_sVoucherNumber = m_voucherInfo.getVoucherNumber();
                voucherStatus.setText("Available");
            } else if ("D".equals(m_voucherInfo.getStatus())) {
                voucherStatus.setText("Redeemed");
            }
        }
    }

    private void cleanStates() {
        m_sVoucherNumber = null;
        voucherAmount = 0.0;
        m_jMoneyEuros.setText(null);
        m_jTendered.setDoubleValue(null);
        webLblcustomerName.setText(null);
        voucherStatus.setText(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        m_jVoucher = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        m_jMoneyEuros = new javax.swing.JLabel();
        webLblCustomer = new javax.swing.JLabel();
        webLblcustomerName = new javax.swing.JLabel();
        voucherStatus = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        m_jKeys = new com.openbravo.editor.JEditorKeys();
        jPanel1 = new javax.swing.JPanel();
        m_jTendered = new com.openbravo.editor.JEditorCurrencyPositive();

        setLayout(new java.awt.BorderLayout());

        jLabel5.setFont(jLabel5.getFont());
        jLabel5.setLabelFor(m_jVoucher);
        jLabel5.setText(AppLocal.getIntString("label.voucher")); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(100, 30));

        m_jVoucher.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jVoucher.setPreferredSize(new java.awt.Dimension(180, 30));
        m_jVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jVoucherActionPerformed(evt);
            }
        });

        jLabel1.setFont(jLabel1.getFont());
        jLabel1.setText(AppLocal.getIntString("label.voucherValue")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 30));

        m_jMoneyEuros.setBackground(new java.awt.Color(204, 255, 51));
        m_jMoneyEuros.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        m_jMoneyEuros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jMoneyEuros.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jMoneyEuros.setOpaque(true);
        m_jMoneyEuros.setPreferredSize(new java.awt.Dimension(180, 30));

        webLblCustomer.setText(AppLocal.getIntString("label.customer")); // NOI18N
        webLblCustomer.setToolTipText(AppLocal.getIntString("label.customer")); // NOI18N
        webLblCustomer.setFont(webLblCustomer.getFont());
        webLblCustomer.setPreferredSize(new java.awt.Dimension(100, 30));

        webLblcustomerName.setText(AppLocal.getIntString("label.customer")); // NOI18N
        webLblcustomerName.setToolTipText("");
        webLblcustomerName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        webLblcustomerName.setPreferredSize(new java.awt.Dimension(100, 30));

        voucherStatus.setText(AppLocal.getIntString("label.voucherStatus")); // NOI18N
        voucherStatus.setToolTipText("");
        voucherStatus.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        voucherStatus.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabel6.setFont(jLabel6.getFont());
        jLabel6.setLabelFor(m_jVoucher);
        jLabel6.setText(AppLocal.getIntString("label.voucherStatus")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 30));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(webLblCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(webLblcustomerName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_jMoneyEuros, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_jVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 2, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(voucherStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(voucherStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jMoneyEuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(webLblCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(webLblcustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(245, 245, 245))
        );

        add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel11.setLayout(new java.awt.BorderLayout());

        jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.Y_AXIS));
        jPanel12.add(m_jKeys);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel1.setLayout(new java.awt.BorderLayout());

        m_jTendered.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTendered.setPreferredSize(new java.awt.Dimension(130, 30));
        jPanel1.add(m_jTendered, java.awt.BorderLayout.CENTER);

        jPanel12.add(jPanel1);

        jPanel11.add(jPanel12, java.awt.BorderLayout.NORTH);

        add(jPanel11, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jVoucherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jVoucherActionPerformed

        VoucherInfo m_voucherInfo = null;
        if (m_VoucherModel.getSelectedKey() != null) {
            try {
                String id = m_VoucherModel.getSelectedKey().toString();
                m_voucherInfo = dlCustomers.getVoucherInfo(id);
            } catch (BasicException ex) {

            }
        }

        setStates(m_voucherInfo);
    }//GEN-LAST:event_m_jVoucherActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel4;
    private com.openbravo.editor.JEditorKeys m_jKeys;
    private javax.swing.JLabel m_jMoneyEuros;
    private com.openbravo.editor.JEditorCurrencyPositive m_jTendered;
    private javax.swing.JComboBox m_jVoucher;
    private javax.swing.JLabel voucherStatus;
    private javax.swing.JLabel webLblCustomer;
    private javax.swing.JLabel webLblcustomerName;
    // End of variables declaration//GEN-END:variables

}
