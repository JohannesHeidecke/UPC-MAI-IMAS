/**
 * IMAS base code for the practical work. Copyright (C) 2014 DEIM - URV
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cat.urv.imas.agent;

import cat.urv.imas.behaviour.system.GarbageStatistic;
import cat.urv.imas.behaviour.system.PerformVehicleActionsBehaviour;
import cat.urv.imas.behaviour.system.ProvideGameBehaviour;
import cat.urv.imas.onthology.InitialGameSettings;
import cat.urv.imas.onthology.GameSettings;
import cat.urv.imas.gui.GraphicInterface;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.map.utility.MapUtility;
import cat.urv.imas.onthology.GarbageType;
import jade.core.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * System agent that controls the GUI and loads initial configuration settings.
 * TODO: You have to decide the onthology and protocol when interacting among
 * the Coordinator agent.
 */
public class SystemAgent extends ImasAgent {

    /**
     * GUI with the map, system agent log and statistics.
     */
    private GraphicInterface gui;
    /**
     * Game settings. At the very beginning, it will contain the loaded initial
     * configuration settings.
     */
    private GameSettings game;
    /**
     * The Coordinator agent with which interacts sharing game settings every
     * round.
     */
    private AID coordinatorAgent;

    private static int simulationStep = 0;
    private static double benefits = 0;
    public static int unitsDiscovered = 0;
    public static int unitsCollected = 0;
    private List<GarbageStatistic> garbageStatList = new ArrayList<>();
    private List<Integer> noUndetectedGarbage = new ArrayList<>();
    private List<Integer> noDetectedGarbage = new ArrayList<>();
    private List<Integer> noCollectedGarbage = new ArrayList<>();
    public static List<cat.urv.imas.plan.Location> newlyDetectedGarbage = new ArrayList<>();
    private int globallyLoadedAmount = 0;

    /**
     * Builds the System agent.
     */
    public SystemAgent() {
        super(AgentType.SYSTEM);
    }

    /**
     * A message is shown in the log area of the GUI, as well as in the stantard
     * output.
     *
     * @param log String to show
     */
    @Override
    public void log(String log) {
        if (gui != null) {
            gui.log(getLocalName() + ": " + log + "\n");
        }
        super.log(log);
    }

    /**
     * An error message is shown in the log area of the GUI, as well as in the
     * error output.
     *
     * @param error Error to show
     */
    @Override
    public void errorLog(String error) {
        if (gui != null) {
            gui.log("ERROR: " + getLocalName() + ": " + error + "\n");
        }
        super.errorLog(error);
    }

    /**
     * Gets the game settings.
     *
     * @return game settings.
     */
    public GameSettings getGame() {
        return this.game;
    }

