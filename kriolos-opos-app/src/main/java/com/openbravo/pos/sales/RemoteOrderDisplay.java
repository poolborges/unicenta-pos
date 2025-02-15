package com.openbravo.pos.sales;

import com.openbravo.basic.BasicException;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.ticket.TicketInfo;

/**
 *  Remote Orders Display
 *  We only know about KriolosPOS POS orders and won't try and handle any that are injected from an external source
 *
 */
public class RemoteOrderDisplay {

    private final DataLogicSystem dlSystem;
    private final DataLogicSales dlSales;
    private final TicketInfo ticketInfo;
    private final String orderId;
    private final String ticketExternalId;

    public RemoteOrderDisplay(AppView appView, TicketInfo ticketInfo, String ticketExternalId, String orderId){

        this.ticketInfo = ticketInfo;
        this.orderId = orderId;
        this.ticketExternalId = ticketExternalId;
        dlSystem = (DataLogicSystem) appView.getBean("com.openbravo.pos.forms.DataLogicSystem");
        dlSales = (DataLogicSales) appView.getBean("com.openbravo.pos.forms.DataLogicSales");
    }

    protected final static System.Logger LOGGER = System.getLogger(RemoteOrderDisplay.class.getName());

    public void remoteOrderDisplay() {
        remoteOrderDisplay(remoteOrderId());
    }

    public void remoteOrderDisplay(String orderId) {
        remoteOrderDisplay(orderId, 1, true);
    }

    public void remoteOrderDisplay(Integer display) {
        remoteOrderDisplay(remoteOrderId(), display, false);
    }

    public String remoteOrderId() {

        if ((ticketInfo.getCustomer() != null)) {
            return ticketInfo.getCustomer().getName();
        } else if (ticketExternalId != null) {
            return ticketExternalId;
        } else {
            if (ticketInfo.getPickupId() == 0) {
                try {
                    ticketInfo.setPickupId(dlSales.getNextPickupIndex());
                } catch (BasicException ex) {
                    LOGGER.log(System.Logger.Level.WARNING, "Exception on: ", ex);
                    ticketInfo.setPickupId(0);
                }
            }

            return orderId;
        }
    }

    public void remoteOrderDisplay(Integer display, boolean primary) {
        this.remoteOrderDisplay(remoteOrderId(), display, primary);
    }

    public void remoteOrderDisplay(String orderId, Integer display, boolean primary) {

        try {
            dlSystem.deleteOrder(orderId);
        } catch (BasicException ex) {
            LOGGER.log(System.Logger.Level.WARNING, "Exception on: ", ex);
        }

        for (int i = 0; i < ticketInfo.getLinesCount(); i++) {
            try {
                if (primary) {
                    if ((ticketInfo.getLine(i).getProperty("display") == null)
                            || ("".equals(ticketInfo.getLine(i).getProperty("display")))) {
                        display = 1;
                    } else {
                        display = Integer.parseInt(ticketInfo.getLine(i).getProperty("display"));
                    }
                }

                dlSystem.addOrder(this.orderId,
                        (int) ticketInfo.getLine(i).getMultiply(),
                        ticketInfo.getLine(i).getProductName(),
                        ticketInfo.getLine(i).getProductAttSetInstDesc(),
                        ticketInfo.getLine(i).getProperty("notes"),
                        orderId,
                        null,
                        display,
                        null,
                        null);

                /* this block for future - right now we're deleting all ticketlines
    and resending for consistency with actual ticketlines
                dlSystem.updateOrder(getPickupString(m_oTicket)
                        , (int) m_oTicket.getLine(i).getMultiply()
                        , m_oTicket.getLine(i).getProductName()
                        , m_oTicket.getLine(i).getProductAttSetInstDesc()
                        , m_oTicket.getLine(i).getProperty("notes")
                        , id
                        , null
                        , display
                        , null
                        , null);
                 */
            } catch (BasicException ex) {
                LOGGER.log(System.Logger.Level.WARNING, "Exception on: ", ex);
            }
        }
    }
}
