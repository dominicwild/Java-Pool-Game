package snookergame;

/**
 * A class that represents the white ball on a SnookerTable.
 *
 * @author DominicWild
 */
public class SnookerWhiteBall extends SnookerBall {

    String colColision = "No collision";        //A string that represents the colour of the first collision the white ball has made.
    boolean colided;                            //A boolean indicating whether or not a collision has occured.

    /**
     * Creates a new white ball.
     *
     * @param x The X coordinate for the white ball to be placed.
     * @param y The Y coordinate for the white ball to be placed.
     * @param diameter The diameter of this ball.
     * @param lay The layer for this ball to be drawn on.
     * @param s The table this ball is associated with.
     */
    public SnookerWhiteBall(double x, double y, double diameter, int lay, SnookerTable s) {
        super(x, y, diameter, "WHITE", lay, s, 0);
    }

    /**
     * Updates information of which ball was first hit by the white ball.
     *
     * @param b The ball in which was hit to update the information.
     */
    public void updateContactInfo(SnookerBall b) {
        if (!colided) { //Only update based on the first contacted ball, no others.
            this.colColision = b.getColour();
            this.colided = true;
            System.out.printf("\ncolided = %b ColourCol = %s\n", colided, colColision);
        }
    }

    /**
     * Resets information on first contact to what it was at object creation.
     */
    public void resetInfo() {
        this.colided = false;
        this.colColision = "No collision";
    }

    /**
     * Returns boolean representing if this ball has hit another ball this turn.
     *
     * @return A boolean representing if this ball has hit another ball this
     * turn.
     */
    public boolean isColided() {
        return colided;
    }

    
    
}
