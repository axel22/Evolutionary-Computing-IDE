/**
 * 
 */
package hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler;

import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.misc.Time;
import hr.fer.zemris.ga_framework.model.misc.Time.Metric;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.IRange;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.RangeData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class TimeStepRange implements IRange {
	private final Time from;

	private final Time step;

	private final List<Object> vals;

	private final Time to;

	private IParameter parameter;

	TimeStepRange(Time from, Time step, Time to, IParameter p) {
		this.from = from;
		this.step = step;
		this.to = to;
		this.parameter = p;
		
		vals = new ArrayList<Object>();
		
		Double dstep = step.convertTo(Metric.us).getInterval();
		for (Double it = from.convertTo(Metric.us).getInterval(); it <= to.convertTo(Metric.us).getInterval(); it += dstep) {
			Time t = new Time(it, Metric.us);
			if (!parameter.isValueValid(t)) {
				throw new IllegalArgumentException("Value " + it + " not allowed by constraint.");
			}
			vals.add(t.convertTo(from.getMetric()));
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













