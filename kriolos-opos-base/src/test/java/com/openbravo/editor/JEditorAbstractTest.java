package com.openbravo.editor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class JEditorAbstractTest {

    @Test
    @DisplayName("JEditorString preferred height scales dynamically with font size")
    void testDynamicHeightScaling() {
        JEditorString editor = new JEditorString();

        // 1. Default / small font test
        editor.setFont(new Font("SansSerif", Font.PLAIN, 12));
        Dimension pref12 = editor.getPreferredSize();
        assertTrue(pref12.height >= 32, "Preferred height should be at least minimum floor (32px), got: " + pref12.height);

        // 2. Touch 16pt font test
        editor.setFont(new Font("SansSerif", Font.PLAIN, 16));
        Dimension pref16 = editor.getPreferredSize();
        int fontHeight16 = editor.getFontMetrics(editor.getFont()).getHeight();
        assertTrue(pref16.height >= fontHeight16 + 8,
                "Preferred height (" + pref16.height + ") must scale to accommodate 16pt font height (" + fontHeight16 + ")");

        // 3. Large 24pt font test
        editor.setFont(new Font("SansSerif", Font.PLAIN, 24));
        Dimension pref24 = editor.getPreferredSize();
        int fontHeight24 = editor.getFontMetrics(editor.getFont()).getHeight();
        assertTrue(pref24.height >= fontHeight24 + 8,
                "Preferred height (" + pref24.height + ") must accommodate 24pt font height (" + fontHeight24 + ")");
        assertTrue(pref24.height > pref16.height,
                "Preferred height for 24pt (" + pref24.height + ") must be larger than 16pt (" + pref16.height + ")");
    }

    @Test
    @DisplayName("JEditorString minimum size matches preferred size to prevent collapse")
    void testMinimumSizeMatchesPreferred() {
        JEditorString editor = new JEditorString();
        editor.setFont(new Font("SansSerif", Font.PLAIN, 16));

        assertEquals(editor.getPreferredSize(), editor.getMinimumSize());
    }
}
