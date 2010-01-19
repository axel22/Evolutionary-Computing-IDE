package hr.fer.zemris.ga_framework.view.gadgets.algorithm_run_display;

import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IPainter;
import hr.fer.zemris.ga_framework.model.IValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;



public class AlgorithmRunDisplay extends Composite implements IInfoListener {

	/* static fields */

	/* private fields */
	private StyledText consoletext;
	private ProgressBar progressBar;
	private AlgorithmCanvas algorithmCanvas;
	private Table resultProperties;
	private Table runPropsTable;
	private Map<String, TableItem> runProperties;
	private List<TableItem> runPropsItemList;
	private Label algorithmStatusLabel;
	private Label runningTimeSlotLabel;
	private List<Color> stcolors;
	private int conssize;
	
	/* ctors */

	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public AlgorithmRunDisplay(Composite parent, int style) {
		super(parent, style);
		
		runProperties = new HashMap<String, TableItem>();
		runPropsItemList = new ArrayList<TableItem>();
		stcolors = new ArrayList<Color>();
		conssize = 0;
		
		initGUI();
		advinitGUI();
	}
	
	
	
	
	
	/* methods */

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void initGUI() {
		final FillLayout fillLayout = new FillLayout();
		fillLayout.marginWidth = 1;
		fillLayout.marginHeight = 1;
		setLayout(fillLayout);

		final SashForm sashForm = new SashForm(this, SWT.VERTICAL);

		final ViewForm viewForm_2 = new ViewForm(sashForm, SWT.FLAT | SWT.BORDER);

		final SashForm sashForm_1 = new SashForm(viewForm_2, SWT.VERTICAL);
		viewForm_2.setContent(sashForm_1);

		final ScrolledComposite scrolledComposite = new ScrolledComposite(sashForm_1, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent arg0) {
				if (algorithmCanvas != null) {
					Point algsz = algorithmCanvas.getSize();
					Point sz = scrolledComposite.getSize();
					if (algsz.x < sz.x) algsz.x = sz.x;
					if (algsz.y < sz.y) algsz.y = sz.y;
					algorithmCanvas.setSize(algsz);
				}
			}
		});
		scrolledComposite.setAlwaysShowScrollBars(true);

		algorithmCanvas = new AlgorithmCanvas(scrolledComposite, SWT.NONE);
		algorithmCanvas.setLocation(0, 0);
		algorithmCanvas.setSize(270, 226);
		scrolledComposite.setContent(algorithmCanvas);
		sashForm_1.setWeights(new int[] {235 });

		final CTabFolder tabFolder = new CTabFolder(sashForm, SWT.BORDER);

		final CTabItem runStatusTabItem = new CTabItem(tabFolder, SWT.NONE);
		runStatusTabItem.setText("Run status");

		final CTabItem runControlTabItem = new CTabItem(tabFolder, SWT.NONE);
		runControlTabItem.setText("Run control");

		final ViewForm viewForm_3 = new ViewForm(tabFolder, SWT.FLAT | SWT.BORDER);
		runControlTabItem.setControl(viewForm_3);

		final CTabItem consoleTabItem = new CTabItem(tabFolder, SWT.NONE);
		consoleTabItem.setText("Console");
		tabFolder.setSelection(runStatusTabItem);

		final ViewForm viewForm_1 = new ViewForm(tabFolder, SWT.FLAT | SWT.BORDER);
		consoleTabItem.setControl(viewForm_1);

		consoletext = new StyledText(viewForm_1, SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL);
		viewForm_1.setContent(consoletext);
		consoletext.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		consoletext.setSize(490, 115);
		consoletext.setFont(new Font(this.getDisplay(), new FontData("Courier New", 10, SWT.NORMAL)));

		final CTabItem algorithmResultsTabItem = new CTabItem(tabFolder, SWT.NONE);
		algorithmResultsTabItem.setText("Results");

		resultProperties = new Table(tabFolder, SWT.BORDER);
		resultProperties.addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent arg0) {
				resultProperties.getColumn(0).setWidth((resultProperties.getSize().x - resultProperties.getBorderWidth() * 2) / 2);
				resultProperties.getColumn(1).setWidth((resultProperties.getSize().x - resultProperties.getBorderWidth() * 2) / 2);
			}
		});
		resultProperties.setLinesVisible(true);
		resultProperties.setHeaderVisible(true);
		algorithmResultsTabItem.setControl(resultProperties);

		final TableColumn newColumnTableColumn_2 = new TableColumn(resultProperties, SWT.NONE);
		newColumnTableColumn_2.setWidth(100);
		newColumnTableColumn_2.setText("Key");

		final TableColumn newColumnTableColumn_3 = new TableColumn(resultProperties, SWT.NONE);
		newColumnTableColumn_3.setWidth(100);
		newColumnTableColumn_3.setText("Value");

		final ViewForm viewForm = new ViewForm(tabFolder, SWT.FLAT | SWT.BORDER);

		final Composite composite = new Composite(viewForm, SWT.NONE);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 3;
		gridLayout_2.marginWidth = 0;
		gridLayout_2.marginHeight = 0;
		gridLayout_2.horizontalSpacing = 0;
		composite.setLayout(gridLayout_2);
		viewForm.setContent(composite);
		runStatusTabItem.setControl(viewForm);

		runPropsTable = new Table(composite, SWT.HIDE_SELECTION | SWT.FULL_SELECTION);
		runPropsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		runPropsTable.addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent ev) {
				runPropsTable.getColumn(0).setWidth((runPropsTable.getSize().x - runPropsTable.getBorderWidth() * 2) * 1/4);
				runPropsTable.getColumn(1).setWidth((runPropsTable.getSize().x - runPropsTable.getBorderWidth() * 2) * 3/4);
			}
		});
		runPropsTable.setLinesVisible(true);

		final TableColumn newColumnTableColumn = new TableColumn(runPropsTable, SWT.NONE);
		newColumnTableColumn.setWidth(100);
		newColumnTableColumn.setText("Property");

		final TableColumn newColumnTableColumn_1 = new TableColumn(runPropsTable, SWT.NONE);
		newColumnTableColumn_1.setWidth(100);
		newColumnTableColumn_1.setText("Value");

		final Label label = new Label(composite, SWT.SEPARATOR);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));

		final Composite composite_1 = new Composite(composite, SWT.NONE);
		final GridData gd_composite_1 = new GridData(SWT.RIGHT, SWT.FILL, false, true);
		gd_composite_1.widthHint = 193;
		composite_1.setLayoutData(gd_composite_1);
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 2;
		gridLayout_3.verticalSpacing = 3;
		gridLayout_3.marginWidth = 2;
		gridLayout_3.marginHeight = 2;
		composite_1.setLayout(gridLayout_3);

		final Label stateLabel = new Label(composite_1, SWT.NONE);
		stateLabel.setText("Status");

		algorithmStatusLabel = new Label(composite_1, SWT.NONE);

		final Label runningTimeLabel = new Label(composite_1, SWT.NONE);
		runningTimeLabel.setText("Running time");

		runningTimeSlotLabel = new Label(composite_1, SWT.NONE);
		runningTimeSlotLabel.setText("0 s");
		runningTimeSlotLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Label percentCompleteLabel = new Label(composite_1, SWT.NONE);
		percentCompleteLabel.setText("Percent complete");

		progressBar = new ProgressBar(composite_1, SWT.SMOOTH);
		final GridData gd_progressBar = new GridData(SWT.FILL, SWT.FILL, true, false);
		progressBar.setLayoutData(gd_progressBar);
		sashForm.setWeights(new int[] {379, 98 });
	}
	
	public void setAlgorithmStatus(boolean isRunning, boolean isPaused) {
		if (isRunning) {
			if (!isPaused) {
				algorithmStatusLabel.setForeground(this.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
				algorithmStatusLabel.setText("Running");
			} else {
				algorithmStatusLabel.setForeground(this.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
				algorithmStatusLabel.setText("Paused");
			}
		} else {
			algorithmStatusLabel.setForeground(this.getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
			algorithmStatusLabel.setText("Stopped");
		}
	}
	
	private void advinitGUI() {
		algorithmCanvas.flip();
		
		setAlgorithmStatus(false, false);
	}

	public void setDisplayingRunInfo(boolean shouldDisplay) {
		if (shouldDisplay) {
		} else {
			algorithmCanvas.showNotAvailable(true);
		}
	}
	
	public void addToResults(String key, IValue val) {
		TableItem item = new TableItem(resultProperties, SWT.NONE);
		item.setText(0, key);
		item.setText(1, val.getValueString());
	}
	
	public void clearResults() {
		resultProperties.removeAll();
	}
	
	public void setTimeLabel(String s) {
		runningTimeSlotLabel.setText(s);
	}
	
	public AlgorithmCanvas getCanvas() {
		return algorithmCanvas;
	}
	
	/* IInfoListener methods */

	public void paint(final IPainter painter) {
		if (this.isDisposed()) return;
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (algorithmCanvas.isDisposed()) return;
				painter.paint(algorithmCanvas);
			}
		});
	}

	public void setPercentage(final double percent) {
		if (this.isDisposed()) return;
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (progressBar.isDisposed()) return;
				progressBar.setSelection((int)(percent * 100));
			}
		});
	}

	public void setProperty(final String propkey, final String propval) {
		if (this.isDisposed()) return;
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (runPropsTable.isDisposed()) return;
				if (!runProperties.containsKey(propkey)) {
					TableItem item = new TableItem(runPropsTable, SWT.NONE);
					item.setText(0, propkey);
					item.setText(1, propval);
					
					runProperties.put(propkey, item);
					runPropsItemList.add(item);
				} else {
					runProperties.get(propkey).setText(1, propval);
				}
			}
		});
	}

	public void useCanvas(final boolean shouldUse) {
		if (this.isDisposed()) return;
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (algorithmCanvas.isDisposed()) return;
				if (shouldUse) {
					algorithmCanvas.showNotAvailable(false);
				} else {
					algorithmCanvas.showNotAvailable(true);
				}
			}
		});
	}
	
	public void removeProperty(final String key) {
		if (this.isDisposed()) return;
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (runPropsTable.isDisposed()) return;
				TableItem item = runProperties.get(key);
				
				if (item == null) return;
				
				int pos = runPropsItemList.indexOf(item);
				
				runPropsTable.remove(pos);
				runPropsItemList.remove(pos);
				runProperties.remove(key);
			}
		});
	}

	public void clearProperties() {
		if (this.isDisposed()) return;
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (runPropsTable.isDisposed()) return;
				runProperties.clear();
				runPropsTable.removeAll();
			}
		});
	}

	public void clearConsole() {
		if (this.isDisposed()) return;
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (consoletext.isDisposed()) return;

				consoletext.setText("");
				for (Color c : stcolors) c.dispose();
				stcolors.clear();
				conssize = 0;
			}
		});
	}

	public void print(final String text) {
		if (this.isDisposed()) return;
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (consoletext.isDisposed()) return;

				consoletext.append(text);
				if (stcolors.size() != 0) {
					StyleRange range = new StyleRange();
					range.start = conssize;
					range.length = text.length();
					range.foreground = stcolors.get(stcolors.size() - 1);
					consoletext.setStyleRange(range);
				}
				conssize += text.length();
			}
		});
	}

	public void println(final String text) {
		if (this.isDisposed()) return;
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (consoletext.isDisposed()) return;
				
				consoletext.append(text);
				consoletext.append("\n");
				if (stcolors.size() != 0) {
					StyleRange range = new StyleRange();
					range.start = conssize;
					range.length = text.length() + 1;
					range.foreground = stcolors.get(stcolors.size() - 1);
					consoletext.setStyleRange(range);
				}
				conssize += text.length() + 1;
			}
		});
	}

	public void setConsoleColor(final int r, final int g, final int b) {
		if (this.isDisposed()) return;
		this.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (consoletext.isDisposed()) return;
				
				Color c = new Color(Display.getCurrent(), r, g, b);
				stcolors.add(c);
			}
		});
	}
	
}














