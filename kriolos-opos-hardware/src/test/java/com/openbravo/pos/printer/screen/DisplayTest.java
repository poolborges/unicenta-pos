/*
 * Copyright (C) 2025 Paulo Borges
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
package com.openbravo.pos.printer.screen;

import com.openbravo.pos.printer.DeviceDisplayBase;
import com.openbravo.pos.util.ThumbNailBuilder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JPanel;

/**
 *
 * @author poolb
 */
public class DisplayTest {

    public static void main(String[] args) {

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {

        DeviceDisplayWindowDualScreen device = new DeviceDisplayWindowDualScreen();
        
        
        Random rand = new Random(5);
        
        
        javax.swing.Timer m_tTimeTimer = new javax.swing.Timer(10000, new PrintTimeAction(device)); 
        m_tTimeTimer.start();
        
        device.setTitle("Customer Display Dest");
        
        device.setCustomerImage(ThumbNailBuilder.createImage(100, 100, Color.YELLOW));
        
        JPanel ticketLine = new JPanel();
        ticketLine.setPreferredSize(new Dimension(720, 480));
        ticketLine.setBackground(Color.MAGENTA);
        device.setTicketLines(ticketLine);
        
    }
    
    private static class PrintTimeAction implements ActionListener {
        
        private DeviceDisplayWindowDualScreen device;
        
        int min = 0;
        int max = 4;
        Random rand = new Random();

// nextInt as provided by Random is exclusive of the top value so you need to add 1 


        public PrintTimeAction(){
        
        }
        
        public PrintTimeAction(DeviceDisplayWindowDualScreen device){
            this.device = device;
        }
        
        @Override
        public void actionPerformed(ActionEvent evt) {
            
            int randomNum = rand.nextInt((max - min) + 1) + min;
            
            //device.writeVisor(randomNum, "Linha 1", "Linha 2");
            
            device.writeVisor(DeviceDisplayBase.ANIMATION_BLINK, "Linha 1."+randomNum, "Linha 2."+randomNum);
        }        
    }   
}
