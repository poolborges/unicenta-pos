//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2018 uniCenta & previous Openbravo POS works
//    Contribution by  Hajinder Singh
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.scale;

import gnu.io.*;
import java.io.*;
import java.util.TooManyListenersException;

/**
*
* @author uniCenta + H Singh
*/
public class ScaleAcomPC100 implements Scale, SerialPortEventListener {

private CommPortIdentifier m_PortIdPrinter;
private SerialPort m_CommPortPrinter;

private final String m_sPortScale;
private OutputStream m_out;
private InputStream m_in;

private static final int SCALE_READY = 0;
private static final int SCALE_READING = 1;
private static final int SCALE_READINGDECIMALS = 2;

private double m_dWeightBuffer;
private double m_dWeightDecimals;
private int m_iStatusScale;
private String m_sScaleReading;

/** Creates a new instance of ScaleComm
* @param sPortPrinter */
    public ScaleAcomPC100(String sPortPrinter) {
        m_sPortScale = sPortPrinter;
        m_out = null;
        m_in = null;
        m_iStatusScale = SCALE_READY;
        m_dWeightBuffer = 0.0;
        m_dWeightDecimals = 1.0;
    }

/**
*
* @return
*/
@Override
public Double readWeight() {

    synchronized(this) {

        if (m_iStatusScale != SCALE_READY) {
        try {
            wait(200);
        } catch (InterruptedException e) {
        }
        if (m_iStatusScale != SCALE_READY) {
            m_iStatusScale = SCALE_READY;
        }
        }

        m_dWeightBuffer = 0.0;
        m_dWeightDecimals = 1.0;
        write(new byte[] {0x57,0X0D}); // $
        flush();

        try {
            wait(200);
        } catch (InterruptedException e) {
        }

        if (m_iStatusScale == SCALE_READY) {
            double dWeight = m_dWeightBuffer / m_dWeightDecimals;
            m_dWeightBuffer = 0.0;
            m_dWeightDecimals = 1.0;
            return dWeight;
        } else {
            m_iStatusScale = SCALE_READY;
            m_dWeightBuffer = 0.0;
            m_dWeightDecimals = 1.0;
            return 0.0;
        }
    }
}

private void flush() {
    try {
        m_out.flush();
    } catch (IOException e) {
    }
}

private void write(byte[] data) {
    try {
        if (m_out == null) {
            m_PortIdPrinter = CommPortIdentifier.getPortIdentifier(m_sPortScale);
            m_CommPortPrinter = (SerialPort) m_PortIdPrinter.open("PORTID", 2000);

            m_out = m_CommPortPrinter.getOutputStream();
            m_in = m_CommPortPrinter.getInputStream();

            m_CommPortPrinter.addEventListener(this);
            m_CommPortPrinter.notifyOnDataAvailable(true);

            m_CommPortPrinter.setSerialPortParams(9600, 
                    SerialPort.DATABITS_7, 
                    SerialPort.STOPBITS_1, 
                    SerialPort.PARITY_EVEN);
        }
        m_out.write(data);
    } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException 
            | TooManyListenersException | IOException e) {
    }
}

/**
*
* @param e
*/
@Override
public void serialEvent(SerialPortEvent e) {

    switch (e.getEventType()) {
        case SerialPortEvent.BI:
        case SerialPortEvent.OE:
        case SerialPortEvent.FE:
        case SerialPortEvent.PE:
        case SerialPortEvent.CD:
        case SerialPortEvent.CTS:
        case SerialPortEvent.DSR:
        case SerialPortEvent.RI:
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            break;
        case SerialPortEvent.DATA_AVAILABLE:
            try {
                int i;
                i = m_in.available();
                byte[] readBuffer = new byte[i];
                if (i > 0) {
                    m_in.read(readBuffer);
                }

                m_sScaleReading = m_sScaleReading + new String(readBuffer);

                try {
                    int start = m_sScaleReading.indexOf((char) 10);
                    int end = m_sScaleReading.indexOf((char) 3);

                if (start >= 0 && end >= 0) {

                    start = m_sScaleReading.indexOf((char) 10);
                    end = m_sScaleReading.indexOf((char) 75);
                    m_dWeightBuffer = Double.parseDouble(m_sScaleReading.substring(start+1, end));
                    m_sScaleReading = "";
                }
            } catch (IndexOutOfBoundsException ex) {
                System.out.println("IndexOutOfBoundsException, message not complete yet. Waiting for more data.");
            }

            } catch (IOException eIO) {}
                break;
        }
    }
}