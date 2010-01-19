package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.controller.impl.NullAlgorithm;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.view.editors.algorithm_editor.AlgorithmEditor;

public class LoadParametersToEditor implements ICommand {
	
	private String filename;
	
	public LoadParametersToEditor(String fileName) {
		filename = fileName;
	}

	public CommandResult doCommand(Model model) {
		// create NullAlgorithm object
		IAlgorithm alg = null;
		try {
			alg = NullAlgorithm.class.newInstance();
		} catch (Exception e) {
			Application.logexcept("Could not create algorithm.", e);
			return new CommandResult("Could not create algorithm.");
		}
		
		return new CommandResult(new Events[]{Events.EDITOR_LOADED},
				Events.KEY.EDITOR_CLSNAME, AlgorithmEditor.class.getName(),
				Events.KEY.ALGORITHM_OBJECT, alg,
				Events.KEY.FILE_FOR_LOAD, filename);
	}

	public boolean doesChangeModel() {
		return false;
	}

	public String getName() {
		return "Load parameters";
	}

}














