package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IGraph;
import hr.fer.zemris.ga_framework.model.IParameterInventory;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.model.RunningAlgorithmInfo;
import hr.fer.zemris.ga_framework.model.misc.Time;
import hr.fer.zemris.ga_framework.model.misc.Time.Metric;

import java.util.List;

public class AddPendingJob implements ICommand {
	
	/* static fields */
	private static final Events[] EVENTS = new Events[] {};
	
	/* private fields */
	private RunningAlgorithmInfo info;
	private Long id;

	/* ctors */
	
	public AddPendingJob(Long jobid, String desc, IAlgorithm alg, IParameterInventory inv, List<IGraph> graphs) {
		id = jobid;
		info = new RunningAlgorithmInfo(desc, alg, inv, false, Model.KEY.GRAPH_LIST, graphs);
		info.setStarted(new Time(System.currentTimeMillis(), Metric.ms));
	}

	/* methods */

	public CommandResult doCommand(Model model) {
		boolean isAdded = model.dispatchJob(id, info, true);
		
		if (isAdded) return new CommandResult(EVENTS);
		else return new CommandResult("Could not add pending job.");
	}

	public boolean doesChangeModel() {
		return true;
	}

	public String getName() {
		return "Add pending job";
	}

}














