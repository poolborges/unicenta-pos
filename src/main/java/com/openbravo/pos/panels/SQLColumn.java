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
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-130

package com.openbravo.pos.panels;

import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 *
 * @author adrianromero
 */
public class SQLColumn implements TreeNode {
    
    private SQLTable m_table;
    private String m_sName;
    
    /** Creates a new instance of SQLColumn
     * @param t
     * @param name */
    public SQLColumn(SQLTable t, String name) {
        m_table = t;
        m_sName = name;
    }
    @Override
    public String toString() {
        return m_sName;
    }
    
    @Override
    public Enumeration children(){
        return null;
    }
    @Override
    public boolean getAllowsChildren() {
        return false;
    }
    @Override
    public TreeNode getChildAt(int childIndex) {
        throw new ArrayIndexOutOfBoundsException();
    }
    @Override
    public int getChildCount() {
        return 0;
    }
    @Override
    public int getIndex(TreeNode node){
        throw new ArrayIndexOutOfBoundsException();
    }
    @Override
    public TreeNode getParent() {
        return m_table;
    }
    @Override
    public boolean isLeaf() {
        return true;
    }      
}
