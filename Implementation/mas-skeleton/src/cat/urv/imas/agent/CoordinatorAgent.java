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

import cat.urv.imas.behaviour.coordinator.CoordinateVehicleActionsBehaviour;
import cat.urv.imas.onthology.GameSettings;
import cat.urv.imas.map.Cell;
import cat.urv.imas.onthology.MessageContent;
import jade.core.*;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPANames.InteractionProtocol;
import jade.lang.acl.*;
import java.util.List;
import java.util.Map;

/**
 * The main Coordinator agent. TODO: This coordinator agent should get the game
 * settings from the System agent every round and share the necessary
 * information to other coordinators.
 */
public class CoordinatorAgent extends ImasAgent {

    /**
     * Game settings in use.
     */
    private GameSettings game;
    /**
     * System agent id.
     */
    private AID systemAgent;

    private AID harvesterCoordinator;
    private AID scoutCoordinator;

    /**
     * Builds the coordinator agent.
     */
    public CoordinatorAgent() {
        super(AgentType.COORDINATOR);
    }

    /**
     * Agent setup method - called when it first come on-line. Configuration of
     * language to use, ontology and initialization of behaviours.
     */
    @Override
    protected void setup() {

        /* ** Very Important Line (VIL) ***************************************/
        this.setEnabledO2ACommunication(true, 1);
        /* ********************************************************************/

        // Register the agent to the DF
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType(AgentType.COORDINATOR.toString());
        sd1.setName(getLocalName());
        sd1.setOwnership(OWNER);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(sd1);
        dfd.setName(getAID());
        try {
            DFService.register(this, dfd);
            log("Registered to the DF");
        } catch (FIPAException e) {
            System.err.println(getLocalName() + " registration with DF unsucceeded. Reason: " + e.getMessage());
            doDelete();
        }

        // search SystemAgent
        ServiceDescription searchCriterion = new ServiceDescription();
        searchCriterion.setType(AgentType.SYSTEM.toString());
        this.systemAgent = UtilsAgents.searchAgent(this, searchCriterion);

        // search ScoutCoordinator and HarvesterCoordinator
        searchCriterion = new ServiceDescription();
        searchCriterion.setType(AgentType.HARVESTER_COORDINATOR.toString());
        this.harvesterCoordinator = UtilsAgents.searchAgent(this, searchCriterion);
        searchCriterion = new ServiceDescription();
        searchCriterion.setType(AgentType.SCOUT_COORDINATOR.toString());
        this.scoutCoordinator = UtilsAgents.searchAgent(this, searchCriterion);
        
        // Add the behaviour to coordinate all vehicle actions:
        this.addBehaviour(new CoordinateVehicleActionsBehaviour(this));

    }

    /**
     * Update the game settings.
     *
     * @param game current game settings.
     */
    public void setGame(GameSettings game) {
        this.game = game;
    }

    /**
     * Gets the current game settings.
     *
     * @return the current game settings.
     */
    public GameSettings getGame() {
        return this.game;
    }

    private static class Coordinator {

        public Coordinator() {
        }
    }

    public AID getSystemAgent() {
        return this.systemAgent;
    }

    public AID getHarvesterCoordinator() {
        return this.harvesterCoordinator;
    }

    public AID getScoutCoordinator() {
        return this.scoutCoordinator;
    }

}
