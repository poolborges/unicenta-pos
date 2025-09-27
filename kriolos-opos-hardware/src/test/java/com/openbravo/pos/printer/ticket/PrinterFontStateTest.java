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
package com.openbravo.pos.printer.ticket;

import com.openbravo.pos.printer.DevicePrinter;
import com.openbravo.pos.printer.DevicePrinter.FontSize;
import java.awt.Font;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PrinterFontStateTest {
    public static void main(String[] args) {
        // Create a base font
        Font baseFont = new Font("Arial", Font.PLAIN, 12);

        // Create instances of the nested PrinterFontStateTest class
        PrinterFontState normalState = new PrinterFontState(FontSize.NORMAL);
        PrinterFontState boldAndUnderlined = new PrinterFontState(FontSize.DOUBLE_WIDTH);
        PrinterFontState doubleHeight = new PrinterFontState(FontSize.DOUBLE_HEIGHT);

        // Get the derived fonts with various styles
        Font normalFont = normalState.getFont(baseFont, DevicePrinter.STYLE_PLAIN);
        Font boldUnderlinedFont = boldAndUnderlined.getFont(baseFont, DevicePrinter.STYLE_BOLD | DevicePrinter.STYLE_UNDERLINE);
        Font doubleHeightFont = doubleHeight.getFont(baseFont, DevicePrinter.STYLE_PLAIN);
        
        Font doubleWidthtFont = new PrinterFontState(FontSize.DOUBLE_WIDTH_HEIGHT).getFont(baseFont, DevicePrinter.STYLE_PLAIN);

        // Print the details to the console
        System.out.println("Normal Font:");
        System.out.println("  Font: " + normalFont);
        System.out.println("  Line Multiplier: " + normalState.getLineMult());
        
        System.out.println("\nBold and Underlined Font:");
        System.out.println("  Font: " + boldUnderlinedFont);
        System.out.println("  Line Multiplier: " + boldAndUnderlined.getLineMult());

        System.out.println("\nDouble Height Font:");
        System.out.println("  Font: " + doubleHeightFont);
        System.out.println("  Line Multiplier: " + doubleHeight.getLineMult());

        // Displaying the fonts in a simple Swing frame
        JFrame frame = new JFrame("Font Demonstration");
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
  
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
  
        JLabel label1 = new JLabel("Normal text.");
        label1.setFont(normalFont);
        panel.add(label1);

        JLabel label2 = new JLabel("Bold, double-width, underlined.");
        label2.setFont(boldUnderlinedFont);
        panel.add(label2);

        JLabel label3 = new JLabel("Double-height.");
        label3.setFont(doubleHeightFont);
        panel.add(label3);
        
        JLabel label4 = new JLabel("Normal text, double-width.");
        label4.setFont(doubleWidthtFont);
        panel.add(label4);
        

        frame.add(panel);
        frame.setVisible(true);
    }
}