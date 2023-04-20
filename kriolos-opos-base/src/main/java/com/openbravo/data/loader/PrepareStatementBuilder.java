/*
 * Copyright (C) 2023 Paulo Borges
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
package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;
import java.sql.PreparedStatement;

/**
 *
 * @author poolb
 */
public class PrepareStatementBuilder implements ISQLBuilderStatic {

    private final PreparedStatement m_ps;
    private final PreparedSentenceDataWrite dataWrite;

    public PrepareStatementBuilder(PreparedStatement ps) {
        this.m_ps = ps;
        this.dataWrite = new PreparedSentenceDataWrite(ps);
    }

    @Override
    public String getSQL(SerializerWrite sw, Object params) throws BasicException {
        sw.writeValues(dataWrite, params);

        return this.m_ps.toString();
    }

}
