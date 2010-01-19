package hr.fer.zemris.ga_framework.tests;

import hr.fer.zemris.ga_framework.algorithms.tsp.CityTable;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.model.impl.Value;
import hr.fer.zemris.ga_framework.model.impl.parameters.PrimitiveParameter;
import hr.fer.zemris.ga_framework.model.impl.parameters.SerializableParameter;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.ParameterEditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ParameterTest extends Shell {


	/* static fields */
	

	/* private fields */
	
	
	
	/* ctors */


	private ParameterEditor ed;

	/**
	 * Create the shell
	 * @param display
	 * @param style
	 */
	public ParameterTest(Display display, int style) {
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
		
		ed = new ParameterEditor(this, SWT.NONE);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void advinitGUI() {
		List<IParameter> params = new ArrayList<IParameter>();
		
		IParameter par = new PrimitiveParameter("Broj", "Obican broj.", ParameterTypes.INTEGER);
		params.add(par);
		
		par = new PrimitiveParameter("Tekst", "Neki tekst.", ParameterTypes.STRING);
//		params.add(par);
		
		par = new PrimitiveParameter("Vrijeme", "Vrijeme leti.", ParameterTypes.TIME);
//		params.add(par);
		
		par = new PrimitiveParameter("Istina", "Relativna je.", ParameterTypes.BOOLEAN);
//		params.add(par);
		
		List<Object> allowed = new ArrayList<Object>();
		allowed.add(Math.PI);
		allowed.add(Math.E);
		par = new PrimitiveParameter("Opaki brojevi", "Opaki su.", ParameterTypes.REAL, allowed);
//		params.add(par);
		
		par = new SerializableParameter("Serijabilni", "Ovo postaje dosadno.", CityTable.class);
		params.add(par);
		
		Map<String, IValue> mp = new HashMap<String, IValue>();
		mp.put("Broj", new Value(7, params.get(0)));
		mp.put("Serijabilni", new Value(new CityTable(1, 100, 100), params.get(1)));
		ed.setParameters(params, mp);
	}
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			ParameterTest shell = new ParameterTest(display, SWT.SHELL_TRIM);
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














