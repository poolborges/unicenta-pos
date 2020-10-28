//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2015 uniCenta & previous Openbravo POS works
//    http://www.unicenta.com
//
//    
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
package com.openbravo.data.user;

import java.util.*;
import javax.swing.*;
import java.awt.Component;
import javax.swing.event.EventListenerList;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.LocalRes;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.customers.CustomerInfoGlobal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;

/**
 *
 * @author JG uniCenta
 */
public class BrowsableEditableData {

    public static final int ST_NORECORD = 0;
    public static final int ST_UPDATE = 1;
    public static final int ST_DELETE = 2;
    public static final int ST_INSERT = 3;

    private final static int INX_EOF = -1;

    private BrowsableData m_bd;

    protected EventListenerList listeners = new EventListenerList();

    private EditorRecord m_editorrecord;
    private DirtyManager m_Dirty;
    private int m_iState; // vinculado siempre al m_editorrecord
//    private DocumentLoader m_keyvalue;
    private int m_iIndex;
    private boolean m_bIsAdjusting;

    private boolean iseditable = true;

    /**
     * Creates a new instance of BrowsableEditableData
     *
     * @param bd
     * @param ed
     * @param dirty
     */
    public BrowsableEditableData(BrowsableData bd, EditorRecord ed, DirtyManager dirty) {
        m_bd = bd;

        m_editorrecord = ed;
        m_Dirty = dirty;
        m_iState = ST_NORECORD;
        m_iIndex = INX_EOF; // En EOF
        m_bIsAdjusting = false;
//        m_keyvalue = DocumentLoaderBasic.INSTANCE;

        // Inicializo ?
        m_editorrecord.writeValueEOF();
        m_Dirty.setDirty(false);
    }

    /**
     * Check Ticket Dirty state
     *
     * @param dataprov
     * @param saveprov
     * @param c
     * @param ed
     * @param dirty
     */
    public BrowsableEditableData(ListProvider dataprov, SaveProvider saveprov, Comparator c, EditorRecord ed, DirtyManager dirty) {
        this(new BrowsableData(dataprov, saveprov, c), ed, dirty);
    }

    /**
     * Ticket Dirty state
     *
     * @param dataprov
     * @param saveprov
     * @param ed
     * @param dirty
     */
    public BrowsableEditableData(ListProvider dataprov, SaveProvider saveprov, EditorRecord ed, DirtyManager dirty) {
        this(new BrowsableData(dataprov, saveprov, null), ed, dirty);
    }

    /**
     * Ticket data for list
     *
     * @return
     */
    public final ListModel getListModel() {
        return m_bd;
    }

    /**
     * Value Changes
     *
     * @return
     */
    public final boolean isAdjusting() {
        return m_bIsAdjusting || m_bd.isAdjusting();
    }

    private Object getCurrentElement() {
        return (m_iIndex >= 0 && m_iIndex < m_bd.getSize()) ? m_bd.getElementAt(m_iIndex) : null;
    }

    /**
     * Return index
     *
     * @return
     */
    public final int getIndex() {
        return m_iIndex;
    }

    /**
     * Add to State listener
     *
     * @param l
     */
    public final void addStateListener(StateListener l) {
        listeners.add(StateListener.class, l);
    }

    /**
     * Delete from State listener
     *
     * @param l
     */
    public final void removeStateListener(StateListener l) {
        listeners.remove(StateListener.class, l);
    }

    /**
     * Edit State listener
     *
     * @param l
     */
    public final void addEditorListener(EditorListener l) {
        listeners.add(EditorListener.class, l);
    }

    /**
     * Delete from State listener
     *
     * @param l
     */
    public final void removeEditorListener(EditorListener l) {
        listeners.remove(EditorListener.class, l);
    }

    /**
     * Add to browse listener
     *
     * @param l
     */
    public final void addBrowseListener(BrowseListener l) {
        listeners.add(BrowseListener.class, l);
    }

    /**
     * Delete from browse listener
     *
     * @param l
     */
    public final void removeBrowseListener(BrowseListener l) {
        listeners.remove(BrowseListener.class, l);
    }

    /**
     * Return State
     *
     * @return
     */
    public int getState() {
        return m_iState;
    }

    private void fireStateUpdate() {
        EventListener[] l = listeners.getListeners(StateListener.class);
        int iState = getState();
        for (EventListener l1 : l) {
            ((StateListener) l1).updateState(iState);
        }
    }

