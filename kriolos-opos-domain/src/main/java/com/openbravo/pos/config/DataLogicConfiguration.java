//    KriolOS POS
//    Copyright (c) 2019-2026 KriolOS
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
package com.openbravo.pos.config;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SentenceExec;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.SerializerWriteString;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.container.BeanFactoryDataSingle;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data access logic for the {@code config_properties} table.
 *
 * @author KriolOS POS
 */
public class DataLogicConfiguration extends BeanFactoryDataSingle {

    private static final Logger LOGGER = Logger.getLogger(DataLogicConfiguration.class.getName());

    public record ConfigPropertyEntry(String posId, String key, String value) {}

    protected Session sessionDB;

    public DataLogicConfiguration() {
    }

    @Override
    public void init(Session s) {
        this.sessionDB = s;
        if (s != null) {
            try {
                ensureTableExists();
            } catch (BasicException e) {
                LOGGER.log(Level.WARNING, "Could not verify or create config_properties table: " + e.getMessage(), e);
            }
        }
    }

    public synchronized void ensureTableExists() throws BasicException {
        if (sessionDB == null) {
            return;
        }

        try {
            Connection conn = sessionDB.getConnection();
            DatabaseMetaData meta = conn.getMetaData();

            boolean exists = false;
            try (ResultSet rs = meta.getTables(null, null, "CONFIG_PROPERTIES", null)) {
                if (rs.next()) {
                    exists = true;
                }
            }
            if (!exists) {
                try (ResultSet rs = meta.getTables(null, null, "config_properties", null)) {
                    if (rs.next()) {
                        exists = true;
                    }
                }
            }

            if (!exists) {
                LOGGER.info("Table config_properties not found. Auto-creating table...");
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(
                            "CREATE TABLE config_properties ("
                            + "pos_id VARCHAR(36) NOT NULL, "
                            + "config_key VARCHAR(128) NOT NULL, "
                            + "config_value VARCHAR(4096), "
                            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                            + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                            + "PRIMARY KEY (pos_id, config_key))"
                    );
                }
                LOGGER.info("Table config_properties created successfully.");
            }
        } catch (SQLException e) {
            throw new BasicException("Failed to verify/create table config_properties: " + e.getMessage(), e);
        }
    }

    public List<ConfigPropertyEntry> readAll() throws BasicException {
        SentenceList<ConfigPropertyEntry> listSentence = new StaticSentence<>(
                sessionDB,
                "SELECT pos_id, config_key, config_value FROM config_properties",
                null,
                (DataRead dr) -> new ConfigPropertyEntry(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3)
                )
        );
        return listSentence.list();
    }

    public List<ConfigPropertyEntry> readByPosId(String posId) throws BasicException {
        PreparedSentence<String, ConfigPropertyEntry> sentence = new PreparedSentence<>(
                sessionDB,
                "SELECT pos_id, config_key, config_value FROM config_properties WHERE pos_id = ?",
                SerializerWriteString.INSTANCE,
                (DataRead dr) -> new ConfigPropertyEntry(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3)
                )
        );
        return sentence.list(posId);
    }

    public void saveProperty(String posId, String key, String value) throws BasicException {
        // Try portable UPDATE first
        SentenceExec updateSentence = new PreparedSentence<>(
                sessionDB,
                "UPDATE config_properties SET config_value = ?, updated_at = CURRENT_TIMESTAMP WHERE pos_id = ? AND config_key = ?",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING})
        );

        int updated = updateSentence.exec(new Object[]{value, posId, key});
        if (updated == 0) {
            // Row didn't exist; perform INSERT
            SentenceExec insertSentence = new PreparedSentence<>(
                    sessionDB,
                    "INSERT INTO config_properties (pos_id, config_key, config_value, created_at, updated_at) "
                    + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                    new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING})
            );
            insertSentence.exec(new Object[]{posId, key, value});
        }
    }

    public void deleteProperty(String posId, String key) throws BasicException {
        SentenceExec deleteSentence = new PreparedSentence<>(
                sessionDB,
                "DELETE FROM config_properties WHERE pos_id = ? AND config_key = ?",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING})
        );
        deleteSentence.exec(new Object[]{posId, key});
    }

    public void deleteAll() throws BasicException {
        SentenceExec deleteSentence = new StaticSentence<>(
                sessionDB,
                "DELETE FROM config_properties"
        );
        deleteSentence.exec();
    }
}
