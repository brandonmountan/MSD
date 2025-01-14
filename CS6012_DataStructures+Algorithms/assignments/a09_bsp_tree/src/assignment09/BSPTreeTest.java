package assignment09;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class BSPTreeTest {

    @Test
    void testBuildTreeAndTraversal() {
        // Create test segments
        Segment s1 = new Segment(0, 0, 1, 0); // Horizontal segment
        Segment s2 = new Segment(1, 1, 2, 1); // Horizontal segment
        Segment s3 = new Segment(-1, -1, 0, -1); // Horizontal segment

        ArrayList<Segment> segments = new ArrayList<>();
        segments.add(s1);
        segments.add(s2);
        segments.add(s3);

        System.out.println(segments);

        BSPTree tree = new BSPTree(segments);

        // Traverse back-to-front from a point
        ArrayList<Segment> visitedSegments = new ArrayList<>();
        tree.traverseFarToNear(2, 2, visitedSegments::add);

        System.out.println(visitedSegments);

        // Validate the traversal order (depends on tree structure)
        assertEquals(3, visitedSegments.size());
        assertTrue(visitedSegments.contains(s1));
        assertTrue(visitedSegments.contains(s2));
        assertTrue(visitedSegments.contains(s3));
    }

    @Test
    void testInsertAndTraversal() {
        BSPTree tree = new BSPTree();

        // Create and insert segments
        Segment s1 = new Segment(0, 0, 1, 0);
        Segment s2 = new Segment(1, 1, 2, 1);
        Segment s3 = new Segment(-1, -1, 0, -1);

        tree.insert(s1);
        tree.insert(s2);
        tree.insert(s3);

        // Traverse back-to-front from a point
        ArrayList<Segment> visitedSegments = new ArrayList<>();
        tree.traverseFarToNear(2, 2, visitedSegments::add);

        // Validate traversal order
        assertEquals(3, visitedSegments.size());
        assertTrue(visitedSegments.contains(s1));
        assertTrue(visitedSegments.contains(s2));
        assertTrue(visitedSegments.contains(s3));
    }

    @Test
    void testCollisionDetection() {
        // Create test segments
        Segment s1 = new Segment(0, 0, 1, 0);
        Segment s2 = new Segment(1, 1, 2, 1);
        Segment s3 = new Segment(-1, -1, 0, -1);

        ArrayList<Segment> segments = new ArrayList<>();
        segments.add(s1);
        segments.add(s2);
        segments.add(s3);

        BSPTree tree = new BSPTree(segments);

        // Test collision with an intersecting segment
        Segment query = new Segment(0.5, 0, 0.5, -1);
        Segment collision = tree.collision(query);
        assertNotNull(collision);
        assertEquals(s1, collision);

        // Test no collision
        Segment noCollisionQuery = new Segment(3, 3, 4, 4);
        assertNull(tree.collision(noCollisionQuery));
    }

    @Test
    void testEmptyTree() {
        BSPTree tree = new BSPTree();

        // Test traversal on an empty tree
        ArrayList<Segment> visitedSegments = new ArrayList<>();
        tree.traverseFarToNear(2, 2, visitedSegments::add);
        assertTrue(visitedSegments.isEmpty());

        // Test collision detection on an empty tree
        Segment query = new Segment(0, 0, 1, 1);
        assertNull(tree.collision(query));
    }
}
