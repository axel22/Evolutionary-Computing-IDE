package hr.fer.zemris.ga_framework.view.gadgets.value_renderer;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.IValueRenderer;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;



public class ValueRendererFactory {
	
	private static HashMap<String, Class<?>> renderers = new HashMap<String, Class<?>>();
	
	static {
		try {
			Properties pr = new Properties();
			pr.load(ValueRendererFactory.class.getResourceAsStream("ValueRendererFactory.conf"));
			for (Entry<Object, Object> ntr : pr.entrySet()) {
				renderers.put((String) ntr.getKey(), Class.forName((String) ntr.getValue()));
			}
		} catch (Exception e) {
			Application.logexcept("Could not load value renderer factory property file.", e);
		}
	}
	
	
	/**
	 * Registers a renderer to a class type.
	 * Note that the renderer must also be a SWT Composite,
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
	 * to a DIFFERENT renderer class.
	 */
	public synchronized static void registerRenderer(Class<? extends ISerializable> valueclass, Class<? extends IValueRenderer> rendererclass) {
		if (valueclass == null || rendererclass == null) throw new NullPointerException("Class cannot be null.");
		Class<?> dcls = renderers.get(valueclass.getName());
		if (dcls != null) {
			if (!dcls.equals(rendererclass)) 
				throw new IllegalArgumentException("Class already contained in parameter dialog " +
						"factory, but for different type.");
		}
		if (!Composite.class.isAssignableFrom(rendererclass)) 
			throw new IllegalArgumentException("Not a SWT Composite: " + rendererclass.getName());
		renderers.put(valueclass.getName(), rendererclass);
	}
	
	
	/**
	 * Creates a renderer.
	 * Value renderer shall be a SWT Composite, this is guaranteed.
	 * 
	 * @param valcls
	 * @param c
	 * @return
	 * Null may be returned if there is no such renderer.
	 */
	public synchronized static IValueRenderer createRenderer(Class<? extends ISerializable> valcls, Composite c) {
		String valclsname = valcls.getName();
		Class<?> dcls = renderers.get(valclsname);
		
		if (dcls == null) return null;
		
		IValueRenderer dialog = null;
		try {
			Constructor<?> ctor = dcls.getConstructor(new Class<?>[]{Composite.class, int.class});
			dialog = (IValueRenderer)ctor.newInstance(new Object[]{c, SWT.NONE});
		} catch (Exception e) {
			Application.logexcept("Could not create parameter dialog.", e);
		}
		
		return dialog;
	}


	public static void registerRenderers(Map<Class<? extends ISerializable>, Class<? extends IValueRenderer>> editors) {
		if (editors != null) {
			for (Entry<Class<? extends ISerializable>, Class<? extends IValueRenderer>> ntr : editors.entrySet()) {
				registerRenderer(ntr.getKey(), ntr.getValue());
			}
		}
	}
	
}














