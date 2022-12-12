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

import javax.swing.JComponent;

import de.mogwai.common.client.binding.PropertyAdapter;
import de.mogwai.common.client.binding.configurator.DefaultConfigurator;
import de.mogwai.common.client.binding.tools.BeanUtils;
import javax.swing.JCheckBox;

/**
 *
 * @author poolborges
 */
public class CheckBoxAdapter extends PropertyAdapter {

    private final JCheckBox checkBox;

    /**
     * Constructor.
     * @param checkBox
     */
    public CheckBoxAdapter(JCheckBox checkBox) {

        super(null, null);
        configurator = DefaultConfigurator.getInstance();
        this.checkBox = checkBox;
    }

    @Override
    public void model2view(Object aModel, String aPropertyName) {

        try {
            Object value = BeanUtils.getProperty(aModel, aPropertyName);
            if (value == null) {
                    checkBox.setSelected(false);
                } else {
                    checkBox.setSelected((boolean)value);
                }
        } catch (Exception e) {
            throw new RuntimeException("Cannot transfer model2view for property " + aPropertyName, e);
        }
    }

    @Override
    public void view2model(Object aModel, String aPropertyName) {
        try {
            BeanUtils.setProperty(aModel, aPropertyName, checkBox.isSelected());
        } catch (Exception e) {
            throw new RuntimeException("Cannot transfer view2model for property " + aPropertyName, e);
        }
    }

    @Override
    public JComponent[] getComponent() {
        return new JComponent[]{this.checkBox};
    }
}

