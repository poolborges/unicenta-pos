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

package com.openbravo.data.user;

import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;

/**
 *
 * @author  adrian
 */
public class DirtyManager implements DocumentListener, ChangeListener, ActionListener, PropertyChangeListener {
    
    private boolean isDirty;    

    private final List<DirtyListener> listeners = new ArrayList<>();

    public DirtyManager() {
        isDirty = false;
    }

    public void addDirtyListener(DirtyListener l) {
        listeners.add(l);
    }

    public void removeDirtyListener(DirtyListener l) {
        listeners.remove(l);
    }

    protected void fireChangedDirty() {
        listeners.stream().forEach((DirtyListener listener) -> {
            listener.changedDirty(isDirty);
        });
    }

    public void setDirty(boolean dirty) {
        if (isDirty != dirty) {
            isDirty = dirty;
            fireChangedDirty();
        }
    }

    public boolean isDirty() {
        return isDirty;
    }
    
    @Override
    public void changedUpdate(DocumentEvent evt) {
        setDirty(true);
    }
    
    @Override
    public void insertUpdate(DocumentEvent evt) {
        setDirty(true);
    }   
    
    @Override
    public void removeUpdate(DocumentEvent evt) {
        setDirty(true);
    }    
    
    @Override
    public void stateChanged(ChangeEvent evt) {
        setDirty(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        setDirty(true);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setDirty(true);
    }
    
}
