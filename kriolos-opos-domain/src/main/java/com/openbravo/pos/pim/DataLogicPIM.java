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
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.PreparedSentenceExec;
import com.openbravo.data.loader.QBFBuilder;
import com.openbravo.data.loader.SentenceExec;
import com.openbravo.data.loader.SentenceExecTransaction;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.SerializerWriteBasicExt;
import com.openbravo.data.loader.SerializerWriteString;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.data.model.Field;
import com.openbravo.data.model.Row;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
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

    // <editor-fold defaultstate="collapsed" desc="PRODUCT MANAGEMENT">  
    public final SentenceExec productInsert() {
        return new SentenceExecTransaction(sessionDB) {
            @Override
            public int execInTransaction(Object[] params) throws BasicException {
                int i = new PreparedSentenceExec(sessionDB,
                        "INSERT INTO products ("
                        + "ID, "
                        + "REFERENCE, "
                        + "CODE, "
                        + "CODETYPE, "
                        + "NAME, "
                        + "PRICEBUY, "
                        + "PRICESELL, "
                        + "CATEGORY, "
                        + "TAXCAT, "
                        + "ATTRIBUTESET_ID, "
                        + "STOCKCOST, "
                        + "STOCKVOLUME, "
                        + "IMAGE, "
                        + "ISCOM, "
                        + "ISSCALE, "
                        + "ISCONSTANT, "
                        + "PRINTKB, "
                        + "SENDSTATUS, "
                        + "ISSERVICE, "
                        + "ATTRIBUTES, "
                        + "DISPLAY, "
                        + "ISVPRICE, "
                        + "ISVERPATRIB, "
                        + "TEXTTIP, "
                        + "WARRANTY, "
                        + "STOCKUNITS, "
                        + "PRINTTO, "
                        + "SUPPLIER, "
                        + "UOM, "
                        + "MEMODATE ) "
                        + "VALUES ("
                        + "?, ?, ?, ?, ?, ?, "
                        + "?, ?, ?, ?, ?, ?, "
                        + "?, ?, ?, ?, ?, ?, "
                        + "?, ?, ?, ?, ?, ?, "
                        + "?, ?, ?, ?, ?, ?)",
                        productsRow.getDatas(),
                        new int[]{0,
                            1, 2, 3, 4, 5, 6,
                            7, 8, 9, 10, 11, 12,
                            13, 14, 15, 16, 17, 18,
                            19, 20, 21, 22, 23, 24,
                            25, 26, 27, 28, 29})
                        .exec(params);

                if (i > 0 && ((Boolean) params[30])) {
                    return new PreparedSentence(sessionDB,
                            "INSERT INTO products_cat (PRODUCT, CATORDER) VALUES (?, ?)",
                            new SerializerWriteBasicExt(productsRow.getDatas(),
                                    new int[]{0, 31}))
                            .exec(params);
                } else {
                    return i;
                }
            }
        };
    }

    Datas[] PRODUCT_TABLE = new Datas[]{
        Datas.STRING,
        Datas.STRING,
        Datas.STRING,
        Datas.STRING,
        Datas.STRING,
        Datas.DOUBLE,
        Datas.DOUBLE,
        Datas.STRING,
        Datas.STRING,
        Datas.STRING,
        Datas.DOUBLE,
        Datas.DOUBLE,
        Datas.IMAGE,
        Datas.BOOLEAN,
        Datas.BOOLEAN,
        Datas.BOOLEAN,
        Datas.BOOLEAN,
        Datas.BOOLEAN,
        Datas.BOOLEAN,
        Datas.BYTES,
        Datas.STRING,
        Datas.BOOLEAN,
        Datas.BOOLEAN,
        Datas.STRING,
        Datas.BOOLEAN,
        Datas.DOUBLE,
        Datas.STRING,
        Datas.STRING,
        Datas.STRING,
        Datas.TIMESTAMP};

    /**
     *
     * @return
     */
    public final SentenceExec productUpdate() {
        return new SentenceExecTransaction(sessionDB) {
            @Override
            public int execInTransaction(Object[] params) throws BasicException {

                int i = new PreparedSentenceExec(sessionDB,
                        "UPDATE products SET "
                        + "ID = ?, "
                        + "REFERENCE = ?, "
                        + "CODE = ?, "
                        + "CODETYPE = ?, "
                        + "NAME = ?, "
                        + "PRICEBUY = ?, "
                        + "PRICESELL = ?, "
                        + "CATEGORY = ?, "
                        + "TAXCAT = ?, "
                        + "ATTRIBUTESET_ID = ?, "
                        + "STOCKCOST = ?, "
                        + "STOCKVOLUME = ?, "
                        + "IMAGE = ?, "
                        + "ISCOM = ?, "
                        + "ISSCALE = ?, "
                        + "ISCONSTANT = ?, "
                        + "PRINTKB = ?, "
                        + "SENDSTATUS = ?, "
                        + "ISSERVICE = ?,  "
                        + "ATTRIBUTES = ?,"
                        + "DISPLAY = ?, "
                        + "ISVPRICE = ?, "
                        + "ISVERPATRIB = ?, "
                        + "TEXTTIP = ?, "
                        + "WARRANTY = ?, "
                        + "STOCKUNITS = ?, "
                        + "PRINTTO = ?, "
                        + "SUPPLIER = ?, "
                        + "UOM = ?, "
                        + "MEMODATE = ? "
                        + "WHERE ID = ?",
                        PRODUCT_TABLE,
                        new int[]{0,
                            1, 2, 3, 4, 5,
                            6, 7, 8, 9, 10,
                            11, 12, 13, 14, 15,
                            16, 17, 18, 19, 20,
                            21, 22, 23, 24, 25,
                            26, 27, 28, 29, 0})
                        .exec(params);
                if (i > 0) {
                    if (((Boolean) params[30])) {
                        if (new PreparedSentence(sessionDB,
                                "UPDATE products_cat SET CATORDER = ? WHERE PRODUCT = ?",
                                new SerializerWriteBasicExt(productsRow.getDatas(),
                                        new int[]{31, 0}))
                                .exec(params) == 0) {
                            new PreparedSentence(sessionDB,
                                    "INSERT INTO products_cat (PRODUCT, CATORDER) VALUES (?, ?)",
                                    new SerializerWriteBasicExt(
                                            productsRow.getDatas(),
                                            new int[]{0, 31}))
                                    .exec(params);
                        }
                    } else {
                        new PreparedSentence(sessionDB,
                                "DELETE FROM products_cat WHERE PRODUCT = ?",
                                new SerializerWriteBasicExt(productsRow.getDatas(),
                                        new int[]{0}))
                                .exec(params);
                    }
                }
                return i;
            }
        };
    }

    private final static Row productsRow = new Row(
            new Field("ID", Datas.STRING, Formats.STRING),
            new Field(AppLocal.getIntString("label.prodref"), Datas.STRING, Formats.STRING, true,
                    true, true),
            new Field(AppLocal.getIntString("label.prodbarcode"), Datas.STRING, Formats.STRING,
                    false, true, true),
            new Field(AppLocal.getIntString("label.prodbarcodetype"), Datas.STRING, Formats.STRING,
                    false, true,
                    true),
            new Field(AppLocal.getIntString("label.prodname"), Datas.STRING, Formats.STRING, true,
                    true, true),
            new Field(AppLocal.getIntString("label.prodpricebuy"), Datas.DOUBLE, Formats.CURRENCY,
                    false, true,
                    true),
            new Field(AppLocal.getIntString("label.prodpricesell"), Datas.DOUBLE, Formats.CURRENCY,
                    false, true,
                    true),
            new Field(AppLocal.getIntString("label.prodcategory"), Datas.STRING, Formats.STRING,
                    false, false,
                    true),
            new Field(AppLocal.getIntString("label.taxcategory"), Datas.STRING, Formats.STRING,
                    false, false, true),
            new Field(AppLocal.getIntString("label.attributeset"), Datas.STRING, Formats.STRING,
                    false, false,
                    true),
            new Field("STOCKCOST", Datas.DOUBLE, Formats.CURRENCY),
            new Field("STOCKVOLUME", Datas.DOUBLE, Formats.DOUBLE),
            new Field("IMAGE", Datas.IMAGE, Formats.NULL),
            new Field("ISCOM", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("ISSCALE", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("ISCONSTANT", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("PRINTKB", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("SENDSTATUS", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("ISSERVICE", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("PROPERTIES", Datas.BYTES, Formats.NULL),
            new Field(AppLocal.getIntString("label.display"), Datas.STRING, Formats.STRING, false,
                    true, true),
            new Field("ISVPRICE", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("ISVERPATRIB", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("TEXTTIP", Datas.STRING, Formats.STRING),
            new Field("WARRANTY", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field(AppLocal.getIntString("label.stockunits"), Datas.DOUBLE, Formats.DOUBLE),
            new Field("PRINTTO", Datas.STRING, Formats.STRING),
            new Field(AppLocal.getIntString("label.prodsupplier"), Datas.STRING, Formats.STRING,
                    false, false,
                    true),
            new Field(AppLocal.getIntString("label.UOM"), Datas.STRING, Formats.STRING),
            new Field("MEMODATE", Datas.TIMESTAMP, Formats.DATE),
            new Field("ISCATALOG", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("CATORDER", Datas.INT, Formats.INT));

    ;

    public final Row getProductsRow() {
        return productsRow;
    }

    /**
     * Loads on ProductsEditor
     *
     * @return
     */
    public final SentenceList getProductCatQBF() {
        return new StaticSentence(sessionDB,
                new QBFBuilder(
                        "SELECT "
                        + "P.ID, "
                        + "P.REFERENCE, "
                        + "P.CODE, "
                        + "P.CODETYPE, "
                        + "P.NAME, "
                        + "P.PRICEBUY, "
                        + "P.PRICESELL, "
                        + "P.CATEGORY, "
                        + "P.TAXCAT, "
                        + "P.ATTRIBUTESET_ID, "
                        + "P.STOCKCOST, "
                        + "P.STOCKVOLUME, "
                        // JG 3 feb 16 speedup + "P.IMAGE, "
                        + sessionDB.DB.CHAR_NULL() + ","
                        + "P.ISCOM, "
                        + "P.ISSCALE, "
                        + "P.ISCONSTANT, "
                        + "P.PRINTKB, "
                        + "P.SENDSTATUS, "
                        + "P.ISSERVICE, "
                        + "P.ATTRIBUTES, "
                        + "P.DISPLAY, "
                        + "P.ISVPRICE, "
                        + "P.ISVERPATRIB, "
                        + "P.TEXTTIP, "
                        + "P.WARRANTY, "
                        + "P.STOCKUNITS, "
                        + "P.PRINTTO, "
                        + "P.SUPPLIER, "
                        + "P.UOM, "
                        + "P.MEMODATE, "
                        + "CASE WHEN "
                        + "C.PRODUCT IS NULL "
                        + "THEN " + sessionDB.DB.FALSE()
                        + " ELSE " + sessionDB.DB.TRUE()
                        + " END, "
                        + "C.CATORDER "
                        + "FROM products P LEFT OUTER JOIN products_cat C "
                        + "ON P.ID = C.PRODUCT "
                        + "WHERE ?(QBF_FILTER) "
                        + "ORDER BY P.REFERENCE",
                        new String[]{
                            "P.NAME", "P.PRICEBUY", "P.PRICESELL", "P.CATEGORY",
                            "P.CODE"}),
                new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT,
            Datas.DOUBLE,
            Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING}),
                productsRow.getSerializerRead());
    }

    /**
     *
     * @return
     */
    public final SentenceExec getProductCatDelete() {
        return new SentenceExecTransaction(sessionDB) {
            @Override
            public int execInTransaction(Object[] params) throws BasicException {
                new PreparedSentence(sessionDB,
                        "DELETE FROM products_cat WHERE PRODUCT = ?",
                        new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0}))
                        .exec(params);
                return new PreparedSentence(sessionDB,
                        "DELETE FROM products WHERE ID = ?",
                        new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0}))
                        .exec(params);
            }
        };
    }
    //// </editor-fold>   PRODUCT MANAGEMENT

}
