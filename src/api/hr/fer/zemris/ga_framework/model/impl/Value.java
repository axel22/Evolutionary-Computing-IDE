package hr.fer.zemris.ga_framework.model.impl;

import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IValue;




/**
 * Value class is a wrapper around values
 * and their parameter types.
 * A value class is composed of both a value
 * of some type and the respective parameter
 * type specified by the IParameter object.
 * 
 * @param <E>
 * This should typically be types other than
 * <code>ISerializable</code>.
 * 
 * @author Axel
 *
 */
public class Value implements IValue {
	
	/* static fields */

	/* private fields */
	private Object val;
	private IParameter param;

	
	/* ctors */

	/**
	 * Standard ctor. Creates a value based
	 * upon an object and parameter.
	 */
	public Value(Object v, IParameter p) {
		if (p == null) throw new NullPointerException("Parameter cannot be null.");
		if (v == null) throw new NullPointerException("Value cannot be null.");
		
		val = v;
		param = p;
	}
	
	
	
	/* methods */

	public IParameter parameter() {
		return param;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T value() {
		return (T)val;
	}

	public <T> void setValue(T val) {
		this.val = val;
	}

	public String getValueString() {
		return val.toString();
	}

	@Override
	public String toString() {
		return val.toString() + " (" + param.toString() + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Value)) return false;
		Value other = (Value) obj;
		return this.val.equals(other.val) && this.param.equals(other.param);
	}

	@Override
	public int hashCode() {
		return val.hashCode() << 16 + param.hashCode();
	}
	
}














