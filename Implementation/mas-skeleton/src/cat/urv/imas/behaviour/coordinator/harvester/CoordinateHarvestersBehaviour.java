/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.coordinator.harvester;

import cat.urv.imas.agent.HarvesterCoordinatorAgent;
import cat.urv.imas.agent.SystemAgent;
import cat.urv.imas.map.BuildingCell;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.onthology.Garbage;
import cat.urv.imas.onthology.GarbageType;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Action;
import cat.urv.imas.plan.Location;
import cat.urv.imas.plan.Movement;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihcrul
 */
public class CoordinateHarvestersBehaviour extends CyclicBehaviour {

    private final HarvesterCoordinatorAgent harvCoordinator;
    private AID coordinator;

    private Cell[][] map;

    private List<AID> harvestersWithoutPlanReply = new ArrayList<>();
    private HashMap<AID, Plan> currentPlans;
    
    private boolean allNegotiationsDone = false;
    private boolean allPlansReceived = false;

    public CoordinateHarvestersBehaviour(HarvesterCoordinatorAgent harvCoordinator) {
        this.harvCoordinator = harvCoordinator;
    }

    @Override
    public void onStart() {
        ((HarvesterCoordinatorAgent) myAgent).log("Started Behaviour: " + this.getClass().toString());
    }

