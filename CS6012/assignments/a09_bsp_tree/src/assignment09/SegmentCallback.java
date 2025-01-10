package assignment09;

/**
 * The traversal algorithm will accept a callback to be performed for each segment visited
 *
 * Basically this is a user-provided "visit" method and the demo applications will pass visitors like:
 * * draw a line segment
 * * draw a 3D rendering of the segment
 * * add the segment to a list
 * * etc
 *
 * Your traversal code should call the callback method on the segments you visit
 */
public interface SegmentCallback {
    void callback(Segment s);
}
