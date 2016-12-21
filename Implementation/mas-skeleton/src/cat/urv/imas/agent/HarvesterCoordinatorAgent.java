/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.agent;

import static cat.urv.imas.agent.ImasAgent.OWNER;
import cat.urv.imas.behaviour.coordinator.harvester.CoordinateHarvestersBehaviour;
import cat.urv.imas.onthology.Garbage;
import cat.urv.imas.plan.Location;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author johannesheidecke
 */
public class HarvesterCoordinatorAgent extends ImasAgent {

    private List<AID> coordinatedHarvesters = new ArrayList<>();

    private List<Garbage> unassignedGarbage = new ArrayList<>();
    private List<Garbage> inNegotiationGarbage = new ArrayList<>();
    private List<Garbage> assignedGarbage = new ArrayList<>();

    public HarvesterCoordinatorAgent() {
        super(AgentType.HARVESTER_COORDINATOR);
    }

    @Override
    public void setup() {

        /* ** Very Important Line (VIL) ***************************************/
        this.setEnabledO2ACommunication(true, 1);
        /* ********************************************************************/

        // Register the agent to the DF
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType(AgentType.HARVESTER_COORDINATOR.toString());
        sd1.setName(getLocalName());
        sd1.setOwnership(OWNER);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(sd1);
        dfd.setName(getAID());
        try {
            DFService.register(this, dfd);
            log("Registered to the DF");
        } catch (FIPAException e) {
            System.err.println(getLocalName() + " registration with DF unsucceeded. Reason: " + e.getMessage());
            doDelete();
        }

        // Add subscription to keep List of coordinated Harvesters up to date:
        DFAgentDescription dfAgentDescr = new DFAgentDescription();
        ServiceDescription sdHarvester = new ServiceDescription();
        sdHarvester.setType(AgentType.HARVESTER.toString());
        dfAgentDescr.addServices(sdHarvester);
        this.addBehaviour(new SubscriptionInitiator(this,
                DFService.createSubscriptionMessage(this, getDefaultDF(), dfAgentDescr, null)) {
                    protected void handleInform(ACLMessage inform) {
                        try {
                            DFAgentDescription[] dfad = DFService.decodeNotification(inform.getContent());
                            for (int i = 0; i < dfad.length; i++) {
                                ((HarvesterCoordinatorAgent) myAgent).addCoordinatedHarvester(dfad[i].getName());
                                ((HarvesterCoordinatorAgent) myAgent).log("now managing " + dfad[i].getName());
                            }

                        } catch (FIPAException ex) {
                            Logger.getLogger(HarvesterCoordinatorAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

        this.addBehaviour(new CoordinateHarvestersBehaviour(this));

    }

    public List<AID> getCoordinatedHarvesters() {
        return this.coordinatedHarvesters;
    }

    public void addCoordinatedHarvester(AID harvester) {
        this.coordinatedHarvesters.add(harvester);
    }

    public List<Garbage> getUnassignedGarbage() {
        return unassignedGarbage;
    }

    public List<Garbage> getInNegotiationGarbage() {
        return inNegotiationGarbage;
    }

    public List<Garbage> getAssignedGarbage() {
        return assignedGarbage;
    }
    public void addUnassignedGarbage(Garbage garbage) {
        unassignedGarbage.add(garbage);
    }

    public void addInNegotiationGarbage(Garbage garbage) {
        inNegotiationGarbage.add(garbage);
    }

    public void addAssignedGarbage(Garbage garbage) {
        assignedGarbage.add(garbage);
    }
    public void registerGarbageAssignment(Garbage garbage, int amount) {

        Iterator<Garbage> it = inNegotiationGarbage.iterator();
        while (it.hasNext()) {
            Garbage negoGarb = it.next();
            if (negoGarb.getLocation().equals(garbage.getLocation())) {
                it.remove();
            }
        }
        Garbage assignedGarbage = new Garbage(garbage.getType(), 
                garbage.getLocation(), garbage.getDetectedAt(), amount);
        addAssignedGarbage(assignedGarbage);
//        log("Assigned in negotiation: "+assignedGarbage);

    }

    public void registerRemainingUnassignedGarbage(Garbage garbage, int amount) {

        Garbage remainingGarbage = new Garbage(garbage.getType(), 
                garbage.getLocation(), garbage.getDetectedAt(), amount);
        addUnassignedGarbage(remainingGarbage);
//        log("Remaining after negotiation: "+remainingGarbage);
    }

    public void removeFromAssignedGarbage(Location loc, Integer amount) {
        
        Garbage toRemove = null;
        for (Garbage garb : assignedGarbage) {
            if (garb.getLocation().equals(loc) && garb.getAmount() == amount) {
                toRemove = garb;
            }
        }
        if (toRemove != null) {
            assignedGarbage.remove(toRemove);
        } else {
            throw new RuntimeException("Tried to remove garbage from assigned garbages "
                    + "that couldn't be found ");
        }
        
    }

}
