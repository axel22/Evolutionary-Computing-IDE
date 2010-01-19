package hr.fer.zemris.ga_framework.model;

import java.util.Map;


/**
 * An interface representing diagram information.
 * Diagrams are used for displaying dependence between
 * algorithm parameters and return values.
 * 
 * @author Axel
 *
 */
public interface IGraph {
	
	/**
	 * @return
	 * The name of the graph.
	 */
	public String getGraphName();
	
	/**
	 * @return
	 * Name of the parameter appearing on x-axis.
	 */
	public String getAbscissaName();
	
	/**
	 * @return
	 * Name of the parameter appearing on y-axis.
	 */
	public String getOrdinateName();
	
	/**
	 * @return
	 * Map of the parameters that are fixed.
	 * Note that for those parameters that are not
	 * fixed, multiple curves will be drawn.
	 */
	public Map<String, Object> getCurveFamily();
	
	/**
	 * @return
	 * Short description of the graph.
	 */
	public String getDescription();
	
}















