//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.panels;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.util.StringUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * October 2018
 * @authors JG
 */
public class PaymentsReprintModel {

    private String m_sHost;
    private String m_sUser;
    private int m_iSeq;
 
    private String m_dDateStart;
    private String m_dDateEnd;      
    private Date rDate;
    private Date m_dPrintDate;    
    
            
    private Integer m_iPayments;
    private Double m_dPaymentsTotal;
    private java.util.List<PaymentsLine> m_lpayments;
    
    private Integer m_iCategorySalesRows;
    private Double m_dCategorySalesTotalUnits;
    private Double m_dCategorySalesTotal;
    private java.util.List<CategorySalesLine> m_lcategorysales;
    
    private Integer m_iProductSalesRows;
    private Double m_dProductSalesTotalUnits;
    private Double m_dProductSalesTotal;
    private java.util.List<ProductSalesLine> m_lproductsales;

    private java.util.List<RemovedProductLines> m_lremovedlines;
    
    private java.util.List<DrawerOpenedLines> m_ldraweropenedlines;    
    
    private final static String[] PAYMENTHEADERS = {"label.Payment", "label.Money"};
    
    private Integer m_iSales;
    private Double m_dSalesBase;
    private Double m_dSalesTaxes;
    private Double m_dSalesTaxNet;
    private java.util.List<SalesLine> m_lsales;
    
    private final static String[] SALEHEADERS = {"label.taxcategory", "label.totaltax", "label.totalnet"};

    private PaymentsReprintModel() {
    }

    /**
     *
     * @return
     */
    public static PaymentsReprintModel emptyInstance() {
        
        PaymentsReprintModel p = new PaymentsReprintModel();
        
        p.m_iPayments = 0;
        p.m_dPaymentsTotal = 0.0;
        p.m_lpayments = new ArrayList<>();

        p.m_iCategorySalesRows = 0;
        p.m_dCategorySalesTotalUnits = 0.0;
        p.m_dCategorySalesTotal = 0.0;
        p.m_lcategorysales = new ArrayList<>();        

        p.m_iSales = 0;
        p.m_dSalesBase = 0.0;
        p.m_dSalesTaxes = 0.0;
        p.m_dSalesTaxNet = 0.0;
        
        p.m_iProductSalesRows = 0;
        p.m_dProductSalesTotalUnits = 0.0;
        p.m_dProductSalesTotal = 0.0;
        p.m_lproductsales = new ArrayList<>();
        p.m_lremovedlines = new ArrayList<>();
        
        p.m_lsales = new ArrayList<>();

        return p;
    }
    
