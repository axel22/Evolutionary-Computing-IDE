package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IGraph;
import hr.fer.zemris.ga_framework.model.IParameterInventory;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.model.ReturnHandler;
import hr.fer.zemris.ga_framework.model.RunningAlgorithmInfo;
import hr.fer.zemris.ga_framework.model.misc.Time;
import hr.fer.zemris.ga_framework.model.misc.Time.Metric;

import java.util.List;


/**
 * Runs an algorithm multiple times for multiple sets of parameters.
 * 
 * @author Axel
 *
 */
public class RunJob implements ICommand {
	
	/* static fields */

	/* private fields */
	private Long id;
	private IAlgorithm algorithm;
	private IParameterInventory paramIterator;
	private String description;
	private boolean isListened;
	private List<IGraph> graphs;
	private List<Object[]> gnuplots;

	/* ctors */
	
	public RunJob(Long alg_id, String desc, IAlgorithm algo, boolean listened, IParameterInventory paramit,
			List<IGraph> graphList, List<Object[]> gnuplotlist) {
		id = alg_id;
		algorithm = algo;
		paramIterator = paramit;
		description = desc;
		isListened = listened;
		graphs = graphList;
		gnuplots = gnuplotlist;
	}
	

	/* methods */
	
	public CommandResult doCommand(Model model) {
		// check if algorithm with the specified id exists in model
		if (model.getDispatcher().existsActiveJob(id)) {
			// return if algorithm is running
			if (!model.getDispatcher().isJobDone(id)) {
				return new CommandResult("Unfinished job with specified id exists.");
			}
			
			// remove algorithm if it is not running
			model.getDispatcher().clearJob(id);
		}
		
		// check for circular dependencies with return handlers
		if (ReturnHandler.hasCircularDependencies(paramIterator.getReturnHandlers())) {
			return new CommandResult("List of return value handlers has circular dependencies.");
		}
		
		// dispatch a new job within the model
		RunningAlgorithmInfo nfo = new RunningAlgorithmInfo(description, algorithm, paramIterator, isListened,
				Model.KEY.GRAPH_LIST, graphs);
		nfo.setData(Model.KEY.GNUPLOT_LIST, gnuplots);
		nfo.setStarted(new Time(System.currentTimeMillis(), Metric.ms));
		nfo.setElapsed(new Time(0, Metric.ms));
		model.dispatchJob(id, nfo, false);
		
		return new CommandResult(new Events[]{});
	}

	public boolean doesChangeModel() {
		return true;
	}

	public String getName() {
		return "Run job";
	}
	
}














