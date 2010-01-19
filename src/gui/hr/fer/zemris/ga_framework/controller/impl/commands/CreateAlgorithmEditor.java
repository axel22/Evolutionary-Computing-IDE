package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.view.editors.algorithm_editor.AlgorithmEditor;
import hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.dialogs.ParameterDialogFactory;
import hr.fer.zemris.ga_framework.view.gadgets.value_renderer.ValueRendererFactory;




public class CreateAlgorithmEditor implements ICommand {
	
	/* static fields */

	/* private fields */
	private Class<?> algoclass;
	private Long id;

	/* ctors */
	
	/**
	 * Creates an editor for the algorithm.
	 * 
	 * @param cls
	 * The class object of the algorithm.
	 */
	public CreateAlgorithmEditor(Class<?> cls) {
		if (!IAlgorithm.class.isAssignableFrom(cls)) throw new
			IllegalArgumentException("Can only create editors for algorithms!");
		algoclass = cls;
		id = null;
	}
	
	/**
	 * Creates an editor for the algorithm.
	 * 
	 * @param cls
	 * The class object of the algorithm.
	 */
	public CreateAlgorithmEditor(Class<?> cls, Long proposedId) {
		if (!IAlgorithm.class.isAssignableFrom(cls)) throw new
			IllegalArgumentException("Can only create editors for algorithms!");
		algoclass = cls;
		id = proposedId;
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
		
		// register all used parameter dialogs and renderers with factory
		if (alg.getEditors() != null) ParameterDialogFactory.registerDialogs(alg.getEditors());
		if (alg.getRenderers() != null) ValueRendererFactory.registerRenderers(alg.getRenderers());
		
		return new CommandResult(new Events[]{Events.EDITOR_CREATED},
				Events.KEY.EDITOR_CLSNAME, AlgorithmEditor.class.getName(),
				Events.KEY.ALGORITHM_OBJECT, alg,
				Events.KEY.EDITOR_ID, id);
	}

	public boolean doesChangeModel() {
		return false;
	}

	public String getName() {
		return "Create Algorithm Editor";
	}
	

}














