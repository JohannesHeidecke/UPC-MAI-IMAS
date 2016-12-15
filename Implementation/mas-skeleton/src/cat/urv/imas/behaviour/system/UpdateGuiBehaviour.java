/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.system;

import cat.urv.imas.agent.SystemAgent;
import jade.core.behaviours.OneShotBehaviour;

/**
 *
 * @author Ihcrul
 */
public class UpdateGuiBehaviour extends OneShotBehaviour {

    @Override
    public void action() {
        
        ((SystemAgent) myAgent).writeStatisticMessage("test");
        ((SystemAgent) myAgent).updateGUI();
        
        
        
    }
    
}
