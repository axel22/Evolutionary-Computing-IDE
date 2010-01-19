/**
 * 
 */
package hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler;

import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.IRange;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.RangeData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class RealStepRange implements IRange {
	private final List<Object> vals;

	private final Double from;

	private final Double to;

	private final Double step;
	
	private final IParameter parameter;

	public RealStepRange(Double from, Double to, Double step, IParameter p) {
		this.from = from;
		this.to = to;
		this.step = step;
		parameter = p;

		vals = new ArrayList<Object>();
		
		for (Double it = from; it <= to; it += step) {
			if (!parameter.isValueValid(it)) {
				throw new IllegalArgumentException("Value " + it + " not allowed by constraint.");
			}
			vals.add(it);
		}
	}

	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		int sz = vals.size();
		sb.append("Interval (").append(sz).append(" value");
		if (sz != 1) sb.append("s");
		sb.append("): From ").append(from).append(" to ").append(to).append(" with step ").append(step);
		
		return sb.toString();
	}

	public String getParamName() {
		return parameter.getName();
	}

	public Iterator<Object> iterator() {
		return vals.iterator();
	}

	public RangeData getRangeData() {
		RangeData data = new RangeData(parameter);
		data.from = from;
		data.to = to;
		data.step = step;
		return data;
	}
}













