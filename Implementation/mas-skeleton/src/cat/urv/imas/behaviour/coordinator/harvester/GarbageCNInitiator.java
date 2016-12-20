/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.coordinator.harvester;

import cat.urv.imas.onthology.Garbage;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihcrul
 */
public class GarbageCNInitiator extends ContractNetInitiator {

    private Garbage garbage;

    public GarbageCNInitiator(Agent a, ACLMessage cfp) {
        super(a, cfp);
        try {
            this.garbage = (Garbage) cfp.getContentObject();
        } catch (UnreadableException ex) {
            Logger.getLogger(GarbageCNInitiator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    @Override
//    public void onStart() {
//        System.err.println("Announcing: " + garbage);
//    }
//
    @Override
    public void handleAllResponses(Vector responses,
            Vector acceptances) {
        System.err.println("Handle all responses for " + garbage);
        // TODO: loop over responses, select one of the ones with performative PROPOSE
    }

    @Override
    protected void handlePropose(ACLMessage propose, Vector v) {
//        System.out.println("Agent " + propose.getSender().getName() + " proposed " + propose.getContent());
    }

    @Override
    protected void handleRefuse(ACLMessage refuse) {
//        System.out.println("Agent " + refuse.getSender().getName() + " refused");
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        System.out.println("Agent " + inform.getSender().getName() + " successfully performed the requested action");
    }

}
