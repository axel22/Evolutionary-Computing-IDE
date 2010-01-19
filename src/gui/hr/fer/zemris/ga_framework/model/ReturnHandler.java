/**
 * 
 */
package hr.fer.zemris.ga_framework.model;

import hr.fer.zemris.ga_framework.model.HandlerTypes;
import hr.fer.zemris.ga_framework.model.IParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class ReturnHandler {
	private String paramname;
	private String boundTo;
	public HandlerTypes handler;
	/**
	 * !!only used while deserializing!!
	 */
	public ReturnHandler() {
		// only used while deserializing
	}
	public ReturnHandler(String parameterName, HandlerTypes handlerType) {
		paramname = parameterName;
		handler = handlerType;
	}
	
	public static List<ReturnHandler> createLastHandlers(List<IParameter> returns) {
		List<ReturnHandler> handlers = new ArrayList<ReturnHandler>();
		
		for (IParameter p : returns) {
			handlers.add(new ReturnHandler(p.getName(), HandlerTypes.Last));
		}
		
		return handlers;
	}
	
	public static boolean hasCircularDependencies(List<ReturnHandler> handlers) {
		Map<String, ReturnHandler> handlermap = new HashMap<String, ReturnHandler>();
		Set<String> encountered = new HashSet<String>();
		Stack<String> stack = new Stack<String>();
		
		// create handler map
		for (ReturnHandler rh : handlers) {
			handlermap.put(rh.paramname, rh);
		}
		
		// now check circular deps
		for (ReturnHandler rh : handlers) {
			stack.clear();
			encountered.clear();
			stack.push(rh.paramname);
			
			while (!stack.empty()) {
				// get next
				String name = stack.pop();
				
				// check if already seen
				if (encountered.contains(name)) return true;
				encountered.add(name);
				
				// now add to stack those he is pointing to
				ReturnHandler handler = handlermap.get(name);
				if (handler.boundTo != null) stack.push(handler.boundTo);
			}
		}
		
		return false;
	}

	public void setParamname(String paramname) {
		this.paramname = paramname;
	}

	public String getParamname() {
		return paramname;
	}

	public void setBoundTo(String boundTo) {
		this.boundTo = boundTo;
	}

	public String getBoundTo() {
		return boundTo;
	}
	
	public void setHandler(String handlerString) {
		if (handlerString == null) handler = null;
		else {
			try {
				handler = HandlerTypes.valueOf(HandlerTypes.class, handlerString);
			} catch (IllegalArgumentException e) {
				handler = null;
			}
		}
	}

	public String getHandler() {
		if (handler == null) return null;
		return handler.toString();
	}
}













