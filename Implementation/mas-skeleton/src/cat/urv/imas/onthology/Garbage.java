/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.onthology;

import cat.urv.imas.plan.Location;

/**
 *
 * @author Ihcrul
 */
public class Garbage {
    
    private GarbageType type;
    private Location location;
    private long detectedAt;
    private int amount;

    public Garbage(GarbageType type, Location location, long detectedAt, int amount) {
        this.type = type;
        this.location = location;
        this.detectedAt = detectedAt;
        this.amount = amount;
    }

    public GarbageType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public long getDetectedAt() {
        return detectedAt;
    }

    public int getAmount() {
        return amount;
    }
    
    
    
}
