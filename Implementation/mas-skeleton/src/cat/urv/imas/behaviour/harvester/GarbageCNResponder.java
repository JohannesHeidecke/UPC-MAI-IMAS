/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.harvester;

import cat.urv.imas.agent.HarvesterAgent;
import cat.urv.imas.onthology.Garbage;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import java.io.IOException;
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
        
        HarvesterAgent harvester = (HarvesterAgent) myAgent;

        // refuse if capacity full or wrong garbage type:
        if (harvester.getFreeCapacity() == 0 || !harvester.canCarry(garbage.getType())
                || (!(harvester.getCurrentLoadType() == null) 
                    && !garbage.getType().equals(harvester.getCurrentLoadType()))) {
            // Refuse the CFP
            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.REFUSE);
            return reply;
        }
        
        int maxAmount = harvester.getFreeCapacity();
        GarbageEvaluation garbEval = harvester.getGarbageEvalFor(garbage);
//        ((HarvesterAgent) myAgent).log("Proposing: "+garbEval.toString());
        CNTender tender = new CNTender(maxAmount, garbEval.getStepsIncr(),
                garbEval.getPrice(), garbEval.getWaitIncr());
        
        try {
            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContentObject(tender);
            return reply;
        } catch (IOException ex) {
            Logger.getLogger(GarbageCNResponder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
//        ((HarvesterAgent) myAgent).log("I got rejected :(");
    }
    
    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        int amount = Integer.parseInt(accept.getContent());
        ((HarvesterAgent) myAgent).log("Got accpeted for " + amount + " units of " + garbage);
        ((HarvesterAgent) myAgent).addGarbageToHarvest(garbage, amount);
        return null;
    }
    
}
