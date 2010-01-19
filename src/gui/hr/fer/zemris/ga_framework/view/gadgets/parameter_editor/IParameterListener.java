package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor;

import hr.fer.zemris.ga_framework.model.IParameter;




/**
 * Listener interface for the ParameterEditor.
 * 
 * @author Axel
 *
 */
public interface IParameterListener {

	public void onParameterValueChange(int paramIndex, IParameter param, Object value);
	
}














