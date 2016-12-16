/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.map.utility;

import cat.urv.imas.map.Cell;
import cat.urv.imas.map.StreetCell;
import cat.urv.imas.plan.Coordinate;
import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.NodeEvaluator2;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 *
 * @author Ihcrul
 */
public class MapUtility {

    static {
        System.loadLibrary("jniortools");
    }

    private static Cell[][] map;

    private static SimpleWeightedGraph<Coordinate, DefaultWeightedEdge> cityGraph
            = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

    private static FloydWarshallShortestPaths shortestPaths;

    private static Coordinate[] coordinates;

    private static long[][] distances;

    public static void initialize(Cell[][] m) {
        map = m;
        constructCityGraph();
        coordinates = cityGraph.vertexSet().toArray(new Coordinate[cityGraph.vertexSet().size()]);
        shortestPaths = new FloydWarshallShortestPaths(cityGraph);
        distances = distancesToArray();

    }

    public static List<Coordinate> getShortestPath(Coordinate from, Coordinate to) {
        // Returns list of all coordinates on the path between 'from' and 'to'
        // Path can only contain Coordinates corresponding to StreetCells on the map
        // 'from' and 'to' correspond to StreetCells on the map

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

        final int size = cityGraph.vertexSet().size();

        RoutingModel routing = new RoutingModel(size, 1, 0);

        NodeEvaluator2 distanceEvaluator = new NodeEvaluator2() {
            @Override
            public long run(int firstIndex, int secondIndex) {
                return distances[firstIndex][secondIndex];
            }
        };

        routing.setCost(distanceEvaluator);

        RoutingSearchParameters searchParameters = RoutingSearchParameters.newBuilder()
                .mergeFrom(RoutingModel.defaultSearchParameters())
                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                .build();

        Assignment solution = routing.solveWithParameters(searchParameters);

        int routeNumber = 0;
        List<Coordinate> tspRoute = new ArrayList<>();
        for (long node = routing.start(routeNumber);
                !routing.isEnd(node);
                node = solution.value(routing.nextVar(node))) {
            tspRoute.add(coordinates[(int)node]);
        }
        
        // if necessary (distance > 1): add paths between coordinates in result:
        Coordinate coord, nextCoord;
        List<Coordinate> result = new ArrayList<>();
        for (int i = 0; i < tspRoute.size(); i++) {
            coord = tspRoute.get(i);
            nextCoord = tspRoute.get((i+1) % tspRoute.size());
            List<Coordinate> path = getShortestPath(coord, nextCoord);
            result.addAll(path.subList(1, path.size()));
        }
        
        return result;

    }

    private static long[][] distancesToArray() {
        long[][] result = new long[coordinates.length][coordinates.length];

        for (int i = 0; i < coordinates.length; i++) {
            result[i][i] = 0;
            for (int j = i+1; j < coordinates.length; j++) {
                int distance = getShortestDistance(coordinates[i], coordinates[j]);
                result[i][j] = distance;
                result[j][i] = distance;
            }
        }

        return result;
    }

}
