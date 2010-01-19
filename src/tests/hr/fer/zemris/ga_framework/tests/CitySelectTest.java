package hr.fer.zemris.ga_framework.tests;

import hr.fer.zemris.ga_framework.algorithms.tsp.CitySelectPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CitySelectTest extends Shell {

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			CitySelectTest shell = new CitySelectTest(display, SWT.SHELL_TRIM);
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
	public CitySelectTest(Display display, int style) {
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
		
		new CitySelectPane(this, SWT.NONE);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}














