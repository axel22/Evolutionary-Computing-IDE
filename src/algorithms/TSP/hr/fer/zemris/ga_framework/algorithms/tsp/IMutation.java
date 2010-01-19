package hr.fer.zemris.ga_framework.algorithms.tsp;



public interface IMutation {
	
	/**
	 * 
	 * @param tomutate
	 * @param len
	 * Specifying the -1 value for len shall yield a random length
	 * for the subsegment to mutate.
	 */
	public void mutate(Permutation tomutate, int len);
	
}














