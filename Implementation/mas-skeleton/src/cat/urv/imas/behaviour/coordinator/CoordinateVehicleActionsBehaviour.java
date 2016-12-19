/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.coordinator;

import cat.urv.imas.agent.CoordinatorAgent;
import cat.urv.imas.map.Cell;
import cat.urv.imas.onthology.GameSettings;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Plan;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

    public CoordinateVehicleActionsBehaviour(CoordinatorAgent coordinator) {

        this.coordinator = coordinator;
        this.system = coordinator.getSystemAgent();
        this.harvesterCoordinator = coordinator.getHarvesterCoordinator();
        this.scoutCoordinator = coordinator.getScoutCoordinator();

    }

    @Override
    public void onStart() {
        ((CoordinatorAgent) myAgent).log("Started Behaviour: "+this.getClass().toString());
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

            Cell[][] map = ((GameSettings) msg.getContentObject()).getMap();
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
        // TODO
        // TODO (also inform subordinate coordinators of changed plans)
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

}
