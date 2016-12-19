/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.scout;

import cat.urv.imas.agent.ScoutAgent;
import cat.urv.imas.agent.SystemAgent;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.onthology.InfoAgent;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Location;
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
                case Performatives.REQUEST_LOCATION_SCOUT:
                    provideCurrentLocation(msg);
                    break;
                default:
                    //TODO:
                    break;
            }
        } else {
            block();
        }

    }
    
    private void provideCurrentLocation(ACLMessage msg) {
        
        try {

            Cell[][] map = (Cell[][]) msg.getContentObject();
            
            // Find current location on map:
            Location myLocation = null;
            InfoAgent myInfoAgent = ((ScoutAgent) myAgent).getInfoAgent();
            outerloop:
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] instanceof StreetCell) {
                        if (((StreetCell) map[i][j]).isThereAnAgent()) {
                            if (((StreetCell) map[i][j]).getAgent().getAID().equals(myInfoAgent.getAID())) {
                                myLocation = new Location(i, j);
                                break outerloop;
                            }
                        }

                    }
                }
            }
            
            if (myLocation == null) {
                throw new RuntimeException("Scout did not find itself on the map");
            }

            try {
                ACLMessage reply = new ACLMessage(Performatives.REPLY_LOCATION_SCOUT);
                reply.setSender(myAgent.getAID());
                reply.addReceiver(msg.getSender());
                reply.setContentObject(myLocation);
                myAgent.send(reply);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (UnreadableException ex) {
            ex.printStackTrace();
        }
    }
    
    
    
}
