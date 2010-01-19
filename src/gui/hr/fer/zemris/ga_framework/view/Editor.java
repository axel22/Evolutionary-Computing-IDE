package hr.fer.zemris.ga_framework.view;

import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.IController;
import hr.fer.zemris.ga_framework.controller.IEditor;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


/**
 * Performs tasks common to all editors.
 * 
 * Classes having this class as base class shall have
 * a ctor with parameters:
 * - Composite - for parent
 * - IController - controller object
 * - long - the id of the editor
 * - CommandResult
 * 
 * The command result typically contains
 * useful information about the editor, and only the
 * editor knows how to extract it and use it, thus,
 * this method exists.
 * 
 * @author Axel
 *
 */
public abstract class Editor extends Composite implements IEditor {
	
	/**
	 * Observer invoked when some event occurs in the editor.
	 * @author Axel
	 *
	 */
	public interface IObserver {
		void onAction();
	}
	
	/* static fields */
	
	/* private fields */
	protected IController ctrl;
	protected long id;
	protected List<IObserver> observerlist;

	/* ctors */
	
	public Editor(Composite c, int style, IController controller, long elemid, Events[] evtypes) {
		super(c, style);
		
		ctrl = controller;
		id = elemid;
		observerlist = new ArrayList<IObserver>();
		
		ctrl.registerEditor(evtypes, this);
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				ctrl.unregisterEditor(Editor.this);
			}
		});
	}

	/* methods */

	@Override
	protected void checkSubclass() {
		// disable subclass prevention
	}
	
	public long getId() {
		return id;
	}
	
	public void addObserver(IObserver obs) {
		observerlist.add(obs);
	}
	
	public boolean removeObserver(IObserver obs) {
		return observerlist.remove(obs);
	}
	
	protected void informObservers() {
		for (IObserver al : observerlist) {
			al.onAction();
		}
	}
	
	/**
	 * Returns an image used for the representation
	 * of the editor. Typically, this should be a reference
	 * to a static object shared by all instances of
	 * the implementing class.
	 * 
	 * @return
	 */
	public abstract Image getImage(Display d);
	
	/**
	 * Used to retrieve the file types this editor can save to.
	 * 
	 * @return
	 * A map of key-value pairs, where key is the extension of the
	 * file type, and value is the name of the file type.
	 * Values should not contain three dots ("...") at the end.
	 */
	public abstract Map<String, String> getSaveTypes();
	
	/**
	 * Saves the file.
	 * 
	 * @param extension
	 * Extension of the file type without a dot. Extension
	 * must be a key of some entry from the map returned by
	 * <code>getSaveTypes</code> method.
	 * @param fullpathandfilename
	 * Full path and filename for the file to be saved.
	 * @throws RuntimeException
	 * If the saving process went wrong somehow.
	 * @throws IllegalArgumentException
	 * If the extension is not supported (not included in
	 * the extension map returned by <code>getSaveTypes</code>).
	 */
	public abstract void save(String extension, OutputStream os);
	
	/**
	 * This is the extension the editor uses for loading.
	 * 
	 * @return
	 * The load extension (without dot), or null if the editor
	 * cannot load it's state.
	 */
	public abstract String getLoadExtension();
	
	/**
	 * Whether or not the editor can load it's serialized state.
	 * If it can't, it mustn't have the load extension.
	 * 
	 * @return
	 */
	public abstract boolean isLoadable();
	
	/**
	 * Loads the file from the stream.
	 * 
	 * @throws UnsupportedOperationException
	 * If the editor is not loadable.
	 * @throws IllegalArgumentException
	 * If the filecontent is corrupted.
	 */
	public abstract void load(InputStream is);
	
}














