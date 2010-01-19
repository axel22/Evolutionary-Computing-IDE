package hr.fer.zemris.ga_framework.controller.impl.commands;

import hr.fer.zemris.ga_framework.controller.CommandResult;
import hr.fer.zemris.ga_framework.controller.Events;
import hr.fer.zemris.ga_framework.controller.ICommand;
import hr.fer.zemris.ga_framework.model.Model;

import java.util.HashMap;
import java.util.Map;


/**
 * Creates view info within the model.
 * 
 * @author Axel
 *
 */
public class CreateView implements ICommand {
	
	/* static fields */
	

	/* private fields */
	private String viewclsnm;
	

	/* ctors */
	
	/**
	 * Creates a new view. Class name of the view
	 * must be specified, as it is it's unique
	 * identifier.
	 */
	public CreateView(String viewclsname) {
		viewclsnm = viewclsname;
	}
	
	

	/* methods */

	public CommandResult doCommand(Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Events.KEY.VIEW_CLSNAME, viewclsnm);
		
		return new CommandResult(new Events[]{Events.VIEW_CREATED}, map);
	}

	public String getName() {
		return "Create View";
	}

	public boolean doesChangeModel() {
		return false;
	}
	
	
	
}














