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

package com.openbravo.pos.panels;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import javax.swing.tree.TreeNode;

/**
 *
 * @author adrianromero
 */
public class SQLDatabase implements TreeNode {
    
    private ArrayList m_aTables;
    private HashMap m_mTables;
    private String m_sName;
    
    /** Creates a new instance of SQLDatabase
     * @param name */
    public SQLDatabase(String name) {
        m_sName = name;
        m_aTables = new ArrayList();
        m_mTables = new HashMap();
    }
    @Override
    public String toString() {
        return m_sName;
    }
    
    /**
     *
     * @param sTable
     */
    public void addTable(String sTable) {
        SQLTable t = new SQLTable(this, sTable);
        m_aTables.add(t);
        m_mTables.put(sTable, t);
    }

    /**
     *
     * @param sTable
     * @return
     */
    public SQLTable getTable(String sTable) {
        return (SQLTable) m_mTables.get(sTable);
    }
    
    @Override
    public Enumeration children(){
        return new EnumerationIter(m_aTables.iterator());
    }
    @Override
    public boolean getAllowsChildren() {
        return true;
    }
    @Override
    public TreeNode getChildAt(int childIndex) {
        return (TreeNode) m_aTables.get(childIndex);
    }
    @Override
    public int getChildCount() {
        return m_aTables.size();
    }
    @Override
    public int getIndex(TreeNode node){
        return m_aTables.indexOf(node);
    }
    @Override
    public TreeNode getParent() {
        return null;
    }
    @Override
    public boolean isLeaf() {
// JG 16 May 2013 use isEmpty
        return m_aTables.isEmpty();
    }    
}
