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

package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 *
 * @author JG uniCenta
 * @param <T>
 */
public abstract class Datas<T> {

    public final static Datas<Double> DOUBLE = new DatasDOUBLE();
    public final static Datas<Integer> INT = new DatasINT();
    public final static Datas<String> STRING = new DatasSTRING();
    public final static Datas<Boolean> BOOLEAN = new DatasBOOLEAN();
    public final static Datas<Date> TIMESTAMP = new DatasTIMESTAMP();
    public final static Datas<byte[]> BYTES = new DatasBYTES();
    public final static Datas<BufferedImage> IMAGE = new DatasIMAGE();
    public final static Datas<Object> OBJECT = new DatasOBJECT();
    public final static Datas<Object> SERIALIZABLE = new DatasSERIALIZABLE();
    public final static Datas<Object> NULL = new DatasNULL();

    private final static DateFormat tsf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private Datas() {}

    public abstract T getValue(DataRead dr, int i) throws BasicException;

    public abstract void setValue(DataWrite dw, int i, T value) throws BasicException;

    public abstract Class<T> getClassValue();

    protected abstract String toStringAbstract(T value);

    protected abstract int compareAbstract(T o1, T o2);

    public String toString(T value) {
        if (value == null) {
            return "null";
        } else {
            return toStringAbstract(value);
        }
    }
    
    @Override
    public String toString() {
        return getClassValue().getName();
    }

