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
    private boolean isPickedUp = false;

    public GarbageStatistic(Location location, GarbageType type, int amount, int generatedAt) {
        this.location = location;
        this.type = type;
        this.amount = amount;
        this.generatedAt = generatedAt;
    }
    
    public void registerDetection(int timestep) {
        this.detectedAt = detectedAt;
        System.err.println("detected after " + (detectedAt-generatedAt));
    }

    public void registerPickUp(int timestep) {
        pickedUpAt.add(timestep);
        if (pickedUpAt.size() == amount) {
            isPickedUp = true;
        }
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public boolean isPickedUp() {
        return isPickedUp;
    }
    
    public double getPickUpTime() {
        int sum = 0;
        for (int at : pickedUpAt) {
            sum += at - generatedAt;
        }
        return (double) sum / pickedUpAt.size();
    }
    
}
