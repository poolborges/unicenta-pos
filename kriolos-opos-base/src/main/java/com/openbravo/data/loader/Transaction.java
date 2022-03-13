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

package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;
import java.sql.SQLException;

/**
 *
 * @author adrianromero
 * Created on 26 de febrero de 2007, 21:50
 * @param <T>
 *
 */
public abstract class Transaction<T> {
    
    private Session s;
    
    /** Creates a new instance of Transaction
     * @param s */
    public Transaction(Session s) {
        this.s = s;
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public final T execute() throws BasicException {
        
        if (s.isTransaction()) {
            return transact();
        } else {
            try {
                try {    
                    s.begin();
                    T result = transact();
                    s.commit();
                    return result;
                } catch (BasicException e) {
                    s.rollback();
                    throw e;
                }
            } catch (SQLException eSQL) {
                throw new BasicException("Transaction error", eSQL);
            }
        }
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    protected abstract T transact() throws BasicException;
}
