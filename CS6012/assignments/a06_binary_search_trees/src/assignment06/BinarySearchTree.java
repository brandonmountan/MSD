package assignment06;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * A Binary Search Tree implementation of the SortedSet interface.
 *
 * @param <T> the type of elements maintained by this set, which must be
 *            Comparable.
 */
public class BinarySearchTree<T extends Comparable<? super T>> implements SortedSet<T> {

    /**
     * Represents a node in the binary search tree.
     */
    private class Node {
        private T element;   // The value stored in the node
        private Node left;   // Left child of the node
        private Node right;  // Right child of the node

        /**
         * Constructs a node with the specified element.
         *
         * @param element the value to be stored in this node
         */
        public Node(T element) {
            this.element = element;
        }
    }

    private Node root; // The root of the binary search tree
    private int size;  // Number of elements in the tree

    /**
     * Constructs an empty BinarySearchTree.
     */
    public BinarySearchTree() {
        root = null;
        size = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(T item) {
        if (item == null) {
            throw new NullPointerException("Item cannot be null.");
        }

        if (contains(item)) {
            return false; // Item already exists
        }

        root = addRecursive(root, item);
        size++;
        return true;
    }

    /**
     * Recursively adds an item to the binary search tree.
     *
     * @param node the current node
     * @param item the item to be added
     * @return the modified node
     */
    private Node addRecursive(Node node, T item) {
        if (node == null) {
            return new Node(item);
        }

        int compare = item.compareTo(node.element);
        if (compare < 0) {
            node.left = addRecursive(node.left, item);
        } else if (compare > 0) {
            node.right = addRecursive(node.right, item);
        }
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends T> items) {
        boolean changed = false;
        for (T item : items) {
            if (add(item)) {
                changed = true;
            }
        }
        return changed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(T item) {
        if (item == null) {
            throw new NullPointerException("Item cannot be null.");
        }

        return containsRecursive(root, item);
    }

    /**
     * Recursively checks if the binary search tree contains an item.
     *
     * @param node the current node
     * @param item the item to be checked
     * @return true if the item is found, false otherwise
     */
    private boolean containsRecursive(Node node, T item) {
        if (node == null) {
            return false;
        }

        int compare = item.compareTo(node.element);
        if (compare < 0) {
            return containsRecursive(node.left, item);
        } else if (compare > 0) {
            return containsRecursive(node.right, item);
        } else {
            return true; // Item found
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(Collection<? extends T> items) {
        for (T item : items) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T first() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException("The tree is empty.");
        }
        return findMin(root).element;
    }

    /**
     * Finds the node with the smallest value in the tree.
     *
     * @param node the current node
     * @return the node with the smallest value
     */
    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T last() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException("The tree is empty.");
        }
        return findMax(root).element;
    }

    /**
     * Finds the node with the largest value in the tree.
     *
     * @param node the current node
     * @return the node with the largest value
     */
    private Node findMax(Node node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(T item) {
        if (item == null) {
            throw new NullPointerException("Item cannot be null.");
        }

        if (!contains(item)) {
            return false;
        }

        root = removeRecursive(root, item);
        size--;
        return true;
    }

    /**
     * Recursively removes an item from the tree.
     *
     * @param node the current node
     * @param item the item to be removed
     * @return the modified node
     */
    private Node removeRecursive(Node node, T item) {
        if (node == null) {
            return null;
        }

        int compare = item.compareTo(node.element);
        if (compare < 0) {
            node.left = removeRecursive(node.left, item);
        } else if (compare > 0) {
            node.right = removeRecursive(node.right, item);
        } else {
            // Node to be removed found
            if (node.left == null && node.right == null) {
                return null; // Leaf node
            } else if (node.left == null) {
                return node.right; // One child
            } else if (node.right == null) {
                return node.left; // One child
            } else {
                // Two children
                Node successor = findMin(node.right);
                node.element = successor.element;
                node.right = removeRecursive(node.right, successor.element);
            }
        }
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection<? extends T> items) {
        boolean changed = false;
        for (T item : items) {
            if (remove(item)) {
                changed = true;
            }
        }
        return changed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<T> toArrayList() {
        ArrayList<T> list = new ArrayList<>();
        inorderTraversal(root, list);
        return list;
    }

    /**
     * Performs an in-order traversal of the tree and adds elements to a list.
     *
     * @param node the current node
     * @param list the list to store the elements
     */
    private void inorderTraversal(Node node, ArrayList<T> list) {
        if (node != null) {
            inorderTraversal(node.left, list);
            list.add(node.element);
            inorderTraversal(node.right, list);
        }
    }

    /**
     * Writes the tree in DOT format to a file.
     * This method generates a .dot file representing the tree structure, with
     * each node labeled by its element value.
     *
     * @param filename the name of the file where the DOT representation will be saved
     */
    public void writeDot(String filename) {
        try {
            PrintWriter output = new PrintWriter(new FileWriter(filename));
            output.println("digraph BST {"); // Using digraph for directed edges
            if (root != null) {
                writeDotRecursive(root, output);
            }
            output.println("}");
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recursively traverses the tree and writes its nodes and edges in DOT format.
     *
     * @param node   the current node
     * @param output the PrintWriter to write to the file
     */
    private void writeDotRecursive(Node node, PrintWriter output) {
        if (node == null) {
            return;
        }

        // Write current node
        output.println("  " + node.hashCode() + " [label=\"" + node.element + "\"];");

        // Write left child edge if exists
        if (node.left != null) {
            output.println("  " + node.hashCode() + " -> " + node.left.hashCode() + ";");
            writeDotRecursive(node.left, output); // Recurse on left subtree
        }

        // Write right child edge if exists
        if (node.right != null) {
            output.println("  " + node.hashCode() + " -> " + node.right.hashCode() + ";");
            writeDotRecursive(node.right, output); // Recurse on right subtree
        }
    }
}
