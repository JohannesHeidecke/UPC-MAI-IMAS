/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.onthology;

import cat.urv.imas.plan.Location;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Ihcrul
 */
public class Garbage implements Serializable {
    
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Garbage other = (Garbage) obj;
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        if (this.amount != other.amount) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Garbage{" + "type=" + type + ", location=" + location + ", detectedAt=" + detectedAt + ", amount=" + amount + '}';
    }
    
    
    
    
    
    
    
}
