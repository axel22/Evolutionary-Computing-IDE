package hr.fer.zemris.ga_framework.model;


/**
 * Used to return the information about the algorithm during it's run.
 * An object implementing this interface will typically be passed to
 * an algorithm when calling it.
 * Through it, the algorithm can then return data during it's run, and
 * this data will be shown in GUI.
 * 
 * Implementers are advised that methods of this object may not
 * be called from the respective UI threads. Any valid implementation
 * takes care of this fact.
 * 
 * 
 * @author Axel
 *
 */
public interface IInfoListener {

	/**
	 * Sets how much of the algorithm is completed.
	 * 
	 * @param percent
	 */
	public void setPercentage(double percent);
	
	/**
	 * Sets a property.
	 * Should be called whenever some property
	 * changes.
	 * 
	 * @param propkey
	 * @param propval
	 */
	public void setProperty(String propkey, String propval);
	
	public void setConsoleColor(int r, int g, int b);
	
	/**
	 * Prints the text to console.
	 * 
	 * @param text
	 */
	public void print(String text);
	
	/**
	 * Prints text with newLine to console.
	 * 
	 * @param text
	 */
	public void println(String text);
	
	/**
	 * Clears console.
	 */
	public void clearConsole();
	
	/**
	 * Removes a property.
	 * Nothing is done if key doesn't exist.
	 * 
	 * @param key
	 */
	public void removeProperty(String key);
	
	/**
	 * Clears all properties that have been added.
	 */
	public void clearProperties();
	
	/**
	 * Sets whether or not a canvas for custom
	 * presentation shall be used.
	 * By default, the canvas will not be used.
	 * 
	 * @param shouldUse
	 */
	public void useCanvas(boolean shouldUse);
	
	/**
	 * Sends a paint event encapsulated in an object.
	 * 
	 * @param painter
	 */
	public void paint(IPainter painter);
	
}














