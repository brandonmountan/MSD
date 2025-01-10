package assignment09;


/**
 * Record representing a line segment in the plane
 * A record is basically a class where all members are public + final
 * So some of the boilerplate (getters, constructor, hashCode, equals, toString) are unnecessary
 * You'll probably need to use the public methods of this class
 * in your BSP Tree class
 *
 */
public record Segment(double x1, double y1, double x2, double y2) {
	//you can access these coordinates via getter methods x1(), x2(), etc


	/**
	 * The convention here is "positive points" are on the left if we look from p1 to p2
	 * But that doesn't really matter.  For your BSP trees, you can say that
	 * "negative" points will be in the left subtree and positive ones will be in the right subtree
	 * @param x
	 * @param y
	 * @return which side of the segment the point is on, the + side or the - side
	 * the + side is "in front of" the segment, the "-" side is behind it
	 */

	public int whichSidePoint(double x, double y) {
		double nx = y1 - y2; //-y
		double ny = x2 - x1; //+x
		double lx = x - x1;
		double ly = y - y1;

		return (int) Math.signum(nx * lx + ny * ly);
	}

	/**
	 *
	 * @param other
	 * @return  which side of "this" is the segment "other" on?
	 * 	1 -- both points are "in front of" this
	 * 	-1 -- both points are "behind" this
	 * 	0 -- one endpoint is on each side of this
	 */

	public int whichSide(Segment other) {


		int side1 = whichSidePoint(other.x1, other.y1);
		int side2 = whichSidePoint(other.x2, other.y2);

		if (side1 > 0 && side2 > 0) {
			return 1; //+ side, both points are in front
		} else if (side1 < 0 && side2 < 0) {
			return -1; //- side, both points are behind
		} else {
			return 0; //straddling, one endpoint is on each side, or one lies on the segment
		}
	}


	//extending this and other out as infinite lines, specifies where this crosses other
	//ie, if this goes through other.start, this method returns 0
	//if this goes through other.end, it returns 1
	//if it crosses exactly at the midpoint between them, it returns 0.5
	//if the return value is not in [0,1], then the segments don't intersect
	private double crossingBarycenter(Segment other) {

		//based on the answer from here:
		//http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect


		//vector between endpoints of other
		double rx = other.x2 - other.x1;
		double ry = other.y2 - other.y1;

		//vector between endpoints of this
		double sx = x2 - x1;
		double sy = y2 - y1;

		//v × w to be vx wy − vy wx
		double rxs = rx * sy - ry * sx;

		double qps = (x1 - other.x1) * sy - (y1 - other.y1) * sx;

		//compute where this crosses other
		return qps / rxs;
	}

	/**
	 * Do the segments intersect?
	 * @param other the other segment being tested
	 * @return true if they intersect
	 */
	public boolean intersects(Segment other) {
		var b1 = crossingBarycenter(other);
		var b2 = other.crossingBarycenter(this);
		//the intersect if the barycentric coordinate is between 0 and 1 for both segments
		return b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1;
	}


	/**
	 * Splits other where it crosses the infinitely extended version of "this"
	 * @param other the segment that's being split
	 * @return Array of the 2 pieces.  The first point in the array is on the '-' side,
	 *  the second is on the '+' side, using the convention from "whichSide"
	 */
	public Segment[] split(Segment other) {
		//return 2 points
		Segment[] ret = new Segment[2];

		var t = crossingBarycenter(other);

		double splitX = other.x1 + t * (other.x2 - other.x1);
		double splitY = other.y1 + t * (other.y2 - other.y1);

		//return them so the first segment is on the - side of this,
		//and the second is on the + side
		if (this.whichSidePoint(other.x1, other.y1) < 0) {
			ret[0] = new Segment(other.x1, other.y1, splitX, splitY);
			ret[1] = new Segment(splitX, splitY, other.x2, other.y2);
		} else {
			//return the - side first
			ret[1] = new Segment(other.x1, other.y1, splitX, splitY);
			ret[0] = new Segment(splitX, splitY, other.x2, other.y2);
		}
		return ret;
	}

}
