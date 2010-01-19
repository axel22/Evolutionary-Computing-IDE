package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.view.editors.info_viewer.InfoViewer;




public class CreateInfoViewer implements ICommand {
	
	/* static fields */

	/* private fields */
	private Class<?> algoclass;

	/* ctors */
	
	/**
	 * Creates a scheduler for the algorithm.
	 * 
	 * @param cls
	 * The class object of the algorithm.
	 */
	public CreateInfoViewer(Class<?> cls) {
		if (!IAlgorithm.class.isAssignableFrom(cls)) throw new
			IllegalArgumentException("Can only create schedulers for algorithms!");
		algoclass = cls;
	}

	/* methods */
	
	public CommandResult doCommand(Model model) {
		// create IAlgorithm object
		IAlgorithm alg = null;
		try {
			alg = (IAlgorithm)algoclass.newInstance();
		} catch (Exception e) {
			Application.logexcept("Could not create algorithm.", e);
			return new CommandResult("Could not create algorithm.");
		}
		
		return new CommandResult(new Events[]{Events.EDITOR_CREATED},
				Events.KEY.EDITOR_CLSNAME, InfoViewer.class.getName(),
				Events.KEY.ALGORITHM_OBJECT, alg);
	}

	public boolean doesChangeModel() {
		return false;
	}

	public String getName() {
		return "Create Algorithm Scheduler";
	}

}














