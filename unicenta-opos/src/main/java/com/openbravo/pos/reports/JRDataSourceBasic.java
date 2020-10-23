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
package com.openbravo.pos.reports;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.BaseSentence;
import com.openbravo.data.loader.DataResultSet;
import com.openbravo.pos.forms.AppLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author JG uniCenta
 */
public class JRDataSourceBasic implements JRDataSource {

    private static final Logger LOGGER = Logger.getLogger(JRDataSourceBasic.class.getName());

    private BaseSentence sent;
    private DataResultSet SRS = null;
    private Object current = null;

    private ReportFields m_fields = null;

    /**
     * Creates a new instance of JRDataSourceBasic
     *
     * @param sent
     * @param fields
     * @param params
     * @throws com.openbravo.basic.BasicException
     */
    public JRDataSourceBasic(BaseSentence sent, ReportFields fields, Object params) throws BasicException {

        this.sent = sent;
        SRS = sent.openExec(params);
        m_fields = fields;
    }

    /**
     *
     * @param jrField
     * @return
     * @throws JRException
     */
    @Override
    public Object getFieldValue(JRField jrField) throws JRException {

        try {
            return m_fields.getField(current, jrField.getName());
        } catch (ReportException ex) {
            LOGGER.log(Level.SEVERE, "Exception on JRDataSourceBasic", ex);
            throw new JRException(ex);
        }
    }

    /**
     *
     * @return @throws JRException
     */
    @Override
    public boolean next() throws JRException {

        if (SRS == null) {
            LOGGER.log(Level.SEVERE, "SRS is null");
            throw new JRException(AppLocal.getIntString("exception.unavailabledataset"));
        }

        try {
            if (SRS.next()) {
                current = SRS.getCurrent();
                return true;
            } else {
                current = null;
                SRS = null;
                sent.closeExec();
                sent = null;
                return false;
            }
        } catch (BasicException e) {
            LOGGER.log(Level.SEVERE, "Exception on JRDataSourceBasic", e);
            throw new JRException(e);
        }
    }
}
