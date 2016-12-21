/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.coordinator.scout;

import cat.urv.imas.agent.ScoutCoordinatorAgent;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.utility.MapUtility;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Location;
import cat.urv.imas.plan.Movement;
import cat.urv.imas.plan.Plan;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Ihcrul
 */
public class CoordinateScoutsBehaviour extends CyclicBehaviour {

    private ScoutCoordinatorAgent scoutCoordinator;
    private AID coordinator;

    private List<AID> scoutsWithoutLocationReply;
    private HashMap<AID, Plan> currentPlans;

    private List<Location> tspRoute = null;
    private HashMap<AID, Integer> tspRoutePositions = null;
    private HashMap<AID, Integer> lastKnownTspRoutePositions = null;
    private HashMap<AID, Location> currentLocations;

    private Cell[][] map;

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

//        ((ScoutCoordinatorAgent) myAgent).log("Received plans request");
        try {
            this.map = (Cell[][]) msg.getContentObject();
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
            ((ScoutCoordinatorAgent) myAgent).log("Created a TSP route (" + tspRoute.size() + " steps) for ScoutAgents");
        }

        try {

            Location location = (Location) msg.getContentObject();

            currentLocations.put(msg.getSender(), location);
            scoutsWithoutLocationReply.remove(msg.getSender());

            if (scoutsWithoutLocationReply.isEmpty()) {
//                ((ScoutCoordinatorAgent) myAgent).log("Received locations from all Scouts.");
                createPlans();
                resolveCollisions();
                sendPlans();
            }

        } catch (UnreadableException ex) {
            ex.printStackTrace();
        }
    }

    private void resolveCollisions() {
        // Scouts that collide with other scouts do a random movement in another direction:
        List<AID> plannedScouts = new ArrayList<>(currentPlans.keySet());

        boolean collisionDetected = true;
        while (collisionDetected) {
            collisionDetected = false;

            outerFor:
            for (int i = 0; i < plannedScouts.size(); i++) {
                for (int j = i + 1; j < plannedScouts.size(); j++) {

                    Movement iMove = (Movement) currentPlans.get(plannedScouts.get(i)).getActions().get(0);
                    Movement jMove = (Movement) currentPlans.get(plannedScouts.get(j)).getActions().get(0);

                    // check for collisions
                    if (iMove.getTo().equals(jMove.getTo())) {
                        collisionDetected = true;
                    }
                    if (iMove.getFrom().equals(jMove.getTo()) && iMove.getTo().equals(jMove.getFrom())) {
                        collisionDetected = true;
                    }

                    // try to resolve found collision
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
            lastKnownTspRoutePositions = new HashMap<>(((ScoutCoordinatorAgent) myAgent).getCoordinatedScouts().size());
        }

        setScoutRoutePositions();

        enhanceEquidistance();

        // move tspRoutePositions one forward:
        for (AID scout : tspRoutePositions.keySet()) {
            Integer position = tspRoutePositions.get(scout);
            Integer newPosition = (position + 1) % tspRoute.size();
            Location from = currentLocations.get(scout);
            Location to = tspRoute.get(newPosition);
            Plan plan = new Plan();
            plan.addAction(new Movement(from.getRow(), from.getCol(), to.getRow(), to.getCol()));
            this.currentPlans.put(scout, plan);

            // save current pos for when scouts get lost (through collisions)
            lastKnownTspRoutePositions.put(scout, tspRoutePositions.get(scout));
            // increase tspRoutePos for next simulation step:
            tspRoutePositions.put(scout, newPosition);

        }

    }

    private void setScoutRoutePositions() {

        // remove scouts from tspRoutePositions who are not on their supposed positions:
        Iterator<AID> iterator = tspRoutePositions.keySet().iterator();
        while (iterator.hasNext()) {
            AID scout = iterator.next();
            Location scoutLocation = currentLocations.get(scout);
            Location supposedScoutLocation = tspRoute.get(tspRoutePositions.get(scout));
            if (!scoutLocation.equals(supposedScoutLocation)) {
                iterator.remove();
            }
        }

        List<AID> scouts = this.scoutCoordinator.getCoordinatedScouts();
        for (AID scout : scouts) {
            if (!tspRoutePositions.keySet().contains(scout)) {
                int closestPosition = 0;
                int closestDistance;
                Location scoutPos = currentLocations.get(scout);
                
                // if there is a former tspPosition known: try to return there
                if (lastKnownTspRoutePositions.keySet().contains(scout)) {
                    closestPosition = lastKnownTspRoutePositions.get(scout);
                    closestDistance = MapUtility.getShortestDistance(scoutPos, tspRoute.get(closestPosition));
                } else {
                    // scout does not have a current or former tsp position
                    closestDistance = Integer.MAX_VALUE;
                    // find the nearest position of the tspRoute and go there
                    for (int i = 0; i < tspRoute.size(); i++) {
                        Location routePos = tspRoute.get(i);
                        int distance = MapUtility.getShortestDistance(scoutPos, routePos);
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestPosition = i;
                        }
                    }
                }

                if (closestDistance == 0) {
//                    ((ScoutCoordinatorAgent) myAgent).log(scout.getName() + " now on pos "+closestPosition);
                    tspRoutePositions.put(scout, closestPosition);
                } else {
                    // if Scout is not on tspRoute: move 1 field towards nearest tspRoute cell
                    Location to = tspRoute.get(closestPosition);
                    Plan plan = new Plan();
                    List<Location> path = MapUtility.getShortestPath(scoutPos, to);
                    Location nextStep = path.get(0);
                    plan.addAction(new Movement(scoutPos.getRow(), scoutPos.getCol(), nextStep.getRow(), nextStep.getCol()));
                    this.currentPlans.put(scout, plan);
                }

            }
        }

    }

    private void enhanceEquidistance() {
        int preferredDistance = tspRoute.size() / currentLocations.size();
        int leftDistanceErrorSum = 0;
        for (AID scout : tspRoutePositions.keySet()) {
            AID scoutBefore, scoutAfter;
            int scoutPos = tspRoutePositions.get(scout);
            int smallestLeftDiff = Integer.MAX_VALUE;
            int smallestRightDiff = Integer.MAX_VALUE;
            for (AID otherScout : tspRoutePositions.keySet()) {
                if (scout.equals(otherScout)) {
                    continue;
                }
                int otherScoutPos = tspRoutePositions.get(otherScout);

                int leftDiff = scoutPos - otherScoutPos;
                if (leftDiff < 0) {
                    leftDiff += tspRoute.size();
                }

                int rightDiff = otherScoutPos - scoutPos;
                if (rightDiff < 0) {
                    rightDiff += tspRoute.size();
                }

                if (leftDiff < smallestLeftDiff) {
                    smallestLeftDiff = leftDiff;
                }
                if (rightDiff < smallestRightDiff) {
                    smallestRightDiff = rightDiff;
                }
            }

            // positive if distance is too large:
            int leftDev = smallestLeftDiff - preferredDistance;
            int rightDev = smallestRightDiff - preferredDistance;

            // if (the distance to the left is too large
            // and (the distance to the right is too small
            // or the distance to the left is much bigger than to the right))
            // let scout wait at current location for one step
            if ((leftDev > 0
                    && rightDev < 0) || smallestLeftDiff > 2 * smallestRightDiff) {
                // let scout wait on current position on the path for one step:
                int currentPos = tspRoutePositions.get(scout);
                tspRoutePositions.put(scout, currentPos - 1);
            }

            leftDistanceErrorSum += Math.abs(smallestLeftDiff - preferredDistance);
        }

//        ((ScoutCoordinatorAgent) myAgent).log("Average equidistance deviation: " + (double) leftDistanceErrorSum / currentLocations.size());
    }

}
