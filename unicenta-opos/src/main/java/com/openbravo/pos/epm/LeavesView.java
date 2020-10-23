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
package com.openbravo.pos.epm;

import com.openbravo.basic.BasicException;
import com.openbravo.beans.JCalendarDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import java.awt.Component;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Calendar;

/**
 *
 * @author Ali Safdar and Aneeqa Baber
 */
public final class LeavesView extends javax.swing.JPanel implements EditorRecord {

    private Object m_oId;
    private Object m_employeeid;
    private SentenceList m_sentcat;

    private DirtyManager m_Dirty;
    private DataLogicPresenceManagement dlPresenceManagement;

    /**
     * Creates new form LeavesView
     *
     * @param app
     * @param dirty
     */
    public LeavesView(AppView app, DirtyManager dirty) {

        dlPresenceManagement = (DataLogicPresenceManagement) app.getBean("com.openbravo.pos.epm.DataLogicPresenceManagement");
        initComponents();

        m_sentcat = dlPresenceManagement.getLeavesList();
        m_Dirty = dirty;
        m_jEmployeeName.getDocument().addDocumentListener(dirty);
        m_jStartDate.getDocument().addDocumentListener(dirty);
        m_jEndDate.getDocument().addDocumentListener(dirty);
        m_jLeaveNote.getDocument().addDocumentListener(dirty);
        writeValueEOF();
    }

    void activate() throws BasicException {
        List a = m_sentcat.list();
        a.add(0, null);
    }

    /**
     *
     */
    @Override
    public void writeValueEOF() {
        m_oId = null;
        m_jEmployeeName.setText(null);
        m_jStartDate.setText(null);
        m_jEndDate.setText(null);
        m_jLeaveNote.setText(null);
        m_jEmployeeName.setEditable(false);
        m_jStartDate.setEnabled(false);
        m_jEndDate.setEnabled(false);
        m_jLeaveNote.setEnabled(false);
    }

    /**
     *
     */
    @Override
    public void writeValueInsert() {
        m_oId = null;
        m_jEmployeeName.setText(null);
        m_jStartDate.setText(null);
        m_jEndDate.setText(null);
        m_jLeaveNote.setText(null);
        m_jEmployeeName.setEditable(true);
        m_jStartDate.setEnabled(true);
        m_jEndDate.setEnabled(true);
        m_jLeaveNote.setEnabled(true);
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        Object[] leaves = (Object[]) value;
        m_oId = leaves[0];
        m_employeeid = leaves[1];
        m_jEmployeeName.setText((String) leaves[2]);
        m_jStartDate.setText(Formats.TIMESTAMP.formatValue((Date) leaves[3]));
        m_jEndDate.setText(Formats.TIMESTAMP.formatValue((Date) leaves[4]));
        m_jLeaveNote.setText((String) leaves[5]);
        m_jEmployeeName.setEditable(true);
        m_jStartDate.setEnabled(true);
        m_jEndDate.setEnabled(true);
        m_jLeaveNote.setEnabled(true);
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        Object[] leaves = (Object[]) value;
        m_oId = leaves[0];
        m_employeeid = leaves[1];
        m_jEmployeeName.setText((String) leaves[2]);
        m_jStartDate.setText(Formats.TIMESTAMP.formatValue((Date) leaves[3]));
        m_jEndDate.setText(Formats.TIMESTAMP.formatValue((Date) leaves[4]));
        m_jLeaveNote.setText((String) leaves[5]);
        m_jEmployeeName.setEditable(false);
        m_jStartDate.setEnabled(false);
        m_jEndDate.setEnabled(false);
        m_jLeaveNote.setEnabled(false);
    }

    /**
     *
     */
    @Override
    public void refresh() {
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
     * @return @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        Object[] leaves = new Object[6];
        leaves[0] = m_oId == null ? UUID.randomUUID().toString() : m_oId;
        leaves[1] = m_employeeid;
        leaves[2] = m_jEmployeeName.getText();
        leaves[3] = (Date) Formats.TIMESTAMP.parseValue(m_jStartDate.getText());
        leaves[4] = (Date) Formats.TIMESTAMP.parseValue(m_jEndDate.getText());
        leaves[5] = m_jLeaveNote.getText();
        boolean isCheckedIn = dlPresenceManagement.IsCheckedIn((String) m_employeeid);
        Date startDate = (Date) Formats.TIMESTAMP.parseValue(m_jStartDate.getText());
        Date endDate = (Date) Formats.TIMESTAMP.parseValue(m_jEndDate.getText());
        Date systemDate = new Date();
        if (isCheckedIn && startDate.before(systemDate) && endDate.after(systemDate)) {
            dlPresenceManagement.BlockEmployee((String) m_employeeid);
        }
        return leaves;
    }
// TODO - rewrite IsValidEndDate using Apache commons or Calendar 

    private boolean IsValidEndDate(Date date) {
        Date systemDate = new Date();
        if (!m_jStartDate.getText().equals("")) {
            Date startdate;
            try {
                startdate = (Date) Formats.TIMESTAMP.parseValue(m_jStartDate.getText());
                return (startdate.before(date)
                        || isSameDay(systemDate, date));

            } catch (BasicException ex) {
            }
        }
        return (systemDate.before(date)
                || isSameDay(systemDate, date));
    }

// TODO - rewrite IsValidStartDate using Apache commons or Calendar 
    private boolean IsValidStartDate(Date date) {
        Date systemDate = new Date();
        boolean validEndDate = true;
        if (!m_jEndDate.getText().equals("")) {
            try {
                Date enddate = (Date) Formats.TIMESTAMP.parseValue(m_jEndDate.getText());
                validEndDate = (enddate.after(date)
                        || isSameDay(systemDate, date));
            } catch (BasicException ex) {
            }
        }
        return validEndDate && (systemDate.before(date)
                || isSameDay(systemDate, date));
    }

