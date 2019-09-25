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

package com.openbravo.pos.ticket;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

/**
 *
 * @author Mikel Irurita
 */
public class FindTicketsRenderer extends DefaultListCellRenderer {
    
    private final Icon icoTicketNormal;
    private final Icon icoTicketRefund;
    private final Icon icoTicketRefunded;
    /**
     *
     */
    public static final int RECEIPT_NORMAL = 0;
    public static final int RECEIPT_REFUND = 1;
    
    /** Creates a new instance of TicketRenderer */
    public FindTicketsRenderer() {
        this.icoTicketNormal = new ImageIcon(getClass().getClassLoader().getResource("com/openbravo/images/pay.png"));
        this.icoTicketRefund = new ImageIcon(getClass().getClassLoader().getResource("com/openbravo/images/refundit.png"));
        this.icoTicketRefunded = new ImageIcon(getClass().getClassLoader().getResource("com/openbravo/images/cancel.png"));        
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);

        int ticketType = ((FindTicketsInfo)value).getTicketType();
        int ticketStatus = ((FindTicketsInfo)value).getTicketStatus();
        
        setText("<html><table>" + value.toString() +"</table></html>");
        
        if (ticketType == RECEIPT_NORMAL) {
            setIcon(icoTicketNormal); 
        } else if (ticketType == RECEIPT_REFUND) {
                setIcon(icoTicketRefund);
        } else if (ticketType == RECEIPT_NORMAL && ticketStatus > 0) {
                setIcon(icoTicketRefunded);
        }
        
        return this;
    }   
}
