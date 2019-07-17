package snookergame;

import dist.Circle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * This class represents a SnookerBall on the SnookerTable. It hold information
 * about the ball and is responsible for moving, its own collision and keep
 * track of its state.
 *
 * @author DominicWild
 */
public class SnookerBall extends dist.Circle implements Runnable {

    private double xSpeed;				// The effective speed of this ball in the X axis.
    private double ySpeed;				// The effective speed of this ball in the Y axis.
    private Point2D.Double prevPoint;                   //The previous point this ball was at before the move() was called.
    private State ballState = State.STATIONARY;         //The state the ball currently is.
    private SnookerTable table;                         //The table this ball is on.
    private final int score;                                  //The score this ball is worth.

    @Override
    public void run() {
        enterMovePhase();
    }

    /**
     * Begins handling move information and setting variables accordingly.
     */
    private void enterMovePhase() {
        if (this.getXPosition() > 50 && this.getYPosition() > 50) {
            prevPoint = new Point2D.Double(this.getXPosition(), this.getYPosition());
        }
        while (table.isABallMoving()) { //Keep attempting to move the ball until all balls are not moving.
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {

            }
            this.move();
        }
    }

    /**
     * Checks if a ball has hit a boundary (side of the table).
     */
    private void boundaryCheck() {
        double inX, inY; //The x and y coordinates that are out of bounds, if not they're in bounds = -1.
        double x = this.getXPosition(); //Assign short hand for current values.
        double y = this.getYPosition();
        double r = this.getSize() / 2; //Radius
        Point2D.Double[] bP = table.getBoundPoints(); //The bounds of the table. Upper left and bottom right point of the rectangle.
        inX = this.inBoundX(bP[0].x, bP[1].x);
        inY = this.inBoundY(bP[0].y, bP[1].y);
        if (!(inX == -1) && !(inY == -1)) { //If both x and y are out of bounds.
            this.setSpeed(this.getXSpeed() * -1, this.getYSpeed() * -1); //Invert both speeds direction.
            if (bP[1].x > inX && bP[1].y > inY) { //Set center point based on where specifically out of bounds on the board the ball is.
                this.setPosition(x + r, y + r);
            } else if (bP[1].x > inX && bP[0].y < inY) {
                this.setPosition(x + r, y - r);
            } else if (bP[0].x < inX && bP[0].y < inY) {
                this.setPosition(x - r, y - r);
            } else if (bP[0].x < inX && bP[1].y > inY) {
                this.setPosition(x - r, y + r);
            }
        } else if (!(inX == -1)) { //If only X is out of bounds.
            this.setSpeed(this.getXSpeed() * -1, this.getYSpeed());
            if (bP[0].x >= inX) { //Set center point based on where specifically out of bounds on the board the ball is.
                this.setXPosition(bP[0].x + r);
            } else if (bP[1].x <= inX) {
                this.setXPosition(bP[1].x - r);
            }
        } else if (!(inY == -1)) { //If only Y is out of bounds.
            this.setSpeed(this.getXSpeed(), this.getYSpeed() * -1);
            if (bP[0].y >= inY) { //Set center point based on where specifically out of bounds on the board the ball is.
                this.setYPosition(bP[0].y + r);
            } else if (bP[1].y <= inY) {
                this.setYPosition(bP[1].y - r);
            }
        }
    }

    /**
     * Sets the holistic position of this ball.
     *
     * @param x X position to set to.
     * @param y Y position to set to.
     */
    public void setPosition(double x, double y) {
        this.setXPosition(x);
        this.setYPosition(y);
    }

    /**
     * Defines the state of a ball.
     */
    public enum State {

        /**
         * MOVING - The ball currently has a speed and is being moved on the
         * table.
         */
        MOVING,
        /**
         * POCKETED - The ball is currently off the board, in a pocketed state.
         * 0 speed, (0,0) in coordinates.
         */
        POCKETED,
        /**
         * STATIONARY - The ball is currently on the board, with 0 speed.
         */
        STATIONARY
    };

