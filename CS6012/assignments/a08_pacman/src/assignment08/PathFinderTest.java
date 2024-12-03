package assignment08;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the PathFinder class.
 */
public class PathFinderTest {

    private static final String INPUT_FILE = "testInputMaze.txt";
    private static final String OUTPUT_FILE = "testOutputMaze.txt";

    @AfterEach
    void cleanup() throws IOException {
        // Delete temporary files after each test
        Files.deleteIfExists(Path.of(INPUT_FILE));
        Files.deleteIfExists(Path.of(OUTPUT_FILE));
    }

    @Test
    void testSolveMazeWithValidPath() throws IOException {
        // Create a test input maze
        String inputMaze = "5 5\n" +
                "S....\n" +
                ".XXXX\n" +
                "....X\n" +
                "XXXX.\n" +
                "...G.\n";

        // Write the input maze to a temporary file
        Files.writeString(Path.of(INPUT_FILE), inputMaze);

        // Solve the maze
        PathFinder.solveMaze(INPUT_FILE, OUTPUT_FILE);

        // Read the solved maze
        List<String> solvedMaze = Files.readAllLines(Path.of(OUTPUT_FILE));

        // Expected output maze
        String[] expectedMaze = {
                "5 5",
                "S....",
                ".XXXX",
                "....X",
                "XXXX.",
                "...G."
        };

        // Validate the solution
        assertArrayEquals(expectedMaze, solvedMaze.toArray());
    }

    @Test
    void testSolveMazeWithNoPath() throws IOException {
        // Create a test input maze with no valid path
        String inputMaze = "5 5\n" +
                "SXXXX\n" +
                "XXXXX\n" +
                "XXXXX\n" +
                "XXXXX\n" +
                "XXXG.\n";

        // Write the input maze to a temporary file
        Files.writeString(Path.of(INPUT_FILE), inputMaze);

        // Solve the maze
        PathFinder.solveMaze(INPUT_FILE, OUTPUT_FILE);

        // Read the solved maze
        List<String> solvedMaze = Files.readAllLines(Path.of(OUTPUT_FILE));

        // Expected output maze (no changes, as there's no path)
        String[] expectedMaze = {
                "5 5",
                "SXXXX",
                "XXXXX",
                "XXXXX",
                "XXXXX",
                "XXXG."
        };

        // Validate the solution
        assertArrayEquals(expectedMaze, solvedMaze.toArray());
    }

    @Test
    void testSolveMazeWithStartEqualsGoal() throws IOException {
        // Create a test input maze where start equals goal
        String inputMaze = "1 1\n" +
                "S\n";

        // Write the input maze to a temporary file
        Files.writeString(Path.of(INPUT_FILE), inputMaze);

        // Solve the maze
        PathFinder.solveMaze(INPUT_FILE, OUTPUT_FILE);

        // Read the solved maze
        List<String> solvedMaze = Files.readAllLines(Path.of(OUTPUT_FILE));

        // Expected output maze (unchanged)
        String[] expectedMaze = {
                "1 1",
                "S"
        };

        // Validate the solution
        assertArrayEquals(expectedMaze, solvedMaze.toArray());
    }
}
