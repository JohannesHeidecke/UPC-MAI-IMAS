/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.map.utility;

import cat.urv.imas.map.BuildingCell;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.plan.Coordinate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 *
 * @author Ihcrul
 */
public class MapUtility {


    private static Cell[][] map;

    private static SimpleWeightedGraph<Coordinate, DefaultWeightedEdge> cityGraph
            = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

    private static FloydWarshallShortestPaths shortestPaths;

    private static Coordinate[] coordinatesBuildingNeighbors;

    private static long[][] distances;

    public static void initialize(Cell[][] m) {
        map = m;
        constructCityGraph();
        coordinatesBuildingNeighbors = constructBuildingNeighborsArray();
        shortestPaths = new FloydWarshallShortestPaths(cityGraph);
        distances = distancesToArray();

    }

    public static List<Coordinate> getShortestPath(Coordinate from, Coordinate to) {
        // Returns list of all coordinates on the path between 'from' and 'to'
        // Path can only contain Coordinates corresponding to StreetCells on the map
        // 'from' and 'to' correspond to StreetCells on the map

        if (from.equals(to)) {
            return null;
        }

        // Find vertices in graph:
        Coordinate startVertex = null, endVertex = null;
        for (Coordinate coord : cityGraph.vertexSet()) {
            if (coord.equals(from)) {
                startVertex = coord;
            }
            if (coord.equals(to)) {
                endVertex = coord;
            }
        }

        return shortestPaths.getShortestPath(startVertex, endVertex).getVertexList();
    }

    public static int getShortestDistance(Coordinate from, Coordinate to) {

        if (from.equals(to)) {
            return 0;
        }

        return getShortestPath(from, to).size() - 1;

    }

    public static int getTourLength(List<Coordinate> tour) {

        if (tour == null || tour.size() < 2) {
            return 0;
        }
        int result = 0;
        for (int i = 1; i < tour.size(); i++) {
            result += getShortestDistance(tour.get(i - 1), tour.get(i));
        }
        return result;
    }

