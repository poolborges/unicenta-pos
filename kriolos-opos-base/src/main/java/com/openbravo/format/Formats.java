//    KriolOS POS
//    Copyright (c) 2019-2023 KriolOS
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
package com.openbravo.format;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import com.openbravo.basic.BasicException;

/**
 *
 * @author JG uniCenta
 * @param <T>
 */
public abstract class Formats<T> {

    private final static String DEFAULT_PERCENT_FORMAT = "#,##0.##%";
    private final static String DEFAULT_HOURMIN_FORMAT = "H:mm:ss";
    private final static String DEFAULT_SIMPLEDATE_FORMAT = "dd-MM-yyyy";
    
    public final static Formats<Object> NULL = new FormatsNULL();
    public final static Formats<Integer> INT = new FormatsINT();
    public final static Formats<String> STRING = new FormatsSTRING();
    public final static Formats<Double> DOUBLE = new FormatsDOUBLE();
    public final static Formats<Double> CURRENCY = new FormatsCURRENCY();
    public final static Formats<Double> PERCENT = new FormatsPERCENT();
    public final static Formats<Boolean> BOOLEAN = new FormatsBOOLEAN();
    public final static Formats<Date> TIMESTAMP = new FormatsTIMESTAMP();
    public final static Formats<Date> DATE = new FormatsDATE();
    public final static Formats<Date> TIME = new FormatsTIME();
    public final static Formats<byte[]> BYTEA = new FormatsBYTEA();
    public final static Formats<Date> HOURMIN = new FormatsHOURMIN();
    public final static Formats<Date> SIMPLEDATE = new FormatsSIMPLEDATE();

    //Support those format up
    private static NumberFormat m_integerformat = NumberFormat.getIntegerInstance();
    private static NumberFormat m_doubleformat = NumberFormat.getNumberInstance();
    private static NumberFormat m_currencyformat = NumberFormat.getCurrencyInstance();
    private static NumberFormat m_percentformat = NumberFormat.getCurrencyInstance(); //new DecimalFormat(DEFAULT_PERCENT_FORMAT);
    private static DateFormat m_dateformat = DateFormat.getDateInstance();
    private static DateFormat m_timeformat = DateFormat.getTimeInstance();
    private static DateFormat m_datetimeformat = DateFormat.getDateTimeInstance();
    private static final DateFormat m_hourminformat = new SimpleDateFormat(DEFAULT_HOURMIN_FORMAT);
    private static final DateFormat m_simpledate = new SimpleDateFormat(DEFAULT_SIMPLEDATE_FORMAT);

    protected Formats() {
    }

    public static int getCurrencyDecimals() {
        return m_currencyformat.getMaximumFractionDigits();
    }

    public String formatValue(T value) {
        if (value == null) {
            return "";
        } else {
            return formatValueInt(value);
        }
    }

    public T parseValue(String value, T defvalue) throws BasicException {
        if (value == null || "".equals(value)) {
            return defvalue;
        } else {
            try {
                return parseValueInt(value);
            } catch (ParseException e) {
                throw new BasicException(e.getMessage(), e);
            }
        }
    }

    public T parseValue(String value) throws BasicException {
        return parseValue(value, null);
    }

    public static void setIntegerPattern(String pattern) {
        if (pattern == null || pattern.equals("")) {
            m_integerformat = NumberFormat.getIntegerInstance();
        } else {
            m_integerformat = new DecimalFormat(pattern);
        }
    }

    public static void setDoublePattern(String pattern) {
        if (pattern == null || pattern.equals("")) {
            m_doubleformat = NumberFormat.getNumberInstance();
        } else {
            m_doubleformat = new DecimalFormat(pattern);
        }
    }

    public static void setCurrencyPattern(String pattern) {
        if (pattern == null || pattern.equals("")) {
            m_currencyformat = NumberFormat.getCurrencyInstance();
        } else {
            m_currencyformat = new DecimalFormat(pattern);
        }
    }

    public static void setPercentPattern(String pattern) {
        if (pattern == null || pattern.equals("")) {
            m_percentformat = NumberFormat.getPercentInstance();
        } else {
            m_percentformat = new DecimalFormat(pattern);
        }
    }

    public static void setDatePattern(String pattern) {
        if (pattern == null || pattern.equals("")) {
            m_dateformat = DateFormat.getDateInstance();
        } else {
            m_dateformat = new SimpleDateFormat(pattern);
        }
    }

    public static void setTimePattern(String pattern) {
        if (pattern == null || pattern.equals("")) {
            m_timeformat = DateFormat.getTimeInstance();
        } else {
            m_timeformat = new SimpleDateFormat(pattern);
        }
    }

    public static void setDateTimePattern(String pattern) {
        if (pattern == null || pattern.equals("")) {
            m_datetimeformat = DateFormat.getDateTimeInstance();
        } else {
            m_datetimeformat = new SimpleDateFormat(pattern);
        }
    }

    protected abstract String formatValueInt(T value);

    protected abstract T parseValueInt(String value) throws ParseException;

    public abstract int getAlignment();

    private static final class FormatsNULL extends Formats {

        @Override
        protected String formatValueInt(Object value) {
            return null;
        }

        @Override
        protected Object parseValueInt(String value) throws ParseException {
            return null;
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.LEFT;
        }
    }

