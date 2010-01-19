package hr.fer.zemris.ga_framework.model.impl.criteriums;

import hr.fer.zemris.ga_framework.model.ICriterium;
import hr.fer.zemris.ga_framework.model.misc.Time;

/**
 * A criterium that only allows positive integers.
 * Zero is not allowed.
 * 
 * @author Axel
 *
 */
public class PositiveTimeCriterium implements ICriterium {

	public boolean isValid(Object o) {
		if (!(o instanceof Time)) return false;
		Time i = (Time)o;
		return i.getInterval() > 0;
	}

	public String getDescription() {
		return "Value must be positive.";
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof PositiveTimeCriterium);
	}

	@Override
	public int hashCode() {
		return 63295878;
	}
	
	

}














