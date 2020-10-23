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


package com.openbravo.pos.imports;

import com.csvreader.CsvReader;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.Session;
import com.openbravo.pos.forms.*;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.inventory.ProductStock;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SerializerWriteBasicExt;
import java.util.List;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.UUID;


/**
 * User Interface and code for CSV type data import to update Products
 * Current Stock quantity levels in table: stockcurrent
 */
public class StockQtyImport extends JPanel implements JPanelView {
// the workspace
    private CsvReader products;
    private DocumentListener documentListener;
    
// the db connection session
    private Session s;
    private Connection con;
    private DataLogicSales m_dlSales;
    private DataLogicSystem m_dlSystem;  
    private ProductInfoExt prodInfo;
    private ProductStock prodStock;

// Location   
    private String Location = "0";
    private String m_sInventoryLocation;
    
// Product properties
    private String productBarcode;
    private Double productQty;
    private double oldQty = 0;
    private double newQty = 0;    
    private String recordType = null;
    
// the csv filename    
    private String last_folder;
    private File config_file;
    private String csvFileName;

//Status area messages
    private Integer progress = 0;        
    private int currentRecord;
    private int rowCount = 0;
    private int qtyUpdates = 0;

    /**
     * Constructs a new StockQtyImport object
     *
     * @param oApp AppView
     */
    public StockQtyImport(AppView oApp) {
        this(oApp.getProperties());
    }

