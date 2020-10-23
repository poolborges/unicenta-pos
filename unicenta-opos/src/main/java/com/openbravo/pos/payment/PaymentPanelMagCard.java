//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta
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

import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.io.File;
import javax.swing.*;

import jpos.JposException;
import jpos.MSR;
import jpos.MSRControl113;
import jpos.events.DataEvent;
import jpos.events.DataListener;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;

public class PaymentPanelMagCard extends javax.swing.JPanel implements PaymentPanel,
        DataListener, ErrorListener {
    
    private JPaymentNotifier m_notifier;
    private MagCardReader m_cardreader;
    private String track1 = null;
    private String track2 = null;
    private String track3 = null;
    private String m_sTransactionID;
    private double m_dTotal;
    // --------------------MagTeck variables----------------------------//
    String track1DataTextField = null;
    String track2DataTextField = null;
    String track3DataTextField = null;
    String additionalSecurityInformationTextField = null;
    String track1DataEncryptedTextField = null;
    String track2DataEncryptedTextField = null;
    String track3DataEncryptedTextField = null;
    //JButton clearButton = new JButton("Clear");
    String infoLine = "";
    String title = "";
    String resultValue = "";
    boolean enabled = false;
    boolean openDevice = false;
    boolean init = true;
    String logicalDeviceName = "MagTekMSR_Encrypted";
    String encryptedCardData = null;
    String encryptionKey = null;
    MSRControl113 msr = null;
    //----------------------------------------------------------------------//
    
    /**
     * Creates new form JMagCardReader
     */
    // public PaymentPanelMagCard(String sReader, JPaymentNotifier notifier) {
    public PaymentPanelMagCard(MagCardReader cardreader, JPaymentNotifier notifier) {
        m_notifier = notifier;
        m_cardreader = cardreader;

        initComponents();
        

        AppConfig config = AppConfig.getInstance();
        config.load();


        if (m_cardreader != null) {
            // They will be able to pay by card
            m_jKeyFactory.addKeyListener(new KeyBarsListener());   
            jReset.setEnabled(true);
        } else {
            jReset.setEnabled(false);
        }
        //---------------------------------------------------//

        msr = new MSR();
        msr.addDataListener(this);
        msr.addErrorListener(this);
    }
    
    private void processMSRSession() {


        if ((this.logicalDeviceName != null) && (!"".equals(this.logicalDeviceName))) {
            if (this.openDevice) {
                try {
                    msr.close();
                } catch (JposException localJposException1) {
                    JOptionPane.showMessageDialog(this, "Error Occured " + localJposException1.getMessage());
                }
            }
            try {
                msr.open(this.logicalDeviceName);
                this.openDevice = true;
            } catch (JposException localJposException2) {
                JOptionPane.showMessageDialog(this, "Error Occured " + localJposException2.getMessage());
            }
            //Clear the field data.
            clear();

            try {
                this.msr.claim(1000);
            } catch (JposException localJposException3) {
                JOptionPane.showMessageDialog(this, "Error Occured " + localJposException3.getMessage());
            }
            try {
                this.msr.setDataEventEnabled(true);
            } catch (JposException localJposException5) {
                JOptionPane.showMessageDialog(this, "Error Occured " + localJposException5.getMessage());
            }
            try {
                this.msr.setDeviceEnabled(true);
                this.enabled = true;
            } catch (JposException localJposException4) {
                this.enabled = false;
                JOptionPane.showMessageDialog(this, "Error Occured " + localJposException4.getMessage());
            }
        }
    }

    public void clear() {
        this.track1DataTextField = "";
        this.track2DataTextField = "";
        this.track3DataTextField = "";
        this.additionalSecurityInformationTextField = "";
        this.track1DataEncryptedTextField = "";
        this.track2DataEncryptedTextField = "";
        this.track3DataEncryptedTextField = "";
    }

    @Override
    public void dataOccurred(DataEvent paramDataEvent) {
        String str = "";
        byte[] arrayOfByte = new byte[0];
        try {
            arrayOfByte = this.msr.getTrack1Data();
            str = new String(arrayOfByte);
            if (str.length() == 0) {
                str = "";
            }
            this.track1DataTextField = str;

            arrayOfByte = this.msr.getTrack2Data();
            str = new String(arrayOfByte);
            if (str.length() == 0) {
                str = "";
            }
            this.track2DataTextField = str;

            arrayOfByte = this.msr.getTrack3Data();
            str = new String(arrayOfByte);
            if (str.length() == 0) {
                str = "";
            }
            this.track3DataTextField = str;

            arrayOfByte = this.msr.getAdditionalSecurityInformation();
            str = new String(arrayOfByte);
            if (str.length() == 0) {
                str = "";
            }
            this.additionalSecurityInformationTextField = str;

            arrayOfByte = this.msr.getTrack1EncryptedData();
            str = new String(arrayOfByte);
            if (str.length() == 0) {
                str = "";
            }
            this.track1DataEncryptedTextField = str;

            arrayOfByte = this.msr.getTrack2EncryptedData();
            str = new String(arrayOfByte);
            if (str.length() == 0) {
                str = "";
            }
            this.track2DataEncryptedTextField = str;

            arrayOfByte = this.msr.getTrack3EncryptedData();
            str = new String(arrayOfByte);
            if (str.length() == 0) {
                str = "";
            }
            this.track3DataEncryptedTextField = str;
            this.msr.setDataEventEnabled(true);

            encryptedCardData = track2DataEncryptedTextField;
            encryptionKey = additionalSecurityInformationTextField;

            char[] cData = ("%" + track1DataTextField + "?;" + track2DataTextField + "?").toCharArray();

            if (cData.length > 0) {
                for (int i = 0; i < cData.length; i++) {
                    stateTransition(cData[i]);
                }
            }
        } catch (JposException localJposException) {
            processJposException(localJposException, "");
        }
    }

    @Override
    public void errorOccurred(ErrorEvent paramErrorEvent) {
        String str1 = Integer.toString(paramErrorEvent.getErrorCode());
        String str2 = Integer.toString(paramErrorEvent.getErrorCodeExtended());
        String str3 = Integer.toString(paramErrorEvent.getErrorLocus());
        String str4 = Integer.toString(paramErrorEvent.getErrorResponse());

        JOptionPane.showMessageDialog(this, "ErrorEvent: EC=" + str1 + ", ECE=" + str2 + ", EL=" + str3 + ", ER=" + str4, this.title, 0);
    }

    private void processJposException(JposException paramJposException, String paramString) {
        JOptionPane.showMessageDialog(this, "Exception...message = " + paramJposException.getMessage() + " with errorCode = " + paramJposException.getErrorCode() + ", errorCodeExtended = " + paramJposException.getErrorCodeExtended(), this.title, 0);

    }

    class XmlDialog extends JDialog {

        JScrollPane jScrollPane = new JScrollPane();
        JTextArea jTextArea = new JTextArea();
        GridBagLayout gridBagLayout1 = new GridBagLayout();

        public XmlDialog(String arg2) throws HeadlessException {
            try {
                jaInit();
                String str = "";
                this.jTextArea.append(str);
            } catch (Exception localException) {
                localException.printStackTrace();
            }

        }

        private void jaInit() throws Exception {
            getContentPane().setLayout(this.gridBagLayout1);

            this.jTextArea.setColumns(16);
            this.jTextArea.setRows(18);

            getContentPane().add(this.jScrollPane, new GridBagConstraints(0, 0, 2, 1, 1.0D, 1.0D, 10, 1, new Insets(15, 17, 0, 15), 364, 213));

            this.jScrollPane.getViewport().add(this.jTextArea, null);
        }
    }
    //--------------------------------------------------------------//

    @Override
    public JComponent getComponent() {
        return this;
    }
    
    @Override
    public void activate(String sTransaction, double dTotal) {
        m_sTransactionID = sTransaction;
        m_dTotal = dTotal;
        resetState();
        m_jKeyFactory.setText(null);       
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_jKeyFactory.requestFocus();
            }
        });
    }
    
    private void resetState() {
        
        m_notifier.setStatus(false, false);  
              
        m_jHolderName.setText(null);
        m_jCardNumber.setText(null);
        m_jExpirationDate.setText(null);
        track1 = null;
        track2 = null;
        track3 = null;
        
        if (m_cardreader != null) {
            // Se van a poder efectuar pagos con tarjeta
            m_cardreader.reset();
        }
        //clear();
    }
    
    @Override
    public PaymentInfoMagcard getPaymentInfoMagcard() {

        if (m_dTotal > 0.0) {
            return new PaymentInfoMagcard(
                    m_jHolderName.getText(),
                    m_jCardNumber.getText(), 
                    m_jExpirationDate.getText(),
                    track1,
                    track2,
                    track3,
                    encryptedCardData,
                    encryptionKey,
                    m_sTransactionID,
                    m_dTotal);
        } else {
            return new PaymentInfoMagcardRefund(
                    m_jHolderName.getText(),
                    m_jCardNumber.getText(), 
                    m_jExpirationDate.getText(),
                    track1,
                    track2,
                    track3,
                    encryptedCardData,
                    encryptionKey,
                    m_sTransactionID,
                    m_dTotal);
        }
    } 
    
    private void stateTransition(char cTrans) {
        
        m_cardreader.appendChar(cTrans);
        
        if (m_cardreader.isComplete()) {
            m_jHolderName.setText(m_cardreader.getHolderName());
            m_jCardNumber.setText(m_cardreader.getCardNumber());
            m_jExpirationDate.setText(m_cardreader.getExpirationDate()); 
            track1 = m_cardreader.getTrack1();
            track2 = m_cardreader.getTrack2();
            track3 = m_cardreader.getTrack3();
            m_notifier.setStatus(true, true);  

            m_jKeyFactory.addKeyListener(new KeyBarsListener());
            jReset.setEnabled(true);

        } else {
            m_jHolderName.setText(null);
            m_jCardNumber.setText(null);
            m_jExpirationDate.setText(null); 
            track1 = null;
            track3 = null;
            track3 = null;
            m_notifier.setStatus(false, false);  
        }      
    }    
    
    private class KeyBarsListener extends java.awt.event.KeyAdapter {
        
        @Override
        public void keyTyped(java.awt.event.KeyEvent e) {
            m_jKeyFactory.setText(null);
            stateTransition(e.getKeyChar());
        }
    }   
    
    /**
     *
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jReset = new javax.swing.JButton();
        m_jKeyFactory = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        m_jExpirationDate = new javax.swing.JLabel();
        m_jCardNumber = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        m_jHolderName = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(AppLocal.getIntString("message.paymentgatewayswipe")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(263, 30));
        jPanel2.add(jLabel1);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        jReset.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        jReset.setText(AppLocal.getIntString("button.reset")); // NOI18N
        jReset.setFocusPainted(false);
        jReset.setFocusable(false);
        jReset.setPreferredSize(new java.awt.Dimension(100, 45));
        jReset.setRequestFocusEnabled(false);
        jReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jResetActionPerformed(evt);
            }
        });

        m_jKeyFactory.setPreferredSize(new java.awt.Dimension(1, 1));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.cardnumber")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(150, 30));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.cardexpdate")); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(150, 30));

        m_jExpirationDate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jExpirationDate.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jExpirationDate.setOpaque(true);
        m_jExpirationDate.setPreferredSize(new java.awt.Dimension(180, 30));

        m_jCardNumber.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCardNumber.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jCardNumber.setOpaque(true);
        m_jCardNumber.setPreferredSize(new java.awt.Dimension(200, 30));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.cardholder")); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(150, 30));

        m_jHolderName.setBackground(java.awt.Color.white);
        m_jHolderName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jHolderName.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jHolderName.setOpaque(true);
        m_jHolderName.setPreferredSize(new java.awt.Dimension(200, 30));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(m_jKeyFactory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(m_jExpirationDate, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(165, 165, 165))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(m_jHolderName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jCardNumber, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jKeyFactory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jHolderName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(m_jCardNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jExpirationDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(17, 17, 17))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jResetActionPerformed

        resetState();
        
    }//GEN-LAST:event_jResetActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jReset;
    private javax.swing.JLabel m_jCardNumber;
    private javax.swing.JLabel m_jExpirationDate;
    private javax.swing.JLabel m_jHolderName;
    private javax.swing.JTextArea m_jKeyFactory;
    // End of variables declaration//GEN-END:variables
}
