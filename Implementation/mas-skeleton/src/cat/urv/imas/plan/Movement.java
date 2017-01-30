/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.plan;

import java.io.Serializable;

/**
 *
 * @author Ihcrul
 */
public class Movement extends Action implements Serializable {
    
    private int rowFrom, colFrom, rowTo, colTo;

    public Movement(int rowFrom, int colFrom, int rowTo, int colTo) {
        this.rowFrom = rowFrom;
        this.colFrom = colFrom;
        this.rowTo = rowTo;
        this.colTo = colTo;
    }
    
    public Location getFrom() {
        return new Location(rowFrom, colFrom);
    }
    
    public Location getTo() { 
        return new Location(rowTo, colTo);
    }

    public int getRowFrom() {
        return rowFrom;
    }

    public int getColFrom() {
        return colFrom;
    }

    public int getRowTo() {
        return rowTo;
    }

    public int getColTo() {
        return colTo;
    }

    public void setRowFrom(int rowFrom) {
        this.rowFrom = rowFrom;
    }

    public void setColFrom(int colFrom) {
        this.colFrom = colFrom;
    }

    public void setRowTo(int rowTo) {
        this.rowTo = rowTo;
    }

    public void setColTo(int colTo) {
        this.colTo = colTo;
    }

    @Override
    public String toString() {
        return "Movement{" + "rowFrom=" + rowFrom + ", colFrom=" + colFrom + ", rowTo=" + rowTo + ", colTo=" + colTo + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Movement other = (Movement) obj;
        if (this.rowFrom != other.rowFrom) {
            return false;
        }
        if (this.colFrom != other.colFrom) {
            return false;
        }
        if (this.rowTo != other.rowTo) {
            return false;
        }
        if (this.colTo != other.colTo) {
            return false;
        }
        return true;
    }
    
    
    
    
    
    
    
    
    
    
    
}
