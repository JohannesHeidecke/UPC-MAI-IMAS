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

    public void setRowTo(int rowTo) {
        this.rowTo = rowTo;
    }

    public void setColTo(int colTo) {
        this.colTo = colTo;
    }
    
    
    
    
    
    
    
}
