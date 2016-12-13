/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.system;

import cat.urv.imas.onthology.InfoAgent;
import cat.urv.imas.plan.Coordinate;

/**
 *
 * @author Ihcrul
 */
public class ValidMovement {
    
    private Coordinate from;
    private Coordinate to;
    private InfoAgent infoAgent;

    public ValidMovement(Coordinate from, Coordinate to, InfoAgent infoAgent) {
        this.from = from;
        this.to = to;
        this.infoAgent = infoAgent;
    }

    public Coordinate getFrom() {
        return from;
    }

    public Coordinate getTo() {
        return to;
    }

    public InfoAgent getInfoAgent() {
        return infoAgent;
    }
    
    
    
}
