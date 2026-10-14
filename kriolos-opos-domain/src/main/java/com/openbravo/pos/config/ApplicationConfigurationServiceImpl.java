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
import com.openbravo.app.AppProperties;
import com.openbravo.app.BootstrapConfiguration;
import com.openbravo.pos.container.BeanFactoryApp;
import com.openbravo.pos.container.BeanFactoryException;
import com.openbravo.pos.panels.AppView;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Production implementation of {@link ApplicationConfigurationService}.
 * <p>
 * Manages database persistence via {@link DataLogicConfiguration} and provides
 * low-latency thread-safe cached reads with hierarchical fallback:
 * Station UUID -> Global (0000-0000-0000-0000) -> BootstrapConfiguration.
 *
 * @author KriolOS POS
 */
public class ApplicationConfigurationServiceImpl implements ApplicationConfigurationService, BeanFactoryApp {

    private static final Logger LOGGER = Logger.getLogger(ApplicationConfigurationServiceImpl.class.getName());

    public static final String GLOBAL_POS_ID = BootstrapConfiguration.GLOBAL_POS_ID;

    private DataLogicConfiguration dataLogic;
    private BootstrapConfiguration bootstrapConfig;
    private String stationId;

    /**
     * In-memory hierarchical cache: pos_id -> (config_key -> config_value).
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> cache = new ConcurrentHashMap<>();

    public ApplicationConfigurationServiceImpl() {
    }

    public ApplicationConfigurationServiceImpl(DataLogicConfiguration dataLogic, BootstrapConfiguration bootstrapConfig) {
        this.dataLogic = dataLogic;
        this.bootstrapConfig = bootstrapConfig;
        this.stationId = (bootstrapConfig != null) ? bootstrapConfig.getStationId() : "0000-0000-0000-0000";
        try {
            reloadCache();
        } catch (BasicException e) {
            LOGGER.log(Level.WARNING, "Failed to load configuration cache during construction: " + e.getMessage(), e);
        }
    }

    @Override
    public void init(AppView app) throws BeanFactoryException {
        AppProperties props = app.getProperties();
        if (props instanceof BootstrapConfiguration bConfig) {
            this.bootstrapConfig = bConfig;
        } else {
            this.bootstrapConfig = AppConfig.getInstance();
        }
        this.stationId = this.bootstrapConfig.getStationId();

        this.dataLogic = new DataLogicConfiguration();
        this.dataLogic.init(app.getSession());

        try {
            reloadCache();
            seedGlobalDefaultsIfEmpty();
        } catch (BasicException e) {
            LOGGER.log(Level.WARNING, "Could not initialize database configuration cache: " + e.getMessage(), e);
        }
    }

    @Override
    public Object getBean() {
        return this;
    }

    @Override
    public String getProperty(String key) {
        return getProperty(key, null);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return getStationProperty(stationId, key, defaultValue);
    }

    @Override
    public String getStationProperty(String posId, String key) {
        return getStationProperty(posId, key, null);
    }

    @Override
    public String getStationProperty(String posId, String key, String defaultValue) {
        Objects.requireNonNull(key, "Property key cannot be null");

        // 1. Station-specific lookup
        if (posId != null) {
            ConcurrentHashMap<String, String> stationMap = cache.get(posId);
            if (stationMap != null && stationMap.containsKey(key)) {
                return stationMap.get(key);
            }
        }

        // 2. Global lookup (if posId was not already GLOBAL_POS_ID)
        if (!GLOBAL_POS_ID.equals(posId)) {
            ConcurrentHashMap<String, String> globalMap = cache.get(GLOBAL_POS_ID);
            if (globalMap != null && globalMap.containsKey(key)) {
                return globalMap.get(key);
            }
        }

        // 3. Fallback to BootstrapConfiguration or defaultValue
        if (bootstrapConfig != null) {
            return bootstrapConfig.getProperty(key, defaultValue);
        }

        return defaultValue;
    }

    @Override
    public Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String val = getProperty(key);
        return (val != null) ? Boolean.parseBoolean(val) : defaultValue;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        String val = getProperty(key);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void setProperty(String key, String value) throws BasicException {
        setProperty(stationId, key, value);
    }

    @Override
    public void setGlobalProperty(String key, String value) throws BasicException {
        setProperty(GLOBAL_POS_ID, key, value);
    }

    @Override
    public void setProperty(String posId, String key, String value) throws BasicException {
        Objects.requireNonNull(posId, "posId cannot be null");
        Objects.requireNonNull(key, "key cannot be null");

        if (dataLogic != null) {
            dataLogic.saveProperty(posId, key, value);
        }

        // Atomic cache update
        cache.computeIfAbsent(posId, k -> new ConcurrentHashMap<>()).put(key, value);
        LOGGER.log(Level.FINE, "Saved property [{0}:{1}={2}]", new Object[]{posId, key, value});
    }

    @Override
    public void removeProperty(String posId, String key) throws BasicException {
        Objects.requireNonNull(posId, "posId cannot be null");
        Objects.requireNonNull(key, "key cannot be null");

        if (dataLogic != null) {
            dataLogic.deleteProperty(posId, key);
        }

        cache.computeIfPresent(posId, (k, map) -> {
            map.remove(key);
            return map;
        });
    }

    @Override
    public Map<String, String> getAllProperties(String posId) throws BasicException {
        Objects.requireNonNull(posId, "posId cannot be null");
        ConcurrentHashMap<String, String> map = cache.get(posId);
        if (map == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Map<String, String> getAllActiveStationProperties() throws BasicException {
        return getAllProperties(stationId);
    }

    @Override
    public Map<String, String> getAllGlobalProperties() throws BasicException {
        return getAllProperties(GLOBAL_POS_ID);
    }

    @Override
    public synchronized void reloadCache() throws BasicException {
        if (dataLogic == null) {
            return;
        }

        List<DataLogicConfiguration.ConfigPropertyEntry> entries = dataLogic.readAll();
        cache.clear();

        for (DataLogicConfiguration.ConfigPropertyEntry entry : entries) {
            if (entry.posId() != null && entry.key() != null) {
                cache.computeIfAbsent(entry.posId(), k -> new ConcurrentHashMap<>())
                        .put(entry.key(), entry.value() != null ? entry.value() : "");
            }
        }
        LOGGER.log(Level.INFO, "Loaded {0} configuration entries into ApplicationConfigurationService cache.", entries.size());
    }

    @Override
    public String getStationId() {
        return stationId;
    }

    /**
     * Seeds global settings from the bootstrap configuration if the database has zero records.
     */
    private void seedGlobalDefaultsIfEmpty() throws BasicException {
        if (!cache.isEmpty() || bootstrapConfig == null || dataLogic == null) {
            return;
        }

        LOGGER.info("Table config_properties is empty. Seeding initial global defaults from bootstrap configuration...");

        String[] companyKeys = {
            "company.name", "company.taxid", "company.address", "company.phone",
            "currency.symbol", "currency.code", "tax.calculation_mode",
            "tkt.header1", "tkt.header2", "tkt.header3", "tkt.header4",
            "tkt.footer1", "tkt.footer2", "tkt.footer3"
        };

        for (String key : companyKeys) {
            String val = bootstrapConfig.getProperty(key);
            if (val != null && !val.isBlank()) {
                setGlobalProperty(key, val);
            }
        }
    }
}
