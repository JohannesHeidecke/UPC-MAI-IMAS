/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.coordinator.harvester;

import cat.urv.imas.PerformanceMeasure;
import cat.urv.imas.behaviour.harvester.CNTender;
import cat.urv.imas.onthology.Garbage;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public void handleAllResponses(Vector responses,
            Vector acceptances) {
        // loop over responses, select one of the ones with performative PROPOSE

        CNTender tender;
        for (Object o : responses) {
            ACLMessage msg = (ACLMessage) o;
            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                try {
                    tender = (CNTender) msg.getContentObject();
                    System.err.println(tender + " ::: "+evaluateTender(tender));
                    
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    myAgent.send(reply);
                    
                } catch (UnreadableException ex) {
                    Logger.getLogger(GarbageCNInitiator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        

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

    private double evaluateTender(CNTender tender) {
        double benefitsPerStep = Math.min(garbage.getAmount(), tender.getMaxAmount());
        benefitsPerStep *= tender.getBenefitsEarnedPerUnit();
        benefitsPerStep /= tender.getSimStepsIncrease();
        return PerformanceMeasure.getPerformanceMeasure(benefitsPerStep, tender.getGlobalWaitingTimeIncrease());
    }
    
}
