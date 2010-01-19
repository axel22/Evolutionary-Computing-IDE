package hr.fer.zemris.ga_framework.view.gadgets.dialogs;

import hr.fer.zemris.ga_framework.view.ImageLoader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ErrorDialog extends Dialog {

	private static final ImageData IMAGE_ICON = ImageLoader.loadImage("icons", "exclamation.png");
	
	private Text exceptionText;
	private Label errorTextLabel;
	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public ErrorDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public ErrorDialog(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public Object open(String text, Exception e) {
		initGUI();
		advinitGUI(text, e);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}
	
	private void advinitGUI(String text, final Exception e) {
		// set images
		shell.setImage(new Image(shell.getDisplay(), IMAGE_ICON));
		
		// set texts
		errorTextLabel.setText(text);
		setExceptionText(e);
		
		// set position
		int x = getParent().getLocation().x + (getParent().getSize().x - shell.getSize().x) / 2;
		int y = getParent().getLocation().y + (getParent().getSize().y - shell.getSize().y) / 2;
		shell.setLocation(x, y);
	}

	private void setExceptionText(Exception e) {
		if (e != null) {
			StringBuilder sb = new StringBuilder();
			recursivelyBuildExceptionText(e, sb);
			exceptionText.setText(sb.toString());
		} else {
			exceptionText.setText("");
		}
	}

	private void recursivelyBuildExceptionText(Throwable e, StringBuilder sb) {
		sb.append(e.getClass().getSimpleName());
		sb.append(": ");
		String msg = e.getMessage();
		if (msg != null) sb.append(msg);
		else sb.append("(no exception message)");
		for (StackTraceElement elem : e.getStackTrace()) {
			sb.append('\n').append(elem.toString());
		}
		
		if (e.getCause() != null) {
			sb.append("\nCaused by: ");
			recursivelyBuildExceptionText(e.getCause(), sb);
		}
	}

	/**
	 * Create contents of the dialog
	 */
	protected void initGUI() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.verticalSpacing = 15;
		shell.setLayout(gridLayout_1);
		shell.setSize(460, 296);
		shell.setText("Error");

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		errorTextLabel = new Label(composite, SWT.WRAP);

		final ViewForm viewForm = new ViewForm(shell, SWT.FLAT | SWT.BORDER);
		viewForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		exceptionText = new Text(viewForm, SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		exceptionText.setEditable(false);
		exceptionText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		viewForm.setContent(exceptionText);

		final Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayout(new FillLayout());
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		final Button okButton = new Button(composite_1, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0) {
				shell.dispose();
			}
		});
		okButton.setText("OK");
	}

}














