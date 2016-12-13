/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.coordinator.harvester;

import cat.urv.imas.agent.HarvesterCoordinatorAgent;
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihcrul
 */
public class CoordinateHarvestersBehaviour extends CyclicBehaviour {

    private HarvesterCoordinatorAgent harvCoordinator;
    private AID coordinator;

    private List<AID> harvestersWithoutPlanReply;
    HashMap<AID, Plan> currentPlans;

    public CoordinateHarvestersBehaviour(HarvesterCoordinatorAgent harvCoordinator) {
        this.harvCoordinator = harvCoordinator;
    }

    @Override
    public void action() {
        
        
        
        MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(Performatives.REQUEST_PLAN_HARVESTERS), 
                MessageTemplate.MatchPerformative(Performatives.REPLY_PLAN_HARVESTER));

        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            switch (msg.getPerformative()) {
                case Performatives.REQUEST_PLAN_HARVESTERS:
                    handlePlansRequest(msg);
                    break;
                case Performatives.REPLY_PLAN_HARVESTER:
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
        
//        ((HarvesterCoordinatorAgent) myAgent).log("Received plans request");

        try {
            Cell[][] map = (Cell[][]) msg.getContentObject();
            List<AID> harvesters = this.harvCoordinator.getCoordinatedHarvesters();

            harvestersWithoutPlanReply = new ArrayList<>();
            harvestersWithoutPlanReply.addAll(harvesters);
            currentPlans = new HashMap<>();
            coordinator = msg.getSender();

            ACLMessage request = new ACLMessage(Performatives.REQUEST_PLAN_HARVESTER);
            request.setSender(myAgent.getAID());
            for (AID harvester : harvesters) {
                request.addReceiver(harvester);
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
            harvestersWithoutPlanReply.remove(msg.getSender());

            if (harvestersWithoutPlanReply.isEmpty()) {
//                ((HarvesterCoordinatorAgent) myAgent).log("Received plans from all Harvesters.");
                sendPlans();
            }

        } catch (UnreadableException ex) {
            ex.printStackTrace();
        }
    }

    private void sendPlans() {

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

}
