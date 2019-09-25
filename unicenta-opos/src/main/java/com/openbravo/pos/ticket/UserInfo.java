//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2017 uniCenta
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

import java.io.Serializable;

/**
 *
 * @author adrianromero
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 7537578737839L;
    private String m_sId;
    private String m_sName;

    /** Creates a new instance of UserInfoBasic
     * @param id
     * @param name */
    public UserInfo(String id, String name) {
        m_sId = id;
        m_sName = name;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return m_sId;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return m_sName;
    }
}
