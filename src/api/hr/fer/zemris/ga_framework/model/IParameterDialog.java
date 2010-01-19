package hr.fer.zemris.ga_framework.model;


import hr.fer.zemris.ga_framework.model.misc.Pair;


/**
 * Standard interface for parameter dialogs of
 * yet undefined types.
 * These types must implement interface <code>ISerializable</code>.
 * 
 * If implementers of algorithms decide they want to use
 * a specific and yet undefined type as a parameter for
 * their algorithm, then they should also implement this
 * interface.
 * 
 * Any implementation of this interface MUST BE a SWT Composite.
 * Otherwise, the implementation is NOT VALID!!!
 * This means that implementers must be introduced with SWT.
 * Reason why this isn't an abstract class: implementers that 
 * do not have swt libraries must also be able to use this api.
 * As such, two parameters are needed in ctors - parent Composite 
 * and style (int).
 * 
 * 
 * @author Axel
 *
 */
public interface IParameterDialog {

	/**
	 * Returns the value from the dialog.
	 * A copy of the value is always returned.
	 * 
	 * @return
	 */
	public ISerializable getValue();

	/**
	 * Sets the parameter controls with specified values.
	 * A deep copy should be created from the parameter,
	 * and the parameter dialog itself is responsible for
	 * this.
	 * 
	 * @param value
	 * Null value denotes default values for controls.
	 * 
	 * @throws IllegalArgumentException
	 * If the specified value is of incorrect type.
	 */
	public void setValue(ISerializable value);

	/**
	 * Returns dimensions needed for the dialog.
	 * This should be less than 800x600.
	 * 
	 * @return
	 * Dimensions for the dialog.
	 */
	public Pair<Integer, Integer> getDimensions();
	
	/**
	 * Returns the class of the object this parameter
	 * is used for setting.
	 * 
	 * @return
	 */
	public Class<? extends ISerializable> isUsedFor();

}











