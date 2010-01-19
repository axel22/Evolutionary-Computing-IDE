package hr.fer.zemris.ga_framework.algorithms.tsp.mutations;

import hr.fer.zemris.ga_framework.algorithms.tsp.IMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

import java.util.Random;

public class SwapMutation implements IMutation {
	
	/* static fields */

	/* private fields */
	private Random rand;

	/* ctors */
	
	public SwapMutation() {
		rand = new Random();
	}

	/* methods */
	
	public void mutate(Permutation child, int sl) {
		if (sl == -1) {
			child.swap(rand.nextInt(child.field.length), rand.nextInt(child.field.length));
		} else {
			if (sl < 1) sl = 1;
			else if (sl >= child.field.length) sl = child.field.length - 1;
			
			int randpos = rand.nextInt(child.field.length);
			child.swap(randpos, (randpos + 1 + rand.nextInt(sl)) % child.field.length);
		}
	}

}














