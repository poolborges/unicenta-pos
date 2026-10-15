package com.openbravo.ui.laf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * A FlowLayout extension that wraps components to the next line 
 * and properly calculates preferred vertical height, essential for JScrollPanes.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class WrapLayout extends FlowLayout {

    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            
            // If the container has not been sized yet, check if it's in a JViewport
            if (targetWidth == 0) {
                Container viewport = SwingUtilities.getAncestorOfClass(JViewport.class, target);
                if (viewport != null) {
                    targetWidth = viewport.getSize().width;
                }
            }
            
            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE; // Fallback
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            int dimWidth = 0;
            int dimHeight = 0;
            int rowWidth = 0;
            int rowHeight = 0;

            for (int i = 0; i < target.getComponentCount(); i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    
                    // If adding this component exceeds the max width, wrap to a new row
                    if (rowWidth + d.width > maxWidth) {
                        dimWidth = Math.max(dimWidth, rowWidth);
                        dimHeight += rowHeight + vgap;
                        rowWidth = 0;
                        rowHeight = 0;
                    }
                    rowWidth += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }
            
            dimWidth = Math.max(dimWidth, rowWidth);
            dimHeight += rowHeight;

            // Add container insets and outer gaps
            dimWidth += insets.left + insets.right + hgap * 2;
            dimHeight += insets.top + insets.bottom + vgap * 2;

            // Constrain width to the viewport width to prevent horizontal scrollbars
            Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
            if (scrollPane != null && target.getParent() instanceof JViewport) {
                dimWidth = ((JViewport) target.getParent()).getSize().width;
            }

            return new Dimension(dimWidth, dimHeight);
        }
    }
}