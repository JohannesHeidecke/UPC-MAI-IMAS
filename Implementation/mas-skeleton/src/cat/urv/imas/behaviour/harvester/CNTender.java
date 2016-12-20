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

    public CNTender(int maxAmount, int simStepsIncrease, int benefitsEarnedPerUnit, double globalWaitingTimeIncrease) {
        this.maxAmount = maxAmount;
        this.simStepsIncrease = simStepsIncrease;
        this.benefitsEarnedPerUnit = benefitsEarnedPerUnit;
        this.globalWaitingTimeIncrease = globalWaitingTimeIncrease;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getSimStepsIncrease() {
        return simStepsIncrease;
    }

    public int getBenefitsEarnedPerUnit() {
        return benefitsEarnedPerUnit;
    }

    public double getGlobalWaitingTimeIncrease() {
        return globalWaitingTimeIncrease;
    }

    @Override
    public String toString() {
        return "CNTender{" + "maxAmount=" + maxAmount + ", simStepsIncrease=" + simStepsIncrease + ", benefitsEarnedPerUnit=" + benefitsEarnedPerUnit + ", globalWaitingTimeIncrease=" + globalWaitingTimeIncrease + '}';
    }
    
    
    
    

    
    
    
    
    
    
}
