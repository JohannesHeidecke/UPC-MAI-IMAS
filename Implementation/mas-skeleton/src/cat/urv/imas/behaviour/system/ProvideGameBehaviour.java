/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.system;

import cat.urv.imas.agent.SystemAgent;
import cat.urv.imas.onthology.GameSettings;
import cat.urv.imas.onthology.MessageContent;
import cat.urv.imas.onthology.Performatives;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihcrul
 */
public class ProvideGameBehaviour extends CyclicBehaviour {
    
    private AID coordinator;
    private GameSettings game;
    
    
    public ProvideGameBehaviour(AID authorizedCoordinator, GameSettings game) {
        this.coordinator = authorizedCoordinator;
        this.game = game;
    }

    @Override
    public void action() {
        
        MessageTemplate mt = MessageTemplate.MatchPerformative(Performatives.REQUEST_GAME);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            if (msg.getSender().equals(coordinator)) {
                try {
                    //TODO: remove sleep
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ProvideGameBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                    ((SystemAgent) myAgent).log("Send game settings to Coordinator");
                    ACLMessage reply = new ACLMessage(Performatives.REPLY_GAME);
                    reply.setSender(myAgent.getAID());
                    reply.addReceiver(coordinator);
                    reply.setContentObject(this.game);
                    myAgent.send(reply);
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                  ((SystemAgent) myAgent).log(" An unauthorized agent requested the game settings");            
            }
        } else {
            block();
        }
        
        
    }
    
}
