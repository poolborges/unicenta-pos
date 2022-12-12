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


import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.binding.adapter.RadioButtonAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author msertic
 */
public class DataBindingTest1 {

    public static void main(String args[]) {

        // Setup the view
        JFrame frame = new JFrame("Test");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setSize(640, 480);

        CellConstraints cons = new CellConstraints();

        FormLayout layout = new FormLayout("90dlu,80dlu:grow",
                "2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,p,p,2dlu");

        JPanel content = new JPanel();
        content.setLayout(layout);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(content, BorderLayout.CENTER);

        JLabel label1 = new JLabel("JTextField with value");
        JTextField textfield1 = new JTextField();

        content.add(label1, cons.xy(1, 2));
        content.add(textfield1, cons.xy(2, 2));

        JLabel label2 = new JLabel("JTextField without value");
        JTextField textfield2 = new JTextField();

        content.add(label2, cons.xy(1, 4));
        content.add(textfield2, cons.xy(2, 4));

        JLabel label3 = new JLabel("JCheckBox");
        JCheckBox box3 = new JCheckBox();
        

        content.add(label3, cons.xy(1, 6));
        content.add(box3, cons.xy(2, 6));

        JLabel label4 = new JLabel("JComboBox bound to Vector");
        JComboBox combo1 = new JComboBox();

        content.add(label4, cons.xy(1, 8));
        content.add(combo1, cons.xy(2, 8));

        JLabel label5 = new JLabel("JComboBox bound to Object[]");
        JComboBox combo2 = new JComboBox();

        content.add(label5, cons.xy(1, 10));
        content.add(combo2, cons.xy(2, 10));

        JLabel label6 = new JLabel("JRadioButton");
        JRadioButton radio1 = new JRadioButton("Radio 1");
        JRadioButton radio2 = new JRadioButton("Radio 2");
        JRadioButton radio3 = new JRadioButton("Radio 3");

        content.add(label6, cons.xy(1, 12));
        content.add(radio1, cons.xy(2, 12));
        content.add(radio2, cons.xy(2, 13));
        content.add(radio3, cons.xy(2, 14));

        //
        //
        // Now, here comes the tricky part !
        //
        //

        // Setup the model
        ExampleModel model = new ExampleModel();
        model.setString1("Wutzpu");
        model.setBool(true);
        model.setSelected("2");

        
        // Setup the binding
        final BindingInfo<ExampleModel> binding = new BindingInfo<>();
        binding.addBinding("string1", textfield1);
        binding.addBinding("string2", textfield2);
        //binding.addBinding("bool", new CheckBoxAdapter(box3));
        binding.addBinding("type", combo1);
        //binding.addBinding("entries", new ComboboxModelAdapter(combo1));
        //binding.addBinding("entriesArray", new ComboboxModelAdapter(combo2));

        RadioButtonAdapter adaptor = new RadioButtonAdapter();
        adaptor.addMapping("1", radio1);
        adaptor.addMapping("2", radio2);
        adaptor.addMapping("3", radio3);
        //binding.addBinding("selected", adaptor);

        binding.setDefaultModel(model);

        // Initialize the view !
        // This also forces the collection to model mapping to be initialized !
        binding.model2view();

        // Event listener
        JButton button = new JButton("View 2 Model");

        //
        // This is an example event listener !
        //
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Transfer the data to the model
                binding.view2model();

                // Get the model
                ExampleModel model = (ExampleModel) binding.getDefaultModel();

                // Modify the model
                String test = model.getType() + " " + model.getSelected();
                model.setString2(test);

                // Transfer the model to the view
                binding.model2view();
            }
        });
        frame.getContentPane().add(button, BorderLayout.NORTH);

        frame.show();
    }
    
   
}


 class ExampleModel {

    private String string1;
    private String string2;
    private boolean bool;
    private String selected;
    private String type;
    private Collection<String> entries = new HashSet<String>();
    private String[] entriesArray = new String[0];

    public ExampleModel() {
        entries.add("A");
        entries.add("B");
        entries.add("C");

        entriesArray = new String[]{"D", "E", "F"};
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public boolean isBool() {
        return bool;
    }
    
    public boolean getBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public String getSelected() {
        return selected;
    }


    public void setSelected(String selected) {
        this.selected = selected;
    }


    public String getString2() {
        return string2;
    }


    public void setString2(String string2) {
        this.string2 = string2;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public Collection<String> getEntries() {
        return entries;
    }


    public void setEntries(Collection<String> entries) {
        this.entries = entries;
    }


    public String[] getEntriesArray() {
        return entriesArray;
    }


    public void setEntriesArray(String[] entriesArray) {
        this.entriesArray = entriesArray;
    }
}
