package hr.fer.zemris.ga_framework.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;





/**
 * This class contains all data used by the
 * GUI application.
 * This is typically information about workspace
 * and open projects, files within projects,
 * map of models, list of available algorithms etc.
 * 
 * @author Axel
 * 
 */
public class Model {
	
	public static class KEY {
		public static final String GRAPH_LIST = "graph_list";
		public static final String GNUPLOT_LIST = "gnuplot_list";
	}
	
	/* static fields */
	
	/* private fields */
	private JobDispatcher jobdispatcher;
	private AlgoDir algotree;
	private Map<String, Class<?>> dynamicClassMap;
	private IInfoListenerProvider lprovider;
	private IAlgorithmObserver alobserver;
	
	/* ctors */

	public Model() {
		jobdispatcher = new JobDispatcher();
		algotree = new AlgoDir("Algorithms");
		dynamicClassMap = new HashMap<String, Class<?>>();
	}
	
	/* methods */
	
	public void clearClassMap() {
		dynamicClassMap.clear();
	}
	
	public Class<?> putClass(String name, Class<?> cls) {
		return dynamicClassMap.put(name, cls);
	}
	
	public Class<?> getClass(String name) {
		return dynamicClassMap.get(name);
	}
	
	public void setInfoListenerFromController(IInfoListenerProvider prov) {
		lprovider = prov;
	}
	
	public JobDispatcher getDispatcher() {
		return jobdispatcher;
	}
	
	public boolean dispatchJob(Long model_id, RunningAlgorithmInfo info, boolean isPending) {
		return jobdispatcher.dispatchJob(model_id, info, lprovider, alobserver, isPending);
	}
	
	public AlgoDir getAlgorithmTree() {
		return algotree;
	}
	
	public void setAlgorithmTree(AlgoDir algoroot) {
		algotree = algoroot;
	}
	
	public void resetAlgorithmTree() {
		algotree = new AlgoDir("Algorithms");
	}

	public void setAlgorithmObserver(IAlgorithmObserver alobserver) {
		this.alobserver = alobserver;
	}

	public IAlgorithmObserver getAlgorithmObserver() {
		return alobserver;
	}
	
	public Map<Long, RunningAlgorithmInfo> getActiveJobs() {
		return jobdispatcher.getCurrentlyActiveJobs();
	}
	
	public Map<Long, LinkedList<RunningAlgorithmInfo>> getPendingJobs() {
		return jobdispatcher.getCurrentlyPendingJobs();
	}
	
	public boolean startJobFromPendingList(Long id, int index, Long nid) {
		return jobdispatcher.startJobFromPendingList(id, index, nid, lprovider, alobserver);
	}
	
	public boolean cancelPendingJob(Long id, int index) {
		return jobdispatcher.clearPendingJob(id, index, alobserver);
	}
	
}














