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

package com.openbravo.format;

import java.text.ParseException;

/**
 *
 * @author JG uniCenta
 */
public class FormatsValidate extends Formats {
    
    private Formats m_fmt;
    private FormatsConstrain[] m_aConstrains;

    
    public FormatsValidate(Formats fmt, FormatsConstrain[] constrains) {
        m_fmt = fmt;
        m_aConstrains = constrains;
    }

    public FormatsValidate(Formats fmt) {
        this(fmt, new FormatsConstrain[0]);
    }

    public FormatsValidate(Formats fmt, FormatsConstrain constrain) {
        this(fmt, new FormatsConstrain[]{constrain});
    }

    @Override
    protected String formatValueInt(Object value) {
        return m_fmt.formatValueInt(value);
    }

    @Override
    protected Object parseValueInt(String value) throws ParseException {     
        Object val = m_fmt.parseValueInt(value);        
        for (int i = 0; i < m_aConstrains.length; i++) {
            val = m_aConstrains[i].check(val);
        }
        return val;
    }

    @Override
    public int getAlignment() {
        return m_fmt.getAlignment();
    }
}
