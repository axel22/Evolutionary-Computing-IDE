package hr.fer.zemris.ga_framework.model.impl;

import java.util.List;

import hr.fer.zemris.ga_framework.model.ISerializable;




public abstract class SimpleSerializable implements ISerializable {
	
	/* static fields */

	/* private fields */

	/* ctors */

	/* methods */
	
	public abstract ISerializable deserialize(String s);
	
	public abstract String serialize();

	public boolean hasAverage() {
		return false;
	}

	public boolean hasMax() {
		return false;
	}

	public boolean hasMedian() {
		return false;
	}

	public boolean hasMin() {
		return false;
	}

	public boolean hasStandardDeviation() {
		return false;
	}
	
	public boolean hasSum() {
		return false;
	}
	
	public boolean isComparable() {
		return false;
	}

	public int compareTo(Object o) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public Object average(List<? extends Object> objs) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public Object max(List<? extends Object> objs) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public Object median(List<? extends Object> objs) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public Object min(List<? extends Object> objs) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public Object stddev(List<? extends Object> objs) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public Object sum(List<? extends Object> objs) {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
}














