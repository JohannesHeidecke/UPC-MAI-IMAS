/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.coordinator.harvester;

import cat.urv.imas.PerformanceMeasure;
import cat.urv.imas.agent.HarvesterCoordinatorAgent;
import cat.urv.imas.behaviour.harvester.CNTender;
import cat.urv.imas.onthology.Garbage;
import cat.urv.imas.onthology.Performatives;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
        
        int garbageUnitsToDistribute = garbage.getAmount();

        Map<AID, Integer> chosenHarvestersAmounts = new HashMap<>();

        while (garbageUnitsToDistribute > 0) {

            CNTender tender;
            double bestPerformanceMeasure = Double.NEGATIVE_INFINITY;
            AID chosenHarvester = null;
            int chosenAmount = 0;

            boolean harvesterFound = false;
            for (Object o : responses) {
                ACLMessage msg = (ACLMessage) o;
                if (chosenHarvestersAmounts.keySet().contains(msg.getSender())) {
                    continue;
                }
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                    try {
                        tender = (CNTender) msg.getContentObject();
                        double performanceMeasure = evaluateTender(tender, garbageUnitsToDistribute);

                        if (performanceMeasure > bestPerformanceMeasure) {
                            harvesterFound = true;
                            bestPerformanceMeasure = performanceMeasure;
                            chosenHarvester = msg.getSender();
                            chosenAmount = Math.min(garbageUnitsToDistribute, tender.getMaxAmount());
                            if (chosenAmount <= 0) {
                                throw new RuntimeException("A Harvester was chosen to harvest a zero or negative amount");
                            }
                        }

                    } catch (UnreadableException ex) {
                        Logger.getLogger(GarbageCNInitiator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (!harvesterFound) {
                break;
            }

            garbageUnitsToDistribute -= chosenAmount;
            chosenHarvestersAmounts.put(chosenHarvester, chosenAmount);

        }

        // Remove from the inNegotiation list:
        Iterator<Garbage> it = ((HarvesterCoordinatorAgent) myAgent).getInNegotiationGarbage().iterator();
        while (it.hasNext()) {
            Garbage negoGarb = it.next();
            if (negoGarb.getLocation().equals(garbage.getLocation())) {
                it.remove();
//                ((HarvesterCoordinatorAgent) myAgent).log("Done negotiating: " + garbage.toString());
            }
        }
        // Send update message 
        ACLMessage inform = new ACLMessage(Performatives.INFORM_NEGOTIATION_DONE);
        inform.addReceiver(myAgent.getAID());
        myAgent.send(inform);

        // Store the remaining garbage that could not be assigned:
        if (garbageUnitsToDistribute > 0) {
            ((HarvesterCoordinatorAgent) myAgent).
                    registerRemainingUnassignedGarbage(garbage, garbageUnitsToDistribute);
            ((HarvesterCoordinatorAgent) myAgent).log(garbageUnitsToDistribute + " remaining units after negotiation for " + garbage.toString());
        }

        // Send ACCEPT_PROPOSAL / REJECT_PROPOSAL
        for (Object o : responses) {
            ACLMessage msg = (ACLMessage) o;
            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                ACLMessage reply = msg.createReply();
                if (chosenHarvestersAmounts.keySet().contains(msg.getSender())) {
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    reply.setContent(chosenHarvestersAmounts.get(msg.getSender()).toString());

                    ((HarvesterCoordinatorAgent) myAgent).registerGarbageAssignment(garbage,
                            chosenHarvestersAmounts.get(msg.getSender()));

                } else {
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }
                acceptances.addElement(reply);
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

    private double evaluateTender(CNTender tender, int amount) {
        double benefitsPerStep = Math.min(amount, tender.getMaxAmount());
        benefitsPerStep *= tender.getBenefitsEarnedPerUnit();
        benefitsPerStep /= tender.getSimStepsIncrease();
        return PerformanceMeasure.getPerformanceMeasure(benefitsPerStep, tender.getGlobalWaitingTimeIncrease());
    }

}
