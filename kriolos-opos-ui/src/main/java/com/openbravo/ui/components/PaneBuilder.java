package com.openbravo.ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Canonical form and tabbed panel builder for the KriolOS POS ecosystem.
 * <p>
 * Unifies GridBagLayout form composition, standardized touch dimensions,
 * proportional multi-column inputs, required indicators, automatic tooltips,
 * and multi-button action footers into a single fluent API.
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class PaneBuilder {

    private final GridBagConstraints c;
    private int currentRow = 0;

    // Configurable POS Touch Dimension Standards
    private int globalMinWidth = 150;
    private int globalMinHeight = 80;
    private int globalLabelWidth = 140;
    private int globalFieldHeight = 35;
    private boolean useLabelAsTooltip = false;

    // Multi-Tab Structure
    private JTabbedPane tabbedPane = null;
    private JPanel currentPanel;
    private final Map<String, JPanel> tabs = new LinkedHashMap<>();
    private final Map<String, Icon> tabIcons = new LinkedHashMap<>();
    private final Map<JPanel, Integer> tabRowCounts = new LinkedHashMap<>();

    // Global Footer
    private Component globalFooterComponent = null;

    // Responsive Scrolling
    private boolean scrollable = false;

    private static class ScrollablePanel extends JPanel implements Scrollable {
        public ScrollablePanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 48;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    public PaneBuilder() {
        this.currentPanel = createNewGridBagPanel();
        this.c = new GridBagConstraints();
        this.c.insets = new Insets(4, 8, 4, 8); // Standard comfortable POS margin
    }

    private JPanel createNewGridBagPanel() {
        JPanel p = new ScrollablePanel(new GridBagLayout());
        p.setOpaque(false);
        return p;
    }

    // =========================================================================
    // FLUENT CONFIGURATION
    // =========================================================================

    public PaneBuilder setMinSize(int width, int height) {
        this.globalMinWidth = width;
        this.globalMinHeight = height;
        return this;
    }

    public PaneBuilder setLabelSize(int width) {
        this.globalLabelWidth = width;
        return this;
    }

    public PaneBuilder setFieldHeight(int height) {
        this.globalFieldHeight = height;
        return this;
    }

    public PaneBuilder useLabelAsTooltip(boolean enable) {
        this.useLabelAsTooltip = enable;
        return this;
    }

    public PaneBuilder setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
        return this;
    }

    public boolean isScrollable() {
        return this.scrollable;
    }

    // =========================================================================
    // TAB MANAGEMENT
    // =========================================================================

    public PaneBuilder addTab(String tabTitle) {
        return addTab(tabTitle, null);
    }

    public PaneBuilder addTab(String tabTitle, Icon icon) {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
        }
        // Save row count of the previous panel before switching
        if (currentPanel != null) {
            tabRowCounts.put(currentPanel, currentRow);
        }
        this.currentPanel = createNewGridBagPanel();
        this.tabs.put(tabTitle, this.currentPanel);
        if (icon != null) {
            this.tabIcons.put(tabTitle, icon);
        }
        this.currentRow = 0;
        return this;
    }

    // =========================================================================
    // STANDARD SINGLE-FIELD ROWS
    // =========================================================================

    public PaneBuilder add(String labelText, Component component) {
        return add(labelText, false, component);
    }

    public PaneBuilder add(String labelText, boolean required, Component component) {
        configureComponentSizes(component, globalMinWidth, globalFieldHeight);
        JLabel label = createStandardLabel(labelText, required, component);

        c.gridx = 0;
        c.gridy = currentRow;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.BASELINE_TRAILING;
        c.gridwidth = 1;
        currentPanel.add(label, c);

        c.gridx = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.BASELINE_LEADING;
        c.gridwidth = GridBagConstraints.REMAINDER;
        currentPanel.add(component, c);

        currentRow++;
        return this;
    }

    // =========================================================================
    // MULTI-COLUMN ROWS (WITH LABEL)
    // =========================================================================

    public PaneBuilder add(String labelText, Component... components) {
        return add(labelText, false, null, components);
    }

    public PaneBuilder add(String labelText, double[] weights, Component... components) {
        return add(labelText, false, weights, components);
    }

    public PaneBuilder add(String labelText, boolean required, double[] weights, Component... components) {
        if (components == null || components.length == 0) {
            return this;
        }
        JLabel label = createStandardLabelForMulti(labelText, required, components);

        c.gridx = 0;
        c.gridy = currentRow;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.BASELINE_TRAILING;
        c.gridwidth = 1;
        currentPanel.add(label, c);

        double weightEach = 1.0 / components.length;
        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            double compWeight = (weights != null && i < weights.length) ? weights[i] : weightEach;
            int minWidthForComp = (int) (globalMinWidth * compWeight);
            configureComponentSizes(comp, Math.max(minWidthForComp, 40), globalFieldHeight);

            c.gridx = i + 1;
            c.weightx = compWeight;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.BASELINE_LEADING;
            c.gridwidth = (i == components.length - 1) ? GridBagConstraints.REMAINDER : 1;
            currentPanel.add(comp, c);
        }

        currentRow++;
        return this;
    }

    // =========================================================================
    // MULTI-COLUMN ROWS (WITHOUT LABEL - ALIGNED TO COLUMN 1)
    // =========================================================================

    public PaneBuilder add(Component component) {
        c.gridx = 0;
        c.gridy = currentRow;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = GridBagConstraints.REMAINDER;

        currentPanel.add(component, c);
        currentRow++;
        return this;
    }

    public PaneBuilder add(Component... components) {
        return add((double[]) null, components);
    }

    public PaneBuilder add(double[] weights, Component... components) {
        if (components == null || components.length == 0) {
            return this;
        }

        double weightEach = 1.0 / components.length;
        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            double compWeight = (weights != null && i < weights.length) ? weights[i] : weightEach;
            int minWidthForComp = (int) (globalMinWidth * compWeight);
            configureComponentSizes(comp, Math.max(minWidthForComp, 40), globalFieldHeight);

            // Starts at column 1 to maintain vertical alignment with labeled inputs
            c.gridx = i + 1;
            c.gridy = currentRow;
            c.weightx = compWeight;
            c.weighty = 0.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.BASELINE_LEADING;
            c.gridwidth = (i == components.length - 1) ? GridBagConstraints.REMAINDER : 1;

            currentPanel.add(comp, c);
        }

        currentRow++;
        return this;
    }

    // =========================================================================
    // SCROLLABLE / EXPANDABLE ROWS
    // =========================================================================

    public PaneBuilder addScroll(String labelText, Component component, double weightx, double weighty) {
        return addScroll(labelText, false, component, weightx, weighty);
    }

    public PaneBuilder addScroll(String labelText, boolean required, Component component, double weightx, double weighty) {
        JLabel label = createStandardLabel(labelText, required, component);

        c.gridx = 0;
        c.gridy = currentRow;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.gridwidth = 1;
        currentPanel.add(label, c);

        JScrollPane scroll = (component instanceof JScrollPane) ? (JScrollPane) component : new JScrollPane(component);
        configureComponentSizes(scroll, globalMinWidth, globalMinHeight);

        c.gridx = 1;
        c.weightx = weightx;
        c.weighty = weighty;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridwidth = GridBagConstraints.REMAINDER;
        currentPanel.add(scroll, c);

        currentRow++;
        return this;
    }

    // =========================================================================
    // BACKWARDS COMPATIBILITY ALIASES (v1 PaneBuilder API)
    // =========================================================================

    public PaneBuilder addRow(JLabel label, Component valueComponent) {
        configureComponentSizes(valueComponent, globalMinWidth, globalFieldHeight);
        c.gridx = 0; c.gridy = currentRow; c.weightx = 0.0; c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.BASELINE_TRAILING;
        c.gridwidth = 1;
        currentPanel.add(label, c);

        c.gridx = 1; c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL; c.anchor = GridBagConstraints.BASELINE_LEADING;
        c.gridwidth = GridBagConstraints.REMAINDER;
        currentPanel.add(valueComponent, c);

        currentRow++;
        return this;
    }

    public PaneBuilder addRow(JLabel label, JPanel multiComponentPanel) {
        multiComponentPanel.setOpaque(false);
        return addRow(label, (Component) multiComponentPanel);
    }

    public PaneBuilder addExpandableRow(JLabel label, JComponent scrollableComponent) {
        return addScroll(label.getText(), scrollableComponent, 1.0, 1.0);
    }

    // =========================================================================
    // ACTION FOOTER (SINGLE OR MULTI-BUTTON)
    // =========================================================================

    public PaneBuilder addFooter(Component footerComponent) {
        if (footerComponent instanceof JComponent) {
            ((JComponent) footerComponent).setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));
        }
        configureComponentSizes(footerComponent, globalMinWidth, globalFieldHeight + 10);
        this.globalFooterComponent = footerComponent;
        return this;
    }

    public PaneBuilder addFooter(Component... components) {
        return addFooter(null, components);
    }

    public PaneBuilder addFooter(double[] weights, Component... components) {
        if (components == null || components.length == 0) {
            return this;
        }

        JPanel footerPanel = new JPanel(new GridBagLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));

        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(3, 4, 3, 4);
        fgbc.gridy = 0;
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.anchor = GridBagConstraints.CENTER;

        double weightEach = 1.0 / components.length;
        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            double compWeight = (weights != null && i < weights.length) ? weights[i] : weightEach;

            int minWidthForComp = (int) (globalMinWidth * compWeight);
            configureComponentSizes(comp, Math.max(minWidthForComp, 50), globalFieldHeight + 10);

            fgbc.gridx = i;
            fgbc.weightx = compWeight;
            fgbc.gridwidth = (i == components.length - 1) ? GridBagConstraints.REMAINDER : 1;
            footerPanel.add(comp, fgbc);
        }

        this.globalFooterComponent = footerPanel;
        return this;
    }

    // =========================================================================
    // INTERNAL HELPERS
    // =========================================================================

    private JLabel createStandardLabel(String text, boolean required, Component targetComponent) {
        String plainText = getCleanLabelText(text);
        JLabel label = buildBaseLabel(plainText, required);

        if (useLabelAsTooltip) {
            String tooltipText = plainText + (required ? " (Obrigatório)" : "");
            if (targetComponent instanceof JComponent) {
                ((JComponent) targetComponent).setToolTipText(tooltipText);
            }
            label.setToolTipText(tooltipText);
        }
        return label;
    }

    private JLabel createStandardLabelForMulti(String text, boolean required, Component[] targetComponents) {
        String plainText = getCleanLabelText(text);
        JLabel label = buildBaseLabel(plainText, required);

        if (useLabelAsTooltip) {
            String tooltipText = plainText + (required ? " (Obrigatório)" : "");
            if (targetComponents != null && targetComponents.length > 0 && targetComponents[0] instanceof JComponent) {
                ((JComponent) targetComponents[0]).setToolTipText(tooltipText);
            }
            label.setToolTipText(tooltipText);
        }
        return label;
    }

    private String getCleanLabelText(String text) {
        String plainText = text.trim();
        if (plainText.endsWith(":")) {
            plainText = plainText.substring(0, plainText.length() - 1).trim();
        }
        return plainText;
    }

    private JLabel buildBaseLabel(String plainText, boolean required) {
        JLabel label = new JLabel();
        if (required) {
            label.setText("<html>" + plainText + " <font color='red'>*</font>:</html>");
        } else {
            label.setText(plainText + ":");
        }
        Font font = label.getFont();
        int fontHeight = globalFieldHeight;
        if (font != null) {
            FontMetrics fm = label.getFontMetrics(font);
            if (fm != null) {
                fontHeight = Math.max(fontHeight, fm.getHeight() + 8);
            }
        }
        Dimension minSize = new Dimension(globalLabelWidth, globalFieldHeight);
        Dimension prefSize = new Dimension(globalLabelWidth, fontHeight);
        label.setMinimumSize(minSize);
        label.setPreferredSize(prefSize);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private void configureComponentSizes(Component comp, int minWidth, int minHeight) {
        Dimension pref = comp.getPreferredSize();
        int fontMetricHeight = ComponentFactory.calculateFontMetricHeight(comp);
        int effectiveMinHeight = Math.max(minHeight, globalFieldHeight);
        int targetHeight = Math.max(effectiveMinHeight, Math.max(pref != null ? pref.height : 0, fontMetricHeight));
        int targetWidth = (pref != null) ? Math.max(pref.width, minWidth) : minWidth;

        comp.setMinimumSize(new Dimension(minWidth, effectiveMinHeight));
        comp.setPreferredSize(new Dimension(targetWidth, targetHeight));
    }

    private void applyVerticalSpacer(JPanel targetPanel, int row) {
        GridBagConstraints spacerConstraints = new GridBagConstraints();
        spacerConstraints.gridx = 0;
        spacerConstraints.gridy = row;
        spacerConstraints.weightx = 1.0;
        spacerConstraints.weighty = 1.0;
        spacerConstraints.fill = GridBagConstraints.BOTH;
        spacerConstraints.gridwidth = GridBagConstraints.REMAINDER;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        targetPanel.add(spacer, spacerConstraints);
    }

    private JScrollPane createSmoothScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    // =========================================================================
    // BUILD METHOD
    // =========================================================================

    public JPanel build() {
        if (currentPanel != null) {
            tabRowCounts.put(currentPanel, currentRow);
        }

        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setOpaque(false);

        if (tabbedPane != null) {
            tabbedPane.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

            for (Map.Entry<String, JPanel> entry : tabs.entrySet()) {
                String title = entry.getKey();
                JPanel tabContentPanel = entry.getValue();

                int rowsInTab = tabRowCounts.getOrDefault(tabContentPanel, 999);
                applyVerticalSpacer(tabContentPanel, rowsInTab + 1);
                tabContentPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

                Icon icon = tabIcons.get(title);
                Component compToAdd = scrollable ? createSmoothScrollPane(tabContentPanel) : tabContentPanel;
                tabbedPane.addTab(title, icon, compToAdd);
            }
            mainWrapper.add(tabbedPane, BorderLayout.CENTER);
        } else {
            applyVerticalSpacer(currentPanel, currentRow + 1);
            currentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            if (scrollable) {
                mainWrapper.add(createSmoothScrollPane(currentPanel), BorderLayout.CENTER);
            } else {
                mainWrapper.add(currentPanel, BorderLayout.CENTER);
            }
        }

        if (globalFooterComponent != null) {
            mainWrapper.add(globalFooterComponent, BorderLayout.SOUTH);
        }

        return mainWrapper;
    }
}
