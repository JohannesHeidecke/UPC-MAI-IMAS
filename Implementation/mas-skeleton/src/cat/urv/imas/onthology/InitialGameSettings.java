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
import cat.urv.imas.map.*;
import static cat.urv.imas.onthology.GarbageType.GLASS;
import static cat.urv.imas.onthology.GarbageType.PAPER;
import static cat.urv.imas.onthology.GarbageType.PLASTIC;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Initial game settings and automatic loading from file.
 * 
 * Use the GenerateGameSettings to build the game.settings configuration file.
 */
@XmlRootElement(name = "InitialGameSettings")
public class InitialGameSettings extends GameSettings {
    
    /*
     * Constants that define the type of content into the initialMap.
     * Any other value in a cell means that a cell is a building and
     * the value is the number of people in it.
     * 
     * Cells with mobile vehicles are street cells after vehicles 
     * move around.
     */
    /**
     * Street cell.
     */
    public static final int S = 0;
    /**
     * Harvester cell.
     */
    public static final int H = -1;
    /**
     * Scout cell.
     */
    public static final int SC = -2;
    /**
     * Recycling center cell.
     */
    public static final int R = -3;

    /**
     * City initialMap. Each number is a cell. The type of each is expressed by a
     * constant (if a letter, see above), or a building (indicating the number
     * of people in that building).
     */
    private int[][] initialMap
            = {
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, S, S, S, S, S, S, S, S, S, S, H, S, S, S, S, S, S, S, 10},
                {10, S, SC, S, S, S, S, H, S, S, S, S, S, S, S, S, S, S, H, 10},
                {10, S, S, 10, 10, 10, 10, 10, 10, S, S, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, S, S, 10, 10, 10, 10, 10, R, S, S, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, SC, S, 10, 10, S, S, S, S, S, S, 10, 10, S, S, S, S, S, S, 10},
                {10, S, S, 10, 10, S, S, S, S, S, S, 10, 10, S, S, S, S, S, S, 10},
                {10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10},
                {10, S, S, 10, 10, S, H, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10},
                {10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10},
                {10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10},
                {10, S, S, 10, 10, S, S, 10, 10, S, SC, 10, 10, S, S, 10, 10, S, S, 10},
                {10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, R, 10, S, S, 10},
                {10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, H, S, 10},
                {10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, H, S, 10},
                {10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10},
                {10, S, S, R, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10, 10, S, S, 10},
                {10, S, SC, S, H, S, S, 10, 10, S, S, S, S, S, S, 10, 10, S, S, 10},
                {10, S, S, S, S, S, S, 10, 10, S, S, S, S, S, S, 10, 10, H, S, 10},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},};

    /**
     * Plastic short string.
     */
    public static final String L = PLASTIC.getShortString();
    /**
     * Paper short string.
     */
    public static final String P = PAPER.getShortString();
    /**
     * Glass short string.
     */
    public static final String G = GLASS.getShortString();
    
    private String[][] supportedGarbageByHarvesters = {
        {L},
        {P},
        {G},
        {G, P},
        {L, P},
        {G, L},
        {G, P},
        {G, P, L},
    };
    
    public int[][] getInitialMap() {
        return initialMap;
    }

    @XmlElement(required = true)
    public void setInitialMap(int[][] initialMap) {
        this.initialMap = initialMap;
    }

    public String[][] getSupportedGarbageByHarvesters() {
        return supportedGarbageByHarvesters;
    }

    @XmlElement(required = true)
    public void setSupportedGarbageByHarvesters(String[][] supportedGarbageByHarvesters) {
        this.supportedGarbageByHarvesters = supportedGarbageByHarvesters;
        int rows = supportedGarbageByHarvesters.length;
        this.allowedGarbageTypePerHarvester = new GarbageType[rows][];
        for (int i=0; i < rows; i++) {
            int cols = supportedGarbageByHarvesters[i].length;
            this.allowedGarbageTypePerHarvester[i] = new GarbageType[cols];
            for (int j=0; j < cols; j++) {
                this.allowedGarbageTypePerHarvester[i][j] = GarbageType.fromShortString(supportedGarbageByHarvesters[i][j]);
            }
        }
    }

    public static final GameSettings load(String filename) {
        if (filename == null) {
            filename = "game.settings";
        }
        try {
            // create JAXBContext which will be used to update writer 		
            JAXBContext context = JAXBContext.newInstance(InitialGameSettings.class);
            Unmarshaller u = context.createUnmarshaller();
            InitialGameSettings starter = (InitialGameSettings) u.unmarshal(new FileReader(filename));
            starter.initMap();
            return starter;
        } catch (Exception e) {
            System.err.println(filename);
            System.exit(-1);
        }
        return null;
    }

    /**
     * Initializes the cell map.
     * @throws Exception if some error occurs when adding agents.
     */
    private void initMap() throws Exception {
        int rows = this.initialMap.length;
        int cols = this.initialMap[0].length;
        map = new Cell[rows][cols];
        int recyclingCenterIndex = 0;
        int allowedGarbageTypeIndex = 0;
        int[] recyclingCenterPrice;
        int[][] recyclingCenterPrices = this.getRecyclingCenterPrices();
        this.agentList = new HashMap();
        
        int cell;
        StreetCell c;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                cell = initialMap[row][col];
                switch (cell) {
                    case H: 
                        c = new StreetCell(row, col);
                        if (allowedGarbageTypeIndex >= allowedGarbageTypePerHarvester.length) {
                            throw new Error(getClass().getCanonicalName() + " : There are more harvesters than settings for them.");
                        }
                        c.addAgent(new HarvesterInfoAgent(AgentType.HARVESTER, allowedGarbageTypePerHarvester[allowedGarbageTypeIndex++], this.getHarvestersCapacity()));
                        map[row][col] = c;
                        addAgentToList(AgentType.HARVESTER, c);
                        break;
                    case SC:
                        c = new StreetCell(row, col);
                        c.addAgent(new InfoAgent(AgentType.SCOUT));
                        map[row][col] = c;
                        addAgentToList(AgentType.SCOUT, c);
                        break;
                    case S:
                        map[row][col] = new StreetCell(row, col);
                        break;
                    case R:
                        if (recyclingCenterIndex >= recyclingCenterPrices.length) {
                            throw new Error(getClass().getCanonicalName() + " : More recycling centers in the map than given prices");
                        }
                        recyclingCenterPrice = recyclingCenterPrices[recyclingCenterIndex++];
                        map[row][col] = new RecyclingCenterCell(row, col, recyclingCenterPrice);
                        break;
                    default: //positive value means building.
                        // agents has to check for BuildingCell casts. 
                        // Only SystemAgent can access to the SettableBuildingCell
                        map[row][col] = new SettableBuildingCell(row, col);
                        break;
                }
            }
        }
        if (recyclingCenterIndex != recyclingCenterPrices.length) {
            throw new Error(getClass().getCanonicalName() + " : Less recycling centers in the map than given prices.");
        }
        if (this.allowedGarbageTypePerHarvester.length != this.getAgentList().get(AgentType.HARVESTER).size()) {
            throw new Error(getClass().getCanonicalName() + " : There are less harvesters than settings.");
        }
    }
    
    /**
     * Ensure agent list is correctly updated.
     * 
     * @param type agent type.
     * @param cell cell where appears the agent.
     */
    private void addAgentToList(AgentType type, Cell cell) {
        List<Cell> list = this.agentList.get(type);
        if (list == null) {
            list = new ArrayList();
            this.agentList.put(type, list);
        }
        list.add(cell);
    }
}
