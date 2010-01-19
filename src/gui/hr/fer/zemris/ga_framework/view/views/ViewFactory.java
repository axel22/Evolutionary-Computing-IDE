package hr.fer.zemris.ga_framework.view.views;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.swt.widgets.Composite;



/**
 * When creating a new view in this package,
 * info must be added here, so the view can
 * be created later.
 * 
 * @author Axel
 *
 */
public class ViewFactory {
	
	private static Map<String, String> views = new TreeMap<String, String>();
	private static Map<String, String> ro_views = Collections.unmodifiableMap(views);
	
	static {
		// read all view class names from a configuration file
		InputStream is = ViewFactory.class.getResourceAsStream("ViewFactory.conf");
		Properties props = new Properties();
		
		if (is == null) {
			Application.logerror("Could not find ViewFactory.conf.", "");
		} else {
			try {
				props.load(is);
			} catch (IOException e) {
				Application.logexcept("List of views not loaded.", e);
			}
			
			for (Entry<Object, Object> ntry : props.entrySet()) {
				views.put((String)ntry.getKey(), (String)ntry.getValue());
			}
		}
	}
	
	/**
	 * @return
	 * An unmodifiable view to the map of 
	 * class names of registered views, and their
	 * respective names (names may not be unique,
	 * class names are).
	 */
	public synchronized static Map<String, String> getAllViews() {
		return ro_views;
	}
	
	/**
	 * Creates a view.
	 * 
	 * @param clsname
	 * @param c
	 * @param ctrl
	 * @param id
	 * @return
	 * Returns null if the view class name is not registered.
	 */
	public synchronized static View createView(String clsname, Composite c, IController ctrl, long id) {
		// lookup view
		if (!views.containsKey(clsname)) return null;
		
		View view = null;
		
		// create view
		try {
			Class<?> cls = Class.forName(clsname);
			Constructor<?> ctor = cls.getConstructor(new Class<?>[]{ 
					Composite.class, IController.class, long.class });
			view = (View)ctor.newInstance(new Object[]{c, ctrl, id});
		} catch (Exception e) {
			Application.logexcept("Cannot create view.", e);
			return null;
		}
		
		return view;
	}
	
	
}














