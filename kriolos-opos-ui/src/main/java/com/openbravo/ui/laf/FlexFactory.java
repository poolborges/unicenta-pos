package com.openbravo.ui.laf;

import javax.swing.*;

/**
 * Convenient factory for constructing common Flexbox container panels.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class FlexFactory {

    private FlexFactory() {
        // Utility class: prevent instantiation
    }

    /**
     * Creates a horizontal flex row container without wrapping.
     *
     * @param justify justification along the main axis
     * @param align alignment along the cross axis
     * @param gap spacing between items in pixels
     * @return configured JPanel
     */
    public static JPanel createRow(FlexLayout.JustifyContent justify, FlexLayout.AlignItems align, int gap) {
        JPanel panel = new JPanel();
        panel.setLayout(FlexLayout.builder()
                .direction(FlexLayout.Direction.ROW)
                .wrap(FlexLayout.Wrap.NO_WRAP)
                .justifyContent(justify)
                .alignItems(align)
                .gap(gap)
                .build());
        return panel;
    }

    /**
     * Creates a horizontal flex row container with wrapping enabled.
     *
     * @param justify justification along the main axis
     * @param align alignment along the cross axis
     * @param gap spacing between items in pixels
     * @return configured JPanel
     */
    public static JPanel createRowWrap(FlexLayout.JustifyContent justify, FlexLayout.AlignItems align, int gap) {
        JPanel panel = new JPanel();
        panel.setLayout(FlexLayout.builder()
                .direction(FlexLayout.Direction.ROW)
                .wrap(FlexLayout.Wrap.WRAP)
                .justifyContent(justify)
                .alignItems(align)
                .gap(gap)
                .build());
        return panel;
    }

    /**
     * Creates a vertical flex column container without wrapping.
     *
     * @param justify justification along the main axis
     * @param align alignment along the cross axis
     * @param gap spacing between items in pixels
     * @return configured JPanel
     */
    public static JPanel createColumn(FlexLayout.JustifyContent justify, FlexLayout.AlignItems align, int gap) {
        JPanel panel = new JPanel();
        panel.setLayout(FlexLayout.builder()
                .direction(FlexLayout.Direction.COLUMN)
                .wrap(FlexLayout.Wrap.NO_WRAP)
                .justifyContent(justify)
                .alignItems(align)
                .gap(gap)
                .build());
        return panel;
    }
}
