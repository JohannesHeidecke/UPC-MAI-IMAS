/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.system;

import cat.urv.imas.onthology.GarbageType;
import cat.urv.imas.plan.Location;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ihcrul
 */
public class GarbageStatistic {
    
    private Location location;
    private GarbageType type;
    private int amount;
    private int generatedAt;
    private int detectedAt;
    private List<Integer> pickedUpAt = new ArrayList<>();
    
    private boolean isPartiallyPickedUp = false;
    private boolean isCompletelyPickedUp = false;
    private boolean isDetected = false;

    public GarbageStatistic(Location location, GarbageType type, int amount, int generatedAt) {
        this.location = location;
        this.type = type;
        this.amount = amount;
        this.generatedAt = generatedAt;
    }
    
    public void registerDetection(int timestep) {
        this.isDetected = true;
        this.detectedAt = timestep;
    }

    public void registerPickUp(int timestep) {
        pickedUpAt.add(timestep);
        isPartiallyPickedUp = true;
        if (pickedUpAt.size() == amount) {
            isCompletelyPickedUp = true;
        }
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public boolean isPartiallyPickedUp() {
        return isPartiallyPickedUp;
    }
    
    public boolean isCompletelyPickedUp() {
        return isCompletelyPickedUp;
    }
    
    public double getFirstPickUpTime() {
        return this.pickedUpAt.get(0) - this.detectedAt;
    }
    
    public double getDiscoveryTime() {
        return this.detectedAt - this.generatedAt;
    }
    
    public boolean isDetected() {
        return this.isDetected;
    }
    
    public int getAmount() {
        return this.amount;
    }
    
    public int getRemainingAmountToPickUp() {
        return amount - pickedUpAt.size();
    }
    
}
