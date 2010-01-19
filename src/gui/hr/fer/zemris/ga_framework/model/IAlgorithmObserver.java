package hr.fer.zemris.ga_framework.model;

import java.util.Map;


/**
 * Interface to objects used for observing
 * the state of the algorithm.
 * Thread which runs the algorithm shall call
 * this objects to signify whether algorithm
 * is started, finished, etc.
 * 
 * @author Axel
 *
 */
public interface IAlgorithmObserver {

	public void onAlgorithmStarted(Long id, IAlgorithm alg, Map<String, IValue> inputValues, int setIndex, int totalSets, int algIndex);
	
	public void onAlgorithmFinished(Long id, IAlgorithm alg, Map<String, IValue> returnValues, int setIndex, int totalSets, int algIndex);
	
	public void onError(Long id, Exception e);
	
	public void onJobStarted(Long id);
	
	public void onJobFinished(Long id, RunningAlgorithmInfo nfo);
	
	public void onJobHalted(Long id);

	public void onJobEnqueued(Long id);
	
	public void onJobRemovedFromQueue(Long id, int index);
	
}














