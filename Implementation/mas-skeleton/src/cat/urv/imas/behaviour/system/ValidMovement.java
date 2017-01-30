/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.system;

import cat.urv.imas.onthology.InfoAgent;
import cat.urv.imas.plan.Location;

/**
 *
 * @author Ihcrul
 */
public class ValidMovement {

    private Location from;
    private Location to;
    private InfoAgent infoAgent;

    public ValidMovement(Location from, Location to, InfoAgent infoAgent) {
        this.from = from;
        this.to = to;
        if (infoAgent == null) {

            System.err.println(from);
            System.err.println(to);

            throw new RuntimeException("Trying to create valid movement without an InfoAgent");
        }
        this.infoAgent = infoAgent;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public InfoAgent getInfoAgent() {
        return infoAgent;
    }

    @Override
    public String toString() {
        return from.toString() + "\t->\t" + to.toString() + "\t" + infoAgent.getAID().getLocalName();
    }

}
