/*
 * Copyright (C) 2022 KiolOS<https://github.com/kriolos>
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

package com.openbravo.pos.voucher;

import com.openbravo.data.loader.Datas;
import com.openbravo.data.model.*;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.panels.JPanelTable2;

public class VoucherPanel extends JPanelTable2 {

    private VoucherEditor editor;    

    @Override
    protected void init() {  
        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.Number"), Datas.STRING, Formats.STRING, true, true, true),
                new Field(AppLocal.getIntString("label.customer"), Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.paymenttotal"), Datas.DOUBLE, Formats.DOUBLE),
                new Field(AppLocal.getIntString("label.status"), Datas.STRING, Formats.STRING)
                
        );        
        Table table = new Table(
                "vouchers",
                new PrimaryKey("ID"),
                new Column("VOUCHER_NUMBER"),
                new Column("CUSTOMER"),
                new Column("AMOUNT"),
                new Column("STATUS"));
        lpr = row.getListProvider(app.getSession(), table);
        spr = row.getSaveProvider(app.getSession(), table);              
        
        editor = new VoucherEditor(dirty,app);
    }

   
    @Override
    public EditorRecord getEditor() {
        return editor;
    }  
    
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Vouchers");
    } 
}
