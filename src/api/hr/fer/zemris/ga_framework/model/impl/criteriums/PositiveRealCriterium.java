package hr.fer.zemris.ga_framework.model.impl.criteriums;

import hr.fer.zemris.ga_framework.model.ICriterium;

/**
 * A criterium that only allows positive integers.
 * Zero is not allowed.
 * 
 * @author Axel
 *
 */
public class PositiveRealCriterium implements ICriterium {

	public boolean isValid(Object o) {
		if (!(o instanceof Double)) return false;
		Double i = (Double)o;
		return i >= 0;
	}

	public String getDescription() {
		return "Value must be positive.";
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof PositiveRealCriterium);
	}

	@Override
	public int hashCode() {
		return 22394832;
	}
	
	

}














