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
package com.openbravo.pos.panels;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.util.StringUtils;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class PaymentsReprintModel {

    private static final Logger LOGGER = Logger.getLogger(PaymentsReprintModel.class.getName());
    private String host;
    private String user;
    private int hostSequence;

    private Date startDate;
    private Date endDate;

    private Integer payments;
    private Double paymentsTotal;
    private List<PaymentsLine> paymentsLines;

    private Integer categorySalesRows;
    private Double categorySalesTotalUnits;
    private Double categorySalesTotal;
    private List<CategorySalesLine> CategorySalesLines;

    private Integer productSalesRows;
    private Double productSalesTotalUnits;
    private Double productSalesTotal;
    private List<ProductSalesLine> productSalesLines;

    private List<RemovedProductLines> removedSalesLines;

    private List<DrawerOpenedLines> drawerOpenedLines;

    private final static String[] PAYMENTHEADERS = {"label.Payment", "label.money"};

    private Integer salesNum;
    private Double salesBase;
    private Double salesTaxes;
    private Double salesTaxNet;
    private List<SalesLine> salesLines;

    private final static String[] SALEHEADERS = {"label.taxcategory", "label.totaltax", "label.totalnet"};

    private PaymentsReprintModel() {}

    public static PaymentsReprintModel emptyInstance() {

        PaymentsReprintModel p = new PaymentsReprintModel();

        p.payments = 0;
        p.paymentsTotal = 0.0;
        p.paymentsLines = new ArrayList<>();

        p.categorySalesRows = 0;
        p.categorySalesTotalUnits = 0.0;
        p.categorySalesTotal = 0.0;
        p.CategorySalesLines = new ArrayList<>();

        p.salesNum = 0;
        p.salesBase = 0.0;
        p.salesTaxes = 0.0;
        p.salesTaxNet = 0.0;

        p.productSalesRows = 0;
        p.productSalesTotalUnits = 0.0;
        p.productSalesTotal = 0.0;
        p.productSalesLines = new ArrayList<>();
        p.removedSalesLines = new ArrayList<>();

        p.salesLines = new ArrayList<>();

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

        p.user = app.getAppUserView().getUser().getName();
        p.host = app.getProperties().getHost();

        JFrame frame = new JFrame("Sequence");
        String sequence = JOptionPane.showInputDialog(frame,
                AppLocal.getIntString("message.ccentersequence"),
                JOptionPane.INFORMATION_MESSAGE);
        if (sequence != null) {
            int isequence = Integer.parseInt(sequence);
            p.hostSequence = isequence;
        } else {
            app.getAppUserView().showTask("com.openbravo.pos.panels.JPanelCloseMoneyReprint");
        }

        StaticSentence<String, CloseCash> closeCashSentence = new StaticSentence<>(app.getSession(),
                "SELECT money, host, hostsequence, datestart, dateend "
                + "FROM closedcash "
                + "where hostsequence = ? and dateend is not null "
                + "AND host = " + "'" + app.getProperties().getHost() + "'",
                 SerializerWriteString.INSTANCE,
                 new SerializerReadClass(CloseCash.class));

        CloseCash ccash = closeCashSentence.find(sequence);

        if (ccash == null) {
            JOptionPane.showMessageDialog(null,
                    AppLocal.getIntString("message.ccsequencenotfound"),
                    "",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            String money = ccash.getMoney();
            p.startDate = ccash.getDatestart();
            p.endDate = ccash.getDatestart();

            // Product category Sales
            Object[] valcategorysales = (Object[]) new StaticSentence(app.getSession(),
                     "SELECT COUNT(*), "
                    + "SUM(ticketlines.UNITS), "
                    + "SUM((ticketlines.PRICE + ticketlines.PRICE * taxes.RATE ) * ticketlines.UNITS) "
                    + "FROM ticketlines, tickets, receipts, taxes "
                    + "WHERE ticketlines.TICKET = tickets.ID "
                    + "AND tickets.ID = receipts.ID "
                    + "AND ticketlines.TAXID = taxes.ID "
                    + "AND ticketlines.PRODUCT IS NOT NULL "
                    + "AND receipts.MONEY = ? "
                    + "GROUP BY receipts.MONEY",
                     SerializerWriteString.INSTANCE,
                     new SerializerReadBasic(new Datas[]{Datas.INT, Datas.DOUBLE, Datas.DOUBLE}))
                    .find(money);

            if (valcategorysales == null) {
                p.categorySalesRows = 0;
                p.categorySalesTotalUnits = 0.0;
                p.categorySalesTotal = 0.0;
            } else {
                p.categorySalesRows = (Integer) valcategorysales[0];
                p.categorySalesTotalUnits = (Double) valcategorysales[1];
                p.categorySalesTotal = (Double) valcategorysales[2];
            }

            List<CategorySalesLine> categorys = new StaticSentence(app.getSession(),
                     "SELECT a.NAME, sum(c.UNITS), sum(c.UNITS * (c.PRICE + (c.PRICE * d.RATE))) "
                    + "FROM categories as a "
                    + "LEFT JOIN products as b on a.id = b.CATEGORY "
                    + "LEFT JOIN ticketlines as c on b.id = c.PRODUCT "
                    + "LEFT JOIN taxes as d on c.TAXID = d.ID "
                    + "LEFT JOIN receipts as e on c.TICKET = e.ID "
                    + "WHERE e.MONEY = ? "
                    + "GROUP BY a.NAME",
                     SerializerWriteString.INSTANCE,
                     new SerializerReadClass(PaymentsReprintModel.CategorySalesLine.class))
                    .list(money);

            if (categorys == null) {
                p.CategorySalesLines = new ArrayList<>();
            } else {
                p.CategorySalesLines = categorys;
            }

            // Payments
            Object[] valtickets = (Object[]) new StaticSentence(app.getSession(),
                     "SELECT COUNT(*), SUM(payments.TOTAL) "
                    + "FROM payments, receipts "
                    + "WHERE payments.RECEIPT = receipts.ID AND receipts.MONEY = ?",
                     SerializerWriteString.INSTANCE,
                     new SerializerReadBasic(new Datas[]{Datas.INT, Datas.DOUBLE}))
                    .find(money);

            if (valtickets == null) {
                p.payments = 0;
                p.paymentsTotal = 0.0;
            } else {
                p.payments = (Integer) valtickets[0];
                p.paymentsTotal = (Double) valtickets[1];
            }

            List<PaymentsLine> paymentList = new StaticSentence(app.getSession(),
                    "SELECT payments.PAYMENT, SUM(payments.TOTAL), payments.NOTES "
                    + "FROM payments, receipts "
                    + "WHERE payments.RECEIPT = receipts.ID AND receipts.MONEY = ? "
                    + "GROUP BY payments.PAYMENT, payments.NOTES",
                     SerializerWriteString.INSTANCE,
                     new SerializerReadClass(PaymentsReprintModel.PaymentsLine.class))
                    .list(money);

            if (paymentList == null) {
                p.paymentsLines = new ArrayList<>();
            } else {
                p.paymentsLines = paymentList;
            }

            // Sales
            Object[] recsales = (Object[]) new StaticSentence(app.getSession(),
                    "SELECT COUNT(DISTINCT receipts.ID), SUM(ticketlines.UNITS * ticketlines.PRICE) "
                    + "FROM receipts, ticketlines "
                    + "WHERE receipts.ID = ticketlines.TICKET AND receipts.MONEY = ?",
                     SerializerWriteString.INSTANCE,
                    new SerializerReadBasic(new Datas[]{Datas.INT, Datas.DOUBLE}))
                    .find(money);

            if (recsales == null) {
                p.salesNum = null;
                p.salesBase = null;
            } else {
                p.salesNum = (Integer) recsales[0];
                p.salesBase = (Double) recsales[1];
            }

            // Taxes
            Object[] rectaxes = (Object[]) new StaticSentence(app.getSession(),
                    "SELECT SUM(taxlines.AMOUNT), SUM(taxlines.BASE) "
                    + "FROM receipts, taxlines "
                    + "WHERE receipts.ID = taxlines.RECEIPT AND receipts.MONEY = ?",
                     SerializerWriteString.INSTANCE,
                     new SerializerReadBasic(new Datas[]{
                Datas.DOUBLE, Datas.DOUBLE}))
                    .find(money);

            if (rectaxes == null) {
                p.salesTaxes = null;
                p.salesTaxNet = null;
            } else {
                p.salesTaxes = (Double) rectaxes[0];
                p.salesTaxNet = (Double) rectaxes[1];
            }

            List<SalesLine> asales = new StaticSentence(app.getSession(),
                    "SELECT taxcategories.NAME, SUM(taxlines.AMOUNT), SUM(taxlines.BASE), "
                    + "SUM(taxlines.BASE + taxlines.AMOUNT) "
                    + "FROM receipts, taxlines, taxes, taxcategories "
                    + "WHERE receipts.ID = taxlines.RECEIPT AND taxlines.TAXID = taxes.ID AND taxes.CATEGORY = taxcategories.ID "
                    + "AND receipts.MONEY = ? "
                    + "GROUP BY taxcategories.NAME",
                     SerializerWriteString.INSTANCE,
                     new SerializerReadClass(PaymentsReprintModel.SalesLine.class))
                    .list(money);

            if (asales == null) {
                p.salesLines = new ArrayList<>();
            } else {
                p.salesLines = asales;
            }

            List<RemovedProductLines> removedLines = new StaticSentence(app.getSession(),
                     "SELECT lineremoved.NAME, lineremoved.TICKETID, lineremoved.PRODUCTNAME, "
                    + "SUM(lineremoved.UNITS) AS TOTAL_UNITS  "
                    + "FROM lineremoved "
                    + "WHERE lineremoved.REMOVEDDATE > ?"
                    + "GROUP BY lineremoved.NAME, lineremoved.TICKETID, lineremoved.PRODUCTNAME",
                     SerializerWriteDate.INSTANCE,
                     new SerializerReadClass(PaymentsReprintModel.RemovedProductLines.class))
                    .list(p.startDate);

            if (removedLines == null) {
                p.removedSalesLines = new ArrayList<>();
            } else {
                p.removedSalesLines = removedLines;
            }

            List<DrawerOpenedLines> drawerOpenedLines = new StaticSentence(app.getSession(),
                     "SELECT OPENDATE, NAME, TICKETID  "
                    + "FROM draweropened "
                    + "WHERE TICKETID = 'No Sale' AND OPENDATE > ? "
                    + "GROUP BY NAME, OPENDATE, TICKETID",
                     SerializerWriteDate.INSTANCE,
                     new SerializerReadClass(PaymentsReprintModel.DrawerOpenedLines.class))
                    .list(p.startDate);

            if (drawerOpenedLines == null) {
                p.drawerOpenedLines = new ArrayList<>();
            } else {
                p.drawerOpenedLines = drawerOpenedLines;
            }

            // Product Sales
            Object[] valproductsales = (Object[]) new StaticSentence(app.getSession(),
                     "SELECT COUNT(*), SUM(ticketlines.UNITS), "
                    + "SUM((ticketlines.PRICE + ticketlines.PRICE * taxes.RATE ) * ticketlines.UNITS) "
                    + "FROM ticketlines, tickets, receipts, taxes "
                    + "WHERE ticketlines.TICKET = tickets.ID "
                    + "AND tickets.ID = receipts.ID "
                    + "AND ticketlines.TAXID = taxes.ID "
                    + "AND ticketlines.PRODUCT IS NOT NULL "
                    + "AND receipts.MONEY = ? "
                    + "GROUP BY receipts.MONEY",
                     SerializerWriteString.INSTANCE,
                     new SerializerReadBasic(new Datas[]{Datas.INT, Datas.DOUBLE, Datas.DOUBLE}))
                    .find(money);

            if (valproductsales == null) {
                p.productSalesRows = 0;
                p.productSalesTotalUnits = 0.0;
                p.productSalesTotal = 0.0;
            } else {
                p.productSalesRows = (Integer) valproductsales[0];
                p.productSalesTotalUnits = (Double) valproductsales[1];
                p.productSalesTotal = (Double) valproductsales[2];
            }

            List<ProductSalesLine> products = new StaticSentence(app.getSession(),
                     "SELECT products.NAME, SUM(ticketlines.UNITS), ticketlines.PRICE, taxes.RATE "
                    + "FROM ticketlines, tickets, receipts, products, taxes "
                    + "WHERE ticketlines.PRODUCT = products.ID "
                    + "AND ticketlines.TICKET = tickets.ID "
                    + "AND tickets.ID = receipts.ID "
                    + "AND ticketlines.TAXID = taxes.ID "
                    + "AND receipts.MONEY = ? "
                    + "GROUP BY products.NAME, ticketlines.PRICE, taxes.RATE",
                     SerializerWriteString.INSTANCE,
                     new SerializerReadClass(PaymentsReprintModel.ProductSalesLine.class))
                    .list(money);

            if (products == null) {
                p.productSalesLines = new ArrayList<>();
            } else {
                p.productSalesLines = products;
            }

            return p;
        }

        return null;
    }

    public int getPayments() {
        return payments;
    }

    public double getTotal() {
        return paymentsTotal;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public int getSequence() {
        return hostSequence;
    }

    public Date getDateStart() {
        return startDate;
    }

    public void setDateEnd(Date dValue) {
        endDate = dValue;
    }

    public Date getDateEnd() {
        return endDate;
    }

    public String printHost() {
        return StringUtils.encodeXML(host);
    }

    public String printUser() {
        return StringUtils.encodeXML(user);
    }

    public String printSequence() {
        return Formats.INT.formatValue(hostSequence);
    }

    public String printDateStart() {
        return Formats.TIMESTAMP.formatValue(startDate);
    }

    public String printDateEnd() {
        return Formats.TIMESTAMP.formatValue(endDate);
    }

    public String printPayments() {
        return Formats.INT.formatValue(payments);
    }

    public String printPaymentsTotal() {
        return Formats.CURRENCY.formatValue(paymentsTotal);
    }

    public List<PaymentsLine> getPaymentLines() {
        return paymentsLines;
    }

    public int getSales() {
        return salesNum == null ? 0 : salesNum;
    }

    public String printSales() {
        return Formats.INT.formatValue(salesNum);
    }

    public String printSalesBase() {
        return Formats.CURRENCY.formatValue(salesBase);
    }

    public String printSalesTaxes() {
        return Formats.CURRENCY.formatValue(salesTaxes);
    }

    public String printSalesTotal() {
        return Formats.CURRENCY.formatValue((salesBase == null || salesTaxes == null)
                ? null
                : salesBase + salesTaxes);
    }

    public List<SalesLine> getSaleLines() {
        return salesLines;
    }

    public double getCategorySalesRows() {
        return categorySalesRows;
    }

    public String printCategorySalesRows() {
        return Formats.INT.formatValue(categorySalesRows);
    }

    public double getCategorySalesTotalUnits() {
        return categorySalesTotalUnits;
    }

    public String printCategorySalesTotalUnits() {
        return Formats.DOUBLE.formatValue(categorySalesTotalUnits);
    }

    public double getCategorySalesTotal() {
        return categorySalesTotal;
    }

    public String printCategorySalesTotal() {
        return Formats.CURRENCY.formatValue(categorySalesTotal);
    }

    public List<CategorySalesLine> getCategorySalesLines() {
        return CategorySalesLines;
    }

    public double getProductSalesRows() {
        return productSalesRows;
    }

    public String printProductSalesRows() {
        return Formats.INT.formatValue(productSalesRows);
    }

    public double getProductSalesTotalUnits() {
        return productSalesTotalUnits;
    }

    public String printProductSalesTotalUnits() {
        return Formats.DOUBLE.formatValue(productSalesTotalUnits);
    }

    public double getProductSalesTotal() {
        return productSalesTotal;
    }

    public String printProductSalesTotal() {
        return Formats.CURRENCY.formatValue(productSalesTotal);
    }

    public List<ProductSalesLine> getProductSalesLines() {
        return productSalesLines;
    }

    public List<RemovedProductLines> getRemovedProductLines() {
        return removedSalesLines;
    }

    public List<DrawerOpenedLines> getDrawerOpenedLines() {
        return drawerOpenedLines;
    }

    public AbstractTableModel getPaymentsReprintModel() {
        return new AbstractTableModel() {
            @Override
            public String getColumnName(int column) {
                return AppLocal.getIntString(PAYMENTHEADERS[column]);
            }

            @Override
            public int getRowCount() {
                return paymentsLines.size();
            }

            @Override
            public int getColumnCount() {
                return PAYMENTHEADERS.length;
            }

            @Override
            public Object getValueAt(int row, int column) {
                PaymentsLine l = paymentsLines.get(row);
                switch (column) {
                    case 0:
                        return l.getType();
                    case 1:
                        return l.getValue();
                    default:
                        return null;
                }
            }
        };
    }

    public static class CategorySalesLine implements SerializableRead {

        private String m_CategoryName;
        private Double m_CategoryUnits;
        private Double m_CategorySum;

        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_CategoryName = dr.getString(1);
            m_CategoryUnits = dr.getDouble(2);
            m_CategorySum = dr.getDouble(3);
        }

        public String printCategoryName() {
            return m_CategoryName;
        }

        public String printCategoryUnits() {
            return Formats.DOUBLE.formatValue(m_CategoryUnits);
        }

        public Double getCategoryUnits() {
            return m_CategoryUnits;
        }

        public String printCategorySum() {
            return Formats.CURRENCY.formatValue(m_CategorySum);
        }

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

            m_ProductPriceTax = m_ProductPrice + m_ProductPrice * m_TaxRate;
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
            return Formats.CURRENCY.formatValue(m_ProductPriceTax * m_ProductUnits);
        }

        /**
         * JG 4 Jun 2014
         *
         * @return
         */
        public String printProductPriceNet() {
            return Formats.CURRENCY.formatValue(m_ProductPrice * m_ProductUnits);
        }

    }

    public static class SalesLine implements SerializableRead {

        private String m_SalesTaxName;
        private Double m_SalesTaxes;
        private Double m_SalesTaxNet;
        private Double m_SalesTaxGross;

        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_SalesTaxName = dr.getString(1);
            m_SalesTaxes = dr.getDouble(2);
            m_SalesTaxNet = dr.getDouble(3);
            m_SalesTaxGross = dr.getDouble(4);
        }

        public String printTaxName() {
            return m_SalesTaxName;
        }

        public String printTaxes() {
            return Formats.CURRENCY.formatValue(m_SalesTaxes);
        }

        public String printTaxNet() {
            return Formats.CURRENCY.formatValue(m_SalesTaxNet);
        }

        public String printTaxGross() {
            return Formats.CURRENCY.formatValue(m_SalesTaxes + m_SalesTaxNet);
        }

        public String getTaxName() {
            return m_SalesTaxName;
        }

        public Double getTaxes() {
            return m_SalesTaxes;
        }

        public Double getTaxNet() {
            return m_SalesTaxNet;
        }

        public Double getTaxGross() {
            return m_SalesTaxGross;
        }
    }

    public static class CloseCash implements SerializableRead {

        private String money;
        private String host;
        private Integer hostsequence;
        private Date datestart;
        private Date dateend;

        @Override
        public void readValues(DataRead dr) throws BasicException {
            money = dr.getString(1);
            host = dr.getString(2);
            hostsequence = dr.getInt(3);
            datestart = dr.getTimestamp(4);
            dateend = dr.getTimestamp(5);
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getHostsequence() {
            return hostsequence;
        }

        public void setHostsequence(Integer hostsequence) {
            this.hostsequence = hostsequence;
        }

        public Date getDatestart() {
            return datestart;
        }

        public void setDatestart(Date datestart) {
            this.datestart = datestart;
        }

        public Date getDateend() {
            return dateend;
        }

        public void setDateend(Date dateend) {
            this.dateend = dateend;
        }

    }

    public AbstractTableModel getSalesModel() {
        return new AbstractTableModel() {
            @Override
            public String getColumnName(int column) {
                return AppLocal.getIntString(SALEHEADERS[column]);
            }

            @Override
            public int getRowCount() {
                return salesLines.size();
            }

            @Override
            public int getColumnCount() {
                return SALEHEADERS.length;
            }

            @Override
            public Object getValueAt(int row, int column) {
                SalesLine l = salesLines.get(row);
                switch (column) {
                    case 0:
                        return l.getTaxName();
                    case 1:
                        return l.getTaxes();
                    case 2:
                        return l.getTaxNet();
                    default:
                        return null;
                }
            }
        };
    }


    public static class PaymentsLine implements SerializableRead {

        private String m_PaymentType;
        private Double m_PaymentValue;
        private String s_PaymentReason;

        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_PaymentType = dr.getString(1);
            m_PaymentValue = dr.getDouble(2);
            s_PaymentReason = dr.getString(3) == null ? "" : dr.getString(3);
        }

        public String printType() {
            return AppLocal.getIntString("transpayment." + m_PaymentType);
        }

        public String getType() {
            return m_PaymentType;
        }

        public String printValue() {
            return Formats.CURRENCY.formatValue(m_PaymentValue);
        }

        public Double getValue() {
            return m_PaymentValue;
        }

        public String printReason() {
            return s_PaymentReason;
        }

        public String getReason() {
            return s_PaymentReason;
        }
    }
}
