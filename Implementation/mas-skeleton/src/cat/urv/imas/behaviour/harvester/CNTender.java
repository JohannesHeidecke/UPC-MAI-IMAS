/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.harvester;

import java.io.Serializable;

/**
 *
 * @author Ihcrul
 */
public class CNTender implements Serializable {
    
    // How much of the Garbage can be taken at most:
    private int maxAmount;
    
    // How many steps in total to pick up and recycle:
    private int simStepsIncrease;
    
    // How many benefits earned per unit:
    private int benefitsEarnedPerUnit;
    
    // How much the waiting time increases globally 
    // when picking up at least one unit of this garbage:
    private double globalWaitingTimeIncrease;

    public CNTender(int maxAmount, int simStepsNeededTotal, int benefitsEarnedPerUnit, double globalWaitingTimeIncrease) {
        this.maxAmount = maxAmount;
        this.simStepsIncrease = simStepsNeededTotal;
        this.benefitsEarnedPerUnit = benefitsEarnedPerUnit;
        this.globalWaitingTimeIncrease = globalWaitingTimeIncrease;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getSimStepsNeededTotal() {
        return simStepsIncrease;
    }

    public int getBenefitsEarnedPerUnit() {
        return benefitsEarnedPerUnit;
    }

    public double getGlobalWaitingTimeIncrease() {
        return globalWaitingTimeIncrease;
    }
    
    

    
    
    
    
    
    
}
