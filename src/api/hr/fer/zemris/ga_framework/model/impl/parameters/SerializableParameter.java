package hr.fer.zemris.ga_framework.model.impl.parameters;

import hr.fer.zemris.ga_framework.model.ConstraintTypes;
import hr.fer.zemris.ga_framework.model.ICriterium;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.ParameterTypes;

import java.util.List;




/**
 * Implementation for <code>IParameter</code> interface
 * for the <code>ISERIALIZABLE</code> parameter type.
 * 
 * @author Axel
 *
 */
public class SerializableParameter implements IParameter {
	
	/* static fields */

	/* private fields */
	private String name, description;
	private Class<?> valcls;
	private ConstraintTypes constrtype;
	private List<Object> allowed;
	private ICriterium criterium;
	

	/* ctors */
	
	/**
	 * Standard ctor - no constraints.
	 * 
	 * @param cls
	 * Class of the stored value. Must implement <code>ISerializable</code>.
	 */
	public SerializableParameter(String pname, String desc, Class<?> cls) {
		if (!ISerializable.class.isAssignableFrom(cls)) throw new
			IllegalArgumentException("Only works for classes implementing ISerializable!");
		
		name = pname;
		description = desc;
		valcls = cls;
		constrtype = null;
		allowed = null;
		criterium = null;
	}
	
	/**
	 * Ctor for ENUMERATION constraints.
	 * 
	 * @param pname
	 * @param desc
	 * @param cls
	 * Class of the stored value. Must implement <code>ISerializable</code>.
	 */
	public SerializableParameter(String pname, String desc, Class<?> cls,
			List<Object> allowedVals)
	{
		if (!ISerializable.class.isAssignableFrom(cls)) throw new
			IllegalArgumentException("Only works for classes implementing ISerializable!");
		
		name = pname;
		description = desc;
		valcls = cls;
		constrtype = ConstraintTypes.ENUMERATION;
		allowed = allowedVals;
		criterium = null;
	}
	
	/**
	 * Ctor for CRITERIUM constraints.
	 * 
	 * @param pname
	 * @param desc
	 * @param cls
	 * Class of the stored value. Must implement <code>ISerializable</code>.
	 */
	public SerializableParameter(String pname, String desc, Class<?> cls, ICriterium crit) {
		if (!ISerializable.class.isAssignableFrom(cls)) throw new
			IllegalArgumentException("Only works for classes implementing ISerializable!");
		
		name = pname;
		description = desc;
		valcls = cls;
		constrtype = ConstraintTypes.CRITERIUM;
		allowed = null;
		criterium = crit;
	}
	
	

	/* methods */
	
	public Object deserialize(String str) throws IllegalArgumentException {
		ISerializable obj = null;
		try {
			obj = (ISerializable)valcls.newInstance();
			obj = obj.deserialize(str);
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot deserialize.", e);
		}
		
		return obj;
	}
	
	public String serialize(Object value) throws IllegalArgumentException {
		if (!value.getClass().equals(valcls)) throw new 
			IllegalArgumentException("Incorrect class type.");
		if (!isValueValid(value)) throw new IllegalArgumentException("Not allowed by constraint.");
		ISerializable ser = (ISerializable)value;
		return ser.serialize();
	}

	public List<Object> getAllowed() {
		return allowed;
	}

	public String getConstraintDescription() {
		if (constrtype != null) {
			if (criterium != null) return criterium.getDescription();
			return "Value is restricted to a finite set.";
		}
		return "Value is not constrained.";
	}

	public ConstraintTypes getConstraint() {
		return constrtype;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public ParameterTypes getParamType() {
		return ParameterTypes.ISERIALIZABLE;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getValueClass() {
		return (Class<T>)valcls;
	}

	public String getValueClassName() {
		return valcls.getName();
	}

	public boolean isValueValid(Object value) {
		if (constrtype == null) return true;
		if (constrtype == ConstraintTypes.ENUMERATION) return allowed.contains(value);
		if (constrtype == ConstraintTypes.CRITERIUM) return criterium.isValid(value);
		throw new IllegalStateException("We shouldn't get here.");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SerializableParameter)) return false;
		SerializableParameter other = (SerializableParameter)obj;
		if (!name.equals(other.name) || !description.equals(other.description)) return false;
		if (constrtype != other.constrtype) return false;
		if (constrtype == null) return true;
		switch (constrtype) {
		case CRITERIUM:
			return this.criterium.equals(other.criterium);
		case ENUMERATION:
			return this.allowed.equals(other.allowed);
		}
		throw new IllegalStateException("It seems more constraint types have been added.");
	}

	@Override
	public int hashCode() {
		int hash = name.hashCode() << 24 + description.hashCode() << 16;
		
		if (constrtype != null) hash += constrtype.hashCode();
		if (criterium != null) hash += criterium.hashCode();
		if (allowed != null) hash += allowed.hashCode();
		
		return hash;
	}
}














