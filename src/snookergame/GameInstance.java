
package snookergame;

import dist.Circle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.event.MouseInputAdapter;
import snookergame.SnookerBall.State;

/**
 * The class responsible for the instance of the game. Handling the gathering of
 * data for effecting the state of the game and also handling the game logic and
 * flow.
 *
 * @author DominicWild
 */
public class GameInstance extends MouseInputAdapter implements Runnable {

    private SnookerTable table;                             //The table that the instance of the SnookerGame is taking place on.
    private int playerTurn = 0;                             //An index representing which of one of two players turns it is. 0 and 1 are values used.
    ArrayList<SnookerBall> rBalls = new ArrayList();        //The "round balls" representing balls that have been pocketed in the current shot.
    boolean selectColourTurn = false;                       //A boolean indicator of if a colour needs to be selected by a player.
    private String selectedColour = "NONE";                         //The selected colour currently.
    private String ballVals[] = {"RED", "YELLOW", "GREEN", "BROWN", "BLUE", "PINK", "LIGHTBLACK"};        //A list of balls in score order, index+1 is the score of each ball.
    public static final int  speedDivFactor = 6, maxXSpeed = 30, maxYSpeed = 30;

    /**
     * Creates an instance of GameInstance associated with a passed SnookerTable
     * s.
     *
     * @param s The SnookerTable this GameInstance object handles the game
     * instance for.
     */
    public GameInstance(SnookerTable s) {
        this.table = s;
    }

    @Override
    /**
     * Handles the event on when the mouse is clicked. Handles the occausion of
     * a select colour turn.
     */
    public void mouseClicked(MouseEvent e) {
        if(selectColourTurn){
        selectColourTurn(e.getX(),e.getY());
        }
    }
    
   private void selectColourTurn(double clickedX, double clickedY){
            /**
             * If it is a select colour turn and a valid clicked colour can be
             * determined, then handle the necessary game logic for the player
             * to take the next shot.
             */
            if (determineClickedColour(new Point2D.Double(clickedX, clickedY))) {
                selectColourTurn = false;
                table.cueEnable(true);
            }
    }
    
