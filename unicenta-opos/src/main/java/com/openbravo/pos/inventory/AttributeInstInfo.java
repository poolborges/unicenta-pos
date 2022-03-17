/*
 * Copyright (C) 2022 KriolOS
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
package com.openbravo.pos.inventory;

/**
 *
 * @author poolborges
 */
public class AttributeInstInfo {

    private String attid;
    private String attname;
    private String id;
    private String value;

    public AttributeInstInfo(String attid, String attname, String id, String value) {
        this.attid = attid;
        this.attname = attname;
        this.id = id;
        this.value = value;
    }
    
    public String getAttid() {
        return attid;
    }
    
    public String getAttname() {
        return attname;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
