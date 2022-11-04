//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
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
package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JG uniCenta
 */
public abstract class BaseSentence<T> implements SentenceList<T>, SentenceFind<T>, SentenceExec {

    protected final static Logger LOGGER = Logger.getLogger(BaseSentence.class.getName());

    public abstract DataResultSet<T> openExec(Object[] params) throws BasicException;

    public abstract DataResultSet<T> moreResults() throws BasicException;

    public abstract void closeExec() throws BasicException;

    @Override
    public final int exec() throws BasicException {
        return exec((Object) null);
    }

    @Override
    public final int exec(Object params) throws BasicException {
        return exec(new Object[]{params});
    }
    @Override
    public final int exec(Object[] params) throws BasicException {
        DataResultSet<T> SRS = openExec(params);
        if (SRS == null) {
            LOGGER.log(Level.WARNING, "openExec return ResultSet is NULL");
            throw new BasicException(LocalRes.getIntString("exception.noupdatecount"));
        }
        int iResult = SRS.updateCount();
        SRS.close();
        closeExec();
        return iResult;
    }

    @Override
    public final List<T> list() throws BasicException {
        return list(null);
    }

    @Override
    public List<T> list(Object params) throws BasicException {
        return list(new Object[]{params});
    }
    
    @Override
    public final List<T> list(Object[] params) throws BasicException {
        DataResultSet<T> SRS = openExec(params);
        List<T> aSO = fetchAll(SRS);
        SRS.close();
        closeExec();
        return aSO;
    }

    @Override
    public final List<T> listPage(int offset, int length) throws BasicException {
        return listPage(null, offset, length);
    }

    @Override
    public final List<T> listPage(Object[] params, int offset, int length) throws BasicException {
        List<T> aSO = Collections.<T>emptyList();
        try {
            DataResultSet<T> resultSet = openExec(params);
            fetchPage(resultSet, offset, length);
            resultSet.close();
        } catch (Exception ex) {
            throw new BasicException(ex);
        } finally {
            closeExec();
        }

        return aSO;
    }

    @Override
    public final T find() throws BasicException {
        return find((Object)null);
    }

    @Override
    public final T find(Object... params) throws BasicException {
        T obj = null;
        try {
            DataResultSet<T> resultSet = openExec(params);
            if (resultSet != null) {
                obj = fetchOne(resultSet);
                resultSet.close();
            }

        } catch (Exception ex) {
            throw new BasicException(ex);
        } finally {
            closeExec();
        }

        return obj;
    }

    public final List<T> fetchAll(DataResultSet<T> resultSet) throws BasicException {
        if (resultSet == null) {
            LOGGER.log(Level.WARNING, "fetchAll param DataResultSet is NULL");
            throw new BasicException(LocalRes.getIntString("exception.nodataset"));
        }

        List<T> aSO = new ArrayList<>();
        while (resultSet.next()) {
            aSO.add(resultSet.getCurrent());
        }
        return aSO;
    }

    public final List<T> fetchPage(DataResultSet<T> resultSet, int offset, int length) throws BasicException {

        if (resultSet == null) {
            LOGGER.log(Level.WARNING, "fetchAll param DataResultSet is NULL");
            throw new BasicException(LocalRes.getIntString("exception.nodataset"));
        }

        if (offset < 0 || length < 0) {
            LOGGER.log(Level.WARNING, "fetchPage param offset,length are negative");
            throw new BasicException(LocalRes.getIntString("exception.nonegativelimits"));
        }

        // Skip los primeros que no me importan
        while (offset > 0 && resultSet.next()) {
            offset--;
        }

        // me traigo tantos como me han dicho
        List<T> aSO = new ArrayList<>();
        if (offset == 0) {
            while (length > 0 && resultSet.next()) {
                length--;
                aSO.add(resultSet.getCurrent());
            }
        }
        return aSO;
    }

    public final T fetchOne(DataResultSet<T> resultSet) throws BasicException {

        if (resultSet == null) {
            LOGGER.log(Level.WARNING, "fetchAll param DataResultSet is NULL");
            throw new BasicException(LocalRes.getIntString("exception.nodataset"));
        }

        if (resultSet.next()) {
            return resultSet.getCurrent();
        } else {
            return null;
        }
    }

}
