package hr.fer.zemris.ga_framework.model.impl.parameters;

import hr.fer.zemris.ga_framework.model.ConstraintTypes;
import hr.fer.zemris.ga_framework.model.ICriterium;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.ParameterTypes;

import java.util.List;



/**
 * Implementation of the <code>IParameter</code> interface
 * for primitive types, i.e. those that are not of
 * parameter type <code>ISERIALIZABLE</code>.
 * 
 * @author Axel
 *
 */
public class PrimitiveParameter implements IParameter {
		
	/* static fields */

	/* private fields */
	private String name, description;
	private ParameterTypes paramtype;
	private ConstraintTypes constrtype;
	private List<Object> allowed;
	private ICriterium criterium;
	

	/* ctors */
	
	/**
	 * Standard ctor.
	 * 
	 */
	public PrimitiveParameter(String parName, String desc, ParameterTypes typeOfParam) {
		name = parName;
		description = desc;
		paramtype = typeOfParam;
		constrtype = null;
		allowed = null;
		criterium = null;
		
		// set value class
		if (paramtype == ParameterTypes.ISERIALIZABLE) throw new 
		IllegalArgumentException("Only supporting primitive parameter types!");
	}
	
	/**
	 * Ctor for ENUMERATION constrained parameters.
	 * 
	 * @param parName
	 * @param desc
	 * @param typeOfParam
	 * @param allowed
	 */
	public PrimitiveParameter(String parName, String desc, ParameterTypes typeOfParam, 
			List<Object> allowedVals)
	{
		name = parName;
		description = desc;
		paramtype = typeOfParam;
		constrtype = ConstraintTypes.ENUMERATION;
		allowed = allowedVals;
		criterium = null;
		
		// set value class
		if (paramtype == ParameterTypes.ISERIALIZABLE) throw new 
		IllegalArgumentException("Only supporting primitive parameter types!");
	}
	
	/**
	 * Ctor for CRITERIUM constrained parameters.
	 * 
	 * @param parName
	 * @param desc
	 * @param typeOfParam
	 * @param constr
	 */
	public PrimitiveParameter(String parName, String desc, ParameterTypes typeOfParam, 
			ICriterium crit)
	{
		name = parName;
		description = desc;
		paramtype = typeOfParam;
		constrtype = ConstraintTypes.CRITERIUM;
		allowed = null;
		criterium = crit;
		
		// set value class
		if (paramtype == ParameterTypes.ISERIALIZABLE) throw new 
		IllegalArgumentException("Only supporting primitive parameter types!");
	}
	
	

	/* methods */
	
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
		return paramtype;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getValueClass() {
		return (Class<T>)paramtype.getValueClass();
	}

	public String getValueClassName() {
		return paramtype.getValueClass().getName();
	}

	public boolean isValueValid(Object value) {
		if (constrtype == null) return true;
		if (constrtype == ConstraintTypes.ENUMERATION) return allowed.contains(value);
		if (constrtype == ConstraintTypes.CRITERIUM) return criterium.isValid(value);
		throw new IllegalStateException("We shouldn't get here.");
	}

	public String serialize(Object value) throws IllegalArgumentException {
		if (!isValueValid(value)) throw new IllegalArgumentException("Not allowed by constraint (" + value + ").");
		try {
			return paramtype.serialize(value);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Incorrect class.");
		}
	}
	
	public Object deserialize(String str) throws IllegalArgumentException {
		try {
			return paramtype.deserialize(str);
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not deserialize.", e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof PrimitiveParameter)) return false;
		PrimitiveParameter other = (PrimitiveParameter)obj;
		if (!name.equals(other.name) || !description.equals(other.description)) return false;
		if (!paramtype.equals(other.paramtype)) return false;
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
		int hash = name.hashCode() << 24 + description.hashCode() << 16 + paramtype.hashCode() << 8;
		
		if (constrtype != null) hash += constrtype.hashCode();
		if (criterium != null) hash += criterium.hashCode();
		if (allowed != null) hash += allowed.hashCode();
		
		return hash;
	}
	
}