    /**
     * Agent setup method - called when it first come on-line. Configuration of
     * language to use, ontology and initialization of behaviours.
     */
    @Override
    protected void setup() {

        /* ** Very Important Line (VIL) ************************************* */
        this.setEnabledO2ACommunication(true, 1);

        // 1. Register the agent to the DF
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType(AgentType.SYSTEM.toString());
        sd1.setName(getLocalName());
        sd1.setOwnership(OWNER);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(sd1);
        dfd.setName(getAID());
        try {
            DFService.register(this, dfd);
            log("Registered to the DF");
        } catch (FIPAException e) {
            System.err.println(getLocalName() + " failed registration to DF [ko]. Reason: " + e.getMessage());
            doDelete();
        }

        // 2. Load game settings.
//        this.game = InitialGameSettings.load("game.group7.test4.settings");
        this.game = InitialGameSettings.load("game.secondcall1617.settings");
        log("Initial configuration settings loaded");
        MapUtility.initialize(game.getMap());
        log("Initialized MapUtility structures");

        // 3. Load GUI
        try {
            this.gui = new GraphicInterface(game);
            gui.setVisible(true);
            log("GUI loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Create other agents:
        ContainerController cc = this.getContainerController();
        AgentController agentController;
        try {
            // Coordinator
            agentController = cc.createNewAgent("Coordinator", "cat.urv.imas.agent.CoordinatorAgent", null);
            agentController.start();
            // Scout Coordinator
            agentController = cc.createNewAgent("ScoutCoordinator", "cat.urv.imas.agent.ScoutCoordinatorAgent", null);
            agentController.start();
            // Harvester Coordinator
            agentController = cc.createNewAgent("HarvesterCoordinator", "cat.urv.imas.agent.HarvesterCoordinatorAgent", null);
            agentController.start();

            // before continueing make sure 
            // that ScoutCoordinator and HarvesterCoordinator are created:
            ServiceDescription searchCriterion = new ServiceDescription();
            searchCriterion.setType(AgentType.SCOUT_COORDINATOR.toString());
            UtilsAgents.searchAgent(this, searchCriterion);
            searchCriterion = new ServiceDescription();
            searchCriterion.setType(AgentType.HARVESTER_COORDINATOR.toString());
            UtilsAgents.searchAgent(this, searchCriterion);

            // Create Harvesters and Scouts according to game settings:
            Map<AgentType, List<Cell>> agents = this.game.getAgentList();
            GarbageType[][] garbageTypes = this.game.getAllowedGarbageTypePerHarvester();
            HarvesterAgent.setCapacity(this.game.getHarvestersCapacity());
            for (AgentType agentType : agents.keySet()) {
                String className;
                switch (agentType) {
                    case HARVESTER:
                        className = HarvesterAgent.class.getName();
                        break;
                    case SCOUT:
                        className = ScoutAgent.class.getName();
                        break;
                    default:
                        Logger.getLogger(SystemAgent.class.getName()).log(Level.SEVERE, null,
                                "The game configuration AgentList contained an agent "
                                + "other than Scout or Harvester");
                        continue;
                }
                List<Cell> agentTypeCells = agents.get(agentType);
                int counter = 0;
                for (Cell cell : agentTypeCells) {
                    Object[] args = new Object[]{cell, ((StreetCell) cell).getAgent(), null};
                    if (agentType.equals(AgentType.HARVESTER)) {
                        args[2] = garbageTypes[counter];
                    }
                    agentController = cc.createNewAgent(agentType.getShortString() + "-" + counter, className, args);
                    agentController.start();
                    counter++;
                }
            }
        } catch (StaleProxyException ex) {
            Logger.getLogger(SystemAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        log("Agents created");

        // search CoordinatorAgent
        ServiceDescription searchCriterion = new ServiceDescription();
        searchCriterion.setType(AgentType.COORDINATOR.toString());
        this.coordinatorAgent = UtilsAgents.searchAgent(this, searchCriterion);
        // searchAgent is a blocking method, so we will obtain always a correct AID

        this.addBehaviour(new ProvideGameBehaviour(coordinatorAgent, game));
        this.addBehaviour(new PerformVehicleActionsBehaviour());

    }

    public void writeStatisticMessage(String msg) {
        this.gui.showStatistics(msg);
    }

    public void updateGUI() {
        this.gui.updateGame();
        simulationStep++;
    }

    public static int getCurrentSimulationStep() {
        return simulationStep;
    }

    public void registerGeneratedGarbage(cat.urv.imas.plan.Location loc, GarbageType type, int amount) {
        GarbageStatistic garbStat = new GarbageStatistic(loc, type, amount, simulationStep);
        garbageStatList.add(garbStat);
    }

    public void registerPickUp(cat.urv.imas.plan.Location loc) {
        
        GarbageStatistic garbageStat = null;
        for (int i = 0; i < garbageStatList.size(); i++) {
            garbageStat = garbageStatList.get(garbageStatList.size()-i-1);
            if (garbageStat.getLocation().equals(loc)) {
                break;
            } else {
                garbageStat = null;
            }
        }
        globallyLoadedAmount++;
        garbageStat.registerPickUp(simulationStep);

    }
    
    public void registerRecycle(int amount) {
        globallyLoadedAmount -= amount;
    }
    
    public void registerDetection(cat.urv.imas.plan.Location loc) {
        GarbageStatistic garbageStat = null;
        for (int i = 0; i < garbageStatList.size(); i++) {
            garbageStat = garbageStatList.get(garbageStatList.size()-i-1);
            if (garbageStat.getLocation().equals(loc)) {
                break;
            } else {
                garbageStat = null;
            }
        }
        garbageStat.registerDetection(simulationStep);
        SystemAgent.unitsDiscovered += garbageStat.getAmount();
    }
    
    public double getAverageTimeCollecting() {
        double result = 0;
        
        int doneCounter = 0;
        for (GarbageStatistic stat : garbageStatList) {
            if (stat.isPartiallyPickedUp()) {
                doneCounter++;
                result += stat.getFirstPickUpTime();
            }
        }
        if (doneCounter != 0) {
            result /= doneCounter;
        }
        
        return result;
    }
    
    public double getAverageTimeDiscovering() {
        double result = 0;
        
        int doneCounter = 0;
        for (GarbageStatistic stat : garbageStatList) {
            if (stat.isPartiallyPickedUp()) {
                doneCounter++;
                result += stat.getDiscoveryTime();
            }
        }
        if (doneCounter != 0) {
            result /= doneCounter;
        }
        
        return result;
    }
    
    public static void addBenefits(double b) {
        benefits += b;
    }
    
    public static double getBenefitsPerStep() {
        return benefits / simulationStep;
    }
    
    public List<GarbageStatistic> getGarbageStatList() {
        return this.garbageStatList;
    }
    
    public void addNoUndetectedGarbage(int n) {
        this.noUndetectedGarbage.add(n);
    }

    public void addNoDetectedGarbage(int counter) {
        this.noDetectedGarbage.add(counter);
    }
    
    public double getMeanDiscoveredRatio() {
        double undetected = 0;
        double detected = 0;
        for (int i = 0; i < noUndetectedGarbage.size(); i++) {
            undetected += noUndetectedGarbage.get(i);
            detected += noDetectedGarbage.get(i);
        }
        return detected / undetected;
    }
    
    public double getMeanUndetected() {
        double undetected = 0;
        int counter = 0;
        for (int i = 0; i < noUndetectedGarbage.size(); i++) {
            counter++;
            undetected += noUndetectedGarbage.get(i);
        }
        return undetected / counter;
    }
    
    public double getMeanDetected() {
        double detected = 0;
        int counter = 0;
        for (int i = 0; i < noDetectedGarbage.size(); i++) {
            counter++;
            detected += noDetectedGarbage.get(i);
        }
        return detected / counter;
    }
    
    public double getMeanCollected() {
        double collected = 0;
        int counter = 0;
        for (int i = 0; i < noCollectedGarbage.size(); i++) {
            counter++;
            collected += noCollectedGarbage.get(i);
        }
        return collected / counter;
    }

    public void addNoCollectedGarbage() {
        this.noCollectedGarbage.add(globallyLoadedAmount);
    }
    
    
    
    

}
