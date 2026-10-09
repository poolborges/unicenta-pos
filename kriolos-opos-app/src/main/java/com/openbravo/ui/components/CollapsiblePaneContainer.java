package com.openbravo.ui.components;


import javax.swing.*;
import java.awt.*;

/**
 * Modern replacement for JXTaskPaneContainer with compatible API.
 * 
 * @author KriolOS
 */
public class CollapsiblePaneContainer extends JPanel {

    public CollapsiblePaneContainer() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    // --- API Compatível com JXTaskPaneContainer ---

    @Override
    public Component add(Component comp) {
        super.add(comp);
        // Adiciona um pequeno espaçamento vertical entre os painéis do menu
        super.add(Box.createVerticalStrut(5));
        revalidate();
        repaint();
        return comp;
    }
}