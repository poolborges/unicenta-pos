package com.openbravo.data.user;

/*
 * Copyright (C) 2022 Paulo Borges
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
import java.util.regex.*;

/**
 *
 * @author poolborges
 */
public class ResConverXML {
    public static void main(String []args){
        String test = "INSERT INTO resources(id, name, restype, content) VALUES('1', '.01', 1, $FILE{/com/openbravo/images/.01.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('2', '.02', 1, $FILE{/com/openbravo/images/.02.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('3', '.05', 1, $FILE{/com/openbravo/images/.05.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('4', '.10', 1, $FILE{/com/openbravo/images/.10.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('5', '.20', 1, $FILE{/com/openbravo/images/.20.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('6', '.50', 1, $FILE{/com/openbravo/images/.50.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('7', '1', 1, $FILE{/com/openbravo/images/1.00.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('8', '2', 1, $FILE{/com/openbravo/images/2.00.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('9', '5', 1, $FILE{/com/openbravo/images/5.00.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('10', '10', 1, $FILE{/com/openbravo/images/10.00.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('11', '20', 1, $FILE{/com/openbravo/images/20.00.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('12', '50', 1, $FILE{/com/openbravo/images/50.00.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('13', '100', 1, $FILE{/com/openbravo/images/100.00.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('14', '200', 1, $FILE{/com/openbravo/images/200.00.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('15', '500', 1, $FILE{/com/openbravo/images/500.00.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('16', '1000', 1, $FILE{/com/openbravo/images/1000.00.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('17', 'cash', 1, $FILE{/com/openbravo/images/cash.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('18', 'cashdrawer', 1, $FILE{/com/openbravo/images/cashdrawer.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('19', 'discount', 1, $FILE{/com/openbravo/images/discount.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('20', 'discount_b', 1, $FILE{/com/openbravo/images/discount_b.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('21', 'empty', 1, $FILE{/com/openbravo/images/empty.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('22', 'heart', 1, $FILE{/com/openbravo/images/heart.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('23', 'keyboard_48', 1, $FILE{/com/openbravo/images/keyboard_48.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('24', 'kit_print', 1, $FILE{/com/openbravo/images/kit_print.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('25', 'no_photo', 1, $FILE{/com/openbravo/images/no_photo.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('26', 'refundit', 1, $FILE{/com/openbravo/images/refundit.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('27', 'run_script', 1, $FILE{/com/openbravo/images/run_script.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('28', 'ticket_print', 1, $FILE{/com/openbravo/images/ticket_print.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('29', 'img.posapps', 1, $FILE{/com/openbravo/images/img.posapps.png});\n" +
"\n" +
"-- PRINTER\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('30', 'Printer.CloseCash.Preview', 0, $FILE{/com/openbravo/pos/templates/Printer.CloseCash.Preview.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('31', 'Printer.CloseCash', 0, $FILE{/com/openbravo/pos/templates/Printer.CloseCash.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('32', 'Printer.CustomerPaid', 0, $FILE{/com/openbravo/pos/templates/Printer.CustomerPaid.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('33', 'Printer.CustomerPaid2', 0, $FILE{/com/openbravo/pos/templates/Printer.CustomerPaid2.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('34', 'Printer.FiscalTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.FiscalTicket.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('35', 'Printer.Inventory', 0, $FILE{/com/openbravo/pos/templates/Printer.Inventory.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('36', 'Printer.OpenDrawer', 0, $FILE{/com/openbravo/pos/templates/Printer.OpenDrawer.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('37', 'Printer.PartialCash', 0, $FILE{/com/openbravo/pos/templates/Printer.PartialCash.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('38', 'Printer.PrintLastTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.PrintLastTicket.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('39', 'Printer.Product', 0, $FILE{/com/openbravo/pos/templates/Printer.Product.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('40', 'Printer.ReprintTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.ReprintTicket.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('41', 'Printer.Start', 0, $FILE{/com/openbravo/pos/templates/Printer.Start.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('42', 'Printer.Ticket.P1', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P1.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('43', 'Printer.Ticket.P2', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P2.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('44', 'Printer.Ticket.P3', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P3.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('45', 'Printer.Ticket.P4', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P4.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('46', 'Printer.Ticket.P5', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P5.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('47', 'Printer.Ticket.P6', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P6.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('48', 'Printer.Ticket', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('49', 'Printer.Ticket2', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket2.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('50', 'Printer.TicketClose', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketClose.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('51', 'Printer.TicketRemote', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketRemote.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('52', 'Printer.TicketLine', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketLine.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('53', 'Printer.TicketNew', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketLine.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('54', 'Printer.TicketPreview', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketPreview.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('55', 'Printer.TicketTotal', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketTotal.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('56', 'Printer.Ticket.Logo', 1, $FILE{/com/openbravo/images/app_logo_100x100.png});\n" +
"\n" +
"-- SCRIPTS\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('57', 'script.AddLineNote', 0, $FILE{/com/openbravo/pos/templates/script.AddLineNote.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('58', 'script.Event.Total', 0, $FILE{/com/openbravo/pos/templates/script.Event.Total.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('59', 'script.Keyboard', 0, $FILE{/com/openbravo/pos/templates/script.Keyboard.txt});\n" +
"\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('60', 'script.Linediscount', 0, $FILE{/com/openbravo/pos/templates/script.linediscount.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('61', 'script.ReceiptConsolidate', 0, $FILE{/com/openbravo/pos/templates/script.ReceiptConsolidate.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('62', 'script.Refundit', 0, $FILE{/com/openbravo/pos/templates/script.Refundit.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('63', 'script.SendOrder', 0, $FILE{/com/openbravo/pos/templates/script.SendOrder.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('64', 'script.ServiceCharge', 0, $FILE{/com/openbravo/pos/templates/script.ServiceCharge.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('65', 'script.SetPerson', 0, $FILE{/com/openbravo/pos/templates/script.SetPerson.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('66', 'script.StockCurrentAdd', 0, $FILE{/com/openbravo/pos/templates/script.StockCurrentAdd.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('67', 'script.StockCurrentSet', 0, $FILE{/com/openbravo/pos/templates/script.StockCurrentSet.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('68', 'script.Totaldiscount', 0, $FILE{/com/openbravo/pos/templates/script.TotalDiscount.txt});\n" +
"\n" +
"-- SYSTEM\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('69', 'payment.cash', 0, $FILE{/com/openbravo/pos/templates/payment.cash.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('70', 'ticket.addline', 0, $FILE{/com/openbravo/pos/templates/ticket.addline.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('71', 'ticket.change', 0, $FILE{/com/openbravo/pos/templates/ticket.change.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('72', 'Ticket.Buttons', 0, $FILE{/com/openbravo/pos/templates/Ticket.Buttons.xml});\n" +
"\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('73', 'Ticket.Close', 0, $FILE{/com/openbravo/pos/templates/Ticket.Close.xml});\n" +
"\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('74', 'Ticket.Discount', 0, $FILE{/com/openbravo/pos/templates/Ticket.Discount.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('75', 'Ticket.Line', 0, $FILE{/com/openbravo/pos/templates/Ticket.Line.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('76', 'ticket.removeline', 0, $FILE{/com/openbravo/pos/templates/ticket.removeline.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('77', 'ticket.setline', 0, $FILE{/com/openbravo/pos/templates/ticket.setline.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('78', 'Ticket.TicketLineTaxesIncluded', 0, $FILE{/com/openbravo/pos/templates/Ticket.TicketLineTaxesIncluded.xml});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('79', 'Window.Logo', 1, $FILE{/com/openbravo/pos/templates/app_logo_48x48.png});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('80', 'Window.Title', 0, $FILE{/com/openbravo/pos/templates/Window.Title.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('81', 'script.posapps', 0, $FILE{/com/openbravo/pos/templates/script.posapps.txt});\n" +
"INSERT INTO resources(id, name, restype, content) VALUES('82', 'Cash.Close', 0, $FILE{/com/openbravo/pos/templates/Cash.Close.xml});\n" +
"INSERT INTO resources(ID, name, restype, CONTENT) VALUES('83', 'Customer.Created', 0, $FILE{/com/openbravo/pos/templates/customer.created.xml});\n" +
"INSERT INTO resources(ID, name, restype, CONTENT) VALUES('84', 'Customer.Updated', 0, $FILE{/com/openbravo/pos/templates/customer.updated.xml});\n" +
"INSERT INTO resources(ID, name, restype, CONTENT) VALUES('85', 'Customer.Deleted', 0, $FILE{/com/openbravo/pos/templates/customer.deleted.xml});\n" +
"INSERT INTO resources(ID, name, restype, CONTENT) VALUES('86', 'Application.Started', 0, $FILE{/com/openbravo/pos/templates/application.started.xml});\n" +
"";

        String regex = "(INSERT INTO) (\\S+).*\\((.*?)\\).*(VALUES).*\\((.*?)\\)(.*\\;?)";

        Pattern re = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        
        //StringBuilder
        
        Pattern pre1 = Pattern.compile("'(\\S+)'", Pattern.CASE_INSENSITIVE);
        Pattern pre2 = Pattern.compile("'($FILE\\{\\S+\\})'", Pattern.CASE_INSENSITIVE);
        
        
        String xml = "";
        Matcher m = re.matcher(test);
        while (m.find()) {
            String[] field = m.group(3).split(",");
            String[] values = m.group(5).split(",");
            
            String id = values[0];
            String name = values[1];
            String restype = values[2];
            String content = values[3];
            
            xml = String.format(
         "<insert tableName=\"resources\">\n" +
"            <column name=\"id\" value=\"%s\"/>\n" +
"            <column name=\"name\" value=\"%s\"/>\n" +
"            <column name=\"restype\" value=\"%s\"/>\n" +
"            <column name=\"content\" valueBlobFile=\"%s\" />\n" +
"        </insert>",id, name, restype, content);
            
            System.out.println(xml);
        }
     }
}
