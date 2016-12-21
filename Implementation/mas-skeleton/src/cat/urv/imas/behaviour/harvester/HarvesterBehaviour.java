/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.harvester;

import cat.urv.imas.agent.HarvesterAgent;
import cat.urv.imas.agent.SystemAgent;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.map.utility.MapUtility;
import cat.urv.imas.onthology.HarvesterInfoAgent;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Location;
import cat.urv.imas.plan.Movement;
import cat.urv.imas.plan.Plan;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihcrul
 */
public class HarvesterBehaviour extends CyclicBehaviour {

    private HarvesterAgent harvester;

    @Override
    public void onStart() {
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));

        harvester = (HarvesterAgent) myAgent;
        myAgent.addBehaviour(new GarbageCNResponder(myAgent, mt));

    }

    @Override
    public void action() {

        MessageTemplate mt = MessageTemplate.MatchPerformative(Performatives.REQUEST_PLAN_HARVESTER);

        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            switch (msg.getPerformative()) {
                case Performatives.REQUEST_PLAN_HARVESTER:
                    provideCurrentPlan(msg);
                    break;
                default:
                    //TODO:
                    break;
            }
        } else {
            block();
        }

    }

    private void provideCurrentPlan(ACLMessage msg) {

        try {

            Cell[][] map = (Cell[][]) msg.getContentObject();
            harvester.setMap(map);

            // Find current location on map:
            Cell myLocation = null;
            HarvesterInfoAgent myInfoAgent = harvester.getInfoAgent();
            outerloop:
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] instanceof StreetCell) {
                        if (((StreetCell) map[i][j]).isThereAnAgent()) {
                            if (((StreetCell) map[i][j]).getAgent().getAID().equals(myInfoAgent.getAID())) {
                                myLocation = map[i][j];
                                break outerloop;
                            }
                        }

                    }
                }
            }

            if (myLocation == null) {
                throw new RuntimeException("Harvester did not find itself on the map");
            }

            Location myLoc = new Location(myLocation.getRow(), myLocation.getCol());
            harvester.setCurrentLocation(myLoc);

            Plan plan = new Plan();

            if (harvester.isIdle()) {
                // TODO: replace random movement with following a scout:
                int min = -1;
                int max = 1;
                int rowStep = ThreadLocalRandom.current().nextInt(min, max + 1);
                int colStep = ThreadLocalRandom.current().nextInt(min, max + 1);
                int rowOrCol = ThreadLocalRandom.current().nextInt(0, 2);
                int newRow = Math.max(0, myLocation.getRow() + rowStep * rowOrCol);
                int newCol = Math.max(0, myLocation.getCol() + colStep * (1 - rowOrCol));
                plan.addAction(new Movement(myLocation.getRow(), myLocation.getCol(), newRow, newCol));
            } else {
                // Harvester is not idle:
                Location target;
                if (harvester.hasPickupLocation()) {
                    target = harvester.getNextPickupLocation();
                } else {
                    target = harvester.getTargetedRecyclingCenter();
                }

                List<Location> pathToTarget = MapUtility.getShortestPath(myLoc, target);
                if (pathToTarget != null) {
                    Location nextStep = pathToTarget.get(0);
                    plan.addAction(new Movement(myLoc.getRow(), myLoc.getCol(),
                            nextStep.getRow(), nextStep.getCol()));
                } else {
                    //check if garbage or recycling center, perform corresponding action
                    // TODO: don't stand still, but do the actions.
                    plan.addAction(new Movement(myLoc.getRow(), myLoc.getCol(),
                            myLoc.getRow(), myLoc.getCol()));
                }

            }

            try {
                ACLMessage reply = new ACLMessage(Performatives.REPLY_PLAN_HARVESTER);
                reply.setSender(myAgent.getAID());
                reply.addReceiver(msg.getSender());
                reply.setContentObject(plan);
                myAgent.send(reply);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (UnreadableException ex) {
            ex.printStackTrace();
        }
    }

}
