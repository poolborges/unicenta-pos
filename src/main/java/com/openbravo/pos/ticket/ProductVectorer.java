//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.ticket;

import com.openbravo.format.Formats;
import com.openbravo.data.loader.Vectorer;
import com.openbravo.basic.BasicException;
import com.openbravo.pos.forms.AppLocal;
/**
 *
 * @author  adrian
 */
public class ProductVectorer implements Vectorer {
    
    private static String[] m_sHeaders = {
        AppLocal.getIntString("label.prodref"),
        AppLocal.getIntString("label.prodbarcode"),
        AppLocal.getIntString("label.prodname"),
        AppLocal.getIntString("label.prodpricebuy"),
        AppLocal.getIntString("label.prodpricesell")
    };
    
    /** Creates a new instance of ProductVectorer */
    public ProductVectorer() {
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public String[] getHeaders() throws BasicException {
        return m_sHeaders;
    }

    /**
     *
     * @param obj
     * @return
     * @throws BasicException
     */
    public String[] getValues(Object obj) throws BasicException {   
        ProductInfoExt myprod = (ProductInfoExt) obj;
        String[] m_sValues = new String[5];
        m_sValues[0] = Formats.STRING.formatValue(myprod.getReference());
        m_sValues[1] = Formats.STRING.formatValue(myprod.getCode());
        m_sValues[2] = Formats.STRING.formatValue(myprod.getName());
        m_sValues[3] = Formats.CURRENCY.formatValue(new Double(myprod.getPriceBuy()));
        m_sValues[4] = Formats.CURRENCY.formatValue(new Double(myprod.getPriceSell()));     
        return m_sValues;
    }
}
