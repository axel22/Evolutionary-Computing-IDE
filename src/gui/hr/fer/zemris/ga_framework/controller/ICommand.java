package hr.fer.zemris.ga_framework.controller;

import hr.fer.zemris.ga_framework.model.Model;



/**
 * Defines objects that perform commands on the model.
 * 
 * IMPORTANT:
 * The same command may be sent to the controller at
 * most once.
 * 
 * @author Axel
 *
 */
public interface ICommand {

	/**
	 * States whether the command does some
	 * change on the model.
	 * If it does, undo stack will be modified
	 * accordingly.
	 * 
	 * @return
	 * True if this command object changes the model.
	 * False if it does not change the model in any way.
	 */
	boolean doesChangeModel();
	
	/**
	 * @return
	 * Returns a simple name of the command,
	 * name which may not be unique at all.
	 * This is used mainly for GUI.
	 */
	String getName();
	
	/**
	 * Performs an operation on the model.
	 * May only be invoked once on a model,
	 * unless <code>undoCommand</code> is
	 * performed before. After that, it may
	 * be invoked again in the same manner (once).
	 * If the operation is not successfully
	 * performed, no changes must be done on
	 * the model - it must be left in exactly
	 * the same state it has been before
	 * invocation.
	 * 
	 * @param model
	 * @return
	 * An object specifying the success of
	 * the operation.
	 */
	CommandResult doCommand(Model model);
	
}














