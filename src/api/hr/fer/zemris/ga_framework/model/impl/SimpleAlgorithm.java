package hr.fer.zemris.ga_framework.model.impl;

import hr.fer.zemris.ga_framework.model.AlgorithmTerminatedException;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class SimpleAlgorithm implements IAlgorithm {
	
	/* protected */
	protected volatile boolean running, terminate;
	protected List<IParameter> parameters, returnvalues;
	protected Map<String, IValue> defaultvals;
	private Map<String, IParameter> parammap, retvalmap;
	
	/* ctors */
	
	public SimpleAlgorithm(List<IParameter> parameterList, List<IParameter> returnValues, Map<String, Object> defaultParameterValues) {
		parameters = parameterList;
		returnvalues = returnValues;
		defaultvals = new HashMap<String, IValue>();
		
		parammap = new HashMap<String, IParameter>();
		retvalmap = new HashMap<String, IParameter>();
		for (IParameter p : parameters) {
			parammap.put(p.getName(), p);
		}
		for (IParameter p : returnvalues) {
			retvalmap.put(p.getName(), p);
		}
		
		for (Entry<String, Object> ntr : defaultParameterValues.entrySet()) {
			IParameter p = parammap.get(ntr.getKey());
			defaultvals.put(ntr.getKey(), new Value(ntr.getValue(), p));
		}
	}
	
	/* methods */

	public Map<String, IValue> getDefaultValues() {
		Map<String, IValue> map = new HashMap<String, IValue>();
		
		for (Entry<String, IValue> ntr : defaultvals.entrySet()) {
			map.put(ntr.getKey(), new Value(ntr.getValue().value(), ntr.getValue().parameter()));
		}
		
		return map;
	}
	
	public IParameter getParameter(String name) {
		return parammap.get(name);
	}

	public List<IParameter> getParameters() {
		return parameters;
	}

	public IParameter getReturnValue(String name) {
		return retvalmap.get(name);
	}

	public List<IParameter> getReturnValues() {
		return returnvalues;
	}

	public Map<String, IValue> getDefaultRunProperties() {
		return null;
	}

	public List<IParameter> getRunProperties() {
		return null;
	}

	public void haltAlgorithm() {
		terminate = true;
	}

	public boolean isPausable() {
		return false;
	}

	public boolean isPaused() {
		return false;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isSaveable() {
		return false;
	}

	public void load(String s) {
		throw new UnsupportedOperationException("This algorithm is not saveable nor loadable.");
	}

	public IAlgorithm newInstance() {
		try {
			return this.getClass().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Could not instantiate algorithm.", e);
		}
	}

	public String save() {
		throw new UnsupportedOperationException("This algorithm is not saveable.");
	}

	public void setPaused(boolean on) {
		throw new UnsupportedOperationException("This algorithm is not pausable.");
	}

	public void setRunProperty(String key, Object value) {
	}

	public Map<String, IValue> runAlgorithm(Map<String, IValue> values, IInfoListener listener) {
		Map<String, IValue> retvals = null;
		
		try {
			retvals = new HashMap<String, IValue>();
			terminate = false;
			running = true;
		
			Map<String, Object> retobjs = new HashMap<String, Object>();
			runImplementation(values, listener, retobjs);
			for (Entry<String, Object> ntr : retobjs.entrySet()) {
				retvals.put(ntr.getKey(), new Value(ntr.getValue(), retvalmap.get(ntr.getKey())));
			}
		} finally {
			running = false;
			if (terminate) {
				throw new AlgorithmTerminatedException();
			}
		}
		
		return retvals;
	}
	
	protected abstract void runImplementation(Map<String, IValue> values, IInfoListener listener, Map<String, Object> retvals);

}














