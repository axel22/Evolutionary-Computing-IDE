package hr.fer.zemris.ga_framework.model;


/**
 * Allows or denies an object.
 * Additionally, provides data on how this is done.
 * 
 * Note: must implement equals and hashCode, otherwise
 * the implementation is not valid.
 * 
 * @author Axel
 *
 */
public interface ICriterium {
	
	/**
	 * Checks if object is allowed.
	 * 
	 * @param o
	 * @return
	 * True if object is allowed, false otherwise.
	 */
	public boolean isValid(Object o);
	
	/**
	 * 
	 * @return
	 */
	public String getDescription();
	
	public boolean equals(Object o);
	
	public int hashCode();
}














