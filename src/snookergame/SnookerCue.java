package snookergame;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

/**
 * The SnookerCue is responsible for attributing the speed and user interaction
 * with the white ball. It is also physically represented on the screen as a
 * line.
 *
 * @author DominicWild
 */
public class SnookerCue extends dist.Line implements MouseMotionListener {

    private SnookerTable table;                 //The table this snooker cue is associated with.
    private double length, gradient;            //The length of the line and the gradient it is at.

    /**
     * Creates a new instance of a SnookerCue object.
     *
     * @param t The table this snooker cue is on.
     * @param length The length of this cue.
     * @param thickness The thickness of this cue.
     * @param col The colour of the cue, as a string.
     * @param lay The layer the cue is to be drawn on.
     */
    public SnookerCue(SnookerTable t, double length, double thickness, String col, int lay) {
        super((t.getWhiteBall().getXPosition() - t.getWhiteBall().getSize() / 2) - length, t.getWhiteBall().getYPosition(), t.getWhiteBall().getXPosition() - t.getWhiteBall().getSize() * 0.75, t.getWhiteBall().getYPosition(), thickness, col, lay);
        this.length = length;
        this.table = t;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        alignCue(e.getPoint()); //Align the cue with the current mouse position.
        //Calculate speeds for x and y directions based on distance from center of the white ball.
        double xSpeed = Math.abs(table.getWhiteBall().getXPosition() - e.getX()) / GameInstance.speedDivFactor;
        double ySpeed = Math.abs(table.getWhiteBall().getYPosition() - e.getY()) / GameInstance.speedDivFactor;
        if (xSpeed > GameInstance.maxXSpeed) { //Limit speed to the max speed for game instances.
            xSpeed = GameInstance.maxXSpeed;
        }
        if (ySpeed > GameInstance.maxYSpeed) {
            ySpeed = GameInstance.maxYSpeed;
        }
        table.getInfoComp().setSpeedText(xSpeed, ySpeed); //Update the speed text to reflect this,
    }

    /**
     * Aligns cue with a point P on the table.
     *
     * @param p The point to align the cue to. I.e., a line pointing towards.
     */
    private void alignCue(Point p) {
        Point2D.Double c = table.getWhiteBall().getCenterPoint();
        gradient = calculateGradient(p); //Used to make the line,
        double size = table.getWhiteBall().getSize();
        Point2D.Double cueEnd = new Point2D.Double(c.x, c.y);
        Point2D.Double cueBegin = null;
        double step = 0.01; //The amount to step through the while loop with.
        if (isMouseLeftBall(p.x)) { //Determines if mouse is on the left or right of the ball.
            step *= -1; //Requires generation of line from opposite end, if so.
        }
        while (cueEnd.distance(c) < size / 1.5 + length) { //While the end of the cue hasn't reached the math length its meant to be away from the ball.
            cueEnd.x += step;
            cueEnd.y += step * gradient; //Step gradually further and futher outward from the ball.
            if (cueEnd.distance(c) > size / 1.5 && cueBegin == null) { //Assign cue begin once reached a certain distance.
                cueBegin = new Point2D.Double(cueEnd.x, cueEnd.y);
            }
        }
        //Set line to designated two points.
        this.setLinePosition(cueBegin.x, cueBegin.y, cueEnd.x, cueEnd.y);
    }

    /**
     * Determines if the mouse X coordinate is to the left of the ball or not.
     *
     * @param mouseX The mouse X coordinate
     * @return A boolean representing if the ball is to the left or not.
     */
    private boolean isMouseLeftBall(double mouseX) {
        return mouseX < table.getWhiteBall().getCenterPoint().x;
    }

    /**
     * Calculates the gradient between a point P and the center of the white
     * ball.
     *
     * @param p The point to calculate the gradient to from the white ball.
     * @return The gradient as a double.
     */
    private double calculateGradient(Point p) {
        Point2D.Double c = table.getWhiteBall().getCenterPoint();
        Point2D.Double mousePos = new Point2D.Double(p.x, p.y);
        double diffX = c.x - mousePos.x, diffY = c.y - mousePos.y;
        return diffY / diffX;
    }

    /**
     * Gets the current gradient the Cue is at.
     *
     * @return The current gradient the Cue is at.
     */
    public double getGradient() {
        return gradient;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.mouseMoved(e);
    }
}