    /**
     *
     * @param app
     * @return
     * @throws BasicException
     */
    public static PaymentsReprintModel loadInstance(AppView app) throws BasicException {
        
        PaymentsReprintModel p = new PaymentsReprintModel();

        p.m_sUser = app.getAppUserView().getUser().getName();
        p.m_sHost = app.getProperties().getHost();
        
        JFrame frame = new JFrame("Sequence");
        String sequence = JOptionPane.showInputDialog(frame, 
            AppLocal.getIntString("message.ccentersequence"),
                JOptionPane.INFORMATION_MESSAGE);
        if (sequence != null) {
            int isequence = Integer.parseInt(sequence);
            p.m_iSeq = isequence;
        } else {
            app.getAppUserView().showTask("com.openbravo.pos.panels.JPanelCloseMoneyReprint");            
        }
    
        Object[] ccash;
        ccash = (Object []) new StaticSentence(app.getSession(),
                "SELECT money, host, hostsequence, datestart, dateend, nosales " +
                        "FROM closedcash " +
                        "where hostsequence = ? and dateend is not null " +
                            "AND host = " + "'" + app.getProperties().getHost() + "'"
                , SerializerWriteString.INSTANCE
                , new SerializerReadBasic(new Datas[] {
                    Datas.STRING, Datas.STRING, Datas.INT, Datas.STRING, Datas.STRING, Datas.INT}))
                .find(sequence);                

        if (ccash == null) {
            JOptionPane.showMessageDialog(null, 
                AppLocal.getIntString("message.ccsequencenotfound"),
                "",
                JOptionPane.WARNING_MESSAGE);
        } else {
            p.m_dDateStart = ccash[3].toString();
            p.m_dDateEnd = ccash[4].toString();
                
            // Product category Sales
            Object[] valcategorysales = (Object []) new StaticSentence(app.getSession()
                , "SELECT COUNT(*), " + 
                    "SUM(ticketlines.UNITS), " +
                    "SUM((ticketlines.PRICE + ticketlines.PRICE * taxes.RATE ) * ticketlines.UNITS) " +
                "FROM ticketlines, tickets, receipts, taxes " +
                "WHERE ticketlines.TICKET = tickets.ID " +
                    "AND tickets.ID = receipts.ID " +
                    "AND ticketlines.TAXID = taxes.ID " +
                    "AND ticketlines.PRODUCT IS NOT NULL " + 
                    "AND receipts.MONEY = ? " +
                "GROUP BY receipts.MONEY"
                , SerializerWriteString.INSTANCE
                , new SerializerReadBasic(new Datas[] {Datas.INT, Datas.DOUBLE, Datas.DOUBLE}))
                .find(ccash[0]);
        
            if (valcategorysales == null) {
                p.m_iCategorySalesRows = 0;
                p.m_dCategorySalesTotalUnits = 0.0;
                p.m_dCategorySalesTotal = 0.0;
            } else {
                p.m_iCategorySalesRows = (Integer) valcategorysales[0];
                p.m_dCategorySalesTotalUnits = (Double) valcategorysales[1];
                p.m_dCategorySalesTotal= (Double) valcategorysales[2];
            }

            List categorys = new StaticSentence(app.getSession()
                , "SELECT a.NAME, sum(c.UNITS), sum(c.UNITS * (c.PRICE + (c.PRICE * d.RATE))) " +
                "FROM categories as a " +
                    "LEFT JOIN products as b on a.id = b.CATEGORY " +
                    "LEFT JOIN ticketlines as c on b.id = c.PRODUCT " +
                    "LEFT JOIN taxes as d on c.TAXID = d.ID " +
                    "LEFT JOIN receipts as e on c.TICKET = e.ID " +
                "WHERE e.MONEY = ? " +
                "GROUP BY a.NAME"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentsReprintModel.CategorySalesLine.class))
                .list(ccash[0]);                

            if (categorys == null) {
                p.m_lcategorysales = new ArrayList();
            } else {
                p.m_lcategorysales = categorys;
            }        
        
            // Payments
            Object[] valtickets = (Object []) new StaticSentence(app.getSession()
                , "SELECT COUNT(*), SUM(payments.TOTAL) " +
                "FROM payments, receipts " +
                "WHERE payments.RECEIPT = receipts.ID AND receipts.MONEY = ?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.INT, Datas.DOUBLE}))
                    .find(ccash[0]);                
            
            if (valtickets == null) {
                p.m_iPayments = 0;
                p.m_dPaymentsTotal = 0.0;
            } else {
                p.m_iPayments = (Integer) valtickets[0];
                p.m_dPaymentsTotal = (Double) valtickets[1];
            }  
        
            List l = new StaticSentence(app.getSession(),            
                "SELECT payments.PAYMENT, SUM(payments.TOTAL), payments.NOTES " +
                "FROM payments, receipts " +
                "WHERE payments.RECEIPT = receipts.ID AND receipts.MONEY = ? " +
                "GROUP BY payments.PAYMENT, payments.NOTES"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadClass(PaymentsReprintModel.PaymentsLine.class))
                    .list(ccash[0]);                 
        
            if (l == null) {
                p.m_lpayments = new ArrayList();
            } else {
                p.m_lpayments = l;
            }        
        
            // Sales
            Object[] recsales = (Object []) new StaticSentence(app.getSession(),
                "SELECT COUNT(DISTINCT receipts.ID), SUM(ticketlines.UNITS * ticketlines.PRICE) " +
                "FROM receipts, ticketlines " +
                "WHERE receipts.ID = ticketlines.TICKET AND receipts.MONEY = ?"
                    ,SerializerWriteString.INSTANCE,
                    new SerializerReadBasic(new Datas[] {Datas.INT, Datas.DOUBLE}))
                    .find(ccash[0]);        

            if (recsales == null) {
                p.m_iSales = null;
                p.m_dSalesBase = null;
            } else {
                p.m_iSales = (Integer) recsales[0];
                p.m_dSalesBase = (Double) recsales[1];
            }             
        
            // Taxes
            Object[] rectaxes = (Object []) new StaticSentence(app.getSession(),
                "SELECT SUM(taxlines.AMOUNT), SUM(taxlines.BASE) " +
                "FROM receipts, taxlines " +
                "WHERE receipts.ID = taxlines.RECEIPT AND receipts.MONEY = ?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {
                    Datas.DOUBLE, Datas.DOUBLE})) 
                    .find(ccash[0]);                            

