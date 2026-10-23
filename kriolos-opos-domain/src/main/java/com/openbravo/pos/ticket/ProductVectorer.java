//    KrOS POS
//    Copyright (c) 2019-2023 KriolOS
//    
//
//     
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
//    along with KrOS POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.ticket;

import com.openbravo.format.Formats;
import com.openbravo.data.loader.Vectorer;
import com.openbravo.basic.BasicException;
import com.openbravo.basic.AppLocal;
/**
 *
 * @author  adrian
 */
public class ProductVectorer implements Vectorer<ProductInfoExt> {
    
    private static String[] headers = {
        AppLocal.getIntString("label.prodref"),
        AppLocal.getIntString("label.prodbarcode"),
        AppLocal.getIntString("label.prodname"),
        AppLocal.getIntString("label.prodpricebuy"),
        AppLocal.getIntString("label.prodpricesell")
    };
    

    public ProductVectorer() {}

    @Override
    public String[] getHeaders() throws BasicException {
        return headers;
    }

    /**
     *
     * @param myprod
     * @return String[5] {Reference, Code, Name, PriceBuy, PriceSell}
     * @throws BasicException
     */
    @Override
    public String[] getValues(ProductInfoExt myprod) throws BasicException {   
        String[] values = new String[5];
        values[0] = Formats.STRING.formatValue(myprod.getReference());
        values[1] = Formats.STRING.formatValue(myprod.getCode());
        values[2] = Formats.STRING.formatValue(myprod.getName());
        values[3] = Formats.CURRENCY.formatValue(myprod.getPriceBuy());
        values[4] = Formats.CURRENCY.formatValue(myprod.getPriceSell());     
        return values;
    }
}
