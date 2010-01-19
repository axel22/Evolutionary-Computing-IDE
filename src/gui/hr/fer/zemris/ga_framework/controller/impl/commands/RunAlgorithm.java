package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IParameterInventory;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.model.RunningAlgorithmInfo;
import hr.fer.zemris.ga_framework.model.misc.Time;
import hr.fer.zemris.ga_framework.model.misc.Time.Metric;


/**
 * Runs an algorithm for a single set of parameters.
 * 
 * @author Axel
 *
 */
public class RunAlgorithm implements ICommand {
	
	/* static fields */

	/* private fields */
	private Long id;
	private IAlgorithm algorithm;
	private IParameterInventory paramIterator;
	private String description;
	private boolean isListened;

	/* ctors */
	
	public RunAlgorithm(Long alg_id, String desc, IAlgorithm algo, boolean listened, IParameterInventory paramit) {
		id = alg_id;
		algorithm = algo;
		paramIterator = paramit;
		description = desc;
		isListened = listened;
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
		
		// dispatch a new job within the model
		RunningAlgorithmInfo nfo = new RunningAlgorithmInfo(description, algorithm, paramIterator, isListened);
		nfo.setStarted(new Time(System.currentTimeMillis(), Metric.ms));
		nfo.setElapsed(new Time(0, Metric.ms));
		model.dispatchJob(id, nfo, false);
		
		return new CommandResult(new Events[]{});
	}

	public boolean doesChangeModel() {
		return true;
	}

	public String getName() {
		return "Run algorithm";
	}
	
}














