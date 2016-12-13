/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.scout;

import cat.urv.imas.agent.ScoutAgent;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.onthology.InfoAgent;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Movement;
import cat.urv.imas.plan.Plan;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Ihcrul
 */
public class ScoutBehaviour extends CyclicBehaviour {
    
    @Override
    public void action() {

        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            switch (msg.getPerformative()) {
                case Performatives.REQUEST_PLAN_SCOUT:
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

            // TODO: more intelligent behavior here:
            // Find current location on map:
            Cell myLocation = null;
            InfoAgent myInfoAgent = ((ScoutAgent) myAgent).getInfoAgent();
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
                throw new RuntimeException("Scout did not find itself on the map");
            }

            // TODO: remove random plan
            Plan randomPlan = new Plan();
            int min = -1;
            int max = 1;
            int rowStep = ThreadLocalRandom.current().nextInt(min, max + 1);
            int colStep = ThreadLocalRandom.current().nextInt(min, max + 1);
            int newRow = Math.max(0, myLocation.getRow() + rowStep);
            int newCol = Math.max(0, myLocation.getCol() + colStep);
            randomPlan.addAction(new Movement(myLocation.getRow(), myLocation.getCol(), newRow, newCol));

            try {
                ACLMessage reply = new ACLMessage(Performatives.REPLY_PLAN_SCOUT);
                reply.setSender(myAgent.getAID());
                reply.addReceiver(msg.getSender());
                reply.setContentObject(randomPlan);
                myAgent.send(reply);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (UnreadableException ex) {
            ex.printStackTrace();
        }
    }
    
    
    
}
