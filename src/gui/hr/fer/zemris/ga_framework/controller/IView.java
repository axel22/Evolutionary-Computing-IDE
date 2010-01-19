package hr.fer.zemris.ga_framework.controller;




/**
 * Represents a view in the editor.
 * 
 * @author Axel
 *
 */
public interface IView extends IListener {
	
	/**
	 * @return
	 * Returns the name of the view
	 * which may not be unique.
	 */
	public String getViewName();
	
	/**
	 * @return
	 * Returns the id of the view, which
	 * is set up by the factory upon view
	 * creation. This id will be unique.
	 */
	public long getId();
	
	
	
}
