    /**
     * Compare two date by Day/Month/Year
     * @param date1
     * @param date2
     * @return true is two date is same day in time
     */
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jEmployeeName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jLeaveNote = new javax.swing.JTextArea();
        m_Name = new javax.swing.JLabel();
        m_StartDate = new javax.swing.JLabel();
        m_EndDate = new javax.swing.JLabel();
        m_jStartDate = new javax.swing.JTextField();
        m_Notes = new javax.swing.JLabel();
        btnEmployee = new javax.swing.JButton();
        btnEndDate = new javax.swing.JButton();
        btnStartDate = new javax.swing.JButton();
        m_jEndDate = new javax.swing.JTextField();

        m_jEmployeeName.setEditable(false);
        m_jEmployeeName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jEmployeeName.setPreferredSize(new java.awt.Dimension(0, 30));

        m_jLeaveNote.setColumns(20);
        m_jLeaveNote.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        m_jLeaveNote.setLineWrap(true);
        m_jLeaveNote.setRows(5);
        jScrollPane1.setViewportView(m_jLeaveNote);

        m_Name.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        m_Name.setText(bundle.getString("label.epm.employee")); // NOI18N
        m_Name.setPreferredSize(new java.awt.Dimension(0, 30));

        m_StartDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_StartDate.setText(AppLocal.getIntString("label.epm.startdate")); // NOI18N
        m_StartDate.setPreferredSize(new java.awt.Dimension(0, 30));

        m_EndDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_EndDate.setText(AppLocal.getIntString("label.epm.enddate")); // NOI18N
        m_EndDate.setPreferredSize(new java.awt.Dimension(0, 30));

        m_jStartDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jStartDate.setPreferredSize(new java.awt.Dimension(0, 30));

        m_Notes.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_Notes.setText(AppLocal.getIntString("label.epm.notes")); // NOI18N
        m_Notes.setPreferredSize(new java.awt.Dimension(0, 30));

        btnEmployee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/user_sml.png"))); // NOI18N
        btnEmployee.setFocusPainted(false);
        btnEmployee.setFocusable(false);
        btnEmployee.setMaximumSize(new java.awt.Dimension(57, 33));
        btnEmployee.setMinimumSize(new java.awt.Dimension(57, 33));
        btnEmployee.setPreferredSize(new java.awt.Dimension(80, 45));
        btnEmployee.setRequestFocusEnabled(false);
        btnEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeActionPerformed(evt);
            }
        });

        btnEndDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        btnEndDate.setPreferredSize(new java.awt.Dimension(80, 45));
        btnEndDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndDateActionPerformed(evt);
            }
        });

        btnStartDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        btnStartDate.setPreferredSize(new java.awt.Dimension(80, 45));
        btnStartDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartDateActionPerformed(evt);
            }
        });

        m_jEndDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jEndDate.setPreferredSize(new java.awt.Dimension(0, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(m_Notes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(m_EndDate, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(m_StartDate, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(m_Name, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(m_jEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(m_jStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(m_jEmployeeName, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEmployee, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
                .addGap(77, 77, 77))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jEmployeeName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_StartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_EndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_Notes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeActionPerformed

        JEmployeeFinder finder = JEmployeeFinder.getEmployeeFinder(this, dlPresenceManagement);
        finder.search(null);
        finder.setVisible(true);

        try {
            m_jEmployeeName.setText(finder.getSelectedEmployee() == null
                    ? null
                    : dlPresenceManagement.loadEmployeeExt(finder.getSelectedEmployee().getId()).toString());
            m_employeeid = finder.getSelectedEmployee() == null
                    ? null
                    : finder.getSelectedEmployee().getId();
        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindemployee"), e);
            msg.show(this);
        }
}//GEN-LAST:event_btnEmployeeActionPerformed

    private void btnEndDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndDateActionPerformed
        Date date;
        try {
            date = (Date) Formats.TIMESTAMP.parseValue(m_jEndDate.getText());
        } catch (BasicException e) {
            date = null;
        }
        date = JCalendarDialog.showCalendarTimeHours(this, date);
        if (date != null) {
            if (IsValidEndDate(date)) {
                m_jEndDate.setText(Formats.TIMESTAMP.formatValue(date));
            } else {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.invalidenddate"));
                msg.show(this);
            }
        }
}//GEN-LAST:event_btnEndDateActionPerformed

    private void btnStartDateActionPerformed(java.awt.event.ActionEvent evt) {
        Date date;
        try {
            date = (Date) Formats.TIMESTAMP.parseValue(m_jStartDate.getText());
        } catch (BasicException e) {
            date = null;
        }
        date = JCalendarDialog.showCalendarTimeHours(this, date);
        if (date != null) {
            if (IsValidStartDate(date)) {
                m_jStartDate.setText(Formats.TIMESTAMP.formatValue(date));
            } else {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.invalidstartdate"));
                msg.show(this);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEmployee;
    private javax.swing.JButton btnEndDate;
    private javax.swing.JButton btnStartDate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel m_EndDate;
    private javax.swing.JLabel m_Name;
    private javax.swing.JLabel m_Notes;
    private javax.swing.JLabel m_StartDate;
    private javax.swing.JTextField m_jEmployeeName;
    private javax.swing.JTextField m_jEndDate;
    private javax.swing.JTextArea m_jLeaveNote;
    private javax.swing.JTextField m_jStartDate;
    // End of variables declaration//GEN-END:variables
}
