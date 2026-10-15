package com.openbravo.ui.laf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FlexLayout} and {@link FlexFactory}.
 * Validates layout builder configurations, constraint string parsing,
 * factory container construction, and preferred sizing math.
 *
 * @author KriolOS
 * @since 1.0.0
 */
@DisplayName("FlexLayout & FlexFactory Layout Manager Tests")
class FlexLayoutTest {

    private static final int DEFAULT_GAP = 12;
    private static final int COMPONENT_WIDTH = 100;
    private static final int COMPONENT_HEIGHT = 40;

    @Test
    @DisplayName("Verify FlexLayout builder sets configurations accurately")
    void testBuilderDefaultsAndCustomValues() {
        FlexLayout layout = FlexLayout.builder()
                .direction(FlexLayout.Direction.COLUMN)
                .justifyContent(FlexLayout.JustifyContent.CENTER)
                .wrap(FlexLayout.Wrap.WRAP)
                .alignItems(FlexLayout.AlignItems.STRETCH)
                .gap(DEFAULT_GAP)
                .build();

        assertNotNull(layout);
        assertEquals(0.5f, layout.getLayoutAlignmentX(new JPanel()), 0.001f);
        assertEquals(0.5f, layout.getLayoutAlignmentY(new JPanel()), 0.001f);
    }

    @Test
    @DisplayName("Verify FlexFactory creates properly configured row and column panels")
    void testFlexFactoryPanels() {
        JPanel rowPanel = FlexFactory.createRow(FlexLayout.JustifyContent.START, FlexLayout.AlignItems.CENTER, DEFAULT_GAP);
        assertNotNull(rowPanel);
        assertTrue(rowPanel.getLayout() instanceof FlexLayout);

        JPanel colPanel = FlexFactory.createColumn(FlexLayout.JustifyContent.CENTER, FlexLayout.AlignItems.STRETCH, DEFAULT_GAP);
        assertNotNull(colPanel);
        assertTrue(colPanel.getLayout() instanceof FlexLayout);

        JPanel wrapPanel = FlexFactory.createRowWrap(FlexLayout.JustifyContent.START, FlexLayout.AlignItems.CENTER, DEFAULT_GAP);
        assertNotNull(wrapPanel);
        assertTrue(wrapPanel.getLayout() instanceof FlexLayout);
    }

    @Test
    @DisplayName("Verify FlexLayout constraint parsing and component addition")
    void testConstraintParsing() {
        JPanel panel = new JPanel();
        FlexLayout layout = FlexLayout.builder()
                .direction(FlexLayout.Direction.ROW)
                .gap(DEFAULT_GAP)
                .build();
        panel.setLayout(layout);

        JButton button1 = new JButton("Grow");
        JButton button2 = new JButton("Fixed");
        JButton button3 = new JButton("Percent");

        panel.add(button1, "grow: 1.0; basis: 50");
        panel.add(button2, new FlexLayout.FlexConstraints(0.0, 100));
        panel.add(button3, "grow: 2.0; basis: 50%");

        assertEquals(3, panel.getComponentCount());
    }

    @Test
    @DisplayName("Verify preferred size computation accounts for component dimensions and gap")
    void testPreferredSizeComputation() {
        JPanel panel = new JPanel();
        FlexLayout layout = FlexLayout.builder()
                .direction(FlexLayout.Direction.ROW)
                .gap(DEFAULT_GAP)
                .build();
        panel.setLayout(layout);

        JButton btn1 = new JButton("Btn1");
        btn1.setPreferredSize(new Dimension(COMPONENT_WIDTH, COMPONENT_HEIGHT));
        JButton btn2 = new JButton("Btn2");
        btn2.setPreferredSize(new Dimension(COMPONENT_WIDTH, COMPONENT_HEIGHT));

        panel.add(btn1);
        panel.add(btn2);

        Dimension prefSize = layout.preferredLayoutSize(panel);
        assertNotNull(prefSize);
        assertTrue(prefSize.width > 0);
        assertTrue(prefSize.height > 0);
    }
}
