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

package com.openbravo.pos.epm;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.BeanFactoryDataSingle;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Ali Safdar and Aneeqa Baber
 */
public class DataLogicPresenceManagement extends BeanFactoryDataSingle {

    /**
     *
     */
    protected Session s;

    private SentenceExec m_checkin;
    private SentenceExec m_checkout;
    private SentenceFind m_checkdate;

    private SentenceList m_breaksvisible;
    private SentenceExec m_startbreak;
    private SentenceExec m_endbreak;

    private SentenceFind m_isonbreak;
    private SentenceFind m_isonleave;
    private SentenceFind m_shiftid;

    private SentenceFind m_lastcheckin;
    private SentenceFind m_lastcheckout;
    private SentenceFind m_startbreaktime;

    private SentenceFind m_lastbreakid;
    private SentenceFind m_breakname;
    
    private SerializerRead breakread;
    private TableDefinition tbreaks;
    private TableDefinition tleaves;

    /**
     *
     */
    public DataLogicPresenceManagement() {
    }
    
    /**
     *
     * @param s
     */
    @Override
    public void init(Session s){

        this.s = s;
        breakread = new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new Break(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getBoolean(4));
            }
        };

        tbreaks = new TableDefinition(s
            , "breaks"
            , new String[] { "ID", "NAME", "NOTES", "VISIBLE"}
            , new String[] { "ID", AppLocal.getIntString("label.epm.employee"), AppLocal.getIntString("label.epm.notes"), "VISIBLE"}
            , new Datas[] { Datas.STRING, Datas.STRING, Datas.STRING, Datas.BOOLEAN}
            , new Formats[] { Formats.STRING, Formats.STRING, Formats.STRING, Formats.BOOLEAN}
            , new int[] {0}
        );

         tleaves = new TableDefinition(s
            , "leaves"
            , new String[] { "ID", "PPLID", "NAME", "STARTDATE", "ENDDATE", "NOTES"}
            , new String[] { "ID", AppLocal.getIntString("label.epm.employee.id"), AppLocal.getIntString("label.epm.employee"), AppLocal.getIntString("label.StartDate"), AppLocal.getIntString("label.EndDate"), AppLocal.getIntString("label.notes")}
            , new Datas[] { Datas.STRING, Datas.STRING, Datas.STRING, Datas.TIMESTAMP, Datas.TIMESTAMP, Datas.STRING}
            , new Formats[] { Formats.STRING, Formats.STRING, Formats.STRING, Formats.TIMESTAMP, Formats.TIMESTAMP, Formats.STRING}
            , new int[] {0}
        );

        m_breaksvisible = new StaticSentence(s
            , "SELECT ID, NAME, NOTES, VISIBLE FROM breaks WHERE VISIBLE = " + s.DB.TRUE()
            , null
            , breakread);

        m_checkin =  new PreparedSentence(s
                , "INSERT INTO shifts(ID, STARTSHIFT, PPLID) VALUES (?, ?, ?)"
                , new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.TIMESTAMP, Datas.STRING}));

        m_checkout = new StaticSentence(s
                , "UPDATE shifts SET ENDSHIFT = ? WHERE ENDSHIFT IS NULL AND PPLID = ?"
                ,new SerializerWriteBasic(new Datas[] {Datas.TIMESTAMP, Datas.STRING}));

        m_checkdate = new StaticSentence(s
            , "SELECT COUNT(*) FROM shifts WHERE ENDSHIFT IS NULL AND PPLID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadString.INSTANCE);

        m_startbreak =  new PreparedSentence(s
                , "INSERT INTO shift_breaks(ID, SHIFTID, BREAKID, STARTTIME) VALUES (?, ?, ?, ?)"
                , new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.TIMESTAMP}));

        m_endbreak = new StaticSentence(s
                , "UPDATE shift_breaks SET ENDTIME = ? WHERE ENDTIME IS NULL AND SHIFTID = ?"
                ,new SerializerWriteBasic(new Datas[] {Datas.TIMESTAMP, Datas.STRING}));

        m_isonbreak = new StaticSentence(s
//            , "SELECT COUNT(*) FROM shift_breaks WHERE ENDTIME IS NULL AND SHIFTID = ?"
            , "SELECT COUNT(*) FROM shift_breaks WHERE ENDTIME IS NULL"                
            , SerializerWriteString.INSTANCE
            , SerializerReadString.INSTANCE);

        m_shiftid = new StaticSentence(s
            , "SELECT ID FROM shifts WHERE ENDSHIFT IS NULL AND PPLID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadString.INSTANCE);
        
        m_isonleave = new StaticSentence(s
            , "SELECT COUNT(*) FROM leaves WHERE STARTDATE < ? AND ENDDATE > ? AND PPLID = ?"
            , new SerializerWriteBasic(new Datas[] {Datas.TIMESTAMP, Datas.TIMESTAMP, Datas.STRING})
            , SerializerReadString.INSTANCE);

        m_lastcheckin = new StaticSentence(s
            , "SELECT STARTSHIFT FROM shifts WHERE ENDSHIFT IS NULL AND PPLID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadDate.INSTANCE);

        m_lastcheckout = new StaticSentence(s
            , "SELECT MAX(ENDSHIFT) FROM shifts WHERE PPLID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadDate.INSTANCE);

        m_startbreaktime = new StaticSentence(s
            , "SELECT STARTTIME FROM shift_breaks WHERE ENDTIME IS NULL AND SHIFTID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadDate.INSTANCE);

        m_lastbreakid = new StaticSentence(s
            , "SELECT BREAKID FROM shift_breaks WHERE ENDTIME IS NULL AND SHIFTID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadString.INSTANCE);

        m_breakname = new StaticSentence(s
            , "SELECT NAME FROM breaks WHERE ID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadString.INSTANCE);
    }

    /**
     *
     * @return
     */
    public final SentenceList getBreaksList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM breaks ORDER BY NAME"
            , null
            , new SerializerRead() {@Override
 public Object readValues(DataRead dr) throws BasicException {
                return new BreaksInfo(dr.getString(1), dr.getString(2));
            }});
    }

    /**
     *
     * @return
     */
    public final SentenceList getLeavesList() {
        return new StaticSentence(s
            , "SELECT ID, PPLID, NAME, STARTDATE, ENDDATE, NOTES FROM leaves ORDER BY NAME"
            , null
            , new SerializerRead() {@Override
 public Object readValues(DataRead dr) throws BasicException {
                return new LeavesInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4), dr.getString(5), dr.getString(6));
            }});
    }

    /**
     *
     * @return
     * @throws BasicException
     */
    public final List listBreaksVisible()throws BasicException {
        return m_breaksvisible.list();
    }      

    /**
     *
     * @param user
     * @throws BasicException
     */
    public final void CheckIn(String user) throws BasicException {
        Object[] value = new Object[] {UUID.randomUUID().toString(), new Date(), user};
        m_checkin.exec(value);
    }

    /**
     *
     * @param user
     * @throws BasicException
     */
    public final void CheckOut(String user) throws BasicException {
        Object[] value = new Object[] {new Date(), user};
        m_checkout.exec(value);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final boolean IsCheckedIn(String user) throws BasicException {
        String Data = (String) m_checkdate.find(user);
        // "0" rows shows user is not checked in
        if (Data.equals("0")) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param UserID
     * @param BreakID
     * @throws BasicException
     */
    public final void StartBreak(String UserID,  String BreakID) throws BasicException {
        String ShiftID = GetShiftID(UserID);
        Object[] value = new Object[] {UUID.randomUUID().toString(), ShiftID, BreakID, new Date()};
        m_startbreak.exec(value);
    }

    /**
     *
     * @param UserID
     * @throws BasicException
     */
    public final void EndBreak(String UserID) throws BasicException {
        String ShiftID = GetShiftID(UserID);
        Object[] value = new Object[] {new Date(), ShiftID};
        m_endbreak.exec(value);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final boolean IsOnBreak(String user) throws BasicException {
        String ShiftID = GetShiftID(user);
        String Data = (String) m_isonbreak.find(ShiftID);
        // "0" rows shows user is not on break
        if (Data.equals("0")) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final String GetShiftID(String user) throws BasicException {
        return (String) m_shiftid.find(user);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final Date GetLastCheckIn(String user) throws BasicException {
        return (Date) m_lastcheckin.find(user);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final Date GetLastCheckOut(String user) throws BasicException {
        return (Date) m_lastcheckout.find(user);
    }

    /**
     *
     * @param ShiftID
     * @return
     * @throws BasicException
     */
    public final Date GetStartBreakTime(String ShiftID) throws BasicException {
        return (Date) m_startbreaktime.find(ShiftID);
    }

    /**
     *
     * @param ShiftID
     * @return
     * @throws BasicException
     */
    public final String GetLastBreakID(String ShiftID) throws BasicException {
        return (String) m_lastbreakid.find(ShiftID);
    }

    /**
     *
     * @param ShiftID
     * @return
     * @throws BasicException
     */
    public final String GetLastBreakName(String ShiftID) throws BasicException {
        String BreakID = GetLastBreakID(ShiftID);
        return (String) m_breakname.find(BreakID);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final Object [] GetLastBreak(String user) throws BasicException {
        String ShiftID = GetShiftID(user);
        Date StartBreakTime = GetStartBreakTime(ShiftID);
        String BreakName = GetLastBreakName(ShiftID);
        return new Object[] {BreakName, StartBreakTime};
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final boolean IsOnLeave(String user) throws BasicException {
        Object[] value = new Object[] {new Date(), new Date(), user};
        String Data = (String) m_isonleave.find(value);
        // "0" rows shows user is not on leave
        if (Data.equals("0")) {
            return false;
        }
        return true;
    }

    // EmployeeList list
    // Changed ='4' to !='0' --it lists all the users except admin who doesn´t clock in

    /**
     *
     * @return
     */
        public SentenceList getEmployeeList() {
        return new StaticSentence(s
            , new QBFBuilder("SELECT ID, NAME FROM people WHERE ROLE != '0' AND VISIBLE = " + s.DB.TRUE() + " AND ?(QBF_FILTER) ORDER BY NAME", new String[] {"NAME"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING})
            , new SerializerRead() {
            @Override
                    public Object readValues(DataRead dr) throws BasicException {
                        EmployeeInfo c = new EmployeeInfo(dr.getString(1));
                        c.setName(dr.getString(2));
                        return c;
                    }
                });
    }

    /**
     *
     * @param user
     * @throws BasicException
     */
    public void BlockEmployee(String user) throws BasicException {
        boolean isOnBreak = IsOnBreak(user);
        if (isOnBreak) {
            EndBreak(user);
        }
        CheckOut(user);
    }

    TableDefinition getTableBreaks() {
        return tbreaks;
    }

    TableDefinition getTableLeaves() {
        return tleaves;
    }

    /**
     *
     * @param id
     * @return
     * @throws BasicException
     */
    public EmployeeInfoExt loadEmployeeExt(String id) throws BasicException {
        return (EmployeeInfoExt) new PreparedSentence(s
                , "SELECT ID, NAME FROM people WHERE ID = ?"
                , SerializerWriteString.INSTANCE
                , new EmployeeExtRead()).find(id);
    }

    /**
     *
     */
    protected static class EmployeeExtRead implements SerializerRead {

        /**
         *
         * @param dr
         * @return
         * @throws BasicException
         */
        @Override
        public Object readValues(DataRead dr) throws BasicException {
            EmployeeInfoExt c = new EmployeeInfoExt(dr.getString(1));
            c.setName(dr.getString(2));
            return c;
        }
    }
}
