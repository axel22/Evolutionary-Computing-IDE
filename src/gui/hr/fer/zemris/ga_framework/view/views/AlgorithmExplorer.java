package hr.fer.zemris.ga_framework.view.views;


import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.controller.impl.commands.CreateAlgorithmEditor;
import hr.fer.zemris.ga_framework.controller.impl.commands.CreateAlgorithmScheduler;
import hr.fer.zemris.ga_framework.controller.impl.commands.CreateInfoViewer;
import hr.fer.zemris.ga_framework.model.AlgoDir;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.view.ImageLoader;
import hr.fer.zemris.ga_framework.view.View;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;




public class AlgorithmExplorer extends View {
	
	public static interface Observer {
		public void onAlgorithmOpened(Class<?> algoclass);
		public void onAlgorithmScheduled(Class<?> algoclass);
		public void onAlgorithmInfoRequested(Class<?> algoclass);
	}

	/* static fields */
	private static final ImageData XPL_IMAGE = ImageLoader.loadImage("icons", "AlgorithmExplorer.png");
	private static final ImageData DIR_IMAGE = ImageLoader.loadImage("icons", "folder.png");
	private static final ImageData ALG_IMAGE = ImageLoader.loadImage("icons", "algorithm.png");
	private static final ImageData OPEN_ALGED_IMAGE = ImageLoader.loadImage("icons", "application_side_list.png");
	private static final ImageData OPEN_ALGSCH_IMAGE = ImageLoader.loadImage("icons", "table.png");
	private static final ImageData OPEN_ALGINFO_IMAGE = ImageLoader.loadImage("icons", "book.png");
	
	/* private fields */
	private Tree tree;
	private List<Observer> listeners;
	private SelectionListener sel_listener;
	private ToolItem openAlgInfo;
	private Composite composite;
	private ToolItem openAlgScheduler;
	private ToolItem openAlgEditor;
	private Text nativeText;
	private Text interactionText;
	private Text descText;
	private Text authorText;
	private Text nameText;
	private MenuItem showExtensiveInfoMenuItem;
	private MenuItem openAlgorithmSchedulerMenuItem;
	private MenuItem openAlgorithmEditorMenuItem;
	
	/* ctors */

	/**
	 * Create the algorithm explorer view.
	 * @param parent
	 * @param style
	 */
	public AlgorithmExplorer(Composite parent, IController controller, long id) {
		super(parent, SWT.NONE, controller, id, new Events[] {Events.ALGORITHM_TREE_CHANGED});
		
		listeners = new ArrayList<Observer>();
		
		initGUI();
		fillTree();
		advinitGUI();
	}

	/* methods */
	
	public void addAlgorithmListener(Observer o) {
		listeners.add(o);
	}
	
	public boolean removeAlgorithmListener(Observer o) {
		return listeners.remove(o);
	}
	
	private void clearTree() {
		tree.removeAll();
	}

