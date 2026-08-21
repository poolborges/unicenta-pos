package com.openbravo.pos.reports;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of FinancialReportService.
 */
public class FinancialReportServiceImpl implements FinancialReportService {

    private final Session session;

    public FinancialReportServiceImpl(Session session) {
        this.session = session;
    }

    @Override
    public FinancialReport getFinancialReport(String money, Date dateStart, Date dateEnd) throws BasicException {
        FinancialReport report = new FinancialReport();
        report.setDateStart(dateStart);
        report.setDateEnd(dateEnd);

        // 1. Payments (Count, SUM)
        Object[] valtickets = (Object[]) new PreparedSentence(session,
                "SELECT COUNT(*) AS total_count, COALESCE(SUM(payments.TOTAL), 0) AS total_sum "
                        + "FROM payments, receipts "
                        + "WHERE payments.RECEIPT = receipts.ID AND receipts.MONEY = ?",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[] { Datas.INT, Datas.DOUBLE }))
                .find(money);

        //check columns
        if (valtickets != null && valtickets.length == 2) {
            report.setPaymentCount((Integer) valtickets[0]);
            report.setPaymentTotal((Double) valtickets[1]);
        } else {
            
            report.setPaymentCount(0);
            report.setPaymentTotal(0.0);
        }

        // 2. Payment Lines
        List<PaymentsListLine> paymentLines = new PreparedSentence(session,
                "SELECT payments.PAYMENT, SUM(payments.TOTAL), payments.NOTES, COUNT(payments.PAYMENT) "
                        + "FROM payments, receipts "
                        + "WHERE payments.RECEIPT = receipts.ID AND receipts.MONEY = ? "
                        + "GROUP BY payments.PAYMENT, payments.NOTES",
                SerializerWriteString.INSTANCE,
                new SerializerReadClass(PaymentsListLine.class))
                .list(money);

        report.setPaymentLines(paymentLines != null ? paymentLines : new ArrayList<>());

