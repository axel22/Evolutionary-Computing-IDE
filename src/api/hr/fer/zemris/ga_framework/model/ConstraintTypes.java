package hr.fer.zemris.ga_framework.model;



/**
 * Describes types of constraints that exist
 * for parameters.
 * 
 * @author Axel
 *
 */
public enum ConstraintTypes {
	/**
	 * Values are enumerated, meaning there
	 * are finite number of values, and they
	 * are chosen through a combo box in GUI.
	 */
	ENUMERATION,
	/**
	 * Values are constrained according to
	 * some criterium (e.g. algorithm that
	 * decides whether or not a value is
	 * valid).
	 */
	CRITERIUM
}














