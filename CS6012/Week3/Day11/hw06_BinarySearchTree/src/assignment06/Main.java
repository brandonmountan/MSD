package assignment06;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Create a new BinarySearchTree instance
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();

        // Add some elements to the tree
        bst.addAll(Arrays.asList(10, 5, 15, 3, 7, 12, 18));

        // Generate the .dot file
        bst.writeDot("binarySearchTree.dot");

        System.out.println("DOT file generated successfully!");
    }
}
