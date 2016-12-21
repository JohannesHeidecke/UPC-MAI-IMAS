/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.agent;

import cat.urv.imas.PerformanceMeasure;
import cat.urv.imas.behaviour.harvester.GarbageEvaluation;
import static cat.urv.imas.agent.ImasAgent.OWNER;
import cat.urv.imas.behaviour.harvester.HarvesterBehaviour;
import cat.urv.imas.map.BuildingCell;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.RecyclingCenterCell;
import cat.urv.imas.map.utility.MapUtility;
import cat.urv.imas.map.utility.Permute;
import cat.urv.imas.onthology.Garbage;
import cat.urv.imas.onthology.GarbageType;
import cat.urv.imas.onthology.HarvesterInfoAgent;
import cat.urv.imas.onthology.Performatives;
import cat.urv.imas.plan.Location;
import cat.urv.imas.plan.Plan;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author johannesheidecke
 */
public class HarvesterAgent extends ImasAgent {

    private Location location;
    private HarvesterInfoAgent infoAgent;
    private GarbageType[] garbageTypes;
    
    private AID myCoordinator;

    private static int capacity;

    private int currentLoad = 0;
    private GarbageType currentLoadType = null;
    private List<Location> pickUpOrder = new ArrayList<>();
    private Map<Location, Integer> pickUpPlan = new HashMap<>();
    private Location targetedRecyclingCenter;

    private Location currentPickUpLoc = null;
    private int currentPickupAmount = 0;

    private Cell[][] map;

    public HarvesterAgent() {
        super(AgentType.HARVESTER);

    }

    @Override
    protected void setup() {

        /* ** Very Important Line (VIL) ***************************************/
        this.setEnabledO2ACommunication(true, 1);
        /* ********************************************************************/

        // Register the agent to the DF
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType(AgentType.HARVESTER.toString());
        sd1.setName(getLocalName());
        sd1.setOwnership(OWNER);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(sd1);
        dfd.setName(getAID());
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            System.err.println(getLocalName() + " registration with DF unsucceeded. Reason: " + e.getMessage());
            doDelete();
        }

        Cell initialCell = (Cell) this.getArguments()[0];
        Location myLoc = new Location(initialCell.getRow(), initialCell.getCol());
        this.location = myLoc;
        infoAgent = (HarvesterInfoAgent) this.getArguments()[1];
        infoAgent.setAID(this.getAID());
        this.garbageTypes = (GarbageType[]) this.getArguments()[2];

