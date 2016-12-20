/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.harvester;

import cat.urv.imas.agent.HarvesterAgent;
import cat.urv.imas.onthology.Garbage;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihcrul
 */
public class GarbageCNResponder extends ContractNetResponder {

    private Garbage garbage;

    public GarbageCNResponder(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
        try {
            this.garbage = (Garbage) cfp.getContentObject();
        } catch (UnreadableException ex) {
            Logger.getLogger(GarbageCNResponder.class.getName()).log(Level.SEVERE, null, ex);
        }
//        System.out.println("Agent " + ": CFP received from " + cfp.getSender().getName() + " for " + this.garbage);

        HarvesterAgent harvester = (HarvesterAgent) myAgent;
        int maxAmount = harvester.getFreeCapacity();
        GarbageEvaluation garbEval = harvester.getGarbageEvalFor(garbage);
//        CNTender tender = new CNTender(maxAmount, garbEval.getTotalSteps(), 
//                garbEval.getBenefits(), garbEval.getWaitIncr());


        return null;
    }
}
