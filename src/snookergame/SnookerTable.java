package snookergame;

import dist.Circle;
import dist.GameArena;
import dist.Line;
import dist.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import snookergame.SnookerBall.State;

/**
 * Represents a SnookerTable and all its assets. It holds a GameArena which
 * holds the display of the table and visualisations of all balls, pockets cues
 * etc. This class handles placement of these elements.
 *
 * @author DominicWild
 */
public class SnookerTable {

    private SnookerBall[] balls;                    //All the balls present on this table, excluding the white ball.
    private SnookerWhiteBall whiteBall;             //The white ball on this table.
    private Circle[] pockets;                //The pockets present on this table.
    private Line[] tableBounds;                     //The boundaries of the table, as 4 line objects.
    private Point2D.Double[] boundPoints;           //The points that bind the tables bounds.
    private Line baulkLine;                         //The line on the snooker table used for game rules.
    private Rectangle innerTable;                   //The inner part of the table, used the inner part of the bounds green.
    private GameArena snookerGA;                    //The GameArena which houses the snooker table visual elements.
    private SnookerCue cue;                         //The cue which is associated with this table.
    private SnookerPlayer[] players;                //The 2 players associated with this snooker table.
    private GameInstance game;                      //The game instance that handles the logic for snooker game.
    private GameInfoComponents infoComp;            //The game info components object that displays information on this snooker table.

    /**
     * Creates a new snooker table with specified width, height and indentation
     * on a JFrame.
     *
     * @param width The width of the window to house this snooker table.
     * @param height The height of the window to house this snooker table.
     * @param indent The indentation that is used to displace the snooker table
     * within the JFrame.
     */
    public SnookerTable(int width, int height, int indent) {
        //Creating the GameArena and innerTable objects.
        int layer = 1;
        int tableWidth = width - 2 * (indent);
        int tableHeight = height - 2 * (indent);
        snookerGA = new GameArena(width, height);
        innerTable = new Rectangle(indent, indent, tableWidth, tableHeight, "DARKGREEN", 1);
        //Gets the 2 diagonal coordinates of the inner table for defining bounds.
        double x1 = innerTable.getXPosition();
        double x2 = innerTable.getXEnd();
        double y1 = innerTable.getYPosition();
        double y2 = innerTable.getYEnd();
        //Create the table bounds with coordinates gathered.
        tableBounds = new Line[4];
        int thickness = 10;
        placeBounds(x1, x2, y1, y2, thickness);

        double pocketSize = tableWidth * 0.05;
        //Move boundary coordinates in by 1/2 thickness, due to how the line expands when drawn from reference points.
        indent = thickness / 2;
        x1 += indent;
        x2 -= indent;
        y1 += indent;
        y2 -= indent; //These used to place pockets within the corners of the table.
        boundPoints = new Point2D.Double[2];
        boundPoints[0] = new Point2D.Double(x1, y1);
        boundPoints[1] = new Point2D.Double(x2, y2);

        placePockets(x1, x2, y1, y2, pocketSize, tableWidth);
        placeBaulkLine(x1, x2, y1, y2);
        //Create white ball.
        double diameter = tableWidth * 0.02; //Diameter of white ball in respect to table width.
        whiteBall = new SnookerWhiteBall(baulkLine.getXStart(), getMidY() + 20, diameter, 4, this);
        //Create cue.
        cue = new SnookerCue(this, width * 0.19, 4, "ORANGE", 5);
        //Create two players.
        players = new SnookerPlayer[2];
        players[0] = new SnookerPlayer("Player 1", 30, width / 8, height * 0.1, "WHITE", 1);
        players[1] = new SnookerPlayer("Player 2", 30, (3 * width) / 4, height * 0.1, "WHITE", 1);
        //Create components.
        infoComp = new GameInfoComponents(snookerGA);
        //Create game instance and add it as a listener.
        GameInstance gI = new GameInstance(this);
        this.snookerGA.addMouseListener(gI);
        this.game = gI;
        //Add all the create objects to the game arena for display.
        placeSnookerBalls(diameter, (x1 + x2) / 2);
        snookerGA.addBall(whiteBall);
        snookerGA.addText(players[0].getDisplay());
        snookerGA.addText(players[1].getDisplay());
        snookerGA.addRectangle(innerTable);
        snookerGA.addLine(cue);
        snookerGA.setVisible(true);
        snookerGA.addMouseMotionListener(cue);
        //Select the current player for this table.
        game.selectCurrentPlayer();
    }

