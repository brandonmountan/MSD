package assignment09;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BSPTree {
    private BSPNode root;
    Random rand = new Random(42);

    // create empty tree. nothing in it yet
    public BSPTree() {
        this.root = null;
    }

    // take your list of segments and automatically organize them into the tree structure using buildTree
    public BSPTree(ArrayList<Segment> segments) {
        buildTree(segments);
    }

    // main method for building the tree
    private BSPNode buildTree(ArrayList<Segment> segments) {
        // if list of segments is empty, the is no tree to build, so set tree to be empty (root = null)
        if (segments.isEmpty()) {
            root = null;
            return new BSPNode();
        }

        // if there are segments, pick one segment at random to be splitter
        int randomIndex = rand.nextInt(segments.size());
        Segment splittingSegment = segments.get(randomIndex);
        ArrayList<Segment> left = new ArrayList<>();
        ArrayList<Segment> right = new ArrayList<>();
        ArrayList<Segment> straddling = new ArrayList<>();

        // partition
        // check where each segment belongs
        for (Segment segment : segments) {
            int side = splittingSegment.whichSide(segment);
            // right
            if (side == 1) {
                right.add(segment);
                // left
            } else if (side == -1) {
                left.add(segment);
                // segment crosses splitter (partly on each side)
            } else {
                straddling.add(segment);
            }
        }

        // Create node in the tree for splitter
        root = new BSPNode(splittingSegment);

        // Recursively build smaller trees for left and right sides
        root.behind = new BSPTree(left);
        root.front = new BSPTree(right);

        // for straddling segments, split them and add pieces to lef or right trees as needed
        if (!straddling.isEmpty()) {
            for (Segment segment : straddling) {
                if (splittingSegment.whichSide(segment) == 1) {
                    root.front.insert(segment);
                } else {
                    root.behind.insert(segment);
                }
            }
        }
    }

    // so we can add segment to tree after it's built
    public void insert(Segment segment) {
        if (root == null) {
            root = new BSPNode(segment);
        } else {
            insert(root, segment);
        }
    }

    // private helper method
    private void insert(BSPNode node, Segment segment) {
        if (node == null) {
            return; // Should not happen due to the method call structure
        }

        int side = node.splitter.whichSide(segment);
        if (side > 0) {
            if (node.front == null) {
                node.front = new BSPTree();
            }
            node.front.insert(segment);
        } else if (side < 0) {
            if (node.behind == null) {
                node.behind = new BSPTree();
            }
            node.behind.insert(segment);
        }
    }

    // "walk through" the tree
    // start with segments that are farthest away from the point (x, y)
    public void traverseFarToNear(double x, double y, SegmentCallback callback) {
        Set<Segment> visited = new HashSet<>();
        traverseFarToNear(root, x, y, callback, visited);
    }

    private void traverseFarToNear(BSPNode node, double x, double y, SegmentCallback callback, Set<Segment> visited) {
        if (node == null) {
            return;
        }

        int side = node.splitter.whichSidePoint(x, y);

        if (side > 0) {
            // Point is "in front" of the splitter; visit far (right) first
            traverseFarToNear(node.front != null ? node.front.root : null, x, y, callback, visited);
            if (!visited.contains(node.splitter)) {
                visited.add(node.splitter);
                callback.callback(node.splitter);
            }
            traverseFarToNear(node.behind != null ? node.behind.root : null, x, y, callback, visited);
        } else {
            // Point is "behind" the splitter; visit far (left) first
            traverseFarToNear(node.behind != null ? node.behind.root : null, x, y, callback, visited);
            if (!visited.contains(node.splitter)) {
                visited.add(node.splitter);
                callback.callback(node.splitter);
            }
            traverseFarToNear(node.front != null ? node.front.root : null, x, y, callback, visited);
        }
    }

    // check if given segment overlaps (collides) with any segment in tree
    public Segment collision(Segment query) {
        return collision(root, query);
    }

    // look at segments stored in the tree and compare them to the query segment
    private Segment collision(BSPNode node, Segment query) {
        if (node == null) {
            return null;
        }
        // keep checking other parts of the tree until collision found or finished checking

        // Recur to the side where the query segment lies
        int side = node.splitter.whichSide(query);
        if (side > 0 && node.front != null) {
            return collision(node.front.root, query);
        } else if (side < 0 && node.behind != null) {
            return collision(node.behind.root, query);
        }

        // if query collides with segment, return segment
        if (node.splitter.intersects(query)) {
            // Check if the intersection occurs with this splitter
            return node.splitter;
        }

        return null; // No collision detected
    }

    // Node = a piece of the tree
    private static class BSPNode {
        Segment splitter;
        BSPTree behind;
        BSPTree front;

        public BSPNode(Segment splitter) {
            this.splitter = splitter;
            this.behind = new BSPTree();
            this.front = new BSPTree();
        }
    }
}