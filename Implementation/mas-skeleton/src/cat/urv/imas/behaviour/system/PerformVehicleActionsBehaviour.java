/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.system;

import cat.urv.imas.agent.AgentType;
import cat.urv.imas.agent.CoordinatorAgent;
import cat.urv.imas.agent.SystemAgent;
import cat.urv.imas.gui.CellVisualizer;
import cat.urv.imas.map.BuildingCell;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.CellType;
import cat.urv.imas.map.SettableBuildingCell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.onthology.GarbageType;
import cat.urv.imas.onthology.InfoAgent;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Coordinate;
import cat.urv.imas.plan.Movement;
import cat.urv.imas.plan.Plan;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihcrul
 */
public class PerformVehicleActionsBehaviour extends CyclicBehaviour {

    @Override
    public void action() {

        MessageTemplate mt = MessageTemplate.MatchPerformative(Performatives.REQUEST_VEHICLE_ACTIONS);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            handleRequestedActions(msg);
            myAgent.addBehaviour(new AddGarbageBehavior());
        } else {
            block();
        }

    }

    private void handleRequestedActions(ACLMessage msg) {

        ((SystemAgent) myAgent).log("Performing vehicle Actions");

        try {
            HashMap<AID, Plan> plans = (HashMap<AID, Plan>) msg.getContentObject();
            Cell[][] map = ((SystemAgent) myAgent).getGame().getMap();

            cancelIllegalMovements(plans);

            List<ValidMovement> validMovements;
            do {
                List<Coordinate> collisionCells = getCollisionCells(plans);
                validMovements = new ArrayList<>();
                outerLoop:
                for (AID agent : plans.keySet()) {
                    Plan plan = plans.get(agent);
                    int rowTo = ((Movement) plan.getActions().get(0)).getRowTo();
                    int colTo = ((Movement) plan.getActions().get(0)).getColTo();
                    Coordinate to = new Coordinate(rowTo, colTo);
                    // check if toCell is a collision:
                    for (Coordinate collision : collisionCells) {
                        if (to.equals(collision)) {
                            // Vehicles that would collide are not allowed to move:
                            Movement movement = (Movement) plan.getActions().get(0);
                            int rowFrom = movement.getRowFrom();
                            int colFrom = movement.getColFrom();
                            movement.setRowTo(rowFrom);
                            movement.setColTo(colFrom);
                            continue outerLoop;
                        }
                    }

                    // If there was no collision: add Movement to validMovements
                    int rowFrom = ((Movement) plan.getActions().get(0)).getRowFrom();
                    int colFrom = ((Movement) plan.getActions().get(0)).getColFrom();
                    Coordinate from = new Coordinate(rowFrom, colFrom);
                    InfoAgent infoAgent = ((StreetCell) map[rowFrom][colFrom]).getAgent();
                    validMovements.add(new ValidMovement(from, to, infoAgent));

                }
                // repeat until all agents are doing valid movements
            } while (validMovements.size() < plans.size());

            executeValidMovements(validMovements, map);

        } catch (UnreadableException ex) {
            Logger.getLogger(PerformVehicleActionsBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private List<Coordinate> getCollisionCells(HashMap<AID, Plan> plans) {
        List<Coordinate> collisions = new ArrayList<>();
        List<Coordinate> tos = new ArrayList<>();

        for (Plan plan : plans.values()) {
            Movement movement = (Movement) plan.getActions().get(0);
            Coordinate to = new Coordinate(movement.getRowTo(), movement.getColTo());
            // check if to is already in tos (collision):
            for (Coordinate coord : tos) {
                if (to.equals(coord)) {
                    collisions.add(to);
                    break;
                }
            }

            tos.add(to);
        }

        return collisions;
    }

    public void cancelIllegalMovements(HashMap<AID, Plan> plans) {

        Cell[][] map = ((SystemAgent) myAgent).getGame().getMap();

        for (Plan plan : plans.values()) {
            Movement movement = (Movement) plan.getActions().get(0);
            int rowTo = movement.getRowTo();
            int colTo = movement.getColTo();
            int rowFrom = movement.getRowFrom();
            int colFrom = movement.getColFrom();
            if (!(map[rowTo][colTo] instanceof StreetCell)) {
                movement.setRowTo(rowFrom);
                movement.setColTo(colFrom);
            }
        }

    }

    private void executeValidMovements(List<ValidMovement> validMovements, Cell[][] map) {
        // Remove all agents of valid movements:
        for (ValidMovement validMovement : validMovements) {
            int rowFrom = validMovement.getFrom().getRow();
            int colFrom = validMovement.getFrom().getCol();
            try {
                ((StreetCell) map[rowFrom][colFrom]).removeAgent(validMovement.getInfoAgent());
            } catch (Exception ex) {
                Logger.getLogger(PerformVehicleActionsBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Add all agents of valid movements:
        for (ValidMovement validMovement : validMovements) {
            int rowTo = validMovement.getTo().getRow();
            int colTo = validMovement.getTo().getCol();
            try {
                ((StreetCell) map[rowTo][colTo]).addAgent(validMovement.getInfoAgent());
                if (validMovement.getInfoAgent().getType().equals(AgentType.SCOUT)) {
                  ((SystemAgent) myAgent).getGame().detectBuildingsWithGarbage(rowTo, colTo);
                }
            } catch (Exception ex) {
                Logger.getLogger(PerformVehicleActionsBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ((SystemAgent) myAgent).updateGUI();
    }

}