    private static final class FormatsINT extends Formats<Integer> {

        @Override
        protected String formatValueInt(Integer value) {
            return m_integerformat.format(((Number) value).longValue());
        }

        @Override
        protected Integer parseValueInt(String value) throws ParseException {
            return m_integerformat.parse(value).intValue();
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.RIGHT;
        }
    }

    private static final class FormatsSTRING extends Formats<String> {

        @Override
        protected String formatValueInt(String value) {
            return value;
        }

        @Override
        protected String parseValueInt(String value) throws ParseException {
            return value;
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.LEFT;
        }
    }

    private static final class FormatsDOUBLE extends Formats<Double> {

        @Override
        protected String formatValueInt(Double value) {
            return m_doubleformat.format(DoubleUtils.fixDecimals((Number) value)); // quickfix for 3838
        }

        @Override
        protected Double parseValueInt(String value) throws ParseException {
            return m_doubleformat.parse(value).doubleValue();
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.RIGHT;
        }
    }

    private static final class FormatsPERCENT extends Formats<Double> {

        @Override
        protected String formatValueInt(Double value) {
            return m_percentformat.format(DoubleUtils.fixDecimals((Number) value)); // quickfix for 3838
        }

        @Override
        protected Double parseValueInt(String value) throws ParseException {
            try {
                return m_percentformat.parse(value).doubleValue();
            } catch (ParseException e) {
                // Segunda oportunidad como numero normalito
                return m_doubleformat.parse(value).doubleValue() / 100;
            }
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.RIGHT;
        }
    }

    private static final class FormatsCURRENCY extends Formats<Double> {

        @Override
        protected String formatValueInt(Double value) {
            return m_currencyformat.format(DoubleUtils.fixDecimals((Number) value)); // quickfix for 3838
        }

        @Override
        protected Double parseValueInt(String value) throws ParseException {
            try {
                return m_currencyformat.parse(value).doubleValue();
            } catch (ParseException e) {
                return m_doubleformat.parse(value).doubleValue();
            }
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.RIGHT;
        }
    }

    private static final class FormatsBOOLEAN extends Formats<Boolean> {

        @Override
        protected String formatValueInt(Boolean value) {
            return value.toString();
        }

        @Override
        protected Boolean parseValueInt(String value) throws ParseException {
            return Boolean.valueOf(value);
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.CENTER;
        }
    }

    private static final class FormatsTIMESTAMP extends Formats<Date> {

        @Override
        protected String formatValueInt(Date value) {
            return m_datetimeformat.format( value);
        }

        @Override
        protected Date parseValueInt(String value) throws ParseException {
            try {
                return m_datetimeformat.parse(value);
            } catch (ParseException e) {
                return m_dateformat.parse(value);
            }
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.CENTER;
        }
    }

    private static final class FormatsDATE extends Formats<Date> {

        @Override
        protected String formatValueInt(Date value) {
            return m_dateformat.format(value);
        }

        @Override
        protected Date parseValueInt(String value) throws ParseException {
            return m_dateformat.parse(value);
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.CENTER;
        }
    }

    private static final class FormatsTIME extends Formats<Date> {

        @Override
        protected String formatValueInt(Date value) {
            return m_timeformat.format(value);
        }

        @Override
        protected Date parseValueInt(String value) throws ParseException {
            return m_timeformat.parse(value);
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.CENTER;
        }
    }

    private static final class FormatsBYTEA extends Formats<byte[]> {

        @Override
        protected String formatValueInt(byte[] value) {
            try {
                return new String(value, "UTF-8");
            } catch (java.io.UnsupportedEncodingException eu) {
                return "";
            }
        }

        @Override
        protected byte[] parseValueInt(String value) throws ParseException {
            try {
                return value.getBytes("UTF-8");
            } catch (java.io.UnsupportedEncodingException eu) {
                return new byte[0];
            }
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.LEADING;
        }
    }

    private static final class FormatsHOURMIN extends Formats<Date> {

        @Override
        protected String formatValueInt(Date value) {
            return m_hourminformat.format(value);
        }

        @Override
        protected Date parseValueInt(String value) throws ParseException {
            return m_hourminformat.parse(value);
        }

        @Override
        public int getAlignment() {

            return javax.swing.SwingConstants.CENTER;

        }

    }

    private static final class FormatsSIMPLEDATE extends Formats<Date> {

        @Override
        protected String formatValueInt(Date value) {

            return m_simpledate.format(value);
        }

        @Override
        protected Date parseValueInt(String value) throws ParseException {

            return m_simpledate.parse(value);
        }

        @Override
        public int getAlignment() {

            return javax.swing.SwingConstants.CENTER;
        }

    }

    private static final class FormatsRESOURCE extends Formats {

        private ResourceBundle m_rb;
        private String m_sPrefix;

        public FormatsRESOURCE(ResourceBundle rb, String sPrefix) {
            m_rb = rb;
            m_sPrefix = sPrefix;
        }

        @Override
        protected String formatValueInt(Object value) {
            try {
                return m_rb.getString(m_sPrefix + (String) value);
            } catch (MissingResourceException e) {
                return (String) value;
            }
        }

        @Override
        protected Object parseValueInt(String value) throws ParseException {
            return value;
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.LEFT;
        }
    }
}
