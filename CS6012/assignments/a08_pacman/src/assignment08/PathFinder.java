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
        try {
            // Read the maze from the file
            List<String> mazeLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    mazeLines.add(line);
                }
            }

            // Parse maze dimensions
            String[] dimensions = mazeLines.get(0).split(" ");
            int height = Integer.parseInt(dimensions[0]);
            int width = Integer.parseInt(dimensions[1]);

            // Parse maze into a grid
            char[][] maze = new char[height][width];
            int startX = -1, startY = -1, goalX = -1, goalY = -1;
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
            boolean[][] visited = new boolean[height][width];
            int[][] prev = new int[height * width][2];
            for (int[] p : prev) Arrays.fill(p, -1);

            Queue<int[]> queue = new LinkedList<>();
            queue.add(new int[]{startX, startY});
            visited[startX][startY] = true;

            int[] dx = {-1, 1, 0, 0}; // Movement directions
            int[] dy = {0, 0, -1, 1};

            boolean pathFound = false;
            while (!queue.isEmpty()) {
                int[] current = queue.poll();
                int x = current[0];
                int y = current[1];

                if (x == goalX && y == goalY) {
                    pathFound = true;
                    break;
                }

                for (int d = 0; d < 4; d++) {
                    int nx = x + dx[d];
                    int ny = y + dy[d];

                    if (nx >= 0 && ny >= 0 && nx < height && ny < width && !visited[nx][ny] && maze[nx][ny] != 'X') {
                        queue.add(new int[]{nx, ny});
                        visited[nx][ny] = true;
                        prev[nx * width + ny] = new int[]{x, y};
                    }
                }
            }

            // If a path was found, reconstruct it
            if (pathFound) {
                int cx = goalX, cy = goalY;
                while (cx != startX || cy != startY) {
                    int[] p = prev[cx * width + cy];
                    cx = p[0];
                    cy = p[1];
                    if (maze[cx][cy] != 'S') {
                        maze[cx][cy] = '.';
                    }
                }
            }

            // Write the solved maze to the output file
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                writer.println(height + " " + width);
                for (char[] row : maze) {
                    writer.println(new String(row));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