    private static void constructCityGraph() {

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {

                Cell cell = map[i][j];
                if (cell instanceof StreetCell) {
                    Coordinate cellCoord = new Coordinate(i, j);
                    List<Coordinate> streetCellNeighbors = getStreetCellNeighbors(cellCoord);
                    cityGraph.addVertex(cellCoord);
                    for (Coordinate neighbor : streetCellNeighbors) {
                        cityGraph.addVertex(neighbor);
                        if (!cityGraph.containsEdge(neighbor, cellCoord)) {
                            DefaultWeightedEdge edge = cityGraph.addEdge(cellCoord, neighbor);
                            cityGraph.setEdgeWeight(edge, 1);
                        }
                    }
                }

            }
        }

    }

    private static List<Coordinate> getStreetCellNeighbors(Coordinate cellCoord) {

        // only non-diagonal neighbors are considered!
        List<Coordinate> streetCellNeighbors = new ArrayList<>();
        int row = cellCoord.getRow();
        int col = cellCoord.getCol();

        if (row > 0) {
            if (map[row - 1][col] instanceof StreetCell) {
                streetCellNeighbors.add(new Coordinate(row - 1, col));
            }
        }

        if (row < map.length - 1) {
            if (map[row + 1][col] instanceof StreetCell) {
                streetCellNeighbors.add(new Coordinate(row + 1, col));
            }
        }

        if (col > 0) {
            if (map[row][col - 1] instanceof StreetCell) {
                streetCellNeighbors.add(new Coordinate(row, col - 1));
            }
        }

        if (col < map[row].length - 1) {
            if (map[row][col + 1] instanceof StreetCell) {
                streetCellNeighbors.add(new Coordinate(row, col + 1));
            }
        }

        return streetCellNeighbors;
    }

    public static List<Coordinate> getTravelingSalesmanPath() {

        HashMap<Coordinate, HashMap<Coordinate, Long>> distancesMap;
        distancesMap = new HashMap<>(coordinatesBuildingNeighbors.length);

        for (int i = 0; i < coordinatesBuildingNeighbors.length; i++) {

            Coordinate coord = coordinatesBuildingNeighbors[i];
            HashMap<Coordinate, Long> coordDistances = new HashMap<>(coordinatesBuildingNeighbors.length);

            for (int j = 0; j < coordinatesBuildingNeighbors.length; j++) {
                coordDistances.put(coordinatesBuildingNeighbors[j], distances[i][j]);
            }

            distancesMap.put(coord, coordDistances);

        }

        List<Coordinate> vertices = new LinkedList<>(Arrays.asList(coordinatesBuildingNeighbors));
        List<Coordinate> tour = new LinkedList<>();

        while (tour.size() != coordinatesBuildingNeighbors.length) {
            boolean firstEdge = true;
            double minEdgeValue = 0;
            int minVertexFound = 0;
            int vertexConnectedTo = 0;

            for (int i = 0; i < tour.size(); i++) {
                Coordinate v = tour.get(i);
                for (int j = 0; j < vertices.size(); j++) {
                    double weight = distancesMap.get(v).get(vertices.get(j));
                    if (firstEdge || (weight < minEdgeValue)) {
                        firstEdge = false;
                        minEdgeValue = weight;
                        minVertexFound = j;
                        vertexConnectedTo = i;
                    }
                }
            }
            tour.add(vertexConnectedTo, vertices.get(minVertexFound));
            if ((tour.size() % 10) == 0) {
                System.out.println("TSP progress: "+tour.size()+"/"+coordinatesBuildingNeighbors.length);
            }
            vertices.remove(minVertexFound);
        }

        Coordinate coord, nextCoord;
        List<Coordinate> result = new ArrayList<>();
        result.add(tour.get(0));
        for(int i = 0; i < tour.size(); i++) {
            coord = tour.get(i);
            nextCoord = tour.get((i+1) % tour.size());
            List<Coordinate> path = getShortestPath(coord, nextCoord);
            if (path != null) {
                result.addAll(path.subList(1, path.size()));
            }
            
        }

        return result;

    }

    private static long[][] distancesToArray() {
        long[][] result = new long[coordinatesBuildingNeighbors.length][coordinatesBuildingNeighbors.length];

        for (int i = 0; i < coordinatesBuildingNeighbors.length; i++) {
            result[i][i] = 0;
            for (int j = i + 1; j < coordinatesBuildingNeighbors.length; j++) {
                int distance = getShortestDistance(coordinatesBuildingNeighbors[i], coordinatesBuildingNeighbors[j]);
                result[i][j] = distance;
                result[j][i] = distance;
            }
        }

        return result;
    }

    private static Coordinate[] constructBuildingNeighborsArray() {

        Set<Coordinate> streetCells = cityGraph.vertexSet();
        Set<Coordinate> streetsNextToBuildings = new HashSet<>();

        for (Coordinate streetCell : streetCells) {
            // check if one of the neighbors is a building cell:
            int minRow = Math.max(0, streetCell.getRow() - 1);
            int minCol = Math.max(0, streetCell.getCol() - 1);
            int maxRow = Math.min(map.length - 1, streetCell.getRow() + 1);
            int maxCol = Math.min(map.length - 1, streetCell.getCol() + 1);
            boolean buildingFound = false;
            outerLoop:
            for (int i = minRow; i <= maxRow; i++) {
                for (int j = minCol; j <= maxCol; j++) {
                    if (i == streetCell.getRow() && j == streetCell.getCol()) {
                        continue;
                    }
                    if (map[i][j] instanceof BuildingCell) {
                        buildingFound = true;
                        break outerLoop;
                    }
                }
            }
            streetsNextToBuildings.add(streetCell);

        }

        Coordinate[] result = streetsNextToBuildings.toArray(new Coordinate[streetsNextToBuildings.size()]);
        return result;

    }

}
