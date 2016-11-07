/**
 * IMAS base code for the practical work. 
 * Copyright (C) 2016 DEIM - URV
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

import cat.urv.imas.onthology.GarbageType;

/**
 * Building cell API for System agent which allows to set new garbage in buildings.
 * Set new garbage on building is restricted only to System agent, so that
 * BuildingCell is the API provided to agents.
 */
public class SettableBuildingCell extends BuildingCell {
    
    public SettableBuildingCell(int row, int col) {
        super(row, col);
    }
    
    public void setGarbage(GarbageType type, int amount) {
        if (!garbage.isEmpty()) {
            throw new IllegalStateException("This building (" + this.getRow() + "," + this.getCol() + ") has garbage yet: " + this.getMapMessage());
        }
        garbage.put(type, amount);
    }
}
