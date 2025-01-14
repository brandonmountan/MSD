package assignment08;

import java.io.*;
import java.util.*;

/**
 * Class responsible for solving mazes using breadth-first search to find the shortest path from start to goal.
 */
public class PathFinder {

    /**
     * Solves a maze given in an input file and outputs the solution to an output file.
     * @param inputFile the path to the input file containing the maze
     * @param outputFile the path to the output file where the solved maze will be written
     */
    public static void solveMaze(String inputFile, String outputFile) {
        // initialize Scanner and PrintWriter. Will be set later
        Scanner scanner = null;
        PrintWriter writer = null;

        try {
            // Open input file and read contents
            scanner = new Scanner(new File(inputFile));
            // store lines of maze as strings
            List<String> mazeLines = new ArrayList<>();

            while (scanner.hasNextLine()) {
                // read all lines from input file and add them to mazeLines List
                mazeLines.add(scanner.nextLine());
            }

            // Parse maze dimensions
            // First line contains dimensions (height, width) separated by space
            String[] dimensions = mazeLines.get(0).split(" ");
            // convert to integers
            int height = Integer.parseInt(dimensions[0]);
            int width = Integer.parseInt(dimensions[1]);

            // Parse maze into a grid
            // 2D character array to represent maze grid
            char[][] maze = new char[height][width];
            // store coordinates of start and goal positions
            int startX = -1, startY = -1, goalX = -1, goalY = -1;
            // iterate through remaining lines to fill maze grid
            // identify start and goal positions and record coordinates
            for (int i = 0; i < height; i++) {
                String row = mazeLines.get(i + 1);
                for (int j = 0; j < width; j++) {
                    maze[i][j] = row.charAt(j);
                    if (maze[i][j] == 'S') {
                        startX = i;
                        startY = j;
                    } else if (maze[i][j] == 'G') {
                        goalX = i;
                        goalY = j;
                    }
                }
            }

            // Perform BFS to find the shortest path
            // track if cell has been visited during BFS
            boolean[][] visited = new boolean[height][width];
            // store previous cell for each cell and use it to reconstruct path
            int[][] prev = new int[height * width][2];
            for (int[] p : prev) Arrays.fill(p, -1);

            // initialize queue for BFS and add starting position
            Queue<int[]> queue = new LinkedList<>();
            queue.add(new int[]{startX, startY});
            // mark starting cell as visited
            visited[startX][startY] = true;

            int[] dx = {-1, 1, 0, 0}; // represent movement directions
            int[] dy = {0, 0, -1, 1};

            boolean pathFound = false;
            // while queue is not empty, dequeue front cell (current) and extract coordinates
            while (!queue.isEmpty()) {
                int[] current = queue.poll();
                int x = current[0];
                int y = current[1];

                // if goal is reached, set pathFound to true and exit loop
                if (x == goalX && y == goalY) {
                    pathFound = true;
                    break;
                }

                // loop through all possible directions
                for (int d = 0; d < 4; d++) {
                    int nx = x + dx[d];
                    int ny = y + dy[d];

                    // check if neighbor cell is within bounds, not visited, and not a wall ('X')
                    if (nx >= 0 && ny >= 0 && nx < height && ny < width && !visited[nx][ny] && maze[nx][ny] != 'X') {
                        // add neighbor to queue
                        queue.add(new int[]{nx, ny});
                        // mark as visited
                        visited[nx][ny] = true;
                        // set current cell as its predecessor
                        prev[nx * width + ny] = new int[]{x, y};
                    }
                }
            }

            // If a path was found, reconstruct it
            if (pathFound) {
                int cx = goalX, cy = goalY;
                while (cx != startX || cy != startY) {
                    // bqcktrack from goal to start using prev array
                    int[] p = prev[cx * width + cy];
                    cx = p[0];
                    cy = p[1];
                    // mark path to cells with . unless it is starting cell
                    if (maze[cx][cy] != 'S') {
                        maze[cx][cy] = '.';
                    }
                }
            }

            // Write the solved maze to the output file
            writer = new PrintWriter(new FileWriter(outputFile));
            writer.println(height + " " + width);
            for (char[] row : maze) {
                writer.println(new String(row));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
}
