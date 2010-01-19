package hr.fer.zemris.ga_framework.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public class AlgoDir {
	
	/* static fields */
	
	/* private fields */
	private String name;
	private List<AlgoDir> children, ro_children;
	private List<Class<?>> algorithms, ro_algorithms;

	/* ctors */
	
	public AlgoDir(String dirname) {
		children = new ArrayList<AlgoDir>();
		ro_children = Collections.unmodifiableList(children);
		algorithms = new ArrayList<Class<?>>();
		ro_algorithms = Collections.unmodifiableList(algorithms);
		name = dirname;
	}
	
	/* methods */
	
	public List<AlgoDir> getChildren() {
		return ro_children;
	}
	
	public void addSubdir(AlgoDir subdir) {
		children.add(subdir);
	}
	
	public List<Class<?>> getAlgorithms() {
		return ro_algorithms;
	}

	public void addAlgorithm(Class<?> alg) {
		algorithms.add(alg);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		name = n;
	}



	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		for (AlgoDir child : children) {
			sb.append(child.toString());
			sb.append("\n");
		}
		for (Class<?> cls : algorithms) {
			sb.append(cls.getSimpleName());
			sb.append("\n");
		}
		sb.append("]");
		return sb.toString();
	}
	
	
	
}














