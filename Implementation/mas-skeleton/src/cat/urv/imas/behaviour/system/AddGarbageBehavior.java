/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.behaviour.system;

import cat.urv.imas.agent.SystemAgent;
import cat.urv.imas.map.BuildingCell;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.SettableBuildingCell;
import cat.urv.imas.onthology.GarbageType;
import cat.urv.imas.plan.Location;
import jade.core.behaviours.SimpleBehaviour;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Ihcrul
 */
public class AddGarbageBehavior extends SimpleBehaviour {
    
    public static int noGarbageGenerated = 0;

    @Override
    public void action() {

        int maxAmountOfNewGarbage = ((SystemAgent) myAgent).getGame().getMaxAmountOfNewGargabe();
        int maxNumberBuildingsWithGarbage = ((SystemAgent) myAgent).getGame().getMaxNumberBuildingWithNewGargabe();
        int newGarbageProbability = ((SystemAgent) myAgent).getGame().getNewGarbageProbability();
        

        newGarbageProbability /= 2;
        
        int min = 1;
        int max = 100;
        boolean addGarbage = newGarbageProbability >= ThreadLocalRandom.current().nextInt(min, max + 1);

        if (!addGarbage) {
            return;
        }

//        if (currentNumberBuildingsWithGarbage() >= maxNumberBuildingsWithGarbage) {
//            return;
//        }

        Cell[][] map = ((SystemAgent) myAgent).getGame().getMap();

        min = 1;
        max = maxNumberBuildingsWithGarbage;
        int nOBuildings = ThreadLocalRandom.current().nextInt(min, max + 1);

        for (int i = 0; i < nOBuildings; i++) {
            boolean added = false;
            int minRow = 0;
            int minCol = 0;
            int maxRow = map.length;
            int maxCol = map[0].length;
            while (!added) {
                int randomRow = ThreadLocalRandom.current().nextInt(minRow, maxRow);
                int randomCol = ThreadLocalRandom.current().nextInt(minCol, maxCol);
                Cell randomCell = map[randomRow][randomCol];
                if (randomCell instanceof SettableBuildingCell) {
                    if (!((BuildingCell) randomCell).hasGarbage()) {
                        int garbageAmount = ThreadLocalRandom.current().nextInt(1, maxAmountOfNewGarbage + 1);
                        int randomType = ThreadLocalRandom.current().nextInt(0, 3);
                        GarbageType garbageType = null;
                        switch (randomType) {
                            case 0:
                                garbageType = GarbageType.GLASS;
                                break;
                            case 1:
                                garbageType = GarbageType.PAPER;
                                break;
                            case 2:
                                garbageType = GarbageType.PLASTIC;
                        }

                        ((SettableBuildingCell) randomCell).setGarbage(garbageType, garbageAmount);
                        Location loc = new Location(randomRow, randomCol);
                        ((SystemAgent) myAgent).registerGeneratedGarbage(loc, garbageType, garbageAmount);
                        added = true;
                        noGarbageGenerated += garbageAmount;

                    }
                }
            }
        }

    }

    private int currentNumberBuildingsWithGarbage() {
        int result = 0;
        Cell[][] map = ((SystemAgent) myAgent).getGame().getMap();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] instanceof SettableBuildingCell) {
                    if (((BuildingCell) map[i][j]).hasGarbage()) {
                        result++;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean done() {
        return true;
    }

}
