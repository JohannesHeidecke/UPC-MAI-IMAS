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
import cat.urv.imas.map.BuildingCell;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.RecyclingCenterCell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.onthology.GarbageType;
import cat.urv.imas.onthology.InfoAgent;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Action;
import cat.urv.imas.plan.Location;
import cat.urv.imas.plan.Movement;
import cat.urv.imas.plan.PickUp;
import cat.urv.imas.plan.Plan;
import cat.urv.imas.plan.Recycle;
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

//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(PerformVehicleActionsBehaviour.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        if (SystemAgent.getCurrentSimulationStep() > ((SystemAgent) myAgent).getGame().getSimulationSteps()) {
            SystemAgent sa = ((SystemAgent) myAgent);
            
            double finalDiscoveredR = (double) (SystemAgent.unitsDiscovered - SystemAgent.unitsCollected) 
                    / (AddGarbageBehavior.noGarbageGenerated - SystemAgent.unitsCollected);
            
            double finalCollectedR = (double) SystemAgent.unitsCollected / AddGarbageBehavior.noGarbageGenerated;
          
            String out = "\n- - - - - - - - - - - - - - -\n";
            out += "Units of garbage generated: " + AddGarbageBehavior.noGarbageGenerated;
            out += "\nUnits of garbage discovered: " + SystemAgent.unitsDiscovered;
            out += "\nUnits of garbage collected: " + SystemAgent.unitsCollected;
            out += "\nBenefits/Step: " + SystemAgent.getBenefitsPerStep();
            out += "\nBenefits Total: " + SystemAgent.getBenefitsPerStep() * SystemAgent.getCurrentSimulationStep();
            out += "\nMean time for discovering garbage: " + sa.getAverageTimeDiscovering();
            out += "\nMean time for collecting garbage: " + sa.getAverageTimeCollecting();
            double discoveredRatio = sa.getMeanDetected() / sa.getMeanUndetected();
            out += "\nMean ratio of discovered garbage: " + discoveredRatio;
            double collectedRatio = sa.getMeanCollected() / sa.getMeanDetected();
            out += "\nMean ratio of collected garbage: " + collectedRatio;
            out += "\nFinal ratio of discovered garbage: " + finalDiscoveredR;
            out += "\nFinal ratio of collected garbage: " + finalCollectedR;
            out += "\n- - - - - - - - - - - - - - -\n";
            System.out.println(out);
            ((SystemAgent) myAgent).writeStatisticMessage("\n\n" + out);
            try {
            Thread.sleep(300);
            } catch (InterruptedException ex) {
                Logger.getLogger(PerformVehicleActionsBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new RuntimeException("SIMULATION IS DONE!");
        }

        try {

            HashMap<AID, Plan> plans = (HashMap<AID, Plan>) msg.getContentObject();
            Cell[][] map = ((SystemAgent) myAgent).getGame().getMap();

            HashMap<AID, Plan> movements = new HashMap<>();
            for (AID vehicle : plans.keySet()) {
                Plan plan = plans.get(vehicle);
                Action nextStep = plan.getActions().get(0);
                if (nextStep instanceof Movement) {
                    Location vLoc = findVehiclePosition(vehicle);
//                    ((SystemAgent) myAgent).log(vehicle.getLocalName() + " (" + vLoc.toString() + ") moving: " + vehicle.getLocalName() + " \t" + ((Movement) nextStep).toString());
                    movements.put(vehicle, plan);
                } else {
                    if (nextStep instanceof PickUp) {
                        performPickUp(vehicle, (PickUp) nextStep, map);
                    } else if (nextStep instanceof Recycle) {
                        performRecycle(vehicle, (Recycle) nextStep, map);
                    }

                }
            }

            // Add movement (stand still) for all not-moving agents:
            Map<AID, Location> notMovingVehicles = getVehcilesNotMoving(map, movements);
            for (AID vehicle : notMovingVehicles.keySet()) {
                Location standingAt = notMovingVehicles.get(vehicle);
                Plan plan = new Plan();
                Movement standingMove = new Movement(standingAt.getRow(), standingAt.getCol(), standingAt.getRow(), standingAt.getCol());
//                ((SystemAgent) myAgent).log("Standing: " + vehicle.getLocalName() + " \t" + standingMove.toString());
                plan.addAction(standingMove);
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
            } while (validMovements.size() < movements.size());

            executeValidMovements(validMovements, map);

        } catch (UnreadableException ex) {
            Logger.getLogger(PerformVehicleActionsBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Register newly detected garbage:
        if (!SystemAgent.newlyDetectedGarbage.isEmpty()) {
            for (Location loc : SystemAgent.newlyDetectedGarbage) {
                ((SystemAgent) myAgent).registerDetection(loc);
            }
            SystemAgent.newlyDetectedGarbage.clear();
        }

        // Register how much garbage undetected:
        int counter = 0;
        for (GarbageStatistic garbStat : ((SystemAgent) myAgent).getGarbageStatList()) {
            if (!garbStat.isDetected()) {
                counter += garbStat.getAmount();
            }
        }
        ((SystemAgent) myAgent).addNoUndetectedGarbage(counter);

        // Register how much garbage detected:
        // (detected and not completely picked up)
        counter = 0;
        for (GarbageStatistic garbStat : ((SystemAgent) myAgent).getGarbageStatList()) {
            if (garbStat.isDetected() && !garbStat.isCompletelyPickedUp()) {
                counter += garbStat.getRemainingAmountToPickUp();
            }
        }
        ((SystemAgent) myAgent).addNoDetectedGarbage(counter);

        // Register how much garbage in harvesters:
        ((SystemAgent) myAgent).addNoCollectedGarbage();

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

        for (int i = 0; i < map.length; i++) {
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

    private void performPickUp(AID vehicle, PickUp pickUp, Cell[][] map) {

        // check if pick-up is valid and legal:
        // vehicle actually next to pick-up location?
        Location pickUpLoc = pickUp.getPickUpLoc();
        int minRow = Math.max(0, pickUpLoc.getRow() - 1);
        int minCol = Math.max(0, pickUpLoc.getCol() - 1);
        int maxRow = Math.min(map.length - 1, pickUpLoc.getRow() + 1);
        int maxCol = Math.min(map[0].length - 1, pickUpLoc.getCol() + 1);

        boolean vehicleFound = false;
        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (map[i][j] instanceof StreetCell) {
                    if (((StreetCell) map[i][j]).isThereAnAgent()) {
                        if (((StreetCell) map[i][j]).getAgent().getAID().equals(vehicle)) {
                            vehicleFound = true;
                        }
                    }
                }
            }
        }
        if (!vehicleFound) {
            throw new RuntimeException(vehicle.getLocalName() + " tried to perform an illegal pick-up. "
                    + "Not close enough to pick up the garbage.");
        }

        // does garbage exist in this type and amount?
        if (!(map[pickUpLoc.getRow()][pickUpLoc.getCol()] instanceof BuildingCell)) {
            throw new RuntimeException(vehicle.getLocalName() + " tried to perform an illegal pick-up. "
                    + "There is no building at the pick-up location " + pickUpLoc);
        } else {
            BuildingCell building = (BuildingCell) map[pickUpLoc.getRow()][pickUpLoc.getCol()];
            if (!building.hasGarbage()) {
                // TODO: this exception happens in very rare cases
                // uncomment and debug
                return;
//                throw new RuntimeException(vehicle.getLocalName() + " tried to perform an illegal pick-up. "
//                                 + "There is no garbage at "+pickUpLoc);
            }
            Map<GarbageType, Integer> garbage = building.getGarbage();
            boolean typeFound = false;
            for (GarbageType type : garbage.keySet()) {
                if (type.equals(pickUp.getType())) {
                    typeFound = true;
                }
            }
            if (!typeFound) {
                throw new RuntimeException(vehicle.getLocalName() + " tried to perform an illegal pick-up. "
                        + "There is no garbage of that type at " + pickUpLoc);
            }
        }

        // perform pick-up logic:
        ((BuildingCell) map[pickUpLoc.getRow()][pickUpLoc.getCol()]).removeGarbage();
        SystemAgent.unitsCollected++;
        ((SystemAgent) myAgent).registerPickUp(pickUpLoc);

    }

    private void performRecycle(AID vehicle, Recycle recycle, Cell[][] map) {

        // check if pick-up is valid and legal:
        // vehicle actually next to center's location?
        Location centerLoc = recycle.getCenterLoc();
        int minRow = Math.max(0, centerLoc.getRow() - 1);
        int minCol = Math.max(0, centerLoc.getCol() - 1);
        int maxRow = Math.min(map.length - 1, centerLoc.getRow() + 1);
        int maxCol = Math.min(map[0].length - 1, centerLoc.getCol() + 1);

        boolean vehicleFound = false;
        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (map[i][j] instanceof StreetCell) {
                    if (((StreetCell) map[i][j]).isThereAnAgent()) {
                        if (((StreetCell) map[i][j]).getAgent().getAID().equals(vehicle)) {
                            vehicleFound = true;
                        }
                    }
                }
            }
        }
        if (!vehicleFound) {
            throw new RuntimeException(vehicle.getLocalName() + " tried to perform an illegal recycling. "
                    + "Not close enough to the center");
        }

        // TODO: check if harvester has enough loaded.
        // perform recycling logic:
        int price = ((RecyclingCenterCell) map[centerLoc.getRow()][centerLoc.getCol()])
                .getPriceFor(recycle.getType());

        double benefits = price * recycle.getAmount();
        ((SystemAgent) myAgent).log(recycle.getAmount() + " units of " + recycle.getType().toString() + " recycled for " + benefits + " points  by " + vehicle.getLocalName() + " at " + centerLoc + ".");
        SystemAgent.addBenefits(benefits);

        ((SystemAgent) myAgent).registerRecycle(recycle.getAmount());

    }

    private Location findVehiclePosition(AID vehicleAID) {

        int row = -1, col = -1;
        outerLoop:
        for (int i = 0; i < ((SystemAgent) myAgent).getGame().getMap().length; i++) {
            for (int j = 0; j < ((SystemAgent) myAgent).getGame().getMap()[i].length; j++) {
                Cell cell = ((SystemAgent) myAgent).getGame().getMap()[i][j];
                if (cell instanceof StreetCell) {
                    StreetCell sCell = (StreetCell) cell;
                    if (sCell.isThereAnAgent()) {
                        if (vehicleAID.equals(sCell.getAgent().getAID())) {
                            row = i;
                            col = j;
                            break outerLoop;
                        }
                    }
                }
            }
        }

        if (row == -1 || col == -1) {
            throw new RuntimeException("Vehicle not found on map: " + vehicleAID.toString());
        }

        return new Location(row, col);

    }

}
