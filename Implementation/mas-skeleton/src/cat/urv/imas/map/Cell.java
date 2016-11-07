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

/**
 * This class keeps all the information about a cell in the map.
 * Coordinates (row, col) are zero based. This means all values goes from
 * [0..n-1], both included, for each dimension.
 */
public abstract class Cell implements java.io.Serializable {

    /**
     * Cell type.
     */
    private final CellType type;
    /**
     * Row number for this cell, zero based.
     */
    private int row = -1;
    /**
     * Column number for this cell, zero based.
     */
    private int col = -1;

    /**
     * Builds a cell with a given type.
     *
     * @param type Initial cell type.
     * @param row row number.
     * @param col column number.
     */
    public Cell(CellType type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
    }

    /* ********************************************************************** */
    /**
     * Gets the current row.
     *
     * @return the current row number in the map, in zero base.
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Gets the current column number in the map, in zero base.
     *
     * @return Column number in the map, in zero base.
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Gets the current cell type.
     *
     * @return Cell type.
     */
    public CellType getCellType() {
        return this.type;
    }

    /* ********************************************************************** */
    /**
     * Gets a string representation of the cell.
     *
     * @return
     */
    @Override
    public String toString() {
        String str = "(cell-type " + this.getCellType() + " "
                + "(r " + this.getRow() + ")"
                + "(c " + this.getCol() + ")";
        str += this.toStringSpecialization();
        return str + ")";
    }
    
    /**
     * Allows subclasses to build a specific string.
     * @return string specialization for the cell.
     */
    public String toStringSpecialization() {
        return "";
    }
    
    /* ************ Map visualization ****************************************/
    
    /**
     * The cell will be asked to be drawn, using the given CellVisualizer API.
     * To do so, it also has to override when necessary the getMessage() method.
     * @param visual provides the API to draw any kind of cell.
     */
    public abstract void draw(CellVisualizer visual);
    
    /**
     * Tells the message to show in the map. Empty string to paint nothing.
     * @return The text to show in the map, located in the current cell.
     */
    public String getMapMessage() {
        return "";
    }
}
