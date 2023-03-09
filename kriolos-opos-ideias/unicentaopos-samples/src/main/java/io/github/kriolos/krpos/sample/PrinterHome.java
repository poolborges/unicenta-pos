/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.kriolos.krpos.sample;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class PrinterHome extends javax.swing.JFrame {

    Double totalAmount = 0.0;
    Double cash = 0.0;
    Double balance = 0.0;
    Double bHeight = 0.0;

    ArrayList<String> itemName = new ArrayList<>();
    ArrayList<String> quantity = new ArrayList<>();
    ArrayList<String> itemPrice = new ArrayList<>();
    ArrayList<String> subtotal = new ArrayList<>();

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String sqr;

    String iname;
    Double iquantity;
    Double iprice;
    Double iamount;
    Double ibillno;

    public PrinterHome() {
       // initComponents();
       // conn = DBConnect.connect();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
/*
        iname = txtitemname.getText();
        iquantity = Double.valueOf(txtquantity.getText());
        iprice = Double.valueOf(txtprice.getText());
        iamount = Double.valueOf(txtsubtotal.getText());
        ibillno = Double.valueOf(txtbillno.getText());
        totalAmount = totalAmount + Double.valueOf(txtsubtotal.getText());
        txttotalAmount.setText(totalAmount + "");
*/
        try {
            String qr = "INSERT INTO `sale`(`bill_no`, `item_name`, `quantity`, `item_price`, `amount`) VALUES ('" + ibillno + "','" + iname + "','" + iquantity + "','" + iprice + "','" + iamount + "')";
            pst = conn.prepareStatement(qr);
            pst.execute();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(rootPane, e);
        }

        clear();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        getData();
        getBillData();
        bHeight = Double.valueOf(itemName.size());
        //JOptionPane.showMessageDialog(rootPane, bHeight);

        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(new BillPrintable(), getPageFormat(pj));
        try {
            pj.print();

        } catch (PrinterException ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void getData() {
        String billNo = "123456"; //txtbillno.getText();

        try {
            String sql = "SELECT `bill_no`, `item_name`, `quantity`, `item_price`, `amount` FROM `sale` WHERE bill_no='" + billNo + "'";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                itemName.add(rs.getString("item_name"));
                quantity.add(rs.getString("quantity"));
                itemPrice.add(rs.getString("item_price"));
                subtotal.add(rs.getString("amount"));

            }
        } catch (Exception e) {
        }
    }

    private void getBillData() {
        String billNo = "123456"; 
        try {
            String sql = "SELECT `bill_no`, `total_amount`, `cash`, `balance` FROM `cash` WHERE bill_no='" + billNo + "'";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                totalAmount = rs.getDouble("total_amount");
                cash = rs.getDouble("cash");

                balance = rs.getDouble("balance");

            }
        } catch (Exception e) {
        }
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

         String billNo = "123456"; //txtbillno.getText() 
         double totalAmount = 99.99; //txttotalAmount.getText()
         double cash = 9.99; //txtcash.getText() 
         double tbalance = 19.99; //txtbalance.getText()
        try {
            String qr = "INSERT INTO `cash`(`bill_no`, `total_amount`, `cash`, `balance`) VALUES ('" + billNo+ "','" + totalAmount + "','" + cash + "','" + tbalance + "')";
            pst = conn.prepareStatement(qr);
            pst.execute();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(rootPane, e);
        }

    }//GEN-LAST:event_jButton3ActionPerformed

    private void clear() {
        //txtitemname.setText("");
        //txtquantity.setText("");
        //txtprice.setText("");
        //txtsubtotal.setText("");
    }

    public PageFormat getPageFormat(PrinterJob pj) {

        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();

        double bodyHeight = bHeight;
        double headerHeight = 5.0;
        double footerHeight = 5.0;
        double width = cm_to_pp(8);
        double height = cm_to_pp(headerHeight + bodyHeight + footerHeight);
        paper.setSize(width, height);
        paper.setImageableArea(0, 10, width, height - cm_to_pp(1));

        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);

        return pf;
    }

    protected static double cm_to_pp(double cm) {
        return toPPI(cm * 0.393600787);
    }

    protected static double toPPI(double inch) {
        return inch * 72d;
    }

    public class BillPrintable implements Printable {
        
        String billNo = "123456"; //txtbillno.getText() 
         double totalAmount = 99.99; //txttotalAmount.getText()
         double cash = 9.99; //txtcash.getText() 
         double tbalance = 19.99; //txtbalance.getText()

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                throws PrinterException {

            int r = itemName.size();
            ImageIcon icon = new ImageIcon("posinvoice/mylogo.jpg");
            int result = NO_SUCH_PAGE;
            if (pageIndex == 0) {

                Graphics2D g2d = (Graphics2D) graphics;
                double width = pageFormat.getImageableWidth();
                g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

                //  FontMetrics metrics=g2d.getFontMetrics(new Font("Arial",Font.BOLD,7));
                try {
                    int y = 20;
                    int yShift = 10;
                    int headerRectHeight = 15;
                    // int headerRectHeighta=40;

                    g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));
                    g2d.drawImage(icon.getImage(), 50, 20, 90, 30, rootPane);
                    y += yShift + 30;
                    g2d.drawString("-------------------------------------", 12, y);
                    y += yShift;
                    g2d.drawString("         CodeGuid.com        ", 12, y);
                    y += yShift;
                    g2d.drawString("   No 00000 Address Line One ", 12, y);
                    y += yShift;
                    g2d.drawString("   Address Line 02 SRI LANKA ", 12, y);
                    y += yShift;
                    g2d.drawString("   www.facebook.com/CodeGuid ", 12, y);
                    y += yShift;
                    g2d.drawString("        +94700000000      ", 12, y);
                    y += yShift;
                    g2d.drawString("-------------------------------------", 12, y);
                    y += headerRectHeight;

                    g2d.drawString(" Item Name                  Price   ", 10, y);
                    y += yShift;
                    g2d.drawString("-------------------------------------", 10, y);
                    y += headerRectHeight;

                    for (int s = 0; s < r; s++) {
                        g2d.drawString(" " + itemName.get(s) + "                            ", 10, y);
                        y += yShift;
                        g2d.drawString("      " + quantity.get(s) + " * " + itemPrice.get(s), 10, y);
                        g2d.drawString(subtotal.get(s), 160, y);
                        y += yShift;

                    }

                    g2d.drawString("-------------------------------------", 10, y);
                    y += yShift;
                    g2d.drawString(" Total amount:               " + totalAmount + "   ", 10, y);
                    y += yShift;
                    g2d.drawString("-------------------------------------", 10, y);
                    y += yShift;
                    g2d.drawString(" Cash      :                 " + cash + "   ", 10, y);
                    y += yShift;
                    g2d.drawString("-------------------------------------", 10, y);
                    y += yShift;
                    g2d.drawString(" Balance   :                 " + balance + "   ", 10, y);
                    y += yShift;

                    g2d.drawString("*************************************", 10, y);
                    y += yShift;
                    g2d.drawString("       THANK YOU COME AGAIN            ", 10, y);
                    y += yShift;
                    g2d.drawString("*************************************", 10, y);
                    y += yShift;
                    g2d.drawString("       SOFTWARE BY:CODEGUID          ", 10, y);
                    y += yShift;
                    g2d.drawString("   CONTACT: contact@codeguid.com       ", 10, y);
                    y += yShift;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                result = PAGE_EXISTS;
            }
            return result;
        }
    }
}