    /**
     * Places the bound lines for this table.
     *
     * @param x1 Top left X coordinate.
     * @param x2 Bottom Right X coordinate.
     * @param y1 Top left Y coordinate.
     * @param y2 Bottom Right Y coordinate.
     * @param thickness The thickness of these bounds.
     */
    private void placeBounds(double x1, double x2, double y1, double y2, int thickness) {
        String colourBounds = "BROWN";
        int layer = 3;
        tableBounds[0] = new Line(x1, y1, x2, y1, thickness, colourBounds, layer);
        tableBounds[1] = new Line(x1, y1, x1, y2, thickness, colourBounds, layer);
        tableBounds[2] = new Line(x2, y2, x1, y2, thickness, colourBounds, layer);
        tableBounds[3] = new Line(x2, y2, x2, y1, thickness, colourBounds, layer);
        for (Line l : tableBounds) {
            snookerGA.addLine(l);
        }
    }

    /**
     * Detects if a ball is currently in the state of moving on this table.
     *
     * @return A boolean to represent if a ball is moving on this table.
     */
    public boolean isABallMoving() {
        for (SnookerBall b : this.getAllBalls()) {
            if (b.getBallState() == State.MOVING) {
                return true;
            }
        }
        return false;
    }

    /**
     * Places the pockets for this table.
     *
     * @param x1 Top left X coordinate of bounds.
     * @param x2 Bottom Right X coordinate of bounds.
     * @param y1 Top left Y coordinate of bounds.
     * @param y2 Bottom Right Y coordinate of bounds.
     * @param size The size of these pockets.
     * @param tableWidth The width the table is.
     */
    private void placePockets(double x1, double x2, double y1, double y2, double size, int tableWidth) {
        pockets = new Circle[6];
        int layer = 2;
        String colourPockets = "BLACK";
        double pocketDisplacement = tableWidth / 2;
        pockets[0] = new Circle(x1, y1, size, colourPockets, layer); //Pockets at the 4 corners
        pockets[1] = new Circle(x2, y1, size, colourPockets, layer);
        pockets[2] = new Circle(x1, y2, size, colourPockets, layer);
        pockets[3] = new Circle(x2, y2, size, colourPockets, layer);
        pockets[4] = new Circle(x1 + pocketDisplacement, y1, size, colourPockets, layer); //2 pockets in the middle of the table.
        pockets[5] = new Circle(x1 + pocketDisplacement, y2, size, colourPockets, layer);
        for (Circle b : pockets) {
            snookerGA.addBall(b);
        }
    }

    /**
     * Gets the middle Y of the table using baulk line coordinates.
     *
     * @return The middle Y value of the table.
     */
    private double getMidY() {
        return (baulkLine.getYEnd() + baulkLine.getYStart()) / 2;
    }

    /**
     * Places all the snooker balls on the table, as well as creates them.
     *
     * @param diameter The diameter of the balls to be created.
     * @param midX The mid x coordinate of the table.
     */
    private void placeSnookerBalls(double diameter, double midX) {
        balls = new SnookerBall[21];
        double midY = getMidY();

        placeColouredBalls(midY, midX, diameter); //To be placed at specific positions.
        //Generates a set of points to which the balls must be placed at.
        Point2D.Double[] pointsToPlaceRed = triRedPos((balls[19].getXPosition() + diameter + 2), midY, diameter);

        for (int i = 0; i < 15; i++) {
            balls[i] = new SnookerBall(pointsToPlaceRed[i].getX(), pointsToPlaceRed[i].getY(), diameter, "RED", 3, this, 1);
        }

        for (SnookerBall b : balls) {
            snookerGA.addBall(b);
        }
    }

    /**
     * Places coloured balls at their standard positions based on rules of
     * Snooker.
     *
     * @param midY The mid Y value of the table.
     * @param midX The mid X value of the table.
     * @param diameter The diameter of these balls.
     */
    private void placeColouredBalls(double midY, double midX, double diameter) {
        double separation = innerTable.getHeight() * 0.15;
        double xBaulk = baulkLine.getXEnd();
        balls[15] = new SnookerBall(xBaulk, midY, diameter, "BROWN", 3, this, 4);
        balls[16] = new SnookerBall(xBaulk, midY - separation, diameter, "GREEN", 3, this, 3);
        balls[17] = new SnookerBall(xBaulk, midY + separation, diameter, "YELLOW", 3, this, 2);
        balls[18] = new SnookerBall(midX + diameter / 3, midY, diameter, "BLUE", 3, this, 5);
        balls[19] = new SnookerBall((midX + innerTable.getXEnd()) / 2, midY, diameter, "PINK", 3, this, 6);
        balls[20] = new SnookerBall(innerTable.getXEnd() - innerTable.getXEnd() * 0.05, midY, diameter, "LIGHTBLACK", 3, this, 7);
    }

