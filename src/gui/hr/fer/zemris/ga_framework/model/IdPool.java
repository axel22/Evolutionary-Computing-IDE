package hr.fer.zemris.ga_framework.model;

import java.util.Stack;



/**
 * Serves as a consistent id pool.
 * 
 * User must also be consistent - must
 * not return already returned id, or
 * an id that hasn't been returned at all.
 * 
 * @author Axel
 *
 */
public class IdPool {
	
	/* static fields */

	/* private fields */
	private static long seed;
	private static Stack<Long> returnstack;
	

	/* ctors */
	
	public IdPool() {
		seed = 0;
		returnstack = new Stack<Long>();
	}
	

	/* methods */
	
	public long getId() {
		if (returnstack.isEmpty()) {
			return ++seed;
		}
		return returnstack.pop();
	}
	
	public void returnId(long id) {
		returnstack.push(id);
	}

}