    @Override
    public void action() {

        MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(Performatives.INFORM_NEGOTIATION_DONE), 
                MessageTemplate.or(MessageTemplate.MatchPerformative(Performatives.INFORM_PICKUP),
                MessageTemplate.or(MessageTemplate.MatchPerformative(Performatives.REQUEST_PLAN_HARVESTERS),
                        MessageTemplate.MatchPerformative(Performatives.REPLY_PLAN_HARVESTER))));

        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            switch (msg.getPerformative()) {
                case Performatives.REQUEST_PLAN_HARVESTERS:
                    handlePlansRequest(msg);
                    break;
                case Performatives.INFORM_NEGOTIATION_DONE:
                    handleNegotiationDone();
                    break;
                case Performatives.REPLY_PLAN_HARVESTER:
                    handlePlanReply(msg);
                    break;
                case Performatives.INFORM_PICKUP:
                    handlePickUpComplete(msg);
                    break;
                default:
                    //TODO
                    break;
            }
        } else {
            block();
        }
        
        if (allNegotiationsDone) {
            requestPlansFromHarvesters();
            allNegotiationsDone = false;
        }

        if (allPlansReceived) {
            resolveCollisions();
            sendPlans();
            allPlansReceived = false;
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
                allPlansReceived = true;
            }

        } catch (UnreadableException ex) {
            ex.printStackTrace();
        }
    }

    private void sendPlans() {

        // TODO: remove next line?
        ((HarvesterCoordinatorAgent) myAgent).logManagedGarbageReport();

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
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] instanceof BuildingCell) {

                    Map<GarbageType, Integer> detectedGarbageMap = ((BuildingCell) map[i][j]).getGarbage();
                    if (detectedGarbageMap.isEmpty()) {
                        continue;
                    }

                    GarbageType gType = (GarbageType) detectedGarbageMap.keySet().toArray(new GarbageType[1])[0];

                    int detectedAt = SystemAgent.getCurrentSimulationStep();
                    Garbage detectedGarbage = new Garbage(gType, new Location(i, j), detectedAt, detectedGarbageMap.get(gType));

                    // check if this garbage is new:
                    boolean isNewGarbage = true;

                    for (Garbage garbage : harvCoordinator.getUnassignedGarbage()) {
                        if (garbage.getLocation().equals(detectedGarbage.getLocation())) {
                            isNewGarbage = false;
                        }
                    }
                    for (Garbage garbage : harvCoordinator.getInNegotiationGarbage()) {
                        if (garbage.getLocation().equals(detectedGarbage.getLocation())) {
                            isNewGarbage = false;
                        }
                    }
                    for (Garbage garbage : harvCoordinator.getAssignedGarbage()) {
                        if (garbage.getLocation().equals(detectedGarbage.getLocation())) {
                            isNewGarbage = false;
                        }
                    }

                    if (isNewGarbage) {
                        ((HarvesterCoordinatorAgent) myAgent).log("New garbage detected: " + detectedGarbage);
                        harvCoordinator.addUnassignedGarbage(detectedGarbage);
                    }

                }
            }
        }

    }

    private void announceUnassignedGarbage() {
        
        if (harvCoordinator.getUnassignedGarbage().isEmpty()) {
            allNegotiationsDone = true;
            return;
        }
        
//        harvCoordinator.log("Negotiating " + harvCoordinator.getUnassignedGarbage().size() + " occurences of garbage this turn");

        Iterator<Garbage> iterator = harvCoordinator.getUnassignedGarbage().iterator();
        // TODO: this counter limits to announcing 1 garbage per turn
        // this fixed collisions when assigning several garbages at once
        // might be more efficient to remove the counter and resolve collisions
        int counter = 0;
        while (iterator.hasNext() && counter < 1) {
            counter++;
            Garbage garbage = iterator.next();
            harvCoordinator.addInNegotiationGarbage(garbage);
            iterator.remove();
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
        
//        harvCoordinator.log("Requesting plans from all Harvesters");
        
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

    private void resolveCollisions() {

        // TODO: base this on PerformanceMeasure, not random.
        // Harvesters that collide with other harvesters do a random movement in another direction:
        List<AID> plannedHarvesters = new ArrayList<>(currentPlans.keySet());

        boolean collisionDetected = true;
        while (collisionDetected) {
            collisionDetected = false;

            outerFor:
            for (int i = 0; i < plannedHarvesters.size(); i++) {
                Action iStep = currentPlans.get(plannedHarvesters.get(i)).getActions().get(0);
                if (!(iStep instanceof Movement)) {
                    continue;
                }
                for (int j = i + 1; j < plannedHarvesters.size(); j++) {
                    Action jStep = currentPlans.get(plannedHarvesters.get(j)).getActions().get(0);
                    if (!(jStep instanceof Movement)) {
                        continue;
                    }
                    Movement iMove = (Movement) iStep;
                    Movement jMove = (Movement) jStep;

                    // check for collisions
                    if (iMove.getTo().equals(jMove.getTo())) {
                        collisionDetected = true;
                    }
                    if (iMove.getFrom().equals(jMove.getTo()) && iMove.getTo().equals(jMove.getFrom())) {
                        collisionDetected = true;
                    }

                    // resolve found collision
                    if (collisionDetected) {
                        int rowStep = ThreadLocalRandom.current().nextInt(-1, 1 + 1);
                        int colStep = ThreadLocalRandom.current().nextInt(-1, 1 + 1);
                        int rowOrCol = ThreadLocalRandom.current().nextInt(0, 2);
                        int newRow = Math.max(0, jMove.getFrom().getRow() + rowStep * rowOrCol);
                        int newCol = Math.max(0, jMove.getFrom().getCol() + colStep * (1 - rowOrCol));
                        jMove.setRowTo(newRow);
                        jMove.setColTo(newCol);
                        break outerFor;
                    }

                }
            }
        }

    }

    private void handlePickUpComplete(ACLMessage msg) {
        try {
            Object[] contentObj = (Object[]) msg.getContentObject();
            Location loc = (Location) contentObj[0];
            Integer amount = (Integer) contentObj[1];
            harvCoordinator.removeFromAssignedGarbage(loc, amount);
        } catch (UnreadableException ex) {
            Logger.getLogger(CoordinateHarvestersBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleNegotiationDone() {
        if (harvCoordinator.getInNegotiationGarbage().isEmpty()) {
//            harvCoordinator.log("Negotiated all unassigned garbage.");
            allNegotiationsDone = true;
        }
    }
    
    private Location findVehiclePosition(AID vehicleAID) {

        int row = -1, col = -1;
        outerLoop:
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                Cell cell = map[i][j];
                if (cell instanceof StreetCell) {
                    StreetCell sCell = (StreetCell) cell;
                    if (sCell.isThereAnAgent()) {
                        if (vehicleAID.equals(sCell.getAgent().getAID())) {
                            row = i;
                            col = j;
                            break outerLoop;
                        }
                    }
                }
            }
        }

        if (row == -1 || col == -1) {
            throw new RuntimeException("Vehicle not found on map: " + vehicleAID.toString());
        }

        return new Location(row, col);

    }

}
