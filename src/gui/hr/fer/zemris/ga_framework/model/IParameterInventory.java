package hr.fer.zemris.ga_framework.model;

import hr.fer.zemris.ga_framework.view.editors.algorithm_scheduler.Model.AdditionalHandler;

import java.util.List;
import java.util.Map;



/**
 * Iterates over the parameter values
 * for the algorithm.
 * 
 * @author Axel
 *
 */
public interface IParameterInventory extends Iterable<Map<String, IValue>> {
	
	/**
	 * @return
	 * A list of names for the parameters that are changing.
	 */
	public List<String> getChangingParamNames();
	
	/**
	 * Appends return values of the algorithm the specified input values.
	 * 
	 * @param returnValues
	 * @throws IllegalArgumentException
	 * If the specified map of input values does not conform any such map
	 * returned by the parameter inventory. In other words, only maps
	 * returned by the iterator should be passed as input values.
	 */
	public void appendTo(Map<String, IValue> inputValues, Map<String, IValue> returnValues);
	
	/**
	 * Returns a set of return values for a previously appended
	 * set of input parameters.
	 * 
	 * @param setOfInputParams
	 * @return
	 * Previously appended map of return values for the
	 * specified input values.
	 * Null if nothing has been appended. Throws exception
	 * if input values haven't been returned by the iterator.
	 * @throws IllegalArgumentException
	 * If no such set of input values was returned by the iterator.
	 */
	public Map<String, IValue> getReturnValues(Map<String, IValue> setOfInputParams);
	
	/**
	 * Returns the list of objects describing how to return values
	 * should be handled.
	 * 
	 * @return
	 */
	public List<ReturnHandler> getReturnHandlers();
	
	/**
	 * Returns a list of objects describing how to handle additional values.
	 * 
	 * @return
	 */
	public List<AdditionalHandler> getAdditionalHandlers();
	
	/**
	 * Returns the number of runs per set.
	 * 
	 * @return
	 */
	public int getRunsPerSet();
	
	/**
	 * Returns the total number of parameter sets within the inventory.
	 * 
	 * @return
	 */
	public int size();
	
}














