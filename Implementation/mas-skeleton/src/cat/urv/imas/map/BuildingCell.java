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
package cat.urv.imas.map;

import cat.urv.imas.gui.CellVisualizer;
import cat.urv.imas.onthology.GarbageType;
import java.util.HashMap;
import java.util.Map;

/**
 * Building cell.
 */
public class BuildingCell extends Cell {
    
    /**
     * When this garbage is not found yet, an empty list of garbage is returned.
     */
    protected static Map<GarbageType, Integer> empty = new HashMap();

    /**
     * Garbage of the building: it can only be of one type at a time.
     * But, once generated, it can be of any type and amount.
     */
    protected Map<GarbageType, Integer> garbage;
    /**
     * If true, scouts have found this garbage. false when scouts have
     * to find it yet.
     */
    protected boolean found = false;
    
    /**
     * Builds a cell corresponding to a building.
     *
     * @param row row number.
     * @param col column number.
     */
    public BuildingCell(int row, int col) {
        super(CellType.BUILDING, row, col);
        garbage = new HashMap();
    }
    
    /**
     * Detects the real garbage on this building.
     * @return the garbage on it.
     */
    public Map<GarbageType, Integer> detectGarbage() {
        found = (!garbage.isEmpty());
        return garbage;
    }

    /**
     * Whenever the garbage has been detected, it informs about the 
     * current garbage on this building. Otherwise, it will behave as if
     * no garbage was in.
     * @return the garbage on it.
     */
    public Map<GarbageType, Integer> getGarbage() {
        return (found) ? garbage : empty;
    }
    
    /**
     * Removes an item of the current garbage, if any.
     * When there is no more garbage after removing it, the set of 
     * garbage is emptied.
     */
    public void removeGarbage() {
        if (found && garbage.size() > 0) {
            for (Map.Entry<GarbageType, Integer> entry: garbage.entrySet()) {
                if (entry.getValue() == 1) {
                    garbage.clear();
                    found = false;
                } else {
                    garbage.replace(entry.getKey(), entry.getValue()-1);
                }
            }
        }
    }
    
    /* ***************** Map visualization API ********************************/
    
    @Override
    public void draw(CellVisualizer visual) {
        visual.drawBuilding(this);
    }

    /**
     * Shows the type of garbage and the amount of it, with the form:
     * <pre>
     *    {type}:{amount}
     * </pre>
     * or an empty string if no garbage is present. A star is placed at the end
     * of the string if the garbage is found by scouts.
     * @return String detail of the garbage present in this building.
     */
    @Override
    public String getMapMessage() {
        if (garbage.isEmpty()) {
            return "";
        }
        for (Map.Entry<GarbageType, Integer> entry: garbage.entrySet()) {
            return entry.getKey().getShortString() + ":" + entry.getValue() + 
                    ((found) ? "*" : "");
        }
        return "";
    }
}
