/*
 * Copyright (C) 2022 Paulo Borges
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
package io.github.kriolos.opos.sample.mogwai;

/**
 *
 * @author poolb
 */
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;

import de.mogwai.common.client.binding.PropertyAdapter;
import de.mogwai.common.client.binding.configurator.DefaultConfigurator;
import de.mogwai.common.client.binding.tools.BeanUtils;
import java.util.Map;
import javax.swing.JToggleButton;

/**
 * @author msertic
 */
public class ToggleButtonAdapter extends PropertyAdapter {

    private final ButtonGroup buttonGroup;
    private final HashMap<Object, JToggleButton> mapping;

    /**
     * Constructor.
     */
    public ToggleButtonAdapter() {

        super(null, null);
        configurator = DefaultConfigurator.getInstance();
        mapping = new HashMap<>();
        buttonGroup = new ButtonGroup();
    }

    /**
     * Add a mapping.
     * 
     * @param aValue
     * @param aButton
     */
    public void addMapping(Object aValue, JToggleButton aButton) {

        mapping.put(aValue, aButton);
        buttonGroup.add(aButton);
    }

    @Override
    public void model2view(Object aModel, String aPropertyName) {

        try {
            Object value = BeanUtils.getProperty(aModel, aPropertyName);
            for (Map.Entry<Object, JToggleButton> entry : mapping.entrySet()) {
                Object key = entry.getKey();
                JToggleButton radio = entry.getValue();
                if (value == null) {
                    radio.setSelected(false);
                } else {
                    radio.setSelected(value.equals(key));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot transfer model2view for property " + aPropertyName, e);
        }
    }

    @Override
    public void view2model(Object aModel, String aPropertyName) {

        try {
            Object value = null;
            for (Map.Entry<Object, JToggleButton> entry : mapping.entrySet()) {
                Object key = entry.getKey();
                JToggleButton radio = entry.getValue();
                if (radio.isSelected()) {
                    value = key;
                }
            }
            BeanUtils.setProperty(aModel, aPropertyName, value);
        } catch (Exception e) {
            throw new RuntimeException("Cannot transfer view2model for property " + aPropertyName, e);
        }
    }

    @Override
    public JComponent[] getComponent() {
        return mapping.values().toArray(JComponent[]::new);
    }
}

