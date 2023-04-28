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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.sql.DataSource;

/**
 *
 * @author adrianromero Created on February 6, 2007, 4:06 PM
 * @author poolborges updated 2023-04-06
 */
public final class Session {

    private final static Logger LOGGER = Logger.getLogger(Session.class.getName());

    private final FakeDataSouce fakeDS;

    private Connection mConnection;
    private boolean m_bInTransaction;

    private final DataSource datasource;
    public final SessionDB DB;

    /**
     * Creates a new instance of Session
     *
     * @param url
     * @param user
     * @param password
     * @throws java.sql.SQLException
     */
    public Session(String url, String user, String password) throws SQLException {
        this.datasource = null;
        fakeDS = new FakeDataSouce(url, user, password);

        mConnection = null;
        m_bInTransaction = false;

        connect(); // no lazy connection

        DB = getDiff();
    }

    public Session(DataSource ds) throws SQLException {

        this.datasource = ds;
        this.fakeDS = null;

        mConnection = null;
        m_bInTransaction = false;

        connect(); // no lazy connection

        DB = getDiff();
    }

    /**
     * Open Connection to datasource
     *
     * @throws SQLException
     */
    public void connect() throws SQLException {

        if (this.datasource != null) {
            this.mConnection = datasource.getConnection();
        } else {
            this.mConnection = fakeDS.getConnection();
        }

        mConnection.setAutoCommit(true);
        m_bInTransaction = false;
    }

    /**
     * Close Connection
     *
     * @throws java.sql.SQLException
     */
    public void close() throws SQLException {

        if (mConnection != null) {
            try {
                if (m_bInTransaction) {
                    m_bInTransaction = false; // lo primero salimos del estado
                    mConnection.rollback();
                    mConnection.setAutoCommit(true);
                }
                mConnection.close();
            } catch (SQLException e) {
                throw new SQLException(e);
            } finally {
                mConnection = null;
            }
        }
    }

    /**
     * Get opened Connections
     *
     * @return @throws SQLException
     */
    public Connection getConnection() throws SQLException {

        if (!m_bInTransaction) {
            ensureConnection();
        }
        return mConnection;
    }

    /**
     * Begin Transaction
     *
     * @throws SQLException
     */
    public void begin() throws SQLException {
        try {
            if (m_bInTransaction) {
                throw new SQLException("Transaction already started");
            } else {
                ensureConnection();
                mConnection.setAutoCommit(false);
                m_bInTransaction = true;
            }
        } catch (SQLException ex) {
            throw new SQLException(ex);
        } finally {
            mConnection.setAutoCommit(true);
        }
    }

    /**
     * Commit Transaction
     *
     * @throws SQLException
     */
    public void commit() throws SQLException {
        try {
            if (m_bInTransaction) {
                m_bInTransaction = false; // lo primero salimos del estado
                mConnection.commit();
                mConnection.setAutoCommit(true);
            } else {
                throw new SQLException("Transaction not started");
            }
        } catch (SQLException ex) {
            throw new SQLException(ex);
        } finally {
            mConnection.setAutoCommit(true);
        }
    }

    /**
     * Rollback Transaction
     *
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        try {
            if (m_bInTransaction) {
                m_bInTransaction = false; // lo primero salimos del estado
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } else {
                throw new SQLException("Transaction not started");
            }
        } catch (SQLException ex) {
            throw new SQLException(ex);
        } finally {
            mConnection.setAutoCommit(true);
        }
    }

    /**
     * is in Transaction
     *
     * @return
     */
    public boolean isTransaction() {
        return m_bInTransaction;
    }

    /**
     * Only to be used internal, when in manual transaction
     *
     * @throws SQLException
     */
    private void ensureConnection() throws SQLException {
        boolean bclosed;
        try {
            bclosed = mConnection == null || mConnection.isClosed();
        } catch (SQLException ex) {
            bclosed = true;
            throw new SQLException(ex);
        }
        // reconnect if closed
        if (bclosed) {
            connect();
        }
    }

    /**
     * Get URL
     *
     * @return @throws SQLException
     */
    public String getURL() throws SQLException {
        return getConnection().getMetaData().getURL();
    }

    private SessionDB getDiff() throws SQLException {

        String dbDriver = getConnection().getMetaData().getDriverName();
        String sdbmanager = getConnection().getMetaData().getDatabaseProductName();
        LOGGER.log(Level.INFO, "DB Session for DatabaseProductName: " + sdbmanager + "; Driver: " + dbDriver);
        switch (sdbmanager) {
            case "HSQL Database Engine":
                return new SessionDBHSQLDB();
            case "MariaDB":
            case "MySQL":
                return new SessionDBMySQL();
            case "PostgreSQL":
                return new SessionDBPostgreSQL();
            case "Oracle":
                return new SessionDBOracle();
            case "Apache Derby":
                return new SessionDBDerby();
            default:
                return new SessionDBGeneric(sdbmanager);
        }
    }

    private class FakeDataSouce {

        private final String m_surl;
        private final String m_sappuser;
        private final String m_spassword;

        public FakeDataSouce(String url, String user, String password) throws SQLException {
            m_surl = url;
            m_sappuser = user;
            m_spassword = password;
        }

        public Connection getConnection() throws SQLException {

            return (m_sappuser == null && m_spassword == null)
                    ? DriverManager.getConnection(m_surl)
                    : DriverManager.getConnection(m_surl, m_sappuser, m_spassword);
        }
    }
}
