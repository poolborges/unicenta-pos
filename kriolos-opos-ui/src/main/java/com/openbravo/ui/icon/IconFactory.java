package com.openbravo.ui.icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Agnostic icon resolver and factory for KriolOS POS.
 * Resolves named icon specifications to scalable vector SVGs with seamless fallback to legacy PNGs.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class IconFactory {

    private static final Logger LOGGER = Logger.getLogger(IconFactory.class.getName());

    public static final int DEFAULT_SIZE = 24;
    private static final String SVG_BASE_PATH = "com/openbravo/images/svg/";
    private static final String PNG_BASE_PATH = "com/openbravo/images/";

    private static final Map<String, Icon> RESOLVED_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, IconKey> LEGACY_PATH_MAP = new HashMap<>();

    static {
        // Map common legacy PNG filenames to modern IconKeys for transparent NetBeans .form migration
        for (IconKey key : IconKey.values()) {
            if (key.getLegacyFileName() != null) {
                LEGACY_PATH_MAP.put(key.getLegacyFileName(), key);
                LEGACY_PATH_MAP.put(PNG_BASE_PATH + key.getLegacyFileName(), key);
                LEGACY_PATH_MAP.put("/" + PNG_BASE_PATH + key.getLegacyFileName(), key);
            }
        }
        // Additional legacy aliases
        LEGACY_PATH_MAP.put("customer.png", IconKey.CUSTOMER);
        LEGACY_PATH_MAP.put("/com/openbravo/images/customer.png", IconKey.CUSTOMER);
        LEGACY_PATH_MAP.put("user_sml.png", IconKey.CUSTOMER);
        LEGACY_PATH_MAP.put("/com/openbravo/images/user_sml.png", IconKey.CUSTOMER);
        LEGACY_PATH_MAP.put("editdelete.png", IconKey.DELETE);
        LEGACY_PATH_MAP.put("/com/openbravo/images/editdelete.png", IconKey.DELETE);
        LEGACY_PATH_MAP.put("sale_editline.png", IconKey.EDIT);
        LEGACY_PATH_MAP.put("/com/openbravo/images/sale_editline.png", IconKey.EDIT);
        LEGACY_PATH_MAP.put("menu-left.png", IconKey.NAV_BACK);
        LEGACY_PATH_MAP.put("/com/openbravo/images/menu-left.png", IconKey.NAV_BACK);
        LEGACY_PATH_MAP.put("menu-right.png", IconKey.NAV_FORWARD);
        LEGACY_PATH_MAP.put("/com/openbravo/images/menu-right.png", IconKey.NAV_FORWARD);
        LEGACY_PATH_MAP.put("fileclose.png", IconKey.CANCEL);
        LEGACY_PATH_MAP.put("/com/openbravo/images/fileclose.png", IconKey.CANCEL);
    }

    private IconFactory() {
        // Utility class: prevent instantiation
    }

    /**
     * Resolves an icon by semantic {@link IconKey} with default dimensions (24x24) and ambient theming.
     *
     * @param key semantic icon key
     * @return resolved Icon instance
     */
    public static Icon getIcon(IconKey key) {
        return getIcon(key, DEFAULT_SIZE, DEFAULT_SIZE, null);
    }

    /**
     * Resolves an icon by semantic {@link IconKey} with the specified square dimension and ambient theming.
     *
     * @param key semantic icon key
     * @param size target width and height in pixels
     * @return resolved Icon instance
     */
    public static Icon getIcon(IconKey key, int size) {
        return getIcon(key, size, size, null);
    }

    /**
     * Resolves an icon by semantic {@link IconKey} with the specified square dimension and explicit color.
     *
     * @param key semantic icon key
     * @param size target width and height in pixels
     * @param color target icon tint color (null for ambient theme color)
     * @return resolved Icon instance
     */
    public static Icon getIcon(IconKey key, int size, Color color) {
        return getIcon(key, size, size, color);
    }

    /**
     * Resolves an icon by semantic {@link IconKey} with specific width, height, and color.
     *
     * @param key semantic icon key
     * @param width target width in pixels
     * @param height target height in pixels
     * @param color target icon tint color (null for ambient theme color)
     * @return resolved Icon instance
     */
    public static Icon getIcon(IconKey key, int width, int height, Color color) {
        if (key == null) {
            return createEmptyIcon(width, height);
        }

        String cacheKey = "KEY:" + key.name() + "@" + width + "x" + height + (color != null ? "#" + Integer.toHexString(color.getRGB()) : "");
        return RESOLVED_CACHE.computeIfAbsent(cacheKey, k -> resolveIcon(key.getLiteralName(), key.getLegacyFileName(), width, height, color));
    }

    /**
     * Resolves an icon by literal visual glyph name (e.g. "user", "credit-card", "arrow-left").
     *
     * @param literalName literal glyph name
     * @param size target width and height in pixels
     * @return resolved Icon instance
     */
    public static Icon getIcon(String literalName, int size) {
        return getIcon(literalName, size, size, null);
    }

    /**
     * Resolves an icon by literal visual glyph name with specific dimensions and color.
     *
     * @param literalName literal glyph name
     * @param width target width in pixels
     * @param height target height in pixels
     * @param color target icon tint color
     * @return resolved Icon instance
     */
    public static Icon getIcon(String literalName, int width, int height, Color color) {
        if (literalName == null || literalName.isBlank()) {
            return createEmptyIcon(width, height);
        }

        String cacheKey = "NAME:" + literalName + "@" + width + "x" + height + (color != null ? "#" + Integer.toHexString(color.getRGB()) : "");
        return RESOLVED_CACHE.computeIfAbsent(cacheKey, k -> resolveIcon(literalName, null, width, height, color));
    }

    /**
     * Translates a legacy PNG path to its modern vector SVG equivalent or PNG fallback.
     * Used for progressive migration of NetBeans .form files.
     *
     * @param legacyPath legacy resource path (e.g., "/com/openbravo/images/customer_sml.png")
     * @param size target size in pixels
     * @return resolved modern Icon instance
     */
    public static Icon getIconByLegacyPath(String legacyPath, int size) {
        return getIconByLegacyPath(legacyPath, size, null);
    }

    /**
     * Translates a legacy PNG path to its modern vector SVG equivalent or PNG fallback with explicit color tint.
     *
     * @param legacyPath legacy resource path
     * @param size target size in pixels
     * @param color target icon tint color
     * @return resolved modern Icon instance
     */
    public static Icon getIconByLegacyPath(String legacyPath, int size, Color color) {
        if (legacyPath == null || legacyPath.isBlank()) {
            return createEmptyIcon(size, size);
        }

        IconKey mappedKey = LEGACY_PATH_MAP.get(legacyPath);
        if (mappedKey != null) {
            return getIcon(mappedKey, size, color);
        }

        // Try extracting filename
        String fileName = legacyPath.substring(legacyPath.lastIndexOf('/') + 1);
        mappedKey = LEGACY_PATH_MAP.get(fileName);
        if (mappedKey != null) {
            return getIcon(mappedKey, size, color);
        }

        // Direct fallback to legacy PNG
        return loadPngIcon(legacyPath, size, size);
    }

    private static Icon resolveIcon(String literalName, String legacyFileName, int width, int height, Color color) {
        // 1. Try vector SVG first
        String svgResourcePath = SVG_BASE_PATH + literalName + ".svg";
        URL svgUrl = IconFactory.class.getClassLoader().getResource(svgResourcePath);
        if (svgUrl != null) {
            try {
                return new SvgIcon(svgResourcePath, width, height, color);
            } catch (Exception ex) {
                LOGGER.log(Level.FINE, "Failed to load vector SVG: " + svgResourcePath, ex);
            }
        }

        // 2. Fall back to legacy PNG if available
        if (legacyFileName != null) {
            String pngPath = PNG_BASE_PATH + legacyFileName;
            Icon pngIcon = loadPngIcon(pngPath, width, height);
            if (pngIcon != null) {
                return pngIcon;
            }
        }

        // 3. Fallback to empty placeholder icon
        return createEmptyIcon(width, height);
    }

    private static Icon loadPngIcon(String resourcePath, int width, int height) {
        String normalizedPath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        URL url = IconFactory.class.getClassLoader().getResource(normalizedPath);
        if (url == null) {
            return null;
        }

        ImageIcon rawIcon = new ImageIcon(url);
        if (rawIcon.getIconWidth() <= 0) {
            return null;
        }

        if (rawIcon.getIconWidth() == width && rawIcon.getIconHeight() == height) {
            return rawIcon;
        }

        Image scaledImage = rawIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private static Icon createEmptyIcon(int width, int height) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                // Empty icon: no-op
            }

            @Override
            public int getIconWidth() {
                return width;
            }

            @Override
            public int getIconHeight() {
                return height;
            }
        };
    }
}