    /**
     * Constructs a new StockQtyImport object
     *
     * @param props AppProperties
     */
    @SuppressWarnings("empty-statement")
    public StockQtyImport(AppProperties props) {

        initComponents();

// Get current db session connection        
        AppProperties m_props = props;        

        try {
            s = AppViewConnection.createSession(props);
            con = s.getConnection();
        } catch (BasicException | SQLException e) {;
        }

// Set db tables        
        m_dlSales = new DataLogicSales();
        m_dlSales.init(s);
        m_dlSystem = new DataLogicSystem();
        m_dlSystem.init(s);

// Get terminal's current resource property settings
        Properties m_propsdb = m_dlSystem.getResourceAsProperties(m_props.getHost() + "/properties");

// Get terminal's set Location property <entry key="location">0</entry>
        m_sInventoryLocation = m_propsdb.getProperty("location");
        try {
            Location = m_dlSystem.findLocationName(m_sInventoryLocation);
        } catch (BasicException ex) {
            Logger.getLogger(StockQtyImport.class.getName()).log(Level.SEVERE, null, ex);
        }

// last used folder stored in unicentaopos.properties
        last_folder = props.getProperty("CSV.last_folder");
        config_file = props.getConfigFile();

        jFileName.getDocument().addDocumentListener(documentListener);
        documentListener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                jFileRead.setEnabled(true);
            }
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                if (!"".equals(jFileName.getText().trim())) {
                    jFileRead.setEnabled(true);
                }
            }
            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                if (jFileName.getText().trim().equals("")) {
                    jFileRead.setEnabled(false);
                }
            }
        };
    }

    /**
     * Enables form components
     */
    private void enableForm() {
        jFileRead.setEnabled(true);
        jImport.setEnabled(true);
        jbtnReset.setEnabled(true);        
        m_jLocation.setEnabled(true);
    }
       
    /**
     * This forms Title
     * @return The name of the panel
    */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.CSVImport");
    }

    /**
     * Returns this object
     * @return 
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     * Get this form object ready
     * @throws com.openbravo.basic.BasicException
     */
    @Override
    public void activate() throws BasicException {
        // Current Location
        m_jLocation.setText("Current Location : " + Location);

        // Set the column delimiter
        jComboSeparator.removeAllItems();
        jComboSeparator.addItem(",");
        jComboSeparator.addItem(";");
        jComboSeparator.addItem("~");
        jComboSeparator.addItem("^");
    }

    /**
     * Resets all the form fields
     */
    public void resetForm() {

        m_jLocation.setEnabled(false);
        jImport.setEnabled(false);
        jbtnReset.setEnabled(true);        
        jFileRead.setEnabled(false);
        jFileName.setText(null);
        csvFileName = "";

// Status area
        progress = 0;
        webPBar.setValue(progress);
        jTextUpdate.setText("");
        jTextRecords.setText("");
        qtyUpdates = 0;        
    }

    /**
     * Deactivates and resets all form fields.
     *
     * @return
     */
    @Override
    public boolean deactivate() {
        resetForm();
        return (true);
    }

    /**
     * Check file can be opened, read and closed
     * No Headers in the CSVFileName are required,
     * @param CSVFileName Name of the file (including the path) to open and read
     * @throws IOException If there is an issue reading the CSV file
     */
    private void checkFile(String CSVFileName) throws IOException {

        File f = new File(CSVFileName);
        if (f.exists()) {
            products = new CsvReader(CSVFileName, ',' ,Charset.forName("UTF-8"));
            products.setDelimiter(((String) jComboSeparator.getSelectedItem()).charAt(0));

            rowCount = 0;
            int i = 0;
            
            while (products.readRecord()) {
                ++rowCount;
            }
            jTextRecords.setText(Long.toString(rowCount));
            products.close();

            JOptionPane.showMessageDialog(null, "File Check "
                    + CSVFileName,
                    "File read OK",
                    JOptionPane.WARNING_MESSAGE);
            
            enableForm();
            
        } else {
            JOptionPane.showMessageDialog(null, "Unable to locate "
                    + CSVFileName,
                    "File not found",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Imports the external file
     * @param CSVFileName Name of the file (including path) to import.
     * @throws IOException If there are file reading issues.
     */
    private void ImportCsvFile(String CSVFileName) throws IOException {

        File f = new File(CSVFileName);
        if (f.exists()) {
            webPBar.setString ( "Starting..." );
            webPBar.setVisible(true);
            jImport.setEnabled(true);
            
// Read file
            products = new CsvReader(CSVFileName, ',' ,Charset.forName("UTF-8"));
            products.setDelimiter(((String) jComboSeparator.getSelectedItem()).charAt(0));
            currentRecord = 0;

// Prime: read the csv record and update to zero matching stockcurrent values
            while (products.readRecord()) {
                recordType = "delete";
                deleteRecord(recordType);
            }
            products.close();

// Work: read the file again and update stockcurrent values          
            products = new CsvReader(CSVFileName, ',' ,Charset.forName("UTF-8"));
            products.setDelimiter(((String) jComboSeparator.getSelectedItem()).charAt(0));
            currentRecord = 0;
            
            while (products.readRecord()) {
                currentRecord++;
                progress = currentRecord;
                recordType = "update";
                updateRecord(recordType);
            }
                products.close();
               
        } else {
            JOptionPane.showMessageDialog(null, 
                "Unable to locate " 
                + CSVFileName, 
                "File not found", 
                    JOptionPane.WARNING_MESSAGE);
        }

            jTextUpdate.setText(Integer.toString(qtyUpdates));
            
            JOptionPane.showMessageDialog(null,
                "Import Complete",
                "Imported",
                    JOptionPane.WARNING_MESSAGE);            

            progress = 100;
            webPBar.setValue(progress);
            webPBar.setString("Imported" + progress); 
    }
    
    /**
     * Update the record in the database with the new Quantities
     * @param pId Unique product id of the record to be updated
     */
    private void updateRecord(String pId) throws IOException {
        prodInfo = new ProductInfoExt();
        prodStock = new ProductStock();
        try {

            String sCode = products.get(0);

            prodInfo = m_dlSales.getProductInfoByCode(sCode);
            
            if (prodInfo != null) {
                prodStock = m_dlSales.getProductStockState(prodInfo.getID(), m_sInventoryLocation);
                productBarcode = products.get(0);
                oldQty = prodStock.getUnits();
                newQty = Double.valueOf(products.get(1));
                productQty = oldQty + newQty;
                updateStockCurrent(m_sInventoryLocation, prodInfo.getID(), productQty);
                CSVStockUpdate(Location, productBarcode, newQty);                 
                qtyUpdates++;
            }
        } catch (BasicException ex) {
            Logger.getLogger(StockQtyImport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Delete the record in the database with the new Quantities
     * @param pId Unique product id of the record to be updated
     */
    private void deleteRecord(String pId) throws IOException {
        prodInfo = new ProductInfoExt();
        prodStock = new ProductStock();
        try {

            String sCode = products.get(0);

            prodInfo = m_dlSales.getProductInfoByCode(sCode);

            if (prodInfo != null) {
                prodStock = m_dlSales.getProductStockState(prodInfo.getID(), m_sInventoryLocation);
                productQty = 0.;

                deleteStockCurrent(m_sInventoryLocation, prodInfo.getID(), productQty);
            }
        } catch (BasicException ex) {
            Logger.getLogger(StockQtyImport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * FUTURE - Add non existing minimal Products
     * @param LocationID
     * @param ProductID
     * @param Units
     * @throws com.openbravo.basic.BasicException
     */    
    public void addStockCurrent(String LocationID, String ProductID, Double Units) throws BasicException {

        Object[] values = new Object[3];
        values[0] = LocationID;
        values[1] = ProductID;                                      
        values[2] = (double) Units;

        PreparedSentence sentence = new PreparedSentence(s, 
                "INSERT INTO stockcurrent ( "
                + "LOCATION, PRODUCT, UNITS) VALUES (?, ?, ?)"
                , new SerializerWriteBasicExt((new Datas[]{
                    Datas.STRING, 
                    Datas.STRING, 
                    Datas.DOUBLE
                }),
                new int[]{0, 1, 2
                }));

        sentence.exec(values);
    }   

    /**
     * Update existing Product Current Quantity
     * @param LocationID
     * @param ProductID
     * @param Units
     * @throws com.openbravo.basic.BasicException
     */        
    public void updateStockCurrent(String LocationID, String ProductID, Double Units) throws BasicException {

        Object[] newValues = new Object[3];
        newValues[0] = (double) Units;
        newValues[1] = LocationID;
        newValues[2] = ProductID;                                      

        PreparedSentence sentence = new PreparedSentence(s, 
                "UPDATE stockcurrent SET "
                        + "UNITS = ? "
                + "WHERE LOCATION = ? "
                        + "AND PRODUCT = ?"
                , new SerializerWriteBasicExt((new Datas[]{
                    Datas.DOUBLE, 
                    Datas.STRING, 
                    Datas.STRING
                }),
                new int[]{
                    0, 1, 2}
                ));

        sentence.exec(newValues);
    }     

    /**
     * Reset existing Product Current Quantity to Zero
     * @param LocationID
     * @param ProductID
     * @param Units
     * @throws com.openbravo.basic.BasicException
     */     
    public void deleteStockCurrent(String LocationID, String ProductID, Double Units) throws BasicException {

        Object[] oldValues = new Object[3];
        oldValues[0] = (double) Units;
        oldValues[1] = LocationID;
        oldValues[2] = ProductID;                                      

        PreparedSentence sentence = new PreparedSentence(s, 
                "UPDATE stockcurrent SET "
                        + "UNITS = ? "
                + "WHERE LOCATION = ? "
                        + "AND PRODUCT = ?"
                , new SerializerWriteBasicExt((new Datas[]{
                    Datas.DOUBLE, 
                    Datas.STRING, 
                    Datas.STRING
                }),
                new int[]{
                    0, 1, 2}
                ));

        sentence.exec(oldValues);
    }    

    /**
     * Add Product Update log entry - Updates only
     * @param LocationID
     * @param Units
     */
    public void CSVStockUpdate(String LocationID, String Code, Double Units) {

        Object[] myprod = new Object[6];
        myprod[0] = UUID.randomUUID().toString();                               // ID string
        myprod[1] = Integer.toString(currentRecord);                            // Record number
        myprod[2] = "Qty update";                                               // Error description        
        myprod[3] = Location;                                                   // Location ID
        myprod[4] = productBarcode;                                             // Product Barcode
        myprod[5] = newQty;                                                     // Product Quantity

                try {
            m_dlSystem.execCSVStockUpdate(myprod);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelCSVImport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Pushes the Import process into a new thread so it doesn't interfere with
     * the UI responsiveness.
    */
    private void setWorker() {
        progress = 0;
        webPBar.setStringPainted(true);

        final SwingWorker<Integer, Integer> pbWorker;
        pbWorker = new SwingWorker<Integer, Integer>() {
 
            @Override
            protected final Integer doInBackground() throws Exception {
                while ((progress >= 0) && (progress < 100)) {
                    Thread.sleep(50);
                    this.publish(progress);
                }
                this.publish(100);
                this.done();
                return 100;
            }

            @Override
            protected final void process(final List<Integer> chunks) {
                webPBar.setValue(chunks.get(0));
                if (progress > 100) {
                    progress = 100;
                    webPBar.setString("Imported 100%");
                } else {
                    webPBar.setString("Imported " + progress + "%");
                }    
            }
        };
        pbWorker.execute();
    }

    /**
     * Runs the setWorker.
     *
     */    
    private class workProcess implements Runnable {

        @Override
        public void run() {
            try {
                ImportCsvFile(jFileName.getText());
            } catch (IOException ex) {
                Logger.getLogger(StockQtyImport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooserPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jFileName = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jbtnFileChoose = new javax.swing.JButton();
        jComboSeparator = new javax.swing.JComboBox();
        jFileRead = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTextUpdates = new javax.swing.JLabel();
        jTextRecords = new javax.swing.JTextField();
        jTextUpdate = new javax.swing.JTextField();
        webPBar = new com.alee.laf.progressbar.WebProgressBar();
        m_jLocation = new javax.swing.JLabel();
        jImport = new javax.swing.JButton();
        jbtnReset = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(700, 350));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel1.setText(bundle.getString("label.csvfile")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 30));

        jFileName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jFileName.setPreferredSize(new java.awt.Dimension(300, 30));
        jFileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileNameActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel18.setText(bundle.getString("label.csvdelimit")); // NOI18N
        jLabel18.setPreferredSize(new java.awt.Dimension(100, 30));

        jbtnFileChoose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/fileopen.png"))); // NOI18N
        jbtnFileChoose.setMaximumSize(new java.awt.Dimension(64, 32));
        jbtnFileChoose.setMinimumSize(new java.awt.Dimension(64, 32));
        jbtnFileChoose.setPreferredSize(new java.awt.Dimension(80, 45));
        jbtnFileChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnFileChooseActionPerformed(evt);
            }
        });

        jComboSeparator.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jComboSeparator.setPreferredSize(new java.awt.Dimension(50, 30));

        javax.swing.GroupLayout jFileChooserPanelLayout = new javax.swing.GroupLayout(jFileChooserPanel);
        jFileChooserPanel.setLayout(jFileChooserPanelLayout);
        jFileChooserPanelLayout.setHorizontalGroup(
            jFileChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFileChooserPanelLayout.createSequentialGroup()
                .addGroup(jFileChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFileChooserPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jFileChooserPanelLayout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnFileChoose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jFileChooserPanelLayout.setVerticalGroup(
            jFileChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFileChooserPanelLayout.createSequentialGroup()
                .addGroup(jFileChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jFileChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnFileChoose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jFileRead.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jFileRead.setText(bundle.getString("label.csvread")); // NOI18N
        jFileRead.setEnabled(false);
        jFileRead.setPreferredSize(new java.awt.Dimension(85, 45));
        jFileRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileReadActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true), bundle.getString("title.CSVImport"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14), new java.awt.Color(102, 102, 102))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText(bundle.getString("label.csvrecordsfound")); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(150, 30));

        jTextUpdates.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextUpdates.setText(bundle.getString("label.csvchanged")); // NOI18N
        jTextUpdates.setPreferredSize(new java.awt.Dimension(150, 30));

        jTextRecords.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextRecords.setForeground(new java.awt.Color(102, 102, 102));
        jTextRecords.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextRecords.setBorder(null);
        jTextRecords.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextRecords.setEnabled(false);
        jTextRecords.setPreferredSize(new java.awt.Dimension(100, 30));

        jTextUpdate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextUpdate.setForeground(new java.awt.Color(102, 102, 102));
        jTextUpdate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextUpdate.setBorder(null);
        jTextUpdate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextUpdate.setEnabled(false);
        jTextUpdate.setPreferredSize(new java.awt.Dimension(100, 30));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextRecords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextUpdates, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextRecords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextUpdates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47))
        );

        webPBar.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        webPBar.setPreferredSize(new java.awt.Dimension(240, 30));

        m_jLocation.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jLocation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLocation.setText(bundle.getString("label.location")); // NOI18N
        m_jLocation.setPreferredSize(new java.awt.Dimension(100, 30));

        jImport.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jImport.setText(bundle.getString("label.csvimpostbtn")); // NOI18N
        jImport.setEnabled(false);
        jImport.setPreferredSize(new java.awt.Dimension(85, 45));
        jImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jImportActionPerformed(evt);
            }
        });

        jbtnReset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnReset.setText(bundle.getString("button.reset")); // NOI18N
        jbtnReset.setPreferredSize(new java.awt.Dimension(85, 45));
        jbtnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnResetActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(bundle.getString("label.csvnotice")); // NOI18N
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jLabel2.setOpaque(true);
        jLabel2.setPreferredSize(new java.awt.Dimension(710, 110));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFileChooserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jLocation, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jFileRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(webPBar, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jFileChooserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jFileRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtnReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(webPBar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jFileReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileReadActionPerformed
        try {
            checkFile(jFileName.getText());
                webPBar.setString("Source file OK");            
                m_jLocation.setEnabled(true);
        } catch (IOException ex) {
            Logger.getLogger(StockQtyImport.class.getName()).log(Level.SEVERE, null, ex);
                webPBar.setString("Source file error!");            
                m_jLocation.setEnabled(false);                
        }
    }//GEN-LAST:event_jFileReadActionPerformed

    private void jFileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileNameActionPerformed
        jImport.setEnabled(false);
        jFileRead.setEnabled(true);
    }//GEN-LAST:event_jFileNameActionPerformed

    private void jbtnFileChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnFileChooseActionPerformed
        resetForm();
        setWorker();        

        JFileChooser chooser = new JFileChooser(last_folder == null ? "C:\\" : last_folder);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("csv files", "csv");
        chooser.setFileFilter(filter);
        chooser.showOpenDialog(null);
        File csvFile = chooser.getSelectedFile();
      
        if (csvFile == null) {
            return;
        }

        File current_folder = chooser.getCurrentDirectory();

        if (last_folder == null || !last_folder.equals(current_folder.getAbsolutePath())) {
            AppConfig CSVConfig = new AppConfig(config_file);
            CSVConfig.load();
            CSVConfig.setProperty("CSV.last_folder", current_folder.getAbsolutePath());
            last_folder = current_folder.getAbsolutePath();
            try {
                CSVConfig.save();
            } catch (IOException ex) {
                Logger.getLogger(StockQtyImport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String csv = csvFile.getName();
        if (!(csv.trim().equals(""))) {
            csvFileName = csvFile.getAbsolutePath();
            jFileName.setText(csvFileName);
            jFileRead.setEnabled(true);
        }
    }//GEN-LAST:event_jbtnFileChooseActionPerformed

    private void jbtnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnResetActionPerformed
        resetForm();
        progress = 0;
        webPBar.setString("Waiting...");
    }//GEN-LAST:event_jbtnResetActionPerformed

    private void jImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jImportActionPerformed
        
        jFileRead.setEnabled(false);
        jImport.setEnabled(false);

        workProcess work = new workProcess();
        Thread thread2 = new Thread(work);
        thread2.start();
    }//GEN-LAST:event_jImportActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboSeparator;
    private javax.swing.JPanel jFileChooserPanel;
    private javax.swing.JTextField jFileName;
    private javax.swing.JButton jFileRead;
    private javax.swing.JButton jImport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextRecords;
    private javax.swing.JTextField jTextUpdate;
    private javax.swing.JLabel jTextUpdates;
    private javax.swing.JButton jbtnFileChoose;
    private javax.swing.JButton jbtnReset;
    private javax.swing.JLabel m_jLocation;
    private com.alee.laf.progressbar.WebProgressBar webPBar;
    // End of variables declaration//GEN-END:variables
}
