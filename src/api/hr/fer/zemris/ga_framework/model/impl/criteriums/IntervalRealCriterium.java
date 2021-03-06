package hr.fer.zemris.ga_framework.model.impl.criteriums;

import hr.fer.zemris.ga_framework.model.ICriterium;


/**
 * Criterium for real numbers within an interval, inclusively.
 * 
 * @author Axel
 *
 */
public class IntervalRealCriterium implements ICriterium {

	private double lval, hval;
	
	/**
	 * Ctor.
	 * 
	 * @param lowval
	 * @param highval
	 * @throws IllegalArgumentException
	 * If lowval is greater than highval.
	 */
	public IntervalRealCriterium(double lowval, double highval) {
		if (lowval > highval) throw new IllegalArgumentException("Low value of the interval "
				+ "higher than high value.");
		lval = lowval;
		hval = highval;
	}
	
	
	public String getDescription() {
		return "Value must be between " + lval + " and " + hval + " inclusively.";
	}

	public boolean isValid(Object o) {
		if (!(o instanceof Double)) return false;
		Double i = (Double)o;
		return (i >= lval) && (i <= hval);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntervalRealCriterium)) return false;
		IntervalRealCriterium that = (IntervalRealCriterium)obj;
		return (that.lval == this.lval && that.hval == this.hval);
	}

	@Override
	public int hashCode() {
		return (int)((long)(lval) << 16 + (long)hval);
	}

}














