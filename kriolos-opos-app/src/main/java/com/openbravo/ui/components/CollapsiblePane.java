/*
 * Copyright (C) 2026 KriolOS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.openbravo.ui.components; // Ajuste para o seu pacote de componentes

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Modern replacement for JXTaskPane with compatible API.
 * 
 * @author poolborges / KriolOS
 */
public class CollapsiblePane extends JPanel {

    private final JPanel contentPanel;
    private final JButton headerButton;
    private boolean expanded = true;

    public CollapsiblePane() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Header estilo acordeão moderno compatível com FlatLaf
        headerButton = new JButton();
        headerButton.setHorizontalAlignment(SwingConstants.LEFT);
        headerButton.putClientProperty("JButton.buttonType", "toolBarButton");
        headerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerButton.setFocusPainted(false);
        headerButton.addActionListener(e -> toggleCollapse());

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        add(headerButton, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void setTitle(String title) {
        headerButton.setText(title);
    }

    public String getTitle() {
        headerButton.getText();
        return headerButton.getText();
    }

    private void toggleCollapse() {
        expanded = !expanded;
        contentPanel.setVisible(expanded);
        revalidate();
        repaint();
    }

    // --- API Compatível com JXTaskPane ---

    public Component add(Action action) {
        JButton btn = new JButton(action);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.putClientProperty("JButton.buttonType", "toolBarButton");
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contentPanel.add(btn);
        revalidate();
        repaint();
        return btn;
    }

    @Override
    public Component add(Component comp) {
        contentPanel.add(comp);
        revalidate();
        repaint();
        return comp;
    }
}