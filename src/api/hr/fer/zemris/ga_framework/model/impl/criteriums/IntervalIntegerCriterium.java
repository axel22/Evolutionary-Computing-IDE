package hr.fer.zemris.ga_framework.model.impl.criteriums;

import hr.fer.zemris.ga_framework.model.ICriterium;


/**
 * Criterium for integers within an interval, inclusively.
 * 
 * @author Axel
 *
 */
public class IntervalIntegerCriterium implements ICriterium {

	private int lval, hval;
	
	/**
	 * Ctor.
	 * 
	 * @param lowval
	 * @param highval
	 * @throws IllegalArgumentException
	 * If lowval is greater than highval.
	 */
	public IntervalIntegerCriterium(int lowval, int highval) {
		if (lowval > highval) throw new IllegalArgumentException("Low value of the interval "
				+ "higher than high value.");
		lval = lowval;
		hval = highval;
	}
	
	
	public String getDescription() {
		return "Value must be between " + lval + " and " + hval + " inclusively.";
	}

	public boolean isValid(Object o) {
		if (!(o instanceof Integer)) return false;
		Integer i = (Integer)o;
		return (i >= lval) && (i <= hval);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntervalIntegerCriterium)) return false;
		IntervalIntegerCriterium that = (IntervalIntegerCriterium)obj;
		return (that.lval == this.lval && that.hval == this.hval);
	}

	@Override
	public int hashCode() {
		return lval << 16 + hval;
	}

}