    /**
     * Constructs a SnookerBall with a given (x,y) coordinate, diameter, colour,
     * layer and associates it with a table and a score.
     *
     * @param x X coordinate this ball starts at.
     * @param y Y coordinate this ball starts at.
     * @param diameter The diameter of how large this ball should be.
     * @param col The colour of this ball.
     * @param lay The layer this ball should be painted on.
     * @param s The SnoookerTable that this ball resides on.
     * @param score The score this ball is worth.
     */
    public SnookerBall(double x, double y, double diameter, String col, int lay, SnookerTable s, int score) {
        super(x, y, diameter, col, lay);
        this.score = score;
        this.table = s;
    }

    /**
     * Calculates and returns the current hit box for this SnookerBall.
     *
     * @return An array of points that defines the hit box for this SnookerBall.
     */
    public Point2D.Double[] calculateHitBox() {
        /**
         * Defines 4 points, separated by r in both the x and y direction from
         * the center point of the SnookerBall.
         */
        final int TOP = 0, LEFT = 1, BOTTOM = 2, RIGHT = 3;
        double x = this.getXPosition(), y = this.getYPosition(), r = this.getSize() / 2;
        Point2D.Double[] hitBox = new Point2D.Double[4];
        hitBox[TOP] = new Point2D.Double(x, y + r);
        hitBox[LEFT] = new Point2D.Double(x - r, y);
        hitBox[BOTTOM] = new Point2D.Double(x, y - r);
        hitBox[RIGHT] = new Point2D.Double(x + r, y);
        return hitBox;
    }

    /**
     * Gets the x coordinate that is laying out of bounds of the x1 and x2
     * coordinates, from the balls hit box. If all points in the hit box are
     * within bounds, return -1.
     *
     * @param x1 The first bound point.
     * @param x2 The second bound point.
     * @return The x coordinate that is out of bounds or -1 if not out of
     * bounds.
     */
    public double inBoundX(double x1, double x2) {
        Point2D.Double[] points = calculateHitBox();
        for (int i = 0; i < 4; i++) {
            if (!(x1 < points[i].x && x2 > points[i].x)) { //If not in bounds.
                return points[i].x;
            }
        }
        return -1;
    }

    /**
     * Gets the Y coordinate that is laying out of bounds of the x1 and x2
     * coordinates, from the balls hit box. If all points in the hit box are
     * within bounds, return -1.
     *
     * @param y1 The first bound point.
     * @param y2 The second bound point.
     * @return The x coordinate that is out of bounds or -1 if not out of
     * bounds.
     */
    public double inBoundY(double y1, double y2) {
        Point2D.Double[] points = calculateHitBox();
        for (int i = 0; i < 4; i++) {
            if (!(y1 < points[i].y && y2 > points[i].y)) { //If not in bounds.
                return points[i].y;
            }
        }
        return -1;
    }

    /**
     * Obtains the current speed of this Ball.
     *
     * @return the X speed of this Ball within the GameArena.
     */
    public double getXSpeed() {
        return xSpeed;
    }

    /**
     * Obtains the current speed of this Ball.
     *
     * @return the Y speed of this Ball within the GameArena.
     */
    public double getYSpeed() {
        return ySpeed;
    }

    /**
     * Sets the speed of this Ball.
     *
     * @param xSpeed Set the speed component of this ball in the X direction
     * @param ySpeed Set the speed component of this ball in the Y direction
     */
    public void setSpeed(double xSpeed, double ySpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        if (xSpeed != 0 || ySpeed != 0) {
            this.ballState = State.MOVING;
        }
    }

    /**
     * Attempts to move the current ball in relation to its current speed.
     */
    private void move() {
        if (Math.abs(xSpeed) > 0.1 || Math.abs(ySpeed) > 0.1) { //If moving below these speeds, the ball is considered not moving.
            this.ballState = State.MOVING;
            /**
             * Moves using current speed over a 25 part interval to ensure the
             * ball does not fall out of bounds by a huge range and waste speed.
             */
            Point2D.Double inc = new Point2D.Double(xSpeed / 25, ySpeed / 25);
            for (int i = 0; i < 25; i++) {
                this.xPosition = this.xPosition + inc.x;
                this.yPosition = this.yPosition + inc.y;
                collisionCheck();
                if (inAnyPockets()) {
                    this.pocketBall();
                    return; //If pocketed, moving is no longer relevent.
                }
            }
            boundaryCheck(); //Check if the ball is out of bounds and correct accordingly.
            this.xSpeed = this.xSpeed - Math.copySign(xSpeed * 0.01, xSpeed); //Reduce percentage of X and Y speed.
            this.ySpeed = this.ySpeed - Math.copySign(ySpeed * 0.01, ySpeed);
        } else {
            if (this.getBallState() != State.POCKETED) {
                this.ballState = State.STATIONARY;
            }
            stopBall(); //Set the values of the ball to 0, so it will eventually stop moving.
        }
    }

