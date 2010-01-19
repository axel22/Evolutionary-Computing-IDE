package hr.fer.zemris.ga_framework.model;

import hr.fer.zemris.ga_framework.model.misc.Pair;
import hr.fer.zemris.ga_framework.model.misc.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Wrapper for information about the running algorithm.
 * 
 * @author Axel
 *
 */
public class RunningAlgorithmInfo {
	
	/* static fields */

	/* private fields */
	private String description;
	private boolean shouldBeListened;
	private List<Pair<IAlgorithm, IParameterInventory>> jobs;
	private volatile boolean terminate;
	private Time started, elapsed;
	private Map<String, Object> data;

	/* ctors */
	
	public RunningAlgorithmInfo(String desc, IAlgorithm algorithm, IParameterInventory it, boolean shouldListen) {
		description = desc;
		setJobs(new ArrayList<Pair<IAlgorithm,IParameterInventory>>());
		getJobs().add(new Pair<IAlgorithm, IParameterInventory>(algorithm, it));
		shouldBeListened = shouldListen;
		terminate = false;
		started = null;
		elapsed = null;
		data = new HashMap<String, Object>();
	}
	
	public RunningAlgorithmInfo(String desc, IAlgorithm algorithm, IParameterInventory it, boolean shouldListen,
			String key, Object value) {
		description = desc;
		setJobs(new ArrayList<Pair<IAlgorithm,IParameterInventory>>());
		getJobs().add(new Pair<IAlgorithm, IParameterInventory>(algorithm, it));
		shouldBeListened = shouldListen;
		terminate = false;
		started = null;
		elapsed = null;
		data = new HashMap<String, Object>();
		data.put(key, value);
	}
	
	public RunningAlgorithmInfo(String desc, List<Pair<IAlgorithm, IParameterInventory>> listOfJobs, boolean shouldListen,
			String key, Object value) {
		description = desc;
		jobs = listOfJobs;
		shouldBeListened = shouldListen;
		terminate = false;
		started = null;
		elapsed = null;
		data = new HashMap<String, Object>();
		data.put(key, value);
	}

	/* methods */
	
	/**
	 * Returns a short description of the algorithm run.
	 */
	public String getDescription() {
		return description;
	}
	
	public void setJobs(List<Pair<IAlgorithm, IParameterInventory>> jobs) {
		this.jobs = jobs;
	}
	
	public List<Pair<IAlgorithm, IParameterInventory>> getJobs() {
		return jobs;
	}

	public void setShouldBeListened(boolean shouldBeListened) {
		this.shouldBeListened = shouldBeListened;
	}

	public boolean shouldBeListened() {
		return shouldBeListened;
	}
	
	public void setTerminate(boolean term) {
		terminate = term;
	}
	
	public boolean getTerminate() {
		return terminate;
	}

	public void setStarted(Time started) {
		this.started = started;
	}

	public Time getStarted() {
		return started;
	}

	public void setElapsed(Time elapsed) {
		this.elapsed = elapsed;
	}

	public Time getElapsed() {
		return elapsed;
	}
	
	public Object getData(String key) {
		return data.get(key);
	}
	
	public Object setData(String key, Object o) {
		return data.put(key, o);
	}

}






















