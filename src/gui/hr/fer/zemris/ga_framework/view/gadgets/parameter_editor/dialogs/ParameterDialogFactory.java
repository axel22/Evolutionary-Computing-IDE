package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.dialogs;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.model.IParameterDialog;
import hr.fer.zemris.ga_framework.model.ISerializable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ParameterDialogFactory {

	
	private static HashMap<String, Class<?>> dialogs = new HashMap<String, Class<?>>();
	
	static {
		try {
			Properties pr = new Properties();
			pr.load(ParameterDialogFactory.class.getResourceAsStream("ParameterDialogFactory.conf"));
			for (Entry<Object, Object> ntr : pr.entrySet()) {
				dialogs.put((String) ntr.getKey(), Class.forName((String) ntr.getValue()));
			}
		} catch (Exception e) {
			Application.logexcept("Could not load parameter dialog factory property file.", e);
		}
	}
	
	
	/**
	 * Registers a dialog to a class type.
	 * Note that dialog must also be a SWT Composite,
	 * otherwise, mistakes will happen later on.
	 * Also note that adding a dialog for an already
	 * registered class will throw an exception if the
	 * new dialog is different than the old one.
	 * However, if new dialog is same as the old one,
	 * nothing will happen.
	 * 
	 * @param valueclass
	 * @param dialogclass
	 * @param IllegalArgumentException
	 * If class is already registered and registered
	 * to a DIFFERENT dialog class.
	 */
	public synchronized static void registerDialog(Class<? extends ISerializable> valueclass, Class<? extends IParameterDialog> dialogclass) {
		if (valueclass == null || dialogclass == null) throw new NullPointerException("Class cannot be null.");
		Class<?> dcls = dialogs.get(valueclass.getName());
		if (dcls != null) {
			if (!dcls.equals(dialogclass)) 
				throw new IllegalArgumentException("Class already contained in parameter dialog " +
						"factory, but for different type.");
		}
		if (!Composite.class.isAssignableFrom(dialogclass)) 
			throw new IllegalArgumentException("Not a SWT Composite: " + dialogclass.getName());
		dialogs.put(valueclass.getName(), dialogclass);
	}
	
	
	/**
	 * Creates a parameter dialog.
	 * Parameter dialog shall be a SWT Composite, this is guaranteed.
	 * 
	 * @param valcls
	 * @param c
	 * @return
	 * Null may be returned if there is no such dialog.
	 */
	public synchronized static IParameterDialog createDialog(Class<? extends ISerializable> valcls, Composite c) {
		String valclsname = valcls.getName();
		Class<?> dcls = dialogs.get(valclsname);
		
		if (dcls == null) return null;
		
		IParameterDialog dialog = null;
		try {
			Constructor<?> ctor = dcls.getConstructor(new Class<?>[]{Composite.class, int.class});
			dialog = (IParameterDialog)ctor.newInstance(new Object[]{c, SWT.NONE});
		} catch (Exception e) {
			Application.logexcept("Could not create parameter dialog.", e);
		}
		
		return dialog;
	}


	public static void registerDialogs(Map<Class<? extends ISerializable>, Class<? extends IParameterDialog>> editors) {
		if (editors != null) {
			for (Entry<Class<? extends ISerializable>, Class<? extends IParameterDialog>> ntr : editors.entrySet()) {
				registerDialog(ntr.getKey(), ntr.getValue());
			}
		}
	}
	

}














