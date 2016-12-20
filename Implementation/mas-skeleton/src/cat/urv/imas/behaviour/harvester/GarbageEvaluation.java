/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.harvester;

/**
 *
 * @author Ihcrul
 */
public class GarbageEvaluation {
    
    private int stepsIncr;
    private int price;
    private double waitIncr;

    public GarbageEvaluation(int stepsIncr, int price, double waitIncr) {
        this.stepsIncr = stepsIncr;
        this.price = price;
        this.waitIncr = waitIncr;
    }

    public int getStepsIncr() {
        return stepsIncr;
    }

    public int getPrice() {
        return price;
    }

    public double getWaitIncr() {
        return waitIncr;
    }

    @Override
    public String toString() {
        return "GarbageEvaluation{" + "stepsIncr=" + stepsIncr + ", price=" + price + ", waitIncr=" + waitIncr + '}';
    }

    
    
    
    
    

}
