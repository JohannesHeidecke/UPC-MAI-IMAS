/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.coordinator.scout;

import cat.urv.imas.agent.AgentType;
import cat.urv.imas.agent.ScoutCoordinatorAgent;
import cat.urv.imas.agent.SystemAgent;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.utility.MapUtility;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Coordinate;
import cat.urv.imas.plan.Movement;
import cat.urv.imas.plan.Plan;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.SubscriptionInitiator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihcrul
 */
public class CoordinateScoutsBehaviour extends CyclicBehaviour {

    private ScoutCoordinatorAgent scoutCoordinator;
    private AID coordinator;

    private List<AID> scoutsWithoutLocationReply;
    private HashMap<AID, Plan> currentPlans;

    private List<Coordinate> tspRoute = null;
    private HashMap<AID, Integer> tspRoutePositions = null;
    private HashMap<AID, Coordinate> currentLocations;

    public CoordinateScoutsBehaviour(ScoutCoordinatorAgent scoutCoordinator) {
        this.scoutCoordinator = scoutCoordinator;
    }

    @Override
    public void onStart() {

        ((ScoutCoordinatorAgent) myAgent).log("Started Behaviour: " + this.getClass().toString());

    }

    @Override
    public void action() {

        MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(Performatives.REQUEST_PLAN_SCOUTS),
                MessageTemplate.MatchPerformative(Performatives.REPLY_LOCATION_SCOUT));

        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            switch (msg.getPerformative()) {
                case Performatives.REQUEST_PLAN_SCOUTS:
                    handlePlansRequest(msg);
                    break;
                case Performatives.REPLY_LOCATION_SCOUT:
                    handleLocationReply(msg);
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

        ((ScoutCoordinatorAgent) myAgent).log("Received plans request");

        try {
            Cell[][] map = (Cell[][]) msg.getContentObject();
            List<AID> scouts = this.scoutCoordinator.getCoordinatedScouts();

            scoutsWithoutLocationReply = new ArrayList<>();
            scoutsWithoutLocationReply.addAll(scouts);
            currentPlans = new HashMap<>();
            coordinator = msg.getSender();

            ACLMessage request = new ACLMessage(Performatives.REQUEST_LOCATION_SCOUT);
            request.setSender(myAgent.getAID());
            for (AID scout : scouts) {
                request.addReceiver(scout);
            }
            request.setContentObject(map);
            myAgent.send(request);
        } catch (IOException | UnreadableException ex) {
            ex.printStackTrace();
        }
    }

    private void handleLocationReply(ACLMessage msg) {

        if (tspRoute == null) {
            currentLocations = new HashMap<>(((ScoutCoordinatorAgent) myAgent).getCoordinatedScouts().size());
            tspRoute = MapUtility.getTravelingSalesmanPath();
            ((ScoutCoordinatorAgent) myAgent).log("Created a TSP route ("+tspRoute.size()+" steps) for ScoutAgents");
        }

        try {

            Coordinate location = (Coordinate) msg.getContentObject();

            currentLocations.put(msg.getSender(), location);
            scoutsWithoutLocationReply.remove(msg.getSender());

            if (scoutsWithoutLocationReply.isEmpty()) {
                ((ScoutCoordinatorAgent) myAgent).log("Received locations from all Scouts.");
                createPlans();
                resolveCollisions();
                sendPlans();
            }

        } catch (UnreadableException ex) {
            ex.printStackTrace();
        }
    }

    private void resolveCollisions() {
        // TODO
    }

    private void sendPlans() {

        try {
            // Send all plans to Coordinator:
            ACLMessage reply = new ACLMessage(Performatives.REPLY_PLAN_SCOUTS);
            reply.setSender(myAgent.getAID());
            reply.addReceiver(coordinator);
            reply.setContentObject(currentPlans);
            myAgent.send(reply);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void createPlans() {

        if (tspRoutePositions == null) {
            tspRoutePositions = new HashMap<>(((ScoutCoordinatorAgent) myAgent).getCoordinatedScouts().size());
            initRoutePositions();
        }

        // move tspRoutePositions one forward:
        for (AID scout : tspRoutePositions.keySet()) {
            Integer position = tspRoutePositions.get(scout);
            Integer newPosition = (position.intValue() + 1) % tspRoute.size();
            tspRoutePositions.put(scout, newPosition);
            Coordinate from = currentLocations.get(scout);
            Coordinate to = tspRoute.get(newPosition);
            Plan plan = new Plan();
            plan.addAction(new Movement(from.getRow(), from.getCol(), to.getRow(), to.getCol()));
            this.currentPlans.put(scout, plan);

        }

    }

    private void initRoutePositions() {

        for (AID scout : currentLocations.keySet()) {
            Coordinate location = currentLocations.get(scout);
            int counter = 0;
            for (Coordinate step : tspRoute) {
                if (step.equals(location)) {
                    tspRoutePositions.put(scout, counter);
                    break;
                }
                counter++;
            }
        }

    }

}
