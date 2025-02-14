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
package com.openbravo.pos.reports;

import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BeanFactoryApp;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.BaseSentence;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.Session;
import com.openbravo.data.user.EditorCreator;
import com.openbravo.pos.forms.*;
import com.openbravo.pos.sales.TaxesLogic;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author JG uniCenta
 */
public abstract class JPanelReport extends JPanel implements JPanelView, BeanFactoryApp {

    private static final Logger LOGGER = Logger.getLogger(JPanelReport.class.getName());
    private JRViewer400 reportviewer = null;
    private EditorCreator editor = null;
    protected AppView m_App;
    private Session s;
    private Connection con;
    protected SentenceList taxsent;
    protected TaxesLogic taxeslogic;

    /**
     * Creates new form JPanelReport
     */
    public JPanelReport() {

        initComponents();
    }

    /**
     *
     * @param app
     * @throws BeanFactoryException
     */
    @Override
    public void init(AppView app) throws BeanFactoryException {

        m_App = app;

        DataLogicSales dlSales = (DataLogicSales) app.getBean("com.openbravo.pos.forms.DataLogicSales");
        taxsent = dlSales.getTaxList();

        editor = getEditorCreator();
        if (editor instanceof ReportEditorCreator) {
            jPanelFilter.add(((ReportEditorCreator) editor).getComponent(), BorderLayout.CENTER);
        }

        reportviewer = new JRViewer400(null);

        add(reportviewer, BorderLayout.CENTER);
    }

    public static JasperReport createJasperReport(String reportFilename) throws JRException {

        JasperReport jasperReport = null;

        if (reportFilename != null) {
            String fullName = reportFilename + ".ser";

            try (InputStream in = JPanelReport.class.getResourceAsStream(fullName)) {
                if (in != null) {
                    try (ObjectInputStream oin = new ObjectInputStream(in)) {
                        jasperReport = (JasperReport) oin.readObject();
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                LOGGER.log(Level.WARNING, "Exception load report file(.ser): " + fullName, ex);
            }
        }

        if (jasperReport == null && reportFilename != null) {
            String fullName = reportFilename + ".jrxml";
            try (InputStream in = JPanelReport.class.getResourceAsStream(fullName)) {
                if (in != null) {
                    //JasperDesign jd = JRXmlLoader.load(in);
                    jasperReport = JasperCompileManager.compileReport(in);
                }
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Exception load report file(.jrxml): " + fullName, ex);
            }
        }

        if (jasperReport == null) {
            LOGGER.log(Level.WARNING, "Cannot create JasperReport, because reportFilename is null");
        }

        return jasperReport;
    }

    /**
     *
     * @return
     */
    @Override
    public Object getBean() {
        return this;
    }

    /**
     *
     * @return
     */
    protected abstract String getReport();

    /**
     *
     * @return
     */
    protected abstract String getResourceBundle();

    /**
     *
     * @return
     */
    protected abstract BaseSentence getSentence();

    /**
     *
     * @return
     */
    protected abstract ReportFields getReportFields();

    /**
     *
     * @return
     */
    protected EditorCreator getEditorCreator() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {

        setVisibleFilter(true);
        taxeslogic = new TaxesLogic(taxsent.list());
    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {

        reportviewer.loadJasperPrint(null);
        return true;
    }

    /**
     *
     * @param value
     */
    protected void setVisibleButtonFilter(boolean value) {
        jToggleFilter.setVisible(value);
    }

    /**
     *
     * @param value
     */
    protected void setVisibleFilter(boolean value) {
        jToggleFilter.setSelected(value);
        jToggleFilterActionPerformed(null);
    }

    private void launchreport() {

        m_App.waitCursorBegin();

        String reportFilename = getReport();
        LOGGER.log(Level.INFO, "Launch report file: "+reportFilename);
        try {

            JasperReport jasperReport = JPanelReport.createJasperReport(reportFilename);
            if (jasperReport != null) {

                //RESOURCE FILE
                String res = getResourceBundle();

                //GET PARAMETER AND DATA
                Object params = (editor == null) ? null : editor.createValue();
                JRDataSource data = new JRDataSourceBasic(getSentence(), getReportFields(), params);

                //PARAMETERS
                Map<String, Object> reportparams = new HashMap<>();
                reportparams.put("ARG", params);
                if (res != null) {
                    reportparams.put("REPORT_RESOURCE_BUNDLE", ResourceBundle.getBundle(res));
                }
                reportparams.put("TAXESLOGIC", taxeslogic);

                JasperPrint jp = JasperFillManager.fillReport(jasperReport, reportparams, data);

                reportviewer.loadJasperPrint(jp);

                setVisibleFilter(false);
            }

        } catch (MissingResourceException | JRException |BasicException ex) {
            LOGGER.log(Level.SEVERE, "Exception lauch report file: "+reportFilename, ex);
            MessageInf.showDialogWarn(this, "<html>"+AppLocal.getIntString("message.cannotloadreportdata") + "<br>"+reportFilename, ex);
        } finally {
            m_App.waitCursorEnd();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelHeader = new javax.swing.JPanel();
        jPanelFilter = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jToggleFilter = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setLayout(new java.awt.BorderLayout());

        jPanelHeader.setLayout(new java.awt.BorderLayout());

        jPanelFilter.setLayout(new java.awt.BorderLayout());
        jPanelHeader.add(jPanelFilter, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jToggleFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1downarrow.png"))); // NOI18N
        jToggleFilter.setSelected(true);
        jToggleFilter.setToolTipText("Hide/Show Filter");
        jToggleFilter.setPreferredSize(new java.awt.Dimension(80, 45));
        jToggleFilter.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1uparrow.png"))); // NOI18N
        jToggleFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleFilterActionPerformed(evt);
            }
        });
        jPanel1.add(jToggleFilter);

        jButton1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/ok.png"))); // NOI18N
        jButton1.setText(AppLocal.getIntString("button.executereport")); // NOI18N
        jButton1.setToolTipText("Execute Report");
        jButton1.setPreferredSize(new java.awt.Dimension(150, 45));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        jPanelHeader.add(jPanel1, java.awt.BorderLayout.SOUTH);

        add(jPanelHeader, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        launchreport();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jToggleFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleFilterActionPerformed

        jPanelFilter.setVisible(jToggleFilter.isSelected());

    }//GEN-LAST:event_jToggleFilterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelFilter;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JToggleButton jToggleFilter;
    // End of variables declaration//GEN-END:variables

}
