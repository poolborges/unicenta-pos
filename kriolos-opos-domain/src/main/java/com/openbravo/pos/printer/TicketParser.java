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
package com.openbravo.pos.printer;

import com.openbravo.basic.BasicException;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.printer.escpos.DeviceDisplayLED8;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.util.AudioUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author JG uniCenta
 */
public class TicketParser extends DefaultHandler {

    private static final Logger LOGGER = Logger.getLogger(TicketParser.class.getName());

    private static SAXParser m_sp = null;

    private DeviceTicket printer;
    private DataLogicSystem dataLogicSystem;

    private StringBuilder currentText;

    private String barcodeType;
    private String barcodePosition;
    private int textAlignment;
    private int textLength;
    private int textStyle;
    private int size;

    private StringBuilder visorLineBuilder;
    private int visorAnimation;
    private String visorLine1;
    private String visorLine2;

    private double fiscalTicketLinePrice;
    private double fiscalTicketLineQty;
    private double fiscalTicketTotalPaid;
    private int fiscalTicketLineTaxInfo;

    private int outputType;
    private static final int OUTPUT_NONE = 0;
    private static final int OUTPUT_DISPLAY = 1;
    private static final int OUTPUT_TICKET = 2;
    private static final int OUTPUT_FISCAL = 3;
    private DevicePrinter outputPrinter;
    private final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private final Date today;
    private String currentUser;
    private String ticketId;
    private String pickupId;


    public TicketParser(DeviceTicket printer, DataLogicSystem system) {
        this.printer = printer;
        this.dataLogicSystem = system;
        this.today = Calendar.getInstance().getTime();
    }

    public void printTicket(String sIn, TicketInfo ticket) throws TicketPrinterException {
        this.currentUser = ticket.getName();
        this.ticketId = Integer.toString(ticket.getTicketId());
        this.pickupId = Integer.toString(ticket.getPickupId());

        if (ticket.getTicketId() == 0) {
            this.ticketId = "No Sale";
        }
        if (ticket.getPickupId() == 0) {
            this.pickupId = "No PickupId";
        }
        printTicket(new StringReader(sIn));

    }

    public void printTicket(String sIn) throws TicketPrinterException {
        printTicket(new StringReader(sIn));
    }

