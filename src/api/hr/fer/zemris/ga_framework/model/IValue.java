package hr.fer.zemris.ga_framework.model;


/**
 * Value interface is a wrapper around values
 * and their parameter types.
 * A value is composed of both a reference to
 * an object of some type and the respective 
 * parameter type specified by the IParameter
 * object.
 * Unlike the <code>IParameter</code> interface,
 * implementation of this interface isn't supposed
 * to be immutable.
 * Must implement equals and hashCode.
 * 
 * @author Axel
 *
 */
public interface IValue {

	/**
	 * @return
	 * Returns the parameter associated with
	 * this value.
	 */
	public IParameter parameter();
	
	/**
	 * Sets the value to this value object.
	 * 
	 * @param <T>
	 * @param val
	 * @throws ClassCastException
	 * If the value is not of correct type.
	 */
	public <T> void setValue(T val);
	
	/**
	 * Returns the value associated with this object.
	 * 
	 * @param <T>
	 * @return
	 */
	public <T> T value();
	
	/**
	 * Returns a short string associated with this
	 * value. It should be descriptive - it shouldn't
	 * be a serialization of the value. It may not
	 * even be unique, although this is recommended
	 * if possible.
	 * This will be used in parameter editors in GUI.
	 * 
	 * @return
	 */
	public String getValueString();
	
	public boolean equals(Object o);
	
	public int hashCode();
}














