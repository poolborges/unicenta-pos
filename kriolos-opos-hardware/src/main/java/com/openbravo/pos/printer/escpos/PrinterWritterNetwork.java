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

    public PrinterWritterNetwork(String hostAddress, int hostPort) {
        this.hostAddress = hostAddress;
        this.m_iPort = hostPort;
        this.clientSocket = null;
        this.outStream = null;
    }

    @Override
    protected void internalWrite(byte[] data) {
        try {
            if (this.outStream == null || this.clientSocket == null) {
                LOGGER.log(Level.INFO, "PrinterNetwork connet to host: "+this.hostAddress + ", port: "+this.m_iPort);
                this.clientSocket = new Socket(this.hostAddress, this.m_iPort);
                this.outStream = new DataOutputStream(this.clientSocket.getOutputStream());
            }
            this.outStream.write(data);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Exception on write network at host: "+this.hostAddress + ", port: "+this.m_iPort, e);
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
            LOGGER.log(Level.WARNING, "Exception on flush to network at host: "+this.hostAddress + ", port: "+this.m_iPort, e);
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
                LOGGER.log(Level.INFO, "PrinterNetwork connet to " 
                        + "host: "+ this.clientSocket.getInetAddress().getHostAddress() 
                        + ", port: "+this.clientSocket.getPort());
                this.clientSocket.close();
                this.clientSocket = null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Exception on close network at host: "+this.hostAddress + ", port: "+this.m_iPort, e);
        }
    }
}
