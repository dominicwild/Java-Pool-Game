package dist;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import java.util.Collections;


/**
 * This class provides a simple window in which grahical objects can be drawn. 
 * @author Joe Finney
 */
public class GameArena extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener
{
	// Size of playarea
	private int arenaWidth;
	private int arenaHeight;

	// Size of redraw area
	private int canvasWidth;
	private int canvasHeight;

	private boolean exiting = false;

	private ArrayList<Object> things = new ArrayList<Object>();

	private boolean up = false;
	private boolean down = false;
	private boolean left = false;
	private boolean right = false;
	private boolean spacebar = false;
	private boolean shift = false;
	private boolean leftMouse = false;
	private boolean rightMouse = false;
	private int mouseX = 0;
	private int mouseY = 0;

	private JFrame window;

	/**
	 * Create a view of a GameArena.
	 * The GameArena will be created with the default size of 300x300 pixels.
     *
     * @param width The width of the window to create
     * @param height The width of the window to create
	 */
	public GameArena(int width, int height)
	{
		this.window = new JFrame();
		this.setSize(width, height);

		window.setTitle("Let's Play!");
		window.setBackground(Color.BLACK);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(this);
		window.setVisible(true);		

		Thread t = new Thread(this);
		t.start();

		window.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public void run() {
		try {
			while (true) {
				this.repaint();
				Thread.sleep(10);
			}
		} catch (InterruptedException iex) {}
	}

	/**
	 * Update the size of the GameArena.
	 *
	 * @param width the new width of the window in pixels.
	 * @param height the new height of the window in pixels.
	 */
	public void setSize(int width, int height)
	{
		this.arenaWidth = width;
		this.arenaHeight = height;

		this.canvasWidth = width+this.getInsets().left+this.getInsets().right;
		this.canvasHeight = height+this.getInsets().top+this.getInsets().bottom;
		window.setSize(canvasWidth, canvasHeight);
	}	

	/**
	 * Close this GameArena window.
	 * 
	 */
	public void exit()
	{
		this.exiting = true;
	}

	/**
	 * A method called by the operating system to draw onto the screen - <p><B>YOU DO NOT (AND SHOULD NOT) NEED TO CALL THIS METHOD.</b></p>
	 */
	public void paint (Graphics gr)
	{
		if (this.getCanvasWidth() <= 0 || this.getCanvasHeight() <=0)
			return;

		BufferedImage i = new BufferedImage(this.getCanvasWidth(), this.getCanvasHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = i.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		synchronized (this)
		{
			if (!this.exiting)
			{
				g.clearRect(0,0,this.getCanvasWidth(), this.getCanvasHeight());

				for (Object o : things)
				{
					if (o instanceof Circle)
					{
						Circle b = (Circle) o;
						g.setColor(this.getColourFromString(b.getColour()));
						g.fillOval((int)(b.getXPosition()-b.getSize()/2), (int)(b.getYPosition()-b.getSize()/2), (int)b.getSize(), (int)b.getSize());
					}

					if (o instanceof Rectangle)
					{
						Rectangle r = (Rectangle) o;
						g.setColor(this.getColourFromString(r.getColour()));
						g.fillRect((int)r.getXPosition(), (int)r.getYPosition(), (int)r.getWidth(), (int)r.getHeight());
					}

					if (o instanceof Line)
					{
						Line l = (Line) o;
						g.setColor(this.getColourFromString(l.getColour()));
						g.setStroke(new BasicStroke((float)l.getWidth()));

                		g.draw(new Line2D.Float((float)l.getXStart(), (float)l.getYStart(), (float)l.getXEnd(), (float)l.getYEnd()));
					}

					if (o instanceof Text)
					{
						Text t = (Text) o;
						g.setFont(new Font("SansSerif", Font.BOLD, t.getSize()));
						g.setColor(this.getColourFromString(t.getColour()));
						g.drawString(t.getText(),(float)t.getXPosition(), (float)t.getYPosition());
					}
				}

			}

			gr.drawImage(i, this.getInsets().left, this.getInsets().top, this);
			try{ Thread.sleep(0); } catch (Exception e) {} 
		}
	}

	//
	// Shouldn't really handle colour this way, but the student's haven't been introduced
	// to constants properly yet, hmmm....
	// 
	private Color getColourFromString(String col)
	{
		Color colour = Color.WHITE;
		col = col.toUpperCase();

		if (col.equals("BLACK"))
			colour = Color.BLACK;	

		if (col.equals("BLUE"))
			colour = Color.BLUE;	

		if (col.equals("CYAN"))
			colour = Color.CYAN;	

		if (col.equals("DARKGREY"))
			colour = Color.DARK_GRAY;	

		if (col.equals("GREY"))
			colour = Color.GRAY;	

		if (col.equals("GREEN"))
			colour = Color.GREEN;	

		if (col.equals("LIGHTGREY"))
			colour = Color.LIGHT_GRAY;	
				
		if (col.equals("MAGENTA"))
			colour = Color.MAGENTA;	

		if (col.equals("ORANGE"))
			colour = Color.ORANGE;	

		if (col.equals("PINK"))
			colour = Color.PINK;	

		if (col.equals("RED"))
			colour = Color.RED;	
		
		if (col.equals("WHITE"))
			colour = Color.WHITE;	

		if (col.equals("YELLOW"))
			colour = Color.YELLOW;	

		if (col.equals("BROWN"))
			colour = new Color(109, 39, 5);	

		if (col.equals("DARKGREEN"))
			colour = new Color(0, 100, 0);
                
                if (col.equals("LIGHTBLACK"))
			colour = new Color(40, 40, 40);

		return colour;
	}
	
	/**
	 * Adds a given Object to the drawlist, maintaing z buffering order. 
	 *
	 * @param o the object to add to the drawlist.
	 */
	private void addThing(Object o, int layer)
	{
		boolean added = false;

		synchronized (this)
		{
			if (things.size() > 100000)
			{
				System.out.println("\n\n");
				System.out.println(" ********************************************************* ");
				System.out.println(" ***** Only 100000 Objects Supported per Game Arena! ***** ");
				System.out.println(" ********************************************************* ");
				System.out.println("\n");
				System.out.println("-- Joe\n\n");

				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
			}
			else
			{
				// Try to insert this object into the list.
				for (int i=0; i<things.size(); i++)
				{
					int l = 0;
					Object obj = things.get(i);

					if (obj instanceof Circle)
						l = ((Circle)obj).getLayer();

					if (obj instanceof Rectangle)
						l = ((Rectangle)obj).getLayer();

					if (obj instanceof Line)
						l = ((Line)obj).getLayer();

					if (layer <= l)
					{
						things.add(i,o);
						added = true;
						break;
					}
				}

				// If there are no items in the list with an equivalent or higher layer, append this object to the end of the list.
				if (!added)
					things.add(o);
			}
		}
	}

	/**
	 * Remove an object from the drawlist. 
	 *
	 * @param o the object to remove from the drawlist.
	 */
	private void removeObject(Object o)
	{
		synchronized (this)
		{
			things.remove(o);
		}
	}

	/**
	 * Adds a given Ball to the GameArena. 
	 * Once a Ball is added, it will automatically appear on the window. 
	 *
	 * @param b the ball to add to the GameArena.
	 */
	public void addBall(Circle b)
	{
		this.addThing(b, b.getLayer());
	}

	/**
	 * Adds a given Rectangle to the GameArena. 
	 * Once a Rectangle is added, it will automatically appear on the window. 
	 *
	 * @param r the rectangle to add to the GameArena.
	 */
	public void addRectangle(Rectangle r)
	{
		this.addThing(r, r.getLayer());
	}

	/**
	 * Adds a given Line to the GameArena. 
	 * Once a Line is added, it will automatically appear on the window. 
	 *
	 * @param l the line to add to the GameArena.
	 */
	public void addLine(Line l)
	{
		this.addThing(l, l.getLayer());
	}

	/**
	 * Adds a given Text object to the GameArena. 
	 * Once a Text object is added, it will automatically appear on the window. 
	 *
	 * @param t the text object to add to the GameArena.
	 */
	public void addText(Text t)
	{
		this.addThing(t, t.getLayer());
	}

	/**
	 * Remove a Rectangle from the GameArena. 
	 * Once a Rectangle is removed, it will no longer appear on the window. 
	 *
	 * @param r the rectangle to remove from the GameArena.
	 */
	public void removeRectangle(Rectangle r)
	{
		this.removeObject(r);
	}

	/**
	 * Remove a Ball from the GameArena. 
	 * Once a Ball is removed, it will no longer appear on the window. 
	 *
	 * @param b the ball to remove from the GameArena.
	 */
	public void removeBall(Circle b)
	{
		this.removeObject(b);
	}

	/**
	 * Remove a Line from the GameArena. 
	 * Once a Line is removed, it will no longer appear on the window. 
	 *
	 * @param l the line to remove from the GameArena.
	 */
	public void removeLine(Line l)
	{
		this.removeObject(l);
	}

	/**
	 * Remove a Text object from the GameArena. 
	 * Once a Text object is removed, it will no longer appear on the window. 
	 *
	 * @param t the text object to remove from the GameArena.
	 */
	public void removeText(Text t)
	{
		this.removeObject(t);
	}

	/**
	 * Pause for a 1/50 of a second. 
	 * This method causes your program to delay for 1/50th of a second. You'll find this useful if you're trying to animate your application.
	 *
	 */
	public void pause()
	{
		try { Thread.sleep(20); }
		catch (Exception e) {};
	}

 	public void keyPressed(KeyEvent e) 
	{
		int code = e.getKeyCode();

		if (code == KeyEvent.VK_UP)
			up = true;		
		if (code == KeyEvent.VK_DOWN)
			down = true;		
		if (code == KeyEvent.VK_LEFT)
			left = true;		
		if (code == KeyEvent.VK_RIGHT)
			right = true;		
		if (code == KeyEvent.VK_SPACE)
			spacebar = true;		
		if (code == KeyEvent.VK_SHIFT)
			shift = true;		
	}
 	
	public void keyReleased(KeyEvent e) 
	{
		int code = e.getKeyCode();

		if (code == KeyEvent.VK_UP)
			up = false;		
		if (code == KeyEvent.VK_DOWN)
			down = false;		
		if (code == KeyEvent.VK_LEFT)
			left = false;		
		if (code == KeyEvent.VK_RIGHT)
			right = false;		
		if (code == KeyEvent.VK_SPACE)
			spacebar = false;		
		if (code == KeyEvent.VK_SHIFT)
			shift = false;		
	}

 	public void keyTyped(KeyEvent e) 
	{
	}

	public void mousePressed(MouseEvent e) 
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			this.leftMouse = true;

		if (e.getButton() == MouseEvent.BUTTON3)
			this.rightMouse = true;
	}

	public void mouseReleased(MouseEvent e) 
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			this.leftMouse = false;

		if (e.getButton() == MouseEvent.BUTTON3)
			this.rightMouse = false;
	}

