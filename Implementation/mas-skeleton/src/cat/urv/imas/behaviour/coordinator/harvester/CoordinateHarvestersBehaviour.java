/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.coordinator.harvester;

import cat.urv.imas.agent.HarvesterCoordinatorAgent;
import cat.urv.imas.map.BuildingCell;
import cat.urv.imas.map.Cell;
import cat.urv.imas.onthology.Garbage;
import cat.urv.imas.onthology.GarbageType;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Location;
import cat.urv.imas.plan.Plan;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihcrul
 */
public class CoordinateHarvestersBehaviour extends CyclicBehaviour {

    private HarvesterCoordinatorAgent harvCoordinator;
    private AID coordinator;

    private Cell[][] map;

    private List<AID> harvestersWithoutPlanReply;
    private HashMap<AID, Plan> currentPlans;

    private List<Garbage> unassignedGarbage = new ArrayList<>();
    private List<Garbage> inNegotiationGarbage = new ArrayList<>();
    private List<Garbage> assignedGarbage = new ArrayList<>();
    
    private boolean cnDone = false;

    public CoordinateHarvestersBehaviour(HarvesterCoordinatorAgent harvCoordinator) {
        this.harvCoordinator = harvCoordinator;
    }

    @Override
    public void onStart() {
        ((HarvesterCoordinatorAgent) myAgent).log("Started Behaviour: " + this.getClass().toString());
    }

    @Override
    public void action() {
        
        MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(Performatives.REQUEST_PLAN_HARVESTERS),
                MessageTemplate.MatchPerformative(Performatives.REPLY_PLAN_HARVESTER));

        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            switch (msg.getPerformative()) {
                case Performatives.REQUEST_PLAN_HARVESTERS:
                    handlePlansRequest(msg);
                    requestPlansFromHarvesters();
                    break;
                case Performatives.REPLY_PLAN_HARVESTER:
                    handlePlanReply(msg);
                    break;
                default:
                    //TODO
                    break;
            }
        } else {
            block();
        }

    }

    private void handlePlansRequest(ACLMessage msg) {

        try {
            this.map = (Cell[][]) msg.getContentObject();
            this.coordinator = msg.getSender();
        } catch (UnreadableException ex) {
            Logger.getLogger(CoordinateHarvestersBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }

        detectNewUnassignedGarbage();
        orderUnassignedGarbage();
        announceUnassignedGarbage();

    }

    private void handlePlanReply(ACLMessage msg) {
        try {

            Plan plan = (Plan) msg.getContentObject();
            currentPlans.put(msg.getSender(), plan);
            harvestersWithoutPlanReply.remove(msg.getSender());

            if (harvestersWithoutPlanReply.isEmpty()) {
//                ((HarvesterCoordinatorAgent) myAgent).log("Received plans from all Harvesters.");
                sendPlans();
            }

        } catch (UnreadableException ex) {
            ex.printStackTrace();
        }
    }

    private void sendPlans() {

        try {
            // Send all plans to Coordinator:
            ACLMessage reply = new ACLMessage(Performatives.REPLY_PLAN_HARVESTERS);
            reply.setSender(myAgent.getAID());
            reply.addReceiver(coordinator);
            reply.setContentObject(currentPlans);
            myAgent.send(reply);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void detectNewUnassignedGarbage() {
        
        for (int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                if (map[i][j] instanceof BuildingCell) {
                    
                    Map<GarbageType, Integer> detectedGarbageMap = ((BuildingCell) map[i][j]).getGarbage();
                    if(detectedGarbageMap.isEmpty()) {
                        continue;
                    }
                    
                    // TODO: exception if detectedGarbageMap.keySet().size > 1
                    GarbageType gType = (GarbageType) detectedGarbageMap.keySet().toArray(new GarbageType[1])[0];
                    // TODO: get time step from system agent
                    int detectedAt = 100;
                    Garbage detectedGarbage = new Garbage(gType, new Location(i, j), detectedAt, detectedGarbageMap.get(gType));
                    
                    // check if this garbage is new:
                    boolean isNewGarbage = true;
                    
                    for(Garbage garbage : unassignedGarbage) {
                        if (garbage.equals(detectedGarbage)) {
                            isNewGarbage = false;
                        }
                    }
                    for (Garbage garbage : assignedGarbage) {
                        if (garbage.equals(detectedGarbage)) {
                            isNewGarbage = false;
                        }
                    }
                    
                    if (isNewGarbage) {
                        ((HarvesterCoordinatorAgent) myAgent).log("New garbage detected: "+detectedGarbage);
                        unassignedGarbage.add(detectedGarbage);
                    }
                    
                }
            }
        }

    }

    private void announceUnassignedGarbage() {

        for (Garbage garbage : unassignedGarbage) {
            inNegotiationGarbage.add(garbage);
            try {
                ACLMessage cnMessage = new ACLMessage(ACLMessage.CFP);
                cnMessage.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
                cnMessage.setReplyByDate(new Date(System.currentTimeMillis() + 1000));
                cnMessage.setContentObject(garbage);
                cnMessage.setSender(myAgent.getAID());
                for (AID harvester : this.harvCoordinator.getCoordinatedHarvesters()) {
                    cnMessage.addReceiver(harvester);
                }
                myAgent.addBehaviour(new GarbageCNInitiator(myAgent, cnMessage));
            } catch (IOException ex) {
                Logger.getLogger(CoordinateHarvestersBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void orderUnassignedGarbage() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void requestPlansFromHarvesters() {
        List<AID> harvesters = this.harvCoordinator.getCoordinatedHarvesters();
        harvestersWithoutPlanReply = new ArrayList<>();
        harvestersWithoutPlanReply.addAll(harvesters);
        currentPlans = new HashMap<>();

        ACLMessage request = new ACLMessage(Performatives.REQUEST_PLAN_HARVESTER);
        request.setSender(myAgent.getAID());
        for (AID harvester : harvesters) {
            request.addReceiver(harvester);
        }
        try {
            request.setContentObject(this.map);
            myAgent.send(request);
        } catch (IOException ex) {
            Logger.getLogger(CoordinateHarvestersBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
