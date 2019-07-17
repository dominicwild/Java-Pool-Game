
package snookergame;

/**
 * The main class to initiate an instance of a Snooker game.
 * @author DominicWild
 */
public class SnookerGame {

    /** Main function to initiate the game.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SnookerTable snookerTable = new SnookerTable(800, 600, 100);
        //SnookerTable snookerTable = new SnookerTable(1000, 800, 100);
    }

}
