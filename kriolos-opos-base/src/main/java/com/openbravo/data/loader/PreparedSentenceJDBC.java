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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

/**
 *
 * @author pauloborges
 */
public final class PreparedSentenceJDBC implements SentenceExec {

    protected final static System.Logger LOGGER = System.getLogger(PreparedSentenceJDBC.class.getName());

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
    @Override
    public int exec() throws BasicException {
        int rowsAffected = 0;
        try ( PreparedStatement preparedStatement = session.getConnection().prepareStatement(sql)) {
            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            LOGGER.log(System.Logger.Level.WARNING, "Exception while execute SQL: " + sql, sqlex);
            throw new BasicException(sqlex);
        } catch (Exception ex) {
            LOGGER.log(System.Logger.Level.WARNING, "Exception while execute SQL: " + sql, ex);
            throw new BasicException(ex);
        }

        return rowsAffected;
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    @Override
    public int exec(Object params) throws BasicException {

        int rowsAffected = 0;
        if (params instanceof Object[]) {
            rowsAffected = exec((Object[]) params);
        } else {
            try ( PreparedStatement preparedStatement = session.getConnection().prepareStatement(sql)) {
                int posi = 0;
                int pindex = indexValue[posi];
                Datas da = paramsDatas[pindex];
                Object obj = params;
                int paramPosi = posi + 1;
                
                int parameterCount = preparedStatement.getParameterMetaData().getParameterCount();
                LOGGER.log(System.Logger.Level.DEBUG, "VARIBLES length "
                        + "{params: " + (params == null? "NULL" : params.getClass())
                        + ", indexValue: " + indexValue.length
                        + ", paramsDatas: " + paramsDatas.length 
                        + ", parameterCount:" +parameterCount
                        + "} "+sql);

                preparedStatement(preparedStatement, da, obj, paramPosi);
                rowsAffected = preparedStatement.executeUpdate();
            } catch (SQLException sqlex) {
                LOGGER.log(System.Logger.Level.WARNING, "Exception while execute SQL: " + sql, sqlex);
                throw new BasicException(sqlex);
            } catch (Exception ex) {
                LOGGER.log(System.Logger.Level.WARNING, "Exception while execute SQL: " + sql, ex);
                throw new BasicException(ex);
            }
        }

        return rowsAffected;
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    private int exec(Object[] params) throws BasicException {

        int rowsAffected = 0;
        LOGGER.log(System.Logger.Level.INFO,
                         "{params.length: " + (params == null? "NULL" : params.length)
                        + ", indexValue.length: " + indexValue.length
                        + ", paramsDatas.length: " + paramsDatas.length
                        + "} "+sql);
        LOGGER.log(System.Logger.Level.INFO, "SQL: " +sql);
        try ( PreparedStatement preparedStatement = session.getConnection().prepareStatement(sql)) {

            int parameterCount = preparedStatement.getParameterMetaData().getParameterCount();
            

                
   
            for (int posi = 0; posi < paramsDatas.length; posi++) {

                int pindex;
                /*
                if(indexValue.length == 1){
                    pindex = posi;
                }else {
                   pindex = indexValue[posi];
                }
                */
                pindex = indexValue[posi];
                
                Datas da = paramsDatas[pindex];
                Object obj = params[pindex];

                int paramPosi = posi + 1;
                preparedStatement(preparedStatement, da, obj, paramPosi);
            }
            
            LOGGER.log(System.Logger.Level.INFO, "PreparedStatement: " + preparedStatement.toString());
            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            LOGGER.log(System.Logger.Level.WARNING, "Exception while execute SQL: " + sql, sqlex);
            throw new BasicException(sqlex);
        } catch (Exception ex) {
            LOGGER.log(System.Logger.Level.WARNING, "Exception while execute SQL: " + sql, ex);
            throw new BasicException(ex);
        }

        return rowsAffected;
    }

    private void preparedStatement(PreparedStatement preparedStatement, Datas da, Object obj, int paramPosi)
            throws SQLException {

        LOGGER.log(System.Logger.Level.DEBUG, "PreparedStatement :: VARIBLES{ "
                + "paramPosi: " + paramPosi
                + ", Datas: " + da.getClassValue()
                + ", obj: " + (obj==null? "NULL" : obj.toString())
                + ", obj: " + (obj==null? "UNKNOW" : obj.getClass()) + "}");
        if (da.getClassValue() == Double.class) {
            if(obj == null){
                preparedStatement.setNull(paramPosi, Types.DOUBLE);
            }else{
                preparedStatement.setDouble(paramPosi, (Double) obj);
            }
        } else if (da.getClassValue() == Integer.class) {
            if(obj == null){
                preparedStatement.setNull(paramPosi, Types.INTEGER);
            }else{
                preparedStatement.setInt(paramPosi, (Integer) obj);
            }
        } else if (da.getClassValue() == String.class) {
            if(obj == null){
                preparedStatement.setNull(paramPosi, Types.VARCHAR);
            }else{
                preparedStatement.setString(paramPosi, (String) obj);
            }
        } else if (da.getClassValue() == Boolean.class) {
            if(obj == null){
                preparedStatement.setNull(paramPosi, Types.BOOLEAN);
            }else{
                preparedStatement.setBoolean(paramPosi, (Boolean) obj);
            }
        } else if (da.getClassValue() == Date.class) {
            if(obj == null){
                preparedStatement.setNull(paramPosi, Types.TIMESTAMP);
            }else{
                preparedStatement.setTimestamp(paramPosi, new Timestamp(((Date) obj).getTime()));
            }
        } else if (da.getClassValue() == byte[].class) {
            if(obj == null){
                preparedStatement.setNull(paramPosi, Types.BINARY);
            }else{
                byte[] ba = (byte[]) obj;
                InputStream strem = new ByteArrayInputStream(ba);
                preparedStatement.setBinaryStream(paramPosi, strem);
            }
        } else if (da.getClassValue() == BufferedImage.class) {
            if(obj == null){
                preparedStatement.setNull(paramPosi, Types.BINARY);
            }else{
                byte[] ba = ImageUtils.writeImage((BufferedImage) obj);
                InputStream strem = new ByteArrayInputStream(ba);
                preparedStatement.setBinaryStream(paramPosi, strem);
            }
        }
    }
}