	public void onEvent(final Events evtype, CommandResult messages) {
		this.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed()) return;
				if (evtype == Events.ALGORITHM_TREE_CHANGED) {
					clearTree();
					fillTree();
				}
			}
		});
	}

	private void fillTree() {
		// access model and build tree
		AlgoDir dir = ctrl.getModel().getAlgorithmTree();
		if (sel_listener == null) {
			sel_listener = new SelectionAdapter() {
				@Override
				public void widgetDefaultSelected(SelectionEvent event) {
					Object obj = event.item.getData();
					if (obj != null) informListenersOfOpened((Class<?>) obj);
				}
				@Override
				public void widgetSelected(SelectionEvent e) {
					Object obj = e.item.getData();
					setAlgorithmInfoAndButtons((Class<?>)obj);
				}
			};
			tree.addSelectionListener(sel_listener);
		}
		
		// create root
		Image dirimg = new Image(this.getDisplay(), DIR_IMAGE);
		TreeItem root = new TreeItem(tree, SWT.NONE);
		root.setText("Algorithms");
		root.setImage(dirimg);
		
		// create algorithm image
		Image img = new Image(this.getDisplay(), ALG_IMAGE);
		
		// create subnodes
		for (AlgoDir sub : dir.getChildren()) {
			TreeItem item = new TreeItem(root, SWT.NONE);
			item.setText(sub.getName());
			item.setImage(dirimg);
			fillTreeRec(sub, item, img, dirimg, sel_listener);
		}
		
		// create algorithms
		for (final Class<?> cls : dir.getAlgorithms()) {
			TreeItem item = new TreeItem(root, SWT.NONE);
			item.setText(cls.getSimpleName());
			item.setImage(img);
			item.setData(cls);
		}
	}
	
	private void setAlgorithmInfoAndButtons(Class<?> obj) {
		if (obj == null) {
			nameText.setText("");
			authorText.setText("");
			interactionText.setText("");
			nativeText.setText("");
			descText.setText("");
			openAlgEditor.setEnabled(false);
			openAlgScheduler.setEnabled(false);
			openAlgInfo.setEnabled(false);
			openAlgorithmEditorMenuItem.setEnabled(false);
			openAlgorithmSchedulerMenuItem.setEnabled(false);
			showExtensiveInfoMenuItem.setEnabled(false);
		} else try {
			IAlgorithm alg = (IAlgorithm)obj.newInstance();
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			if (alg.doesReturnInfoDuringRun()) {
				first = false;
				sb.append("returns info");
			}
			if (alg.isPausable()) {
				if (!first) sb.append(", ");
				first = false;
				sb.append("pausable");
			}
			if (alg.isSaveable()) {
				if (!first) sb.append(", ");
				first = false;
				sb.append("saveable");
			}
			String inter = sb.toString();
			inter = (String.valueOf(inter.charAt(0)).toUpperCase()) + inter.substring(1);

			nameText.setText(alg.getName());
			authorText.setText(createAuthorsString(alg.getAuthors()));
			interactionText.setText(inter);
			descText.setText(alg.getDescription());
			if (alg.isNative()) nativeText.setText("Yes"); else nativeText.setText("No");
			
			// set buttons
			openAlgEditor.setEnabled(true);
			openAlgScheduler.setEnabled(true);
			openAlgInfo.setEnabled(true);
			openAlgorithmEditorMenuItem.setEnabled(true);
			openAlgorithmSchedulerMenuItem.setEnabled(true);
			showExtensiveInfoMenuItem.setEnabled(true);
		} catch (Exception e) {
			Application.logexcept("Could not display algorithm info.", e);
		}
	}

	private String createAuthorsString(List<String> authorlist) {
		StringBuilder sb = new StringBuilder();
		
		if (authorlist.size() > 0) {
			boolean first = true;
			for (String a : authorlist) {
				if (first) first = false; else sb.append(", ");
				sb.append(a);
			}
		} else {
			sb.append("(none)");
		}
		
		return sb.toString();
	}

	private void fillTreeRec(AlgoDir dir, TreeItem subroot, Image img, Image dirimg,
			SelectionListener sel_listener)
	{
		// create subnodes
		for (AlgoDir sub : dir.getChildren()) {
			TreeItem item = new TreeItem(subroot, SWT.NONE);
			item.setText(sub.getName());
			item.setImage(dirimg);
			fillTreeRec(sub, item, img, dirimg, sel_listener);
		}
		
		// create algorithms
		for (final Class<?> cls : dir.getAlgorithms()) {
			TreeItem item = new TreeItem(subroot, SWT.NONE);
			item.setText(cls.getSimpleName());
			item.setImage(img);
			item.setData(cls);
		}
	}

	public String getViewName() {
		return "Algorithm Explorer";
	}
	
	@Override
	public Image getImage(Display d) {
		return new Image(d, XPL_IMAGE);
	}
	
	private void informListenersOfOpened(Class<?> cls) {
		for (Observer o : listeners) {
			o.onAlgorithmOpened(cls);
		}
	}
	
	private void informListenersOfInfoRequested(Class<?> cls) {
		for (Observer o : listeners) {
			o.onAlgorithmInfoRequested(cls);
		}
	}
	
	private void informListenersOfScheduled(Class<?> cls) {
		for (Observer o : listeners) {
			o.onAlgorithmScheduled(cls);
		}
	}

	private void initGUI() {
		final FillLayout fillLayout = new FillLayout();
		fillLayout.marginWidth = 2;
		fillLayout.marginHeight = 2;
		setLayout(fillLayout);
		final SashForm sashForm = new SashForm(this, SWT.VERTICAL);

		final ViewForm viewForm = new ViewForm(sashForm, SWT.FLAT | SWT.BORDER);

		tree = new Tree(viewForm, SWT.NONE);
		viewForm.setContent(tree);

		final Menu treeMenu = new Menu(tree);
		tree.setMenu(treeMenu);

		openAlgorithmEditorMenuItem = new MenuItem(treeMenu, SWT.NONE);
		openAlgorithmEditorMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0) {
					Object obj = selection[0].getData();
					if (obj != null) informListenersOfOpened((Class<?>) obj);
				}
			}
		});
		openAlgorithmEditorMenuItem.setText("Open algorithm editor");

		openAlgorithmSchedulerMenuItem = new MenuItem(treeMenu, SWT.NONE);
		openAlgorithmSchedulerMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0) {
					Object obj = selection[0].getData();
					if (obj != null) informListenersOfScheduled((Class<?>) obj);
				}
			}
		});
		openAlgorithmSchedulerMenuItem.setText("Open algorithm scheduler");

		showExtensiveInfoMenuItem = new MenuItem(treeMenu, SWT.NONE);
		showExtensiveInfoMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0) {
					Object obj = selection[0].getData();
					if (obj != null) informListenersOfInfoRequested((Class<?>) obj);
				}
			}
		});
		showExtensiveInfoMenuItem.setText("Show extensive info");

		final Composite composite_4 = new Composite(sashForm, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.verticalSpacing = 0;
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 0;
		gridLayout_1.horizontalSpacing = 0;
		composite_4.setLayout(gridLayout_1);

		ToolBar tb = new ToolBar(composite_4, SWT.NONE);
		tb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		openAlgEditor = new ToolItem(tb, SWT.PUSH);
		openAlgEditor.setEnabled(false);
		openAlgEditor.setToolTipText("Open algorithm editor");
		openAlgEditor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0) {
					Object obj = selection[0].getData();
					if (obj != null) informListenersOfOpened((Class<?>) obj);
				}
			}
		});

		openAlgScheduler = new ToolItem(tb, SWT.PUSH);
		openAlgScheduler.setEnabled(false);
		openAlgScheduler.setToolTipText("Open algorithm scheduler");
		openAlgScheduler.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0) {
					Object obj = selection[0].getData();
					if (obj != null) informListenersOfScheduled((Class<?>) obj);
				}
			}
		});

		openAlgInfo = new ToolItem(tb, SWT.PUSH);
		openAlgInfo.setEnabled(false);
		openAlgInfo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0) {
					Object obj = selection[0].getData();
					if (obj != null) informListenersOfInfoRequested((Class<?>) obj);
				}
			}
		});
		openAlgInfo.setToolTipText("Open extensive algorithm info");

		final ViewForm viewForm_1 = new ViewForm(composite_4, SWT.FLAT | SWT.BORDER);
		viewForm_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		composite = new Composite(viewForm_1, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 1;
		gridLayout.horizontalSpacing = 1;
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		viewForm_1.setContent(composite);

		final Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		final GridData gd_composite_1 = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_composite_1.widthHint = 34;
		composite_1.setLayoutData(gd_composite_1);
		composite_1.setLayout(new FillLayout());

		final Label nameLabel = new Label(composite_1, SWT.NONE);
		nameLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		nameLabel.setText("Name");

		nameText = new Text(composite, SWT.READ_ONLY);
		nameText.setEditable(false);
		nameText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		final GridData gd_composite_2 = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_composite_2.widthHint = 34;
		composite_2.setLayoutData(gd_composite_2);
		composite_2.setLayout(new FillLayout());

		final Label authorLabel = new Label(composite_2, SWT.NONE);
		authorLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		authorLabel.setText("Author");

		authorText = new Text(composite, SWT.READ_ONLY);
		authorText.setEditable(false);
		authorText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		authorText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Composite composite_5 = new Composite(composite, SWT.NONE);
		final GridData gd_composite_5 = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_composite_5.widthHint = 30;
		composite_5.setLayoutData(gd_composite_5);
		composite_5.setLayout(new FillLayout());

		final Label returningInfoLabel = new Label(composite_5, SWT.NONE);
		returningInfoLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		returningInfoLabel.setText("Interaction");

		interactionText = new Text(composite, SWT.READ_ONLY);
		interactionText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		interactionText.setEditable(false);
		interactionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Composite composite_6 = new Composite(composite, SWT.NONE);
		composite_6.setLayout(new FillLayout());
		final GridData gd_composite_6 = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_composite_6.widthHint = 30;
		composite_6.setLayoutData(gd_composite_6);

		final Label label = new Label(composite_6, SWT.NONE);
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		label.setText("Native");

		nativeText = new Text(composite, SWT.READ_ONLY);
		nativeText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		nativeText.setEditable(false);
		final GridData gd_nativeText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		nativeText.setLayoutData(gd_nativeText);

		final Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		final GridData gd_composite_3 = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd_composite_3.widthHint = 58;
		composite_3.setLayoutData(gd_composite_3);
		composite_3.setLayout(new FillLayout());

		final Label descriptionLabel = new Label(composite_3, SWT.NONE);
		descriptionLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		descriptionLabel.setText("Description");

		descText = new Text(composite, SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI);
		descText.setEditable(false);
		descText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		descText.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashForm.setWeights(new int[] {256, 112 });
	}
	
	private void advinitGUI() {
		// set pictures
		openAlgEditor.setImage(new Image(this.getDisplay(), OPEN_ALGED_IMAGE));
		openAlgScheduler.setImage(new Image(this.getDisplay(), OPEN_ALGSCH_IMAGE));
		openAlgInfo.setImage(new Image(this.getDisplay(), OPEN_ALGINFO_IMAGE));
		openAlgorithmEditorMenuItem.setImage(new Image(this.getDisplay(), OPEN_ALGED_IMAGE));
		openAlgorithmSchedulerMenuItem.setImage(new Image(this.getDisplay(), OPEN_ALGSCH_IMAGE));
		showExtensiveInfoMenuItem.setImage(new Image(this.getDisplay(), OPEN_ALGINFO_IMAGE));
		
		// add listener for informing controller
		addAlgorithmListener(new Observer() {
			public void onAlgorithmOpened(Class<?> algoclass) {
				ctrl.issueCommand(new CreateAlgorithmEditor(algoclass));
			}
			public void onAlgorithmScheduled(Class<?> algoclass) {
				ctrl.issueCommand(new CreateAlgorithmScheduler(algoclass));
			}
			public void onAlgorithmInfoRequested(Class<?> algoclass) {
				ctrl.issueCommand(new CreateInfoViewer(algoclass));
			}
		});
	}

	@Override
	public Control createTopControl(CTabFolder parent) {
		return null;
	}
	
}














