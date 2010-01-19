package hr.fer.zemris.ga_framework.model.impl.criteriums;

import hr.fer.zemris.ga_framework.model.ICriterium;


/**
 * Criterium for real numbers greater than, exclusively.
 * 
 * @author Axel
 *
 */
public class GreaterThanRealCriterium implements ICriterium {

	private double lval;
	
	/**
	 * Ctor.
	 * 
	 * @param lowval
	 * @param highval
	 * @throws IllegalArgumentException
	 * If lowval is greater than highval.
	 */
	public GreaterThanRealCriterium(double lowval) {
		lval = lowval;
	}
	
	
	public String getDescription() {
		return "Value must be greater than " + lval + ".";
	}

	public boolean isValid(Object o) {
		if (!(o instanceof Double)) return false;
		Double i = (Double)o;
		return (i > lval);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GreaterThanRealCriterium)) return false;
		GreaterThanRealCriterium that = (GreaterThanRealCriterium)obj;
		return (that.lval == this.lval);
	}

	@Override
	public int hashCode() {
		return (int) Double.doubleToLongBits(lval);
	}

}














