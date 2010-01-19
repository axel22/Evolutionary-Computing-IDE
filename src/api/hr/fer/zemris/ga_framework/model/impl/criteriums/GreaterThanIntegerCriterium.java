package hr.fer.zemris.ga_framework.model.impl.criteriums;

import hr.fer.zemris.ga_framework.model.ICriterium;


/**
 * Criterium for integers greater than, exclusively.
 * 
 * @author Axel
 *
 */
public class GreaterThanIntegerCriterium implements ICriterium {

	private int lval;
	
	/**
	 * Ctor.
	 * 
	 * @param lowval
	 * @param highval
	 * @throws IllegalArgumentException
	 * If lowval is greater than highval.
	 */
	public GreaterThanIntegerCriterium(int lowval) {
		lval = lowval;
	}
	
	
	public String getDescription() {
		return "Value must be greater than " + lval + ".";
	}

	public boolean isValid(Object o) {
		if (!(o instanceof Integer)) return false;
		Integer i = (Integer)o;
		return (i > lval);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GreaterThanIntegerCriterium)) return false;
		GreaterThanIntegerCriterium that = (GreaterThanIntegerCriterium)obj;
		return (that.lval == this.lval);
	}

	@Override
	public int hashCode() {
		return lval;
	}

}