    /**
     * Execute listener event fire
     */
    protected void fireDataBrowse() {

        m_bIsAdjusting = true;
        // Lanzamos los eventos...
        Object obj = getCurrentElement();
        int iIndex = getIndex();
        int iCount = m_bd.getSize();

        // actualizo el registro
        if (obj == null) {
            m_iState = ST_NORECORD;
            m_editorrecord.writeValueEOF();
        } else {
            m_iState = ST_UPDATE;
            m_editorrecord.writeValueEdit(obj);
        }
        m_Dirty.setDirty(false);
        fireStateUpdate();

        // Invoco a los Editor Listener
        for (EventListener l1 : listeners.getListeners(EditorListener.class)) {
            ((EditorListener) l1).updateValue(obj);
        }
        // Y luego a los Browse Listener
        for (EventListener l1 : listeners.getListeners(BrowseListener.class)) {
            ((BrowseListener) l1).updateIndex(iIndex, iCount);
        }
        m_bIsAdjusting = false;
    }

    /**
     * Data available
     *
     * @return
     */
    public boolean canLoadData() {
        return m_bd.canLoadData();
    }

    /**
     * Flag data editable
     *
     * @param value
     */
    public void setEditable(boolean value) {
        iseditable = value;
    }

    /**
     * Flag data can insert
     *
     * @return
     */
    public boolean canInsertData() {
        return iseditable && m_bd.canInsertData();
    }

    /**
     * Flag data can delete
     *
     * @return
     */
    public boolean canDeleteData() {
        return iseditable && m_bd.canDeleteData();
    }

    /**
     * Flag can update
     *
     * @return
     */
    public boolean canUpdateData() {
        return iseditable && m_bd.canUpdateData();
    }

    /**
     * Refresh current state
     */
    public void refreshCurrent() {
        baseMoveTo(m_iIndex);
    }

    /**
     * Refresh object data
     *
     * @throws BasicException
     */
    public void refreshData() throws BasicException {
        saveData();
        m_bd.refreshData();
        m_editorrecord.refresh();
        baseMoveTo(0);
    }

    /**
     * Load object data
     *
     * @throws BasicException
     */
    public void loadData() throws BasicException {
        saveData();
        m_bd.loadData();
        m_editorrecord.refresh();
        baseMoveTo(0);
    }

    /**
     * Unload object data
     *
     * @throws BasicException
     */
    public void unloadData() throws BasicException {
        saveData();
        m_bd.unloadData();
        m_editorrecord.refresh();
        baseMoveTo(0);
    }

    /**
     * Sort object data
     *
     * @param c
     * @throws BasicException
     */
    public void sort(Comparator c) throws BasicException {
        saveData();
        m_bd.sort(c);
        baseMoveTo(0);
    }

    /**
     * Move data to object
     *
     * @param i
     * @throws BasicException
     */
    public void moveTo(int i) throws BasicException {
        saveData();
        if (m_iIndex != i) {
            baseMoveTo(i);
        }
    }

    /**
     * Step into data -1 (Back)
     *
     * @throws BasicException
     */
    public final void movePrev() throws BasicException {
        saveData();
        if (m_iIndex > 0) {
            baseMoveTo(m_iIndex - 1);
        }
    }

    /**
     * Step into data +1 (Forward)
     *
     * @throws BasicException
     */
    public final void moveNext() throws BasicException {
        saveData();
        if (m_iIndex < m_bd.getSize() - 1) {
            baseMoveTo(m_iIndex + 1);
        }
    }

    /**
     * Step into data BOF (First)
     *
     * @throws BasicException
     */
    public final void moveFirst() throws BasicException {
        saveData();
        if (m_bd.getSize() > 0) {
            baseMoveTo(0);
        }
    }

    /**
     * Step into data EOF (End)
     *
     * @throws BasicException
     */
    public final void moveLast() throws BasicException {
        saveData();
        if (m_bd.getSize() > 0) {
            baseMoveTo(m_bd.getSize() - 1);
        }
    }

    /**
     * Step into data =value (Next)
     *
     * @param f
     * @return
     * @throws BasicException
     */
    public final int findNext(Finder f) throws BasicException {
        return m_bd.findNext(m_iIndex, f);
    }

    /**
     * Save data
     *
     * @throws BasicException
     */
    public void saveData() throws BasicException {

        if (m_Dirty.isDirty()) {
            switch (m_iState) {
                case ST_UPDATE: {
                    int i = m_bd.updateRecord(m_iIndex, m_editorrecord.createValue());
                    m_editorrecord.refresh();
                    baseMoveTo(i);
                    break;
                }
                case ST_INSERT: {
                    int i = m_bd.insertRecord(m_editorrecord.createValue());
                    m_editorrecord.refresh();
                    baseMoveTo(i);
                    break;
                }
                case ST_DELETE: {
                    int i = m_bd.removeRecord(m_iIndex);
                    m_editorrecord.refresh();
                    baseMoveTo(i);
                    break;
                }
                default:
                    break;
            }
        }
        
        //TODO should remove specific code
        saveCustomerData();
    }

