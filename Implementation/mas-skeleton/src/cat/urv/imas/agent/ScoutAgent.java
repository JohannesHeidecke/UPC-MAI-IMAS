/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.agent;

import static cat.urv.imas.agent.ImasAgent.OWNER;
import cat.urv.imas.behaviour.scout.ScoutBehaviour;
import cat.urv.imas.map.Cell;
import cat.urv.imas.onthology.HarvesterInfoAgent;
import cat.urv.imas.onthology.InfoAgent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 *
 * @author johannesheidecke
 */
public class ScoutAgent extends ImasAgent {
    
    private Cell location;
    private InfoAgent infoAgent;
    
    
    public ScoutAgent() {
        super(AgentType.SCOUT);
    }
    
    @Override
    protected void setup() {
        
        /* ** Very Important Line (VIL) ***************************************/
        this.setEnabledO2ACommunication(true, 1);
        /* ********************************************************************/

        // Register the agent to the DF
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType(AgentType.SCOUT.toString());
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
        
        this.location = (Cell) this.getArguments()[0];
//        log("["+location.getRow()+"|"+location.getCol()+"]");
        infoAgent = (InfoAgent) this.getArguments()[1];
        infoAgent.setAID(this.getAID());
        
        this.addBehaviour(new ScoutBehaviour());
    }
    
    public InfoAgent getInfoAgent() {
        return this.infoAgent;
    }
    
}
