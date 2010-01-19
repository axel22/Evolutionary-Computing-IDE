package hr.fer.zemris.ga_framework.model;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.model.impl.Value;
import hr.fer.zemris.ga_framework.model.impl.parameters.DifferentNameParameter;
import hr.fer.zemris.ga_framework.model.misc.Pair;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.AdditionalHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;





public class JobDispatcher {
	
	/* static fields */

	/* private fields */
	private Map<Long, Thread> threads;
	private Map<Long, RunningAlgorithmInfo> infomap;
	private LinkedHashMap<Long, LinkedList<RunningAlgorithmInfo>> pendingmap;
	private Map<Long, IAlgorithm> currentlyRunning;

	/* ctors */
	
	public JobDispatcher() {
		threads = new HashMap<Long, Thread>();
		infomap = new HashMap<Long, RunningAlgorithmInfo>();
		currentlyRunning = new HashMap<Long, IAlgorithm>();
		pendingmap = new LinkedHashMap<Long, LinkedList<RunningAlgorithmInfo>>();
	}
	

	/* methods */
	
	public boolean existsActiveJob(Long id) {
		return threads.containsKey(id);
	}
	
	public synchronized void clearAllJobs() {
		// first clear pending jobs
		pendingmap.clear();
		
		// now clear active jobs
		for (Entry<Long, Thread> ntr : threads.entrySet()) {
			haltAlgorithm(ntr.getKey());
		}
		
		threads.clear();
		infomap.clear();
	}
	
	private synchronized void haltAlgorithm(Long id) {
		Thread t = threads.get(id);
		RunningAlgorithmInfo info = infomap.get(id);
		
		if (t.isAlive()) {
			// set the terminate flag of the algorithms
			info.setTerminate(true);
			for (Pair<IAlgorithm, IParameterInventory> pairs : info.getJobs()) {
				pairs.first.haltAlgorithm();
			}
			
//			// wait until the thread dies
//			while (t.isAlive()) {
//				try {
//					t.join();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
		}
	}
	
	public synchronized void clearJob(Long id) {
		if (!threads.containsKey(id)) return;
		
		haltAlgorithm(id);
		
		threads.remove(id);
		infomap.remove(id);
	}
	
	/**
	 * Returns info if job exists.
	 * 
	 * @param id
	 * @return
	 * Null if job doesn't exist, info otherwise.
	 */
	public RunningAlgorithmInfo getJobInfo(Long id) {
		return infomap.get(id);
	}
	
	/**
	 * Returns whether the thread running the algorithm
	 * with the specified id has finished.
	 * 
	 * @param id
	 * @return
	 * @throws IllegalArgumentException
	 * If such a job doesn't exist.
	 */
	public boolean isJobDone(Long id) {
		Thread t = threads.get(id);
		
		if (t == null) throw new IllegalStateException("No such job: " + id);
		
		return !t.isAlive();
	}
	
//	private IParameter findByName(String name, List<IParameter> lst) {
//		for (IParameter p : lst) {
//			if (p.getName().equals(name)) return p;
//		}
//		return null;
//	}
	
	public synchronized IAlgorithm getCurrentlyRunning(Long id) {
		return currentlyRunning.get(id);
	}
	
	private synchronized void setCurrentlyRunning(Long id, IAlgorithm a) {
		currentlyRunning.put(id, a);
	}
	
