package hr.fer.zemris.ga_framework.view.editors.algorithm_editor;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.controller.impl.commands.CreateInfoViewer;
import hr.fer.zemris.ga_framework.controller.impl.commands.HaltJob;
import hr.fer.zemris.ga_framework.controller.impl.commands.RunAlgorithm;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IParameterInventory;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.ReturnHandler;
import hr.fer.zemris.ga_framework.model.RunningAlgorithmInfo;
import hr.fer.zemris.ga_framework.model.impl.OneParameterSerie;
import hr.fer.zemris.ga_framework.model.impl.Value;
import hr.fer.zemris.ga_framework.view.Editor;
import hr.fer.zemris.ga_framework.view.ImageLoader;
import hr.fer.zemris.ga_framework.view.gadgets.algorithm_run_display.AlgorithmRunDisplay;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.IParameterListener;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.ParameterEditor;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.dialogs.ParameterDialogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import name.brijest.mvcapi.controller.Controller;
import name.brijest.mvcapi.controller.Message;
import name.brijest.mvcapi.controller.ObserverAdapter;
import name.brijest.mvcapi.controller.Result;
import name.brijest.mvcapi.controller.commands.Irreversible;
import name.brijest.mvcapi.controller.commands.Undoable;

import org.apache.commons.digester.Digester;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.xml.sax.SAXException;






public class AlgorithmEditor extends Editor {
	
	/* static fields */
	private static final ImageData ICON_EDITOR = ImageLoader.loadImage("icons", "application_side_list.png");
	private static final ImageData ICON_RUN = ImageLoader.loadImage("icons", "run.png");
	private static final ImageData ICON_RUN_NO_INFO = ImageLoader.loadImage("icons", "run_no_info.png");
	private static final ImageData ICON_PAUSE = ImageLoader.loadImage("icons", "pause.png");
	private static final ImageData ICON_RESUME = ImageLoader.loadImage("icons", "play.png");
	private static final ImageData ICON_SAVE = ImageLoader.loadImage("icons", "disk.png");
	private static final ImageData ICON_HALT = ImageLoader.loadImage("icons", "cancel.png");
	private static final ImageData ICON_INFO = ImageLoader.loadImage("icons", "book_big.png");
	private static final Map<String, String> EXTENSION_MAP = new LinkedHashMap<String, String>();
	private static final Events[] EVENT_TYPES = new Events[] {
		Events.ALGORITHM_STARTED,
		Events.ALGORITHM_FINISHED,
		Events.ALGORITHM_PAUSED,
		Events.ALGORITHM_RESUMED,
		Events.JOB_HALTED,
		Events.JOB_FINISHED
	};
	public static final String DEFAULT_EXTENSION = "par";
	
	static {
		EXTENSION_MAP.put(DEFAULT_EXTENSION, "Save algorithm parameters");
		EXTENSION_MAP.put("png", "Save canvas");
	}
	
	/* local mvc fields */
	private Model model;
	private Controller<Model> localctrl;
	
	/* private fields */
	private AlgorithmRunDisplay algorithmRunDisplay;
	private ParameterEditor parameterEditor;
	private Composite paramedWrapper;
	private CoolItem coolItem1;
	private ToolItem toolbar1_run;
	private ToolItem toolbar1_runNoInfo;
	private ToolItem toolbar1_resume;
	private ToolItem toolbar1_pause;
	private ToolItem toolbar1_save;
	private ToolItem toolbar1_halt;
	private CoolItem coolItem2;
	private ToolBar toolBar2;
	private ToolBar toolBar1;
	private ToolItem showInfoButt;
	private Timer timer;
	private long elapsed;
	
	/* ctors */
	
	public AlgorithmEditor(Composite c, IController controller, long elemid, CommandResult res) {
		super(c, SWT.NONE, controller, elemid, EVENT_TYPES);
		
		timer = new Timer(true);
		
		initLocalMVC(res);
		initGUI();
		advinitGUI();
		initStartingState(res);
	}
	
	/* methods */
	