	public void mouseEntered(MouseEvent e) 
	{
	}

	public void mouseExited(MouseEvent e) 
	{
	}

	public void mouseClicked(MouseEvent e) 
	{
	}

	public void mouseMoved(MouseEvent e) 
	{
		mouseX = e.getX();	
		mouseY = e.getY();	
	}

	public void mouseDragged(MouseEvent e) 
	{
	}

	/** 
	 * Gets the width of the GameArena window, in pixels.
	 * @return the width in pixels
	 */
	public int getArenaWidth()
	{
		return arenaWidth;
	}

	/** 
	 * Gets the height of the GameArena window, in pixels.
	 * @return the height in pixels
	 */
	public int getArenaHeight()
	{
		return arenaHeight;
	}

	private int getCanvasHeight()
	{
		return canvasHeight;
	}

	private int getCanvasWidth()
	{
		return canvasWidth;
	}

	/** 
	 * Determines if the user is currently pressing the cursor up button.
	 * @return true if the up button is pressed, false otherwise.
	 */
	public boolean upPressed()
	{
		return up;
	}

	/** 
	 * Determines if the user is currently pressing the cursor down button.
	 * @return true if the down button is pressed, false otherwise.
	 */
	public boolean downPressed()
	{
		return down;
	}

	/** 
	 * Determines if the user is currently pressing the cursor left button.
	 * @return true if the left button is pressed, false otherwise.
	 */
	public boolean leftPressed()
	{
		return left;
	}

