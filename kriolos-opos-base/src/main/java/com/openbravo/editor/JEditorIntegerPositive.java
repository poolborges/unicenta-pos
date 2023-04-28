//    KriolOS POS
//    Copyright (c) 2019-2023 KriolOS
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.editor;

import static com.openbravo.editor.JEditorNumber.NUMBER_ZERONULL;
import com.openbravo.format.Formats;

/**
 * 
 * @author JG uniCenta
 */
public class JEditorIntegerPositive extends JEditorNumber<Integer> {

    private static final long serialVersionUID = 1L;

    @Override
    protected Formats getFormat() {
        return Formats.INT;
    }

    @Override
    protected int getMode() {
        return EditorKeys.MODE_INTEGER_POSITIVE;
    }

    public void setValueInteger(int value) {

        String sOldText = getText();
        setCurrentValue(value);
        reprintText();
        firePropertyChange("Text", sOldText, getText());
    }


    @Override
    protected Integer getCurrentValue() {
        try {
            return Integer.parseInt(getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected void setCurrentValue(Integer value) {
        if (value >= 0) {
            setSNumber(Integer.toString(value));
            setBNegative(false);
            setINumberStatus(NUMBER_ZERONULL);
        } else {
            setSNumber(Integer.toString(-value));
            setBNegative(true);
            setINumberStatus(NUMBER_ZERONULL);
        }
    }

}
