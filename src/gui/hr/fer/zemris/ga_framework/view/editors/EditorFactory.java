package hr.fer.zemris.ga_framework.view.editors;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.view.Editor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.swt.widgets.Composite;




/**
 * Factory used for creating editors.
 * 
 * @author Axel
 *
 */
public class EditorFactory {

	private static Map<String, String> editors = new HashMap<String, String>();
	private static Map<String, String> ro_editors = Collections.unmodifiableMap(editors);

	
	static {
		// read all view class names from a configuration file
		InputStream is = EditorFactory.class.getResourceAsStream("EditorFactory.conf");
		Properties props = new Properties();
		
		if (is == null) {
			Application.logerror("Could not find EditorFactory.conf.", "");
		} else {
			try {
				props.load(is);
			} catch (IOException e) {
				Application.logexcept("List of editors not loaded.", e);
			}
			
			for (Entry<Object, Object> ntry : props.entrySet()) {
				editors.put((String)ntry.getKey(), (String)ntry.getValue());
			}
		}
	}
	
	/**
	 * @return
	 * An unmodifiable view to the map of 
	 * class names of registered editors, and their
	 * respective names (names may not be unique,
	 * class names are).
	 */
	public synchronized static Map<String, String> getAllEditors() {
		return ro_editors;
	}
	
	/**
	 * Creates an editor.
	 * 
	 * @param clsname
	 * @param c
	 * @param ctrl
	 * @param id
	 * @return
	 * Returns null if the editor class name is not registered.
	 */
	public synchronized static Editor createEditor(String clsname, Composite c, IController ctrl, long id, CommandResult res) {
		// lookup view
		if (!editors.containsKey(clsname)) return null;
		
		Editor ed = null;
		
		// create view
		try {
			Class<?> cls = Class.forName(clsname);
			Constructor<?> ctor = cls.getConstructor(new Class<?>[]{ 
					Composite.class, IController.class, long.class, CommandResult.class});
			ed = (Editor)ctor.newInstance(new Object[]{c, ctrl, id, res});
		} catch (Exception e) {
			Application.logexcept("Cannot create view.", e);
			return null;
		}
		
		return ed;
	}

}














