package hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.controller.impl.commands.AddPendingJob;
import hr.fer.zemris.ga_framework.controller.impl.commands.CreateInfoViewer;
import hr.fer.zemris.ga_framework.controller.impl.commands.HaltJob;
import hr.fer.zemris.ga_framework.controller.impl.commands.RunJob;
import hr.fer.zemris.ga_framework.model.HandlerTypes;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IGraph;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IParameterInventory;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.model.ReturnHandler;
import hr.fer.zemris.ga_framework.model.impl.Value;
import hr.fer.zemris.ga_framework.view.Editor;
import hr.fer.zemris.ga_framework.view.ImageLoader;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.AdditionalHandler;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.IRange;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.RangeData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import name.brijest.mvcapi.controller.Controller;
import name.brijest.mvcapi.controller.Message;
import name.brijest.mvcapi.controller.ObserverAdapter;
import name.brijest.mvcapi.controller.Result;
import name.brijest.mvcapi.controller.commands.Irreversible;
import name.brijest.mvcapi.controller.commands.Nonaltering;
import name.brijest.mvcapi.controller.commands.Undoable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class AlgorithmScheduler extends Editor {
	
	public static class SingleIterator<T> implements Iterator<T> {
		private T element;
		public SingleIterator(T elem) {
			element = elem;
		}
		public boolean hasNext() {
			return element != null;
		}
		public T next() {
			T curr = element;
			if (curr == null) throw new NoSuchElementException();
			element = null;
			return curr;
		}
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove.");
		}
	}
	
	private class MultipleParameterSerie implements IParameterInventory {
		/* private */
		private Map<Map<String, IValue>, Map<String, IValue>> registry;
		private List<Map<String, IValue>> maps;
		private List<String> changingNames;
		private int runsPerSet;
		
		/* ctor */
		public MultipleParameterSerie() {
			registry = new LinkedHashMap<Map<String,IValue>, Map<String,IValue>>();
			
			// extract all data from algorithm scheduler model
			maps = new ArrayList<Map<String,IValue>>();
			Map<String, IValue> fillmap = new HashMap<String, IValue>();
			recursiveFill(fillmap, 0, model.ranges.get().size());
			changingNames = new ArrayList<String>();
			for (IRange range : model.ranges.get()) {
				if (range.getRangeData().singleVal == null) {
					changingNames.add(range.getParamName());
				}
			}
			runsPerSet = runsSpinner.getSelection();
		}
		
		private void recursiveFill(Map<String, IValue> fillmap, int i, int size) {
			if (i == size) {
				maps.add(new HashMap<String, IValue>(fillmap));
			} else {
				IRange range = model.ranges.getElem(i);
				IParameter p = model.algorithm.get().getParameter(range.getParamName());
				for (Object o : range) {
					if (o instanceof ISerializable) o = ((ISerializable)o).deepCopy();
					fillmap.put(range.getParamName(), new Value(o, p));
					recursiveFill(fillmap, i + 1, size);
				}
			}
		}

		/* methods */
		public void appendTo(Map<String, IValue> inputValues, Map<String, IValue> returnValues) {
			registry.put(inputValues, returnValues);
		}
		public List<String> getChangingParamNames() {
			return changingNames;
		}
		public List<ReturnHandler> getReturnHandlers() {
			return new ArrayList<ReturnHandler>(model.returnhandlers.get());
		}
		public List<AdditionalHandler> getAdditionalHandlers() {
			return new ArrayList<AdditionalHandler>(model.addithandlers.get());
		}
		public Map<String, IValue> getReturnValues(Map<String, IValue> setOfInputParams) {
			return registry.get(setOfInputParams);
		}
		public Iterator<Map<String, IValue>> iterator() {
			return maps.iterator();
		}
		public int size() {
			return maps.size();
		}
		public int getRunsPerSet() {
			return runsPerSet;
		}
	}

	/* static fields */
	private static final ImageData ICON_EDITOR = ImageLoader.loadImage("icons", "table.png");
	private static final ImageData ICON_RUN = ImageLoader.loadImage("icons", "run_no_info.png");
	private static final ImageData ICON_HALT = ImageLoader.loadImage("icons", "cancel.png");
	private static final ImageData ICON_ADD_PENDING = ImageLoader.loadImage("icons", "clock.png");
	private static final ImageData ICON_INFO = ImageLoader.loadImage("icons", "book_big.png");
	private static final Map<String, String> EXTENSION_MAP = new LinkedHashMap<String, String>();
	private static final Events[] EVENT_TYPES = new Events[] {
		Events.JOB_STARTED,
		Events.JOB_FINISHED,
		Events.JOB_HALTED,
		Events.ALGORITHM_FINISHED
	};
	public static final String DEFAULT_EXTENSION = "sched";
	
	static {
		EXTENSION_MAP.put(DEFAULT_EXTENSION, "Save schedule");
	}
	
	/* local mvc */
	private Model model;
	private Controller<Model> localctrl;
	
	/* private fields */
	private Button removeButton_1;
	private Table additRetValsTable;
	private Button removeGnuplotButton;
	private Button editGnuplotButton;
	private Table gnuplotTable;
	private Composite composite_6;
	private ToolBar toolBar_1;
	private CoolItem newItemCoolItem_2;
	private CoolItem newItemCoolItem_1;
	private Label currentSetSlot;
	private ProgressBar progressBar;
	private CoolBar coolbar;
	private CoolItem newItemCoolItem;
	private ToolBar toolBar;
	private ToolItem runAlgButt;
	private ToolItem haltJobButt;
	private ToolItem addPendingJobButt;
	private ToolItem showInfoButt;
	private Spinner runsSpinner;
	private Button setRetValToDefaultButton;
	private Table retValsTable;
	private Button removeButton;
	private Button editButton;
	private Button moveUpButton;
	private Button moveDownButton;
	private Button editSelectedButton;
	private Button setToDefaultButton;
	private Table graphTable;
	private Table parameterTable;

	/* ctors */
	
	public AlgorithmScheduler(Composite c, IController controller, long elemid, CommandResult res) {
		super(c, SWT.NONE, controller, elemid, EVENT_TYPES);
		
		initLocalMVC(res);
		initGUI();
		advinitGUI();
	}
	
	
	/* methods */
	
	private void advinitGUI() {
		// set images
		runAlgButt.setImage(new Image(Display.getCurrent(), ICON_RUN));
		haltJobButt.setImage(new Image(Display.getCurrent(), ICON_HALT));
		addPendingJobButt.setImage(new Image(Display.getCurrent(), ICON_ADD_PENDING));
		showInfoButt.setImage(new Image(Display.getCurrent(), ICON_INFO));
		
		// configure toolbars
		toolBar.pack();
		Point sz = toolBar.getSize();
		newItemCoolItem.setControl(toolBar);
		newItemCoolItem.setSize(newItemCoolItem.computeSize(sz.x, sz.y));
		newItemCoolItem.setMinimumSize(sz);
		newItemCoolItem_1.setControl(toolBar_1);
		newItemCoolItem_1.setSize(newItemCoolItem_1.computeSize(sz.x, sz.y));
		newItemCoolItem_1.setMinimumSize(toolBar_1.getSize());
		newItemCoolItem_2.setControl(composite_6);
		newItemCoolItem_2.setSize(newItemCoolItem_2.computeSize(sz.x, sz.y));
		newItemCoolItem_2.setMinimumSize(composite_6.getSize());
		
		// configure toolbar actions
		runAlgButt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				IParameterInventory inventory = new MultipleParameterSerie();
				ICommand command = new RunJob(id, "Run of the " + model.algorithm.get().getName() +
						" with multiple parameter sets.", model.algorithm.get().newInstance(),
						false, inventory, new ArrayList<IGraph>(model.graphs.get()), new ArrayList<Object[]>(model.gnuplots.get()));
				CommandResult result = ctrl.issueCommand(command);
				
				if (result.isSuccessful()) {
					setToolbarButtons(true);
					currentSetSlot.setText("");
					progressBar.setSelection(0);
				}
			}
		});
		haltJobButt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ICommand command = new HaltJob(id);
				CommandResult result = ctrl.issueCommand(command);
				
				if (result.isSuccessful()) {
					setToolbarButtons(false);
				}
			}
		});
		addPendingJobButt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ICommand command = new AddPendingJob(id, "Run of the " + model.algorithm.get().getName() +
						" with multiple parameter sets.", model.algorithm.get().newInstance(),
						new MultipleParameterSerie(), new ArrayList<IGraph>(model.graphs.get()));
				ctrl.issueCommand(command);
			}
		});
		showInfoButt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ctrl.issueCommand(new CreateInfoViewer(model.algorithm.get().getClass()));
			}
		});
		
		// rebuild parameter table from model
		rebuildParameterTable(-1);
		
		// rebuild return value table from model
		rebuildRetValTable();
		
		// rebuild gnu plot table
		rebuildGnuPlotTable();
		
		// rebuild additional handlers table
		rebuildAdditionalHandlersTable();
		
		// add listeners to local controller
		localctrl.addObserver(new int[]{Const.EVENT.RANGE_TABLE_CHANGED}, new ObserverAdapter() {
			@Override
			public void afterCommand(Message event) {
				if (event.datamap != null) {
					Integer pos = (Integer) event.datamap.get(Const.KEY.MUST_SELECT);
					if (pos == null) pos = -1;
					rebuildParameterTable(pos);
				} else {
					rebuildParameterTable(-1);
				}
			}
		});
		localctrl.addObserver(new int[]{Const.EVENT.RETURN_TABLE_CHANGED}, new ObserverAdapter() {
			@Override
			public void afterCommand(Message event) {
				rebuildRetValTable();
			}
		});
		localctrl.addObserver(new int[]{Const.EVENT.GRAPH_TABLE_CHANGED}, new ObserverAdapter() {
			@Override
			public void afterCommand(Message event) {
				if (event.datamap != null) {
					Integer pos = (Integer)event.datamap.get(Const.KEY.GRAPH_MUST_SELECT);
					if (pos == null) pos = -1;
					rebuildGraphTable(pos);
				} else {
					rebuildGraphTable(-1);
				}
			}
		});
		localctrl.addObserver(new int[]{Const.EVENT.RUN_PER_SET_CHANGED}, new ObserverAdapter() {
			@Override
			public void afterCommand(Message event) {
				if (runsSpinner.getSelection() != model.runsPerSet.get()){
					runsSpinner.setSelection(model.runsPerSet.get());
				}
			}
		});
		localctrl.addObserver(new int[]{Const.EVENT.GNUPLOT_TABLE_CHANGED}, new ObserverAdapter() {
			@Override
			public void afterCommand(Message event) {
				rebuildGnuPlotTable();
			}
		});
		localctrl.addObserver(new int[]{Const.EVENT.ADD_HANDLERS_CHANGED}, new ObserverAdapter() {
			@Override
			public void afterCommand(Message event) {
				rebuildAdditionalHandlersTable();
			}
		});
	}
	
	private void rebuildAdditionalHandlersTable() {
		additRetValsTable.removeAll();
		for (AdditionalHandler ah : model.addithandlers.get()) {
			TableItem item = new TableItem(additRetValsTable, SWT.NONE);
			item.setText(0, ah.getHandlerName());
			item.setText(1, ah.getActualValueName());
			item.setText(2, ah.getHandler());
		}
	}


	@SuppressWarnings("unchecked")
	private void rebuildGnuPlotTable() {
		gnuplotTable.removeAll();
		for (Object[] of : model.gnuplots.get()) {
			TableItem item = new TableItem(gnuplotTable, SWT.NONE);
			item.setText(0, (String) of[0]);
			item.setText(1, "Columns: " + extractColumnsString((List<String[]>) of[1]));
			item.setData(of);
		}
	}


	private void initGUI() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		coolbar = new CoolBar(this, SWT.NONE);
		coolbar.setLocked(true);
		coolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		newItemCoolItem = new CoolItem(coolbar, SWT.PUSH);
		newItemCoolItem.setText("New item");

		toolBar = new ToolBar(coolbar, SWT.NONE);
		newItemCoolItem.setControl(toolBar);

		runAlgButt = new ToolItem(toolBar, SWT.PUSH);
		runAlgButt.setToolTipText("Run with selected parameter set");

		addPendingJobButt = new ToolItem(toolBar, SWT.PUSH);
		addPendingJobButt.setToolTipText("Add job to pending list");

		haltJobButt = new ToolItem(toolBar, SWT.PUSH);
		haltJobButt.setToolTipText("Halt job");
		haltJobButt.setEnabled(false);

		newItemCoolItem_1 = new CoolItem(coolbar, SWT.PUSH);
		newItemCoolItem_1.setText("New item");

		toolBar_1 = new ToolBar(coolbar, SWT.NONE);
		newItemCoolItem_1.setControl(toolBar_1);

		showInfoButt = new ToolItem(toolBar_1, SWT.PUSH);
		showInfoButt.setToolTipText("Show extensive algorithm info");

		newItemCoolItem_2 = new CoolItem(coolbar, SWT.PUSH);
		newItemCoolItem_2.setText("New item");

		composite_6 = new Composite(coolbar, SWT.NONE);
		final GridLayout gridLayout_11 = new GridLayout();
		gridLayout_11.marginWidth = 0;
		gridLayout_11.numColumns = 4;
		gridLayout_11.marginHeight = 0;
		composite_6.setLayout(gridLayout_11);
		newItemCoolItem_2.setControl(composite_6);

		final Label percentDoneLabel = new Label(composite_6, SWT.NONE);
		percentDoneLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
		percentDoneLabel.setText("Percent done:");

		progressBar = new ProgressBar(composite_6, SWT.NONE);
		final GridData gd_progressBar = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		gd_progressBar.widthHint = 111;
		progressBar.setLayoutData(gd_progressBar);

		final Label currentSetLabel = new Label(composite_6, SWT.NONE);
		currentSetLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
		currentSetLabel.setText("Sets complete:");

		currentSetSlot = new Label(composite_6, SWT.NONE);
		final GridData gd_currentSetSlot = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gd_currentSetSlot.widthHint = 60;
		currentSetSlot.setLayoutData(gd_currentSetSlot);

		final Composite composite = new Composite(this, SWT.NONE);
		final FillLayout fillLayout = new FillLayout();
		fillLayout.marginHeight = 2;
		composite.setLayout(fillLayout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		final SashForm sashForm = new SashForm(composite, SWT.VERTICAL);

		final CTabFolder tabFolder = new CTabFolder(sashForm, SWT.FLAT | SWT.BORDER);

		final CTabItem generalOptionsTabItem = new CTabItem(tabFolder, SWT.NONE);
		generalOptionsTabItem.setText("General");

		final CTabItem parameterRangesTabItem = new CTabItem(tabFolder, SWT.NONE);
		parameterRangesTabItem.setText("Parameter ranges");
		tabFolder.setSelection(parameterRangesTabItem);

		final Composite composite_4 = new Composite(tabFolder, SWT.NONE);
		final GridLayout gridLayout_8 = new GridLayout();
		gridLayout_8.verticalSpacing = 1;
		gridLayout_8.marginWidth = 2;
		gridLayout_8.marginHeight = 2;
		gridLayout_8.horizontalSpacing = 1;
		composite_4.setLayout(gridLayout_8);
		generalOptionsTabItem.setControl(composite_4);

		final Composite composite_5 = new Composite(composite_4, SWT.NONE);
		final GridLayout gridLayout_10 = new GridLayout();
		gridLayout_10.numColumns = 2;
		gridLayout_10.marginHeight = 0;
		gridLayout_10.marginWidth = 0;
		gridLayout_10.verticalSpacing = 2;
		gridLayout_10.horizontalSpacing = 2;
		composite_5.setLayout(gridLayout_10);

		final Label runsPerParameterLabel = new Label(composite_5, SWT.NONE);
		runsPerParameterLabel.setText("Runs per parameter set:");

		runsSpinner = new Spinner(composite_5, SWT.BORDER);
		runsSpinner.setSelection(30);
		runsSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				final int val = runsSpinner.getSelection();
				if (val == model.runsPerSet.get()) return;
				
				localctrl.issueCommand(new Undoable<Model>("Change runs per set", new Message(Const.EVENT.RUN_PER_SET_CHANGED)) {
					private int oldval;
					public Result doCommand(Model model) {
						oldval = model.runsPerSet.get();
						model.runsPerSet.set(val);
						
						return new Result(eventTypes());
					}
					public Result undoCommand(Model model)
							throws IllegalStateException,
							UnsupportedOperationException {
						model.runsPerSet.set(oldval);
						
						return new Result(eventTypes());
					}
				});
			}
		});
		runsSpinner.setMinimum(1);
		runsSpinner.setMaximum(10000);

		final Composite parameterRangesGroup = new Composite(tabFolder, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		gridLayout_1.verticalSpacing = 2;
		gridLayout_1.marginWidth = 2;
		gridLayout_1.marginHeight = 2;
		gridLayout_1.horizontalSpacing = 2;
		parameterRangesGroup.setLayout(gridLayout_1);
		parameterRangesTabItem.setControl(parameterRangesGroup);

		parameterTable = new Table(parameterRangesGroup, SWT.FULL_SELECTION | SWT.BORDER);
		parameterTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		parameterTable.setLinesVisible(true);
		parameterTable.setHeaderVisible(true);
		parameterTable.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				int wdt = parameterTable.getSize().x - parameterTable.getBorderWidth() * 2;
				parameterTable.getColumn(0).setWidth((int) (wdt * 0.25));
				parameterTable.getColumn(1).setWidth((int) (wdt * 0.75));
			}
		});
		parameterTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setEnableStateOfParameterButtons();
			}
		});
		parameterTable.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				editSelectedRange();
			}
		});


		final TableColumn newColumnTableColumn = new TableColumn(parameterTable, SWT.NONE);
		newColumnTableColumn.setWidth(124);
		newColumnTableColumn.setText("Parameter name");

		final TableColumn newColumnTableColumn_1 = new TableColumn(parameterTable, SWT.NONE);
		newColumnTableColumn_1.setWidth(348);
		newColumnTableColumn_1.setText("Range");

		final Composite composite_1 = new Composite(parameterRangesGroup, SWT.NONE);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.makeColumnsEqualWidth = true;
		gridLayout_2.verticalSpacing = 2;
		gridLayout_2.marginWidth = 2;
		gridLayout_2.marginHeight = 2;
		gridLayout_2.horizontalSpacing = 2;
		composite_1.setLayout(gridLayout_2);
		final GridData gd_composite_1 = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gd_composite_1.widthHint = 153;
		composite_1.setLayoutData(gd_composite_1);

		moveUpButton = new Button(composite_1, SWT.NONE);
		moveUpButton.setEnabled(false);
		moveUpButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		moveUpButton.setText("Move up");
		moveUpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final int pos = parameterTable.getSelectionIndex();
				if (pos == -1) return;
				
				localctrl.issueCommand(new Undoable<Model>("Move parameter up", 
						new Message(Const.EVENT.RANGE_TABLE_CHANGED)) {
					public Result doCommand(Model m) {
						IRange range = m.ranges.get().remove(pos);
						m.ranges.get().add(pos - 1, range);
						
						return new Result(eventTypes(), Const.KEY.MUST_SELECT, pos - 1);
					}
					public Result undoCommand(Model m)
							throws IllegalStateException,
							UnsupportedOperationException {
						IRange range = m.ranges.get().remove(pos - 1);
						m.ranges.get().add(pos, range);
						
						return new Result(eventTypes(), Const.KEY.MUST_SELECT, pos);
					}
				});
			}
		});

		moveDownButton = new Button(composite_1, SWT.NONE);
		moveDownButton.setEnabled(false);
		moveDownButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		moveDownButton.setText("Move down");
		moveDownButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				final int pos = parameterTable.getSelectionIndex();
				if (pos == -1) return;
				
				localctrl.issueCommand(new Undoable<Model>("Move parameter down", new Message(Const.EVENT.RANGE_TABLE_CHANGED)) {
							public Result doCommand(Model model) {
								IRange range = model.ranges.get().remove(pos);
								model.ranges.get().add(pos + 1, range);
								
								return new Result(eventTypes(), Const.KEY.MUST_SELECT, pos + 1);
							}
							public Result undoCommand(Model model) throws IllegalStateException, UnsupportedOperationException {
								IRange range = model.ranges.get().remove(pos + 1);
								model.ranges.get().add(pos, range);
								
								return new Result(eventTypes(), Const.KEY.MUST_SELECT, pos);
							}
				});
			}
		});

		editSelectedButton = new Button(composite_1, SWT.NONE);
		editSelectedButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				editSelectedRange();
			}
		});
		editSelectedButton.setEnabled(false);
		editSelectedButton.setLayoutData(new GridData(106, SWT.DEFAULT));
		editSelectedButton.setText("Edit selected...");

		setToDefaultButton = new Button(composite_1, SWT.NONE);
		setToDefaultButton.setEnabled(false);
		final GridData gd_setToDefaultButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
		setToDefaultButton.setLayoutData(gd_setToDefaultButton);
		setToDefaultButton.setText("Set to default");
		setToDefaultButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final int pos = parameterTable.getSelectionIndex();
				if (pos == -1) return;
				
				final Map<String, IValue> defaultvals = model.algorithm.get().getDefaultValues();
				final String paramname = model.ranges.getElem(pos).getParamName();
				localctrl.issueCommand(new Undoable<Model>("Set to default", new Message(Const.EVENT.RANGE_TABLE_CHANGED)) {
					private IRange oldrange = null;
					public Result doCommand(Model model) {
						oldrange = model.ranges.getElem(pos);
						model.ranges.get().set(pos, getSingleRange(paramname, defaultvals.get(paramname)));
						
						return new Result(eventTypes(), Const.KEY.MUST_SELECT, pos);
					}
					public Result undoCommand(Model model) throws IllegalStateException,
							UnsupportedOperationException
					{
						model.ranges.get().set(pos, oldrange);
						
						return new Result(eventTypes(), Const.KEY.MUST_SELECT, pos);
					}
				});
			}
		});

		final CTabFolder tabFolder_1 = new CTabFolder(sashForm, SWT.FLAT | SWT.BORDER);

		final CTabItem returnValuesTabItem = new CTabItem(tabFolder_1, SWT.NONE);
		returnValuesTabItem.setText("Return values");
		tabFolder_1.setSelection(returnValuesTabItem);

		final CTabItem additionalTabItem = new CTabItem(tabFolder_1, SWT.NONE);
		additionalTabItem.setText("Additional return values");

		final Composite addRetValsGroup = new Composite(tabFolder_1, SWT.NONE);
		final GridLayout gridLayout_12 = new GridLayout();
		gridLayout_12.numColumns = 2;
		gridLayout_12.verticalSpacing = 2;
		gridLayout_12.marginWidth = 2;
		gridLayout_12.marginHeight = 2;
		gridLayout_12.horizontalSpacing = 2;
		addRetValsGroup.setLayout(gridLayout_12);
		additionalTabItem.setControl(addRetValsGroup);

		additRetValsTable = new Table(addRetValsGroup, SWT.FULL_SELECTION | SWT.BORDER);
		additRetValsTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				int selpos = additRetValsTable.getSelectionIndex();
				if (selpos == -1) {
					removeButton_1.setEnabled(false);
				} else {
					removeButton_1.setEnabled(true);
				}
			}
		});
		additRetValsTable.setLinesVisible(true);
		additRetValsTable.setHeaderVisible(true);
		final GridData gd_additRetValsTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		additRetValsTable.setLayoutData(gd_additRetValsTable);

		final TableColumn newColumnTableColumn_8 = new TableColumn(additRetValsTable, SWT.NONE);
		newColumnTableColumn_8.setWidth(194);
		newColumnTableColumn_8.setText("Name");

		final TableColumn newColumnTableColumn_9 = new TableColumn(additRetValsTable, SWT.NONE);
		newColumnTableColumn_9.setWidth(214);
		newColumnTableColumn_9.setText("Original value name");

		final TableColumn newColumnTableColumn_10 = new TableColumn(additRetValsTable, SWT.NONE);
		newColumnTableColumn_10.setWidth(133);
		newColumnTableColumn_10.setText("Policy");

		final Composite composite_10 = new Composite(addRetValsGroup, SWT.NONE);
		final GridData gd_composite_10 = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gd_composite_10.widthHint = 153;
		composite_10.setLayoutData(gd_composite_10);
		final GridLayout gridLayout_13 = new GridLayout();
		gridLayout_13.verticalSpacing = 2;
		gridLayout_13.marginWidth = 2;
		gridLayout_13.marginHeight = 2;
		gridLayout_13.horizontalSpacing = 2;
		composite_10.setLayout(gridLayout_13);

		final Button addButton_2 = new Button(composite_10, SWT.NONE);
		addButton_2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				AdditionalHandlerSelector selector = new AdditionalHandlerSelector(AlgorithmScheduler.this.getShell());
				List<IParameter> retvals = model.algorithm.get().getReturnValues();
				Set<String> takennames = new HashSet<String>();
				for (IParameter p : model.algorithm.get().getParameters()) takennames.add(p.getName());
				for (IParameter p : retvals) takennames.add(p.getName());
				for (TableItem item : additRetValsTable.getItems()) takennames.add(item.getText(0));
				final AdditionalHandler ah = selector.open(retvals, takennames);
				if (ah != null) {
					localctrl.issueCommand(new Undoable<Model>("Add additional handler", new Message(Const.EVENT.ADD_HANDLERS_CHANGED)) {
						public Result doCommand(Model model) {
							model.addithandlers.add(ah);
							
							return new Result(eventTypes());
						}
						public Result undoCommand(Model model) throws IllegalStateException, UnsupportedOperationException {
							model.addithandlers.remove(model.addithandlers.get().size() - 1);

							return new Result(eventTypes());
						}
					});
				}
			}
		});
		final GridData gd_addButton_2 = new GridData(77, SWT.DEFAULT);
		addButton_2.setLayoutData(gd_addButton_2);
		addButton_2.setText("Add...");

		removeButton_1 = new Button(composite_10, SWT.NONE);
		removeButton_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				final int sel = additRetValsTable.getSelectionIndex();
				if (sel != -1) {
					localctrl.issueCommand(new Undoable<Model>("Remove additional handler", new Message(Const.EVENT.ADD_HANDLERS_CHANGED)) {
						public AdditionalHandler old = null;
						public Result doCommand(Model model) {
							old = model.addithandlers.getElem(sel);
							model.addithandlers.remove(sel);
							
							return new Result(eventTypes());
						}
						public Result undoCommand(Model model) throws IllegalStateException, UnsupportedOperationException {
							model.addithandlers.get().add(sel, old);
							
							return new Result(eventTypes());
						}
					});
				}
			}
		});
		removeButton_1.setEnabled(false);
		final GridData gd_removeButton_1 = new GridData(77, SWT.DEFAULT);
		removeButton_1.setLayoutData(gd_removeButton_1);
		removeButton_1.setText("Remove");

		final Composite returnValuesGroup = new Composite(tabFolder_1, SWT.NONE);
		final GridLayout gridLayout_5 = new GridLayout();
		gridLayout_5.verticalSpacing = 2;
		gridLayout_5.marginWidth = 2;
		gridLayout_5.marginHeight = 2;
		gridLayout_5.horizontalSpacing = 2;
		gridLayout_5.numColumns = 2;
		returnValuesGroup.setLayout(gridLayout_5);
		returnValuesTabItem.setControl(returnValuesGroup);

		retValsTable = new Table(returnValuesGroup, SWT.FULL_SELECTION | SWT.BORDER);
		retValsTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				int pos = retValsTable.getSelectionIndex();
				if (setRetValToDefaultButton == null) return;
				if (pos == -1) {
					setRetValToDefaultButton.setEnabled(false);
				} else {
					setRetValToDefaultButton.setEnabled(true);
				}
			}
		});
		retValsTable.setLinesVisible(true);
		retValsTable.setHeaderVisible(true);
		retValsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		retValsTable.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				int wdt = retValsTable.getSize().x - retValsTable.getBorderWidth() * 2;
				retValsTable.getColumn(0).setWidth((int) (wdt * 0.25));
				retValsTable.getColumn(1).setWidth((int) (wdt * 0.75));
			}
		});

		final TableColumn newColumnTableColumn_4 = new TableColumn(retValsTable, SWT.NONE);
		newColumnTableColumn_4.setWidth(149);
		newColumnTableColumn_4.setText("Value name");

		final TableColumn newColumnTableColumn_5 = new TableColumn(retValsTable, SWT.NONE);
		newColumnTableColumn_5.setWidth(119);
		newColumnTableColumn_5.setText("Policy");

		final Composite composite_3 = new Composite(returnValuesGroup, SWT.NONE);
		final GridData gd_composite_3 = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gd_composite_3.widthHint = 153;
		composite_3.setLayoutData(gd_composite_3);
		final GridLayout gridLayout_7 = new GridLayout();
		gridLayout_7.verticalSpacing = 2;
		gridLayout_7.marginWidth = 2;
		gridLayout_7.marginHeight = 2;
		gridLayout_7.horizontalSpacing = 2;
		composite_3.setLayout(gridLayout_7);

		setRetValToDefaultButton = new Button(composite_3, SWT.NONE);
		setRetValToDefaultButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				final int pos = retValsTable.getSelectionIndex();
				if (pos == -1) return;
				
				localctrl.issueCommand(
						new Undoable<Model>("Set return handler to default", 
								new Message(Const.EVENT.RETURN_TABLE_CHANGED)) {
							private HandlerTypes oldhandler;
							private String oldbinding;
							public Result doCommand(Model model) {
								ReturnHandler rh = model.returnhandlers.getElem(pos);
								oldhandler = rh.handler;
								oldbinding = rh.getBoundTo();
								rh.setBoundTo(null);
								if (getReturnValue(rh.getParamname()).getParamType().isSimpleArithmeticType()) {
									rh.handler = HandlerTypes.Average;
								} else {
									rh.handler = HandlerTypes.Last;
								}
								
//								System.out.println(model.returnhandlers.get());
								return new Result(eventTypes());
							}
							public Result undoCommand(Model model)
									throws IllegalStateException,
									UnsupportedOperationException {
								model.returnhandlers.getElem(pos).handler = oldhandler;
								model.returnhandlers.getElem(pos).setBoundTo(oldbinding);
								
								return new Result(eventTypes());
							}
				});
			}
		});
		setRetValToDefaultButton.setEnabled(false);
		setRetValToDefaultButton.setText("Set to default");
		returnValuesTabItem.setControl(returnValuesGroup);

		final CTabFolder tabFolder_2 = new CTabFolder(sashForm, SWT.FLAT | SWT.BORDER);

		final CTabItem outputGraphsTabItem = new CTabItem(tabFolder_2, SWT.NONE);
		outputGraphsTabItem.setText("Output graphs");

		final CTabItem gnuPlotOutputTabItem = new CTabItem(tabFolder_2, SWT.NONE);
		gnuPlotOutputTabItem.setText("Gnuplot output");

		final Composite composite_7 = new Composite(tabFolder_2, SWT.NONE);
		final GridLayout gridLayout_6 = new GridLayout();
		gridLayout_6.verticalSpacing = 2;
		gridLayout_6.numColumns = 2;
		gridLayout_6.marginWidth = 2;
		gridLayout_6.marginHeight = 2;
		gridLayout_6.horizontalSpacing = 2;
		composite_7.setLayout(gridLayout_6);
		gnuPlotOutputTabItem.setControl(composite_7);

		gnuplotTable = new Table(composite_7, SWT.HIDE_SELECTION | SWT.FULL_SELECTION | SWT.BORDER);
		gnuplotTable.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				int selpos = gnuplotTable.getSelectionIndex();
				if (selpos != -1) {
					final int selind = gnuplotTable.getSelectionIndex();
					if (selind == -1) return;
					
					GnuplotOutputSelector selector = new GnuplotOutputSelector(AlgorithmScheduler.this.getShell(), SWT.NONE);
					
					List<String> inputs = getParameterNames();
					List<String> outputs = getReturnValueNames();
					Object[] olddata = (Object[]) gnuplotTable.getItem(selind).getData();
					final Object[] res = selector.open(inputs, outputs, (List<String[]>)olddata[1], (String)olddata[0]);
					if (res != null) {
						localctrl.issueCommand(new Irreversible<Model>("Edit gnu plot", new Message(Const.EVENT.GNUPLOT_TABLE_CHANGED)) {
							public Result doCommand(Model model) {
								model.gnuplots.get().set(selind, res);
								
								return new Result(evtypes);
							}
						});
					}
				}
			}
			public void widgetSelected(final SelectionEvent arg0) {
				int selpos = gnuplotTable.getSelectionIndex();
				if (selpos != -1) {
					editGnuplotButton.setEnabled(true);
					removeGnuplotButton.setEnabled(true);
				} else {
					editGnuplotButton.setEnabled(false);
					removeGnuplotButton.setEnabled(false);					
				}
			}
		});
		gnuplotTable.setLinesVisible(true);
		gnuplotTable.setHeaderVisible(true);
		gnuplotTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final TableColumn newColumnTableColumn_6 = new TableColumn(gnuplotTable, SWT.NONE);
		newColumnTableColumn_6.setWidth(167);
		newColumnTableColumn_6.setText("File name");

		final TableColumn newColumnTableColumn_7 = new TableColumn(gnuplotTable, SWT.NONE);
		newColumnTableColumn_7.setWidth(288);
		newColumnTableColumn_7.setText("Description");

		final Composite composite_8 = new Composite(composite_7, SWT.NONE);
		final GridData gd_composite_8 = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gd_composite_8.widthHint = 153;
		composite_8.setLayoutData(gd_composite_8);
		final GridLayout gridLayout_9 = new GridLayout();
		gridLayout_9.verticalSpacing = 2;
		gridLayout_9.marginWidth = 2;
		gridLayout_9.marginHeight = 2;
		gridLayout_9.horizontalSpacing = 2;
		composite_8.setLayout(gridLayout_9);

		final Button addButton_1 = new Button(composite_8, SWT.NONE);
		addButton_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				GnuplotOutputSelector selector = new GnuplotOutputSelector(AlgorithmScheduler.this.getShell(), SWT.NONE);
				
				List<String> inputs = getParameterNames();
				List<String> outputs = getReturnValueNames();
				
				final Object[] res = selector.open(inputs, outputs, null, null);
				if (res != null) {
					localctrl.issueCommand(new Irreversible<Model>("Add gnu plot", new Message(Const.EVENT.GNUPLOT_TABLE_CHANGED)) {
						public Result doCommand(Model model) {
							model.gnuplots.add(res);
							
							return new Result(evtypes);
						}
					});
				}
			}
		});
		addButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		addButton_1.setText("Add...");

		editGnuplotButton = new Button(composite_8, SWT.NONE);
		editGnuplotButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			public void widgetSelected(final SelectionEvent arg0) {
				final int selind = gnuplotTable.getSelectionIndex();
				if (selind == -1) return;
				
				GnuplotOutputSelector selector = new GnuplotOutputSelector(AlgorithmScheduler.this.getShell(), SWT.NONE);
				
				List<String> inputs = getParameterNames();
				List<String> outputs = getReturnValueNames();
				Object[] olddata = (Object[]) gnuplotTable.getItem(selind).getData();
				final Object[] res = selector.open(inputs, outputs, (List<String[]>)olddata[1], (String)olddata[0]);
				if (res != null) {
					localctrl.issueCommand(new Irreversible<Model>("Edit gnu plot", new Message(Const.EVENT.GNUPLOT_TABLE_CHANGED)) {
						public Result doCommand(Model model) {
							model.gnuplots.get().set(selind, res);
							
							return new Result(evtypes);
						}
					});
				}
			}
		});
		editGnuplotButton.setEnabled(false);
		final GridData gd_editGnuplotButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
		editGnuplotButton.setLayoutData(gd_editGnuplotButton);
		editGnuplotButton.setText("Edit...");

		removeGnuplotButton = new Button(composite_8, SWT.NONE);
		removeGnuplotButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				final int selind = gnuplotTable.getSelectionIndex();
				if (selind == -1) return;
				
				localctrl.issueCommand(new Irreversible<Model>("Remove gnu plot", new Message(Const.EVENT.GNUPLOT_TABLE_CHANGED)) {
					public Result doCommand(Model model) {
						model.gnuplots.get().remove(selind);
						
						return new Result(evtypes);
					}
				});
			}
		});
		removeGnuplotButton.setEnabled(false);
		final GridData gd_removeGnuplotButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_removeGnuplotButton.widthHint = 77;
		removeGnuplotButton.setLayoutData(gd_removeGnuplotButton);
		removeGnuplotButton.setText("Remove");
		tabFolder_2.setSelection(outputGraphsTabItem);

		final Composite outputGraphsGroup = new Composite(tabFolder_2, SWT.NONE);
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 2;
		gridLayout_3.verticalSpacing = 2;
		gridLayout_3.marginWidth = 2;
		gridLayout_3.marginHeight = 2;
		gridLayout_3.horizontalSpacing = 2;
		outputGraphsGroup.setLayout(gridLayout_3);

		graphTable = new Table(outputGraphsGroup, SWT.FULL_SELECTION | SWT.BORDER);
		graphTable.setLinesVisible(true);
		graphTable.setHeaderVisible(true);
		graphTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		graphTable.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				int wdt = graphTable.getSize().x - graphTable.getBorderWidth() * 2;
				graphTable.getColumn(0).setWidth((int) (wdt * 0.25));
				graphTable.getColumn(1).setWidth((int) (wdt * 0.75));
			}
		});
		graphTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setEnableStateOfGraphButtons();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				editSelectedGraph();
			}
		});

		final TableColumn newColumnTableColumn_2 = new TableColumn(graphTable, SWT.NONE);
		newColumnTableColumn_2.setWidth(124);
		newColumnTableColumn_2.setText("Graph name");

		final TableColumn newColumnTableColumn_3 = new TableColumn(graphTable, SWT.NONE);
		newColumnTableColumn_3.setWidth(349);
		newColumnTableColumn_3.setText("Dependency");

		final Composite composite_2 = new Composite(outputGraphsGroup, SWT.NONE);
		final GridData gd_composite_2 = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gd_composite_2.widthHint = 153;
		composite_2.setLayoutData(gd_composite_2);
		final GridLayout gridLayout_4 = new GridLayout();
		gridLayout_4.verticalSpacing = 2;
		gridLayout_4.marginWidth = 2;
		gridLayout_4.marginHeight = 2;
		gridLayout_4.horizontalSpacing = 2;
		composite_2.setLayout(gridLayout_4);
		outputGraphsTabItem.setControl(outputGraphsGroup);

		final Button addButton = new Button(composite_2, SWT.NONE);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				GraphSelector selector = new GraphSelector(AlgorithmScheduler.this.getShell(), 
						null, model.algorithm.get(), model.ranges.get());
				final IGraph graph = selector.open();
				if (graph == null) return;
				
				localctrl.issueCommand(new Undoable<Model>("Add graph", new Message(Const.EVENT.GRAPH_TABLE_CHANGED)) {
					public Result doCommand(Model model) {
						model.graphs.add(graph);
						
						return new Result(eventTypes(), Const.KEY.GRAPH_MUST_SELECT, model.graphs.get().size() - 1);
					}
					public Result undoCommand(Model model)
							throws IllegalStateException,
							UnsupportedOperationException {
						model.graphs.get().remove(model.graphs.get().size() - 1);
						
						return new Result(eventTypes(), Const.KEY.GRAPH_MUST_SELECT, model.graphs.get().size() - 1);
					}
				});
			}
		});
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		addButton.setText("Add...");

		editButton = new Button(composite_2, SWT.NONE);
		editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				editSelectedGraph();
			}
		});
		editButton.setEnabled(false);
		editButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		editButton.setText("Edit...");

		removeButton = new Button(composite_2, SWT.NONE);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				final int pos = graphTable.getSelectionIndex();
				if (pos == -1) return;
				
				localctrl.issueCommand(new Undoable<Model>("Remove graph", new Message(Const.EVENT.GRAPH_TABLE_CHANGED)) {
					private IGraph old;
					public Result doCommand(Model model) {
						old = model.graphs.getElem(pos);
						model.graphs.remove(pos);
						
						return new Result(eventTypes(), Const.KEY.MUST_SELECT, model.graphs.get().size() - 1);
					}
					public Result undoCommand(Model model)
							throws IllegalStateException,
							UnsupportedOperationException {
						model.graphs.get().add(pos, old);
						
						return new Result(eventTypes(), Const.KEY.MUST_SELECT, model.graphs.get().size() - 1);
					}
				});
			}
		});
		removeButton.setEnabled(false);
		removeButton.setLayoutData(new GridData(77, SWT.DEFAULT));
		removeButton.setText("Remove");
		sashForm.setWeights(new int[] {244, 122, 83 });
	}

	protected String extractColumnsString(List<String[]> cols) {
		StringBuilder sb = new StringBuilder();
		
		boolean first = true;
		for (String[] sf : cols) {
			if (first) first = false; else sb.append(", ");
			sb.append(sf[0]);
		}
		
		return sb.toString();
	}


	protected List<String> getReturnValueNames() {
		List<IParameter> outputs = model.algorithm.get().getReturnValues();
		List<String> lst = new ArrayList<String>();
		
		for (IParameter p : outputs) {
			lst.add(p.getName());
		}
		
		return lst;
	}


	protected List<String> getParameterNames() {
		List<IParameter> inputs = model.algorithm.get().getParameters();
		List<String> lst = new ArrayList<String>();
		
		for (IParameter p : inputs) {
			lst.add(p.getName());
		}
		
		return lst;
	}


	private void editSelectedGraph() {
		final int pos = graphTable.getSelectionIndex();
		if (pos == -1) return;
		
		GraphSelector selector = new GraphSelector(AlgorithmScheduler.this.getShell(), 
				model.graphs.getElem(pos), model.algorithm.get(), model.ranges.get());
		final IGraph graph = selector.open();
		if (graph == null) return;
		
		localctrl.issueCommand(new Undoable<Model>("Edit graph", new Message(Const.EVENT.GRAPH_TABLE_CHANGED)) {
			private IGraph oldgraph;
			public Result doCommand(Model model) {
				oldgraph = model.graphs.getElem(pos);
				model.graphs.get().set(pos, graph);
				
				return new Result(eventTypes(), Const.KEY.GRAPH_MUST_SELECT, pos);
			}
			public Result undoCommand(Model model)
					throws IllegalStateException,
					UnsupportedOperationException {
				model.graphs.get().set(pos, oldgraph);
				
				return new Result(eventTypes(), Const.KEY.GRAPH_MUST_SELECT, pos);
			}
		});
	}


	private void editSelectedRange() {
		final int pos = parameterTable.getSelectionIndex();
		if (pos == -1) return;
		final String paramname = model.ranges.getElem(pos).getParamName();
		IParameter parameter = model.algorithm.get().getDefaultValues().get(paramname).parameter();
		Object defval = model.algorithm.get().getDefaultValues().get(paramname).value();
		
		RangeSelector rs = new RangeSelector(AlgorithmScheduler.this.getShell(), parameter, defval, 
				model.ranges.getElem(pos));
		final IRange result = rs.open();
		if (result != null) localctrl.issueCommand(new Undoable<Model>("Change range", 
				new Message(Const.EVENT.RANGE_TABLE_CHANGED), new Message(Const.EVENT.GRAPH_TABLE_CHANGED)) {
			private IRange oldrange;
			private List<IGraph> oldgraphlist;
			public Result doCommand(Model model) {
				oldrange = model.ranges.getElem(pos);
				model.ranges.get().set(pos, result);
				oldgraphlist = model.graphs.get();
				List<IGraph> ngraphlist = new ArrayList<IGraph>();
				model.graphs.set(ngraphlist);
				for (IGraph g : oldgraphlist) {
					if (g.getCurveFamily().containsKey(paramname)) continue;
					ngraphlist.add(g);
				}
				
				return new Result(eventTypes(), Const.KEY.MUST_SELECT, pos, Const.KEY.GRAPH_MUST_SELECT, -1);
			}
			public Result undoCommand(Model model) throws IllegalStateException,
					UnsupportedOperationException
			{
				model.ranges.get().set(pos, oldrange);
				model.graphs.set(oldgraphlist);
				
				return new Result(eventTypes(), Const.KEY.MUST_SELECT, pos, Const.KEY.GRAPH_MUST_SELECT, -1);
			}
		});
	}


	private void setEnableStateOfGraphButtons() {
		int pos = graphTable.getSelectionIndex();
		if (pos == -1) {
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
		} else {
			editButton.setEnabled(true);
			removeButton.setEnabled(true);
		}
	}


	private void setEnableStateOfParameterButtons() {
		int pos = parameterTable.getSelectionIndex();
		if (pos == -1) {
			moveUpButton.setEnabled(false);
			moveDownButton.setEnabled(false);
			editSelectedButton.setEnabled(false);
			setToDefaultButton.setEnabled(false);
			return;
		}
		int sz = model.ranges.get().size();
		if (pos != 0) moveUpButton.setEnabled(true);
		else moveUpButton.setEnabled(false);
		if (pos != sz - 1) moveDownButton.setEnabled(true);
		else moveDownButton.setEnabled(false);
		editSelectedButton.setEnabled(true);
		setToDefaultButton.setEnabled(true);
	}


	private void initLocalMVC(CommandResult res) {
		model = new Model();
		localctrl = new Controller<Model>(model);
		
		// extract algorithm information
		model.algorithm.set((IAlgorithm)res.msg(Events.KEY.ALGORITHM_OBJECT));
		model.name.set("Algorithm Scheduler: " + model.algorithm.get().getName());
		model.parameters.set(model.algorithm.get().getParameters());
		model.runsPerSet.set(30);
		
		// build the range list
		final Map<String, IValue> defaultvals = model.algorithm.get().getDefaultValues();
		for (final IParameter param : model.parameters.get()) {
			model.ranges.add(getSingleRange(param.getName(), defaultvals.get(param.getName())));
		}
		
		// build the return value list
		for (final IParameter param : model.algorithm.get().getReturnValues()) {
			HandlerTypes ht = null;
			if (param.getParamType().isSimpleArithmeticType()) ht = HandlerTypes.Average;
			else ht = HandlerTypes.Last;
			model.returnhandlers.add(new ReturnHandler(param.getName(), ht));
		}
	}
	
	private IRange getSingleRange(final String name, final IValue value) {
		return new IRange() {
			public String getDescription() {
				return "Single value: " + value.getValueString();
			}
			public String getParamName() {
				return name;
			}
			public Iterator<Object> iterator() {
				return new SingleIterator<Object>(value.value());
			}
			public RangeData getRangeData() {
				RangeData data = new RangeData(value.parameter());
				data.singleVal = value.value();
				return data;
			}
		};
	}

	private void rebuildGraphTable(int select) {
		graphTable.removeAll();
		
		for (IGraph graph : model.graphs.get()) {
			TableItem item = new TableItem(graphTable, SWT.NONE);
			item.setText(0, graph.getGraphName());
			item.setText(1, graph.getDescription());
		}
		
		if (select != -1) graphTable.select(select);
	}

	private void rebuildRetValTable() {
		retValsTable.removeAll();
		for (Control c : retValsTable.getChildren()) {
			if (c instanceof CCombo) c.dispose();
		}
		
		int elemnum = -1;
		for (ReturnHandler rethandler : model.returnhandlers.get()) {
			elemnum++;
			TableItem item = new TableItem(retValsTable, SWT.NONE);
			item.setText(0, rethandler.getParamname());
			
			final CCombo combo = new CCombo(retValsTable, SWT.NONE);
			combo.setEditable(false);
			combo.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			IParameter parameter = getReturnValue(rethandler.getParamname());
			ParameterTypes pt = parameter.getParamType();
			for (HandlerTypes ht : HandlerTypes.values()) {
				if (pt.doesAllowHandler(parameter.getValueClass(), ht))	combo.add(ht.toString());
			}
			for (ReturnHandler other : model.returnhandlers.get()) {
				if (other.getParamname().equals(rethandler.getParamname())) continue;
				String s = "Bind to \"" + other.getParamname() + "\"";
				combo.add(s);
				combo.setData(s, other.getParamname());
			}
			combo.select(findPos(combo.getItems(), rethandler));
			
			final int epos = elemnum;
//			combo.addModifyListener(new ModifyListener() {
			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
//					System.out.println(((CCombo)e.getSource()).getSelectionIndex());
					final int pos = combo.getSelectionIndex();
					if (pos == -1) {
						System.out.println("None selected.");
						return;
					}
					
					final String selected = combo.getItem(pos);
					localctrl.issueCommand(new Undoable<Model>("Change policy",
							new Message(Const.EVENT.RETURN_TABLE_CHANGED)) {
						private ReturnHandler oldhandler;
						private boolean firsttime = true;
						public Result doCommand(Model model) {
							oldhandler = model.returnhandlers.getElem(epos);
							
							ReturnHandler newhandler = null;
							String boundto = (String)combo.getData(selected);
							if (boundto == null) {
								newhandler = new ReturnHandler(oldhandler.getParamname(), HandlerTypes.valueOf(selected));
							} else {
								newhandler = new ReturnHandler(oldhandler.getParamname(), null);
								newhandler.setBoundTo(boundto);
							}
							model.returnhandlers.get().set(epos, newhandler);
							
							if (firsttime) {
								firsttime = false;
								return new Result(new Message[]{});
							} else {
								return new Result(eventTypes());
							}
						}
						public Result undoCommand(Model model)
								throws IllegalStateException,
								UnsupportedOperationException {
							model.returnhandlers.get().set(pos, oldhandler);
							
							return new Result(eventTypes());
						}
					});
				}
			});
			
			TableEditor tabed = new TableEditor(retValsTable);
			tabed.grabHorizontal = true;
			tabed.setEditor(combo, item, 1);
		}
	}
	
	private int findPos(String[] items, ReturnHandler rethandler) {
		for (int i = 0; i < items.length; i++) {
			if ((rethandler.handler != null && items[i].equals(rethandler.handler.toString())) ||
					(rethandler.getBoundTo() != null && items[i].
							equals("Bind to \"" + rethandler.getBoundTo() + "\""))) return i;
		}
		return -1;
	}


	@SuppressWarnings("unused")
	private IParameter getParameter(String paramname) {
		for (IParameter param : model.parameters.get()) {
			if (param.getName().equals(paramname)) return param;
		}
		return null;
	}
	
	private IParameter getReturnValue(String name) {
		for (IParameter param : model.algorithm.get().getReturnValues()) {
			if (param.getName().equals(name)) return param;
		}
		return null;
	}

	private void rebuildParameterTable(int selectPos) {
		parameterTable.removeAll();
		
		for (IRange range : model.ranges.get()) {
			TableItem item = new TableItem(parameterTable, SWT.NONE);
			item.setText(0, range.getParamName());
			item.setText(1, range.getDescription());
		}
		
		if (selectPos >= 0) {
			parameterTable.select(selectPos);
			setEnableStateOfParameterButtons();
		}
	}

	@Override
	public Image getImage(Display d) {
		return new Image(d, ICON_EDITOR);
	}

	public boolean canRedo() {
		return localctrl.canRedo();
	}

	public boolean canUndo() {
		return localctrl.canUndo();
	}

	public String getEditorName() {
		return model.name.get();
	}

	public IInfoListener getInfoListener() {
		return null;
	}

	public void redo() {
		localctrl.redoCommand();
	}

	public void undo() {
		localctrl.undoCommand();
	}

	public void onEvent(final Events evtype, final CommandResult messages) {
		this.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed()) return;
				switch (evtype) {
				case JOB_STARTED:
					onJobStarted(evtype, messages);
					break;
				case JOB_HALTED:
					onJobHalted(evtype, messages);
					break;
				case JOB_FINISHED:
					onJobFinished(evtype, messages);
					break;
				case ALGORITHM_FINISHED:
					onAlgorithmFinished(evtype, messages);
					break;
				}
			}
		});
	}
	
	private void onAlgorithmFinished(Events evtype, CommandResult messages) {
		Long evid = (Long)messages.msg(Events.KEY.JOB_ID);
		
		if (evid != null && evid.equals(id)) {
			Integer setIndex = (Integer)messages.msg(Events.KEY.ITERATION_INDEX);
			Integer totalSets = (Integer)messages.msg(Events.KEY.TOTAL_ITERATIONS);
			setIndex++;
			
			progressBar.setSelection((int) (((double)setIndex) / totalSets * 100));
			currentSetSlot.setText(setIndex + " of " + totalSets);
		}
	}
	
	private void onJobHalted(Events evtype, CommandResult messages) {
		Long evid = (Long)messages.msg(Events.KEY.JOB_ID);
		
		if (evid != null && evid.equals(id)) {
			setToolbarButtons(false);
		}
	}

	private void onJobStarted(Events evtype, CommandResult messages) {
		Long evid = (Long) messages.msg(Events.KEY.JOB_ID);
		
		if (evid != null && evid.equals(id)) setToolbarButtons(true);
	}

	private void onJobFinished(Events evtype, CommandResult messages) {
		Long evid = (Long)messages.msg(Events.KEY.JOB_ID);
		
		if (evid != null && evid.equals(id)) {
			setToolbarButtons(false);
		}
	}

	private void setToolbarButtons(boolean isRunning) {
		if (isRunning) {
			runAlgButt.setEnabled(false);
			haltJobButt.setEnabled(true);
		} else {
			runAlgButt.setEnabled(true);
			haltJobButt.setEnabled(false);
		}
	}

	@Override
	public Map<String, String> getSaveTypes() {
		return EXTENSION_MAP;
	}

	@Override
	public void save(String extension, OutputStream os) {
		if (!DEFAULT_EXTENSION.equals(extension))
			throw new IllegalArgumentException("Invalid extension: " + extension);
		
		OutputStreamWriter w = new OutputStreamWriter(os);
		try {
			w.write(model.serialize());
			w.flush();
		} catch (IOException e) {
			throw new RuntimeException("Could not save to output stream.", e);
		}
	}

	@Override
	public String getLoadExtension() {
		return DEFAULT_EXTENSION;
	}

	@Override
	public boolean isLoadable() {
		return true;
	}

	@Override
	public void load(InputStream is) {
		try {
			model.deserialize(is, ctrl.getModel());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Cannot load.", e);
		}
		
		// successful load - refresh GUI
		localctrl.issueCommand(new Nonaltering<Model>("", new Message[]{new Message(Const.EVENT.GRAPH_TABLE_CHANGED),
				new Message(Const.EVENT.RANGE_TABLE_CHANGED), new Message(Const.EVENT.RETURN_TABLE_CHANGED),
				new Message(Const.EVENT.RUN_PER_SET_CHANGED), new Message(Const.EVENT.GNUPLOT_TABLE_CHANGED),
				new Message(Const.EVENT.ADD_HANDLERS_CHANGED)}) {
			public Result doCommand(Model o) {
				return new Result(evtypes);
			}
		});
	}
	
}














