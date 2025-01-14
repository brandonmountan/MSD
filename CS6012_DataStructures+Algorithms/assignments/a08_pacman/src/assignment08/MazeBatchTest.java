package assignment08;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * This is to test a folder of mazes with print out of how many were passed and
 * what the expected vs actual was.
 *
 */
public class MazeBatchTest {
    public static void main(String[] args) {
        String folderPath = "/Users/brandonmountan/MSD/CS6012/assignments/a08_pacman/src/mazes";
        File folder = new File(folderPath);


        File[] mazeFiles = folder.listFiles((dir, name) -> name.endsWith(".txt") && !name.contains("Sol"));


        int passed = 0;
        int failed = 0;

        for (File mazeFile : mazeFiles) {
            String baseName = mazeFile.getName().replace(".txt", "");
            File solutionFile = new File(folder, baseName + "Sol.txt");


            try {
                String outputFileName = folderPath + File.separator + baseName + "_output.txt";
                PathFinder.solveMaze(mazeFile.getAbsolutePath(), outputFileName);

                List<String> expectedOutput = Files.readAllLines(solutionFile.toPath());
                List<String> actualOutput = Files.readAllLines(Paths.get(outputFileName));

                if (expectedOutput.equals(actualOutput)) {
                    System.out.println("passed for: " + mazeFile.getName());
                    passed++;
                } else {
                    System.out.println("failed for: " + mazeFile.getName());
                    System.out.println("Expected:");
                    expectedOutput.forEach(System.out::println);
                    System.out.println("Actual:");
                    actualOutput.forEach(System.out::println);
                    failed++;
                }
                // delete if needed
                new File(outputFileName).delete();
            } catch (Exception e) {
                System.err.println("Error testing maze: " + mazeFile.getName());
                e.printStackTrace();
                failed++;
            }
        }

        System.out.println("Testing :");
        System.out.println("Total Tests: " + (passed + failed));
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
}