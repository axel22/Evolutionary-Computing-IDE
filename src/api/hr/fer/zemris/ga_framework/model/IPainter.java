package hr.fer.zemris.ga_framework.model;


/**
 * Interface defining paint events that an algorithm
 * issues to the info listener.
 * Once the info listener receives such an object, it
 * will provide it with a canvas on which the object
 * will paint.
 * 
 * @author Axel
 *
 */
public interface IPainter {

	/**
	 * Paints on the given canvas.
	 * 
	 * @param canvas
	 */
	public void paint(ICanvas canvas);
	
}














