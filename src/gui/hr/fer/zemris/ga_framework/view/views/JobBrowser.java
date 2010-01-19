package hr.fer.zemris.ga_framework.view.views;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.controller.impl.commands.CancelPendingJob;
import hr.fer.zemris.ga_framework.controller.impl.commands.CreateAlgorithmEditor;
import hr.fer.zemris.ga_framework.controller.impl.commands.CreateInfoViewer;
import hr.fer.zemris.ga_framework.controller.impl.commands.HaltJob;
import hr.fer.zemris.ga_framework.controller.impl.commands.RunPendingJob;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IParameterInventory;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.RunningAlgorithmInfo;
import hr.fer.zemris.ga_framework.model.misc.Pair;
import hr.fer.zemris.ga_framework.model.misc.Time;
import hr.fer.zemris.ga_framework.model.misc.Time.Metric;
import hr.fer.zemris.ga_framework.view.ImageLoader;
import hr.fer.zemris.ga_framework.view.Master;
import hr.fer.zemris.ga_framework.view.View;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class JobBrowser extends View {
	
	private static class CircularQueue {
		private long[] times;
		private int start, last;
		public CircularQueue(int length) {
			times = new long[length];
			start = 0;
			last = 0;
		}
		public void push(long e) {
			times[last] = e;
			last = (last + 1) % times.length;
			if (last == start) start = (start + 1) % times.length;
		}
		public long estimate(int runsLeft) {
			// find arithmetic mean
			// maybe implement quadratic interpolation later
			long t = 0;
			int elems = 0;
			for (int pos = start; pos != last; pos = (pos + 1) % times.length) {
				t += times[pos];
				elems++;
			}
			if (elems == 0) return Long.MAX_VALUE;
			t /= elems;
			
			// return mean multiplied with number of runs left
			return t * runsLeft;
		}
	}
	
	private static class ProgressInfo {
		public Map<String, IValue> currentParams;
		public int currentSet, totalSets, algIndex;
		public long timeAlgStarted;
		public CircularQueue runTimesQueue;
		public ProgressInfo(long timeStarted) {
			currentSet = -1;
			totalSets = -1;
			algIndex = -1;
			timeAlgStarted = timeStarted;
			runTimesQueue = new CircularQueue(20);
		}
	}
	
	/* static fields */
	private static final ImageData IMAGE_ICON = ImageLoader.loadImage("icons", "JobBrowser.png");
	private static final ImageData IMAGE_ALG_ED = ImageLoader.loadImage("icons", "application_side_list.png");
	private static final ImageData IMAGE_ALG_INFOS = ImageLoader.loadImage("icons", "book.png");
	private static final ImageData IMAGE_START = ImageLoader.loadImage("icons", "play_small.png");
	private static final ImageData IMAGE_HALT_JOB = ImageLoader.loadImage("icons", "cancel_small.png");
	private static final ImageData IMAGE_FILTER = ImageLoader.loadImage("icons", "filter.png");
	private static final ImageData IMAGE_PENDING = ImageLoader.loadImage("icons", "grayball.png");
	private static final ImageData IMAGE_RUNNING = ImageLoader.loadImage("icons", "greenball.png");
	private static final Events[] EVENT_TYPES = new Events[] {
		Events.ALGORITHM_STARTED, Events.ALGORITHM_FINISHED, Events.JOB_STARTED, Events.JOB_HALTED,
		Events.JOB_FINISHED, Events.JOB_ENQUEUED, Events.JOB_REMOVED_FROM_QUEUE
	};

	/* private fields */
	private Map<Long, ProgressInfo> infomap;
	private Table jobTable;
	private ProgressBar progressBar;
	private ToolItem openAlgInfosButt;
	private ToolItem startScheduledButt;
	private MenuItem showSingleRadio;
	private MenuItem showMultipleRadio;
	private MenuItem showAllRadio;
	private Label timeLeftSlotLab;
	private ToolItem viewOptionsButt;
	private Table paramTable;
	private ToolItem haltJobButt;
	private ToolItem openAlgEdButt;
	private Label nameSlotLabel;
	private Label runNumLabelSlot;
	private ToolBar toolBar;

	/* ctors */
	
	public JobBrowser(Composite parent, IController controller, long id) {
		super(parent, SWT.NONE, controller, id, EVENT_TYPES);
		
		infomap = new HashMap<Long, ProgressInfo>();
		
		initGUI();
		advinitGUI();
	}

	/* methods */
	
	private void advinitGUI() {
		// set images for toolbar
		openAlgEdButt.setImage(new Image(this.getDisplay(), IMAGE_ALG_ED));
		openAlgInfosButt.setImage(new Image(this.getDisplay(), IMAGE_ALG_INFOS));
		haltJobButt.setImage(new Image(this.getDisplay(), IMAGE_HALT_JOB));
		viewOptionsButt.setImage(new Image(this.getDisplay(), IMAGE_FILTER));
		startScheduledButt.setImage(new Image(this.getDisplay(), IMAGE_START));
		
		// configure toolbar
		toolBar.pack();
		
		// set enabled status of buttons
		setButtonStatus(false, false, false);
		
		// rebuild job table
		rebuildJobTable(false);
	}
	
	private void initGUI() {
		setLayout(new FillLayout());
		final SashForm sashForm = new SashForm(this, SWT.VERTICAL);

		final Composite composite = new Composite(sashForm, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.marginWidth = 2;
		gridLayout_1.marginHeight = 0;
		composite.setLayout(gridLayout_1);

		toolBar = new ToolBar(composite, SWT.WRAP | SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		viewOptionsButt = new ToolItem(toolBar, SWT.DROP_DOWN);
		viewOptionsButt.setSelection(true);
		viewOptionsButt.setToolTipText("Set filter");

		final Menu menu = new Menu(toolBar);
		addDropDown(viewOptionsButt, menu);

		showAllRadio = new MenuItem(menu, SWT.RADIO);
		showAllRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				rebuildJobTable(true);
				setButtonStatus(false, false, false);
				setAlgorithmInfo(null, null, false);
			}
		});
		showAllRadio.setSelection(true);
		showAllRadio.setText("Show all");

		showMultipleRadio = new MenuItem(menu, SWT.RADIO);
		showMultipleRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				rebuildJobTable(true);
				setButtonStatus(false, false, false);
				setAlgorithmInfo(null, null, false);
			}
		});
		showMultipleRadio.setText("Show multiple runs");

		showSingleRadio = new MenuItem(menu, SWT.RADIO);
		showSingleRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				rebuildJobTable(true);
				setButtonStatus(false, false, false);
				setAlgorithmInfo(null, null, false);
			}
		});
		showSingleRadio.setText("Show single runs");

		final ViewForm viewForm_1 = new ViewForm(composite, SWT.FLAT | SWT.BORDER);
		viewForm_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		jobTable = new Table(viewForm_1, SWT.FULL_SELECTION);
		jobTable.addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent e) {
				int wdt = jobTable.getSize().x - jobTable.getBorderWidth() * 2;
				jobTable.getColumn(0).setWidth(wdt);
			}
		});
		jobTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int pos = jobTable.getSelectionIndex();
				if (pos == -1) {
					setButtonStatus(false, false, false);
					setAlgorithmInfo(null, null, false);
					return;
				}
				
				// retrieve some info
				TableItem item = jobTable.getItem(pos);
				Long id = (Long)item.getData("id");
				boolean isRunning = (Boolean)item.getData("running");
				RunningAlgorithmInfo info = (RunningAlgorithmInfo)item.getData("info");
				
				// set ui appropriately
				setButtonStatus(true, info.shouldBeListened(), !isRunning);
				setAlgorithmInfo(id, info, isRunning);
			}
		});
		viewForm_1.setContent(jobTable);
		

		final TableColumn newColumnTableColumn_2 = new TableColumn(jobTable, SWT.NONE);
		newColumnTableColumn_2.setWidth(100);
		newColumnTableColumn_2.setText("Job name");

		final Composite composite_1 = new Composite(sashForm, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 3;
		gridLayout.verticalSpacing = 3;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 0;
		composite_1.setLayout(gridLayout);

		final ToolBar toolBar_1 = new ToolBar(composite_1, SWT.FLAT);
		toolBar_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		openAlgEdButt = new ToolItem(toolBar_1, SWT.PUSH);
		openAlgEdButt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				int sel = jobTable.getSelectionIndex();
				if (sel == -1) return;
				
				TableItem item = jobTable.getItem(sel);
				RunningAlgorithmInfo info = (RunningAlgorithmInfo) item.getData("info");
				Long id = (Long) item.getData("id");
				Class<?> algcls = info.getJobs().get(0).first.getClass();
				
				ICommand command = new CreateAlgorithmEditor(algcls, id);
				ctrl.issueCommand(command);
			}
		});
		openAlgEdButt.setToolTipText("Open algorithm editor");

		openAlgInfosButt = new ToolItem(toolBar_1, SWT.PUSH);
		openAlgInfosButt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				int sel = jobTable.getSelectionIndex();
				if (sel == -1) return;
				
				TableItem item = jobTable.getItem(sel);
				RunningAlgorithmInfo info = (RunningAlgorithmInfo) item.getData("info");
				
				for (Pair<IAlgorithm, IParameterInventory> job : info.getJobs()) {
					ctrl.issueCommand(new CreateInfoViewer(job.first.getClass()));
				}
			}
		});
		openAlgInfosButt.setToolTipText("Open algorithm infos");

		new ToolItem(toolBar_1, SWT.SEPARATOR);

		startScheduledButt = new ToolItem(toolBar_1, SWT.PUSH);
		startScheduledButt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				int sel = jobTable.getSelectionIndex();
				if (sel == -1) return;
				
				TableItem item = jobTable.getItem(sel);
				Boolean running = (Boolean) item.getData("running");
				if (running) return;
				Long id = (Long) item.getData("id");
				int index = (int)(Integer) item.getData("index");
				
				RunPendingJob run = new RunPendingJob(id, index, Master.getMaster().generateId());
				ctrl.issueCommand(run);
			}
		});
		startScheduledButt.setToolTipText("Start job ahead of schedule");

		haltJobButt = new ToolItem(toolBar_1, SWT.NONE);
		haltJobButt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				int sel = jobTable.getSelectionIndex();
				if (sel == -1) return;
				
				TableItem item = jobTable.getItem(sel);
				Boolean running = (Boolean) item.getData("running");
				Long id = (Long) item.getData("id");
				
				if (running) {
					HaltJob halt = new HaltJob(id);
					ctrl.issueCommand(halt);
				} else {
					int index = (int)(Integer) item.getData("index");
					CancelPendingJob cancel = new CancelPendingJob(id, index);
					ctrl.issueCommand(cancel);
				}
			}
		});
		haltJobButt.setToolTipText("Cancel selected job");

		final Composite infoComposite = new Composite(composite_1, SWT.NONE);
		infoComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		gridLayout_2.verticalSpacing = 1;
		gridLayout_2.marginWidth = 1;
		gridLayout_2.marginHeight = 0;
		infoComposite.setLayout(gridLayout_2);

		final Label nameLabel = new Label(infoComposite, SWT.NONE);
		nameLabel.setText("Name:");

		nameSlotLabel = new Label(infoComposite, SWT.NONE);
		final GridData gd_nameSlotLabel = new GridData(SWT.FILL, SWT.CENTER, true, false);
		nameSlotLabel.setLayoutData(gd_nameSlotLabel);

		final Label runLabel = new Label(infoComposite, SWT.NONE);
		runLabel.setText("Run:");

		runNumLabelSlot = new Label(infoComposite, SWT.NONE);
		runNumLabelSlot.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label etaLabel = new Label(infoComposite, SWT.NONE);
		etaLabel.setText("Time left:");

		timeLeftSlotLab = new Label(infoComposite, SWT.NONE);
		timeLeftSlotLab.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label progressLabel = new Label(infoComposite, SWT.NONE);
		progressLabel.setLayoutData(new GridData());
		progressLabel.setText("Progress:");

		progressBar = new ProgressBar(infoComposite, SWT.NONE);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final ViewForm viewForm = new ViewForm(composite_1, SWT.FLAT | SWT.BORDER);
		viewForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		paramTable = new Table(viewForm, SWT.HIDE_SELECTION | SWT.FULL_SELECTION);
		paramTable.addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent e) {
				int wdt = paramTable.getSize().x - paramTable.getBorderWidth() * 2;
				paramTable.getColumn(0).setWidth((int) (wdt * 0.5));
				paramTable.getColumn(1).setWidth((int) (wdt * 0.5));
			}
		});
		paramTable.setLinesVisible(true);
		viewForm.setContent(paramTable);

		final TableColumn newColumnTableColumn = new TableColumn(paramTable, SWT.NONE);
		newColumnTableColumn.setWidth(100);
		newColumnTableColumn.setText("New column");

		final TableColumn newColumnTableColumn_1 = new TableColumn(paramTable, SWT.NONE);
		newColumnTableColumn_1.setWidth(100);
		newColumnTableColumn_1.setText("New column");
		sashForm.setWeights(new int[] {226, 198 });
	}

	@Override
	public Control createTopControl(CTabFolder parent) {
		return null;
	}

	@Override
	public Image getImage(Display d) {
		return new Image(d, IMAGE_ICON);
	}

	public String getViewName() {
		return "Job Browser";
	}
	
	public void onEvent(Events evtype, CommandResult messages) {
		switch (evtype) {
		case ALGORITHM_STARTED:
			onAlgorithmStarted(messages);
			break;
		case ALGORITHM_FINISHED:
			onAlgorithmFinished(messages);
			break;
		case JOB_STARTED:
			onJobStarted(messages);
			break;
		case JOB_HALTED:
			onJobHalted(messages);
			break;
		case JOB_FINISHED:
			onJobFinished(messages);
			break;
		case JOB_ENQUEUED:
			onJobEnqueued(messages);
			break;
		case JOB_REMOVED_FROM_QUEUE:
			onJobRemovedFromQueue(messages);
			break;
		}
	}
	
	private void onJobRemovedFromQueue(CommandResult messages) {
		this.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed()) return;
				
				rebuildJobTable(true);
			}
		});
	}

	private void onJobEnqueued(CommandResult messages) {
		this.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed()) return;
				
				rebuildJobTable(true);
			}
		});
	}

	private void onJobFinished(CommandResult messages) {
		final Long id = (Long) messages.msg(Events.KEY.JOB_ID);
		this.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed()) return;
				
				infomap.remove(id);
				
				rebuildJobTable(true);
			}
		});
	}

	private void onJobHalted(CommandResult messages) {
		final Long id = (Long) messages.msg(Events.KEY.JOB_ID);
		this.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed()) return;
				
				infomap.remove(id);
				
				rebuildJobTable(true);
			}
		});
	}

	private void onJobStarted(CommandResult messages) {
		final Long id = (Long) messages.msg(Events.KEY.JOB_ID);
		this.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed()) return;
				
				if (!infomap.containsKey(id)) infomap.put(id, new ProgressInfo(0));
				
				rebuildJobTable(true);
			}
		});
	}

	private void onAlgorithmFinished(CommandResult messages) {
		final Long id = (Long) messages.msg(Events.KEY.JOB_ID);
		this.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed()) return;
				
				// set running time for algorithm
				ProgressInfo nfo = infomap.get(id);
				long time = System.currentTimeMillis() - nfo.timeAlgStarted;
				nfo.runTimesQueue.push(time);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void onAlgorithmStarted(CommandResult messages) {
		final Long id = (Long) messages.msg(Events.KEY.JOB_ID);
		final Map<String, IValue> inputvals = (Map<String, IValue>) messages.msg(Events.KEY.INPUT_VALS);
		final int currSet = (Integer) messages.msg(Events.KEY.ITERATION_INDEX);
		final int totalSets = (Integer) messages.msg(Events.KEY.TOTAL_ITERATIONS);
		final int algIndex = (Integer) messages.msg(Events.KEY.ALGORITHM_INDEX);
		this.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed()) return;
				
				// set info for algorithm
				ProgressInfo info = infomap.get(id);
				info.timeAlgStarted = System.currentTimeMillis();
				info.currentSet = currSet;
				info.totalSets = totalSets;
				info.algIndex = algIndex;
				info.currentParams = inputvals;
				
				// refresh info if selected
				refreshInfo(id);
			}
		});
	}

	private void refreshInfo(Long id) {
		int sel = jobTable.getSelectionIndex();
		if (sel == -1) setAlgorithmInfo(null, null, false);
		else {
			TableItem item = jobTable.getItem(sel);
			Long selid = (Long) item.getData("id");
			Boolean isRunning = (Boolean) item.getData("running");
			if (id == null || !id.equals(selid)) return;
			RunningAlgorithmInfo info = (RunningAlgorithmInfo) item.getData("info");
			setAlgorithmInfo(id, info, isRunning);
		}
	}
	
	private static void addDropDown(final ToolItem item, final Menu menu) {
		item.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.ARROW) {
					Rectangle rect = item.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = item.getParent().toDisplay(pt);
					menu.setLocation(pt.x, pt.y);
					menu.setVisible(true);
				}
			}
		});
	}
	
	private void rebuildJobTable(boolean reselect) {
		Long selid = null;
		int selindex = -1;
		int sel = jobTable.getSelectionIndex();
		if (sel != -1) {
			TableItem sitem = jobTable.getItem(sel);
			selid = (Long) sitem.getData("id");
			if (!(Boolean)sitem.getData("running")) selindex = (Integer) sitem.getData("index");
		}
		
		jobTable.removeAll();
		
		Map<Long, RunningAlgorithmInfo> activejobs = ctrl.getModel().getActiveJobs();
		long millis = System.currentTimeMillis();
		for (Entry<Long, RunningAlgorithmInfo> ntr : activejobs.entrySet()) {
			Long id = ntr.getKey();
			addItemToJobTable(id, ntr.getValue(), false, 0);
			if (!infomap.containsKey(id)) infomap.put(id, new ProgressInfo(millis));
		}
		
		Map<Long, LinkedList<RunningAlgorithmInfo>> pendingjobs = ctrl.getModel().getPendingJobs();
		for (Entry<Long, LinkedList<RunningAlgorithmInfo>> ntr : pendingjobs.entrySet()) {
			int index = -1;
			for (RunningAlgorithmInfo info : ntr.getValue()) {
				index++;
				addItemToJobTable(ntr.getKey(), info, true, index);
			}
		}
		
		// reselect if needed
		if (reselect && selid != null) {
			// search for the right item
			int pos = -1;
			boolean didSelect = false;
			for (TableItem ti : jobTable.getItems()) {
				pos++;
				if (selindex != -1) {
					// job is not running
					if (selid.equals(ti.getData("id"))) {
						if (!(Boolean) ti.getData("running")) {
							if (selindex == (int)(Integer)ti.getData("index")) {
								jobTable.setSelection(pos);
								didSelect = true;
								break;
							}
						}
					}
				} else {
					// job is running
					if (selid.equals(ti.getData("id"))) {
						if ((Boolean) ti.getData("running")) {
							jobTable.setSelection(pos);
							didSelect = true;
							break;
						}
					}
				}
			}
			if (!didSelect) {
				setAlgorithmInfo(null, null, false);
				setButtonStatus(false, false, false);
			}
		} else {
			setAlgorithmInfo(null, null, false);
			setButtonStatus(false, false, false);
		}
	}
	
	private void setAlgorithmInfo(Long id, RunningAlgorithmInfo info, boolean isrunning) {
		if (info == null || id == null) {
			nameSlotLabel.setText("");
			runNumLabelSlot.setText("");
			timeLeftSlotLab.setText("");
			progressBar.setSelection(0);
			paramTable.removeAll();
		} else if (isrunning) {
			String name = ctrl.getModel().getDispatcher().getCurrentlyRunning(id).getName();
			ProgressInfo pi = infomap.get(id);
			
			nameSlotLabel.setText(name);
			runNumLabelSlot.setText(createRunNumberText(pi, info));
			timeLeftSlotLab.setText(calculateTimeLeft(pi, info));
			progressBar.setSelection(calculateProgressBarSelection(pi, info));
			rebuildParameterTable(pi);
		} else {
			nameSlotLabel.setText(info.getJobs().get(0).first.getName() + " (first algorithm)");
			runNumLabelSlot.setText("-/-");
			timeLeftSlotLab.setText("(unknown)");
			progressBar.setSelection(0);
			paramTable.removeAll();
		}
	}
	
	private void rebuildParameterTable(ProgressInfo pi) {
		paramTable.removeAll();
		
		if (pi.currentParams != null) {
			for (Entry<String, IValue> ntr : pi.currentParams.entrySet()) {
				TableItem item = new TableItem(paramTable, SWT.NONE);
				item.setText(0, ntr.getKey());
				item.setText(1, ntr.getValue().value().toString());
			}
		}
	}

	private String calculateTimeLeft(ProgressInfo pi, RunningAlgorithmInfo info) {
		if (info.getJobs().size() > 1) return "Cannot estimate - multiple algorithms.";
		else if (pi.currentSet == -1 || pi.totalSets == -1 || pi.algIndex == -1) return "(unknown)";
		else {
			long t = pi.runTimesQueue.estimate(pi.totalSets - (pi.currentSet + 1));
			if (t == Long.MAX_VALUE) return "(unknown)";
			return new Time(t, Metric.ms).toNiceString(Metric.h, Metric.s);
		}
	}

	private int calculateProgressBarSelection(ProgressInfo pi, RunningAlgorithmInfo info) {
		if (pi.currentSet == -1 || pi.totalSets == -1 || pi.algIndex == -1) return 0;
		
		int total = 0;
		int done = pi.currentSet + 1;
		int currentAlg = -1;
		for (Pair<IAlgorithm, IParameterInventory> pair : info.getJobs()) {
			int increase = pair.getSecond().size() * pair.getSecond().getRunsPerSet();
			if (++currentAlg < pi.algIndex) done += increase;
			total += increase;
		}
		
		return (int) (((double)done) / total * 100);
	}

	private String createRunNumberText(ProgressInfo pi, RunningAlgorithmInfo info) {
		StringBuilder sb = new StringBuilder();
		
		if (pi.currentSet == -1 || pi.totalSets == -1 || pi.algIndex == -1) {
			if (info.getJobs().size() != 1) sb.append("-/- (algorithm -/-)");
			else {
				if (info.getJobs().get(0).second.size() == 1 &&
					info.getJobs().get(0).second.getRunsPerSet() == 1) {
					sb.append("1/1 (algorithm 1/1)");
				} else sb.append("-/- (algorithm 1/1)");
			}
		} else {
			sb.append(pi.currentSet + 1).append("/").append(pi.totalSets);
			sb.append(" (algorithm ").append(pi.algIndex + 1).append("/");
			sb.append(info.getJobs().size()).append(")");
		}
		
		return sb.toString();
	}

	private void addItemToJobTable(Long id, RunningAlgorithmInfo info, boolean isPending, int index) {
		// skip if filter is turned on
		boolean isSingle = (info.getJobs().size() == 1) && (info.getJobs().get(0).getSecond().size() == 1)
			&& (info.getJobs().get(0).getSecond().getRunsPerSet() == 1);
		if (isSingle) {
			if (showMultipleRadio.getSelection()) return;
		} else {
			if (showSingleRadio.getSelection()) return;
		}
		
		// add item to table
		TableItem item = new TableItem(jobTable, SWT.NONE);
		item.setData("running", !isPending);
		item.setData("id", id);
		item.setData("info", info);
		if (!isPending) {
			item.setImage(new Image(this.getDisplay(), IMAGE_RUNNING));
			item.setText("Job " + id + ": " + info.getDescription());
		} else {
			item.setData("index", index);
			item.setImage(new Image(this.getDisplay(), IMAGE_PENDING));
			item.setText("Pending job " + id + " (index " + index + "): " + info.getDescription());
		}
	}
	
//	private int getTotalRuns(RunningAlgorithmInfo info) {
//		int total = 0;
//		for (Pair<IAlgorithm, IParameterInventory> pair : info.getJobs()) {
//			total += pair.getSecond().size() * pair.getSecond().getRunsPerSet();
//		}
//		return total;
//	}
	
	private void setButtonStatus(boolean isAnythingSelected, boolean shouldBeListened, boolean isPending) {
		if (!isAnythingSelected) {
			openAlgEdButt.setEnabled(false);
			openAlgInfosButt.setEnabled(false);
			startScheduledButt.setEnabled(false);
			haltJobButt.setEnabled(false);
		} else {
			if (shouldBeListened) openAlgEdButt.setEnabled(true);
			else openAlgEdButt.setEnabled(false);
			
			openAlgInfosButt.setEnabled(true);
			
			haltJobButt.setEnabled(true);
			if (isPending) {
				startScheduledButt.setEnabled(true);
			} else {
				startScheduledButt.setEnabled(false);
			}
		}
	}
	
}














