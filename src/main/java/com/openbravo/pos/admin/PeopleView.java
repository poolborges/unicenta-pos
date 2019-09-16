//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta
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

package com.openbravo.pos.admin;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.util.Hashcypher;
import com.openbravo.pos.util.StringUtils;
import com.openbravo.pos.util.uOWWatch;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.util.UUID;
import javax.swing.*;

/**
 *
 * @author adrianromero
 */
public class PeopleView extends JPanel implements EditorRecord {

    private Object m_oId;
    private String m_sPassword;
    
    private final DirtyManager m_Dirty;
    
    private final SentenceList m_sentrole;
    private ComboBoxValModel m_RoleModel;  
    
    private final ComboBoxValModel m_ReasonModel;        
    
    /** Creates new form PeopleEditor
     * @param dlAdmin
     * @param dirty */
    public PeopleView(DataLogicAdmin dlAdmin, DirtyManager dirty) {

        initComponents();
                
        // El modelo de roles
        m_sentrole = dlAdmin.getRolesList();
        m_RoleModel = new ComboBoxValModel();
        
        m_Dirty = dirty;
        m_jName.getDocument().addDocumentListener(dirty);
        m_jRole.addActionListener(dirty);
        m_jVisible.addActionListener(dirty);
        m_jImage.addPropertyChangeListener("image", dirty);
        m_jcard.getDocument().addDocumentListener(dirty);

        m_ReasonModel = new ComboBoxValModel();
        m_ReasonModel.add(AppLocal.getIntString("cboption.generate"));
        m_ReasonModel.add(AppLocal.getIntString("cboption.clear"));              
        m_ReasonModel.add(AppLocal.getIntString("cboption.iButton"));                      
        
        webCBSecurity.setModel(m_ReasonModel);
        
        writeValueEOF();
    }

    /**
     *
     */
    @Override
    public void writeValueEOF() {
        m_oId = null;
        m_jName.setText(null);
        m_sPassword = null;
        m_RoleModel.setSelectedKey(null);
        m_jVisible.setSelected(false);
        m_jcard.setText(null);
        m_jImage.setImage(null);
        m_jName.setEnabled(false);
        m_jRole.setEnabled(false);
        m_jVisible.setEnabled(false);
        m_jcard.setEnabled(false);
        m_jImage.setEnabled(false);
        jButton1.setEnabled(false);
        webCBSecurity.setEnabled(false);
    }
    
    /**
     *
     */
    @Override
    public void writeValueInsert() {
        m_oId = null;
        m_jName.setText(null);
        m_sPassword = null;
        m_RoleModel.setSelectedKey(null);
        m_jVisible.setSelected(true);
        m_jcard.setText(null);
        m_jImage.setImage(null);
        m_jName.setEnabled(true);
        m_jRole.setEnabled(true);
        m_jVisible.setEnabled(true);
        m_jcard.setEnabled(true);
        m_jImage.setEnabled(true);
        jButton1.setEnabled(true);
        webCBSecurity.setEnabled(true);        
    }
    
    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        Object[] people = (Object[]) value;
        m_oId = people[0];
        m_jName.setText(Formats.STRING.formatValue(people[1]));
        m_sPassword = Formats.STRING.formatValue(people[2]);
        m_RoleModel.setSelectedKey(people[3]);
        m_jVisible.setSelected(((Boolean) people[4]).booleanValue());
        m_jcard.setText(Formats.STRING.formatValue(people[5]));
        m_jImage.setImage((BufferedImage) people[6]);
        
        m_jName.setEnabled(false);
        m_jRole.setEnabled(false);
        m_jVisible.setEnabled(false);
        m_jcard.setEnabled(false);
        m_jImage.setEnabled(false);        
        jButton1.setEnabled(false);
        webCBSecurity.setEnabled(false);        
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        Object[] people = (Object[]) value;
        m_oId = people[0];
        m_jName.setText(Formats.STRING.formatValue(people[1]));
        m_sPassword = Formats.STRING.formatValue(people[2]);
        m_RoleModel.setSelectedKey(people[3]);
        m_jVisible.setSelected(((Boolean) people[4]).booleanValue());
        m_jcard.setText(Formats.STRING.formatValue(people[5]));
        m_jImage.setImage((BufferedImage) people[6]);
        
        if (m_jcard.getText().length() == 16) {
            jLblCardID.setText(AppLocal.getIntString("label.ibutton"));
        } else {
            jLblCardID.setText(AppLocal.getIntString("label.card"));            
        }
        System.out.println(m_jcard.getText().length());        
        
