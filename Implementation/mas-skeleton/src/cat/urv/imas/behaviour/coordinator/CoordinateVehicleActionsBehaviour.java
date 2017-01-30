/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.coordinator;

import cat.urv.imas.agent.CoordinatorAgent;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.onthology.GameSettings;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Action;
import cat.urv.imas.plan.Location;
import cat.urv.imas.plan.Movement;
import cat.urv.imas.plan.PickUp;
import cat.urv.imas.plan.Plan;
import cat.urv.imas.plan.Recycle;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihcrul
 */
public class CoordinateVehicleActionsBehaviour extends CyclicBehaviour {

    private boolean planScoutsReceived = false;
    private boolean planHarvestersReceived = false;

    private CoordinatorAgent coordinator;
    private AID system;
    private AID harvesterCoordinator;
    private AID scoutCoordinator;

    private HashMap<AID, Plan> currentHarvestingPlan;
    private HashMap<AID, Plan> currentScoutingPlan;

    private Cell[][] map;

    public CoordinateVehicleActionsBehaviour(CoordinatorAgent coordinator) {

        this.coordinator = coordinator;
        this.system = coordinator.getSystemAgent();
        this.harvesterCoordinator = coordinator.getHarvesterCoordinator();
        this.scoutCoordinator = coordinator.getScoutCoordinator();

    }

    @Override
    public void onStart() {
        ((CoordinatorAgent) myAgent).log("Started Behaviour: " + this.getClass().toString());
        // Send initial Request to SystemAgent
        ACLMessage msg = new ACLMessage(Performatives.REQUEST_GAME);
        msg.setSender(myAgent.getAID());
        msg.addReceiver(system);
        myAgent.send(msg);
        ((CoordinatorAgent) myAgent).log("Requested Initial Game Settings");
    }

    @Override
    public void action() {

        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            // Process the new message
            switch (msg.getPerformative()) {
                case Performatives.REPLY_GAME:
                    handleNewGameState(msg);
                    break;
                case Performatives.REPLY_PLAN_HARVESTERS:
                    handleNewPlanHarvesting(msg);
                    break;
                case Performatives.REPLY_PLAN_SCOUTS:
                    handleNewPlanScouting(msg);
                    break;
                default:
                    handleNotUnderstood(msg);
                    break;
            }
        } else {
            // No new message, block behaviour until new message arrives:
            block();
            return;
        }

