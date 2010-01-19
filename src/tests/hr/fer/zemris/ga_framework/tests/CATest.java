package hr.fer.zemris.ga_framework.tests;

import hr.fer.zemris.ga_framework.view.gadgets.dialogs.about_dialog.BannerComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CATest extends Shell {

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			CATest shell = new CATest(display, SWT.SHELL_TRIM);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* static fields */

	/**
	 * Create the shell
	 * @param display
	 * @param style
	 */
	public CATest(Display display, int style) {
		super(display, style);
		initGUI();
	}

	/**
	 * Create contents of the window
	 */
	protected void initGUI() {
		setText("SWT Application");
		setSize(500, 375);
		setLayout(new FillLayout());
		
		new BannerComposite(this);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}














