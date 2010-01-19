package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.model.Model;

public class RunPendingJob implements ICommand {
	
	/* static fields */
	private static final Events[] EVENTS = new Events[] {};

	/* private fields */
	private Long id, nid;
	private int index;

	/* ctors */
	
	public RunPendingJob(Long jobid, int queueindex, Long newid) {
		id = jobid;
		index = queueindex;
		nid = newid;
	}

	/* methods */

	public CommandResult doCommand(Model model) {
		boolean isRun = model.startJobFromPendingList(id, index, nid);
		
		if (isRun) return new CommandResult(EVENTS);
		else return new CommandResult("Could not run pending job.");
	}

	public boolean doesChangeModel() {
		return true;
	}

	public String getName() {
		return "Run pending job";
	}

}














