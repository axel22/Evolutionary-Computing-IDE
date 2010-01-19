package hr.fer.zemris.ga_framework.model;



/**
 * Interface to an element representing a canvas
 * for drawing custom data. 
 * 
 * Implementers are advised that methods of this object may not
 * be called from the respective UI threads. Any valid implementation
 * takes care of this fact.
 * 
 * @author Axel
 *
 */
public interface ICanvas {

	/**
	 * Clears the content of the canvas.
	 */
	public void clearCanvas();
	
	/**
	 * A 'not available' insignia is shown on the canvas.
	 * This is the default state of the canvas.
	 */
	public void showNotAvailable(boolean notAvailable);
	
	/**
	 * Sets the canvas size. Changing the
	 * canvas size clears the current image.
	 * It also counts as a flip.
	 * See <code>flip</code>.
	 * 
	 * @param width
	 * @param height
	 */
	public void setCanvasSize(int width, int height);
	
	/**
	 * Flips the buffers, causing the changes to become visible
	 * on the canvas.
	 * Should be called after logically similar draw operations
	 * (for instance, after drawing a frame).
	 */
	public void flip();
	
	public void setDrawColor(int r, int g, int b);
	
	public void setFillColor(int r, int g, int b);
	
	/**
	 * A line is drawn between (x1, y1) and (x2, y2).
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawLine(int x1, int y1, int x2, int y2);
	
	/**
	 * Draws an oval.
	 * 
	 * @param x
	 * @param y
	 * @param a
	 * @param b
	 */
	public void drawOval(int x, int y, int a, int b);
	
	/**
	 * Fills an oval.
	 * 
	 * @param x
	 * @param y
	 * @param a
	 * @param b
	 */
	public void fillOval(int x, int y, int r1, int r2);
	
	/**
	 * Draws some text.
	 * 
	 * @param text
	 * @param x
	 * @param y
	 */
	public void drawText(String text, int x, int y, boolean centerTextAroundSpot);
	
}














