package hr.fer.zemris.ga_framework.model;


/**
 * Thrown when the algorithm checks the terminate
 * flag and determines it has been set.
 * This terminates the algorithm.
 * The exception itself is caught within the algorithm
 * thread, but not inside the algorithm object's methods.
 * 
 * @author Axel
 *
 */
public class AlgorithmTerminatedException extends RuntimeException {

	
	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 1L;
	
	

	public AlgorithmTerminatedException() {
		super();
	}

	public AlgorithmTerminatedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public AlgorithmTerminatedException(String arg0) {
		super(arg0);
	}

	public AlgorithmTerminatedException(Throwable arg0) {
		super(arg0);
	}

	
}














