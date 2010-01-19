package hr.fer.zemris.ga_framework.view;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.controller.IEditor;
import hr.fer.zemris.ga_framework.controller.IListener;
import hr.fer.zemris.ga_framework.controller.IUnsuccessfulListener;
import hr.fer.zemris.ga_framework.controller.IView;
import hr.fer.zemris.ga_framework.controller.impl.commands.CreateView;
import hr.fer.zemris.ga_framework.controller.impl.commands.LoadAlgorithmsAndResources;
import hr.fer.zemris.ga_framework.controller.impl.commands.LoadGraphToGraphEditor;
import hr.fer.zemris.ga_framework.controller.impl.commands.LoadParametersToEditor;
import hr.fer.zemris.ga_framework.controller.impl.commands.LoadScheduleToScheduler;
import hr.fer.zemris.ga_framework.model.IGraph;
import hr.fer.zemris.ga_framework.model.IParameterInventory;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.model.RunningAlgorithmInfo;
import hr.fer.zemris.ga_framework.view.Editor.IObserver;
import hr.fer.zemris.ga_framework.view.editors.EditorFactory;
import hr.fer.zemris.ga_framework.view.editors.algorithm_editor.AlgorithmEditor;
import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.AlgorithmScheduler;
import hr.fer.zemris.ga_framework.view.editors.graph_editor.GraphEditor;
import hr.fer.zemris.ga_framework.view.gadgets.dialogs.ErrorDialog;
import hr.fer.zemris.ga_framework.view.gadgets.dialogs.InstallModuleDialog;
import hr.fer.zemris.ga_framework.view.gadgets.dialogs.WarningListDialog;
import hr.fer.zemris.ga_framework.view.gadgets.dialogs.about_dialog.AboutDialog;
import hr.fer.zemris.ga_framework.view.views.ViewFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.UIManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;




public class Master implements IListener, IUnsuccessfulListener {
	
