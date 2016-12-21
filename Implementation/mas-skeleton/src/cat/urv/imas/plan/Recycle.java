/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.plan;

import cat.urv.imas.onthology.GarbageType;

/**
 *
 * @author Ihcrul
 */
public class Recycle extends Action {
    
    private GarbageType type;
    private int amount;
    private Location centerLoc;

    public Recycle(GarbageType type, int amount, Location centerLoc) {
        this.type = type;
        this.amount = amount;
        this.centerLoc = centerLoc;
    }

    public GarbageType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public Location getCenterLoc() {
        return centerLoc;
    }
    
    
    
}
