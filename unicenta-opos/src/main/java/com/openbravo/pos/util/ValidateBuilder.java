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
package com.openbravo.pos.util;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.format.Formats;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;


public class ValidateBuilder {
    
    public static final int IS_NOT_EMPTY = 1;
    public static final int IS_INT = 2;
    public static final int IS_DOUBLE = 3;
    public static final int IS_CURRENCY = 4;
    public static final int IS_DOUBLE_NULL = 5 ;
    
    private final List<String> listMessage ;
    private final Component parent;
    
    public ValidateBuilder(Component _parent) {
        parent = _parent;
        listMessage = new ArrayList<>();
    }
    
    public void setValidate(Object value , int tipe ,  String message){
        if (value==null){
                listMessage.add(message);
        }
    }
    
    public void setValidate(String value , int tipe ,  String message){
        if(tipe == IS_NOT_EMPTY){
            if (value.equals("")){
                listMessage.add(message);
            }
        }else if (tipe == IS_INT){
            if (!isInt(value)){
                listMessage.add(message);
            }
        }else if (tipe == IS_DOUBLE){
            if (!isDouble(value)){
                listMessage.add(message);
            }
        }else if (tipe == IS_CURRENCY){
             if (!isCurrency(value)){
                  listMessage.add(message);
             }
        }else if (tipe == IS_DOUBLE_NULL){
            if (!isDoubleNull(value)){
                listMessage.add(message);
            }
        }
    }
    
    private void showMessage(){
        StringBuilder sb = new StringBuilder();    
        sb.append("<br>");
        for (String message : listMessage){
            sb.append(message+"<br>");
        }
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE,sb.toString() , null);
            msg.show(parent);
         
    }
            
    public boolean  getValid(){
        boolean isValid = true;
        if (listMessage.size()>0){
            isValid= false;
            showMessage();
        }
        return isValid;
    }
    
    private  boolean isInt(String n) {
            try {
            Object a = Formats.INT.parseValue(n);
            if (a!=null){
                return true;
            }else{
                return false;
            }
        } catch (BasicException ex) {
            return false;
        }
    }
    
    
    private  boolean isDouble(String n) {
            try {
            Object a = Formats.DOUBLE.parseValue(n);
            if (a!=null){
                return true;
            }else{
                return false;
            }
        } catch (BasicException ex) {
            return false;
        }
    }
    
    
    
      private  boolean isDoubleNull(String n) {
            try {
            Object a = Formats.DOUBLE.parseValue(n);
             return true;   
        } catch (BasicException ex) {
            return false;
        }
    }
    
    
    private boolean isCurrency(String n){
        try {
            Object a = Formats.CURRENCY.parseValue(n);
            if (a!=null){
                return true;
            }else{
                return false;
            }
            
        } catch (BasicException ex) {
            return false;
        }
    }
}
