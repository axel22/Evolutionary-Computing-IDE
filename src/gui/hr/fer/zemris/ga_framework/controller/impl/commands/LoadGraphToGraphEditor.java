package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.Application;
import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.controller.impl.NullAlgorithm;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IGraph;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.view.editors.graph_editor.GraphEditor;

import java.util.HashMap;
import java.util.Map;

public class LoadGraphToGraphEditor implements ICommand {
	
	private String filename;
	
	public LoadGraphToGraphEditor(String fileName) {
		filename = fileName;
	}

	public CommandResult doCommand(Model model) {
		// create NullAlgorithm object
		IAlgorithm alg = null;
		IGraph graph = new IGraph() {
			public String getAbscissaName() {
				return "";
			}
			public Map<String, Object> getCurveFamily() {
				return new HashMap<String, Object>();
			}
			public String getDescription() {
				return "";
			}
			public String getGraphName() {
				return "";
			}
			public String getOrdinateName() {
				return "";
			}
		};
		try {
			alg = NullAlgorithm.class.newInstance();
		} catch (Exception e) {
			Application.logexcept("Could not create algorithm.", e);
			return new CommandResult("Could not create algorithm.");
		}
		
		Map<String, Object> messages = new HashMap<String, Object>();
		messages.put(Events.KEY.EDITOR_CLSNAME, GraphEditor.class.getName());
		messages.put(Events.KEY.ALGORITHM_OBJECT, alg);
		messages.put(Events.KEY.FILE_FOR_LOAD, filename);
		messages.put(Events.KEY.GRAPH_OBJECT, graph);
		
		return new CommandResult(new Events[]{Events.EDITOR_LOADED}, messages);
	}

	public boolean doesChangeModel() {
		return false;
	}

	public String getName() {
		return "Load schedule";
	}

}














