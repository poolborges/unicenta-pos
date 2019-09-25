/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.mant;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author jack
 */
@Entity
@Table(name = "places", catalog = "unicentaopos", schema = "")
@NamedQueries({
    @NamedQuery(name = "Places.findAll", query = "SELECT p FROM Places p"),
    @NamedQuery(name = "Places.findById", query = "SELECT p FROM Places p WHERE p.id = :id"),
    @NamedQuery(name = "Places.findByName", query = "SELECT p FROM Places p WHERE p.name = :name"),
    @NamedQuery(name = "Places.findByX", query = "SELECT p FROM Places p WHERE p.x = :x"),
    @NamedQuery(name = "Places.findByY", query = "SELECT p FROM Places p WHERE p.y = :y"),
    @NamedQuery(name = "Places.findByFloor", query = "SELECT p FROM Places p WHERE p.floor = :floor"),
    @NamedQuery(name = "Places.findByCustomer", query = "SELECT p FROM Places p WHERE p.customer = :customer"),
    @NamedQuery(name = "Places.findByWaiter", query = "SELECT p FROM Places p WHERE p.waiter = :waiter"),
    @NamedQuery(name = "Places.findByTicketid", query = "SELECT p FROM Places p WHERE p.ticketid = :ticketid"),
    @NamedQuery(name = "Places.findByTablemoved", query = "SELECT p FROM Places p WHERE p.tablemoved = :tablemoved")})
public class Places implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    @Basic(optional = false)
    @Column(name = "NAME")
    private String name;
    @Column(name = "SEATS")
    private String seats;    
    @Basic(optional = false)
    @Column(name = "X")
    private int x;
    @Basic(optional = false)
    @Column(name = "Y")
    private int y;
    @Basic(optional = false)
    @Column(name = "FLOOR")
    private String floor;
    @Column(name = "CUSTOMER")
    private String customer;
    @Column(name = "WAITER")
    private String waiter;
    @Column(name = "TICKETID")
    private String ticketid;
    @Basic(optional = false)
    @Column(name = "TABLEMOVED")
    private short tablemoved;

    public Places() {
    }

    public Places(String id) {
        this.id = id;
    }

    public Places(String id, String name, String seats, int x, int y, String floor, short tablemoved) {
        this.id = id;
        this.name = name;
        this.seats = seats;        
        this.x = x;
        this.y = y;
        this.floor = floor;
        this.tablemoved = tablemoved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        String oldId = this.id;
        this.id = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        changeSupport.firePropertyChange("name", oldName, name);
    }

    public String getSeats() {
        return seats;
    }
    public void setSeats(String seats) {
        String oldSeats = this.seats;
        this.seats = seats;
        changeSupport.firePropertyChange("seats", oldSeats, seats);
    }    

    public int getX() {
        return x;
    }

    public void setX(int x) {
        int oldX = this.x;
        this.x = x;
        changeSupport.firePropertyChange("x", oldX, x);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        int oldY = this.y;
        this.y = y;
        changeSupport.firePropertyChange("y", oldY, y);
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        String oldFloor = this.floor;
        this.floor = floor;
        changeSupport.firePropertyChange("floor", oldFloor, floor);
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        String oldCustomer = this.customer;
        this.customer = customer;
        changeSupport.firePropertyChange("customer", oldCustomer, customer);
    }

    public String getWaiter() {
        return waiter;
    }

    public void setWaiter(String waiter) {
        String oldWaiter = this.waiter;
        this.waiter = waiter;
        changeSupport.firePropertyChange("waiter", oldWaiter, waiter);
    }

    public String getTicketid() {
        return ticketid;
    }

    public void setTicketid(String ticketid) {
        String oldTicketid = this.ticketid;
        this.ticketid = ticketid;
        changeSupport.firePropertyChange("ticketid", oldTicketid, ticketid);
    }

    public short getTablemoved() {
        return tablemoved;
    }

    public void setTablemoved(short tablemoved) {
        short oldTablemoved = this.tablemoved;
        this.tablemoved = tablemoved;
        changeSupport.firePropertyChange("tablemoved", oldTablemoved, tablemoved);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Places)) {
            return false;
        }
        Places other = (Places) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.openbravo.pos.mant.Places[ id=" + id + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
