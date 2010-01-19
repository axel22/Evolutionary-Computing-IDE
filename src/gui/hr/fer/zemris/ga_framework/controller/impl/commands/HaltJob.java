package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.model.Model;
import hr.fer.zemris.ga_framework.model.RunningAlgorithmInfo;
import hr.fer.zemris.ga_framework.model.misc.Time;
import hr.fer.zemris.ga_framework.model.misc.Time.Metric;


public class HaltJob implements ICommand {
	
	/* static fields */

	/* private fields */
	private Long id;
	
	/* ctors */
	
	public HaltJob(Long jobId) {
		id = jobId;
	}

	/* methods */

	public CommandResult doCommand(Model model) {
		RunningAlgorithmInfo alginfo = model.getDispatcher().getJobInfo(id);
		if (alginfo == null) return new CommandResult("No job with given id.");

		Time lastElapsed = Time.subtract(new Time(System.currentTimeMillis(), Metric.ms), alginfo.getStarted());
		Time elapsed = alginfo.getElapsed();
		if (elapsed == null) elapsed = new Time(0, Metric.ms);
		alginfo.setElapsed(Time.add(elapsed, lastElapsed));
		
		model.getDispatcher().clearJob(id);
		
		return new CommandResult(new Events[]{});
	}

	public boolean doesChangeModel() {
		return true;
	}

	public String getName() {
		return "Halt algorithm";
	}

}














