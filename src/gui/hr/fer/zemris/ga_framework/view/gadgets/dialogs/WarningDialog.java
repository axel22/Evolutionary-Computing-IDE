package hr.fer.zemris.ga_framework.view.gadgets.dialogs;

import hr.fer.zemris.ga_framework.view.ImageLoader;

import org.eclipse.swt.SWT;
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

public class WarningDialog extends Dialog {
	
	private Label label;
	private static final ImageData IMAGE_ICON = ImageLoader.loadImage("icons", "warning.png");

	protected boolean result;
	protected Shell shell;

	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public WarningDialog(Shell parent, int style) {
		super(parent, style);
		
		result = false;
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public WarningDialog(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public boolean open(String title, String question) {
		initGUI();
		advinitGUI(title, question);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	private void advinitGUI(String title, String question) {
		shell.setImage(new Image(shell.getDisplay(), IMAGE_ICON));
		shell.setText(title);
		label.setText(question);
		
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
		shell.setSize(365, 130);
		shell.setText("SWT Dialog");

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 0;
		composite.setLayout(gridLayout_1);

		label = new Label(composite, SWT.WRAP);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

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














