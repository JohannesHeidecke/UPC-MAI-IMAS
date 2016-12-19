/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.map.utility;

import cat.urv.imas.map.BuildingCell;
import cat.urv.imas.map.Cell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.plan.Location;
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

    private static SimpleWeightedGraph<Location, DefaultWeightedEdge> cityGraph
            = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

    private static FloydWarshallShortestPaths shortestPaths;

    private static Location[] coordinatesBuildingNeighbors;

    private static long[][] distances;

    public static void initialize(Cell[][] m) {
        map = m;
        constructCityGraph();
        coordinatesBuildingNeighbors = constructBuildingNeighborsArray();
        shortestPaths = new FloydWarshallShortestPaths(cityGraph);
        distances = distancesToArray();

    }

    public static List<Location> getShortestPath(Location from, Location to) {
        // Returns list of all coordinates on the path between 'from' and 'to'
        // Path can only contain Coordinates corresponding to StreetCells on the map
        // 'from' and 'to' correspond to StreetCells on the map
        // from is not included in the result list.

        if (from.equals(to)) {
            return null;
        }

        // Find vertices in graph:
        Location startVertex = null;
        Location endVertex = null;
        for (Location coord : cityGraph.vertexSet()) {
            if (coord.equals(from)) {
                startVertex = coord;
            }
            if (coord.equals(to)) {
                endVertex = coord;
            }
        }

        List<Location> result = shortestPaths.getShortestPath(startVertex, endVertex).getVertexList();
        result = result.subList(1, result.size());
        return result;
    }

    public static int getShortestDistance(Location from, Location to) {

        if (from.equals(to)) {
            return 0;
        }

        return getShortestPath(from, to).size();

    }

    private static void constructCityGraph() {

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {

                Cell cell = map[i][j];
                if (cell instanceof StreetCell) {
                    Location cellCoord = new Location(i, j);
                    List<Location> streetCellNeighbors = getStreetCellNeighbors(cellCoord);
                    cityGraph.addVertex(cellCoord);
                    for (Location neighbor : streetCellNeighbors) {
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

    private static List<Location> getStreetCellNeighbors(Location cellCoord) {

        // only non-diagonal neighbors are considered!
        List<Location> streetCellNeighbors = new ArrayList<>();
        int row = cellCoord.getRow();
        int col = cellCoord.getCol();

        if (row > 0) {
            if (map[row - 1][col] instanceof StreetCell) {
                streetCellNeighbors.add(new Location(row - 1, col));
            }
        }

        if (row < map.length - 1) {
            if (map[row + 1][col] instanceof StreetCell) {
                streetCellNeighbors.add(new Location(row + 1, col));
            }
        }

        if (col > 0) {
            if (map[row][col - 1] instanceof StreetCell) {
                streetCellNeighbors.add(new Location(row, col - 1));
            }
        }

        if (col < map[row].length - 1) {
            if (map[row][col + 1] instanceof StreetCell) {
                streetCellNeighbors.add(new Location(row, col + 1));
            }
        }

        return streetCellNeighbors;
    }

    public static List<Location> getTravelingSalesmanPath() {

        HashMap<Location, HashMap<Location, Long>> distancesMap;
        distancesMap = new HashMap<>(coordinatesBuildingNeighbors.length);

        for (int i = 0; i < coordinatesBuildingNeighbors.length; i++) {

            Location coord = coordinatesBuildingNeighbors[i];
            HashMap<Location, Long> coordDistances = new HashMap<>(coordinatesBuildingNeighbors.length);

            for (int j = 0; j < coordinatesBuildingNeighbors.length; j++) {
                coordDistances.put(coordinatesBuildingNeighbors[j], distances[i][j]);
            }

            distancesMap.put(coord, coordDistances);

        }

        List<Location> vertices = new LinkedList<>(Arrays.asList(coordinatesBuildingNeighbors));
        List<Location> tour = new LinkedList<>();

        while (tour.size() != coordinatesBuildingNeighbors.length) {
            boolean firstEdge = true;
            double minEdgeValue = 0;
            int minVertexFound = 0;
            int vertexConnectedTo = 0;

            for (int i = 0; i < tour.size(); i++) {
                Location v = tour.get(i);
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
                System.out.println("TSP progress: " + tour.size() + "/" + coordinatesBuildingNeighbors.length);
            }
            vertices.remove(minVertexFound);
        }

        Location coord;
        Location nextCoord;
        List<Location> result = new ArrayList<>();
        result.add(tour.get(0));
        for (int i = 0; i < tour.size(); i++) {
            coord = tour.get(i);
            nextCoord = tour.get((i + 1) % tour.size());
            List<Location> path = getShortestPath(coord, nextCoord);
            if (path != null) {
                result.addAll(path);
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

    private static Location[] constructBuildingNeighborsArray() {

        Set<Location> streetCells = cityGraph.vertexSet();
        List<Location> streetsNextToBuildings = new ArrayList<>(streetCells.size());

        for (Location streetCell : streetCells) {
            if (streetNextToBuilding(streetCell)) {
                streetsNextToBuildings.add(streetCell);
            }
        }

        Location[] result = streetsNextToBuildings.toArray(new Location[streetsNextToBuildings.size()]);
        return result;

    }

    private static boolean streetNextToBuilding(Location street) {
        boolean buildingFound = false;

        int minRow = Math.max(0, street.getRow() - 1);
        int minCol = Math.max(0, street.getCol() - 1);
        int maxRow = Math.min(map.length - 1, street.getRow() + 1);
        int maxCol = Math.min(map[0].length - 1, street.getCol() + 1);

        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (map[i][j] instanceof BuildingCell) {
                    buildingFound = true;
                }
            }
        }

        return buildingFound;
    }

}
