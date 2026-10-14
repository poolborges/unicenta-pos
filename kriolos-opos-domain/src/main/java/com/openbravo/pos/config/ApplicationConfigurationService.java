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
import java.util.Map;

/**
 * Service contract for Tier 2 database-backed application configuration.
 * <p>
 * Implements hierarchical property resolution:
 * <ol>
 *   <li>Station-specific override: {@code pos_id = {stationId}}</li>
 *   <li>Global store setting: {@code pos_id = '0000-0000-0000-0000'}</li>
 *   <li>Bootstrap fallback: {@code BootstrapConfiguration.getProperty(key)}</li>
 * </ol>
 *
 * @author KriolOS POS
 */
public interface ApplicationConfigurationService {

    /**
     * Resolves a property using hierarchical fallback (Station -> Global -> Bootstrap).
     *
     * @param key the property key
     * @return the resolved property value, or null if not found
     */
    String getProperty(String key);

    /**
     * Resolves a property using hierarchical fallback with a default value.
     *
     * @param key the property key
     * @param defaultValue the default fallback value
     * @return the resolved property value or defaultValue
     */
    String getProperty(String key, String defaultValue);

    /**
     * Resolves a property for an explicit POS station UUID without fallback to other stations.
     * Still falls back to Global (0000-0000-0000-0000) and Bootstrap if not set for posId.
     *
     * @param posId the station UUID (or {@code "0000-0000-0000-0000"} for global)
     * @param key the property key
     * @return the resolved property value, or null
     */
    String getStationProperty(String posId, String key);

    /**
     * Resolves a property for an explicit POS station UUID with a default value.
     *
     * @param posId the station UUID (or {@code "0000-0000-0000-0000"} for global)
     * @param key the property key
     * @param defaultValue the default value
     * @return the resolved property value or defaultValue
     */
    String getStationProperty(String posId, String key, String defaultValue);

    /**
     * Resolves a boolean property using hierarchical fallback.
     *
     * @param key the property key
     * @return boolean value
     */
    Boolean getBoolean(String key);

    /**
     * Resolves a boolean property with default value.
     *
     * @param key the property key
     * @param defaultValue default value
     * @return boolean value or default
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * Resolves an integer property with default value.
     *
     * @param key the property key
     * @param defaultValue default value
     * @return parsed int or default
     */
    int getInt(String key, int defaultValue);

    /**
     * Sets or updates a property scoped to the current station UUID.
     *
     * @param key the property key
     * @param value the property value
     * @throws BasicException if database update fails
     */
    void setProperty(String key, String value) throws BasicException;

    /**
     * Sets or updates a global property (applicable to all POS terminals unless overridden).
     *
     * @param key the property key
     * @param value the property value
     * @throws BasicException if database update fails
     */
    void setGlobalProperty(String key, String value) throws BasicException;

    /**
     * Sets or updates a property for an explicit POS station or global scope.
     *
     * @param posId the target station UUID or {@code "0000-0000-0000-0000"}
     * @param key the property key
     * @param value the property value
     * @throws BasicException if database update fails
     */
    void setProperty(String posId, String key, String value) throws BasicException;

    /**
     * Removes a property for a specific station or global scope.
     *
     * @param posId the target station UUID or {@code "0000-0000-0000-0000"}
     * @param key the property key
     * @throws BasicException if database deletion fails
     */
    void removeProperty(String posId, String key) throws BasicException;

    /**
     * Returns all stored properties for a specific posId.
     *
     * @param posId the station UUID or global UUID
     * @return immutable map of properties
     * @throws BasicException if database access fails
     */
    Map<String, String> getAllProperties(String posId) throws BasicException;

    /**
     * Returns all properties scoped to the active station UUID.
     *
     * @return map of station properties
     * @throws BasicException if database access fails
     */
    Map<String, String> getAllActiveStationProperties() throws BasicException;

    /**
     * Returns all global properties (pos_id = '0000-0000-0000-0000').
     *
     * @return map of global properties
     * @throws BasicException if database access fails
     */
    Map<String, String> getAllGlobalProperties() throws BasicException;

    /**
     * Forces an immediate reload of the in-memory cache from the database.
     *
     * @throws BasicException if database load fails
     */
    void reloadCache() throws BasicException;

    /**
     * Returns the active POS station UUID.
     *
     * @return active station UUID
     */
    String getStationId();
}
