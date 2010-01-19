package hr.fer.zemris.ga_framework.controller;


/**
 * Listener of unsuccessful commands.
 * 
 * @author Axel
 *
 */
public interface IUnsuccessfulListener {

	/**
	 * Takes the unsuccessful command result and
	 * does something with it (e.g. displays message).
	 * 
	 * NOTE: !IMPORTANT!
	 * Not all events may be dispatched to the listener
	 * within the same thread. For instance, SWT framework
	 * requires that calls to native widgets are performed
	 * within the UI-thread.
	 * This means that the implementer should insure some
	 * method of transfering these calls from one thread
	 * to another.
	 * For instance, in SWT this is done with Display.asyncExec.
	 * 
	 * @param result
	 */
	public void onUnsuccessfulCommand(CommandResult result);
	
}