        if (planHarvestersReceived && planScoutsReceived) {

            resolveCollisions(currentHarvestingPlan, currentScoutingPlan);

            HashMap<AID, Plan> nextActions = generateNextActions(currentHarvestingPlan, currentScoutingPlan);

            try {
                sendVehicleMovementsToSystemAgent(nextActions);
            } catch (IOException ex) {
                // TODO
            }

            // Update the state of the behaviour:
            planHarvestersReceived = false;
            planScoutsReceived = false;
        }

    }

    private void handleNewGameState(ACLMessage msg) {

        try {

//            ((CoordinatorAgent) myAgent).log("Request new plans from HC and SC");
            this.map = ((GameSettings) msg.getContentObject()).getMap();
            // Send requests for new plans to HCoord and SCoord,
            // containing the updated map
            ACLMessage request = new ACLMessage(Performatives.REQUEST_PLAN_HARVESTERS);
            request.setSender(myAgent.getAID());
            request.addReceiver(harvesterCoordinator);
            request.setContentObject(map);
            myAgent.send(request);

            request = new ACLMessage(Performatives.REQUEST_PLAN_SCOUTS);
            request.setSender(myAgent.getAID());
            request.addReceiver(scoutCoordinator);
            request.setContentObject(map);
            myAgent.send(request);

        } catch (UnreadableException | IOException ex) {
            ex.printStackTrace();
        }

    }

    private void handleNewPlanHarvesting(ACLMessage msg) {
        try {
//            ((CoordinatorAgent) myAgent).log("Harvester Plans received");
            this.currentHarvestingPlan = (HashMap<AID, Plan>) msg.getContentObject();
            planHarvestersReceived = true;
        } catch (UnreadableException ex) {
            Logger.getLogger(CoordinateVehicleActionsBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleNewPlanScouting(ACLMessage msg) {
        try {
//            ((CoordinatorAgent) myAgent).log("Scout Plans received");
            this.currentScoutingPlan = (HashMap<AID, Plan>) msg.getContentObject();
            planScoutsReceived = true;
        } catch (UnreadableException ex) {
            Logger.getLogger(CoordinateVehicleActionsBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleNotUnderstood(ACLMessage msg) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
        reply.setContent("The Coordinator did not understand this message");
        myAgent.send(reply);
    }

    private void resolveCollisions(HashMap<AID, Plan> harvestingPlan, HashMap<AID, Plan> scoutingPlan) {

        HashMap<AID, Plan> vehiclePlans = new HashMap<>();
        vehiclePlans.putAll(harvestingPlan);
        vehiclePlans.putAll(scoutingPlan);

        for (AID vehicle : vehiclePlans.keySet()) {
            Plan plan = vehiclePlans.get(vehicle);
            Action nextStep = plan.getActions().get(0);
            if (nextStep instanceof Movement) {
                Movement nextMove = (Movement) nextStep;
                Location vehicleLoc = findVehiclePosition(vehicle);
                Location moveFrom = new Location(nextMove.getRowFrom(), nextMove.getColFrom());
                if (!vehicleLoc.equals(moveFrom)) {
                    // TODO: this is a quick fix, look for origin of bug
                    nextMove.setRowFrom(vehicleLoc.getRow());
                    nextMove.setColFrom(vehicleLoc.getCol());
                    nextMove.setRowTo(vehicleLoc.getRow());
                    nextMove.setColTo(vehicleLoc.getCol());
                }
            }
        }

        boolean collisionFound = true;
        while (collisionFound) {
            collisionFound = false;
            for (AID outerVehicle : vehiclePlans.keySet()) {
                Plan outerPlan = vehiclePlans.get(outerVehicle);
                Action outerStep = outerPlan.getActions().get(0);
                Movement outerMove;
                if (outerStep instanceof Movement) {
                    outerMove = (Movement) outerStep;
                } else {
                    Location vehicleLoc = findVehiclePosition(outerVehicle);
                    outerMove = new Movement(vehicleLoc.getRow(), vehicleLoc.getCol(),
                            vehicleLoc.getRow(), vehicleLoc.getCol());
                }
                innerLoop:
                for (AID innerVehicle : vehiclePlans.keySet()) {
                    Plan innerPlan = vehiclePlans.get(innerVehicle);
                    Action innerStep = innerPlan.getActions().get(0);
                    Movement innerMove;
                    if (innerStep instanceof Movement) {
                        innerMove = (Movement) innerStep;
                    } else {
                        Location vehicleLoc = findVehiclePosition(innerVehicle);
                        innerMove = new Movement(vehicleLoc.getRow(), vehicleLoc.getCol(),
                                vehicleLoc.getRow(), vehicleLoc.getCol());
                    }

                    if (outerMove.equals(innerMove)) {
                        // vehicle can't collide with itself
                        continue;
                    }
                    // DETECT COLLISIONS:
                    // moving onto same cell or switching positions:
                    if ((outerMove.getRowTo() == innerMove.getRowTo()
                            && outerMove.getColTo() == innerMove.getColTo())
                            || (outerMove.getRowFrom() == innerMove.getRowTo()
                            && outerMove.getColFrom() == innerMove.getColTo()
                            && innerMove.getRowFrom() == outerMove.getRowTo()
                            && innerMove.getColFrom() == outerMove.getColTo())) {
                        collisionFound = true;

                        int random = ThreadLocalRandom.current().nextInt(0, 2);
                        Movement firstMove, secondMove;
                        if (random == 0) {
                            firstMove = outerMove;
                            if (firstMove.getRowFrom() == firstMove.getRowTo()
                                    && firstMove.getColFrom() == firstMove.getColTo()) {
                                firstMove = innerMove;
                            }
                        } else {
                            firstMove = innerMove;
                        }

                        boolean validRandStep = false;
                        while (!validRandStep) {
                            validRandStep = true;
                            int min = -1;
                            int max = 1;
                            int rowStep = ThreadLocalRandom.current().nextInt(min, max + 1);
                            int colStep = ThreadLocalRandom.current().nextInt(min, max + 1);
                            int rowOrCol = ThreadLocalRandom.current().nextInt(0, 2);
                            int newRow = Math.max(0, firstMove.getRowFrom() + rowStep * rowOrCol);
                            int newCol = Math.max(0, firstMove.getColFrom() + colStep * (1 - rowOrCol));

                            // check if valid randStep:

                            if (!(map[newRow][newCol] instanceof StreetCell)) {
                                validRandStep = false;
                                continue;
                            }

                            firstMove.setRowTo(newRow);
                            firstMove.setColTo(newCol);
                            continue innerLoop;

                        }

                    }

                }
            }
        }

    }

    private HashMap<AID, Plan> generateNextActions(HashMap<AID, Plan> harvestingPlan, HashMap<AID, Plan> scoutingPlan) {
        HashMap<AID, Plan> nextActions = new HashMap<>();
        nextActions.putAll(harvestingPlan);
        nextActions.putAll(scoutingPlan);
        return nextActions;
    }

    private void sendVehicleMovementsToSystemAgent(HashMap<AID, Plan> nextActions) throws IOException {
        // Request SystemAgent to perform the vehicle actions:
        ACLMessage msg = new ACLMessage(Performatives.REQUEST_VEHICLE_ACTIONS);
        msg.setSender(myAgent.getAID());
        msg.addReceiver(system);
        msg.setContentObject(nextActions);
        myAgent.send(msg);

        // Request an updated map from the SystemAgent:
        msg = new ACLMessage(Performatives.REQUEST_GAME);
        msg.setSender(myAgent.getAID());
        msg.addReceiver(system);
        myAgent.send(msg);
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
