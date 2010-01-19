package hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler;

import hr.fer.zemris.ga_framework.view.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class GnuplotOutputSelector extends Dialog {

	/* private fields */
	private Button clearAllButton;
	private Button removeButton;
	private Combo retvalcombo;
	private Combo inputvalcombo;
	private Text filenameText;
	private Table table;
	protected Object[] result;
	protected Shell shell;
	
	/* static fields */
	private static final ImageData ICON_PAGE_WHITE = ImageLoader.loadImage("icons", "page_white_pencil.png");

	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public GnuplotOutputSelector(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public GnuplotOutputSelector(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public Object[] open(List<String> inputvals, List<String> retvals, List<String[]> oldcolumns, String filename) {
		initGUI();
		advinitGUI(inputvals, retvals, oldcolumns, filename);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		return result;
	}
	
	protected void advinitGUI(List<String> inputvals, List<String> retvals, List<String[]> oldcolumns, String filename) {
		// images
		shell.setImage(new Image(Display.getCurrent(), ICON_PAGE_WHITE));
		
		// fill combos
		for (String s : inputvals) {
			inputvalcombo.add(s);
		}
		for (String s : retvals) {
			retvalcombo.add(s);
		}
		
		// input old state of column list if provided
		if (oldcolumns != null) for (String[] sf : oldcolumns) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(sf[0]);
			if (sf[1] != null) item.setData(true);
		}
		
		// input filename
		if (filename != null) filenameText.setText(filename);
		
		// position yerself
		Point parloc = getParent().getLocation();
		Point parsz = getParent().getSize();
		shell.setLocation(parloc.x + (parsz.x - shell.getSize().x) / 2, parloc.y + (parsz.y - shell.getSize().y) / 2);
	}

	/**
	 * Create contents of the dialog
	 */
	protected void initGUI() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		gridLayout.horizontalSpacing = 2;
		shell.setLayout(gridLayout);
		shell.setSize(439, 375);
		shell.setText("Gnuplot output selector");

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.verticalSpacing = 2;
		gridLayout_3.marginWidth = 2;
		gridLayout_3.marginHeight = 2;
		gridLayout_3.horizontalSpacing = 2;
		gridLayout_3.numColumns = 2;
		composite.setLayout(gridLayout_3);

		final Label fileNameLabel = new Label(composite, SWT.NONE);
		fileNameLabel.setText("File name:");

		final Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_4 = new GridLayout();
		gridLayout_4.numColumns = 2;
		gridLayout_4.verticalSpacing = 0;
		gridLayout_4.marginWidth = 0;
		gridLayout_4.marginHeight = 0;
		gridLayout_4.horizontalSpacing = 2;
		composite_3.setLayout(gridLayout_4);

		filenameText = new Text(composite_3, SWT.BORDER);
		final GridData gd_filenameText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		filenameText.setLayoutData(gd_filenameText);

		final Button button = new Button(composite_3, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				FileDialog fd = new FileDialog(shell);
				fd.setText("Select gnuplot output file");
				fd.setOverwrite(true);
				String fname = fd.open();
				if (fname != null) {
					filenameText.setText(fname);
				}
			}
		});
		button.setLayoutData(new GridData());
		button.setText("...");

		final Label inputParametersLabel = new Label(composite, SWT.NONE);
		inputParametersLabel.setText("Input values:");

		final Composite composite_4 = new Composite(composite, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout gridLayout_5 = new GridLayout();
		gridLayout_5.numColumns = 2;
		gridLayout_5.verticalSpacing = 0;
		gridLayout_5.marginWidth = 0;
		gridLayout_5.marginHeight = 0;
		gridLayout_5.horizontalSpacing = 4;
		composite_4.setLayout(gridLayout_5);

		inputvalcombo = new Combo(composite_4, SWT.NONE);
		final GridData gd_inputvalcombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		inputvalcombo.setLayoutData(gd_inputvalcombo);

		final Button addButton = new Button(composite_4, SWT.NONE);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				int selind = inputvalcombo.getSelectionIndex();
				if (selind == -1) return;
				String s = inputvalcombo.getItem(selind);
				
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(s);
			}
		});
		addButton.setText("Add");

		final Label returnValuesLabel = new Label(composite, SWT.NONE);
		returnValuesLabel.setText("Return values:");

		final Composite composite_5 = new Composite(composite, SWT.NONE);
		composite_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout gridLayout_6 = new GridLayout();
		gridLayout_6.numColumns = 2;
		gridLayout_6.verticalSpacing = 0;
		gridLayout_6.marginWidth = 0;
		gridLayout_6.marginHeight = 0;
		gridLayout_6.horizontalSpacing = 4;
		composite_5.setLayout(gridLayout_6);

		retvalcombo = new Combo(composite_5, SWT.NONE);
		final GridData gd_retvalcombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		retvalcombo.setLayoutData(gd_retvalcombo);

		final Button addButton_1 = new Button(composite_5, SWT.NONE);
		addButton_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				int selind = retvalcombo.getSelectionIndex();
				if (selind == -1) return;
				String s = retvalcombo.getItem(selind);
				
				TableItem item = new TableItem(table, SWT.NONE);
				item.setData(true);
				item.setText(s);
			}
		});
		addButton_1.setText("Add");

		final Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		gridLayout_2.verticalSpacing = 2;
		gridLayout_2.marginWidth = 2;
		gridLayout_2.marginHeight = 2;
		gridLayout_2.horizontalSpacing = 2;
		composite_1.setLayout(gridLayout_2);

		table = new Table(composite_1, SWT.BORDER);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				int selind = table.getSelectionIndex();
				if (selind == -1) {
					removeButton.setEnabled(false);
				} else {
					removeButton.setEnabled(true);
				}
			}
		});
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(320);
		newColumnTableColumn.setText("Gnuplot file column");

		final Composite composite_6 = new Composite(composite_1, SWT.NONE);
		final GridData gd_composite_6 = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gd_composite_6.widthHint = 98;
		composite_6.setLayoutData(gd_composite_6);
		final GridLayout gridLayout_7 = new GridLayout();
		gridLayout_7.verticalSpacing = 2;
		gridLayout_7.marginWidth = 2;
		gridLayout_7.marginHeight = 2;
		gridLayout_7.horizontalSpacing = 2;
		composite_6.setLayout(gridLayout_7);

		removeButton = new Button(composite_6, SWT.NONE);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				int selind = table.getSelectionIndex();
				if (selind == -1) return;
				
				List<String[]> cols = extractColumns();
				table.removeAll();
				cols.remove(selind);
				
				for (String[] sf : cols) {
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(sf[0]);
					if (sf[1] != null) item.setData(true);
				}
			}
		});
		removeButton.setEnabled(false);
		removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		removeButton.setText("Remove");

		clearAllButton = new Button(composite_6, SWT.NONE);
		clearAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				table.removeAll();
			}
		});
		clearAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		clearAllButton.setText("Clear all");

		final Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.marginHeight = 2;
		gridLayout_1.verticalSpacing = 2;
		gridLayout_1.marginWidth = 2;
		gridLayout_1.horizontalSpacing = 2;
		gridLayout_1.numColumns = 2;
		composite_2.setLayout(gridLayout_1);

		final Button okButton = new Button(composite_2, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				String fn = filenameText.getText().trim();
				if (fn.equals("")) {
					MessageBox box = new MessageBox(GnuplotOutputSelector.this.shell, SWT.ICON_WARNING | SWT.OK);
					box.setText("Warning");
					box.setMessage("Must specify file name.");
					box.open();
					return;
				}
				Object o2 = extractColumns();
				result = new Object[]{fn,o2};
				shell.dispose();
			}
		});
		okButton.setText("OK");

		final Button cancelButton = new Button(composite_2, SWT.NONE);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				result = null;
				shell.dispose();
			}
		});
		cancelButton.setText("Cancel");
		//
	}
	
	/* methods */
	
	private List<String[]> extractColumns() {
		List<String[]> lst = new ArrayList<String[]>();
		
		for (TableItem ti : table.getItems()) {
			if (ti.getData() != null) {
				// return value
				lst.add(new String[]{ti.getText(), ""});
			} else {
				// input value
				lst.add(new String[]{ti.getText(), null});
			}
		}
		
		return lst;
	}

}














