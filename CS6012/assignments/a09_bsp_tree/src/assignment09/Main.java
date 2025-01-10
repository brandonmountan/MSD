package assignment09;

import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Create segments for testing
        ArrayList<Segment> segments = new ArrayList<>();
        segments.add(new Segment(0, 0, 1, 0)); // Horizontal segment
        segments.add(new Segment(1, 1, 2, 1)); // Horizontal segment
        segments.add(new Segment(-1, -1, 0, -1)); // Horizontal segment

        BSPTree tree = new BSPTree(segments);

        // Traverse back to front from a point (2, 2)
        tree.traverseFarToNear(2, 2, new SegmentCallback() {
            @Override
            public void callback(Segment segment) {
                System.out.println("Visited: " + segment);
            }
        });
    }

}