	private void dispatchJobNow(final Long id, final RunningAlgorithmInfo jobinfo,
			final IInfoListenerProvider provider, final IAlgorithmObserver observer)
	{
		if (threads.containsKey(id)) throw new IllegalStateException("Id already taken.");
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				boolean halted = false;
				
				try {
					observer.onJobStarted(id);
					
					IInfoListener listener = (jobinfo.shouldBeListened()) ? 
							(provider.getInfoListener(id)) : (null);
					
					// iterate through jobs and parameter sets
					List<Pair<IAlgorithm, IParameterInventory>> lst = jobinfo.getJobs();
					int algIndex = -1;
					for (Pair<IAlgorithm, IParameterInventory> algopair : lst) {
						int runsPerSet = algopair.getSecond().getRunsPerSet();
						setCurrentlyRunning(id, algopair.first);
						algIndex++;
						
						// create handlers for this parameter inventory
						Map<String, IValue> returnvals = new HashMap<String, IValue>(), currentretvals = null;
						Map<String, Pair<IValueHandler, List<String>>> handlers = 
							new HashMap<String, Pair<IValueHandler, List<String>>>();
						Map<String, String> boundForLater = new HashMap<String, String>();
						for (ReturnHandler rh : algopair.second.getReturnHandlers()) {
							IParameter param = algopair.first.getReturnValue(rh.getParamname());
							if (rh.handler != null) {
								handlers.put(rh.getParamname(), new Pair<IValueHandler, List<String>>(
										ValueHandlerFactory.createHandler(rh.handler, param.getParamType()),
										new ArrayList<String>()));
							} else {
								// value is bound
								boundForLater.put(rh.getParamname(), rh.getBoundTo());
							}
						}
						// assign bound values to the ones they are bound to
						for (Entry<String, String> bval : boundForLater.entrySet()) {
							String boundto = bval.getValue();
							while (handlers.get(boundto) == null) {
								// handle multi-level bindings
								boundto = boundForLater.get(boundto);
							}
							handlers.get(boundto).second.add(bval.getKey());
						}
						// create additional handlers
						Map<AdditionalHandler, IValueHandler> additionalHandlers = new HashMap<AdditionalHandler, IValueHandler>();
						for (AdditionalHandler ah : algopair.second.getAdditionalHandlers()) {
							IParameter actualrv = algopair.first.getReturnValue(ah.getActualValueName());
							additionalHandlers.put(ah, ValueHandlerFactory.createHandler(ah.getHandlerType(), actualrv.getParamType()));
						}
						int setIndex = -1, invsize = algopair.second.size();
						for (Map<String, IValue> inputvals : algopair.second) {
							setIndex++;
							
							// reset handlers
							for (Pair<IValueHandler, List<String>> pair : handlers.values()) {
								pair.first.reset();
							}
							for (Entry<AdditionalHandler, IValueHandler> ntr : additionalHandlers.entrySet()) {
								ntr.getValue().reset();
							}
							
							// run algorithm
							for (int i = 0; i < runsPerSet; i++) {
								if (jobinfo.getTerminate()) {
									throw new AlgorithmTerminatedException();
								}
								
								// perform algorithm
								observer.onAlgorithmStarted(id, algopair.first, inputvals, setIndex * runsPerSet + i,
										invsize * runsPerSet, algIndex);
								currentretvals = algopair.first.runAlgorithm(inputvals, listener);
								observer.onAlgorithmFinished(id, algopair.first, currentretvals, setIndex * runsPerSet + i,
										invsize * runsPerSet, algIndex);
								
								// append values to handlers
								for (Entry<String, Pair<IValueHandler, List<String>>> ntr : handlers.entrySet()) {
									IValueHandler vhand = ntr.getValue().first;
									vhand.appendValue(currentretvals.get(ntr.getKey()).value());
									for (String s : ntr.getValue().second) {
										vhand.appendBoundValue(s, currentretvals.get(s).value());
									}
								}
								
								// append values to additional handlers
								for (Entry<AdditionalHandler, IValueHandler> ntr : additionalHandlers.entrySet()) {
									IValueHandler vhand = ntr.getValue();
									vhand.appendValue(currentretvals.get(ntr.getKey().getActualValueName()).value());
								}
							}
//							System.out.println("-------------------------------");
							
							// extract values from handlers and add them to return value map
							returnvals.clear();
							for (Entry<String, Pair<IValueHandler, List<String>>> ntr : handlers.entrySet()) {
								IValueHandler vhand = ntr.getValue().first;
								IValue val = new Value(vhand.getResultingValue(), algopair.first.getReturnValue(ntr.getKey()));
								returnvals.put(ntr.getKey(), val);
								for (String s : ntr.getValue().second) {
									val = new Value(vhand.getResultingBoundValue(s), algopair.first.getReturnValue(s));
//									val.setValue(vhand.getResultingBoundValue(s));
									returnvals.put(s, val);
								}
							}
							// extract values from additional handlers
							for (Entry<AdditionalHandler, IValueHandler> ntr : additionalHandlers.entrySet()) {
								IValueHandler vhand = ntr.getValue();
								IParameter origrv = algopair.first.getReturnValue(ntr.getKey().getActualValueName());
								IParameter nretval = new DifferentNameParameter(ntr.getKey().getHandlerName(), origrv);
								IValue val = new Value(vhand.getResultingValue(), nretval);
								returnvals.put(ntr.getKey().getHandlerName(), val);
							}
							algopair.second.appendTo(inputvals, new HashMap<String, IValue>(returnvals));
						}
					}
				} catch (AlgorithmTerminatedException e) {
					halted = true;
				} catch (Exception e) {
					e.printStackTrace();
					Application.logexcept("Algorithm exception occured.", e);
					observer.onError(id, e);
				} finally {
					removeIdFromThreads(id);
					
					if (halted) observer.onJobHalted(id);
					else observer.onJobFinished(id, jobinfo);
					
					checkIfNoJobIsActiveAndDispatch(provider, observer);
				}
			}
		});
		
		threads.put(id, t);
		infomap.put(id, jobinfo);
		t.start();
	}
	
	private synchronized void removeIdFromThreads(Long id) {
//		System.out.println("From runned: " + id);
		threads.remove(id);
	}
	
	private void addJobToPendingList(final Long id, final RunningAlgorithmInfo info) {
		LinkedList<RunningAlgorithmInfo> lst = pendingmap.get(id);
		if (lst == null) {
			lst = new LinkedList<RunningAlgorithmInfo>();
			pendingmap.put(id, lst);
		}
		
		lst.add(info);
	}
	
	@SuppressWarnings("unused")
	private RunningAlgorithmInfo popJobFromPendingList(final Long id) {
		LinkedList<RunningAlgorithmInfo> lst = pendingmap.get(id);
		if (lst == null) return null;
		
		RunningAlgorithmInfo info = lst.poll();
		if (lst.size() == 0) pendingmap.remove(id);
		
		return info;
	}
	
	private synchronized boolean checkIfNoJobIsActiveAndDispatch(IInfoListenerProvider provider,
			IAlgorithmObserver observer)
	{
		if (threads.isEmpty()) {
			startFirstJobFromPendingList(provider, observer);
			
			return true;
		}
		
		return false;
	}
	
	public synchronized boolean startFirstJobFromPendingList(IInfoListenerProvider provider,
			IAlgorithmObserver observer) {
		if (pendingmap.size() > 0) {
			Entry<Long, LinkedList<RunningAlgorithmInfo>> elem = pendingmap.entrySet().iterator().next();
			LinkedList<RunningAlgorithmInfo> lst = elem.getValue();
			Long id = elem.getKey();
			
			RunningAlgorithmInfo info = lst.poll();
			if (lst.size() == 0) pendingmap.remove(id);
			
			dispatchJobNow(id, info, provider, observer);
			
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized boolean startJobFromPendingList(Long id, int index, Long freeid,
			IInfoListenerProvider provider, IAlgorithmObserver observer)
	{
		LinkedList<RunningAlgorithmInfo> lst = pendingmap.get(id);
		if (lst == null) return false;
		if (index >= lst.size()) return false;
		
		RunningAlgorithmInfo nfo = lst.remove(index);
		if (lst.size() == 0) pendingmap.remove(id);
		
		if (threads.containsKey(id)) {
			dispatchJobNow(freeid, nfo, provider, observer);
		} else {
			dispatchJobNow(id, nfo, provider, observer);
		}
		
		return true;
	}
	
	public boolean dispatchJob(final Long id, final RunningAlgorithmInfo jobinfo,
			final IInfoListenerProvider provider, final IAlgorithmObserver observer,
			boolean isPending)
	{
		synchronized (this) {
			if (isPending) {
				addJobToPendingList(id, jobinfo);
				isPending = !checkIfNoJobIsActiveAndDispatch(provider, observer);
			} else {
				if (threads.containsKey(id)) return false;
				dispatchJobNow(id, jobinfo, provider, observer);
				
			}
		}
		
//		System.out.println("--------------------");
//		System.out.println(infomap.values());
//		for (List<? extends Object> lst : pendingmap.values()) {
//			System.out.println(lst);
//		}
		if (isPending) {
			new Thread(new Runnable() {
				public void run() {
					observer.onJobEnqueued(id);
				}
			}).start();
		}
		
		return true;
	}

	/**
	 * Returns a copy of thread map.
	 */
	public synchronized Map<Long, Thread> getThreadMap() {
		return new HashMap<Long, Thread>(threads);
	}
	
	public synchronized Map<Long, RunningAlgorithmInfo> getCurrentlyActiveJobs() {
		HashMap<Long, RunningAlgorithmInfo> jobmap = new HashMap<Long, RunningAlgorithmInfo>();
		
		for (Long id : threads.keySet()) {
			jobmap.put(id, infomap.get(id));
		}
		
		return jobmap;
	}
	
	public synchronized Map<Long, LinkedList<RunningAlgorithmInfo>> getCurrentlyPendingJobs() {
		LinkedHashMap<Long, LinkedList<RunningAlgorithmInfo>> map = 
			new LinkedHashMap<Long, LinkedList<RunningAlgorithmInfo>>();
		
		for (Entry<Long, LinkedList<RunningAlgorithmInfo>> ntr : pendingmap.entrySet()) {
			map.put(ntr.getKey(), new LinkedList<RunningAlgorithmInfo>(ntr.getValue()));
		}
		
		return map;
	}
	
	public boolean clearPendingJob(Long id, int index, IAlgorithmObserver obs) {
		synchronized (this) {
			List<RunningAlgorithmInfo> lst = pendingmap.get(id);
			
			if (lst == null) return false;
			if (index >= lst.size()) return false;
			
			lst.remove(index);
			
			if (lst.size() == 0) pendingmap.remove(id);
		}
		
		obs.onJobRemovedFromQueue(id, index);
		
		return true;
	}
	
}














