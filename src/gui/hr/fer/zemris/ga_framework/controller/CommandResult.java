package hr.fer.zemris.ga_framework.controller;

import java.util.HashMap;
import java.util.Map;



/**
 * Encapsulates the result of performing
 * a command.
 * 
 * @author Axel
 *
 */
public class CommandResult {
	
	/* static fields */
	

	/* private fields */
	private String errorMessage;
	private Events[] evtypes;
	private Map<String, Object> msgmap;
	
	
	/* ctors */
	
	/**
	 * Ctor for a successful command result.
	 */
	public CommandResult(Events[] eventTypes) {
		evtypes = eventTypes;
	}
	
	/**
	 * Ctor for a successful command result.
	 * Used to insert a key and an object into
	 * the message map.
	 */
	public CommandResult(Events[] eventTypes, String key, Object msgobj) {
		evtypes = eventTypes;
		if (key != null) {
			msgmap = new HashMap<String, Object>();
			msgmap.put(key, msgobj);
		}
	}
	
	/**
	 * Ctor for a successful command result.
	 * Used to insert two keys and two objects into
	 * the message map.
	 */
	public CommandResult(Events[] eventTypes, String k1, Object o1, String k2, Object o2) {
		evtypes = eventTypes;
		if (k1 != null) {
			msgmap = new HashMap<String, Object>();
			msgmap.put(k1, o1);
		}
		if (k2 != null) {
			if (msgmap == null) msgmap = new HashMap<String, Object>();
			msgmap.put(k2, o2);
		}
	}
	
	/**
	 * Ctor for a successful command result.
	 * Used to insert two keys and two objects into
	 * the message map.
	 */
	public CommandResult(Events[] eventTypes, String k1, Object o1, String k2, Object o2,
			String k3, Object o3) {
		evtypes = eventTypes;
		if (k1 != null) {
			msgmap = new HashMap<String, Object>();
			msgmap.put(k1, o1);
		}
		if (k2 != null) {
			if (msgmap == null) msgmap = new HashMap<String, Object>();
			msgmap.put(k2, o2);
		}
		if (k3 != null) {
			if (msgmap == null) msgmap = new HashMap<String, Object>();
			msgmap.put(k3, o3);
		}
	}

	/**
	 * Ctor for a successful command result.
	 * 
	 * @param eventTypes
	 * An array of events that the command has
	 * triggered.
	 */
	public CommandResult(Events[] eventTypes, Map<String, Object> messages) {
		evtypes = eventTypes;
		msgmap = messages;
	}
	
	/**
	 * Ctor for unsuccessful commands.
	 * 
	 * @param errorString
	 * A message indicating why the error has occurred.
	 */
	public CommandResult(String errorString) {
		errorMessage = errorString;
	}
	
	
	/* methods */
	
	/**
	 * @return
	 * True if command was successful,
	 * false otherwise.
	 */
	public boolean isSuccessful() {
		return errorMessage == null;
	}
	
	/**
	 * @return
	 * If the command was not successful
	 * returns an error message string.
	 * Otherwise, returns null.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
	/**
	 * @return
	 * A list of events returned by the command.
	 * Null value indicates no event has been
	 * triggered.
	 */
	public Events[] getEventTypes() {
		return evtypes;
	}
	
	/**
	 * Returns a message from the message map.
	 * It is used to transfer info about the event
	 * that occured.
	 * 
	 * @param key
	 * @return
	 */
	public Object msg(String key) {
		if (msgmap == null) return null;
		return msgmap.get(key);
	}
	
	public Object putMsg(String key, Object msg) {
		if (msgmap == null) msgmap = new HashMap<String, Object>();
		return msgmap.put(key, msg);
	}
	
}














