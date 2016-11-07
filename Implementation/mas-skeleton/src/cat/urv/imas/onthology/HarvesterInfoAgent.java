/**
 * IMAS base code for the practical work. 
 * Copyright (C) 2014 DEIM - URV
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
package cat.urv.imas.onthology;

import cat.urv.imas.agent.AgentType;
import jade.core.AID;

/**
 * Agent information for harvester agents.
 */
public class HarvesterInfoAgent extends InfoAgent {

    /**
     * Types of garbage allowed to harvest.
     */
    protected GarbageType[] allowedTypes;
    /**
     * Maximum units of garbage of any type able to harvest at a time.
     */
    protected int capacity;
    
    public HarvesterInfoAgent(AgentType type) {
        super(type);
    }
    
    public HarvesterInfoAgent(AgentType type, GarbageType[] allowedTypes, int capacity) {
        super(type);
        this.allowedTypes = allowedTypes;
        this.capacity = capacity;
    }
    
    public HarvesterInfoAgent(AgentType type, AID aid) {
        super(type, aid);
    }
    
    public HarvesterInfoAgent(AgentType type, AID aid, GarbageType[] allowedTypes, int capacity) {
        super(type, aid);
        this.allowedTypes = allowedTypes;
        this.capacity = capacity;
    }
    
    /**
     * String representation of this isntance.
     *
     * @return string representation.
     */
    @Override
    public String toString() {
        boolean[] allowed = new boolean[]{false, false, false};
        for (GarbageType g : allowedTypes) {
            switch (g) {
                case GLASS: 
                    allowed[0] = true;
                    break;
                case PAPER:
                    allowed[1] = true;
                    break;
                case PLASTIC:
                    allowed[2] = true;
                    break;
            }
        }
        String[] names = new String[] {"glass", "paper", "plastic"}; 
        String types = "";
        for (int i=0; i < allowed.length; i++) {
            if (allowed[i]) {
                types += " " +names[i];
            }
        }
        if (types.length() > 0) {
            types = "(allowed-garbage " + types + ")";
        }
        return "(info-agent (agent-type " + this.getType() + ")"
                + ((null != this.getAID()) ? (" (aid " + this.getAID() + ")") : "")
                + " (capacity " + capacity + ")"
                + " " + types
                + ")";
    }
    
    @Override
    public String getMapMessage() {
        String types = "";
        for (GarbageType g: allowedTypes) {
            types += "," + g.getShortString();
        }
        return super.getMapMessage() +  ": " + types.substring(1);
    }

}
