package dist;

import java.awt.geom.Point2D;

/**
 * Models a simple solid sphere. 
 * This class represents a Ball object. When combined with the GameArena class,
 * instances of the Ball class can be displayed on the screen.
 */
public class Circle 
{
	// The following instance variables define the
	// information needed to represent a Ball
	// Feel free to more instance variables if you think it will 
	// support your work... 
	
	protected double xPosition;			// The X coordinate of this Ball
	protected double yPosition;			// The Y coordinate of this Ball
	private double size;				// The diameter of this Ball
	private String colour = "WHITE";	// The colour of this Ball

										// Permissable colours are:
										// BLACK, BLUE, CYAN, DARKGREY, GREY,
										// GREEN, LIGHTGREY, MAGENTA, ORANGE,
										// PINK, RED, WHITE, YELLOW, BROWN

	private int layer;					// Layer the ball is on. Higher layers take priority.

	

	/**
	 * Obtains the current position of this Ball.
	 * @return the X coordinate of this Ball within the GameArena.
	 */
	public double getXPosition()
	{
		return xPosition;
	}
        
	

	/**
	 * Obtains the current position of this Ball.
	 * @return the Y coordinate of this Ball within the GameArena.
	 */
	public double getYPosition()
	{
		return yPosition;
	}

	

	/**
	 * Moves the current position of this Ball to the given co-ordinates
	 * @param x the new x co-ordinate of this Ball
	 */
	public void setXPosition(double x)
	{
		this.xPosition = x;
	}

	/**
	 * Moves the current position of this Ball to the given co-ordinates
	 * @param y the new y co-ordinate of this Ball
	 */
	public void setYPosition(double y)
	{
		this.yPosition = y;
	}

	/**
	 * Obtains the size of this Ball.
	 * @return the diameter of this Ball,in pixels.
	 */
	public double getSize()
	{
		return size;
	}


	/**
	 * Obtains the colour of this Ball.
	 * @return a textual description of the colour of this Ball.
	 */
	public String getColour()
	{
		return colour;
	}

	/**
	 * Obtains the layer of this Ball.
	 * @return the layer of this Ball.
	 */
	public int getLayer()
	{
		return layer;
	}


    /** 
     * Create a Ball of the given size, position and colour
     *
     * @param x The initial x co-ordinate of the ball.
     * @param y The initial x co-ordinate of the ball.
     * @param diameter The diameter of the ball.
     * @param col The colour of the Ball.  Permissable BLACK, BLUE, CYAN, DARKGREY, GREY, GREEN, LIGHTGREY, MAGENTA, ORANGE, PINK, RED, WHITE, YELLOW, BROWN
     */
	public Circle(double x, double y, double diameter, String col)
	{
		xPosition = x;
		yPosition = y;
		size = diameter;
		colour = col;
		layer = 0;
	}	

    /** 
     * Create a Ball of the given size, position and colour on the given layer
     *
     * @param x The initial x co-ordinate of the ball.
     * @param y The initial x co-ordinate of the ball.
     * @param diameter The diameter of the ball.
     * @param col The colour of the Ball.  Permissable BLACK, BLUE, CYAN, DARKGREY, GREY, GREEN, LIGHTGREY, MAGENTA, ORANGE, PINK, RED, WHITE, YELLOW, BROWN
     * @param lay The layer of Ball - higher layers are drawn on top of lower layers.
     */
	public Circle(double x, double y, double diameter, String col, int lay)
	{
		xPosition = x;
		yPosition = y;
		size = diameter;
		colour = col;
		layer = lay;
	}
        
    /**
     * Returns if 2 points collide or overlap, around a distance d from each other.
     * @param p1 A point to check for overlap.
     * @param p2 A point to check for overlap.
     * @param d The radius to check for overlap.
     * @return If the 2 points as circles overlap.
     */
    public static boolean circlePointColide(Point2D.Double p1, Point2D.Double p2, double d){
            return Math.pow(p1.x - p2.x, 2) + Math.pow(p2.y - p1.y, 2) <= Math.pow(d, 2);
        }
    
    /**
     * Gets the x and y coordinates as a point.
     * @return The center point.
     */
    public Point2D.Double getCenterPoint(){
        double x = this.getXPosition();
        double y = this.getYPosition();
        
        return new Point2D.Double(x,y);
    }
        
	
}
