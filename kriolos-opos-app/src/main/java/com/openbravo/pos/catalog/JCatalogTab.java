package com.openbravo.pos.catalog;

import com.openbravo.pos.catalog.ProductCardV2.CardConfig;
import com.openbravo.pos.catalog.ProductCardV2.ViewMode;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

/**
 * Modernized catalog tab panel displaying responsive product card grids.
 * Fully configurable and Look-and-Feel agnostic (supports FlatLaf and Native).
 *
 * @author adrianromero
 * @author poolborges
 * @author KriolOS
 */
public class JCatalogTab extends JPanel {

    private static final long serialVersionUID = 1L;

    private final WrapPanel gridContainer;
    private final JScrollPane scrollPane;
    private ViewMode viewMode;
    private CardConfig cardConfig;

    /**
     * Default constructor using standard DETAILED mode.
     */
    public JCatalogTab() {
        this(ViewMode.DETAILED, CardConfig.defaultDetailed());
    }

    /**
     * Constructor allowing specific view mode configuration.
     *
     * @param viewMode Layout style (COMPACT or DETAILED)
     */
    public JCatalogTab(ViewMode viewMode) {
        this(viewMode, viewMode == ViewMode.COMPACT 
                ? CardConfig.defaultCompact() 
                : CardConfig.defaultDetailed());
    }

    /**
     * Fully parameterized constructor allowing external dimensions, sizing, and styling.
     *
     * @param viewMode Layout style (COMPACT or DETAILED)
     * @param cardConfig Sizing, padding, and font configurations
     */
    public JCatalogTab(ViewMode viewMode, CardConfig cardConfig) {
        this.viewMode = viewMode != null ? viewMode : ViewMode.DETAILED;
        this.cardConfig = cardConfig != null ? cardConfig : CardConfig.defaultDetailed();

        setLayout(new BorderLayout());
        setOpaque(false);

        // 1. Grid container using fluid wrap layout
        gridContainer = new WrapPanel(FlowLayout.LEADING, 10, 10);
        gridContainer.setOpaque(false);
        gridContainer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // 2. High-performance, touch-friendly scroll pane
        scrollPane = new JScrollPane(gridContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Touch-screen optimized scrollbar width and wheel speed
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(30, 30));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void setEnabled(boolean value) {
        super.setEnabled(value);
        gridContainer.setEnabled(value);
        synchronized (gridContainer.getTreeLock()) {
            for (Component c : gridContainer.getComponents()) {
                c.setEnabled(value);
            }
        }
    }

    /**
     * Adds an action button represented as a ProductCardV2.
     */
    public void addButton(Icon icon, ActionListener actionListener, String text, String textTip) {
        CatalogItem item = new CatalogItem(text);
        item.setTextTip(textTip);
        if (icon instanceof ImageIcon imageIcon) {
            item.setImage(imageIcon.getImage());
        }

        ProductCardV2 card = new ProductCardV2(item, viewMode, cardConfig, actionListener);
        gridContainer.add(card);
        refreshLayout();
    }

    /**
     * Adds a catalog product item card to the grid.
     */
    public void addCatalogItem(CatalogItem catalogItem, ActionListener actionListener) {
        ProductCardV2 card = new ProductCardV2(catalogItem, viewMode, cardConfig, actionListener);
        gridContainer.add(card);
        refreshLayout();
    }

    /**
     * Removes all items and resets viewport scroll back to the top.
     */
    public void clearItems() {
        gridContainer.removeAll();
        refreshLayout();
        SwingUtilities.invokeLater(() -> {
            if (scrollPane != null && scrollPane.getViewport() != null) {
                scrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));
            }
        });
    }

    private void refreshLayout() {
        gridContainer.revalidate();
        gridContainer.repaint();
    }

    // --- Configuration Getters and Setters ---

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public CardConfig getCardConfig() {
        return cardConfig;
    }

    public void setCardConfig(CardConfig cardConfig) {
        this.cardConfig = cardConfig;
    }

    /**
     * Responsive panel implementing Scrollable to dynamically wrap items without horizontal scrolling.
     */
    private static class WrapPanel extends JPanel implements Scrollable {

        private static final long serialVersionUID = 1L;
        private final int hGap;
        private final int vGap;

        public WrapPanel(int align, int hGap, int vGap) {
            super(new FlowLayout(align, hGap, vGap));
            this.hGap = hGap;
            this.vGap = vGap;
        }

        @Override
        public Dimension getPreferredSize() {
            return calculateWrappedSize(this);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 60;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            // Forces container to respect viewport width and wrap components downward
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }

        private Dimension calculateWrappedSize(JPanel target) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                if (targetWidth == 0) {
                    JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, target);
                    if (viewport != null) {
                        targetWidth = viewport.getWidth();
                    }
                }
                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }

                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hGap * 2);

                int rowWidth = 0;
                int rowHeight = 0;
                int totalHeight = 0;
                int maxRowWidth = 0;

                for (Component comp : target.getComponents()) {
                    if (comp.isVisible()) {
                        Dimension d = comp.getPreferredSize();
                        if (rowWidth + d.width > maxWidth && rowWidth > 0) {
                            maxRowWidth = Math.max(maxRowWidth, rowWidth);
                            totalHeight += rowHeight + vGap;
                            rowWidth = 0;
                            rowHeight = 0;
                        }
                        rowWidth += d.width + hGap;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }

                maxRowWidth = Math.max(maxRowWidth, rowWidth);
                totalHeight += rowHeight;

                return new Dimension(
                        maxRowWidth + insets.left + insets.right + hGap * 2,
                        totalHeight + insets.top + insets.bottom + vGap * 2
                );
            }
        }
    }
}