    private void triggerCustomerEvent(String event, Object[] customer, AppView appContext) {
        try {
            ScriptEngine scriptEngine = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);

            DataLogicSystem dlSystem = (DataLogicSystem) appContext.getBean("com.openbravo.pos.forms.DataLogicSystem");
            String script = dlSystem.getResourceAsXML(event);
            scriptEngine.put("customer", customer);
            scriptEngine.put("device", appContext.getProperties().getProperty("machine.hostname"));
            scriptEngine.eval(script);

        } catch (BeanFactoryException | ScriptException e) {
            System.err.println("Script Exception: " + e);
        }
    }

    /**
     * Reinstantiate data
     *
     * @param c
     */
    public void actionReloadCurrent(Component c) {
        if (!m_Dirty.isDirty()
                || JOptionPane.showConfirmDialog(c,
                        LocalRes.getIntString("message.changeslost"),
                        LocalRes.getIntString("title.editor"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            refreshCurrent();
        }
    }

    /**
     * Evaluate data before before commit
     *
     * @param c
     * @return
     * @throws BasicException
     */
    public boolean actionClosingForm(Component c) throws BasicException {
        if (m_Dirty.isDirty()) {
            int res = JOptionPane.showConfirmDialog(c, LocalRes.getIntString("message.wannasave"), LocalRes.getIntString("title.editor"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            switch (res) {
                case JOptionPane.YES_OPTION:
                    saveData();
                    return true;
                case JOptionPane.NO_OPTION:
                    refreshCurrent();
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    /*
     * Metodos publicos finales (algunos protegidos que podrian ser finales
     */
    /**
     * Instantiate data
     *
     * @throws BasicException
     */
    public final void actionLoad() throws BasicException {
        loadData();
        if (m_bd.getSize() == 0) {
            actionInsert();
        }
    }

    /**
     * Insert data - conditional
     *
     * @throws BasicException
     */
    public final void actionInsert() throws BasicException {
        // primero persistimos
        saveData();

        if (canInsertData()) {
            // Y nos ponemos en estado de insert
            m_iState = ST_INSERT;
            m_editorrecord.writeValueInsert();
            m_Dirty.setDirty(false);
            fireStateUpdate(); // ?
        }
    }

    /**
     * Delete data
     *
     * @throws BasicException
     */
    public final void actionDelete() throws BasicException {
        // primero persistimos
        saveData();

        if (canDeleteData()) {

            // Y nos ponemos en estado de delete
            Object obj = getCurrentElement();
            int iIndex = getIndex();
            int iCount = m_bd.getSize();
            if (iIndex >= 0 && iIndex < iCount) {
                m_iState = ST_DELETE;
                m_editorrecord.writeValueDelete(obj);
                m_Dirty.setDirty(true);
                fireStateUpdate(); // ?
            }
        }
    }

    private void baseMoveTo(int i) {
        // Este senor y el constructor a INX_EOF, son los unicos que tienen potestad de modificar m_iIndex.
        if (i >= 0 && i < m_bd.getSize()) {
            m_iIndex = i;
        } else {
            m_iIndex = INX_EOF;
        }
        fireDataBrowse();
    }

    /**
     * saveCustomerData Trigger Script Execution on event
     * Execute only in view context of com.openbravo.pos.customers.CustomersView
     * 
     *   Events: customer.created, customer.updated, customer.deleted
     * 
     * @throws BasicException 
     */
    private void saveCustomerData() throws BasicException {
   
        if (m_editorrecord.getClass().getName().equals("com.openbravo.pos.customers.CustomersView")) {

            // com.openbravo.pos.customers.CustomersView MUST return Array of 27 Objects
            Object[] customer = (Object[]) m_editorrecord.createValue();
            AppView appView = (AppView) customer[27];

            if (m_Dirty.isDirty()) {
                switch (m_iState) {
                    case ST_UPDATE: {
                        triggerCustomerEvent("customer.updated", customer, appView);
                        break;
                    }
                    case ST_INSERT: {
                        triggerCustomerEvent("customer.created", customer, appView);

                        int n = JOptionPane.showConfirmDialog(
                                null,
                                AppLocal.getIntString("message.customerassign"),
                                AppLocal.getIntString("title.editor"),
                                JOptionPane.YES_NO_OPTION);

                        if (n == 0) {
                            CustomerInfoGlobal customerInfoGlobal = CustomerInfoGlobal.getInstance();
                            CustomerInfoExt customerInfoExt = new CustomerInfoExt(customer[0].toString());
                            customerInfoGlobal.setCustomerInfoExt(customerInfoExt);
                            customerInfoExt.setName(customer[3].toString());

                            if (appView != null) {
                                appView.getAppUserView().showTask("com.openbravo.pos.sales.JPanelTicketSales");
                            }
                        }
                        break;
                    }
                    case ST_DELETE: {
                        triggerCustomerEvent("customer.deleted", customer, appView);
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }
}
