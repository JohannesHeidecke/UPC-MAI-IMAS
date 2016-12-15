/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.map;

import cat.urv.imas.plan.Coordinate;
import java.util.List;

/**
 *
 * @author Ihcrul
 */
public class MapUtility {
    
    Cell[][] map;
    
    public MapUtility(Cell[][] map) {
        this.map = map;
    }
    
    public List<Coordinate> getShortestPath(Coordinate from, Coordinate to) {
        // Returns list of all coordinates on the path between 'from' and 'to'
        // Path can only contain Coordinates corresponding to StreetCells on the map
        // 'from' and 'to' correspond to StreetCells on the map
        return null;
    }
    
    public List<Coordinate> getTravelingSalesmanPath(Coordinate start) {
        // Returns a cyclic path through the map based on TSP-solution
        return null;
    }
    
    public int getTravelDistance(Coordinate from, Coordinate to) {
        return getShortestPath(from, to).size();
    }
    
    
    
}
