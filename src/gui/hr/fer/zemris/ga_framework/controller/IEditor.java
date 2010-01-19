package hr.fer.zemris.ga_framework.controller;

import hr.fer.zemris.ga_framework.model.IInfoListener;



/**
 * All classes representing a GUI editor (view)
 * must implement this interface.
 * 
 * @author Axel
 *
 */
public interface IEditor extends IListener {
	
	/**
	 * @return
	 * Returns a name for the editor, name which
	 * may not be unique nor always same.
	 */
	public String getEditorName();
	
	/**
	 * @return
	 * Returns a unique id of this editor.
	 */
	public long getId();
	
	/**
	 * Denotes whether the editor can currently
	 * perform an undo of the last operation
	 * performed within the editor.
	 * 
	 * @return
	 */
	public boolean canUndo();
	
	/**
	 * Denotes whether the editor can currently
	 * perform a redo of the last undone operation.
	 * 
	 * @return
	 */
	public boolean canRedo();
	
	/**
	 * Performs undo within the editor.
	 * 
	 * @throws IllegalStateException
	 * If undo cannot be performed.
	 */
	public void undo();
	
	/**
	 * Redoes last undone operation.
	 * 
	 * @throws IllegalStateException
	 * If undo cannot be performed. 
	 */
	public void redo();
	
	/**
	 * Returns an info listener interface, if any.
	 * 
	 * @return
	 * Null if this editor cannot provide an info listener.
	 */
	public IInfoListener getInfoListener();
	
}