    /**
     * Runs through each balls coordinates on the board and checks to see if the
     * mouse was click on one of these balls and if that clicked ball is a valid
     * one to be selected. If it is, that is determined as the selected colour
     * and a boolean returned to reflect this was successful.
     *
     * @param p The coordinate of which was clicked.
     * @return A boolean indicating whether there was a successful valid colour
     * clicked.
     */
    private boolean determineClickedColour(Point2D.Double p) {
        for (SnookerBall b : table.getAllBalls()) {
            if (Circle.circlePointColide(p, b.getCenterPoint(), b.getSize())) {
                if (!b.getColour().equals("WHITE") && !b.getColour().equals("RED")) {
                    this.setSelectedColour(b.getColour());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!table.isABallMoving() && !selectColourTurn){
        double xSpeed=0 , ySpeed=0;
        
        xSpeed = (table.getWhiteBall().getXPosition() - e.getX()) / speedDivFactor;
        ySpeed = (table.getWhiteBall().getYPosition() - e.getY()) / speedDivFactor;
         if(Math.abs(xSpeed) > GameInstance.maxXSpeed){
            xSpeed = Math.signum(xSpeed)*GameInstance.maxXSpeed;
        }
        if(Math.abs(ySpeed) > GameInstance.maxYSpeed){
            ySpeed = Math.signum(ySpeed)*GameInstance.maxYSpeed;
        }
        table.getWhiteBall().setSpeed(xSpeed, ySpeed);
        this.movePhase(table);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e); 
    }
    
    

    @Override
    public void run() {
       playoutTurn();
    }
    /**
     * Plays out the end of the turn, checking all game logic and handling
     * placement of balls and etc., related to the game.
     */
    private void playoutTurn() {
        do {/*Nothing*/} while (table.isABallMoving()); //Waits til all balls are not moving.
        /**
         * Checks conditions to determine how variables change for the next
         * turn. Assigning score, turn type, if balls are to be replaced, if the
         * cue is to be visible etc.
         */
        if (isThereFoul()) {
            handleFoul();
        } else {
            handleNonFoul();
        }
        if (isAllRedPocketed()) { //Checks for the phase of the game. 
            selectNextColour();
            selectColourTurn = false;
        } else {
            this.setSelectedColour("NONE");
        }
        // Resets all the details for the next turn. 
        table.getWhiteBall().resetInfo();
        rBalls.removeAll(rBalls);
    }
    /**
    * Handles the situation in which a foul is made.
    */
    private void handleFoul() {
        int foulScore = calculateFoulScore();
        table.getPlayers()[(playerTurn + 1) % 2].incrementScore(foulScore);
        replaceFoulBalls();
        nextTurn();
    }
    /**
     * Handles the situation in which a foul is not made.
     */
    private void handleNonFoul(){
         if (rBalls.isEmpty()) {
                nextTurn();
            } else {
                table.getPlayers()[playerTurn].incrementScore(calculateNonFoulScore());
                if (rBalls.get(0).getColour().equals("RED")) {
                    selectColourTurn = true;
                    table.cueEnable(false);
                } else {
                    selectColourTurn = false;
                    table.cueEnable(true);
                }
            }
    }
    /**
     * Selects the next colour assuming the state of the game is where no red
     * balls remain.
     */
    private void selectNextColour() {
        /**
         * Finds the ball with the lowest score, this is the ball to be assigned
         * as the next to be pocketed in this phase. The highest score for a
         * ball in Snooker is 7, therefore if it remains at 8, this means all
         * balls have been pocketed, thus it is the end of the game and the end
         * of game state is triggered.
         */
        int lowestScore = 8;
        for (SnookerBall b : table.getAllBalls()) {
            if (!(b.getColour().equals("RED") || b.getColour().equals("WHITE")) && b.getBallState() != State.POCKETED) {
                int ballScore = b.getScore();
                if (lowestScore > ballScore) {
                    lowestScore = ballScore;
                }
            }
        }
        if (lowestScore == 8) {
            /* Game is over, no more balls on the board. */
            gameEnd();
        } else {
            this.setSelectedColour(valToCol(lowestScore));
        }
    }
    /**
     * Takes a value that is the score of a ball and returns its colour.
     * @param score The score of the ball to be returned.
     * @return The colour of the ball of the passed score.
     */
    private String valToCol(int score){
        return ballVals[score-1];
    }
    /**
     * Determines whether all red balls are pocketed.
     * @return A boolean representing if all red balls are pocketed.
     */
    private boolean isAllRedPocketed() {
        /**
         * Go through all balls on the board and checks if all the red balls
         * have a state of "pocketed". If one does not, false is returned.
         */
        for (SnookerBall b : table.getAllBalls()) {
            if (b.getColour().equals("RED")) {
                if (b.getBallState() != State.POCKETED) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Calculates the non-foul score based on Snooker rules.
     * @return The score for this turn.
     */
    private int calculateNonFoulScore(){
        int score=0;
        for(SnookerBall b : rBalls){
            score += colToVal(b.getColour());
        }
        return score;
    }
    /**
     * Replaces necessary foul balls, if there are any.
     */
    private void replaceFoulBalls(){
        //Replaces any balls which are not red that have been pocketed this round.
        for(SnookerBall b : rBalls){
            if(!b.getColour().equals("RED")){
                b.replaceBall();
            }
        }
    }
    /**
     * Calculates the score in case of a foul.
     *
     * @return The score for this foul turn.
     */
    private int calculateFoulScore() {
        /**
         * Uses Snooker rules to check conditions and apply the highest of the
         * scores that should be applied based on these conditions.
         */
        int selectedBallVal = colToVal(selectedColour);
        int highestFoulVal = highestFoulBall();
        int foulScore;
        if (selectedBallVal > highestFoulVal) {
            foulScore = selectedBallVal;
        } else {
            foulScore = highestFoulVal;
        }

        if (foulScore >= 4 && foulScore <= 7) {
            return foulScore;
        } else if (foulScore < 4) {
            return 4;
        } else if (foulScore > 7) {
            return 7;
        }
        return 0;
    }
    /**
     * Finds the ball with the highest score that was potted in a foul.
     * @return The value of the highest score of a potted foul ball.
     */
    private int highestFoulBall(){
        int highest = 0;
        for(SnookerBall b : rBalls){
            int val = b.getScore(); 
            if(highest < val){
                highest = val;
            }
        }
        return highest;
    }
    /**
     * Takes a colour of a SnookerBall and returns its corresponding score.
     * @param col The colour of ball to get the score of.
     * @return The score of this ball colour.
     */
    private int colToVal(String col){
        for(int i=0;i<7;i++){
            if(ballVals[i].equals(col)){
                return i+1;
            }
        }
        return 0;
    }
    /**
     * Determines if there is a foul by checking conditions that classify a foul.
     * @return Returns boolean reflecting if there is a foul.
     */
    private boolean isThereFoul(){
        return foulNoHit()||foulWrongColourHit()||foulPotWhite()||foulPotWrongColour();
    }
    /**
     * Checks condition no ball was hit.
     * @return Boolean of if a ball was hit this turn.
     */
    private boolean foulNoHit(){
        return !table.getWhiteBall().isColided();
    }
    /**
     * Checks condition if the wrong coloured ball was pocketed.
     * @return Boolean of if a ball was pocketed not in accordance with the rules.
     */
    private boolean foulPotWrongColour(){
        for(SnookerBall b : rBalls){ //Steps through all pocketed balls this round.
            if(selectedColour.equals("NONE")){ //Checks if only the selected coloured ball has been pocketed this turn.
                if(!b.getColour().equals("RED")){
                    return true;
                }
            } else {
                if(!b.getColour().equals(selectedColour)){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Checks whether the first ball hit, was the wrong colour.
     * @return A boolean reflecting if this is true.
     */
    private boolean foulWrongColourHit() {
        if (selectedColour.equals("NONE")) {
            return !table.getWhiteBall().colColision.equals("RED");
        } else {
            return !table.getWhiteBall().colColision.equals(selectedColour);
        }
    }
    /**
     * Checks if the white was pocketed.
     * @return A boolean reflecting if the white is pocketed this turn.
     */
    private boolean foulPotWhite(){
        return rBalls.contains(table.getWhiteBall());
    }
    /**
     * Initiates the move phase of this turn. Where all balls are responsive to movement.
     * @param s The snooker table of which this move phase has began on.
     */
    public void movePhase(SnookerTable s) {
        ArrayList<SnookerBall> allBalls = s.getAllBalls();
        this.table.cueEnable(false);
        for (SnookerBall b : allBalls) { //Starts the threads of all the balls to begin moving in accordance with the set speed of the white ball.
            Thread t = new Thread(b);
            t.start();
        }
        Thread t = new Thread(this);
        t.start(); //As balls are now moving, launches thread to wait for the end of the turn.
    }

    /**
     * Makes the text of the current players turn, yellow.
     */
    public void selectCurrentPlayer() {
        this.table.getPlayers()[playerTurn].getDisplay().setColour("YELLOW");
        this.table.getPlayers()[(playerTurn + 1) % 2].getDisplay().setColour("WHITE");
    }
    /**
     * Triggers the next turn and handles variables to reflect this.
     */
    private void nextTurn(){
        this.playerTurn = (this.playerTurn + 1) % 2;
        selectCurrentPlayer();
        this.table.cueEnable(true);
    }
    /**
     * Gets the index of the current players turn.
     * @return The index of the current players turn.
     */
    public int getPlayerTurn() {
        return playerTurn;
    }
    /**
     * Gets this game instances table.
     * @return This game instances table.
     */
    public SnookerTable getTable() {
        return table;
    }
    /**
     * Gets the balls that have been pocketed this round, as an ArrayList.
     * @return ArrayList of balls pocketed this round.
     */
    public ArrayList<SnookerBall> getrBalls() {
        return rBalls;
    }
    /**
     * As the colour is selected, updates the necessary GameInfoComponents.
     * @param selectedColour The colour to set the selected colour to.
     */
    public void setSelectedColour(String selectedColour) {
        this.selectedColour = selectedColour;
        table.getInfoComp().updateColourInfo(selectedColour);
    }
    /**
     * Handles the end of the game. Making the SnookerTable inactive.
     */
    public void gameEnd(){
        table.cueEnable(false);
        table.getWhiteBall().pocketBall();
        table.getInfoComp().endGameMessage();
    }
    
}
