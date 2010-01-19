package hr.fer.zemris.ga_framework.algorithms.tsp.mutations;

import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

public class MultiShiftMutation extends ShiftMutation {

	/* static fields */
	
	/* private fields */
	
	/* ctors */
	
	public MultiShiftMutation(int len) {
		super(len);
	}
	
	/* methods */
	
	@Override
	public void mutate(Permutation tomutate, int sl) {
		super.mutate(tomutate, sl);
		super.mutate(tomutate, sl);
	}
}