    /**
     * Alternate version of collision check without parameter toDeflect,
     * defaulting to true.
     */
    private void collisionCheck() {
        this.collisionCheck(true);
    }

    /**
     * Checks for collision between the current ball and any other balls on the
     * table.
     *
     * @param toDeflect A parameter indicating whether to deflect the balls on
     * collision.
     * @return A boolean reflecting on if this ball is currently colliding with
     * another ball.
     */
    private boolean collisionCheck(boolean toDeflect) {
        SnookerBall b = this;
        if (b.getBallState() == SnookerBall.State.MOVING || !toDeflect) {
            ArrayList<SnookerBall> checkBalls = new ArrayList(table.getAllBalls());
            checkBalls.remove(b); //Remove this ball, so it doesn't check itself.
            for (SnookerBall b2 : checkBalls) { //Checks against all other balls.
                Point2D.Double centB = b.getCenterPoint(), centB2 = b2.getCenterPoint();
                double d = b.getSize();
                //Sees if the balls areas overlap
                if (circlePointColide(centB, centB2, d) && b2.getBallState() != State.POCKETED) {
                    if (toDeflect) {
                        b.deflect(b2); //Deflects them if required
                    } else {
                        return true; //If not indicates there is a collision
                    }
                    if (b instanceof SnookerWhiteBall) {
                        SnookerWhiteBall wb = (SnookerWhiteBall) b;
                        wb.updateContactInfo(b2); //Assigns first collision information.
                    }
                }
            }
        }
        return false;
    }

    /**
     * Replaces a ball back onto the table, in accordance with the rules of
     * Snooker.
     */
    public void replaceBall() {
        this.setPosition(prevPoint.x, prevPoint.y); //Attempts to put ball back where it was before movement.
        if (!this.collisionCheck(false)) { //If there isn't a collision in putting it down on its original spot, place it back on the board.
            table.getSnookerGA().addBall(this);
        } else {
            /**
             * If there is a collision, move the ball north until there isn't a
             * collision. If a Y bound is hit, begin moving it in the X
             * direction west.
             */
            int yInc = -1;
            int xInc = -1;
            Point2D.Double[] bP = table.getBoundPoints();
            while (this.collisionCheck(false) && !inAnyPockets()) { //Ensure no overlap with other balls or pockets.
                if (prevPoint.y + yInc > bP[0].y && prevPoint.y + yInc < bP[1].y) {
                    this.setPosition(prevPoint.x, prevPoint.y + yInc);
                    yInc -= 1;
                } else {
                    this.setPosition(prevPoint.x + xInc, prevPoint.y);
                    xInc -= 1;
                }
            }
            table.getSnookerGA().addBall(this);
        }
        this.setBallState(State.STATIONARY);
    }

