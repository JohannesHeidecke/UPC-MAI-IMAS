/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.system;

import cat.urv.imas.agent.SystemAgent;
import jade.core.behaviours.OneShotBehaviour;
import java.text.DecimalFormat;

/**
 *
 * @author Ihcrul
 */
public class UpdateGuiBehaviour extends OneShotBehaviour {

    @Override
    public void action() {
        
        if ((SystemAgent.getCurrentSimulationStep()) % 100 == 0) {
            
            DecimalFormat df = new DecimalFormat("#.##");
            double meanWaitTime = ((SystemAgent) myAgent).getAverageWaitTime();
            String waitS = df.format(meanWaitTime);
            String beneS = df.format(SystemAgent.getBenefitsPerStep());
            
            ((SystemAgent) myAgent).writeStatisticMessage("Mean Garbage Waiting Time: " + waitS);
            ((SystemAgent) myAgent).writeStatisticMessage("\t|\tBenefits/Step: "+ beneS + "\n");
        }
        ((SystemAgent) myAgent).updateGUI();
        
        
        
        
    }
    
}
