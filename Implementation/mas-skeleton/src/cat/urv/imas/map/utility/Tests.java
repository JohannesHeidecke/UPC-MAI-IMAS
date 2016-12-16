/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.map.utility;

import cat.urv.imas.map.BuildingCell;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.onthology.InitialGameSettings;
import cat.urv.imas.plan.Coordinate;
import java.util.List;

/**
 *
 * @author Ihcrul
 */
public class Tests {
    
    public static void main(String[] args) {
        
        Cell[][] map = new Cell[2][2];
//        map[0][0] = new StreetCell(0, 0);
//        map[0][1] = new StreetCell(0, 1);
//        map[1][1] = new StreetCell(1, 1);
//        map[1][0] = new StreetCell(1, 0);
        
        map =  InitialGameSettings.load("game.settings").getMap();
        
        long startTime = System.currentTimeMillis();
        MapUtility.initialize(map);
        long nowTime = System.currentTimeMillis();
        System.out.println("Initialize: "+(nowTime - startTime));
        startTime = nowTime;
        List<Coordinate> tspPath = MapUtility.getTravelingSalesmanPath();
        System.out.println(tspPath);
        System.out.println("TSP length: "+MapUtility.getTourLength(tspPath));
        nowTime = System.currentTimeMillis();
        System.out.println("Calculate: "+(nowTime - startTime));
        
        
        
    }
    
}
