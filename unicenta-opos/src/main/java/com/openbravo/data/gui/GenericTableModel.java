/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.data.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author pauloborges
 */
public class GenericTableModel<T> extends AbstractTableModel {

    List<T> rows = new ArrayList<>();
    //private final Map<String, Column<T, ?>> columnKeys = new HashMap<>();

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValueAt(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class Column<T, V> {

    }

    @FunctionalInterface
    public interface ValueProvider<SOURCE, TARGET> {

        /**
         * Provides a value from the given source object.
         *
         * @param source the source to retrieve the value from
         * @return the value provided by the source
         */
        public TARGET apply(SOURCE source);
    }

}
