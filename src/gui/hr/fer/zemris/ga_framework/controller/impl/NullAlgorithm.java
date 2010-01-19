package hr.fer.zemris.ga_framework.controller.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IParameterDialog;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.IValueRenderer;

public class NullAlgorithm implements IAlgorithm {

	/* static fields */

	/* private fields */

	/* ctors */
	
	/* methods */

	public boolean doesReturnInfoDuringRun() {
		return false;
	}

	public List<String> getAuthors() {
		return new ArrayList<String>();
	}

	public Map<String, IValue> getDefaultValues() {
		return new HashMap<String, IValue>();
	}

	public String getDescription() {
		return "";
	}

	public Map<Class<? extends ISerializable>, Class<? extends IParameterDialog>> getEditors() {
		return null;
	}

	public String getExtensiveInfo() {
		return "";
	}

	public List<String> getLiterature() {
		return new ArrayList<String>();
	}

	public String getName() {
		return "";
	}

	public IParameter getParameter(String name) {
		return null;
	}

	public List<IParameter> getParameters() {
		return new ArrayList<IParameter>();
	}

	public IParameter getReturnValue(String name) {
		return null;
	}

	public List<IParameter> getReturnValues() {
		return new ArrayList<IParameter>();
	}

	public void haltAlgorithm() {
	}
	
	public boolean isNative() {
		return false;
	}

	public boolean isPausable() {
		return false;
	}

	public boolean isPaused() {
		return false;
	}

	public boolean isRunning() {
		return false;
	}

	public boolean isSaveable() {
		return false;
	}

	public void load(String s) {
		throw new UnsupportedOperationException();
	}

	public IAlgorithm newInstance() {
		return new NullAlgorithm();
	}

	public Map<String, IValue> runAlgorithm(Map<String, IValue> values,
			IInfoListener listener)
	{
		return new HashMap<String, IValue>();
	}

	public String save() {
		throw new UnsupportedOperationException();
	}

	public void setPaused(boolean on) {
		throw new UnsupportedOperationException();
	}

	public List<IParameter> getRunProperties() {
		return null;
	}

	public void setRunProperty(String key, Object value) {
	}

	public Map<String, IValue> getDefaultRunProperties() {
		return null;
	}

	public Map<Class<? extends ISerializable>, Class<? extends IValueRenderer>> getRenderers() {
		return null;
	}

}