    public int compare(T o1, T o2) {
        if (o1 == null) {
            if (o2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (o2 == null) {
            return +1;
        } else {
            return compareAbstract(o1, o2);
        }
    }

    private static final class DatasINT extends Datas<Integer> {
        
        @Override
        public Integer getValue(DataRead dr, int i) throws BasicException {
            return dr.getInt(i);
        }
        
        @Override
        public void setValue(DataWrite dw, int i, Integer value) throws BasicException {
            dw.setInt(i, value);
        }
        
        @Override
        public Class<Integer> getClassValue() {
            return java.lang.Integer.class;
        }
        
        @Override
        protected String toStringAbstract(Integer value) {
            return (value).toString();
        }
        
        @Override
        protected int compareAbstract(Integer o1, Integer o2) {
            return (o1).compareTo(o2);
        }        
    }

    private static final class DatasSTRING extends Datas<String> {
        
        @Override
        public String getValue(DataRead dr, int i) throws BasicException {
            return dr.getString(i);
        }
        
        @Override
        public void setValue(DataWrite dw, int i, String value) throws BasicException {
            dw.setString(i, value);
        }
        
        @Override
        public Class<String> getClassValue() {
            return java.lang.String.class;
        }
        
        @Override
        protected String toStringAbstract(String value) {
            return "\'" + DataWriteUtils.getEscaped(value) + "\'";
        }
        
        @Override
        protected int compareAbstract(String o1, String o2) {
            return (o1).compareTo(o2);
        }           
    }

    private static final class DatasDOUBLE extends Datas<Double> {
        
        @Override
        public Double getValue(DataRead dr, int i) throws BasicException {
            return dr.getDouble(i);
        }
        
        @Override
        public void setValue(DataWrite dw, int i, Double value) throws BasicException {
            dw.setDouble(i, value);
        }
        
        @Override
        public Class<Double> getClassValue() {
            return java.lang.Double.class;
        }
        
        @Override
        protected String toStringAbstract(Double value) {
            return (value).toString();
        }
        
        @Override
        protected int compareAbstract(Double o1, Double o2) {
            return (o1).compareTo(o2);
        }   
    }

    private static final class DatasBOOLEAN extends Datas<Boolean> {
        
        @Override
        public Boolean getValue(DataRead dr, int i) throws BasicException {
            return dr.getBoolean(i);
        }
        
        @Override
        public void setValue(DataWrite dw, int i, Boolean value) throws BasicException {
            dw.setBoolean(i, value);
        }
        
        @Override
        public Class<Boolean> getClassValue() {
            return java.lang.Boolean.class;
        }
        
        @Override
        protected String toStringAbstract(Boolean value) {
            return (value).toString();
        }
        
        @Override
        protected int compareAbstract(Boolean o1, Boolean o2) {
            return (o1).compareTo(o2);
        }   
    }

    private static final class DatasTIMESTAMP extends Datas<Date> {
        
        @Override
        public java.util.Date getValue(DataRead dr, int i) throws BasicException {
            return dr.getTimestamp(i);
        }
        
        @Override
         public void setValue(DataWrite dw, int i, Date value) throws BasicException {
            dw.setTimestamp(i, value);
        }
        
        @Override
        public Class<Date> getClassValue() {
            return java.util.Date.class;
        }
        
        @Override
        protected String toStringAbstract(Date value) {
            return tsf.format(value);
        }
        
        @Override
        protected int compareAbstract(Date o1, Date o2) {
            return (o1).compareTo(o2);
        }   
    }

    private static final class DatasBYTES extends Datas<byte[]> {
        
        @Override
        public byte[] getValue(DataRead dr, int i) throws BasicException {
            return dr.getBytes(i);
        }
        
        @Override
        public void setValue(DataWrite dw, int i, byte[] value) throws BasicException {
            dw.setBytes(i, value);
        }
        
        @Override
        public Class<byte[]> getClassValue() {
            return byte[].class;
        }
        
        @Override
        protected String toStringAbstract(byte[] value) {
            return Base64.getEncoder().encodeToString(value);
        }
        
        @Override
        protected int compareAbstract(byte[] o1, byte[] o2) {
            throw new UnsupportedOperationException();
        }   
    }

    private static final class DatasIMAGE extends Datas<BufferedImage> {
        @Override
        public BufferedImage getValue(DataRead dr, int i) throws BasicException {
            
            return ImageUtils.readImage(dr.getBytes(i));
        }
        @Override
        public void setValue(DataWrite dw, int i, BufferedImage value) throws BasicException {
            dw.setBytes(i, ImageUtils.writeImage( value));
        }
        @Override
        public Class<BufferedImage> getClassValue() {
            return java.awt.image.BufferedImage.class;
        }
        @Override
        protected String toStringAbstract(BufferedImage value) {
            return Base64.getEncoder().encodeToString(ImageUtils.writeImage(value));
        }
        @Override
        protected int compareAbstract(BufferedImage o1, BufferedImage o2) {
            throw new UnsupportedOperationException();
        }   
    }  

    private static final class DatasOBJECT extends Datas<Object> {
        
        @Override
        public Object getValue(DataRead dr, int i) throws BasicException {
            return dr.getObject(i);
        }
        
        @Override
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            dw.setObject(i, value);
        }
        
        @Override
        public Class<Object> getClassValue() {
            return java.lang.Object.class;
        }
        
        @Override
        protected String toStringAbstract(Object value) {
            return Base64.getEncoder().encodeToString(ImageUtils.writeSerializable(value));
        }
        
        @Override
        protected int compareAbstract(Object o1, Object o2) {
            throw new UnsupportedOperationException();
        }   
    }
    
    private static final class DatasSERIALIZABLE extends Datas<Object> {
        
        @Override
        public Object getValue(DataRead dr, int i) throws BasicException {
            return ImageUtils.readSerializable(dr.getBytes(i));
        }
        
        @Override
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            dw.setBytes(i, ImageUtils.writeSerializable(value));
        }
        
        @Override
        public Class<Object> getClassValue() {
            return java.lang.Object.class;
        }
        
        @Override
        protected String toStringAbstract(Object value) {
            return "0x" + ImageUtils.bytes2hex(ImageUtils.writeSerializable(value));
        }
        
        @Override
        protected int compareAbstract(Object o1, Object o2) {
            throw new UnsupportedOperationException();
        }   
    }       
    
    private static final class DatasNULL extends Datas<Object> {
        
        @Override
        public Object getValue(DataRead dr, int i) throws BasicException {
            return null;
        }
        @Override
        public void setValue(DataWrite dw, int i, Object value) throws BasicException {
            // No asigno null, no asigno nada.
        }
        @Override
        public Class<Object> getClassValue() {
            return java.lang.Object.class;
        }
        @Override
        protected String toStringAbstract(Object value) {
            return "null";
        }
        @Override
        protected int compareAbstract(Object o1, Object o2) {
            throw new UnsupportedOperationException();
        }   
    }    
}