	/** 
	 * Determines if the user is currently pressing the cursor right button.
	 * @return true if the right button is pressed, false otherwise.
	 */
	public boolean rightPressed()
	{
		return right;
	}

	/** 
	 * Determines if the user is currently pressing the spacebar.
	 * @return true if the spacebar is pressed, false otherwise.
	 */
	public boolean spacebarPressed()
	{
		return spacebar;
	}

	/** 
	 * Determines if the user is currently pressing the shift key.
	 * @return true if the shift key is pressed, false otherwise.
	 */
	public boolean shiftPressed()
	{
		return shift;
	}

	/** 
	 * Determines if the user is currently pressing the left mouse button.
	 * @return true if the left mouse button is pressed, false otherwise.
	 */
	public boolean leftMousePressed()
	{
		return leftMouse;
	}

	/** 
	 * Determines if the user is currently pressing the right mouse button.
	 * @return true if the right mouse button is pressed, false otherwise.
	 */
	public boolean rightMousePressed()
	{
		return rightMouse;
	}

	/**
	 * Gathers location informaiton on the mouse pointer.
	 * @return the current X coordinate of the mouse pointer in the GameArena.
	 */
	public int getMousePositionX()
	{
		return mouseX;
	}

	/**
	 * Gathers location informaiton on the mouse pointer.
	 * @return the current Y coordinate of the mouse pointer in the GameArena.
	 */
	public int getMousePositionY()
	{
		return mouseY;
	}
}
