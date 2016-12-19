/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.system;

import cat.urv.imas.SystemConstants;
import cat.urv.imas.agent.AgentType;
import cat.urv.imas.agent.SystemAgent;
import cat.urv.imas.behaviour.coordinator.harvester.CoordinateHarvestersBehaviour;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.onthology.InfoAgent;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Location;
import cat.urv.imas.plan.Movement;
import cat.urv.imas.plan.Plan;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihcrul
 */
public class PerformVehicleActionsBehaviour extends CyclicBehaviour {

    @Override
    public void onStart() {
        ((SystemAgent) myAgent).log("Started Behaviour: " + this.getClass().toString());
    }

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

        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(PerformVehicleActionsBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            
            HashMap<AID, Plan> plans = (HashMap<AID, Plan>) msg.getContentObject();
            Cell[][] map = ((SystemAgent) myAgent).getGame().getMap();
            
            
            HashMap<AID, Plan> movements = new HashMap<>();
            for (AID vehicle : plans.keySet()) {
                Plan plan  = plans.get(vehicle);
                if (plan.getActions().get(0) instanceof Movement) {
                    movements.put(vehicle, plan);
                }
            }
            
            // Add movement (stand still) for all not-moving agents:
            Map<AID, Location> notMovingVehicles = getVehcilesNotMoving(map, movements);
            for (AID vehicle : notMovingVehicles.keySet()) {
                Location standingAt = notMovingVehicles.get(vehicle);
                Plan plan = new Plan();
                plan.addAction(new Movement(standingAt.getRow(), standingAt.getCol(), standingAt.getRow(), standingAt.getCol()));
                movements.put(vehicle, plan);
            }


            List<ValidMovement> validMovements;
            do {
                
                cancelIllegalMovements(movements);
                
                Set<Location> collisionCells = getCollisionCells(movements);
                
                validMovements = new ArrayList<>();
                
                outerLoop:
                for (AID agent : movements.keySet()) {
                    Plan plan = movements.get(agent);
                    int rowTo = ((Movement) plan.getActions().get(0)).getRowTo();
                    int colTo = ((Movement) plan.getActions().get(0)).getColTo();
                    Location to = new Location(rowTo, colTo);
                    // check if toCell is a collision:
                    for (Location collision : collisionCells) {
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
                    Location from = new Location(rowFrom, colFrom);
                    InfoAgent infoAgent = ((StreetCell) map[rowFrom][colFrom]).getAgent();
                    validMovements.add(new ValidMovement(from, to, infoAgent));

                }
                // repeat until all agents are doing valid movements
                // repeat until all agents are doing valid movements
            } while (validMovements.size() < movements.size());

            executeValidMovements(validMovements, map);

        } catch (UnreadableException ex) {
            Logger.getLogger(PerformVehicleActionsBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Set<Location> getCollisionCells(HashMap<AID, Plan> movements) {
        Set<Location> collisions = new HashSet<>();
        Set<Location> tos = new HashSet<>();

        for (AID vehicle : movements.keySet()) {
            Plan plan = movements.get(vehicle);
            Movement movement = (Movement) plan.getActions().get(0);

            Location to = new Location(movement.getRowTo(), movement.getColTo());
            Location from = new Location(movement.getRowFrom(), movement.getColFrom());

            // Check if two vehicles are trying to move on the same cell:
            // check if to is already in tos (collision):
            for (Location coord : tos) {
                if (to.equals(coord)) {
                    collisions.add(to);
                    break;
                }
            }
            tos.add(to);

            // Check if two vehicles are trying to swap positions:
            for (AID otherVehicle : movements.keySet()) {
                Plan otherPlan = movements.get(otherVehicle);
                Movement otherMovement = (Movement) otherPlan.getActions().get(0);
                Location otherTo = new Location(otherMovement.getRowTo(), otherMovement.getColTo());
                Location otherFrom = new Location(otherMovement.getRowFrom(), otherMovement.getColFrom());
                if ((!to.equals(from)) && to.equals(otherFrom) && from.equals(otherTo)) {
                    collisions.add(to);
                    collisions.add(from);
                }
            }

        }

        return collisions;
    }

    public void cancelIllegalMovements(HashMap<AID, Plan> plans) {

        Cell[][] map = ((SystemAgent) myAgent).getGame().getMap();

        for (AID vehicle : plans.keySet()) {
            Plan plan = plans.get(vehicle);
            Movement movement = (Movement) plan.getActions().get(0);
            int rowTo = movement.getRowTo();
            int colTo = movement.getColTo();
            int rowFrom = movement.getRowFrom();
            int colFrom = movement.getColFrom();

            boolean illegal = false;

            // Trying to move on a non-street cell:
            if (!(map[rowTo][colTo] instanceof StreetCell)) {
                illegal = true;
            }

            // Trying to move more than one field:
            if (((Math.abs(rowFrom - rowTo)) + (Math.abs(colFrom - colTo))) > 1) {
                illegal = true;
            }

            if (illegal) {
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

        myAgent.addBehaviour(new UpdateGuiBehaviour());
    }

    private Map<AID, Location> getVehcilesNotMoving(Cell[][] map, HashMap<AID, Plan> movements) {
        
        Map<AID, Location> vehicles = new HashMap<>((int) (movements.size() * 1.2));
        
        for (int i = 0; i < map.length; i++){
            for (int j = 0; j < map[i].length; j++) {
                Cell cell = map[i][j];
                if (cell instanceof StreetCell) {
                    StreetCell sCell = (StreetCell) cell;
                    if (sCell.isThereAnAgent()) {
                        vehicles.put(sCell.getAgent().getAID(), new Location(i, j));
                    }
                }
            }
        }
        
        Map<AID, Location> vehiclesNotMoving = new HashMap<>(vehicles.size() - movements.size());
        for (AID vehicle : vehicles.keySet()) {
            if (!movements.keySet().contains(vehicle)) {
                vehiclesNotMoving.put(vehicle, vehicles.get(vehicle));
            }
        }
        return vehiclesNotMoving;
        
    }

}
