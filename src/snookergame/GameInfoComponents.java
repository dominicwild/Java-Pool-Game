
package snookergame;

import dist.GameArena;
import dist.Text;

/**
 * The class responsible for displaying information to the user through
 * components on the screen.
 *
 * @author DominicWild
 */
public class GameInfoComponents {

    private Text selectedCol;           //The current selected colour Text object. Used to display what the current selected colour is.
    private Text endGameMsg;            //The end game message to be displayed when the game ends.
    private GameArena gameArena;        //The game Arena these components should be displayed on.
    private Text speedMsg;              //The text showing the current speed the ball will be launched at.

    /**
     * The constructor for a GameInfoComponents object, taking the GameArena it
     * is associated to as an argument.
     *
     * @param gA The GameArena this object is associated with.
     */
    public GameInfoComponents(GameArena gA) {
        this.gameArena = gA; //Assign passed Game Arena, among instansiate class objects.
        selectedCol = new Text("Selected Ball Colour: NONE", 20, gA.getArenaWidth() * 0.35, gA.getArenaHeight() * 0.1, "WHITE", 10);
        endGameMsg = new Text("The game has ended!", 30, gA.getArenaWidth() / 3.6, gA.getArenaHeight() * 0.9, "YELLOW", 10);
        speedMsg = new Text("Speed: 0",20,gA.getArenaWidth() * 0.35, gA.getArenaHeight() * 0.05, "WHITE", 10);
        this.gameArena.addText(speedMsg);
        this.gameArena.addText(selectedCol);
    }

    /**
     * Updates the colour Text object to display the current selected ball
     * colour.
     *
     * @param col A string representing the colour of the selected ball.
     */
    public void updateColourInfo(String col) {
        /**
         * Handles a few special cases where the colour is not set or where the
         * colouring would class with the black background.
         *
         */
        switch (col) {
            case "LIGHTBLACK":
                selectedCol.setText("Selected Ball Colour: BLACK");
                selectedCol.setColour("GREY");
                break;
            case "NONE":
                selectedCol.setText("Selected Ball Colour: NONE");
                selectedCol.setColour("WHITE");
                break;
            default:
                selectedCol.setText("Selected Ball Colour: " + col);
                selectedCol.setColour(col);
                break;
        }
    }

    /**
     * Displays the end game message on the GameArena assigned to this
     * GameInfoComponents.
     */
    public void endGameMessage() {
        gameArena.addText(endGameMsg);
    }
    
    public void setSpeedText(double xSpeed, double ySpeed){
        this.speedMsg.setText("doo dood");
        this.speedMsg.setText(String.format("Speed X: %.2f Y: %.2f", xSpeed, ySpeed));
    }
}
