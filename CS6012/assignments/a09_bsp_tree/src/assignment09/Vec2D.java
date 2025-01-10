package assignment09;


import java.awt.geom.Point2D;

/**
 * Basic 2D vector class with just enough for the 3D renderer to work
 */
public class Vec2D {
    @Override
    public String toString() {
        return "Vec2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public double x, y;
    public Vec2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    //returns a vec rotated 90 degrees CCW
    public Vec2D perp(){
        return new Vec2D(-y, x);
    }

    public double dot(Vec2D b){
        return x*b.x + y*b.y;
    }

    public double squaredMag(){
        return dot(this);
    }

    public double mag(){
        return Math.sqrt(squaredMag());
    }

    public Vec2D plus(Vec2D b){
        return new Vec2D(x + b.x, y + b.y);
    }

    public Vec2D scaled(double s){
        return new Vec2D(x*s, y*s);
    }

    public static Segment pointWithDirection(Point2D.Double p, Vec2D dir){
        return new Segment(p.x, p.y, p.x + dir.x, p.y + dir.y);
    }
}
