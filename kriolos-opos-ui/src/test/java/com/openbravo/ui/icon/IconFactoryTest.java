package com.openbravo.ui.icon;

import java.awt.Color;
import javax.swing.Icon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link IconFactory}, {@link IconKey}, {@link SvgIcon}, and {@link IconPalette}.
 *
 * @author KriolOS
 * @since 1.0.0
 */
class IconFactoryTest {

    @Test
    @DisplayName("getIcon resolves vector SVG with requested dimensions")
    void testGetIconByIconKey() {
        Icon icon = IconFactory.getIcon(IconKey.CUSTOMER, 32);
        assertNotNull(icon, "Icon should not be null");
        assertEquals(32, icon.getIconWidth());
        assertEquals(32, icon.getIconHeight());
    }

    @Test
    @DisplayName("getIcon resolves by literal glyph name")
    void testGetIconByLiteralName() {
        Icon icon = IconFactory.getIcon("user", 24);
        assertNotNull(icon, "Literal 'user' icon should resolve");
        assertEquals(24, icon.getIconWidth());
        assertEquals(24, icon.getIconHeight());
    }

    @Test
    @DisplayName("getIcon supports explicit color tinting")
    void testGetIconWithColor() {
        Icon icon = IconFactory.getIcon(IconKey.DELETE, 24, IconPalette.DANGER);
        assertNotNull(icon);
        assertEquals(24, icon.getIconWidth());
        assertEquals(24, icon.getIconHeight());
    }

    @Test
    @DisplayName("getIconByLegacyPath transparently maps legacy PNG paths to modern icons")
    void testGetIconByLegacyPath() {
        Icon icon = IconFactory.getIconByLegacyPath("/com/openbravo/images/customer_sml.png", 24);
        assertNotNull(icon);
        assertEquals(24, icon.getIconWidth());
        assertEquals(24, icon.getIconHeight());
    }

    @Test
    @DisplayName("getIcon handles null or unknown keys safely with empty icon fallback")
    void testGetIconUnknownFallback() {
        Icon icon = IconFactory.getIcon((IconKey) null, 24);
        assertNotNull(icon);
        assertEquals(24, icon.getIconWidth());
        assertEquals(24, icon.getIconHeight());

        Icon unknown = IconFactory.getIcon("non_existent_glyph_xyz", 16);
        assertNotNull(unknown);
        assertEquals(16, unknown.getIconWidth());
    }

    @Test
    @DisplayName("PosIcon delegates smoothly to IconFactory")
    void testPosIconDelegation() {
        Icon icon = PosIcon.PAY.asIcon(28, IconPalette.SUCCESS);
        assertNotNull(icon);
        assertEquals(28, icon.getIconWidth());
        assertEquals(28, icon.getIconHeight());
    }
}