    /**
     * Determines if the current ball is over any area considered to be "in" the
     * pocket.
     *
     * @return A boolean representing if the current ball is in the area of a
     * pocket.
     */
    private boolean inAnyPockets() {
        Point2D.Double c = this.getCenterPoint();
        for (Circle p : table.getPockets()) {
            if (circlePointColide(p.getCenterPoint(), c, p.getSize())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 0's the balls speed.
     */
    private void stopBall() {
        this.setSpeed(0, 0);
    }

    /**
     * Calculates the deflection of each ball when the two balls collide.
     *
     * @param b - the ball this ball has collided with.
     */
    public void deflect(SnookerBall b) {
        // Calculate initial momentum of the balls... We assume unit mass here.
        double p1InitialMomentum = Math.sqrt(this.xSpeed * this.xSpeed + this.ySpeed * this.ySpeed);
        double p2InitialMomentum = Math.sqrt(b.xSpeed * b.xSpeed + b.ySpeed * b.ySpeed);

        // calculate motion vectors
        double[] p1Trajectory = {this.xSpeed, this.ySpeed};
        double[] p2Trajectory = {b.xSpeed, b.ySpeed};

        // Calculate Impact Vector
        double[] impactVector = {b.xPosition - this.xPosition, b.yPosition - this.yPosition};
        double[] impactVectorNorm = normalizeVector(impactVector);

        // Calculate scalar product of each trajectory and impact vector
        double p1dotImpact = Math.abs(p1Trajectory[0] * impactVectorNorm[0] + p1Trajectory[1] * impactVectorNorm[1]);
        double p2dotImpact = Math.abs(p2Trajectory[0] * impactVectorNorm[0] + p2Trajectory[1] * impactVectorNorm[1]);

        // Calculate the deflection vectors - the amount of energy transferred from one ball to the other in each axis
        double[] p1Deflect = {-impactVectorNorm[0] * p2dotImpact, -impactVectorNorm[1] * p2dotImpact};
        double[] p2Deflect = {impactVectorNorm[0] * p1dotImpact, impactVectorNorm[1] * p1dotImpact};

        // Calculate the final trajectories 
        double[] p1FinalTrajectory = {p1Trajectory[0] + p1Deflect[0] - p2Deflect[0], p1Trajectory[1] + p1Deflect[1] - p2Deflect[1]};
        double[] p2FinalTrajectory = {p2Trajectory[0] + p2Deflect[0] - p1Deflect[0], p2Trajectory[1] + p2Deflect[1] - p1Deflect[1]};

        // Calculate the final energy in the system.
        double p1FinalMomentum = Math.sqrt(p1FinalTrajectory[0] * p1FinalTrajectory[0] + p1FinalTrajectory[1] * p1FinalTrajectory[1]);
        double p2FinalMomentum = Math.sqrt(p2FinalTrajectory[0] * p2FinalTrajectory[0] + p2FinalTrajectory[1] * p2FinalTrajectory[1]);

        // Scale the resultant trajectories if we've accidentally broken the laws of physics.
        double mag = (p1InitialMomentum + p2InitialMomentum) / (p1FinalMomentum + p2FinalMomentum);

        // Write back the scaled values and we're done.
        this.xSpeed = p1FinalTrajectory[0] * mag;
        this.ySpeed = p1FinalTrajectory[1] * mag;
        b.xSpeed = p2FinalTrajectory[0] * mag;
        b.ySpeed = p2FinalTrajectory[1] * mag;
        this.ballState = State.MOVING;
        b.ballState = State.MOVING; //Ensure both states are now that the balls are moving.
        //Increment +1 on the direction of the speed, to ensure another collision doesn't occur immediantly after.
        this.setXPosition(this.getXPosition() + (int) Math.signum(xSpeed));
        this.setYPosition(this.getYPosition() + (int) Math.signum(ySpeed));
    }

    /**
     * Converts a vector into a unit vector. Internal method - used by the
     * deflect() method to calculate the resultant direction after a collision.
     */
    private double[] normalizeVector(double[] vec) {
        double mag = 0.0;
        int dimensions = vec.length;
        double[] result = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            mag += vec[i] * vec[i];
        }
        mag = Math.sqrt(mag);
        if (mag == 0.0) {
            result[0] = 1.0;
            for (int i = 1; i < dimensions; i++) {
                result[i] = 0.0;
            }
        } else {
            for (int i = 0; i < dimensions; i++) {
                result[i] = vec[i] / mag;
            }
        }
        return result;
    }

    /**
     * Gets the state of this ball.
     *
     * @return The state of this ball.
     */
    public State getBallState() {
        return ballState;
    }

    /**
     * Sets the state of this ball.
     *
     * @param ballState The state to set this ball.
     */
    public void setBallState(State ballState) {
        this.ballState = ballState;
    }

    /**
     * Pockets the current ball, by setting the required information to reflect
     * this.
     */
    public void pocketBall() {
        table.getGame().getrBalls().add(this);
        stopBall();
        this.setPosition(0, 0);
        this.setBallState(SnookerBall.State.POCKETED);
        table.getSnookerGA().removeBall(this);
    }

    /**
     * Gets the value this ball is worth when pocketed.
     *
     * @return The value this ball is worth when pocketed.
     */
    public int getScore() {
        return score;
    }

}
