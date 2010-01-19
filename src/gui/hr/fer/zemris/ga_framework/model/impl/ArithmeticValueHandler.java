package hr.fer.zemris.ga_framework.model.impl;

import hr.fer.zemris.ga_framework.model.HandlerTypes;
import hr.fer.zemris.ga_framework.model.IValueHandler;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.model.misc.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Can handle average, median, minimum, maximum.
 * 
 * @author Axel
 *
 */
public class ArithmeticValueHandler implements IValueHandler {

	/* static fields */
	
	/* private fields */
	private List<Object> values;
	private Map<String, List<Object>> boundvalues;
	private ParameterTypes paramtp;
	private HandlerTypes htp;
	private int position;
	
	/* ctors */
	
	public ArithmeticValueHandler(ParameterTypes paramtype, HandlerTypes handlertype) {
		paramtp = paramtype;
		htp = handlertype;
		values = new ArrayList<Object>();
		boundvalues = new HashMap<String, List<Object>>();
		position = -1;
	}
	
	/* methods */
	
	public void appendBoundValue(String name, Object o) {
		List<Object> bounded = boundvalues.get(name);
		if (bounded == null) {
			bounded = new ArrayList<Object>();
			boundvalues.put(name, bounded);
		}
		
		bounded.add(o);
	}

	public void appendValue(Object o) {
		values.add(o);
	}

	public Object getResultingBoundValue(String name) {
		return boundvalues.get(name).get(position);
	}

	public Object getResultingValue() {
		switch (htp) {
		case Minimal:
			position = paramtp.minimum(values);
			break;
		case Maximal:
			position = paramtp.maximum(values);
			break;
		case Median:
			position = paramtp.median(values);
			break;
		case Average:
			Pair<Object, Integer> pair = paramtp.average(values);
			position = pair.getSecond();
			return pair.getFirst();
		case StandardDeviation:
			pair = paramtp.stddev(values);
			position = pair.getSecond();
			return pair.getFirst();
		case Sum:
			pair = paramtp.sum(values);
			position = pair.getSecond();
			return pair.getFirst();
		}
		
		return values.get(position);
	}

	public void reset() {
		boundvalues.clear();
		values.clear();
		position = -1;
	}

}














