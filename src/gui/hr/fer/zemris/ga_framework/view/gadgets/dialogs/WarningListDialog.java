package hr.fer.zemris.ga_framework.view.gadgets.dialogs;

import hr.fer.zemris.ga_framework.view.ImageLoader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class WarningListDialog extends Dialog {
	
	private Table table;
	private Label label;
	private static final ImageData IMAGE_ICON = ImageLoader.loadImage("icons", "warning.png");

	protected boolean result;
	protected Shell shell;

	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public WarningListDialog(Shell parent, int style) {
		super(parent, style);
		
		result = false;
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public WarningListDialog(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public boolean open(String title, String question, String[] items, ImageData imagedata) {
		initGUI();
		advinitGUI(title, question, items, imagedata);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	private void advinitGUI(String title, String question, String[] items, ImageData imagedata) {
		shell.setImage(new Image(shell.getDisplay(), IMAGE_ICON));
		shell.setText(title);
		label.setText(question);
		
		// fill with items
		for (String s : items) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(s);
			item.setImage(new Image(shell.getDisplay(), imagedata));
		}
		
		// set position
		int x = getParent().getLocation().x + (getParent().getSize().x - shell.getSize().x) / 2;
		int y = getParent().getLocation().y + (getParent().getSize().y - shell.getSize().y) / 2;
		shell.setLocation(x, y);
	}
	
	/**
	 * Create contents of the dialog
	 */
	protected void initGUI() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setLayout(new GridLayout());
		shell.setSize(467, 245);
		shell.setText("SWT Dialog");

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 0;
		composite.setLayout(gridLayout_1);

		label = new Label(composite, SWT.WRAP);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label label_1 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final ViewForm viewForm = new ViewForm(composite, SWT.FLAT | SWT.BORDER);
		viewForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		table = new Table(viewForm, SWT.HIDE_SELECTION | SWT.FULL_SELECTION);
		table.addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent arg0) {
				int wdt = table.getSize().x - table.getBorderWidth() * 2;
				table.getColumn(0).setWidth(wdt);
			}
		});
		viewForm.setContent(table);

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(100);
		newColumnTableColumn.setText("New column");

		final Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite_1.setLayout(gridLayout);

		final Button okButton = new Button(composite_1, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				result = true;
				shell.dispose();
			}
		});
		okButton.setText("OK");

		if ((getStyle() & SWT.CANCEL) != 0) {
			final Button cancelButton = new Button(composite_1, SWT.NONE);
			cancelButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(final SelectionEvent arg0) {
					result = false;
					shell.dispose();
				}
			});
			cancelButton.setText("Cancel");
		}
	}

}














