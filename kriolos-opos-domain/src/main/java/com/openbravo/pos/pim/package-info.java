/*
 * Copyright (C) 2026 KriolOS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Provides the Product Information Management (PIM) architecture and data structures 
 * for the KriolOS POS system.
 * 
 * <p>This package manages core inventory and catalog entities, including:</p>
 * <ul>
 *   <li>{@link com.openbravo.pos.pim.Product} - Core item definitions, barcodes, and pricing.</li>
 *   <li>{@link com.openbravo.pos.pim.Category} - Hierarchical product classification trees.</li>
 *   <li>{@link com.openbravo.pos.pim.UOM} - Units of measurement for inventory tracking.</li>
 *   <li>{@link com.openbravo.pos.pim.Attribute} - Dynamic item features and variant properties.</li>
 * </ul>
 *
 * @since 10.0.0
 */
package com.openbravo.pos.pim;
