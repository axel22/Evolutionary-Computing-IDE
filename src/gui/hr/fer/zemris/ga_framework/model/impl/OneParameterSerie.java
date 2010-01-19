package hr.fer.zemris.ga_framework.model.impl;

import hr.fer.zemris.ga_framework.model.IParameterInventory;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.ReturnHandler;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.AdditionalHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class OneParameterSerie implements IParameterInventory {
	
	/* static fields */

	/* private fields */
	private Map<String, IValue> inputvals;
	private Map<String, IValue> returnvals;
	private List<ReturnHandler> handlers;
	private int runsperset;

	/* ctors */
	
	public OneParameterSerie(Map<String, IValue> inputs, List<ReturnHandler> returnHandlers, int runsPerSet) {
		inputvals = inputs;
		returnvals = null;
		handlers = returnHandlers;
		runsperset = runsPerSet;
	}
	
	
	

	/* methods */
	
	public void appendTo(Map<String, IValue> inputValues, Map<String, IValue> returnValues) {
		if (!inputvals.equals(inputValues)) throw new IllegalArgumentException("Invalid input value map.");
		returnvals = returnValues;
	}

	public List<String> getChangingParamNames() {
		return new ArrayList<String>();
	}

	public Map<String, IValue> getReturnValues(Map<String, IValue> setOfInputParams) {
		if (!inputvals.equals(setOfInputParams)) throw new IllegalArgumentException("Invalid input value map.");
		return returnvals;
	}

	public Iterator<Map<String, IValue>> iterator() {
		return new Iterator<Map<String, IValue>>() {
			private boolean hasmore = true;
			public boolean hasNext() {
				return hasmore;
			}
			public Map<String, IValue> next() {
				hasmore = false;
				return inputvals;
			}
			public void remove() {
				throw new UnsupportedOperationException("Cannot remove from parameter inventory.");
			}
		};
	}
	
	public List<ReturnHandler> getReturnHandlers() {
		return handlers;
	}
	
	public List<AdditionalHandler> getAdditionalHandlers() {
		return new ArrayList<AdditionalHandler>();
	}

	public int size() {
		return 1;
	}

	public int getRunsPerSet() {
		return runsperset;
	}
}