    /**
     * Places baulk line on the table at standard position based on Snooker
     * Table specifications.
     *
     * @param x1 Upper left X coordinate of bounds.
     * @param x2 Bottom Right X coordinate of bounds.
     * @param y1 Upper left Y coordinate of bounds.
     * @param y2 Bottom Right Y coordinate of bounds.
     */
    private void placeBaulkLine(double x1, double x2, double y1, double y2) {
        double baulkLineRatio = 73.66 / 365.76; //The baulk line ratio of width of a standard snooker table over the length it should be placed from the left of the table.
        x1 += (baulkLineRatio * innerTable.getWidth());
        baulkLine = new Line(x1, y1, x1, y2, 1, "LIGHTGREY", 2);
        snookerGA.addLine(baulkLine);
    }

    /**
     * Generates a triangular set of points of which to place the red balls.
     *
     * @param startX The X coordinate the triangle should start at.
     * @param startY The Y coordinate the triangle should start at.
     * @param distance The distance between the points.
     * @return The generated array of triangular points.
     */
    private Point2D.Double[] triRedPos(double startX, double startY, double distance) {
        Point2D.Double[] points = new Point2D.Double[15];
        Point2D.Double startP = new Point2D.Double(startX, startY);
        Point2D.Double newP;
        int k = 0; //Index by which to place the points in array.
        /**
         * Places points by starting from a starting point, then stepping down a
         * distance D from that point. Where D is the distance variable, that
         * takes the value of the diameter. It starts by placing one point on
         * the first loop, then 2, them 3 etc. Until i no longer increments.
         * After each segment of placing balls in a vertical line, the new
         * starting point of the next loop will step forward the distance D from
         * the last starting point and begin again.
         */
        for (int i = 0; i < 5; i++) {
            newP = new Point2D.Double(startP.x, startP.y);
            for (int j = 0; j < i + 1; j++) {
                points[k] = new Point2D.Double(newP.x, newP.y);
                newP.y = newP.y - distance;
                k++;
            }
            startP.setLocation(startP.x + distance, startP.y + distance / 2);
        }
        return points;
    }

    /**
     * Gets the white ball object on this snooker table.
     *
     * @return The white ball object on this snooker table.
     */
    public SnookerWhiteBall getWhiteBall() {
        return whiteBall;
    }

    /**
     * Gets the game arena object for this snooker table.
     *
     * @return The game arena object for this snooker table.
     */
    public GameArena getSnookerGA() {
        return snookerGA;
    }

    /**
     * Gets the table boundary points.
     *
     * @return The table boundary points.
     */
    public Point2D.Double[] getBoundPoints() {
        return boundPoints;
    }

    /**
     * Gets the balls on the snooker table, excluding white.
     *
     * @return The balls on the snooker table, excluding white.
     */
    public SnookerBall[] getBalls() {
        return balls;
    }

    /**
     * Gets all balls on this table, inclusive of the white ball.
     *
     * @return An array list with all balls on the table.
     */
    public ArrayList<SnookerBall> getAllBalls() {
        ArrayList<SnookerBall> allBalls = new ArrayList();
        allBalls.addAll(Arrays.asList(this.getBalls()));
        allBalls.add(this.getWhiteBall());
        return allBalls;
    }

    /**
     * Gets the SnookerCue associated with this SnookerTable.
     *
     * @return The SnookerCue associated with this SnookerTable.
     */
    public SnookerCue getCue() {
        return cue;
    }

    /**
     * Gets the pockets on this SnookerTable.
     *
     * @return The pockets on this SnookerTable.
     */
    public Circle[] getPockets() {
        return pockets;
    }

    /**
     * Gets the SnookerPlayers for this table.
     *
     * @return The SnookerPlayers for this table.
     */
    public SnookerPlayer[] getPlayers() {
        return players;
    }

    /**
     * Gets the current player whose turn it is on this snooker table.
     *
     * @return The current player whose turn it is on this snooker table.
     */
    public SnookerPlayer getCurrentPlayer() {
        return this.players[game.getPlayerTurn()];
    }

    /**
     * Enables or disables the cue for this table, if it isn't already set to
     * the past boolean value.
     *
     * @param state To enable to disable the cue on this board and all its
     * function.
     */
    public void cueEnable(boolean state) {
        if (state) {
            snookerGA.addLine(this.cue);
            snookerGA.addMouseMotionListener(this.cue);
            //Makes the cue appear in the correct position if reappearing on the board after a persons turn.
            this.cue.mouseMoved(new MouseEvent(snookerGA, 0, 0, 0, 0, 0, 0, false));
        } else {
            snookerGA.removeLine(this.cue);
            snookerGA.removeMouseMotionListener(this.cue);
        }
    }

    /**
     * Gets the GameInstance for this SnookerTable.
     *
     * @return The GameInstance for this SnookerTable.
     */
    public GameInstance getGame() {
        return game;
    }

    /**
     * Gets the game info components for this table.
     *
     * @return The game info components for this table.
     */
    public GameInfoComponents getInfoComp() {
        return infoComp;
    }

}
