package dist;
/**
 * Models a simple, solid rectangle. 
 * This class represents a Rectabgle object. When combined with the GameArena class,
 * instances of the Rectangle class can be displayed on the screen.
 */
public class Rectangle 
{
	// The following instance variables define the
	// information needed to represent a Rectangle
	// Feel free to more instance variables if you think it will 
	// support your work... 
	
	private double xBegin;			// The X coordinate this triangle begins at.
	private double yBegin;			// The Y coordinate this triangle begins at.
        private double xEnd;			// The X coordinate this triangle ends at.
	private double yEnd;                    // The Y coordinate this triangle ends at.
	private double width;				// The width of this Rectangle
	private double height;				// The height of this Rectangle
	private int layer;
	private String colour = "WHITE";	// The colour of this Rectangle

										// Permissable colours are:
										// BLACK, BLUE, CYAN, DARKGREY, GREY,
										// GREEN, LIGHTGREY, MAGENTA, ORANGE,
										// PINK, RED, WHITE, YELLOW, BROWN 


	/**
	 * Obtains the current position of this Rectangle.
	 * @return the X coordinate of this Rectangle within the GameArena.
	 */
	public double getXPosition()
	{
		return xBegin;
	}
        
        
        
	/**
	 * Obtains the current position of this Rectangle.
	 * @return the Y coordinate of this Rectangle within the GameArena.
	 */
	public double getYPosition()
	{
		return yBegin;
	}

	/**
	 * Moves the current position of this Rectangle to the given X co-ordinate
	 * @param x the new x co-ordinate of this Rectangle
	 */
	public void setXPosition(double x)
	{
		this.xBegin = x;
                this.xEnd = x + this.width;
	}

	/**
	 * Moves the current position of this Rectangle to the given Y co-ordinate
	 * @param y the new y co-ordinate of this Rectangle
	 */
	public void setYPosition(double y)
	{
		this.yBegin = y;
                this.yEnd = y + this.height;
	}
    /**
    * Gets the end x coordinate of the Rectangle.
    * @return The end x coordinate of the Rectangle.
    */
    public double getXEnd() {
        return xEnd;
    }
    /**
    * Gets the end y coordinate of the Rectangle.
    * @return The end y coordinate of the Rectangle.
    */
    public double getYEnd() {
        return yEnd;
    }

        
        
	/**
	 * Obtains the width of this Rectangle.
	 * @return the width of this Rectangle,in pixels.
	 */
	public double getWidth()
	{
		return width;
	}

	/**
	 * Obtains the height of this Rectangle.
	 * @return the height of this Rectangle,in pixels.
	 */
	public double getHeight()
	{
		return height;
	}

	public void setHeight(double h)
	{
		this.height = h;	
	}


	/**
	 * Obtains the colour of this Rectangle.
	 * @return a textual description of the colour of this Rectangle.
	 */
	public String getColour()
	{
		return colour;
	}

	/**
	 * Obtains the layer of this Rectangle.
	 * @return the layer of this Rectangle.
	 */
	public int getLayer()
	{
		return layer;
	}


    /** 
     * Create a rectangle of the given size, position and colour
     *
     * @param x The initial x co-ordinate of the Rectangle.
     * @param y The initial x co-ordinate of the Rectangle.
     * @param w The width of the Rectangle.
     * @param h The height of the Rectangle.
     * @param col The colour of the Rectangle.  Permissable BLACK, BLUE, CYAN, DARKGREY, GREY, GREEN, LIGHTGREY, MAGENTA, ORANGE, PINK, RED, WHITE, YELLOW, BROWN
     */
	public Rectangle(double x, double y, double w, double h, String col)
	{
		xBegin = x;
		yBegin = y;
                xEnd = x + w;
                yEnd = y + h;
		width = w;
		height = h;
		colour = col;
		layer = 0;
	}	

    /** 
     * Create a rectangle of the given size, position and colour
     *
     * @param x The initial x co-ordinate of the Rectangle.
     * @param y The initial x co-ordinate of the Rectangle.
     * @param w The width of the Rectangle.
     * @param h The height of the Rectangle.
     * @param col The colour of the Rectangle.  Permissable BLACK, BLUE, CYAN, DARKGREY, GREY, GREEN, LIGHTGREY, MAGENTA, ORANGE, PINK, RED, WHITE, YELLOW, BROWN
     * @param lay The layer of Rectangle - higher layers are drawn on top of lower layers.
     */
	public Rectangle(double x, double y, double w, double h, String col, int lay)
	{
		xBegin = x;
		yBegin = y;
                xEnd = x + w;
                yEnd = y + h;
		width = w;
		height = h;
		colour = col;
		layer = lay;
	}	
}
