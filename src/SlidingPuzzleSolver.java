/*
*Student ID : w1956202 20221610
*Student Name : S.P.Nathasha Dewduni
*/

package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SlidingPuzzleSolver {

    public static void main(String[] args) {
        // Input file path
        String filePath = "example.txt";
        processInput(new File(filePath)); // Process the input file
        displayWelcomeMessage(); // Display the welcome message
        processInput(new File(filePath)); // Process the input file for solving
        displayEndGameMessage(); // Display the end game message
    }

    // Display the welcome message for the game
    private static void displayWelcomeMessage() {
        System.out.println("");
        System.out.println("***********************************************************************");
        System.out.println("                Welcome to the Sliding Puzzle Game!                    ");
        System.out.println("***********************************************************************");
        System.out.println("");
        System.out.println("       Navigate from 'S' (start) to 'F' (finish) on the map.");
        System.out.println("       You can move in all four directions: up, down, left, right.");
        System.out.println("       Find the shortest path to reach the finish!");
        
        System.out.println();
    }

    // Display the  message for completing the game
    private static void displayEndGameMessage() {
        System.out.println();
        System.out.println("**Congratulations! You have completed the Sliding Puzzle Game.**");
        System.out.println("");
    }

    // Process the input file to read the puzzle map and solve the puzzle
    private static void processInput(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // Parsing map dimensions and content
            List<String> lines = new ArrayList<>();
            String line;
            int width = -1;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                if (width == -1) {
                    width = line.length();
                }
            }
            int height = lines.size();
            char[][] map = new char[height][width];

            // Finding start and finish positions
            int startRow = -1, startCol = -1, finishRow = -1, finishCol = -1;
            for (int row = 0; row < height; row++) {
                line = lines.get(row);
                for (int col = 0; col < width; col++) {
                    char square = line.charAt(col);
                    map[row][col] = square;

                    if (square == 'S') {
                        startRow = row;
                        startCol = col;
                    } else if (square == 'F') {
                        finishRow = row;
                        finishCol = col;
                    }
                }
            }

            reader.close();

            // Solving the puzzle and outputting steps
            long startTime = System.nanoTime(); // Start time
            solve(map, startRow, startCol, finishRow, finishCol); // Solve the puzzle
            long endTime = System.nanoTime(); // End time

            // Print elapsed time in milliseconds
            long elapsedTimeMs = (endTime - startTime) / 1_000_000; // Convert nanoseconds to milliseconds
            System.out.println("");
            System.out.println("Elapsed Time for Pathfinding: " + elapsedTimeMs + " milliseconds");

        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }

    // Solve the puzzle using the A* search algorithm
    private static void solve(char[][] map, int startRow, int startCol, int finishRow, int finishCol) {
        int height = map.length;
        int width = map[0].length;

        // Initializing data structures for pathfinding
        Queue<GridNode> openList = new LinkedList<>();
        Map<String, GridNode> allNodes = new HashMap<>();
        Set<GridNode> closedList = new HashSet<>();

        // Adding start node to the open list
        GridNode startNode = new GridNode(startRow, startCol, null, 0);
        openList.offer(startNode);
        allNodes.put(startNode.getHash(), startNode);

        // Defining directions: up, down, left, right
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        // A* search
        while (!openList.isEmpty()) {
            GridNode currentNode = openList.poll();
            closedList.add(currentNode);

            if (currentNode.getRow() == finishRow && currentNode.getCol() == finishCol) {
                // Path found, reconstructing and printing the path
                printShortestPath(map, startRow, startCol, currentNode);
                return;
            }

            for (int[] direction : directions) {
                int newRow = currentNode.getRow();
                int newCol = currentNode.getCol();
                int steps = currentNode.getSteps() + 1;

                // Sliding in the chosen direction until hitting an obstacle
                while (newRow + direction[0] >= 0 && newRow + direction[0] < height &&
                        newCol + direction[1] >= 0 && newCol + direction[1] < width &&
                        map[newRow + direction[0]][newCol + direction[1]] != '0') {
                    newRow += direction[0];
                    newCol += direction[1];
                    // If the current position is ice ('.'), keep sliding in the same direction
                    if (map[newRow][newCol] == '.') {
                        steps++;
                    } else {
                        break; // Exit loop if not ice
                    }
                }

                GridNode neighbor = new GridNode(newRow, newCol, currentNode, steps);

                if (!closedList.contains(neighbor)) {
                    if (!allNodes.containsKey(neighbor.getHash()) || steps < allNodes.get(neighbor.getHash()).getSteps()) {
                        openList.offer(neighbor);
                        allNodes.put(neighbor.getHash(), neighbor);
                    }
                }
            }
        }

        // No path found
        System.out.println("No solution found.");
    }

    // Print the shortest path from start to finish on the map
    private static void printShortestPath(char[][] map, int startRow, int startCol, GridNode finalNode) {
        System.out.println("");
        System.out.println("1. Start at (" + (startCol + 1) + "," + (startRow + 1) + ")");
        List<String> path = new ArrayList<>();
        GridNode currentNode = finalNode;
        while (currentNode != null) {
            path.add(0, currentNode.getRow() + "," + currentNode.getCol());
            currentNode = currentNode.getParent();
        }
        for (int i = 0; i < path.size() - 1; i++) {
            String[] currentCoords = path.get(i).split(",");
            String[] nextCoords = path.get(i + 1).split(",");
            int currentRow = Integer.parseInt(currentCoords[0]);
            int currentCol = Integer.parseInt(currentCoords[1]);
            int nextRow = Integer.parseInt(nextCoords[0]);
            int nextCol = Integer.parseInt(nextCoords[1]);

            String direction;
            if (nextRow > currentRow) {
                direction = "down";
            } else if (nextRow < currentRow) {
                direction = "up";
            } else if (nextCol > currentCol) {
                direction = "right";
            } else {
                direction = "left";
            }

            System.out.println((i + 2) + ". Move " + direction + " to (" + (nextCol + 1) + "," + (nextRow + 1) + ")");
        }
        System.out.println(path.size() + 1 + ". Done!");
    }

    // Define a class to represent a node in the puzzle grid
    static class GridNode {
        private int row;
        private int col;
        private GridNode parent;
        private int steps;

        public GridNode(int row, int col, GridNode parent, int steps) {
            this.row = row;
            this.col = col;
            this.parent = parent;
            this.steps = steps;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public GridNode getParent() {
            return parent;
        }

        public int getSteps() {
            return steps;
        }

        // Generate a unique hash for each node based on its position
        public String getHash() {
            return row + "," + col;
        }
    }
}