        m_jName.setEnabled(true);
        m_jRole.setEnabled(true);
        m_jVisible.setEnabled(true);
        m_jcard.setEnabled(true);
        m_jImage.setEnabled(true);
        jButton1.setEnabled(true);
        webCBSecurity.setEnabled(true);        
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        Object[] people = new Object[7];
        people[0] = m_oId == null ? UUID.randomUUID().toString() : m_oId;
        people[1] = Formats.STRING.parseValue(m_jName.getText());
        people[2] = Formats.STRING.parseValue(m_sPassword);
        people[3] = m_RoleModel.getSelectedKey();
        people[4] = m_jVisible.isSelected();
        people[5] = Formats.STRING.parseValue(m_jcard.getText());
        people[6] = m_jImage.getImage();
        return people;
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
     * @throws BasicException
     */
    public void activate() throws BasicException {
        
        m_RoleModel = new ComboBoxValModel(m_sentrole.list());
        m_jRole.setModel(m_RoleModel);
    }
    
   
    /**
     *
     */
    @Override
    public void refresh() {
    }
     
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        m_jVisible = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        m_jImage = new com.openbravo.data.gui.JImageEditor();
        jButton1 = new javax.swing.JButton();
        m_jRole = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        m_jcard = new javax.swing.JTextField();
        jLblCardID = new javax.swing.JLabel();
        webCBSecurity = new com.alee.laf.combobox.WebComboBox();
        jLabel6 = new javax.swing.JLabel();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setPreferredSize(new java.awt.Dimension(500, 500));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/info.png"))); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.peoplenamem")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(110, 30));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        m_jName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jName.setPreferredSize(new java.awt.Dimension(0, 30));

        m_jVisible.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jVisible.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.peoplevisible")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.peopleimage")); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jImage.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jImage.setPreferredSize(new java.awt.Dimension(300, 250));

        jButton1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/password.png"))); // NOI18N
        jButton1.setText(AppLocal.getIntString("button.peoplepassword")); // NOI18N
        jButton1.setToolTipText("");
        jButton1.setPreferredSize(new java.awt.Dimension(80, 45));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        m_jRole.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jRole.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.rolem")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jcard.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jcard.setPreferredSize(new java.awt.Dimension(0, 30));

        jLblCardID.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblCardID.setText(AppLocal.getIntString("label.card")); // NOI18N
        jLblCardID.setPreferredSize(new java.awt.Dimension(110, 30));

        webCBSecurity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Generate new key", "Delete exisitng key", "Use iButton ID" }));
        webCBSecurity.setSelectedIndex(0);
        webCBSecurity.setToolTipText(AppLocal.getIntString("tooltip.peoplesecurity")); // NOI18N
        webCBSecurity.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        webCBSecurity.setPreferredSize(new java.awt.Dimension(140, 45));
        webCBSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                webCBSecurityActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.Password")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(110, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(m_jVisible, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(m_jRole, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(m_jImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLblCardID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(m_jcard, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(webCBSecurity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLblCardID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jcard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(webCBSecurity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jVisible, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String sNewPassword = Hashcypher.changePassword(this);
        if (sNewPassword != null) {
            m_sPassword = sNewPassword;
            m_Dirty.setDirty(true);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void webCBSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webCBSecurityActionPerformed


        if(webCBSecurity.getSelectedIndex() == 0){
            if (JOptionPane.showConfirmDialog(this, 
                AppLocal.getIntString("message.cardnew"), 
                AppLocal.getIntString("title.editor"), 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) 
            {  
                m_jcard.setText("C" + StringUtils.getCardNumber());
                m_Dirty.setDirty(true);
            }        
        }         

        if(webCBSecurity.getSelectedIndex() == 1){
            if (JOptionPane.showConfirmDialog(this, 
                AppLocal.getIntString("message.cardremove"), 
                AppLocal.getIntString("title.editor"), 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) 
            {  
                m_jcard.setText(null);
                m_Dirty.setDirty(true);
            }
        }
        
        if(webCBSecurity.getSelectedIndex() == 2){
            if (JOptionPane.showConfirmDialog(this, 
                AppLocal.getIntString("message.ibuttonassign"), 
                AppLocal.getIntString("title.editor"), 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.INFORMATION_MESSAGE) == JOptionPane.OK_OPTION) 

            {  
                m_jcard.setText( uOWWatch.getibuttonid() );
                jLblCardID.setText(AppLocal.getIntString("label.ibutton"));
                m_Dirty.setDirty(true);
            }

        }                
               
    }//GEN-LAST:event_webCBSecurityActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        if (evt.getClickCount() == 2) {
            String uuidString = m_oId.toString();
            StringSelection stringSelection = new StringSelection(uuidString);
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
        
            JOptionPane.showMessageDialog(null, 
                AppLocal.getIntString("message.uuidcopy"));
        }
    }//GEN-LAST:event_jLabel1MouseClicked
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLblCardID;
    private com.openbravo.data.gui.JImageEditor m_jImage;
    private javax.swing.JTextField m_jName;
    private javax.swing.JComboBox m_jRole;
    private javax.swing.JCheckBox m_jVisible;
    private javax.swing.JTextField m_jcard;
    private com.alee.laf.combobox.WebComboBox webCBSecurity;
    // End of variables declaration//GEN-END:variables
    
}
