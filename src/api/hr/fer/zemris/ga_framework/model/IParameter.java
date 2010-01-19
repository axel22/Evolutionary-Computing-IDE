package hr.fer.zemris.ga_framework.model;

import java.util.List;







/**
 * Describes a parameter of an algorithm.
 * Each parameter describes some value.
 * Each algorithm has a number of parameters.
 * 
 * NOTE: instances of classes implementing this
 * interface MUST BE immutable.
 * 
 * NOTE: implementations of this interface must
 * include hashCode and equals.
 * 
 * @author Axel
 *
 */
public interface IParameter {
	
	/**
	 * @return
	 * Returns the name of this parameter.
	 */
	public String getName();
	
	/**
	 * Returns a description of the parameter, typically
	 * used in GUIs for tooltips.
	 * 
	 * @return
	 * A description.
	 */
	public String getDescription();
	
	/**
	 * @return
	 * Returns the type of the parameter.
	 */
	public ParameterTypes getParamType();
	
	/**
	 * @param <T>
	 * @return
	 * Returns the class object of the stored type.
	 */
	public <T extends Object> Class<T> getValueClass();
	
	/**
	 * @return
	 * Returns the class name of the value
	 * this parameter represents.
	 * This will be used in conjunction with
	 * <code>ParameterTypes.ISERIALIZABLE</code>.
	 */
	public String getValueClassName();
	
	/**
	 * Returns constraint type associated with this parameter.
	 * Constraints are used to describe which values are valid
	 * for this parameter. Constraint type is useful for GUIs
	 * in which user enters data.
	 * 
	 * @return
	 * Null if there is no constraint. An element
	 * of the enumeration otherwise.
	 */
	public ConstraintTypes getConstraint();
	
	/**
	 * Returns constraint's description.
	 * 
	 * @return
	 * A description of the value beginning with:<br/>
	 * <code>"Value is ..."</code><br/>
	 * or<br/>
	 * <code>"Value is not constrained."</code><br/>
	 * if there is no constraint.
	 */
	public String getConstraintDescription();
	
	/**
	 * If constraint type is <code>ConstraintTypes.ENUMERATION</code>,
	 * this method returns a finite set of allowed values.
	 * 
	 * @return
	 * A set of allowed values. This set is either a deep copy of the
	 * allowed set of values, or it is an unmodifiable collection (this
	 * is recommended for implementers).
	 * Null is returned if constraint is not enumeration, or doesn't exist.
	 */
	public List<Object> getAllowed();
	
	/**
	 * Returns whether the value is allowed by the constraint
	 * of this parameter.
	 * 
	 * @param value
	 * @return
	 * True if constraint allows the value, false otherwise.
	 * If there is no constraint, true is returned.
	 * If the object has an incorrect type, false is returned.
	 */
	public boolean isValueValid(Object value);
	
	/**
	 * Serializes a value to a string. Checks if the
	 * object is of correct type and allowed by constraint.
	 * 
	 * 
	 * @param value
	 * Value corresponding by it's type to this
	 * parameter.
	 * @return
	 * The string version of the value.
	 * NOTE TO IMPLEMENTERS: NEVER include a "]]>" within a string.
	 * NEVER, NEVER do this. Escape the characters if
	 * you must, but never include this character sequence
	 * within a string.
	 * @throws IllegalArgumentException
	 * If the value is not of the corresponding type or is
	 * not allowed by the constraint.
	 */
	public String serialize(Object value) throws IllegalArgumentException;
	
	/**
	 * Deserializes an object from it's serialized string.
	 * 
	 * @param str
	 * Serialized string.
	 * @return
	 * Returns an object corresponding to the serialized
	 * string.
	 * @throws IllegalArgumentException
	 * If the value is not allowed by the constraint, or
	 * the string does not much the serialized version of
	 * any value.
	 */
	public Object deserialize(String str) throws IllegalArgumentException;
	
	public int hashCode();
	
	public boolean equals(Object other);
	
}














