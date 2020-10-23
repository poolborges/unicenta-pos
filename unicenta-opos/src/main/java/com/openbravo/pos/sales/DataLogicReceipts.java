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

package com.openbravo.pos.sales;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SerializerReadBasic;
import com.openbravo.data.loader.SerializerReadClass;
import com.openbravo.data.loader.SerializerWriteBasicExt;
import com.openbravo.data.loader.SerializerWriteString;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.forms.BeanFactoryDataSingle;
import com.openbravo.pos.ticket.TicketInfo;
import java.util.List;

/**
 *
 * @author adrianromero
 */
public class DataLogicReceipts extends BeanFactoryDataSingle {
    
    private Session s;
    
    /** Creates a new instance of DataLogicReceipts */
    public DataLogicReceipts() {
    }
    
    /**
     *
     * @param s
     */
    @Override
    public void init(Session s){
        this.s = s;
    }
     
    /**
     *
     * @param Id
     * @return
     * @throws BasicException
     */
    public final TicketInfo getSharedTicket(String Id) throws BasicException {
        
        if (Id == null) {
            return null; 
        } else {
            Object[]record = (Object[]) new StaticSentence(s
                    , "SELECT CONTENT, LOCKED FROM sharedtickets WHERE ID = ?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {
                        Datas.SERIALIZABLE})).find(Id);
            return record == null ? null : (TicketInfo) record[0];
        }
    }

    /**
     * JG Dec 14 Administrator and Manager Roles always have access to ALL SHAREDtickets
     * @return
     * @throws BasicException
     */
    public final List<SharedTicketInfo> getSharedTicketList() throws BasicException {
        
        return (List<SharedTicketInfo>) new StaticSentence(s
                , "SELECT ID, NAME, CONTENT, APPUSER, PICKUPID, LOCKED FROM sharedtickets ORDER BY ID"
                , null
                , new SerializerReadClass(SharedTicketInfo.class)).list();
    }
    
    /**
     * Return only current APPUSER SHAREDtickets
     * @param appuser
     * @return
     * @throws BasicException
     */
    public final List<SharedTicketInfo> getUserSharedTicketList(String appuser) throws BasicException {
        String sql = "SELECT ID, NAME, CONTENT, APPUSER, PICKUPID, LOCKED FROM sharedtickets WHERE APPUSER =\""+ appuser +"\" ORDER BY ID";
  
            return (List<SharedTicketInfo>) new StaticSentence(s
            , sql
            , null
            , new SerializerReadClass(SharedTicketInfo.class)).list();
    }
    
    /**
     * For Standard View
     * @param id
     * @param ticket
     * @param pickupid
     * @throws BasicException
     */
    public final void insertSharedTicket(final String id, final TicketInfo ticket, int pickupid) throws BasicException {
        
        Object[] values = new Object[] {
            id, 
            ticket.getName(), 
            ticket,
            ticket.getUser().getId(),
            pickupid

        };
        Datas[] datas;
        datas = new Datas[] {
            Datas.STRING, 
            Datas.STRING, 
            Datas.SERIALIZABLE, 
            Datas.STRING,            
            Datas.INT

        };
        new PreparedSentence(s
            , "INSERT INTO sharedtickets ("
                + "ID, "
                + "NAME, "
                + "CONTENT, "
                + "APPUSER, "                    
                + "PICKUPID) "
                + "VALUES (?, ?, ?, ?, ?)"                
            , new SerializerWriteBasicExt(datas, new int[] {0, 1, 2, 3, 4})).exec(values);                
    }
    
    /**
     *
     * @param id
     * @param ticket
     * @param pickupid
     * @throws BasicException
     */
    public final void updateSharedTicket(final String id, final TicketInfo ticket, int pickupid) throws BasicException {
            
        Object[] values = new Object[] {
            id, 
            ticket.getName(), 
            ticket, 
            ticket.getUser().getId(),
            pickupid
        };
        Datas[] datas = new Datas[] {
            Datas.STRING, 
            Datas.STRING, 
            Datas.SERIALIZABLE, 
            Datas.STRING,
            Datas.INT
        };
        new PreparedSentence(s
                , "UPDATE sharedtickets SET "
                + "NAME = ?, "
                + "CONTENT = ?, "
                + "APPUSER = ?, "                        
                + "PICKUPID = ? "
                + "WHERE ID = ?"
                , new SerializerWriteBasicExt(datas, new int[] {1, 2, 3, 4, 0})).exec(values);
//                , new SerializerWriteBasicExt(datas, new int[] {1, 2, 3, 0})).exec(values);                
    }

    /**
     *
     * @param id
     * @param ticket
     * @param pickupid
     * @throws BasicException
     */
    public final void updateRSharedTicket(final String id, final TicketInfo ticket, int pickupid) throws BasicException {
            
        Object[] values = new Object[] {
            id, 
            ticket.getName(), 
            ticket, 
//            ticket.getUser().getId(),
            pickupid
        };
        Datas[] datas = new Datas[] {
            Datas.STRING, 
            Datas.STRING, 
            Datas.SERIALIZABLE, 
//            Datas.STRING,
            Datas.INT
        };
        new PreparedSentence(s
                , "UPDATE sharedtickets SET "
                + "NAME = ?, "
                + "CONTENT = ?, "
//                + "APPUSER = ?, "                        
                + "PICKUPID = ? "
                + "WHERE ID = ?"
//                , new SerializerWriteBasicExt(datas, new int[] {1, 2, 3, 4, 0})).exec(values);
                , new SerializerWriteBasicExt(datas, new int[] {1, 2, 3, 0})).exec(values);                
    }
    
    
     /**
     * In place for multi-purposing like containing data from elsewhere and/or
     * using Place and User for Notifications
     * @param id
     * @param locked
     * @throws BasicException
     */
    public final void lockSharedTicket(final String id, final String locked) throws BasicException {
            
        Object[] values = new Object[] {
            id, 
            locked
        };
        Datas[] datas = new Datas[] {
            Datas.STRING, 
            Datas.STRING
        };
        new PreparedSentence(s
                , "UPDATE sharedtickets SET "
                + "LOCKED = ? "
                + "WHERE ID = ?"
                , new SerializerWriteBasicExt(datas, new int[] {1, 0})).exec(values);
    }

     /**
     * In place for multi-purposing like flushing locks from elsewhere and/or
     * using Place and User for Notifications
     * @param id
     * @param unlocked
     * @throws BasicException
     */
    public final void unlockSharedTicket(final String id, final String unlocked) throws BasicException {
            
        Object[] values = new Object[] {
            id, 
            unlocked
        };
        Datas[] datas = new Datas[] {
            Datas.STRING, 
            Datas.STRING
        };
        new PreparedSentence(s
                , "UPDATE sharedtickets SET "
                + "LOCKED = ? "
                + "WHERE ID = ?"
                , new SerializerWriteBasicExt(datas, new int[] {1, 0})).exec(values);
    }    
    

    /**
     * For Restaurant View
     * @param id
     * @param ticket
     * @param pickupid
     * @throws BasicException
     */
    public final void insertRSharedTicket(final String id, final TicketInfo ticket, int pickupid) throws BasicException {
        
        Object[] values = new Object[] {
            id, 
            ticket.getName(), 
            ticket,
            ticket.getUser(),
            ticket.getPickupId(),
            ticket.getHost(),                            

        };
        Datas[] datas;
        datas = new Datas[] {
            Datas.STRING, 
            Datas.STRING, 
            Datas.SERIALIZABLE, 
            Datas.STRING,            
            Datas.INT

        };
        new PreparedSentence(s
            , "INSERT INTO sharedtickets ("
                + "ID, "
                + "NAME, "
                + "CONTENT, "
                + "APPUSER, "                    
                + "PICKUPID) "
                + "VALUES (?, ?, ?, ?, ?)"                
//                + "VALUES (?, ?, ?, ?)"                                
            , new SerializerWriteBasicExt(datas, new int[] {0, 1, 2, 3, 4})).exec(values);                
//                , new SerializerWriteBasicExt(datas, new int[] {0, 1, 2, 3})).exec(values);                
    }    

    /**
     * 
     * @param id
     * @throws BasicException
     */
    public final void deleteSharedTicket(final String id) throws BasicException {

        new StaticSentence(s
            , "DELETE FROM sharedtickets WHERE ID = ?"
            , SerializerWriteString.INSTANCE).exec(id);      
    }

    /**
     *
     * @param Id
     * @return
     * @throws BasicException
     */
    public final Integer getPickupId(String Id) throws BasicException {
        
        if (Id == null) {
            return null; 
        } else {
            Object[]record = (Object[]) new StaticSentence(s
                    , "SELECT PICKUPID FROM sharedtickets WHERE ID = ?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.INT})).find(Id);
            return record == null ? 0 : (Integer)record[0];
        }
    } 


    public final String getUserId(final String id) throws BasicException {
        Object[] userID = (Object []) new StaticSentence(s
            , "SELECT APPUSER FROM sharedtickets WHERE ID = ?"
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(id);
            if( userID == null ) {
                return null;
            } else {
                return (String) userID[0];
        }
    }
    
    public final String getLockState(final String id, String lockState) throws BasicException {
        Object[] state = (Object[]) new StaticSentence(s
            , "SELECT LOCKED FROM sharedtickets WHERE ID = ?"
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {
                Datas.STRING
            })).find(id);
                return (String) state[0];
    }
}
