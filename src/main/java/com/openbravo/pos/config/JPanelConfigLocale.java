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

package com.openbravo.pos.config;

import com.openbravo.data.user.DirtyManager;
import java.awt.Component;
import java.util.Locale;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author  adrianromero
 */
public class JPanelConfigLocale extends javax.swing.JPanel implements PanelConfig {
    
    private final DirtyManager dirty = new DirtyManager();
    
    private final static String DEFAULT_VALUE = "(Default)";

    /** Creates new form JPanelConfigLocale */
    public JPanelConfigLocale() {
        
        initComponents();
        
        jcboLocale.addActionListener(dirty);
        jcboInteger.addActionListener(dirty);
        jcboDouble.addActionListener(dirty);
        jcboCurrency.addActionListener(dirty);
        jcboPercent.addActionListener(dirty);
        jcboDate.addActionListener(dirty);
        jcboTime.addActionListener(dirty);
        jcboDatetime.addActionListener(dirty);
        
        List<Locale> availablelocales = new ArrayList<>();
        availablelocales.addAll(Arrays.asList(Locale.getAvailableLocales())); // Available java locales
//        addLocale(availablelocales, new Locale("en", "GB", "")); // English GB
//        addLocale(availablelocales, new Locale("en", "US", "")); // English USA
        
        Collections.sort(availablelocales, new LocaleComparator());
        
        jcboLocale.addItem(new LocaleInfo (null));
        for (Locale l : availablelocales) {
            jcboLocale.addItem(new LocaleInfo(l));
        }
        
        jcboInteger.addItem(DEFAULT_VALUE);
        jcboInteger.addItem("#0");
        jcboInteger.addItem("#,##0");
        
        jcboDouble.addItem(DEFAULT_VALUE);
        jcboDouble.addItem("#0.0");
        jcboDouble.addItem("#,##0.#");
        
        jcboCurrency.addItem(DEFAULT_VALUE);
        jcboCurrency.addItem("\u00A4 #0.00");
        jcboCurrency.addItem("'$' #,##0.00");
        
        jcboPercent.addItem(DEFAULT_VALUE);
        jcboPercent.addItem("#,##0.##%");
        
        jcboDate.addItem(DEFAULT_VALUE);
        
        jcboTime.addItem(DEFAULT_VALUE);
        
        jcboDatetime.addItem(DEFAULT_VALUE);
               
    }

