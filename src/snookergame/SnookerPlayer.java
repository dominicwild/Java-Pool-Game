package snookergame;

import dist.Text;

/**
 * A representation of a player, with a score and name.
 *
 * @author DominicWild
 */
public class SnookerPlayer {

    private int score;      //This current players score.
    private String name;    //The name of this player.
    private Text display;   //The display text that is shown for give player feedback.

    /**
     * Creates a new player.
     *
     * @param n The name of this player.
     * @param sizeOfText The size of the text to appear to display the player
     * name and score.
     * @param x The X coordinate this text is to be drawn.
     * @param y The Y coordinate this text is to be drawn.
     * @param col The colour of this text.
     * @param lay The layer this text is to be drawn on.
     */
    public SnookerPlayer(String n, int sizeOfText, double x, double y, String col, int lay) {
        this.score = 0;
        this.name = n;
        display = new Text(this.toString(), sizeOfText, x, y, col, lay);
    }

    @Override
    public String toString() {
        return name + ": " + score;
    }

    /**
     * Gets the Text object for this Player displaying their information.
     *
     * @return The Text object for this Player displaying their information.
     */
    public Text getDisplay() {
        return display;
    }

    /**
     * Increments the score of this player by a specified amount.
     *
     * @param amount The amount to add to the players score.
     */
    public void incrementScore(int amount) {
        this.score += amount;
        updateDisplay();
    }

    /**
     * Updates the text displayed about the player.
     */
    public void updateDisplay() {
        this.display.setText(this.toString());
    }

}
