package hr.fer.zemris.ga_framework.model.impl.parameters;

import hr.fer.zemris.ga_framework.model.ConstraintTypes;
import hr.fer.zemris.ga_framework.model.ICriterium;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.model.impl.ObjectList;

import java.util.List;


/**
 * Parameter for lists of primitive objects (object of parameter
 * type different than ISERIALIZABLE).
 * 
 * @author Axel
 *
 */
public class ListParameter implements IParameter {
	
	/* static fields */

	/* private fields */
	private String name, desc;
	private ConstraintTypes constraintType;
	@SuppressWarnings("unused")
	private ParameterTypes ptype;
	private ICriterium criterium;

	/* ctors */
	
	public ListParameter(String paramName, String description, ParameterTypes pt) {
		name = paramName;
		desc = description;
		ptype = pt;
		constraintType = null;
		criterium = null;
	}
	
	public ListParameter(String paramName, String description, ParameterTypes pt, ICriterium c) {
		name = paramName;
		desc = description;
		ptype = pt;
		constraintType = ConstraintTypes.CRITERIUM;
		criterium = c;
	}

	/* methods */
	
	public Object deserialize(String str) throws IllegalArgumentException {
		ObjectList obj = new ObjectList();
		try {
			obj = (ObjectList) obj.deserialize(str);
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot deserialize.", e);
		}
		
		return obj;
	}

	public List<Object> getAllowed() {
		return null;
	}

	public ConstraintTypes getConstraint() {
		return constraintType;
	}

	public String getConstraintDescription() {
		if (constraintType != null) {
			if (constraintType.equals(ConstraintTypes.CRITERIUM)) return criterium.getDescription();
			else return "Value is restricted to a finite set.";
		}
		return "Value is not constrained.";
	}

	public String getDescription() {
		return desc;
	}

	public String getName() {
		return name;
	}

	public ParameterTypes getParamType() {
		return ParameterTypes.ISERIALIZABLE;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getValueClass() {
		return (Class<T>) ObjectList.class;
	}

	public String getValueClassName() {
		return ObjectList.class.getName();
	}

	public boolean isValueValid(Object value) {
		if (!(value instanceof ObjectList)) return false;
		if (criterium != null) return criterium.isValid(value);
//		if (allowedList != null) {
//			ObjectList olst = (ObjectList) value;
//			for (Object o : olst) {
//				if (!allowedList.contains(o)) return false;
//			}
//			return true;
//		}
		else return true;
	}

	public String serialize(Object value) throws IllegalArgumentException {
		if (!value.getClass().equals(ObjectList.class)) throw new 
			IllegalArgumentException("Incorrect class type.");
		if (!isValueValid(value)) throw new IllegalArgumentException("Not allowed by constraint.");
		ObjectList olst = (ObjectList)value;
		return olst.serialize();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ListParameter)) return false;
		ListParameter other = (ListParameter)obj;
		if (!name.equals(other.name) || !desc.equals(other.desc)) return false;
		if (constraintType != other.constraintType) return false;
		if (constraintType == null) return true;
		switch (constraintType) {
		case CRITERIUM:
			return this.criterium.equals(other.criterium);
//		case ENUMERATION:
//			return this.allowedList.equals(other.allowedList);
		}
		throw new IllegalStateException("It seems more constraint types have been added.");
	}

	@Override
	public int hashCode() {
		int hash = name.hashCode() << 24 + desc.hashCode() << 16;
		
		if (criterium != null) hash += criterium.hashCode();
//		if (allowedList != null) hash += criterium.hashCode();
		
		return hash;
	}
	
}














