package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.model.Model;

public class CancelPendingJob implements ICommand {
	
	/* static fields */
	private static final Events[] EVENTS = new Events[] {};

	/* private fields */
	private Long id;
	private int index;

	/* ctors */
	
	public CancelPendingJob(Long jobid, int jobindex) {
		id = jobid;
		index = jobindex;
	}

	/* methods */

	public CommandResult doCommand(Model model) {
		boolean isrun = model.cancelPendingJob(id, index);
		
		if (isrun) return new CommandResult(EVENTS);
		else return new CommandResult("No such job exists.");
	}

	public boolean doesChangeModel() {
		return true;
	}

	public String getName() {
		return "Cancel pending job";
	}

}