	private void advinitGUI() {
		// configure icons for toolbars
		toolbar1_run.setImage(new Image(this.getDisplay(), ICON_RUN));
		toolbar1_runNoInfo.setImage(new Image(this.getDisplay(), ICON_RUN_NO_INFO));
		toolbar1_resume.setImage(new Image(this.getDisplay(), ICON_RESUME));
		toolbar1_pause.setImage(new Image(this.getDisplay(), ICON_PAUSE));
		toolbar1_save.setImage(new Image(this.getDisplay(), ICON_SAVE));
		toolbar1_halt.setImage(new Image(this.getDisplay(), ICON_HALT));
		showInfoButt.setImage(new Image(this.getDisplay(), ICON_INFO));
		
		// configure toolbars
		toolBar1.pack();
		Point sz = toolBar1.getSize();
		coolItem1.setControl(toolBar1);
		coolItem1.setSize(coolItem1.computeSize(sz.x, sz.y));
		coolItem1.setMinimumSize(sz);
		toolBar2.pack();
		sz = toolBar2.getSize();
		coolItem2.setControl(toolBar2);
		coolItem2.setSize(coolItem2.computeSize(sz.x, sz.y));
		coolItem2.setMinimumSize(sz);
		
		// configure toolbar actions
		toolbar1_run.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev) {
				IParameterInventory paramit = new OneParameterSerie(parameterEditor.getValues(), 
						ReturnHandler.createLastHandlers(model.algorithm.get().getReturnValues()), 1);
				ICommand run = new RunAlgorithm(id, "Single run of the " + model.algorithm.get().getName() + ".",
						model.algorithm.get().newInstance(), true, paramit);
				CommandResult result = ctrl.issueCommand(run);
				
				if (result.isSuccessful()) {
					setAlgorithmControlButtons(true);
				}
			}
		});
		toolbar1_runNoInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev) {
				IParameterInventory paramit = new OneParameterSerie(parameterEditor.getValues(), 
						ReturnHandler.createLastHandlers(model.algorithm.get().getReturnValues()), 1);
				ICommand run = new RunAlgorithm(id, "A single run of the " + model.name.get() + ".",
						model.algorithm.get().newInstance(), false, paramit);
				CommandResult result = ctrl.issueCommand(run);
				
				if (result.isSuccessful()) {
					setAlgorithmControlButtons(true);
				}
			}
		});
		toolbar1_halt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ctrl.issueCommand(new HaltJob(id));
			}
		});
		showInfoButt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ctrl.issueCommand(new CreateInfoViewer(model.algorithm.get().getClass()));
			}
		});
		
		// init listeners
		// set parameter listener
		parameterEditor.addParameterListener(new IParameterListener() {
			public void onParameterValueChange(int paramIndex, final IParameter param, final Object newvalue) {
				// try to change the value of this parameter in the model
				Object paramOldValue = model.parametervalues.getVal(param.getName()).value();
				if (newvalue.equals(paramOldValue)) return;
				
				Result r = localctrl.issueCommand(new Undoable<Model>("Change parameter", new Message(Const.EVENT.PARAMETER_CHANGED)) {
					private Object oldvalue = null;
					public Result doCommand(Model model) {
						// save old value
						IValue val = model.parametervalues.get().get(param.getName());
						oldvalue = val.value();
						
						// change value in the model
						if (val.parameter().isValueValid(newvalue)) {
							val.setValue(newvalue);
							return new Result(new Message(Const.EVENT.PARAMETER_CHANGED), "ParamName", param.getName());
						} else {
							return new Result(Const.ERROR.VALUE_NOT_ALLOWED, "Value is not allowed.");
						}
					}
					public Result undoCommand(Model model)
							throws IllegalStateException,
							UnsupportedOperationException
					{
						// change value in model to old value
						model.parametervalues.get().get(param.getName()).setValue(oldvalue);
						
						return new Result(new Message(Const.EVENT.PARAMETER_CHANGED), "ParamName", param.getName());
					}
				});
				
				// if model value could not be changed, set old value to cell
				if (!r.isSuccessful()) {
					parameterEditor.setParameterValue(param.getName(), 
							model.parametervalues.get().get(param.getName()).value());
				}
			}
		});
		
		// display parameters in parameter editor
		parameterEditor.setParameters(model.parameters.get(), model.parametervalues.get());
		pack();
		
		// update algorithm control buttons enabled/disabled
		setAlgorithmControlButtons(model.algorithm.get().isRunning());
		
		// add dispose listener
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				timer.cancel();
			}
		});
	}

	private void initStartingState(CommandResult res) {
		boolean isRunning = ctrl.getModel().getActiveJobs().containsKey(id);
		
		if (isRunning) {
			// set button state
			// set timer state
			// set run state label
			algorithmRunDisplay.setAlgorithmStatus(true, false);
			setAlgorithmControlButtons(true);
			resumeTimer();
			algorithmRunDisplay.useCanvas(true);
		}
	}

	private void initGUI() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		
		final CoolBar coolBar = new CoolBar(this, SWT.NONE);
		coolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		coolBar.setLocked(true);

		coolItem1 = new CoolItem(coolBar, SWT.NONE);
		coolItem1.setText("Run");

		toolBar1 = new ToolBar(coolBar, SWT.NONE);
		coolItem1.setControl(toolBar1);

		toolbar1_run = new ToolItem(toolBar1, SWT.PUSH);
		toolbar1_run.setToolTipText("Run algorithm");
		
		toolbar1_runNoInfo = new ToolItem(toolBar1, SWT.PUSH);
		toolbar1_runNoInfo.setToolTipText("Run algorithm without displaying info");
		
		toolbar1_resume = new ToolItem(toolBar1, SWT.PUSH);
		toolbar1_resume.setEnabled(false);
		toolbar1_resume.setToolTipText("Resume algorithm");
		
		toolbar1_pause = new ToolItem(toolBar1, SWT.PUSH);
		toolbar1_pause.setEnabled(false);
		toolbar1_pause.setToolTipText("Pause algorithm");

		toolbar1_halt = new ToolItem(toolBar1, SWT.PUSH);
		toolbar1_halt.setEnabled(false);
		toolbar1_halt.setToolTipText("Terminate algorithm");
		
		toolbar1_save = new ToolItem(toolBar1, SWT.PUSH);
		toolbar1_save.setEnabled(false);
		toolbar1_save.setToolTipText("Save algorithm state");

		coolItem2 = new CoolItem(coolBar, SWT.PUSH);
		coolItem2.setText("New item");

		toolBar2 = new ToolBar(coolBar, SWT.NONE);
		coolItem2.setControl(toolBar2);

		showInfoButt = new ToolItem(toolBar2, SWT.PUSH);
		showInfoButt.setToolTipText("Show extensive info about the algorithm");
		
		final Composite composite = new Composite(this, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 0;
		composite.setLayout(gridLayout_1);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		final SashForm sashForm = new SashForm(composite, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final ViewForm viewForm = new ViewForm(sashForm, SWT.FLAT | SWT.BORDER);

		paramedWrapper = new Composite(viewForm, SWT.NONE);
		paramedWrapper.setLayout(new FillLayout());
		viewForm.setContent(paramedWrapper);
		
		parameterEditor = new ParameterEditor(paramedWrapper, SWT.NONE);

		algorithmRunDisplay = new AlgorithmRunDisplay(sashForm, SWT.NONE);
		sashForm.setWeights(new int[] {140, 439 });
	}
	
	private void initLocalMVC(CommandResult res) {
		model = new Model();
		IAlgorithm algo = (IAlgorithm)res.msg(Events.KEY.ALGORITHM_OBJECT);
		model.name.set("Algorithm Editor: " + algo.getName());
		model.algorithm.set(algo);
		localctrl = new Controller<Model>(model);
		
		// cycle through the list of parameters of the algorithm
		// and create them inside the model
		Map<String, IValue> parametermap = model.parametervalues.get();
		for (Entry<String, IValue> ntr : algo.getDefaultValues().entrySet()) {
			parametermap.put(ntr.getKey(), ntr.getValue());
		}
		model.parameters.set(algo.getParameters());
		
		// init listeners
		localctrl.addObserver(new int[] { Const.EVENT.PARAMETER_CHANGED }, new ObserverAdapter() {
			@Override
			public void afterCommand(Message event) {
				String paramname = (String) event.datamap.get("ParamName");
				parameterEditor.setParameterValue(paramname, model.parametervalues.getVal(paramname).value());
			}
		});
	}

	@Override
	public Image getImage(Display d) {
		return new Image(d, ICON_EDITOR);
	}
	
	
	/* IEditor methods */

	public void onEvent(final Events evtype, final CommandResult messages) {
		this.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed()) return;
				switch (evtype) {
				case ALGORITHM_STARTED:
					onAlgorithmStarted(messages);
					break;
				case ALGORITHM_FINISHED:
					onAlgorithmFinished(messages);
					break;
				case ALGORITHM_PAUSED:
					onAlgorithmPaused(messages);
					break;
				case ALGORITHM_RESUMED:
					onAlgorithmResumed(messages);
					break;
				case JOB_HALTED:
					onJobHaltedOrFinished(messages);
					break;
				case JOB_FINISHED:
					onJobHaltedOrFinished(messages);
					break;
				default:
					break;
				}
			}
		});
	}
	
	private void resumeTimer() {
		RunningAlgorithmInfo nfo = ctrl.getModel().getDispatcher().getJobInfo(id);
		if (nfo == null) return;
		elapsed = (long)nfo.getElapsed().getInterval();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!algorithmRunDisplay.isDisposed()) {
					algorithmRunDisplay.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (algorithmRunDisplay.isDisposed()) return;
							elapsed += 1000;
							algorithmRunDisplay.setTimeLabel(String.valueOf(elapsed / 1000) + " s");
						}
					});
				} else {
					timer.cancel();
				}
			}
		}, 1000, 1000);
	}
	
	private void stopTimer() {
		timer.cancel();
		timer = new Timer(true);
	}
	
	private void onAlgorithmResumed(CommandResult messages) {
		Long jobid = (Long)messages.msg(Events.KEY.JOB_ID);
		if (jobid == null || id != jobid) return;
		algorithmRunDisplay.setAlgorithmStatus(true, false);
		setAlgorithmControlButtons(true);
		resumeTimer();
	}


	private void onAlgorithmPaused(CommandResult messages) {
		Long jobid = (Long)messages.msg(Events.KEY.JOB_ID);
		if (jobid == null || id != jobid) return;
		algorithmRunDisplay.setAlgorithmStatus(true, true);
		setAlgorithmControlButtons(true);
		stopTimer();
	}


	private void onJobHaltedOrFinished(CommandResult messages) {
		Long jobid = (Long)messages.msg(Events.KEY.JOB_ID);
		if (jobid == null || id != jobid) return;
		algorithmRunDisplay.setAlgorithmStatus(false, false);
		setAlgorithmControlButtons(false);
		stopTimer();
	}

	private void onAlgorithmStarted(CommandResult messages) {
		Long jobid = (Long)messages.msg(Events.KEY.JOB_ID);
		if (jobid == null || id != jobid) return;
		algorithmRunDisplay.setAlgorithmStatus(true, false);
		setAlgorithmControlButtons(true);
		algorithmRunDisplay.clearProperties();
		algorithmRunDisplay.clearConsole();
		resumeTimer();
	}
	
	@SuppressWarnings("unchecked")
	private void onAlgorithmFinished(CommandResult messages) {
		Long jobid = (Long)messages.msg(Events.KEY.JOB_ID);
		if (jobid == null || id != jobid) return;
		algorithmRunDisplay.setAlgorithmStatus(false, false);
		setAlgorithmControlButtons(false);
		stopTimer();
		
		// extract values from return map and set them
		displayReturnValues((Map<String, IValue>)messages.msg(Events.KEY.RETURN_VALS));
	}

	private void displayReturnValues(Map<String, IValue> retvals) {
		algorithmRunDisplay.clearResults();
		for (Entry<String, IValue> ntr : retvals.entrySet()) {
			algorithmRunDisplay.addToResults(ntr.getKey(), ntr.getValue());
		}
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

	public void redo() {
		localctrl.redoCommand();
	}

	public void undo() {
		localctrl.undoCommand();
	}

	public IInfoListener getInfoListener() {
		return algorithmRunDisplay;
	}
	
	/**
	 * Sets the algorithm control buttons to appropriate
	 * enabled/disabled state.
	 */
	private void setAlgorithmControlButtons(boolean isRunning) {
		if (!isRunning) {
			// halt button
			toolbar1_halt.setEnabled(false);
			
			// save button
			toolbar1_save.setEnabled(false);
			
			// run buttons
			toolbar1_runNoInfo.setEnabled(true);
			if (model.algorithm.get().doesReturnInfoDuringRun()) {
				toolbar1_run.setEnabled(true);
			} else {
				toolbar1_run.setEnabled(false);
			}
			
			// pause/resume buttons
			toolbar1_pause.setEnabled(false);
			toolbar1_resume.setEnabled(false);
		} else {
			// halt button
			toolbar1_halt.setEnabled(true);
			
			// run buttons
			toolbar1_run.setEnabled(false);
			toolbar1_runNoInfo.setEnabled(false);
			
			// pause/resume buttons, save button
			toolbar1_save.setEnabled(false);
			if (model.algorithm.get().isPausable()) {
				if (model.algorithm.get().isPaused()) {
					toolbar1_pause.setEnabled(false);
					toolbar1_resume.setEnabled(true);
					
					// save button
					if (model.algorithm.get().isSaveable()) toolbar1_save.setEnabled(true);
				} else {
					toolbar1_pause.setEnabled(true);
					toolbar1_resume.setEnabled(false);
				}
			} else {
				toolbar1_pause.setEnabled(false);
				toolbar1_resume.setEnabled(false);
			}
		}
	}

	@Override
	public Map<String, String> getSaveTypes() {
		return EXTENSION_MAP;
	}

	@Override
	public void save(String extension, OutputStream os) {
		if (extension.equals(DEFAULT_EXTENSION)) saveParameters(os);
		else if (extension.equals("png")) saveCanvas(os);
		else throw new IllegalArgumentException("Unknown extension: " + extension);
	}

	private void saveCanvas(OutputStream os) {
		Image img = algorithmRunDisplay.getCanvas().getImage();
		ImageData data = img.getImageData();
		org.eclipse.swt.graphics.ImageLoader loader = new org.eclipse.swt.graphics.ImageLoader();
		loader.data = new ImageData[] {data};
		loader.save(os, SWT.IMAGE_PNG);
	}

	private void saveParameters(OutputStream os) {
		Writer w = new OutputStreamWriter(os);
		try {
			w.write("<AlgorithmParameters>\n");
			
			w.write("<AlgorithmClassName>");
			w.write(model.algorithm.get().getClass().getName());
			w.write("</AlgorithmClassName>\n");
			
			w.write("<Parameters>\n");
			for (Entry<String, IValue> ntr : model.parametervalues.get().entrySet()) {
				// iterate through parameters and serialize them
				w.write("<p>\n");
				w.write("\t<name>");
				w.write(ntr.getKey());
				w.write("</name>\n");
				if (!ntr.getValue().parameter().getParamType().isISerializable()) {
					w.write("\t<value>\n\t\t");
					w.write(ntr.getValue().value().toString());
					w.write("\n\t</value>\n");
				} else {
					w.write("\t<value>\n\t\t");
					String s = ((ISerializable)ntr.getValue().value()).serialize();
					w.write("<![CDATA[");
					w.write(s);
					w.write("]]>");
					w.write("\n\t</value>\n");
					w.write("\t<type>\n\t\t");
					w.write(ntr.getValue().value().getClass().getName());
					w.write("\n\t</type>\n");
				}
				w.write("</p>\n");
			}
			w.write("</Parameters>\n");
			
			w.write("</AlgorithmParameters>\n");
			w.flush();
		} catch (IOException e) {
			throw new RuntimeException("Could not write to stream.", e);
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
		Digester d = new Digester();
		
		// set rules
		d.addObjectCreate("AlgorithmParameters", ParameterFileWrapper.class);
		d.addBeanPropertySetter("AlgorithmParameters/AlgorithmClassName", "algorithmClassName");
		d.addObjectCreate("AlgorithmParameters/Parameters/p", ParameterFileWrapper.ParameterWrapper.class);
		d.addBeanPropertySetter("AlgorithmParameters/Parameters/p/name", "name");
		d.addBeanPropertySetter("AlgorithmParameters/Parameters/p/value", "valueString");
		d.addBeanPropertySetter("AlgorithmParameters/Parameters/p/type", "type");
		d.addSetNext("AlgorithmParameters/Parameters/p", "addParameterWrapper");
		
		// parse
		ParameterFileWrapper pwrap = null;
		try {
			pwrap = (ParameterFileWrapper) d.parse(is);
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load.", e);
		} catch (SAXException e) {
			throw new IllegalArgumentException("Could not load.", e);
		}
		final ParameterFileWrapper wrapper = pwrap;
		
		// load
		Result result = localctrl.issueCommand(new Irreversible<Model>("Load model", 
				new Message(Const.EVENT.ALGORITHM_CHANGED)) {
			public Result doCommand(Model model) {
				// create algorithm class and algorithm instance
				String clazzname = wrapper.getAlgorithmClassName();
				try {
					Class<?> cls = ctrl.getModel().getClass(clazzname);
					if (cls == null) throw new ClassNotFoundException();
					IAlgorithm algo = (IAlgorithm) cls.newInstance();
					model.algorithm.set(algo);
				} catch (ClassNotFoundException e) {
					return new Result(Const.ERROR.ALGORITHM_DOES_NOT_EXIST, "Algorithm class '" + clazzname +
							"' does not exist.");
				} catch (InstantiationException e) {
					return new Result(Const.ERROR.UNKNOWN_INSTANTIATION_ERROR, e.toString());
				} catch (IllegalAccessException e) {
					return new Result(Const.ERROR.UNKNOWN_INSTANTIATION_ERROR, e.toString());
				}
				
				// set parameters and their values, set editor name
				IAlgorithm alg = model.algorithm.get();
				ParameterDialogFactory.registerDialogs(alg.getEditors());
				model.parameters.set(alg.getParameters());
				model.parametervalues.set(new HashMap<String, IValue>());
				for (ParameterFileWrapper.ParameterWrapper pw : wrapper.getParameters()) {
					IParameter param = alg.getParameter(pw.getName());
					if (param == null) 
						return new Result(Const.ERROR.INVALID_SERIALIZATION, "An invalid serialization.");
					if (!param.getParamType().isISerializable()) {
						IValue val = new Value(param.getParamType().deserialize(pw.getValueString()), param);
						model.parametervalues.putVal(pw.getName(), val);
					} else {
						try {
							Class<?> iserclass = ctrl.getModel().getClass(pw.getType());
							if (iserclass == null) iserclass = Class.forName(pw.getType());
							ISerializable ser = (ISerializable) iserclass.newInstance();
							ser = ser.deserialize(pw.getValueString());
							IValue val = new Value(ser, param);
							model.parametervalues.putVal(pw.getName(), val);
						} catch (Exception e) {
							return new Result(Const.ERROR.UNKNOWN_INSTANTIATION_ERROR,
									"Could not create ISerializable parameter of type '" + pw.getType() +
									"' due to exception: " + e + ", " + e.getStackTrace()[0]);
						}
					}
				}
				model.name.set("Algorithm Editor: " + alg.getName());
				
				return new Result(new Message(Const.EVENT.ALGORITHM_CHANGED));
			}
		});
		
		if (!result.isSuccessful()) {
			// not successful
			throw new IllegalArgumentException(result.getErrorMessage());
		} else {
			// success - update GUI
			parameterEditor.rebuild(model.parameters.get(), model.parametervalues.get());
			setAlgorithmControlButtons(false);
		}
	}
	
}