    public void printTicket(Reader in) throws TicketPrinterException {

        try {

            if (m_sp == null) {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                m_sp = spf.newSAXParser();
            }
            m_sp.parse(new InputSource(in), this);

        } catch (ParserConfigurationException ePC) {
            throw new TicketPrinterException("exception.parserconfig", ePC);
        } catch (SAXException eSAX) {
            throw new TicketPrinterException("exception.xmlfile", eSAX);
        } catch (IOException eIO) {
            throw new TicketPrinterException("exception.iofile", eIO);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        // inicalizo las variables pertinentes
        currentText = null;
        barcodeType = null;
        barcodePosition = null;
        visorLineBuilder = null;
        visorAnimation = DeviceDisplayBase.ANIMATION_NULL;
        visorLine1 = null;
        visorLine2 = null;
        outputType = OUTPUT_NONE;
        outputPrinter = null;
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String openDate = df.format(today);
        Date dNow = new Date();

        switch (outputType) {
            case OUTPUT_NONE:
                switch (qName) {
                    case "opendrawer":
                        printer.getDevicePrinter(readString(attributes.getValue("printer"), "1")).openDrawer();
                        // Cashdrawer has been activated record the data in the table
                        try {
                            dataLogicSystem.execDrawerOpened(
                                    //new Object[] {df.format(dNow),cUser,ticketId});
                                    new Object[]{currentUser, ticketId});
                        } catch (BasicException ex) {
                        }
                        break;
                    case "play":
                        currentText = new StringBuilder();
                        break;
                    case "ticket":
                        outputType = OUTPUT_TICKET;
                        outputPrinter = printer.getDevicePrinter(readString(attributes.getValue("printer"), "1"));
                        outputPrinter.beginReceipt();
                        break;
                    case "display":
                        outputType = OUTPUT_DISPLAY;
                        String animation = attributes.getValue("animation");
                        if ("scroll".equals(animation)) {
                            visorAnimation = DeviceDisplayBase.ANIMATION_SCROLL;
                        } else if ("flyer".equals(animation)) {
                            visorAnimation = DeviceDisplayBase.ANIMATION_FLYER;
                        } else if ("blink".equals(animation)) {
                            visorAnimation = DeviceDisplayBase.ANIMATION_BLINK;
                        } else if ("curtain".equals(animation)) {
                            visorAnimation = DeviceDisplayBase.ANIMATION_CURTAIN;
                        } else { // "none"
                            visorAnimation = DeviceDisplayBase.ANIMATION_NULL;
                        }
                        visorLine1 = null;
                        visorLine2 = null;
                        outputPrinter = null;
                        break;
                    case "fiscalreceipt":
                        outputType = OUTPUT_FISCAL;
                        printer.getFiscalPrinter().beginReceipt();
                        break;
                    case "fiscalzreport":
                        printer.getFiscalPrinter().printZReport();
                        break;
                    case "fiscalxreport":
                        printer.getFiscalPrinter().printXReport();
                        break;
                }
                break;
            case OUTPUT_TICKET:
                if ("logo".equals(qName)) {
                    currentText = new StringBuilder();
                } else if ("image".equals(qName)) {
                    currentText = new StringBuilder();
                } else if ("barcode".equals(qName)) {
                    currentText = new StringBuilder();
                    barcodeType = attributes.getValue("type");
                    barcodePosition = attributes.getValue("position");
                } else if ("line".equals(qName)) {
                    outputPrinter.beginLine(parseInt(attributes.getValue("size"), DevicePrinter.SIZE_0));
                } else if ("text".equals(qName)) {
                    currentText = new StringBuilder();
                    textStyle = ("true".equals(attributes.getValue("bold"))
                            ? DevicePrinter.STYLE_BOLD : DevicePrinter.STYLE_PLAIN)
                            | ("true".equals(attributes.getValue("underline"))
                            ? DevicePrinter.STYLE_UNDERLINE : DevicePrinter.STYLE_PLAIN);
                    String sAlign = attributes.getValue("align");
                    if ("right".equals(sAlign)) {
                        textAlignment = DevicePrinter.ALIGN_RIGHT;
                    } else if ("center".equals(sAlign)) {
                        textAlignment = DevicePrinter.ALIGN_CENTER;
                    } else {
                        textAlignment = DevicePrinter.ALIGN_LEFT;
                    }
                    textLength = parseInt(attributes.getValue("length"), 0);
                }
                break;
            case OUTPUT_DISPLAY:
                if ("line".equals(qName)) { // line 1 or 2 of the display
                    visorLineBuilder = new StringBuilder();
                } else if ("line1".equals(qName)) { // linea 1 del visor
                    visorLineBuilder = new StringBuilder();
                } else if ("line2".equals(qName)) { // linea 2 del visor
                    visorLineBuilder = new StringBuilder();
                } else if ("text".equals(qName)) {
                    currentText = new StringBuilder();
                    String sAlign = attributes.getValue("align");
                    if ("right".equals(sAlign)) {
                        textAlignment = DevicePrinter.ALIGN_RIGHT;
                    } else if ("center".equals(sAlign)) {
                        textAlignment = DevicePrinter.ALIGN_CENTER;
                    } else {
                        textAlignment = DevicePrinter.ALIGN_LEFT;
                    }
                    textLength = parseInt(attributes.getValue("length"));
                }
                break;
            case OUTPUT_FISCAL:
                if ("line".equals(qName)) {
                    currentText = new StringBuilder();
                    fiscalTicketLinePrice = parseDouble(attributes.getValue("price"));
                    fiscalTicketLineQty = parseDouble(attributes.getValue("units"), 1.0);
                    fiscalTicketLineTaxInfo = parseInt(attributes.getValue("tax"));

                } else if ("message".equals(qName)) {
                    currentText = new StringBuilder();
                } else if ("total".equals(qName)) {
                    currentText = new StringBuilder();
                    fiscalTicketTotalPaid = parseDouble(attributes.getValue("paid"));
                }
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        switch (outputType) {
            case OUTPUT_NONE:
                if ("play".equals(qName)) {
                    AudioUtils.play(this.currentText.toString());
                    currentText = null;
                }
                break;
         
            case OUTPUT_TICKET:
                if ("logo".equals(qName)) {
                    //Star TSP700 to print stored logo image JDL
                    outputPrinter.printLogo();     
                } else if ("image".equals(qName)) {
                    try {
                        // BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(m_sText.toString()));
                        BufferedImage image = dataLogicSystem.getResourceAsImage(currentText.toString());
                        if (image != null) {
                            outputPrinter.printImage(image);
                        }
                    } catch (Exception fnfe) {
                        //throw new ResourceNotFoundException( fnfe.getMessage() );
                    }
                    currentText = null;
                } else if ("barcode".equals(qName)) {
                    outputPrinter.printBarCode(barcodeType,
                            barcodePosition,
                            currentText.toString());
                    currentText = null;
                } else if ("text".equals(qName)) {
                    if (textLength > 0) {
                        switch (textAlignment) {
                            case DevicePrinter.ALIGN_RIGHT:
                                outputPrinter.printText(textStyle, DeviceTicket.alignRight(currentText.toString(), textLength));
                                break;
                            case DevicePrinter.ALIGN_CENTER:
                                outputPrinter.printText(textStyle, DeviceTicket.alignCenter(currentText.toString(), textLength));
                                break;
                            default: // DevicePrinter.ALIGN_LEFT
                                outputPrinter.printText(textStyle, DeviceTicket.alignLeft(currentText.toString(), textLength));
                                break;
                        }
                    } else {
                        outputPrinter.printText(textStyle, currentText.toString());
                    }
                    currentText = null;
                } else if ("line".equals(qName)) {
                    outputPrinter.endLine();
                } else if ("ticket".equals(qName)) {
                    outputPrinter.endReceipt();
                    outputType = OUTPUT_NONE;
                    outputPrinter = null;
                }
                break;
            case OUTPUT_DISPLAY:
                if ("line".equals(qName)) { // line 1 or 2 of the display
                    if (visorLine1 == null) {
                        visorLine1 = visorLineBuilder.toString();
                    } else {
                        visorLine2 = visorLineBuilder.toString();
                    }
                    visorLineBuilder = null;
                } else if ("line1".equals(qName)) { // linea 1 del visor
                    visorLine1 = visorLineBuilder.toString();
                    visorLineBuilder = null;
                } else if ("line2".equals(qName)) { // linea 2 del visor
                    visorLine2 = visorLineBuilder.toString();
                    visorLineBuilder = null;
                } else if ("text".equals(qName)) {
                    if (textLength > 0) {
                        switch (textAlignment) {
                            case DevicePrinter.ALIGN_RIGHT:
                                visorLineBuilder.append(DeviceTicket.alignRight(currentText.toString(), textLength));
                                break;
                            case DevicePrinter.ALIGN_CENTER:
                                visorLineBuilder.append(DeviceTicket.alignCenter(currentText.toString(), textLength));
                                break;
                            default: // DevicePrinter.ALIGN_LEFT
                                visorLineBuilder.append(DeviceTicket.alignLeft(currentText.toString(), textLength));
                                break;
                        }
                    } else {
                        visorLineBuilder.append(currentText);
                    }
                    if (this.textStyle > -1 && this.printer.getDeviceDisplay() instanceof DeviceDisplayLED8) {
                        ((DeviceDisplayLED8) this.printer.getDeviceDisplay()).displayLight(this.textStyle);
                    }
                    currentText = null;
                } else if ("display".equals(qName)) {
                    printer.getDeviceDisplay().writeVisor(visorAnimation, visorLine1, visorLine2);
                    visorAnimation = DeviceDisplayBase.ANIMATION_NULL;
                    visorLine1 = null;
                    visorLine2 = null;
                    outputType = OUTPUT_NONE;
                    outputPrinter = null;
                }
                break;
            case OUTPUT_FISCAL:
                if ("fiscalreceipt".equals(qName)) {
                    printer.getFiscalPrinter().endReceipt();
                    outputType = OUTPUT_NONE;
                } else if ("line".equals(qName)) {
                    printer.getFiscalPrinter().printLine(currentText.toString(), fiscalTicketLinePrice, fiscalTicketLineQty, fiscalTicketLineTaxInfo);
                    currentText = null;
                } else if ("message".equals(qName)) {
                    printer.getFiscalPrinter().printMessage(currentText.toString());
                    currentText = null;
                } else if ("total".equals(qName)) {
                    printer.getFiscalPrinter().printTotal(currentText.toString(), fiscalTicketTotalPaid);
                    currentText = null;
                }
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentText != null) {
            currentText.append(ch, start, length);
        }
    }

    private int parseInt(String sValue, int iDefault) {
        try {
            return Integer.parseInt(sValue);
        } catch (NumberFormatException eNF) {
            return iDefault;
        }
    }

    private int parseInt(String sValue) {
        return parseInt(sValue, 0);
    }

    private double parseDouble(String sValue, double ddefault) {
        try {
            return Double.parseDouble(sValue);
        } catch (NumberFormatException eNF) {
            return ddefault;
        }
    }

    private double parseDouble(String sValue) {
        return parseDouble(sValue, 0.0);
    }

    private String readString(String sValue, String sDefault) {
        if (sValue == null || sValue.equals("")) {
            return sDefault;
        } else {
            return sValue;
        }
    }
}
