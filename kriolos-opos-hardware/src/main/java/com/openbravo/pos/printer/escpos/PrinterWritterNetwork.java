package com.openbravo.pos.printer.escpos;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrinterWritterNetwork extends PrinterWritter {

    private static final Logger LOGGER = Logger.getLogger(PrinterWritterNetwork.class.getName());
    
    private final String hostAddress;
    private final int m_iPort;
    private Socket clientSocket;
    private OutputStream outStream;

    public PrinterWritterNetwork(String sFilePrinter, int hostPort) {
        this.hostAddress = sFilePrinter;
        this.m_iPort = hostPort;
        this.clientSocket = null;
        this.outStream = null;
    }

    @Override
    protected void internalWrite(byte[] data) {
        try {
            if (this.outStream == null) {
                this.clientSocket = new Socket(this.hostAddress, this.m_iPort);
                this.outStream = new DataOutputStream(this.clientSocket.getOutputStream());
            }
            this.outStream.write(data);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Exception on write", e);
        }
    }

    @Override
    protected void internalFlush() {
        try {
            if (this.outStream != null) {
                this.outStream.flush();
                this.outStream.close();
                this.outStream = null;
            }
            if(clientSocket != null){
                this.clientSocket.close();
                this.clientSocket = null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Exception on flush", e);
        }
    }

    @Override
    protected void internalClose() {
        try {
            if (this.outStream != null) {
                this.outStream.flush();
                this.outStream.close();
                this.outStream = null;
            }
            if(clientSocket != null){
                this.clientSocket.close();
                this.clientSocket = null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Exception on close", e);
        }
    }
}
