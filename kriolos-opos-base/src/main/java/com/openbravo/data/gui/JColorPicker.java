/*
 * Copyright (C) 2023 Paulo Borges
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openbravo.data.gui;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JColorChooser;

/**
 *
 * @author poolb
 */
public class JColorPicker extends JButton {

    private static final long serialVersionUID = 1L;

    private Color color = null;

    //This is important to allow use nas Bean on IDE
    public JColorPicker() {
        this(null);
    }

    public JColorPicker(Color color) {
        this.color = color;
        this.setBackground(this.color);
        initComponent();
    }

    private void initComponent() {
        this.addActionListener((java.awt.event.ActionEvent evt) -> {
            jBtnColourActionPerformed(evt);
        });
    }

    private void jBtnColourActionPerformed(java.awt.event.ActionEvent evt) {
        this.color = JColorChooser.showDialog(this, "", this.color);
        if (color != null) {
            this.setBackground(color);
            this.setText(getHexColor());
        }
    }

    public Color getColor() {
        return this.color;
    }
    
    public String getHexColor() {
        String colorName = null;
        if(this.color != null){
            colorName = "#" + Integer.toHexString(color.getRGB() & 0x00ffffff);
        }
        return colorName;
    }
}