            if (rectaxes == null) {
                p.m_dSalesTaxes = null;
                p.m_dSalesTaxNet = null;
            } else {
                p.m_dSalesTaxes = (Double) rectaxes[0];
                p.m_dSalesTaxNet = (Double) rectaxes[1];
            } 
                
            List<SalesLine> asales = new StaticSentence(app.getSession(),
                "SELECT taxcategories.NAME, SUM(taxlines.AMOUNT), SUM(taxlines.BASE), " +
                    "SUM(taxlines.BASE + taxlines.AMOUNT) " +
                "FROM receipts, taxlines, taxes, taxcategories " +
                "WHERE receipts.ID = taxlines.RECEIPT AND taxlines.TAXID = taxes.ID AND taxes.CATEGORY = taxcategories.ID " +
                    "AND receipts.MONEY = ? " +
                "GROUP BY taxcategories.NAME"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadClass(PaymentsReprintModel.SalesLine.class))
                    .list(ccash[0]);                

            if (asales == null) {
                p.m_lsales = new ArrayList<>();
            } else {
                p.m_lsales = asales;
            }

            List removedLines = new StaticSentence(app.getSession()
                , "SELECT lineremoved.NAME, lineremoved.TICKETID, lineremoved.PRODUCTNAME, " +
                        "SUM(lineremoved.UNITS) AS TOTAL_UNITS  "
                + "FROM lineremoved "
                + "WHERE lineremoved.REMOVEDDATE > " + "'" + p.m_dDateStart + "'" + " "
                + "GROUP BY lineremoved.NAME, lineremoved.TICKETID, lineremoved.PRODUCTNAME"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentsReprintModel.RemovedProductLines.class))
                .list(p.m_dDateStart);
        
            if (removedLines == null) {
                p.m_lremovedlines = new ArrayList();
            } else {
                p.m_lremovedlines = removedLines;
            }
 
            List drawerOpenedLines = new StaticSentence(app.getSession()
                , "SELECT OPENDATE, NAME, TICKETID  " +
                "FROM draweropened " +
                "WHERE TICKETID = 'No Sale' AND OPENDATE > ? " +
                "GROUP BY NAME, OPENDATE, TICKETID"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentsReprintModel.DrawerOpenedLines.class))
                .list(p.m_dDateStart);
        
            if (drawerOpenedLines == null) {
                p.m_ldraweropenedlines = new ArrayList();
            } else {
                p.m_ldraweropenedlines = drawerOpenedLines;
            }        
                
            // Product Sales
            Object[] valproductsales = (Object []) new StaticSentence(app.getSession()
                , "SELECT COUNT(*), SUM(ticketlines.UNITS), "
                    + "SUM((ticketlines.PRICE + ticketlines.PRICE * taxes.RATE ) * ticketlines.UNITS) "
                + "FROM ticketlines, tickets, receipts, taxes "
                + "WHERE ticketlines.TICKET = tickets.ID " +
                    "AND tickets.ID = receipts.ID " +
                    "AND ticketlines.TAXID = taxes.ID " +
                    "AND ticketlines.PRODUCT IS NOT NULL " +
                    "AND receipts.MONEY = ? "
                + "GROUP BY receipts.MONEY"
                , SerializerWriteString.INSTANCE
                , new SerializerReadBasic(new Datas[] {Datas.INT, Datas.DOUBLE, Datas.DOUBLE}))
                .find(ccash[0]);                
 
            if (valproductsales == null) {
                p.m_iProductSalesRows = 0;
                p.m_dProductSalesTotalUnits = 0.0;
                p.m_dProductSalesTotal = 0.0;
            } else {
                p.m_iProductSalesRows = (Integer) valproductsales[0];
                p.m_dProductSalesTotalUnits = (Double) valproductsales[1];
                p.m_dProductSalesTotal= (Double) valproductsales[2];
            }
 
            List products = new StaticSentence(app.getSession()
                , "SELECT products.NAME, SUM(ticketlines.UNITS), ticketlines.PRICE, taxes.RATE "
                + "FROM ticketlines, tickets, receipts, products, taxes "
                + "WHERE ticketlines.PRODUCT = products.ID " +
                    "AND ticketlines.TICKET = tickets.ID " +
                    "AND tickets.ID = receipts.ID " + 
                    "AND ticketlines.TAXID = taxes.ID " +
                    "AND receipts.MONEY = ? "
                + "GROUP BY products.NAME, ticketlines.PRICE, taxes.RATE"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentsReprintModel.ProductSalesLine.class))
                .list(ccash[0]);
        
            if (products == null) {
                p.m_lproductsales = new ArrayList();
            } else {
                p.m_lproductsales = products;
            }
      
            return p;
        }

        return null;
    }

    /**
     *
     * @return
     */
    public int getPayments() {
        return m_iPayments;
    }

    /**
     *
     * @return
     */
    public double getTotal() {
        return m_dPaymentsTotal;
    }

    /**
     *
     * @return
     */
    public String getHost() {
        return m_sHost;
    }
    /**
     *
     * @return
     */
    public String getUser() {
        return m_sUser;
    }

    /**
     *
     * @return
     */
    public int getSequence() {
        return m_iSeq;
    }
    
    public String getPrintDate() {
        Date m_dPrintDate = new Date();
        return Formats.TIMESTAMP.formatValue(m_dPrintDate);
    }
    /**
     *
     * @return
     */
    public String getDateStart() {
        return m_dDateStart;
    }

    /**
     *
     * @param dValue
     */
    public void setDateEnd(String dValue) {
        m_dDateEnd = dValue;
    }

    /**
     *
     * @return
     */
    public String getDateEnd() {
        return m_dDateEnd;
    }
    
    /**
     *
     * @return
     */
    public String getDateStartDerby(){
        SimpleDateFormat ndf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return ndf.format(m_dDateStart);
    }
    
    /**
     *
     * @return
     */
    public String printHost() {
        return StringUtils.encodeXML(m_sHost);
    }
    
    /**
     *
     * @return
     */
    public String printUser() {
        return StringUtils.encodeXML(m_sUser);
    }
    
    /**
     *
     * @return
     */
    public String printSequence() {
        return Formats.INT.formatValue(m_iSeq);
    }
    
    public String printDate() {
        Date m_dPrintDate = new Date();
        return Formats.TIMESTAMP.formatValue(m_dPrintDate);
    }


    /**
     *
     * @return
     */
    public String printDateStart() {
//        return Formats.TIMESTAMP.formatValue(m_dDateStart);
        return m_dDateStart;        
    }
    
    public String reformDateStart() {

        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        String input = m_dDateStart;
        Date date = null;
        try {
            date = sdfIn.parse(input);
        } catch (ParseException ex) {
            Logger.getLogger(PaymentsReprintModel.class.getName()).log(Level.SEVERE, null, ex);
        }

//        System.out.println(sdfOut.format(date));        
        m_dDateStart=sdfOut.format(date); 
       
        return m_dDateStart;
    }    
    
    public String reformDateEnd() {

        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        String input = m_dDateEnd;
        Date date = null;
        try {
            date = sdfIn.parse(input);
        } catch (ParseException ex) {
            Logger.getLogger(PaymentsReprintModel.class.getName()).log(Level.SEVERE, null, ex);
        }

//        System.out.println(sdfOut.format(date));        
        m_dDateEnd=sdfOut.format(date); 
       
        return m_dDateEnd;
    }      

    /**
     *
     * @return
     */
    public String printDateEnd() {
//        return Formats.TIMESTAMP.formatValue(m_dDateEnd);
        return m_dDateEnd;                
    }

    /**
     *
     * @return
     */
    public String printPayments() {
        return Formats.INT.formatValue(m_iPayments);
    }

    /**
     *
     * @return
     */
    public String printPaymentsTotal() {
        return Formats.CURRENCY.formatValue(m_dPaymentsTotal);
    }

    /**
     *
     * @return
     */
    public List<PaymentsLine> getPaymentLines() {
        return m_lpayments;
    }
    
    /**
     *
     * @return
     */
    public int getSales() {
        return m_iSales == null ? 0 : m_iSales;
    }    

    /**
     *
     * @return
     */
    public String printSales() {
        return Formats.INT.formatValue(m_iSales);
    }

    /**
     *
     * @return
     */
    public String printSalesBase() {
        return Formats.CURRENCY.formatValue(m_dSalesBase);
    }     

    /**
     *
     * @return
     */
    public String printSalesTaxes() {
        return Formats.CURRENCY.formatValue(m_dSalesTaxes);
    }     

    /**
     *
     * @return
     */
    public String printSalesTotal() {            
        return Formats.CURRENCY.formatValue((m_dSalesBase == null || m_dSalesTaxes == null)
                ? null
                : m_dSalesBase + m_dSalesTaxes);
    }     

    /**
     *
     * @return
     */
    public List<SalesLine> getSaleLines() {
        return m_lsales;
    }

    /**
     *
     * @return
     */
        public double getCategorySalesRows() {
        return m_iCategorySalesRows;
    }

    /**
     *
     * @return
     */
    public String printCategorySalesRows() {
        return Formats.INT.formatValue(m_iCategorySalesRows);
    }

    /**
     *
     * @return
     */
    public double getCategorySalesTotalUnits() {
        return m_dCategorySalesTotalUnits;
    }

    /**
     *
     * @return
     */
    public String printCategorySalesTotalUnits() {
        return Formats.DOUBLE.formatValue(m_dCategorySalesTotalUnits);
    }

    /**
     *
     * @return
     */
    public double getCategorySalesTotal() {
        return m_dCategorySalesTotal;
    }

    /**
     *
     * @return
     */
    public String printCategorySalesTotal() {
        return Formats.CURRENCY.formatValue(m_dCategorySalesTotal);
    }

    /**
     *
     * @return
     */
    public List<CategorySalesLine> getCategorySalesLines() {
        return m_lcategorysales;
    }    

    
    /**
     *
     * @return
     */
        public double getProductSalesRows() {
        return m_iProductSalesRows;
    }
    
    /**
     *
     * @return
     */
    public String printProductSalesRows() {
        return Formats.INT.formatValue(m_iProductSalesRows);
    }
 
    /**
     *
     * @return
     */
    public double getProductSalesTotalUnits() {
        return m_dProductSalesTotalUnits;
    }
 
    /**
     *
     * @return
     */
    public String printProductSalesTotalUnits() {
        return Formats.DOUBLE.formatValue(m_dProductSalesTotalUnits);
    }
 
    /**
     *
     * @return
     */
    public double getProductSalesTotal() {
        return m_dProductSalesTotal;
    }
 
    /**
     *
     * @return
     */
    public String printProductSalesTotal() {
        return Formats.CURRENCY.formatValue(m_dProductSalesTotal);
    }
 
    /**
     *
     * @return
     */
    public List<ProductSalesLine> getProductSalesLines() {
        return m_lproductsales;
    }
    
    /**
     * 
     * @return
     */
        public List<RemovedProductLines> getRemovedProductLines() {
        return m_lremovedlines;
    }

    /**
     * JG Dec 14
     * @return
     */
        public List<DrawerOpenedLines> getDrawerOpenedLines() {
        return m_ldraweropenedlines;
    }
    
    /**
     *
     * @return
     */
    public AbstractTableModel getPaymentsReprintModel() {
        return new AbstractTableModel() {
            @Override
            public String getColumnName(int column) {
                return AppLocal.getIntString(PAYMENTHEADERS[column]);
            }
            @Override
            public int getRowCount() {
                return m_lpayments.size();
            }
            @Override
            public int getColumnCount() {
                return PAYMENTHEADERS.length;
            }
            @Override
            public Object getValueAt(int row, int column) {
                PaymentsLine l = m_lpayments.get(row);
                switch (column) {
                case 0: return l.getType();
                case 1: return l.getValue();
                default: return null;
                }
            }  
        };
    }
    
        public static class CategorySalesLine implements SerializableRead {

        private String m_CategoryName;
        private Double m_CategoryUnits;
        private Double m_CategorySum;

        /**
         *
         * @param dr
         * @throws BasicException
         */
        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_CategoryName = dr.getString(1);
            m_CategoryUnits = dr.getDouble(2);
            m_CategorySum = dr.getDouble(3);
        }

        /**
         *
         * @return
         */
        public String printCategoryName() {
            return m_CategoryName;
        }

        /**
         *
         * @return
         */
        public String printCategoryUnits() {
            return Formats.DOUBLE.formatValue(m_CategoryUnits);
        }

        /**
         *
         * @return
         */
        public Double getCategoryUnits() {
            return m_CategoryUnits;
        }

        /**
         *
         * @return
         */
        public String printCategorySum() {
            return Formats.CURRENCY.formatValue(m_CategorySum);
        }

        /**
         *
         * @return
         */
        public Double getCategorySum() {
            return m_CategorySum;
        }
    }    
    
        public static class RemovedProductLines implements SerializableRead {

            private String m_Name;
            private String m_TicketId;
            private String m_ProductName;
            private Double m_TotalUnits;
        
        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_Name = dr.getString(1);
            m_TicketId = dr.getString(2);
            m_ProductName = dr.getString(3);
            m_TotalUnits = dr.getDouble(4);
        }
          
        public String printWorkerName() {
            return StringUtils.encodeXML(m_Name);
        }
        
        public String printTicketId() {
            return StringUtils.encodeXML(m_TicketId);
        }
        
        public String printProductName() {
            return StringUtils.encodeXML(m_ProductName);
        }
 
        public String printTotalUnits() {
            return Formats.DOUBLE.formatValue(m_TotalUnits);
        }
 
    }
        
    /**
     * JG Dec 14
     */
        public static class DrawerOpenedLines implements SerializableRead {

            private String m_DrawerOpened;
            private String m_Name;
            private String m_TicketId;
        
            @Override
            public void readValues(DataRead dr) throws BasicException {
                m_DrawerOpened = dr.getString(1);
                m_Name = dr.getString(2);
                m_TicketId = dr.getString(3);
            }
          
            public String printDrawerOpened() {
                return StringUtils.encodeXML(m_DrawerOpened);
            }
            
            public String printUserName() {
                return StringUtils.encodeXML(m_Name);
            }
        
            public String printTicketId() {
                return StringUtils.encodeXML(m_TicketId);
            }
        }        

        public static class ProductSalesLine implements SerializableRead {
 
        private String m_ProductName;
        private Double m_ProductUnits;
        private Double m_ProductPrice;
        private Double m_TaxRate;
        private Double m_ProductPriceTax;
        private Double m_ProductPriceNet;
 
        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_ProductName = dr.getString(1);
            m_ProductUnits = dr.getDouble(2);
            m_ProductPrice = dr.getDouble(3);
            m_TaxRate = dr.getDouble(4);
 
            m_ProductPriceTax = m_ProductPrice + m_ProductPrice*m_TaxRate;
            m_ProductPriceNet = m_ProductPrice * m_TaxRate;
        }
 
        public String printProductName() {
            return StringUtils.encodeXML(m_ProductName);
        }
 
        public String printProductUnits() {
            return Formats.DOUBLE.formatValue(m_ProductUnits);
        }
 
        public Double getProductUnits() {
            return m_ProductUnits;
        }
 
        public String printProductPrice() {
            return Formats.CURRENCY.formatValue(m_ProductPrice);
        }
 
        public Double getProductPrice() {
            return m_ProductPrice;
        }
 
        public String printTaxRate() {
            return Formats.PERCENT.formatValue(m_TaxRate);
        }
 
        public Double getTaxRate() {
            return m_TaxRate;
        }
 
        public String printProductPriceTax() {
            return Formats.CURRENCY.formatValue(m_ProductPriceTax);
        }
        
        public String printProductSubValue() {
            return Formats.CURRENCY.formatValue(m_ProductPriceTax*m_ProductUnits);
        }
        
        /**
         * JG 4 Jun 2014
         * @return
         */
        public String printProductPriceNet() {
            return Formats.CURRENCY.formatValue(m_ProductPrice*m_ProductUnits);
    }
        
    }

    public static class SalesLine implements SerializableRead {
        
        private String m_SalesTaxName;
        private Double m_SalesTaxes;
        private Double m_SalesTaxNet;  
        private Double m_SalesTaxGross;
        /**
         *
         * @param dr
         * @throws BasicException
         */
        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_SalesTaxName = dr.getString(1);
            m_SalesTaxes = dr.getDouble(2);
            m_SalesTaxNet = dr.getDouble(3);
            m_SalesTaxGross = dr.getDouble(4);
        }

        /**
         *
         * @return
         */
        public String printTaxName() {
            return m_SalesTaxName;
        }      

        /**
         *
         * @return
         */
        public String printTaxes() {
            return Formats.CURRENCY.formatValue(m_SalesTaxes);
        }

        /**
         * 
         * @return
         */
        public String printTaxNet() {
            return Formats.CURRENCY.formatValue(m_SalesTaxNet);
        }

        /**
         * 
         * @return
         */
        public String printTaxGross() {
            return Formats.CURRENCY.formatValue(m_SalesTaxes + m_SalesTaxNet);
        }
        
        
        /**
         *
         * @return
         */
        public String getTaxName() {
            return m_SalesTaxName;
        }

        /**
         *
         * @return
         */
        public Double getTaxes() {
            return m_SalesTaxes;
        }        
        
        /**
         * 
         * @return
         */
        public Double getTaxNet() {
            return m_SalesTaxNet;
    }

    /**
         * JG June 2014
         * @return
         */
        public Double getTaxGross() {
            return m_SalesTaxGross;
        }
        

        
    }

    /**
     *
     * @return
     */
    public AbstractTableModel getSalesModel() {
        return new AbstractTableModel() {
            @Override
            public String getColumnName(int column) {
                return AppLocal.getIntString(SALEHEADERS[column]);
            }
            @Override
            public int getRowCount() {
                return m_lsales.size();
            }
            @Override
            public int getColumnCount() {
                return SALEHEADERS.length;
            }
            @Override
            public Object getValueAt(int row, int column) {
                SalesLine l = m_lsales.get(row);
                switch (column) {
                case 0: return l.getTaxName();
                case 1: return l.getTaxes();
                case 2: return l.getTaxNet();
                default: return null;
                }
            }  
        };
    }
    
    /**
     *
     */
    public static class PaymentsLine implements SerializableRead {
        
        private String m_PaymentType;
        private Double m_PaymentValue;
        private String s_PaymentReason;
        
        /**
         *
         * @param dr
         * @throws BasicException
         */
        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_PaymentType = dr.getString(1);
            m_PaymentValue = dr.getDouble(2);
            s_PaymentReason=dr.getString(3) == null ? "": dr.getString(3);            
        }
        
        /**
         *
         * @return
         */
        public String printType() {
            return AppLocal.getIntString("transpayment." + m_PaymentType);
        }

        /**
         *
         * @return
         */
        public String getType() {
            return m_PaymentType;
        }

        /**
         *
         * @return
         */
        public String printValue() {
            return Formats.CURRENCY.formatValue(m_PaymentValue);
        }

        /**
         *
         * @return
         */
        public Double getValue() {
            return m_PaymentValue;
        }

        /**
         *
         * @return
         */
        public String printReason() {
            return s_PaymentReason;
        }

        /**
         *
         * @return
         */
        public String getReason() {
            return s_PaymentReason;        
    }
  }
}