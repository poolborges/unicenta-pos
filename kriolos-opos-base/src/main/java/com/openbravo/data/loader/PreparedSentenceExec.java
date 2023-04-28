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
public class PreparedSentenceExec<W extends Object, T> extends JDBCBaseSentence<T> {

    protected final static System.Logger LOGGER = System.getLogger(PreparedSentenceExec.class.getName());

    private final String sql;
    //private PrepareStatementWriter preBuilder;
    private final SerializerWrite serwrite;
    private final SerializerRead<T> serread;
    private PreparedStatement preparedStatement;

    public PreparedSentenceExec(Session session, String sqlSentence, Datas[] param, int[] index) {
        super(session);
        this.sql = sqlSentence;
        serwrite = new SerializerWriteBasicExt(param, index);
        serread = null;
    }

    public PreparedSentenceExec(Session session, String sqlSentence, SerializerWrite serwrite) {
        super(session);
        this.sql = sqlSentence;
        //this.preBuilder = new PrepareStatementWriter(param, index);
        this.serwrite = serwrite;
        serread = null;
    }

    public PreparedSentenceExec(Session session, String sqlSentence, SerializerWrite serwrite, SerializerRead<T> serread) {
        super(session);
        this.sql = sqlSentence;
        //this.preBuilder = new PrepareStatementWriter(param, index);
        this.serwrite = serwrite;
        this.serread = serread;
    }
    
    public PreparedSentenceExec(Session session, String sqlSentence,SerializerRead<T> serread) {
        super(session);
        this.sql = sqlSentence;
        //this.preBuilder = new PrepareStatementWriter(param, index);
        this.serwrite = null;
        this.serread = serread;
    }

