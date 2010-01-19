package hr.fer.zemris.ga_framework.controller;





public interface IListener {
	
	/**
	 * Method called when the event of a given
	 * type occurs.
	 * 
	 * NOTE: If commands are issued to
	 * the controller while performing this
	 * method, a feedback loop can occur.
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
	 * @param evtype
	 * @param messages
	 */
	public void onEvent(Events evtype, CommandResult messages);
	
}
















