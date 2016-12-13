/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.coordinator.scout;

import cat.urv.imas.agent.ScoutCoordinatorAgent;
import cat.urv.imas.map.Cell;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Plan;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Ihcrul
 */
public class CoordinateScoutsBehaviour extends CyclicBehaviour {
    
    private ScoutCoordinatorAgent scoutCoordinator;
    private AID coordinator;

    private List<AID> scoutsWithoutPlanReply;
    HashMap<AID, Plan> currentPlans;

    public CoordinateScoutsBehaviour(ScoutCoordinatorAgent scoutCoordinator) {
        this.scoutCoordinator = scoutCoordinator;
    }
    
    @Override
    public void action() {
        
        
        
        MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(Performatives.REQUEST_PLAN_SCOUTS), 
                MessageTemplate.MatchPerformative(Performatives.REPLY_PLAN_SCOUT));

        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            switch (msg.getPerformative()) {
                case Performatives.REQUEST_PLAN_SCOUTS:
                    handlePlansRequest(msg);
                    break;
                case Performatives.REPLY_PLAN_SCOUT:
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
        
//        ((ScoutCoordinatorAgent) myAgent).log("Received plans request");

        try {
            Cell[][] map = (Cell[][]) msg.getContentObject();
            List<AID> scouts = this.scoutCoordinator.getCoordinatedScouts();

            scoutsWithoutPlanReply = new ArrayList<>();
            scoutsWithoutPlanReply.addAll(scouts);
            currentPlans = new HashMap<>();
            coordinator = msg.getSender();

            ACLMessage request = new ACLMessage(Performatives.REQUEST_PLAN_SCOUT);
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
    
    private void handlePlanReply(ACLMessage msg) {
        try {

            Plan plan = (Plan) msg.getContentObject();
            currentPlans.put(msg.getSender(), plan);
            scoutsWithoutPlanReply.remove(msg.getSender());

            if (scoutsWithoutPlanReply.isEmpty()) {
//                ((ScoutCoordinatorAgent) myAgent).log("Received plans from all Scouts.");
                sendPlans();
            }

        } catch (UnreadableException ex) {
            ex.printStackTrace();
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
    
}
