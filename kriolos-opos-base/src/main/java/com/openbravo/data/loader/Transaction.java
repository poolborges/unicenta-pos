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
package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;
import java.sql.SQLException;

/**
 *
 * @author adrianromero Created on 26 de febrero de 2007, 21:50
 * @param <T>
 *
 */
public abstract class Transaction<T> {

    private Session session;

    /**
     * Creates a new instance of Transaction
     *
     * @param session
     */
    public Transaction(Session session) {
        this.session = session;
    }

    /**
     *
     * @return @throws BasicException
     */
    public final T execute() throws BasicException {

        if (session.isTransaction()) {
            return transact();
        } else {
            try {
                session.begin();
                T result = transact();
                session.commit();
                return result;

            } catch (BasicException ex) {
                try {
                    session.rollback();
                } catch (SQLException exSQL) {
                     throw new BasicException("Exception on Transaction rollback", exSQL);
                }
                throw ex;
            } catch (SQLException eSQL) {
                throw new BasicException("Exception on Transaction execute", eSQL);
            }
        }
    }

    /**
     *
     * @return @throws BasicException
     */
    protected abstract T transact() throws BasicException;
}