    private void addLocale(List<Locale> ll, Locale l) {
        if (!ll.contains(l)) {
            ll.add(l);
        }
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean hasChanged() {
        return dirty.isDirty();
    }
    
    /**
     *
     * @return
     */
    @Override
    public Component getConfigComponent() {
        return this;
    }
   
    /**
     *
     * @param config
     */
    @Override
    public void loadProperties(AppConfig config) {
        
        String slang = config.getProperty("user.language");
        String scountry = config.getProperty("user.country");
        String svariant = config.getProperty("user.variant");
        
        if (slang != null && !slang.equals("") && scountry != null && svariant != null) {                    
            Locale currentlocale = new Locale(slang, scountry, svariant);
            for (int i = 0 ; i < jcboLocale.getItemCount(); i++)  {
                LocaleInfo l = (LocaleInfo) jcboLocale.getItemAt(i);
                if (currentlocale.equals(l.getLocale())) {
                    jcboLocale.setSelectedIndex(i);
                    break;
                }
            }        
        } else {
            jcboLocale.setSelectedIndex(0);
        }
        
        jcboInteger.setSelectedItem(writeWithDefault(config.getProperty("format.integer")));
        jcboDouble.setSelectedItem(writeWithDefault(config.getProperty("format.double")));
        jcboCurrency.setSelectedItem(writeWithDefault(config.getProperty("format.currency")));
        jcboPercent.setSelectedItem(writeWithDefault(config.getProperty("format.percent")));
        jcboDate.setSelectedItem(writeWithDefault(config.getProperty("format.date")));
        jcboTime.setSelectedItem(writeWithDefault(config.getProperty("format.time")));
        jcboDatetime.setSelectedItem(writeWithDefault(config.getProperty("format.datetime")));
               
        dirty.setDirty(false);
    }
    
    /**
     *
     * @param config
     */
    @Override
    public void saveProperties(AppConfig config) {
        
        Locale l = ((LocaleInfo) jcboLocale.getSelectedItem()).getLocale();
        if (l == null) {
            config.setProperty("user.language", "");
            config.setProperty("user.country", "");
            config.setProperty("user.variant", "");
        } else {
            config.setProperty("user.language", l.getLanguage());
            config.setProperty("user.country", l.getCountry());
            config.setProperty("user.variant", l.getVariant());
        }
         
        config.setProperty("format.integer", readWithDefault(jcboInteger.getSelectedItem()));
        config.setProperty("format.double", readWithDefault(jcboDouble.getSelectedItem()));
        config.setProperty("format.currency", readWithDefault(jcboCurrency.getSelectedItem()));
        config.setProperty("format.percent", readWithDefault(jcboPercent.getSelectedItem()));
        config.setProperty("format.date", readWithDefault(jcboDate.getSelectedItem()));
        config.setProperty("format.time", readWithDefault(jcboTime.getSelectedItem()));
        config.setProperty("format.datetime", readWithDefault(jcboDatetime.getSelectedItem()));
        
        dirty.setDirty(false);
    }
    
    private String readWithDefault(Object value) {
        if (DEFAULT_VALUE.equals(value)) {
            return "";
        } else {
            return value.toString();
        }
    }
    
    private Object writeWithDefault(String value) {
        if (value == null || value.equals("") || value.equals(DEFAULT_VALUE)) {
            return DEFAULT_VALUE;
        } else {
            return value;
        }
    }
    
    private static class LocaleInfo {
        private final Locale locale;
        
        public LocaleInfo(Locale locale) {
            this.locale = locale;
        }
        public Locale getLocale() {
            return locale;
        }
        @Override
        public String toString() {
            return locale == null 
                    ? "(System default)" 
                    : locale.getDisplayName();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jcboLocale = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jcboInteger = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jcboDouble = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jcboCurrency = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jcboPercent = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jcboDate = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jcboTime = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jcboDatetime = new javax.swing.JComboBox();

        setMinimumSize(new java.awt.Dimension(0, 0));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(650, 450));

        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 400));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.locale")); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(150, 30));

        jcboLocale.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboLocale.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.integer")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(150, 30));

        jcboInteger.setEditable(true);
        jcboInteger.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboInteger.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.double")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(150, 30));

        jcboDouble.setEditable(true);
        jcboDouble.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboDouble.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.currency")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(150, 30));

        jcboCurrency.setEditable(true);
        jcboCurrency.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboCurrency.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.percent")); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(150, 30));

        jcboPercent.setEditable(true);
        jcboPercent.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboPercent.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.date")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(150, 30));

        jcboDate.setEditable(true);
        jcboDate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboDate.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.time")); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(150, 30));

        jcboTime.setEditable(true);
        jcboTime.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboTime.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.datetime")); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(150, 30));

        jcboDatetime.setEditable(true);
        jcboDatetime.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboDatetime.setPreferredSize(new java.awt.Dimension(0, 30));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcboDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcboTime, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcboDate, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcboPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcboDouble, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcboInteger, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcboLocale, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(51, 51, 51))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboLocale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboInteger, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboDouble, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(100, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(200, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(148, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox jcboCurrency;
    private javax.swing.JComboBox jcboDate;
    private javax.swing.JComboBox jcboDatetime;
    private javax.swing.JComboBox jcboDouble;
    private javax.swing.JComboBox jcboInteger;
    private javax.swing.JComboBox jcboLocale;
    private javax.swing.JComboBox jcboPercent;
    private javax.swing.JComboBox jcboTime;
    // End of variables declaration//GEN-END:variables
    
}