        this.addBehaviour(new HarvesterBehaviour());
    }

    public static void setCapacity(int c) {
        capacity = c;
    }

    public void setMap(Cell[][] map) {
        this.map = map;
    }

    public HarvesterInfoAgent getInfoAgent() {
        return this.infoAgent;
    }

    public boolean isIdle() {
        return (currentLoad == 0 && pickUpPlan.isEmpty());
    }

    public int getFreeCapacity() {
        return capacity - currentLoad;
    }

    public void setCurrentLocation(Location location) {
        this.location = location;
    }

    public Location getCurrentLocation() {
        return this.location;
    }

    public GarbageEvaluation getGarbageEvalFor(Garbage garbage) {
        List<Location> pickUpOrderAndCenter = getPickUpOrderAndCenterWith(garbage);
        double currentWaitingTime = evalWaitingTime(pickUpOrder);
        double newWaitingTime = evalWaitingTime(pickUpOrderAndCenter.subList(0, pickUpOrderAndCenter.size() - 1));
        double waitingTimeIncr = newWaitingTime - currentWaitingTime;
        int currentSteps = evalBusyTime(pickUpOrder, targetedRecyclingCenter);
        int newSteps = evalBusyTime(pickUpOrderAndCenter.
                subList(0, pickUpOrderAndCenter.size() - 1),
                pickUpOrderAndCenter.get(pickUpOrderAndCenter.size() - 1));
        int stepsIncr = newSteps - currentSteps;
        Location plannedCenter = pickUpOrderAndCenter.get(pickUpOrderAndCenter.size() - 1);
        int price = ((RecyclingCenterCell) map[plannedCenter.getRow()][plannedCenter.getCol()]).getPriceFor(garbage.getType());
        GarbageEvaluation garbEval = new GarbageEvaluation(stepsIncr, price, waitingTimeIncr);
        return garbEval;
    }

    private List<Location> getPickUpOrderAndCenterWith(Garbage garbage) {
        // the last element of the list contains the recycling center 

        List<Location> result = new ArrayList<>(this.pickUpOrder.size() + 2);

        // determine current garbage type
        GarbageType gType;
        if (pickUpOrder.isEmpty()) {
            gType = garbage.getType();
        } else {
            gType = this.currentLoadType;
        }

        List<RecyclingCenterCell> recyclingCenters = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] instanceof RecyclingCenterCell) {
                    RecyclingCenterCell center = (RecyclingCenterCell) map[i][j];
                    int gTypePrice = center.getPriceFor(gType);
                    if (gTypePrice > 0) {
                        recyclingCenters.add(center);
                    }
                }
            }
        }

        // for each Recycling Center with the right garbage type:
        // get the shortest route containing all current pick ups and new garbage
        // get all permutations of pick up orders:
        List<Location> pickUpLocs = new ArrayList<>();
        pickUpLocs.addAll(pickUpOrder);
        pickUpLocs.add(garbage.getLocation());

        List<Integer[]> pickUpPermutations = Permute.getIndexPermutations(pickUpLocs.size());
        Map<RecyclingCenterCell, Integer[]> shortestPathByCenter = new HashMap<>();
        Map<RecyclingCenterCell, Integer> shortestDistanceByCenter = new HashMap<>();

        for (RecyclingCenterCell center : recyclingCenters) {
            Location centerLoc = new Location(center.getRow(), center.getCol());
            int bestDistance = Integer.MAX_VALUE;
            Integer[] bestPermut = null;

            List<Location> lastPath = null;
            Location lastValidTo = location;
            for (Integer[] permut : pickUpPermutations) {
                int distance = 0;

                for (int i = 0; i < pickUpLocs.size(); i++) {
                    if (lastPath != null && lastPath.size() != 0) {
                        lastValidTo = lastPath.get(lastPath.size() - 1);
                    }
                    lastPath = MapUtility.getShortestPath(lastValidTo, pickUpLocs.get(permut[i]));
                    distance += lastPath != null ? lastPath.size() : 0;
                }
                if (lastPath != null && lastPath.size() != 0) {
                    lastValidTo = lastPath.get(lastPath.size() - 1);
                }
                lastPath = MapUtility.getShortestPath(lastValidTo, centerLoc);
                distance += lastPath != null ? lastPath.size() : 0;

                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestPermut = permut;
                }
            }

            shortestPathByCenter.put(center, bestPermut);
            shortestDistanceByCenter.put(center, bestDistance);

        }

        // evaluate all recycling centers with price / path length:
        // pick the best
        double bestPriceDistanceRatio = 0.0;
        RecyclingCenterCell bestCenter = null;
        for (RecyclingCenterCell center : recyclingCenters) {
            double priceDistanceRatio = center.getPriceFor(gType);
            priceDistanceRatio /= shortestDistanceByCenter.get(center);
            if (priceDistanceRatio > bestPriceDistanceRatio) {
                bestPriceDistanceRatio = priceDistanceRatio;
                bestCenter = center;
            }
        }

        // add all to result list
        Integer[] bestCenterPermut = shortestPathByCenter.get(bestCenter);
        for (int i = 0; i < pickUpLocs.size(); i++) {
            result.add(pickUpLocs.get(bestCenterPermut[i]));
        }
        Location centerLoc = new Location(bestCenter.getRow(), bestCenter.getCol());
        // the last element of the list contains the recycling center:
        result.add(centerLoc);

        return result;
    }

    private double evalWaitingTime(List<Location> pickUpLocations) {

        List<Integer> waitingTimes = new ArrayList<>();
        List<Location> lastPath = null;
        Location lastValidTo = location;

        int currentWaitingTime = 0;
        for (int i = 0; i < pickUpLocations.size(); i++) {
            if (lastPath != null && lastPath.size() != 0) {
                lastValidTo = lastPath.get(lastPath.size() - 1);
            }
            lastPath = MapUtility.getShortestPath(lastValidTo, pickUpLocations.get(i));
            currentWaitingTime += lastPath != null ? lastPath.size() : 0;
            waitingTimes.add(currentWaitingTime);
        }

        return PerformanceMeasure.getWaitingValue(waitingTimes);
    }

    private int evalBusyTime(List<Location> pickUpLocations, Location center) {

        if (pickUpLocations == null || pickUpLocations.isEmpty()) {
            return 0;
        }

        List<Location> lastPath = null;
        Location lastValidTo = location;

        int currentWaitingTime = 0;
        for (int i = 0; i < pickUpLocations.size(); i++) {
            if (lastPath != null && lastPath.size() != 0) {
                lastValidTo = lastPath.get(lastPath.size() - 1);
            }
            lastPath = MapUtility.getShortestPath(lastValidTo, pickUpLocations.get(i));
            currentWaitingTime += lastPath != null ? lastPath.size() : 0;
        }

        if (lastPath != null && lastPath.size() != 0) {
            lastValidTo = lastPath.get(lastPath.size() - 1);
        }
        lastPath = MapUtility.getShortestPath(lastValidTo, center);
        currentWaitingTime += lastPath != null ? lastPath.size() : 0;

        return currentWaitingTime;
    }

    public boolean canCarry(GarbageType type) {
        for (GarbageType myType : this.garbageTypes) {
            if (myType.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public void addGarbageToHarvest(Garbage garbage, int amount) {

        if (!(currentLoadType == null) && !garbage.getType().equals(this.currentLoadType)) {
            throw new RuntimeException("Harvester was assigned to pick up wrong type");
        }
        currentLoadType = garbage.getType();
        Location loc = garbage.getLocation();

        this.pickUpPlan.put(loc, amount);
        List<Location> order = getPickUpOrderAndCenterWith(garbage);

        this.pickUpOrder.clear();
        this.pickUpOrder.addAll(order.subList(0, (order.size() - 1)));

        this.targetedRecyclingCenter = order.get(order.size() - 1);

    }

    public GarbageType getCurrentLoadType() {
        return this.currentLoadType;
    }

    public boolean hasPickupLocation() {
        return !this.pickUpOrder.isEmpty();
    }

    public Location getNextPickupLocation() {
        return this.pickUpOrder.get(0);
    }

    public Location getTargetedRecyclingCenter() {
        return this.targetedRecyclingCenter;
    }

    public void pickUpOneUnit(Location loc) {

        Location toRemove = null;
        for (Location pickUpLoc : this.pickUpPlan.keySet()) {
            if (loc.equals(pickUpLoc)) {
                if (currentPickUpLoc == null) {
                    currentPickUpLoc = loc;
                    currentPickupAmount = pickUpPlan.get(pickUpLoc);
                }
                int currentAmount = pickUpPlan.get(pickUpLoc);
                pickUpPlan.replace(pickUpLoc, currentAmount - 1);
                if (currentAmount == 1) {
                    // all assigned garbage has been picked up
                    // remove from pick up order and pick up plan:
                    toRemove = pickUpLoc;
                    Iterator<Location> itOrder = pickUpOrder.iterator();
                    while (itOrder.hasNext()) {
                        Location pLoc = itOrder.next();
                        if (pLoc.equals(loc)) {
                            itOrder.remove();
                            log("Done picking up garbage at " + loc);
                            // Inform Coordinator about picked up garbage:
                            ACLMessage msg = new ACLMessage(Performatives.INFORM_PICKUP);
                            msg.setSender(this.getAID());
                            Object[] contentObj = new Object[2];
                            contentObj[0] = this.currentPickUpLoc;
                            contentObj[1] = this.currentPickupAmount;
                            try {
                                msg.setContentObject(contentObj);
                            } catch (IOException ex) {
                                Logger.getLogger(HarvesterAgent.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            msg.addReceiver(myCoordinator);
                            send(msg);
                            currentPickUpLoc = null;
                            break;
                        }
                    }

                }
            }
        }
        if (toRemove != null) {
            pickUpPlan.remove(toRemove);
        }
    }

    public int getCurrentLoadAmount() {
        return this.currentLoad;
    }

    public void loadOneUnit() {
        this.currentLoad++;
    }

    public void clearLoad() {
        this.currentLoad = 0;
        this.currentLoadType = null;
    }
    
    public void setCoordinator(AID coord) {
        this.myCoordinator = coord;
    }
    
    public AID getCoordinator() {
        return this.myCoordinator;
    }

}
