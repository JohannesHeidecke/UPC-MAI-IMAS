/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.agent;

import cat.urv.imas.map.Cell;

/**
 *
 * @author johannesheidecke
 */
public class ScoutAgent extends ImasAgent {
    
    private Cell location;
    
    public ScoutAgent() {
        super(AgentType.SCOUT);
    }
    
    @Override
    protected void setup() {
        this.location = (Cell) this.getArguments()[0];
        log("["+location.getCol()+"|"+location.getRow()+"]");
    }
    
}
