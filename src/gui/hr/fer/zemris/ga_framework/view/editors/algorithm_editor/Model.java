package hr.fer.zemris.ga_framework.view.editors.algorithm_editor;

import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IValue;

import java.util.ArrayList;
import java.util.HashMap;

import name.brijest.mvcapi.model.ListProp;
import name.brijest.mvcapi.model.MapProp;
import name.brijest.mvcapi.model.Prop;




public class Model {
	
	/* static fields */

	/* fields */
	public final MapProp<String, IValue> parametervalues = new MapProp<String, IValue>(new HashMap<String, IValue>());
	public final ListProp<IParameter> parameters = new ListProp<IParameter>(new ArrayList<IParameter>());
	public final Prop<String> name = new Prop<String>();
	public final Prop<IAlgorithm> algorithm = new Prop<IAlgorithm>();
	

	/* ctors */
	
	public Model() {
		
	}
	

	/* methods */

}














