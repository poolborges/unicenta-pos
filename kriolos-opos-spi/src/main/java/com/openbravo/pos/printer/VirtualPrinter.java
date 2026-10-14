/*
 * Copyright (C) 2026 KriolOS
 */
package com.openbravo.pos.printer;

import com.openbravo.pos.printer.DevicePrinter;

/**
 * Marker and accessor interface for virtual/screen printers that can render 
 * tickets into a UI presentation layer.
 */
public interface VirtualPrinter extends DevicePrinter {

    /**
     * Resolves the visual component for the requested UI framework.
     * 
     * @param targetType Expected component class (e.g. JComponent.class or Component.class)
     * @return The UI component, or null if this virtual printer cannot render into targetType
     */
    <T> T getComponent(Class<T> targetType);
}