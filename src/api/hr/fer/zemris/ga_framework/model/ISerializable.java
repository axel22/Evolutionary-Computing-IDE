package hr.fer.zemris.ga_framework.model;

import java.util.List;




/**
 * 
 * An interface describing the framework's
 * serializable objects.
 * Serialization and deserialization must be
 * implemented manually for each new type of
 * object.
 * Both methods of this interface must be
 * bijections.
 * Any object implementing this interface
 * must be immutable.
 * Any class implementing this interface
 * must have a default ctor (ctor with no
 * parameters).
 * 
 * @author Axel
 *
 */
public interface ISerializable extends Comparable<Object> {

	public boolean hasMax();
	
	public boolean hasMin();
	
	public boolean hasMedian();
	
	public boolean hasAverage();
	
	public boolean hasStandardDeviation();

	public boolean hasSum();
	
	public boolean isComparable();
	
	public Object max(List<? extends Object> objs);
	
	public Object min(List<? extends Object> objs);
	
	public Object median(List<? extends Object> objs);
	
	public Object average(List<? extends Object> objs);

	public Object stddev(List<? extends Object> objs);
	
	public Object sum(List<? extends Object> objs);
	
	/**
	 * Serializes this object's contents.
	 * 
	 * @return
	 * A string with enough information for
	 * the <code>deserialize</code> method
	 * to perform deserialization.
	 * 
	 * NOTE: NEVER include a "]]>" within a string.
	 * NEVER, NEVER do this. Escape the characters if
	 * you must, but never include this character sequence
	 * within a string.
	 */
	public String serialize();
	
	/**
	 * Returns a deep copy of this object.
	 * 
	 * @return
	 * A deep copy of the object.
	 */
	public ISerializable deepCopy();
	
	/**
	 * The toString method returns a string
	 * readable for humans in this case.
	 * It should be unique if possible, and
	 * it may not be equal to the value returned
	 * by the <code>serialize</code> method.
	 * It should not be too long, for the sake
	 * of the human reader.
	 * 
	 * @return
	 */
	public String toString();
	
	/**
	 * Deserializes the contents of the object
	 * by reading it's respective string representation.
	 * 
	 * @param s
	 * The string representation, as returned
	 * by the <code>serialize</code> method.
	 * @return
	 * Returns a new object which is a deserialized version
	 * of the specified object.
	 * @throws IllegalArgumentException
	 * If the provided string is not a deserialization
	 * of an object of this implementation.
	 */
	public ISerializable deserialize(String s);
	
	public boolean equals(Object o);
	
	public int hashCode();
	
}