	/* static fields */
	private static final ImageData ITEM_ICON = ImageLoader.loadImage("icons", "greenball.png");
	private static Master MASTER = null;
	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Application.logexcept("Swing look and feel could not be set.", e);
		}
	}

	/* private fields */
	private MenuItem saveMenuItem;
	private Menu dsaveMenu;
	private MenuItem redoMenuItem;
	private MenuItem undoMenuItem;
	private SashForm sashForm;
	private CTabFolder editorTabFolder;
	private Label statusText;
	private CTabFolder leftTabFolder;
	private Menu showview_menu;
	private MenuItem showViewMenuItem;
	private Shell geneticAlgorithmIdeShell;
	private IController ctrl;
	private Map<Long, IView> views;
	private Map<Long, IEditor> editors;
	private Object id_gen_lock;
	private long id_generator;
	private Display disp;
	
	/* ctors */
	
	public Master() {
		views = new HashMap<Long, IView>();
		editors = new HashMap<Long, IEditor>();
		id_generator = 0;
		id_gen_lock = new Object();
		
		if (MASTER != null) throw new IllegalStateException("Cannot create more than one master window.");
		MASTER = this;
	}

	/* methods */
	
	private void initGUI() {
		geneticAlgorithmIdeShell = new Shell();
		geneticAlgorithmIdeShell.setText("Evolutionary Computing IDE");
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 2;
		gridLayout.marginWidth = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		geneticAlgorithmIdeShell.setLayout(gridLayout);

		final Menu menu = new Menu(geneticAlgorithmIdeShell, SWT.BAR);
		geneticAlgorithmIdeShell.setMenuBar(menu);

		final MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.addArmListener(new ArmListener() {
			public void widgetArmed(final ArmEvent arg0) {
				setFileMenuOptions(getSelectedEditor());
			}
		});
		fileMenuItem.setText("&File");

		final Menu menu_1 = new Menu(fileMenuItem);
		fileMenuItem.setMenu(menu_1);

		final MenuItem openMenuItem = new MenuItem(menu_1, SWT.CASCADE);
		openMenuItem.setText("&Open");

		final Menu menu_5 = new Menu(openMenuItem);
		openMenuItem.setMenu(menu_5);

		final MenuItem parameterSetMenuItem = new MenuItem(menu_5, SWT.NONE);
		parameterSetMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				FileDialog fd = new FileDialog(Master.this.geneticAlgorithmIdeShell, SWT.OPEN);
				fd.setText("Open parameter set");
				String[] filterExt = {"*." + AlgorithmEditor.DEFAULT_EXTENSION, "*.*"};
				fd.setFilterExtensions(filterExt);
				
				String selected = fd.open();
				if (selected != null) {
					ctrl.issueCommand(new LoadParametersToEditor(selected));
				}
			}
		});
		parameterSetMenuItem.setText("&Parameter set...");

		final MenuItem scheduleMenuItem = new MenuItem(menu_5, SWT.NONE);
		scheduleMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				FileDialog fd = new FileDialog(Master.this.geneticAlgorithmIdeShell, SWT.OPEN);
				fd.setText("Open schedule");
				String[] filterExt = {"*." + AlgorithmScheduler.DEFAULT_EXTENSION, "*.*"};
				fd.setFilterExtensions(filterExt);
				
				String selected = fd.open();
				if (selected != null) {
					ctrl.issueCommand(new LoadScheduleToScheduler(selected));
				}
			}
		});
		scheduleMenuItem.setText("&Schedule...");

		final MenuItem graphMenuItem = new MenuItem(menu_5, SWT.NONE);
		graphMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				FileDialog fd = new FileDialog(Master.this.geneticAlgorithmIdeShell, SWT.OPEN);
				fd.setText("Open graph");
				String[] filterExt = {"*." + GraphEditor.DEFAULT_EXTENSION, "*.*"};
				fd.setFilterExtensions(filterExt);
				
				String selected = fd.open();
				if (selected != null) {
					ctrl.issueCommand(new LoadGraphToGraphEditor(selected));
				}
			}
		});
		graphMenuItem.setText("&Graph...");

		saveMenuItem = new MenuItem(menu_1, SWT.CASCADE);
		saveMenuItem.addArmListener(new ArmListener() {
			public void widgetArmed(final ArmEvent e) {
				recreateSaveMenu(getSelectedEditor());
			}
		});
		saveMenuItem.setText("&Save");

		dsaveMenu = new Menu(saveMenuItem);
		saveMenuItem.setMenu(dsaveMenu);

		new MenuItem(menu_1, SWT.SEPARATOR);

		final MenuItem exitMenuItem = new MenuItem(menu_1, SWT.NONE);
		exitMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				geneticAlgorithmIdeShell.close();
			}
		});
		exitMenuItem.setText("E&xit");

		final MenuItem editMenuItem = new MenuItem(menu, SWT.CASCADE);
		editMenuItem.addArmListener(new ArmListener() {
			public void widgetArmed(final ArmEvent arg0) {
				setEditMenuOptions(getSelectedEditor());
			}
		});
		editMenuItem.setText("&Edit");

		final Menu menu_4 = new Menu(editMenuItem);
		editMenuItem.setMenu(menu_4);

		undoMenuItem = new MenuItem(menu_4, SWT.NONE);
		undoMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				getSelectedEditor().undo();
			}
		});
		undoMenuItem.setEnabled(false);
		undoMenuItem.setText("&Undo");

		redoMenuItem = new MenuItem(menu_4, SWT.NONE);
		redoMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				getSelectedEditor().redo();
			}
		});
		redoMenuItem.setEnabled(false);
		redoMenuItem.setText("&Redo");

		final MenuItem windowMenuItem = new MenuItem(menu, SWT.CASCADE);
		windowMenuItem.setText("&Window");

		final Menu menu_2 = new Menu(windowMenuItem);
		windowMenuItem.setMenu(menu_2);

		showViewMenuItem = new MenuItem(menu_2, SWT.CASCADE);
		showViewMenuItem.setText("&Show view");

		showview_menu = new Menu(showViewMenuItem);
		showViewMenuItem.setMenu(showview_menu);

		final MenuItem toolsMenuItem = new MenuItem(menu, SWT.CASCADE);
		toolsMenuItem.setText("&Tools");

		final Menu menu_6 = new Menu(toolsMenuItem);
		toolsMenuItem.setMenu(menu_6);

		final MenuItem installModuleMenuItem = new MenuItem(menu_6, SWT.NONE);
		installModuleMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				InstallModuleDialog moddial = new InstallModuleDialog(geneticAlgorithmIdeShell, SWT.NONE);
				moddial.open(ctrl.getModel());
			}
		});
		installModuleMenuItem.setText("&Install module...");

		final MenuItem helpMenuItem = new MenuItem(menu, SWT.CASCADE);
		helpMenuItem.setText("&Help");

		final Menu menu_3 = new Menu(helpMenuItem);
		helpMenuItem.setMenu(menu_3);

		final MenuItem aboutMenuItem = new MenuItem(menu_3, SWT.NONE);
		aboutMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				AboutDialog dial = new AboutDialog(geneticAlgorithmIdeShell, SWT.NONE);
				dial.open();
			}
		});
		aboutMenuItem.setText("&About...");

		final CoolBar coolbar = new CoolBar(geneticAlgorithmIdeShell, SWT.NONE);
		GridData cb_gdata = new GridData(SWT.FILL, SWT.CENTER, true, false);
		coolbar.setLayoutData(cb_gdata);

		final CoolItem newItemCoolItem = new CoolItem(coolbar, SWT.PUSH);
		newItemCoolItem.setText("New item");

		final Composite mainPanel = new Composite(geneticAlgorithmIdeShell, SWT.NONE);
		mainPanel.setLayout(new FillLayout());
		GridData mp_gdata = new GridData(GridData.FILL_BOTH);
		mainPanel.setLayoutData(mp_gdata);

		sashForm = new SashForm(mainPanel, SWT.NONE);
		sashForm.setLayout(new GridLayout());

		leftTabFolder = new CTabFolder(sashForm, SWT.BORDER);
		leftTabFolder.setSelectionForeground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
		leftTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		
		editorTabFolder = new CTabFolder(sashForm, SWT.BORDER);
		editorTabFolder.setSelectionForeground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
		editorTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));

		final Composite statusBar = new Composite(geneticAlgorithmIdeShell, SWT.NONE);
		final GridData gd_statusBar = new GridData(SWT.FILL, SWT.FILL, false, false);
		statusBar.setLayoutData(gd_statusBar);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.marginHeight = 0;
		gridLayout_1.marginWidth = 0;
		gridLayout_1.verticalSpacing = 0;
		gridLayout_1.horizontalSpacing = 0;
		statusBar.setLayout(gridLayout_1);

		statusText = new Label(statusBar, SWT.NONE);
		sashForm.setWeights(new int[] {114, 365 });
	}
	
	private void recreateSaveMenu(final Editor ed) {
		// clear save submenu
		for (MenuItem item : dsaveMenu.getItems()) {
			item.dispose();
		}
		
		if (ed != null) {
			// fill save submenu
			for (final Entry<String, String> ntr : ed.getSaveTypes().entrySet()) {
				MenuItem subitem = new MenuItem(dsaveMenu, SWT.NONE);
				subitem.setText(ntr.getValue() + "...");
				
				subitem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						FileDialog fd = new FileDialog(Master.this.geneticAlgorithmIdeShell, SWT.SAVE);
						fd.setOverwrite(true);
						fd.setText(ntr.getValue());
						String[] filterExt = { "*." + ntr.getKey(), "*.*" };
						fd.setFilterExtensions(filterExt);
						String selected = fd.open();
						
						if (selected != null) {
							// save item under the specified name
							FileOutputStream os = null;
							try {
								os = new FileOutputStream(selected);
							} catch (FileNotFoundException e1) {
								Application.logexcept("Could not create file to save.", e1);
								return;
							}
							try {
								ed.save(ntr.getKey(), os);
							} catch (RuntimeException e2) {
								Application.logexcept("Could not save editor.", e2);
							} finally {
								try {
									os.close();
								} catch (IOException e1) {
									Application.logexcept("Could not close file stream.", e1);
								}
							}
						}
					}
				});
			}
		}
	}

	protected Editor getSelectedEditor() {
		int pos = editorTabFolder.getSelectionIndex();
		if (pos == -1) return null;
		
		CTabItem tabitem = editorTabFolder.getItem(pos);
		Editor ed = (Editor) tabitem.getControl();
		
		return ed;
	}

	private void setMenuOptions(Editor ed) {
		// set undo/redo enable state
		setEditMenuOptions(ed);
		
		// set file menu options
		setFileMenuOptions(ed);
	}
	
	private void setFileMenuOptions(Editor ed) {
		if (ed == null || ed.getSaveTypes().isEmpty()) {
			saveMenuItem.setEnabled(false);
		} else {
			saveMenuItem.setEnabled(true);
		}
	}

	private void setEditMenuOptions(Editor ed) {
		if (ed == null) {
			undoMenuItem.setEnabled(false);
			redoMenuItem.setEnabled(false);
		} else {
			if (ed.canUndo()) undoMenuItem.setEnabled(true);
			else undoMenuItem.setEnabled(false);
			if (ed.canRedo()) redoMenuItem.setEnabled(true);
			else redoMenuItem.setEnabled(false);
		}
	}

	private void advinitGUI() {
		// set pictures
		geneticAlgorithmIdeShell.setImage(new Image(disp, ImageLoader.loadImage("icons", "ga_ide_big.png")));
		
		// generate menu items for creating various views
		Map<String, String> views = getAvailableViews();
		for (Entry<String, String> ntry : views.entrySet()) {
			final String clsname = ntry.getKey();
			MenuItem mi = new MenuItem(showview_menu, SWT.NONE);
			mi.setText(ntry.getValue());
			mi.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					// issue a command to the controller - create view
					ICommand comm = new CreateView(clsname);
					ctrl.issueCommand(comm);
				}
			});
		}
		
		// register as event listener
		ctrl.registerListener(new Events[]{ 
				Events.VIEW_CREATED, Events.EDITOR_CREATED, Events.JOB_FINISHED,
				Events.ALGORITHM_ERROR, Events.EDITOR_LOADED
				}, this);
		
		// register as unsuccessful command listener
		ctrl.registerUnsuccessfulCommandListener(this);
		
		// get open views
		loadGUIProperties();
		
		// add on exit event
		geneticAlgorithmIdeShell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				WarningListDialog wdial = new WarningListDialog(geneticAlgorithmIdeShell, SWT.CANCEL);
				Collection<RunningAlgorithmInfo> c = ctrl.getModel().getActiveJobs().values();
				String[] items = new String[c.size()];
				int i = 0;
				for (RunningAlgorithmInfo info : c) {
					items[i++] = info.getDescription();
				}
				if (items.length == 0 || wdial.open("Jobs still running.",
						"Some of the jobs are still running, and will " +
						"be stopped if the application is terminated. Nonresponding jobs will be stopped " +
						"by terminating their threads. This may cause problems in some cases, and it is " +
						"recommended that you cancel the jobs manually. Click OK to exit anyway.",
						items, ITEM_ICON))
				{
					// save open views to user configuration
					saveGUIProperties();
				} else {
					e.doit = false;
				}
			}
		});
	}
	
	private void saveGUIProperties() {
		// save open views
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (CTabItem item : leftTabFolder.getItems()) {
			if (first) first = false; else sb.append(';');
			sb.append(item.getControl().getClass().getName());
		}
		Application.setUserProperty("Master.open_views", sb.toString());
		
		// save sash pane width
		int[] weights = sashForm.getWeights();
		Application.setUserProperty("Master.sash_form_weights", weights[0] + "," + weights[1]);
		Application.setUserProperty("Master.size", geneticAlgorithmIdeShell.getSize().x + "," + 
				geneticAlgorithmIdeShell.getSize().y);
		Application.setUserProperty("Master.window_max", String.valueOf(geneticAlgorithmIdeShell.getMaximized()));
	}

	private void loadGUIProperties() {
		// load open views
		try {
			String s = Application.getUserProperty("Master.open_views");
			if (s != null) {
				String[] sf = s.split(";");
				for (String vs : sf) {
					createView(generateId(), vs);
				}
			}
		} catch (Exception e) {
			Application.logexcept("Could not open views from previous session.", e);
		}
		
		// load sash pane weights
		try {
			String s = Application.getUserProperty("Master.sash_form_weights");
			if (s != null) {
				String[] sf = s.split(",");
				int[] weights = new int[2];
				weights[0] = Integer.parseInt(sf[0]);
				weights[1] = Integer.parseInt(sf[1]);
				sashForm.setWeights(weights);
			}
		} catch (Exception e) {
			Application.logexcept("Could not load sash width from previous session.", e);
		}
		
		// set sizes
		try {
			String s = Application.getUserProperty("Master.size");
			if (s != null) {
				String[] sf = s.split(",");
				int[] sizes = new int[2];
				sizes[0] = Integer.parseInt(sf[0]);
				sizes[1] = Integer.parseInt(sf[1]);
				geneticAlgorithmIdeShell.setSize(sizes[0], sizes[1]);
			}
		} catch (Exception e) {
			Application.logexcept("Could not load window sizes from previous session.", e);
		}
		
		// set window state
		try {
			String s = Application.getUserProperty("Master.window_max");
			if (s != null) {
				boolean b = Boolean.parseBoolean(s);
				if (b) geneticAlgorithmIdeShell.setMaximized(true);
			}
		} catch (Exception e) {
			Application.logexcept("Could not load window maximize state from previous session.", e);
		}
	}

	private Map<String, String> getAvailableViews() {
		return ViewFactory.getAllViews();
	}
	
	public void onUnsuccessfulCommand(final CommandResult result) {
		if (!geneticAlgorithmIdeShell.isDisposed()) geneticAlgorithmIdeShell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (geneticAlgorithmIdeShell.isDisposed()) return;
				
				if (!result.isSuccessful()) setStatusText(result.getErrorMessage());
			}
		});
	}

	public void onEvent(final Events evtype, final CommandResult result) {
		if (!geneticAlgorithmIdeShell.isDisposed()) geneticAlgorithmIdeShell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (geneticAlgorithmIdeShell.isDisposed()) return;
				
				switch (evtype) {
				case VIEW_CREATED:
					String clsname = (String) result.msg(Events.KEY.VIEW_CLSNAME);
					createView(generateId(), clsname);
					break;
				case EDITOR_CREATED:
					clsname = (String) result.msg(Events.KEY.EDITOR_CLSNAME);
					createEditor(generateId(), clsname, result);
					break;
				case EDITOR_LOADED:
					clsname = (String) result.msg(Events.KEY.EDITOR_CLSNAME);
					loadEditor(generateId(), clsname, result);
					break;
				case JOB_FINISHED:
					onJobFinished(evtype, result);
					break;
				case ALGORITHM_ERROR:
					onAlgorithmError(evtype, result);
					break;
				}
			}
		});
	}
	
	private void loadEditor(long generatedId, String clsname, CommandResult result) {
		Editor ed = createEditor(generatedId, clsname, result);
		
		if (ed == null) {
			Application.logerror("Cannot load.", "Could not create editor, for loading later. Class: " + clsname);
		} else {
			FileInputStream is = null;
			try {
				is = new FileInputStream((String)result.msg(Events.KEY.FILE_FOR_LOAD));
				ed.load(is);
				findEditorCTabWithId(ed.getId()).setText(ed.getEditorName());
			} catch (FileNotFoundException e) {
				Application.logerror("Cannot load.", "Could not find file: " + result.msg(Events.KEY.FILE_FOR_LOAD));
				findEditorCTabWithId(ed.getId()).dispose();
			} catch (IllegalArgumentException e) {
				Application.logexcept("Cannot load (due to corrupt file).", e);
				findEditorCTabWithId(ed.getId()).dispose();
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						Application.logexcept("Could not close file.", e);
					}
			}
		}
	}
	
	private CTabItem findEditorCTabWithId(Long id) {
		for (CTabItem item : editorTabFolder.getItems()) {
			if (((Editor)item.getControl()).getId() == id) return item;
		}
		return null;
	}

	private void onAlgorithmError(Events evtype, CommandResult result) {
		ErrorDialog errdial = new ErrorDialog(geneticAlgorithmIdeShell);
		errdial.open("It seems an algorithm has produced an error. See exception message " +
				"and stack trace for details.",
				(Exception) result.msg(Events.KEY.EXCEPTION_OBJECT));
	}

	@SuppressWarnings("unchecked")
	private void onJobFinished(Events evtype, CommandResult result) {
		RunningAlgorithmInfo nfo = (RunningAlgorithmInfo) result.msg(Events.KEY.JOB_INFO);
		
		// for each graph, open graph editor
		List<IGraph> graphs = (List<IGraph>)nfo.getData(Model.KEY.GRAPH_LIST);
		if (graphs != null) {
			for (IGraph g : graphs) {
				result.putMsg(Events.KEY.GRAPH_OBJECT, g);
				result.putMsg(Events.KEY.ALGORITHM_OBJECT, nfo.getJobs().get(0).first);
				createEditor(++id_generator, GraphEditor.class.getName(), result);
			}
			result.putMsg(Events.KEY.GRAPH_OBJECT, null);
		}
		List<Object[]> gnuplots = (List<Object[]>) nfo.getData(Model.KEY.GNUPLOT_LIST);
		if (gnuplots != null) {
			for (Object[] gp : gnuplots) {
				String filename = (String) gp[0];
				List<String[]> columns = (List<String[]>) gp[1];
				createGnuPlotFile(nfo, filename, columns);
			}
		}
	}

	private void createGnuPlotFile(RunningAlgorithmInfo nfo, String filename, List<String[]> columns) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			
			List<String> incols = new ArrayList<String>();
			List<String> retcols = new ArrayList<String>();
			for (String[] sf : columns) {
				if (sf[1] != null) retcols.add(sf[0]);
				else incols.add(sf[0]);
			}
			Map<List<Object>, List<Object>> valuemap = new LinkedHashMap<List<Object>, List<Object>>();
			
			IParameterInventory inventory = nfo.getJobs().get(0).second;
			for (Map<String, IValue> inputs : inventory) {
				Map<String, IValue> retvals = inventory.getReturnValues(inputs);
				List<Object> ins = new ArrayList<Object>(), outs = new ArrayList<Object>();
				for (String s : incols) ins.add(inputs.get(s).value());
				for (String s : retcols) outs.add(retvals.get(s).value());
				valuemap.put(ins, outs);
			}
			
			for (Entry<List<Object>, List<Object>> ntr : valuemap.entrySet()) {
				for (String[] sf : columns) {
					Object output = null;
					if (sf[1] != null) output = ntr.getValue().get(retcols.indexOf(sf[0]));
					else output = ntr.getKey().get(incols.indexOf(sf[0]));
					osw.write(output.toString());
					osw.write('\t');
				}
				osw.write("\n");
			}
			
			osw.flush();
			fos.close();
		} catch (Exception e) {
			Application.logexcept("Could not save gnuplot file.", e);
		}
	}

	private void createView(final Long id, String clsname) {
		final View view = ViewFactory.createView(clsname, leftTabFolder, ctrl, id);
		if (view != null) {
			CTabItem tabitem = new CTabItem(leftTabFolder, SWT.CLOSE);
			tabitem.setText(view.getViewName());
			tabitem.setImage(view.getImage(disp));
			tabitem.setControl(view);
			tabitem.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent arg0) {
					view.dispose();
					views.remove(id);
				}
			});
			leftTabFolder.setSelection(tabitem);
			views.put(id, view);
		}
	}
	
	private Editor createEditor(Long generatedId, String clsname, CommandResult res) {
		// check for proposed id
		final Long proposedId = (Long) res.msg(Events.KEY.EDITOR_ID);
		if (proposedId != null) generatedId = proposedId;
		final Long id = generatedId;
		
		// see if id already taken
		if (editors.containsKey(id)) {
			// try to focus that editor
			int pos = -1;
			for (CTabItem item : editorTabFolder.getItems()) {
				pos++;
				if (id.equals(item.getData("id"))) editorTabFolder.setSelection(pos);
			}
			return null;
		}
		
		// create editor
		final Editor editor = EditorFactory.createEditor(clsname, editorTabFolder, ctrl, id, res);
		if (editor != null) {
			CTabItem tabitem = new CTabItem(editorTabFolder, SWT.CLOSE);
			tabitem.setData("id", id);
			tabitem.setText(editor.getEditorName());
			tabitem.setImage(editor.getImage(disp));
			tabitem.setControl(editor);
			tabitem.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					editor.dispose();
					editors.remove(id);
				}
			});
			editorTabFolder.setSelection(tabitem);
			editors.put(id, editor);
			editor.addObserver(new IObserver() {
				public void onAction() {
					setMenuOptions(editor);
				}
			});
		}
		
		return editor;
	}
	
	private void initModel() {
		// send necessary commands
		ICommand command = new LoadAlgorithmsAndResources("algorithms");
		CommandResult res = ctrl.issueCommand(command);
		
		if (res.isSuccessful()) {
			clearStatusText();
		} else {
			setStatusText(res.getErrorMessage());
		}
	}
	
	public void setStatusText(String text) {
		statusText.setText(text);
	}
	
	public void clearStatusText() {
		statusText.setText("");
	}

	public void open(IController controller) {
		disp = new Display();
		ctrl = controller;
		
		initGUI();
		advinitGUI();
		initModel();
		geneticAlgorithmIdeShell.open();
		geneticAlgorithmIdeShell.layout();
		
		while (!geneticAlgorithmIdeShell.isDisposed()) {
			if (!disp.readAndDispatch()) disp.sleep();
		}
		
		disp.dispose();
	}
	
	public long generateId() {
		synchronized (id_gen_lock) {
			return ++id_generator;
		}
	}
	
	public static Master getMaster() {
		return MASTER;
	}

}














