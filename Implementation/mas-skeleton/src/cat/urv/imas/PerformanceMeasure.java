/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas;

import java.util.List;

/**
 *
 * @author Ihcrul
 */
public class PerformanceMeasure {
    
    private static final double BWEIGHT = 0.5;
    private static final double WWEIGHT = 1;
    
    
    
    public static double getWaitingValue(List<Integer> waitingTimes) {
        double result = 0.0;
        for (Integer waitingTime : waitingTimes) {
            result += Math.pow(waitingTime, 2);
        }
        return result;
    }
    
    public static double getPerformanceMeasure(double benefitsPerStep, double waitingValue) {
        return BWEIGHT * benefitsPerStep - WWEIGHT * waitingValue;
    }

}
