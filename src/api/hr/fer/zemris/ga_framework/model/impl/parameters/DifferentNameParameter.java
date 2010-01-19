package hr.fer.zemris.ga_framework.model.impl.parameters;

import java.util.List;

import hr.fer.zemris.ga_framework.model.ConstraintTypes;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.ParameterTypes;

public class DifferentNameParameter implements IParameter {

	private IParameter actualparam;
	private String name;
	
	public DifferentNameParameter(String newname, IParameter internal) {
		actualparam = internal;
		name = newname;
		
		if (internal instanceof DifferentNameParameter) {
			DifferentNameParameter other = (DifferentNameParameter) internal;
			this.actualparam = other.actualparam;
		}
	}
	
	public Object deserialize(String str) throws IllegalArgumentException {
		return actualparam.deserialize(str);
	}

	public List<Object> getAllowed() {
		return actualparam.getAllowed();
	}

	public ConstraintTypes getConstraint() {
		return actualparam.getConstraint();
	}

	public String getConstraintDescription() {
		return actualparam.getConstraintDescription();
	}

	public String getDescription() {
		return actualparam.getDescription();
	}

	public String getName() {
		return name;
	}

	public ParameterTypes getParamType() {
		return actualparam.getParamType();
	}

	public <T> Class<T> getValueClass() {
		return actualparam.getValueClass();
	}

	public String getValueClassName() {
		return actualparam.getValueClassName();
	}

	public boolean isValueValid(Object value) {
		return actualparam.isValueValid(value);
	}

	public String serialize(Object value) throws IllegalArgumentException {
		return actualparam.serialize(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DifferentNameParameter)) return false;
		DifferentNameParameter other = (DifferentNameParameter) obj;
		return (this.name.equals(other.name) && this.actualparam.equals(other.actualparam));
	}

	@Override
	public int hashCode() {
		return actualparam.hashCode() + name.hashCode();
	}

}














