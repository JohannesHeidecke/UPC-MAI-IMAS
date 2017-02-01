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
            double meanCollectingTime = ((SystemAgent) myAgent).getAverageTimeCollecting();
            double meanDiscoveryTime = ((SystemAgent) myAgent).getAverageTimeDiscovering();
            double meanUndiscovered = ((SystemAgent) myAgent).getMeanUndetected();
            double meanDiscovered = ((SystemAgent) myAgent).getMeanDetected();
            double meanCollected = ((SystemAgent) myAgent).getMeanCollected();
            String waitColS = df.format(meanCollectingTime);
            String waitDisS = df.format(meanDiscoveryTime);
            String mUndisS = df.format(meanUndiscovered);
            String mDisS = df.format(meanDiscovered);
            String mCols = df.format(meanCollected);
            String beneS = df.format(SystemAgent.getBenefitsPerStep());
            ((SystemAgent) myAgent).writeStatisticMessage("Mean Discovery Time: " + waitDisS);
            ((SystemAgent) myAgent).writeStatisticMessage("\t\tMean Collecting Time: " + waitColS);
            ((SystemAgent) myAgent).writeStatisticMessage("\t|\tBenefits/Step: "+ beneS);
            ((SystemAgent) myAgent).writeStatisticMessage("\t|\tMean Undiscovered: "+ mUndisS);
            ((SystemAgent) myAgent).writeStatisticMessage("\t|\tMean Discovered: "+ mDisS);
            ((SystemAgent) myAgent).writeStatisticMessage("\t|\tMean Collected: "+ mCols + "\n");
        }
        ((SystemAgent) myAgent).updateGUI();
        
        
        
        
    }
    
}
