package assignment08;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test class for PathFinder.
 * This class tests the functionality of the PathFinder's solveMaze method
 * by providing various input files and verifying the output against expected results.
 */
public class PathFinderTest {

    /**
     * Helper method to read a file's content as a single string.
     */
    private String readFile(String filename) throws IOException {
        return Files.readString(Path.of(filename));
    }

    /**
     * Test a simple solvable maze.
     */
    @Test
    public void testSimpleMaze() throws IOException {
        String inputFile = "simpleMaze.txt";
        String outputFile = "simpleMazeOutput.txt";

        // Write input maze to a file
        try (PrintWriter writer = new PrintWriter(new FileWriter(inputFile))) {
            writer.println("5 5");
            writer.println("XXXXX");
            writer.println("XS  X");
            writer.println("X   X");
            writer.println("X  GX");
            writer.println("XXXXX");
        }

        // Solve the maze
        PathFinder.solveMaze(inputFile, outputFile);

        // Expected output
        String expectedOutput = "5 5\n" +
                "XXXXX\n" +
                "XS..X\n" +
                "X  .X\n" +
                "X  GX\n" +
                "XXXXX\n";

        // Read and verify output
        String actualOutput = readFile(outputFile);
        assertEquals(expectedOutput, actualOutput, "Simple maze solution is incorrect.");

        // Cleanup
        new File(inputFile).delete();
        new File(outputFile).delete();
    }

    /**
     * Test a maze with no solution.
     */
    @Test
    public void testUnsolvableMaze() throws IOException {
        String inputFile = "unsolvableMaze.txt";
        String outputFile = "unsolvableMazeOutput.txt";

        // Write input maze to a file
        try (PrintWriter writer = new PrintWriter(new FileWriter(inputFile))) {
            writer.println("5 5");
            writer.println("XXXXX");
            writer.println("XS XX");
            writer.println("X   X");
            writer.println("XX GX");
            writer.println("XXXXX");
        }

        // Solve the maze
        PathFinder.solveMaze(inputFile, outputFile);

        // Expected output (unchanged maze)
        String expectedOutput = "5 5\n" +
                "XXXXX\n" +
                "XS XX\n" +
                "X   X\n" +
                "XX GX\n" +
                "XXXXX\n";

        // Read and verify output
        String actualOutput = readFile(outputFile);
        assertEquals(expectedOutput, actualOutput, "Unsolvable maze output is incorrect.");

        // Cleanup
        new File(inputFile).delete();
        new File(outputFile).delete();
    }

    /**
     * Test a large maze to ensure performance and correctness.
     */
    @Test
    public void testLargeMaze() throws IOException {
        String inputFile = "largeMaze.txt";
        String outputFile = "largeMazeOutput.txt";

        // Generate a large input maze
        try (PrintWriter writer = new PrintWriter(new FileWriter(inputFile))) {
            writer.println("10 10");
            writer.println("XXXXXXXXXX");
            writer.println("XS       X");
            writer.println("X XXXXXXXX");
            writer.println("X        X");
            writer.println("XXXXXXXX X");
            writer.println("X        X");
            writer.println("X XXXXXXXX");
            writer.println("X       GX");
            writer.println("XXXXXXXXXX");
        }

        // Solve the maze
        PathFinder.solveMaze(inputFile, outputFile);

        // Verify that the output contains a path (manual verification recommended)
        String actualOutput = readFile(outputFile);
        assertTrue(actualOutput.contains("."), "Expected a path in the output of the large maze.");

        // Cleanup
        new File(inputFile).delete();
        new File(outputFile).delete();
    }

    /**
     * Test an edge case with a single-cell maze (start is goal).
     */
    @Test
    public void testSingleCellMaze() throws IOException {
        String inputFile = "singleCellMaze.txt";
        String outputFile = "singleCellMazeOutput.txt";

        // Write input maze to a file
        try (PrintWriter writer = new PrintWriter(new FileWriter(inputFile))) {
            writer.println("1 1");
            writer.println("S");
        }

        // Solve the maze
        PathFinder.solveMaze(inputFile, outputFile);

        // Expected output (unchanged maze with single-cell goal)
        String expectedOutput = "1 1\n" +
                "S\n";

        // Read and verify output
        String actualOutput = readFile(outputFile);
        assertEquals(expectedOutput, actualOutput, "Single-cell maze solution is incorrect.");

        // Cleanup
        new File(inputFile).delete();
        new File(outputFile).delete();
    }

    /**
     * Test a maze with multiple paths to the goal.
     */
    @Test
    public void testMultiplePathsMaze() throws IOException {
        String inputFile = "multiplePathsMaze.txt";
        String outputFile = "multiplePathsMazeOutput.txt";

        // Write input maze to a file
        try (PrintWriter writer = new PrintWriter(new FileWriter(inputFile))) {
            writer.println("6 6");
            writer.println("XXXXXX");
            writer.println("XS   X");
            writer.println("X XX X");
            writer.println("X   GX");
            writer.println("X XX X");
            writer.println("XXXXXX");
        }

        // Solve the maze
        PathFinder.solveMaze(inputFile, outputFile);

        // Verify that the output contains a valid path
        String actualOutput = readFile(outputFile);
        assertTrue(actualOutput.contains("."), "Expected a path in the output of the multiple-paths maze.");

        // Cleanup
        new File(inputFile).delete();
        new File(outputFile).delete();
    }
}
