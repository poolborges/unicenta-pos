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

package com.openbravo.editor;

import com.openbravo.basic.BasicException;
import java.awt.Color;
import java.awt.Component;
import javax.swing.border.Border;

/**
 *
 * @author JG uniCenta
 */
public abstract class JEditorAbstract extends javax.swing.JPanel implements EditorComponent {

    private EditorKeys editorkeys;
    
    private boolean m_bActive;
    private final Border m_borderactive =  new javax.swing.border.CompoundBorder(new javax.swing.border.LineBorder(javax.swing.UIManager.getDefaults().getColor("TextField.selectionBackground")), new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 4, 1, 4)));
    private final Border m_borderinactive =  new javax.swing.border.CompoundBorder(new javax.swing.border.LineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 4, 1, 4)));
    
    /** Creates new form JPanelNumber */
    public JEditorAbstract() {
        
        initComponents();
        
        editorkeys = null;
        m_bActive = false;
        m_jText.setBorder(m_borderinactive);
    }

    /**
     *
     * @return
     */
    protected abstract int getMode();    

    /**
     *
     * @return
     */
    protected abstract int getAlignment();  

    /**
     *
     * @return
     */
    protected abstract String getEditMode();

    /**
     *
     * @return
     */
    protected abstract String getTextEdit();

    /**
     *
     * @return
     * @throws BasicException
     */
    protected abstract String getTextFormat() throws BasicException;

    /**
     *
     * @param c
     */
    protected abstract void typeCharInternal(char c);    

    /**
     *
     * @param c
     */
    protected abstract void transCharInternal(char c);
    
    /**
     *
     * @param c
     */
    @Override
    public void typeChar(char c) {
        typeCharInternal(c);
        reprintText();
        firePropertyChange("Edition", null, null);
    }
    
    /**
     *
     * @param c
     */
    @Override
    public void transChar(char c) {
        transCharInternal(c);
        reprintText();
        firePropertyChange("Edition", null, null);
    }
    
    /**
     *
     * @param ed
     */
    @Override
    public void addEditorKeys(EditorKeys ed) {
        editorkeys = ed;
    }

    /**
     *
     */
    @Override
    public void deactivate() {
        setActive(false);
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
     */
    public void activate() {
        if (isEnabled()) {
            editorkeys.setActive(this, getMode());        
            setActive(true);
        }
    }
    
    private void setActive(boolean bValue) {
        m_bActive = bValue;
        m_jText.setBorder(m_bActive ? m_borderactive : m_borderinactive);
        reprintText();
    }
            
    /**
     *
     */
    protected void reprintText() {
        
        m_jText.setHorizontalAlignment(getAlignment());
        if (m_bActive) {
            m_jMode.setText(getEditMode());
            m_jText.setText(getTextEdit());
            m_jText.setForeground(javax.swing.UIManager.getDefaults().getColor("label.foreground"));
        } else {
            m_jMode.setText(null);
            try {
                m_jText.setText(getTextFormat());
                m_jText.setForeground(javax.swing.UIManager.getDefaults().getColor("label.foreground"));
            } catch (BasicException e) {
                m_jText.setText(getTextEdit());
                m_jText.setForeground(Color.RED);
            }
        }
    }
    
    @Override
    public void setEnabled(boolean b) {
        
        if (editorkeys != null) {
            editorkeys.setInactive(this);
        }
        panBackground.setBackground(b 
            ? javax.swing.UIManager.getDefaults().getColor("TextField.background")
            : javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));        
        super.setEnabled(b);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panBackground = new javax.swing.JPanel();
        m_jText = new javax.swing.JButton();
        m_jMode = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        panBackground.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.background"));
        panBackground.setLayout(new java.awt.BorderLayout());

        m_jText.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.background"));
        m_jText.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jText.setContentAreaFilled(false);
        m_jText.setFocusPainted(false);
        m_jText.setFocusable(false);
        m_jText.setMinimumSize(new java.awt.Dimension(100, 25));
        m_jText.setPreferredSize(new java.awt.Dimension(100, 25));
        m_jText.setRequestFocusEnabled(false);
        m_jText.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        m_jText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jTextActionPerformed(evt);
            }
        });
        panBackground.add(m_jText, java.awt.BorderLayout.CENTER);

        add(panBackground, java.awt.BorderLayout.CENTER);

        m_jMode.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        m_jMode.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jMode.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        m_jMode.setPreferredSize(new java.awt.Dimension(32, 0));
        add(m_jMode, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jTextActionPerformed

        activate();
        
    }//GEN-LAST:event_m_jTextActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel m_jMode;
    private javax.swing.JButton m_jText;
    private javax.swing.JPanel panBackground;
    // End of variables declaration//GEN-END:variables
 
}
