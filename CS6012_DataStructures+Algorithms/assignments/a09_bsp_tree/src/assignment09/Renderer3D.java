package assignment09;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class Renderer3D extends JPanel {
    /*

    Algorithm description

    for the 2 endpoints of the segment, compute the x coordinate with:

    pVec = vector from eye to point
    fovPerp = vector at the depth of p across the FOV cone
    eyeCenter = eye to center of FOV of depth at p
    fovPerp = 2* eyeCenter.perp & depth*tangent(fovAngle/2)

    x = pVec.dot(fovPerp)/||fovPerp||^2 * windowWidth

    Y is TBD, but just drawing from eye height to top/bottom of the wall and considering FOV

    Eventually we'll draw a quad using drawPolygon, which should end up looking like a parallelogram

     */

    private BSPTree tree;
    private Point2D.Double eye = new Point2D.Double(0.5, 0.5f);
    private double eyeAngle = Math.PI/2; //looking "north" to start
    private final double eyeHeight = 0.0003;
    private final double wallHeight = 0.0005;

    private final double stepLength = 0.01;

    private final double rotationAmount = Math.PI/16;// 16 steps to spin in a circle
    private final double fov = Math.PI/4; //45 degree field of view
    private Map<Segment, Color> segmentColors;

    public void setOnAngleChange(DoubleConsumer onAngleChange) {
        this.onAngleChange = onAngleChange;
    }

    private DoubleConsumer onAngleChange;

    public void setOnPositionChange(Consumer<Point2D.Double> onPositionChange) {
        this.onPositionChange = onPositionChange;
    }

    private Consumer<Point2D.Double> onPositionChange;

    final static int windowSize = 800;
    public Renderer3D(BSPTree tree){
        this.tree = tree;

        segmentColors = new HashMap<>();
        var rand = new Random();
        tree.traverseFarToNear(eye.x, eye.y, s -> {
            //todo, pick better colors
            segmentColors.put(s, new Color(rand.nextFloat(),rand.nextFloat(),rand.nextFloat()));
        });

        JFrame f = new JFrame("3D BSP Renderer");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(windowSize, windowSize);
        f.add(this);
        f.pack();
        f.setVisible(true);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                handleKey(e.getKeyChar());
            }
        });
        this.setFocusable(true);
        this.requestFocusInWindow();

        repaint();
    }


    private void handleKey(char key){
        switch (key) {
            case 'q' -> setEyeAngle(eyeAngle + rotationAmount);
            case 'e' -> setEyeAngle(eyeAngle - rotationAmount);
            case 'w' -> walk(true);
            //forwards
            case 's' -> walk(false);
            //backwards
            case 'a' -> strafe(false);
            case 'd' -> strafe(true);
        }
        repaint();
    }

    private void setEyeAngle(double newAngle){
        eyeAngle = newAngle;
        if(onAngleChange != null){
            onAngleChange.accept(eyeAngle);
        }
    }

    private void walk(boolean forward){
        var stepLengthWithDirection = forward ? stepLength : -stepLength;
        var newEyeX = eye.x + stepLengthWithDirection*Math.cos(eyeAngle);
        var newEyeY = eye.y + stepLengthWithDirection*Math.sin(eyeAngle);

        tryToMove(newEyeX, newEyeY);
    }

    private void strafe(boolean right){
        var stepLengthWithDirection = right ? -stepLength : stepLength;
        var newEyeX = eye.x - stepLengthWithDirection*Math.sin(eyeAngle);
        var newEyeY = eye.y + stepLengthWithDirection*Math.cos(eyeAngle);

        tryToMove(newEyeX, newEyeY);
    }

    //clamp to range 0..1 and make sure we don't collide with any segments
    private void tryToMove(double newEyeX, double newEyeY) {
        newEyeX = Math.max(0, Math.min(1.0, newEyeX));
        newEyeY = Math.max(0, Math.min(1.0, newEyeY));

        if(tree.collision(new Segment(eye.x, eye.y, newEyeX, newEyeY)) == null){
            eye.x = newEyeX;
            eye.y = newEyeY;
            if(onPositionChange != null){
                onPositionChange.accept(eye);
            }
        }
    }



    @Override
    public Dimension getPreferredSize() {
        return new Dimension(windowSize,windowSize);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.clearRect(0,0,windowSize,windowSize);

        tree.traverseFarToNear(eye.x, eye.y, s -> {
            drawSegment(g2, s);
        });

    }

    private void drawSegment(Graphics2D g2, Segment s) {

        //check if both endpoints are "in front of" or "behind"
        //the camera
        //only draw segments where at least is in front of the camera


        //not needed since the left/right FOV edges will catch anything behind us
        //doesn't hurt anything to do this check, and is maybe faster than the other ones, :shrug:
        Vec2D eyeUnit = new Vec2D(Math.cos(eyeAngle), Math.sin(eyeAngle));
        Vec2D eyeA = new Vec2D(s.x1() - eye.x, s.y1() - eye.y);
        Vec2D eyeB = new Vec2D(s.x2() - eye.x, s.y2() - eye.y);

        if(eyeA.dot(eyeUnit) < 0 && eyeB.dot(eyeUnit) < 0){
            //System.out.println("skipping " + s.toString());
            return; //both behind camera, skip it
        }

        var color = segmentColors.get(s);
        g2.setColor(color);


        //create segments from the eye along the left and right edges of the field of view
        //we'll use those segments to "clip" segments so we only draw the part in the viewing region
        var fovDepth = 5.0; //comfortably outside of the scene
        Vec2D fovLeft = new Vec2D(fovDepth*Math.cos(eyeAngle + fov/2),
                fovDepth* Math.sin(eyeAngle + fov/2));
        Vec2D fovRight = new Vec2D(fovDepth*Math.cos(eyeAngle - fov/2),
                fovDepth* Math.sin(eyeAngle - fov/2));

        var fovLeftSeg = Vec2D.pointWithDirection(eye, fovLeft);
        var fovRightSeg = Vec2D.pointWithDirection(eye, fovRight);

        //make sure it's in the view
        var leftSide = fovLeftSeg.whichSide(s);
        if(leftSide > 0){
            //System.out.println("skipping " + s + "because it's entirely too far left of the FOV");
            return;
        }
        var rightSide = fovRightSeg.whichSide(s);
        if(rightSide < 0){
            //System.out.println("skipping " + s + "because it's entirely too far right of the FOV");
            return;
        }

        if(leftSide == 0 && fovLeftSeg.intersects(s)) {
            //clip it to the right side
            var pieces = fovLeftSeg.split(s);
            s = pieces[0]; //the '-' side piece is "right" of the fov segment
        }
        if(rightSide == 0 && fovRightSeg.intersects(s)){
            var pieces = fovRightSeg.split(s);
            s = pieces[1]; //the '+' side piece is "left" of the fov segment
        }




        var x1 = (int)(getProjectedX(s.x1(), s.y1())*windowSize);
        var y1Low = (int)((1 - getProjectedY(s.x1(), s.y1(), 0))*windowSize);
        var y1High = (int)((1 - getProjectedY(s.x1(), s.y1(), wallHeight))*windowSize);

        var x2 = (int)(getProjectedX(s.x2(), s.y2())*windowSize);
        var y2Low = (int)((1 - getProjectedY(s.x2(), s.y2(), 0))*windowSize);
        var y2High = (int)((1 - getProjectedY(s.x2(), s.y2(), wallHeight))*windowSize);

        //System.out.println(s.toString() + " " + x1 + " " + x2 + " " + y1Low + " " + y1High + " " + y2Low + " " + y2High + " " + color);

        //just draw it as a quad.  Vertices are arranged around the border, which is why y2high/low seem swapped
        g2.fill(new Polygon(new int[]{x1, x1, x2, x2},
                        new int[]{y1Low, y1High, y2High, y2Low},
                        4));

    }



    //get the "projected x coordinate" of a point
    //it's a float specifying where it appears on the screen
    //0 = left edge, 1 = right edge
    //negative would be offscreen left, etc
    double getProjectedX(double px, double py){
        Vec2D eyeUnit = new Vec2D(Math.cos(eyeAngle), Math.sin(eyeAngle));
        Vec2D eyeP = new Vec2D(px - eye.x, py - eye.y);

        //depth is projection of eyP onto eyeUnit
        var depth = eyeP.dot(eyeUnit);
        var eyePerp = eyeUnit.perp();

        var halfWidthAtDepth = Math.tan(fov/2)*depth;
        //want this going left to right, hence the minus
        var eyeAtDepthToTheRight = eyePerp.scaled(-halfWidthAtDepth);

        //projection of eyeP onto acrossVisionAtDepth = b*(a.b)/(b.b)
        //we only care about the fraction along B, so it's the quotient
        //of the 2 dot products
        //add 0.5 since the eye at depth will be the center of the screen
        //and projection of length 1 would be equivalent to going across
        //half the screen
        var vecDot = eyeP.dot(eyeAtDepthToTheRight);
        var fovVecMag = eyeAtDepthToTheRight.squaredMag();

        return 0.5*(vecDot/fovVecMag) + 0.5;

    }

    //same as px, but we only care about depth into the screen
    //z is height
    double getProjectedY(double px, double py, double z){

        Vec2D eyeUnit = new Vec2D(Math.cos(eyeAngle), Math.sin(eyeAngle));
        Vec2D eyeP = new Vec2D(px - eye.x, py - eye.y);

        //depth is projection of eyP onto eyeUnit
        var depth = eyeP.dot(eyeUnit);
        var halfWidthAtDepth = Math.tan(fov/2)*depth;

        //eye is just pointing up by HWAD
        //so projection is just (z - eye height)/halfWidthAtDepth

        var heightDiff = z - eyeHeight;
        var halfFraction = heightDiff/(halfWidthAtDepth*halfWidthAtDepth);

        return 0.5*halfFraction + 0.5;

    }


    public static void main(String[] args){
//        var segments = new ArrayList<>(Arrays.asList(
//                new Segment(.15, .89, .6, .8),
//                new Segment(.5, .75, .65, .89),
//                new Segment(-0.05, 0.9, 1.05, 0.9)
//
//
//        ));

        var segments = new ArrayList<Segment>();
        for(int i = 0; i < 10; i++){
            segments.add(new Segment(Math.random(),Math.random(),Math.random(),Math.random()));
        }
        var tree = new BSPTree(segments);
        Renderer3D r3 = new Renderer3D(tree);
        Renderer2D r2 = new Renderer2D(tree);
        r3.setOnAngleChange(r2::setEyeAngle);
        r3.setOnPositionChange(r2::setEyePosition);
        /*SwingUtilities.invokeLater(() ->{
            r3
            //draw the 2D renderer so you get a sense for the scene
            r2 = new Renderer2D(tree);
        });*/
    }

}
