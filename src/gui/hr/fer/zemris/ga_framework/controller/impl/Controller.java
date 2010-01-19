package hr.fer.zemris.ga_framework.controller.impl;

import hr.fer.zemris.ga_framework.controller.ActionDispatcher;
import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.controller.IEditor;
import hr.fer.zemris.ga_framework.controller.IListener;
import hr.fer.zemris.ga_framework.controller.IUnsuccessfulListener;
import hr.fer.zemris.ga_framework.controller.IView;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IInfoListenerProvider;
import hr.fer.zemris.ga_framework.model.IPainter;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.model.RunningAlgorithmInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Controller implements IController, IInfoListenerProvider {
	
	/* static fields */
	public static final int COMMAND_HISTORY_LENGTH = 50;
	
	/* private fields */
	private Model model;
	private List<IEditor> editors;
	private Map<Long, IEditor> editormap;
	private List<IView> views;
	private List<IListener> listeners;
	private List<IUnsuccessfulListener> unsuccessfullisteners;
	private Map<Events, List<IListener>> eventmap;
	private ActionDispatcher reactdisp;
	
	/* ctors */
	
	public Controller() {
		editors = new ArrayList<IEditor>();
		editormap = new HashMap<Long, IEditor>();
		views = new ArrayList<IView>();
		listeners = new ArrayList<IListener>();
		unsuccessfullisteners = new ArrayList<IUnsuccessfulListener>();
		reactdisp = new ActionDispatcher();
		eventmap = new HashMap<Events, List<IListener>>();
		for (Events event : Events.values()) {
			eventmap.put(event, new ArrayList<IListener>());
		}
	}
	
	/* methods */
	
	public List<IEditor> getEditors() {
		return editors;
	}
	
	public List<IView> getViews() {
		return views;
	}

	public synchronized void registerEditor(Events[] evs, IEditor editor) {
		editors.add(editor);
		editormap.put(editor.getId(), editor);
		registerToEventMap(evs, editor);
	}
	
	public synchronized void unregisterEditor(IEditor editor) {
		editors.remove(editor);
		editormap.remove(editor.getId());
		unregisterFromEventMap(editor);
	}

	public synchronized void registerView(Events[] evs, IView view) {
		views.add(view);
		registerToEventMap(evs, view);
	}
	
	public synchronized void unregisterView(IView view) {
		views.remove(view);
		unregisterFromEventMap(view);
	}

	public Model getModel() {
		return model;
	}

	public synchronized void registerListener(Events[] evtypes, IListener listener) {
		listeners.add(listener);
		registerToEventMap(evtypes, listener);
	}
	
	public synchronized void unregisterListener(IListener listener) {
		listeners.remove(listener);
		unregisterFromEventMap(listener);
	}

	public synchronized void registerUnsuccessfulCommandListener(IUnsuccessfulListener listener) {
		unsuccessfullisteners.add(listener);
	}

	public synchronized void unregisterUnsuccessfulCommandListener(IUnsuccessfulListener listener) {
		unsuccessfullisteners.remove(listener);
	}

	private void registerToEventMap(Events[] evtypes, IListener listener) {
		for (Events event : evtypes) {
			eventmap.get(event).add(listener);
		}
	}
	
	private void unregisterFromEventMap(IListener listener) {
		for (Events event : Events.values()) {
			List<IListener> llist = eventmap.get(event);
			llist.remove(listener);
		}
	}

	public synchronized CommandResult issueCommand(ICommand command) {
		// perform command
		CommandResult result = command.doCommand(model);
		
		if (result.isSuccessful()) {
			for (Events ev : result.getEventTypes()) {
				informEventType(ev, result);
			}
		} else {
			// inform unsuccessful command listeners
			for (IUnsuccessfulListener l : unsuccessfullisteners) {
				l.onUnsuccessfulCommand(result);
			}
		}
		
		return result;
	}
	
	private void informEventType(Events ev, CommandResult res) {
		List<IListener> listenerlist = eventmap.get(ev);
		for (IListener l : listenerlist) {
			l.onEvent(ev, res);
		}
	}
	
	private synchronized List<IListener> getListenerListCopy(Events ev) {
		return new ArrayList<IListener>(eventmap.get(ev));
	}
	
	public synchronized void setModel(Model model) {
		this.model = model;
		
		model.setAlgorithmObserver(this);
		model.setInfoListenerFromController(this);
	}
	
	private synchronized IInfoListener privGetListener(Long id) {
		// search all editors and return appropriate info listener
		// if one exists, otherwise return null
		IEditor ed = editormap.get(id);
		if (ed == null) return null;
		
		IInfoListener listener = ed.getInfoListener();
		return listener;
	}
	
	public IInfoListener getInfoListener(final Long jobid) {
		return new IInfoListener() {
			public void clearProperties() {
				final IInfoListener l = privGetListener(jobid);
				reactdisp.dispatch(new Runnable() {
					public void run() {
						if (l != null) l.clearProperties();
					}
				});
			}
			public void paint(final IPainter painter) {
				final IInfoListener l = privGetListener(jobid);
				reactdisp.dispatch(new Runnable() {
					public void run() {
						if (l != null) l.paint(painter);
					}
				});
			}
			public void removeProperty(final String key) {
				final IInfoListener l = privGetListener(jobid);
				reactdisp.dispatch(new Runnable() {
					public void run() {
						if (l != null) l.removeProperty(key);
					}
				});
			}
			public void setPercentage(final double percent) {
				final IInfoListener l = privGetListener(jobid);
				reactdisp.dispatch(new Runnable() {
					public void run() {
						if (l != null) l.setPercentage(percent);
					}
				});
			}
			public void setProperty(final String key, final String value) {
				final IInfoListener l = privGetListener(jobid);
				reactdisp.dispatch(new Runnable() {
					public void run() {
						if (l != null) l.setProperty(key, value);
					}
				});
			}
			public void useCanvas(final boolean shouldUse) {
				final IInfoListener l = privGetListener(jobid);
				reactdisp.dispatch(new Runnable() {
					public void run() {
						if (l != null) l.useCanvas(shouldUse);
					}
				});
			}
			public void clearConsole() {
				final IInfoListener l = privGetListener(jobid);
				reactdisp.dispatch(new Runnable() {
					public void run() {
						if (l != null) l.clearConsole();
					}
				});
			}
			public void print(final String text) {
				final IInfoListener l = privGetListener(jobid);
				reactdisp.dispatch(new Runnable() {
					public void run() {
						if (l != null) l.print(text);
					}
				});
			}
			public void println(final String text) {
				final IInfoListener l = privGetListener(jobid);
				reactdisp.dispatch(new Runnable() {
					public void run() {
						if (l != null) l.println(text);
					}
				});
			}
			public void setConsoleColor(final int r, final int g, final int b) {
				final IInfoListener l = privGetListener(jobid);
				reactdisp.dispatch(new Runnable() {
					public void run() {
						if (l != null) l.setConsoleColor(r, g, b);
					}
				});
			}
		};
	}
	
	public void onAlgorithmFinished(final Long id, IAlgorithm alg,
			final Map<String, IValue> returnValues, final int currentSetIndex, final int totalSets,
			final int algIndex)
	{
		final List<IListener> listeners = getListenerListCopy(Events.ALGORITHM_FINISHED);
		reactdisp.dispatch(new Runnable() {
			public void run() {
				// inform listeners
				CommandResult result = new CommandResult(new Events[] {Events.ALGORITHM_FINISHED},
						Events.KEY.JOB_ID, id, Events.KEY.RETURN_VALS, returnValues);
				result.putMsg(Events.KEY.ITERATION_INDEX, currentSetIndex);
				result.putMsg(Events.KEY.TOTAL_ITERATIONS, totalSets);
				result.putMsg(Events.KEY.ALGORITHM_INDEX, algIndex);
				for (IListener l : listeners) {
					l.onEvent(Events.ALGORITHM_FINISHED, result);
				}
			}
		});
	}
	
	public void onAlgorithmStarted(final Long id, IAlgorithm alg,
			final Map<String, IValue> inputValues, final int currentSetIndex, final int totalSets,
			final int algIndex)
	{
		final List<IListener> listeners = getListenerListCopy(Events.ALGORITHM_STARTED);
		reactdisp.dispatch(new Runnable() {
			public void run() {
				// inform listeners
				CommandResult result = new CommandResult(new Events[] {Events.ALGORITHM_STARTED},
						Events.KEY.JOB_ID, id);
				result.putMsg(Events.KEY.ITERATION_INDEX, currentSetIndex);
				result.putMsg(Events.KEY.TOTAL_ITERATIONS, totalSets);
				result.putMsg(Events.KEY.ALGORITHM_INDEX, algIndex);
				result.putMsg(Events.KEY.INPUT_VALS, inputValues);
				for (IListener l : listeners) {
					l.onEvent(Events.ALGORITHM_STARTED, result);
				}
			}
		});
	}
	
	public void onJobStarted(final Long id) {
		final List<IListener> listeners = getListenerListCopy(Events.JOB_STARTED);
		reactdisp.dispatch(new Runnable() {
			public void run() {
				// inform listeners
				CommandResult result = new CommandResult(new Events[] {Events.JOB_STARTED},
						Events.KEY.JOB_ID, id);
				for (IListener l : listeners) {
					l.onEvent(Events.JOB_STARTED, result);
				}
			}
		});
	}
	
	public void onJobFinished(final Long id, final RunningAlgorithmInfo nfo) {
		final List<IListener> listeners = getListenerListCopy(Events.JOB_FINISHED);
		reactdisp.dispatch(new Runnable() {
			public void run() {
				// inform listeners
				CommandResult result = new CommandResult(new Events[] {Events.JOB_FINISHED},
						Events.KEY.JOB_ID, id, Events.KEY.JOB_INFO, nfo);
				for (IListener l : listeners) {
					l.onEvent(Events.JOB_FINISHED, result);
				}
			}
		});
	}

	public void onJobHalted(final Long id) {
		final List<IListener> listeners = getListenerListCopy(Events.JOB_HALTED);
		reactdisp.dispatch(new Runnable() {
			public void run() {
				// inform listeners
				CommandResult result = new CommandResult(new Events[] {Events.JOB_HALTED},
						Events.KEY.JOB_ID, id);
				for (IListener l : listeners) {
					l.onEvent(Events.JOB_HALTED, result);
				}
			}
		});
	}

	public void onError(final Long id, final Exception e) {
		final List<IListener> listeners = getListenerListCopy(Events.ALGORITHM_ERROR);
		reactdisp.dispatch(new Runnable() {
			public void run() {
				// inform listeners
				CommandResult result = new CommandResult(new Events[] {Events.ALGORITHM_ERROR},
						Events.KEY.JOB_ID, id, Events.KEY.EXCEPTION_OBJECT, e);
				for (IListener l : listeners) {
					l.onEvent(Events.ALGORITHM_ERROR, result);
				}
			}
		});
	}

	public void onJobEnqueued(final Long id) {
		final List<IListener> listeners = getListenerListCopy(Events.JOB_ENQUEUED);
		reactdisp.dispatch(new Runnable() {
			public void run() {
				// inform listeners
				CommandResult result = new CommandResult(new Events[] {Events.JOB_ENQUEUED},
						Events.KEY.JOB_ID, id);
				for (IListener l : listeners) {
					l.onEvent(Events.JOB_ENQUEUED, result);
				}
			}
		});
	}

	public void onJobRemovedFromQueue(final Long id, final int index) {
		final List<IListener> listeners = getListenerListCopy(Events.JOB_REMOVED_FROM_QUEUE);
		reactdisp.dispatch(new Runnable() {
			public void run() {
				// inform listeners
				CommandResult result = new CommandResult(new Events[] {Events.JOB_REMOVED_FROM_QUEUE},
						Events.KEY.JOB_ID, id, Events.KEY.ENQUEUED_INDEX, index);
				for (IListener l : listeners) {
					l.onEvent(Events.JOB_REMOVED_FROM_QUEUE, result);
				}
			}
		});
	}
	
}













