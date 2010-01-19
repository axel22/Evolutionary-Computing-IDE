package hr.fer.zemris.ga_framework.model;




/**
 * Value handler interface - for classes handling
 * values.
 * Values must be appended in the same sequence
 * as bound values.
 * 
 * Results may be obtained once all the values
 * have been appended - first getResultingValue()
 * method must be called, only then can the
 * getResultingBoundValue methods be called.
 * 
 * @author Axel
 *
 */
public interface IValueHandler {
	
	public void reset();
	public void appendValue(Object o);
	public void appendBoundValue(String name, Object o);
	public Object getResultingValue();
	public Object getResultingBoundValue(String name);
	
}














