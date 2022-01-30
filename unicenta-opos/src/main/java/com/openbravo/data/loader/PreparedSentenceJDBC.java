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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pauloborges
 */
public final class PreparedSentenceJDBC implements SentenceExec {

    private static final Logger LOGGER = Logger.getLogger(PreparedSentenceJDBC.class.getName());

    private final Session session;
    private final String sql;
    private final Datas[] paramsDatas;
    private final int[] indexValue;

    public PreparedSentenceJDBC(Session s, String sentence, Datas[] param, int[] index) {
        session = s;
        sql = sentence;
        paramsDatas = param;
        indexValue = index;
    }

    /**
     *
     * @return @throws BasicException
     */
    public int exec() throws BasicException {
        return exec(null);
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public int exec(Object params) throws BasicException {
        return exec(params);
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public int exec(Object... params) throws BasicException {

        int rowsAffected = 0;
        try ( PreparedStatement preparedStatement = session.getConnection().prepareStatement(sql)) {

            if (params != null) {
                for (int posi = 0; posi < indexValue.length; posi++) {

                    int pindex = indexValue[posi];
                    Datas da = paramsDatas[pindex];
                    Object obj = params[pindex];

                    int paramPosi = posi + 1;
                    if (da.getClassValue() == Double.class) {
                        preparedStatement.setDouble(paramPosi, (Double) obj);
                    } else if (da.getClassValue() == Integer.class) {
                        preparedStatement.setInt(paramPosi, (Integer) obj);
                    } else if (da.getClassValue() == String.class) {
                        preparedStatement.setString(paramPosi, (String) obj);
                    } else if (da.getClassValue() == Boolean.class) {
                        preparedStatement.setBoolean(paramPosi, (Boolean) obj);
                    } else if (da.getClassValue() == Date.class) {
                        preparedStatement.setTimestamp(paramPosi, new Timestamp(((Date) obj).getTime()));
                    } else if (da.getClassValue() == byte[].class) {
                        byte[] ba = (byte[]) obj;
                        InputStream strem = new ByteArrayInputStream(ba);
                        preparedStatement.setBinaryStream(paramPosi, strem, ba.length);
                    }
                }
            }
            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            LOGGER.log(Level.SEVERE, "Exception while execute SQL: " + sql, sqlex);
            throw new BasicException(sqlex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception while execute SQL: " + sql, ex);
            throw new BasicException(ex);
        }

        return rowsAffected;
    }
}
