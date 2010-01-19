package hr.fer.zemris.ga_framework.view.gadgets.parameter_editor.dialogs;

import hr.fer.zemris.ga_framework.model.IParameterDialog;

import org.eclipse.swt.widgets.Composite;


/**
 * Abstract base class for parameter dialogs.
 * 
 * @author Axel
 *
 */
public abstract class ParameterDialog extends Composite implements IParameterDialog {

	/* static fields */

	/* private fields */

	/* ctors */
	
	public ParameterDialog(Composite c, int style) {
		super(c, style);
	}

	/* methods */
	
	@Override
	protected void checkSubclass() {
	}

}














