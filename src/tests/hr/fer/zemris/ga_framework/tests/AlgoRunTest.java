package hr.fer.zemris.ga_framework.tests;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.impl.Controller;
import hr.fer.zemris.ga_framework.view.editors.algorithm_editor.AlgorithmEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class AlgoRunTest extends Shell {


	/* static fields */
	

	/* private fields */
	
	
	
	/* ctors */



	/**
	 * Create the shell
	 * @param display
	 * @param style
	 */
	public AlgoRunTest(Display display, int style) {
		super(display, style);
	}
	
	
	
	
	/* methods */

	/**
	 * Create contents of the window
	 */
	protected void initGUI() {
		setText("SWT Application");
		setSize(500, 375);
		
		this.setLayout(new FillLayout());
		
		new AlgorithmEditor(this, new Controller(), 0, new CommandResult(""));
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void advinitGUI() {
	}
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			AlgoRunTest shell = new AlgoRunTest(display, SWT.SHELL_TRIM);
			shell.initGUI();
			shell.advinitGUI();
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




	
}














