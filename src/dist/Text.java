package dist;
/**
 * Models a simple piece of text. 
 * This class represents a Text object. When combined with the GameArena class,
 * instances of the Text class can be displayed on the screen to show display
 * an appropriate piece of text.
 */
public class Text
{
	// The following instance variables define the
	// information needed to represent a line.
	// Feel free to more instance variables if you think it will 
	// support your work... 
	
	private double xPosition;			// The X coordinate of the start of this text 
	private double yPosition;			// The Y coordinate of the start of this text 
	private int size;					// The font size of this text 
	private int layer;					// The layer this text is drawn on
	private String text;				// The actual text to display
	private String colour;				// The colour of this text


										// Permissable colours are:
										// BLACK, BLUE, CYAN, DARKGREY, GREY,
										// GREEN, DARKGREEN, LIGHTGREY, MAGENTA, ORANGE,
										// PINK, RED, WHITE, YELLOW, BROWN 

	/**
	 * Obtains the position of this text on the X axis.
	 * @return the X coordinate of this text within the GameArena.
	 */
	public double getXPosition()
	{
		return xPosition;
	}

	/**
	 * Obtains the position of this text on the Y axis.
	 * @return the Y coordinate of this text within the GameArena.
	 */
	public double getYPosition()
	{
		return yPosition;
	}


	/**
	 * Obtains the width of this text.
	 * @return the width of this text, in points.
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Obtains the colour of this Text.
	 * @return a textual description of the colour of this Text.
	 */
	public String getColour()
	{
		return colour;
	}

	/**
	 * Obtains the actual text contained in this object.
	 * @return a the text to be displayed.
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Obtains the layer of this Text.
	 * @return the layer of this Text.
	 */
	public int getLayer()
	{
		return layer;
	}

	public Text(String t, int s, double x, double y, String col, int lay)
	{
		xPosition = x;
		yPosition = y;
		size = s;
		text = t;
		colour = col;
		layer = lay;
	}	

	public void setText(String text)
	{
		this.text = text;
	}

	public void setColour(String colour)
	{
		this.colour = colour;
	}
}