    /**
     *
     * @return @throws BasicException
     */
    private int exec1() throws BasicException {
        int rowsAffected = 0;
        try (PreparedStatement preparedStatement = session.getConnection().prepareStatement(sql)) {
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
     * @param param
     * @return
     * @throws BasicException
     */
    private int exec2(Object param) throws BasicException {
        int rowsAffected = 0;

        if (param instanceof Object[]) {
            rowsAffected = exec((Object[]) param);
        } else {
            try (PreparedStatement preparedStatement = session.getConnection().prepareStatement(sql)) {
                //preBuilder.prepare(preparedStatement, param);
                if (serwrite != null) {
                    serwrite.writeValues(new PreparedSentenceDataWrite(preparedStatement), param);
                }

                rowsAffected = preparedStatement.executeUpdate();
            } catch (SQLException sqlex) {
                LOGGER.log(System.Logger.Level.WARNING, "Exception while execute SQL: " + sql, sqlex);
                throw new BasicException(sqlex);
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

        LOGGER.log(System.Logger.Level.INFO, "SQL: " + sql);
        try (PreparedStatement preparedStatement = session.getConnection().prepareStatement(sql)) {
            if (serwrite != null) {
                serwrite.writeValues(new PreparedSentenceDataWrite(preparedStatement), params);
            }
            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            LOGGER.log(System.Logger.Level.WARNING, "Exception while execute SQL: " + sql, sqlex);
            throw new BasicException(sqlex);
        }

        return rowsAffected;
    }

    @Override
    public DataResultSet<T> moreResults() throws BasicException {
        DataResultSet<T> result = null;
        try {
            if (preparedStatement != null) {
                if (preparedStatement.getMoreResults()) {
                    result = new JDBCDataResultSet<>(preparedStatement.getResultSet(), serread);
                } else {
                    int resCount = preparedStatement.getUpdateCount();
                    if (resCount < 0) {
                        result = null;
                    } else {
                        result = new UpdateDataResultSet<>(resCount);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(System.Logger.Level.ERROR, "Exception while execute moreResult: ", ex);
            throw new BasicException(ex);
        }

        return result;
    }

    @Override
    public void closeExec() throws BasicException {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException ex) {
                throw new BasicException(ex);
            } finally {
                preparedStatement = null;
            }
        }
    }

    @Override
    public DataResultSet<T> openExec(Object params) throws BasicException {
        DataResultSet<T> result;
        LOGGER.log(System.Logger.Level.INFO, "SQL: " + sql);
        try {
            preparedStatement = session.getConnection().prepareStatement(sql);
            if (serwrite != null) {
                serwrite.writeValues(new PreparedSentenceDataWrite(preparedStatement), params);
            }
            if (preparedStatement.execute()) {
                result = new JDBCDataResultSet<>(preparedStatement.getResultSet(), serread);
            } else {
                int resCount = preparedStatement.getUpdateCount();
                if (resCount < 0) {
                    result = null;
                } else {
                    result = new UpdateDataResultSet<>(resCount);
                }
            }
        } catch (SQLException sqlex) {
            LOGGER.log(System.Logger.Level.WARNING, "Exception while execute openExec with SQL: " + sql, sqlex);
            throw new BasicException(sqlex);
        }

        return result;
    }

    private final static class PrepareStatementWriter {

        private final Datas[] paramsDataType;
        private final int[] paramValueIndex;

        public PrepareStatementWriter(Datas[] paramsDataType, int[] paramValueIndex) {

            this.paramsDataType = paramsDataType;
            this.paramValueIndex = paramValueIndex;
        }

        public void prepare(PreparedStatement preparedStatement, Object param) throws SQLException {
            int posi = 0;
            int pindex = paramValueIndex[posi];
            Datas da = paramsDataType[pindex];
            int paramPosi = posi + 1;
            int parameterCount = preparedStatement.getParameterMetaData().getParameterCount();

            LOGGER.log(System.Logger.Level.WARNING, "Prepare statement has params total: " + parameterCount);
            preparedStatement(preparedStatement, da, param, paramPosi);
        }

        public void prepare(PreparedStatement preparedStatement, Object[] params) throws SQLException {
            int paramsSize = (params == null) ? 0 : params.length;
            int parameterCount = preparedStatement.getParameterMetaData().getParameterCount();
            LOGGER.log(System.Logger.Level.WARNING, "Prepare statement has params total: " + parameterCount);
            if (paramsSize != parameterCount) {
                //TODO MUST COMPARE PARAMETER AND THROW EXCEPTION
                //throw new BasicException("SQL statement missing paramters: ");
            }
            for (int posi = 0; posi < parameterCount; posi++) {

                int pindex = paramValueIndex[posi];

                Datas da = paramsDataType[pindex];
                Object obj = params[pindex];

                int paramPosi = posi + 1;
                preparedStatement(preparedStatement, da, obj, paramPosi);
            }

            String logSql = preparedStatement.toString();
            LOGGER.log(System.Logger.Level.INFO, "PreparedStatement parameterCount" + parameterCount + "; SQL:" + logSql);
        }

        public static void preparedStatement(PreparedStatement preparedStatement, Datas da, Object obj, int paramPosi)
                throws SQLException {

            LOGGER.log(System.Logger.Level.DEBUG, "PreparedStatement :: VARIBLES{ "
                    + "paramPosi: " + paramPosi
                    + ", Datas: " + da.getClassValue()
                    + ", obj: " + (obj == null ? "NULL" : obj.toString())
                    + ", obj: " + (obj == null ? "UNKNOW" : obj.getClass()) + "}");
            if (da.getClassValue() == Double.class) {
                if (obj == null) {
                    preparedStatement.setNull(paramPosi, Types.DOUBLE);
                } else {
                    preparedStatement.setDouble(paramPosi, (Double) obj);
                }
            } else if (da.getClassValue() == Integer.class) {
                if (obj == null) {
                    preparedStatement.setNull(paramPosi, Types.INTEGER);
                } else {
                    preparedStatement.setInt(paramPosi, (Integer) obj);
                }
            } else if (da.getClassValue() == String.class) {
                if (obj == null) {
                    preparedStatement.setNull(paramPosi, Types.VARCHAR);
                } else {
                    preparedStatement.setString(paramPosi, (String) obj);
                }
            } else if (da.getClassValue() == Boolean.class) {
                if (obj == null) {
                    preparedStatement.setNull(paramPosi, Types.BOOLEAN);
                } else {
                    preparedStatement.setBoolean(paramPosi, (Boolean) obj);
                }
            } else if (da.getClassValue() == Date.class) {
                if (obj == null) {
                    preparedStatement.setNull(paramPosi, Types.TIMESTAMP);
                } else {
                    preparedStatement.setTimestamp(paramPosi, new Timestamp(((Date) obj).getTime()));
                }
            } else if (da.getClassValue() == byte[].class) {
                if (obj == null) {
                    preparedStatement.setNull(paramPosi, Types.BINARY);
                } else {
                    byte[] ba = (byte[]) obj;
                    InputStream strem = new ByteArrayInputStream(ba);
                    preparedStatement.setBinaryStream(paramPosi, strem);
                }
            } else if (da.getClassValue() == BufferedImage.class) {
                if (obj == null) {
                    preparedStatement.setNull(paramPosi, Types.BINARY);
                } else {
                    byte[] ba = ImageUtils.writeImage((BufferedImage) obj);
                    InputStream strem = new ByteArrayInputStream(ba);
                    preparedStatement.setBinaryStream(paramPosi, strem);
                }
            } else if (da.getClassValue() == Object.class && da == Datas.SERIALIZABLE) {
                if (obj == null) {
                    preparedStatement.setNull(paramPosi, Types.BINARY);
                } else {
                    /*
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream bos = new ObjectOutputStream(baos);
                    bos.writeObject(obj);
                    byte[] saves = baos.toByteArray();
                     */
                    byte[] saves = ImageUtils.writeSerializable(obj);
                    InputStream bais = new ByteArrayInputStream(saves);

                    preparedStatement.setBinaryStream(paramPosi, bais);
                }
            }
        }

    }
}
