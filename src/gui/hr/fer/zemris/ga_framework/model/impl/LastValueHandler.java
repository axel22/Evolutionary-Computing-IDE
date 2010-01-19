package hr.fer.zemris.ga_framework.model.impl;

import hr.fer.zemris.ga_framework.model.IValueHandler;

import java.util.HashMap;
import java.util.Map;

public class LastValueHandler implements IValueHandler {

	/* static fields */
	
	/* private fields */
	private Map<String, Object> boundvalues;
	private Object last;
	
	/* ctors */
	
	public LastValueHandler() {
		boundvalues = new HashMap<String, Object>();
	}
	
	/* methods */
	
	public void appendBoundValue(String name, Object o) {
		boundvalues.put(name, o);
	}

	public void appendValue(Object o) {
		last = o;
	}

	public Object getResultingBoundValue(String name) {
		return boundvalues.get(name);
	}

	public Object getResultingValue() {
		return last;
	}

	public void reset() {
		boundvalues.clear();
	}

}














