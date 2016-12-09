/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.agent;

import cat.urv.imas.map.Cell;
import cat.urv.imas.onthology.GarbageType;
import cat.urv.imas.onthology.HarvesterInfoAgent;
import java.util.Arrays;

/**
 *
 * @author johannesheidecke
 */
public class HarvesterAgent extends ImasAgent {
    
    private Cell location;
    private HarvesterInfoAgent infoAgent;
    private GarbageType[] garbageTypes;
    
    private static int capacity;

    public HarvesterAgent() {
        super(AgentType.HARVESTER);

    }

    @Override
    protected void setup() {
        this.location = (Cell) this.getArguments()[0];
        infoAgent = (HarvesterInfoAgent) this.getArguments()[1];
        infoAgent.setAID(this.getAID());
        this.garbageTypes = (GarbageType[]) this.getArguments()[2];
        log("["+location.getRow()+"|"+location.getCol()+"]\t"+Arrays.toString(garbageTypes));
    }
    
    public static void setCapacity(int c) {
        capacity = c;
    }

}
