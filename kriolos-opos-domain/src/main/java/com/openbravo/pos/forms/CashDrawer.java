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
package com.openbravo.pos.forms;

import java.util.Date;

/**
 *
 * @author poolborges
 */
public class CashDrawer {

    private String cashIndex;
    private int cashSequence;
    private Date cashDateStart;
    private Date cashDateEnd;

    public String getCashIndex() {
        return cashIndex;
    }

    public void setCashIndex(String cashIndex) {
        this.cashIndex = cashIndex;
    }

    public int getCashSequence() {
        return cashSequence;
    }

    public void setCashSequence(int cashSequence) {
        this.cashSequence = cashSequence;
    }

    public Date getCashDateStart() {
        return cashDateStart;
    }

    public void setCashDateStart(Date cashDateStart) {
        this.cashDateStart = cashDateStart;
    }

    public Date getCashDateEnd() {
        return cashDateEnd;
    }

    public void setCashDateEnd(Date cashDateEnd) {
        this.cashDateEnd = cashDateEnd;
    }

}

