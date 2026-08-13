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
package com.openbravo.pos.pim;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.SerializerWriteString;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.forms.BeanFactoryDataSingle;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author poolborges
 */
public class DataLogicPIM extends BeanFactoryDataSingle {

    private static final Logger LOGGER = Logger.getLogger(BeanFactoryDataSingle.class.getName());

    private Session sessionDB;

    @Override
    public void init(Session sessionDB) {
        this.sessionDB = sessionDB;
    }

    /**
     *
     * @param id
     * @return
     * @throws BasicException
     */
    public final CategoryInfo getCategoryInfo(String id) throws BasicException {
        return new PreparedSentence<String, CategoryInfo>(sessionDB,
                "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "CATORDER, "
                + "CATALOGCOLOR, "
                + "CATALOGENABLED "
                + "FROM categories "
                + "WHERE ID = ? "
                + "ORDER BY CATORDER, NAME",
                SerializerWriteString.INSTANCE,
                CategoryInfo.getSerializerRead()).find(id);
    }

    /**
     * @deprecated since Nov/2025
     * @return
     */
    public final SentenceList<CategoryInfo> getCategoriesList() {
        return new StaticSentence(sessionDB,
                "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "CATORDER, "
                + "CATALOGCOLOR, "
                + "CATALOGENABLED "
                + "FROM categories "
                + "ORDER BY NAME",
                null,
                CategoryInfo.getSerializerRead());
    }

    public final List<CategoryInfo> getCategoriesListAll() {
        List<CategoryInfo> list = null;
        try {
            list = this.getCategoriesList().list();
        } catch (BasicException ex) {
            LOGGER.log(Level.WARNING, "Cannot get categories list", ex);
        }
        return list;
    }

    /**
     * JG Feb 2017 Returns all PARENT categories
     *
     * @return
     */
    public final SentenceList<CategoryInfo> getCategoriesList_1() {
        return new StaticSentence(sessionDB,
                "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "CATORDER, "
                + "CATALOGCOLOR, "
                + "CATALOGENABLED "
                + "FROM categories "
                + "WHERE PARENTID IS NULL "
                + "ORDER BY NAME",
                null,
                CategoryInfo.getSerializerRead());
    }

    /**
     *
     * @return @throws BasicException
     */
    public final List<CategoryInfo> getRootCategories() throws BasicException {
        return new PreparedSentence<Void, CategoryInfo>(sessionDB,
                "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "CATORDER, "
                + "CATALOGCOLOR, "
                + "CATALOGENABLED "
                + "FROM categories "
                + "WHERE PARENTID IS NULL AND CATSHOWNAME = " + sessionDB.DB.TRUE()
                + " "
                + "ORDER BY CATORDER, NAME",
                null,
                CategoryInfo.getSerializerRead()).list();
    }

    /**
     *
     * @param category ID
     * @return
     * @throws BasicException
     */
    public final List<CategoryInfo> getSubcategories(String category) throws BasicException {
        return new PreparedSentence<String, CategoryInfo>(sessionDB,
                "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "CATORDER, "
                + "CATALOGCOLOR, "
                + "CATALOGENABLED "
                + "FROM categories "
                + "WHERE PARENTID = ? "
                + "ORDER BY CATORDER, NAME",
                SerializerWriteString.INSTANCE,
                CategoryInfo.getSerializerRead()).list(category);
    }
}
