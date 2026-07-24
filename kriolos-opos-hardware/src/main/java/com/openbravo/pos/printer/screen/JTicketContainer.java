/*
 * Copyright (C) 2022 KriolOS
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
 * along with this program.  If not, see <http://gnu.org>.
 */
package com.openbravo.pos.printer.screen;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Container panel that stores and dynamically positions JTicket view components.
 * Uses a dynamic FlowLayout mechanism to wrap components safely without clipping.
 * Includes a public option API to clear and purge ticket history views.
 * 
 * @author Adrian
 * @author KriolOS
 */
public class JTicketContainer extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int HORIZONTAL_GAP = 8;
    private static final int VERTICAL_GAP = 8;
    
    /**
     * Creates new form JTicketContainer
     */
    public JTicketContainer() {
        initComponents();
        // Align tickets to the left and apply proper gap spacing between them
        setLayout(new FlowLayout(FlowLayout.LEFT, HORIZONTAL_GAP, VERTICAL_GAP));
    }

    /**
     * Dynamically calculates the preferred height based on the current width 
     * of the container to ensure scrollbars trigger correctly when text sizes expand.
     * 
     * @return The dynamic Dimension required to fit all rows safely
     */
    @Override
    public Dimension getPreferredSize() { 
        synchronized (getTreeLock()) {
            int width = getWidth();
            if (width == 0) {
                width = 700; // Fallback initial default width if not yet rendered
            }
            
            Insets ins = getInsets();
            int maxWidth = width - ins.left - ins.right;
            int currentX = HORIZONTAL_GAP;
            int currentY = ins.top + VERTICAL_GAP;
            int maxRowHeight = 0;
            
            int componentCount = getComponentCount();
            for (int i = 0; i < componentCount; i++) {
                Component comp = getComponent(i);
                if (comp.isVisible()) {
                    Dimension dc = comp.getPreferredSize();
                    
                    // Wrap to the next line if the component exceeds the maximum width boundary
                    if (currentX + dc.width > maxWidth && currentX > HORIZONTAL_GAP) {
                        currentX = HORIZONTAL_GAP;
                        currentY += VERTICAL_GAP + maxRowHeight;
                        maxRowHeight = 0;
                    }
                    
                    currentX += dc.width + HORIZONTAL_GAP;
                    maxRowHeight = Math.max(maxRowHeight, dc.height);
                }
            }
            
            int totalHeight = currentY + maxRowHeight + VERTICAL_GAP + ins.bottom;
            return new Dimension(width, Math.max(totalHeight, 600));
        }
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(700, 600);
    }
    
    /**
     * Appends a new ticket component to the layout and safely scrolls the view down.
     * 
     * @param ticket The visual receipt view component to display
     */
    public void addTicket(JTicket ticket) {
        add(ticket);
        revalidate();
        repaint();
        
        // Ensure scrolling occurs asynchronously after the component layout completes on the EDT
        SwingUtilities.invokeLater(() -> {
            int componentCount = getComponentCount();
            if (componentCount > 0) {
                Component lastComp = getComponent(componentCount - 1);
                Rectangle bounds = lastComp.getBounds();
                scrollRectToVisible(bounds);
            }
        });
    }
    
    /**
     * Public API option to invoke structural purge operations. 
     * Completely removes all active ticket instances from the graphic hierarchy tree, 
     * requests an immediate interface layout update, and resets scroll bar track bounds.
     */
    public void clearAllTickets() {
        removeAllTickets();
    }

    /**
     * Purges all active ticket instances and resets the scroll position to the top.
     */
    public void removeAllTickets() {
        removeAll();
        revalidate();
        repaint();
        scrollRectToVisible(new Rectangle(0, 0, 1, 1));   
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setPreferredSize(new java.awt.Dimension(700, 600));
        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
