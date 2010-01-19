package hr.fer.zemris.ga_framework.view.editors.algorithm_editor;

import java.util.ArrayList;
import java.util.List;

public class ParameterFileWrapper {
	
	public static class ParameterWrapper {
		private String name;
		private String valueString;
		private String type;
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setValueString(String valueString) {
			this.valueString = valueString;
		}
		public String getValueString() {
			return valueString;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getType() {
			return type;
		}
	}
	
	/* static fields */

	/* private fields */
	private String algorithmClassName;
	private List<ParameterWrapper> parameters;

	/* ctors */
	
	public ParameterFileWrapper() {
		parameters = new ArrayList<ParameterWrapper>();
	}

	/* methods */
	
	public void setAlgorithmClassName(String algorithmClassName) {
		this.algorithmClassName = algorithmClassName;
	}
	
	public String getAlgorithmClassName() {
		return algorithmClassName;
	}
	
	public void addParameterWrapper(ParameterWrapper wrapper) {
		parameters.add(wrapper);
	}
	
	public List<ParameterWrapper> getParameters() {
		return parameters;
	}

}