        // 3. Category Sales Summary
        Object[] valcategorysales = (Object[]) new PreparedSentence(session,
                "SELECT COUNT(*), "
                        + "SUM(ticketlines.UNITS), "
                        + "SUM((ticketlines.PRICE + ticketlines.PRICE * taxes.RATE ) * ticketlines.UNITS) "
                        + "FROM ticketlines, tickets, receipts, taxes "
                        + "WHERE ticketlines.TICKET = tickets.ID AND tickets.ID = receipts.ID "
                        + "AND ticketlines.TAXID = taxes.ID "
                        + "AND ticketlines.PRODUCT IS NOT NULL "
                        + "AND receipts.MONEY = ? "
                        + "GROUP BY receipts.MONEY",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[] { Datas.INT, Datas.DOUBLE, Datas.DOUBLE }))
                .find(money);

        //check columns
        if (valcategorysales != null && valcategorysales.length == 3) {
            report.setCategorySalesRows((Integer) valcategorysales[0]);
            report.setCategorySalesTotalUnits((Double) valcategorysales[1]);
            report.setCategorySalesTotal((Double) valcategorysales[2]);
        } else {
            report.setCategorySalesRows(0);
            report.setCategorySalesTotalUnits(0.0);
            report.setCategorySalesTotal(0.0);
        }

        // 4. Category Sales Lines
        List<CategorySalesLine> categorys = new PreparedSentence(session,
                "SELECT a.NAME, SUM(c.UNITS), SUM(c.UNITS * (c.PRICE + (c.PRICE * d.RATE))) "
                        + "FROM categories as a "
                        + "LEFT JOIN products as b on a.id = b.CATEGORY "
                        + "LEFT JOIN ticketlines as c on b.id = c.PRODUCT "
                        + "LEFT JOIN taxes as d on c.TAXID = d.ID "
                        + "LEFT JOIN receipts as e on c.TICKET = e.ID "
                        + "WHERE e.MONEY = ? "
                        + "GROUP BY a.NAME",
                SerializerWriteString.INSTANCE,
                new SerializerReadClass(CategorySalesLine.class))
                .list(money);

        report.setCategorySalesLines(categorys != null ? categorys : new ArrayList<>());

        // 5. Sales Summary
        Object[] recsales = (Object[]) new PreparedSentence(session,
                "SELECT COUNT(DISTINCT receipts.ID), COALESCE(SUM(ticketlines.UNITS * ticketlines.PRICE), 0) "
                        + "FROM receipts, ticketlines "
                        + "WHERE receipts.ID = ticketlines.TICKET AND receipts.MONEY = ?",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[] { Datas.INT, Datas.DOUBLE }))
                .find(money);

        //check columns
        if (recsales != null && recsales.length == 2) {
            report.setSalesCount((Integer) recsales[0]);
            report.setSalesBase((Double) recsales[1]);
        } else {
            report.setSalesCount(0);
            report.setSalesBase(0.0);
        }

        // 6. Taxes Summary
        Object[] rectaxes = (Object[]) new PreparedSentence(session,
                "SELECT COALESCE(SUM(taxlines.AMOUNT), 0), COALESCE(SUM(taxlines.BASE), 0) "
                        + "FROM receipts, taxlines "
                        + "WHERE receipts.ID = taxlines.RECEIPT AND receipts.MONEY = ?",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[] { Datas.DOUBLE, Datas.DOUBLE }))
                .find(money);

        //check columns
        if (rectaxes != null && rectaxes.length == 2) {
            report.setSalesTaxes((Double) rectaxes[0]);
        } else {
            report.setSalesTaxes(0.0);
        }

        // 7. Sales Lines (Taxes breakdown)
        List<SalesLine> asales = new PreparedSentence(session,
                "SELECT taxcategories.NAME, COALESCE(SUM(taxlines.AMOUNT),0), COALESCE(SUM(taxlines.BASE),0), COALESCE(SUM(taxlines.BASE + taxlines.AMOUNT),0) "
                        + "FROM receipts, taxlines, taxes, taxcategories "
                        + "WHERE receipts.ID = taxlines.RECEIPT AND taxlines.TAXID = taxes.ID AND taxes.CATEGORY = taxcategories.ID "
                        + "AND receipts.MONEY = ?"
                        + "GROUP BY taxcategories.NAME",
                SerializerWriteString.INSTANCE,
                new SerializerReadClass(SalesLine.class))
                .list(money);

        report.setSalesLines(asales != null ? asales : new ArrayList<>());

        // 8. Removed Lines
        List<RemovedProductLines> removedLines = new PreparedSentence(session,
                "SELECT lineremoved.NAME, lineremoved.TICKETID, lineremoved.PRODUCTNAME, COALESCE(SUM(lineremoved.UNITS),0) AS TOTAL_UNITS  "
                        + "FROM lineremoved "
                        + "WHERE lineremoved.REMOVEDDATE > ? "
                        + "GROUP BY lineremoved.NAME, lineremoved.TICKETID, lineremoved.PRODUCTNAME",
                SerializerWriteDate.INSTANCE,
                new SerializerReadClass(RemovedProductLines.class))
                .list(dateStart);

        report.setRemovedProductLines(removedLines != null ? removedLines : new ArrayList<>());

        // 9. Drawer Opened Lines
        List<DrawerOpenedLines> drawerOpenedLines = new PreparedSentence(session,
                "SELECT OPENDATE, NAME, TICKETID  "
                        + "FROM draweropened "
                        + "WHERE TICKETID = 'No Sale' AND OPENDATE > ? "
                        + "GROUP BY NAME, OPENDATE, TICKETID",
                SerializerWriteDate.INSTANCE,
                new SerializerReadClass(DrawerOpenedLines.class))
                .list(dateStart);

        report.setDrawerOpenedLines(drawerOpenedLines != null ? drawerOpenedLines : new ArrayList<>());

        // 10. Product Sales Summary
        Object[] valproductsales = (Object[]) new PreparedSentence(session,
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
                new SerializerReadBasic(new Datas[] { Datas.INT, Datas.DOUBLE, Datas.DOUBLE }))
                .find(money);

        //check columns
        if (valproductsales != null && valproductsales.length == 3) {
            report.setProductSalesRows((Integer) valproductsales[0]);
            report.setProductSalesTotalUnits((Double) valproductsales[1]);
            report.setProductSalesTotal((Double) valproductsales[2]);
        } else {
            report.setProductSalesRows(0);
            report.setProductSalesTotalUnits(0.0);
            report.setProductSalesTotal(0.0);
        }

        // 11. Product Sales Lines
        List<ProductSalesLine> products = new PreparedSentence(session,
                "SELECT products.NAME, SUM(ticketlines.UNITS), ticketlines.PRICE, taxes.RATE "
                        + "FROM ticketlines, tickets, receipts, products, taxes "
                        + "WHERE ticketlines.PRODUCT = products.ID "
                        + "AND ticketlines.TICKET = tickets.ID "
                        + "AND tickets.ID = receipts.ID "
                        + "AND ticketlines.TAXID = taxes.ID "
                        + "AND receipts.MONEY = ? "
                        + "GROUP BY products.NAME, ticketlines.PRICE, taxes.RATE",
                SerializerWriteString.INSTANCE,
                new SerializerReadClass(ProductSalesLine.class))
                .list(money);

        report.setProductSalesLines(products != null ? products : new ArrayList<>());

        return report;
    }
}
