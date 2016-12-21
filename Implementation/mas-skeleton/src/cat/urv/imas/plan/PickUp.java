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
public class PickUp extends Action {
    
    private Location pickUpLoc;
    private GarbageType type;

    public PickUp(Location pickUpLoc, GarbageType type) {
        this.pickUpLoc = pickUpLoc;
        this.type = type;
    }

    public Location getPickUpLoc() {
        return pickUpLoc;
    }

    public GarbageType getType() {
        return type;
    }
    
}
