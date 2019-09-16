//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
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

package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;

/**
 *
 * @author adrianromero
 * Created on February 6, 2007, 4:06 PM
 *
 */
public abstract class SentenceExecTransaction implements SentenceExec {
    
    private Session m_s;
    
    /**
     *
     * @param s
     */
    public SentenceExecTransaction(Session s) {
        m_s = s;
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public final int exec() throws BasicException {
        return exec((Object) null);
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public final int exec(Object... params) throws BasicException {
        return exec((Object) params);
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public final int exec(final Object params) throws BasicException {
        
        Transaction<Integer> t = new Transaction<Integer>(m_s) {
            public Integer transact() throws BasicException{
                return execInTransaction(params);
            }
        };
        
        return t.execute();
    }
    
    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    protected abstract int execInTransaction(Object params) throws BasicException; 
}

