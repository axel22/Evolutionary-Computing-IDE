package hr.fer.zemris.ga_framework.algorithms.tsp.crossovers;

import hr.fer.zemris.ga_framework.algorithms.tsp.ICrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

import java.util.Random;

public class GreedySubtourCrossover implements ICrossover {
	
	/* static fields */

	/* private fields */
	private Random rand;
	private int[] copied;
	private int len, mark;

	/* ctors */
	
	public GreedySubtourCrossover(int permlength) {
		rand = new Random();
		copied = new int[permlength];
		len = permlength;
		mark = 0;
	}

	/* methods */
	
	public void crossover(Permutation firstpar, Permutation secondpar, Permutation child) {
		int l = rand.nextInt(len), r, fcurs, scurs;
		
		// change mark token, rereserve mark field if necessary
		if (++mark == 0) copied = new int[len];
		
		// now copy element from first parent to starting point
		int firstcity = child.field[l] = firstpar.field[l];
		copied[firstpar.field[l]] = mark;
		
		// position yerself (r and l are pointers to child)
		r = (l + 1) % len;
		if (--l < 0) l += len;
		fcurs = l;
		
		// find that element in second parent
		scurs = -1;
		for (int i = 0; i < len; i++) if (secondpar.field[i] == firstcity) {
			scurs = i;
		}
		
		// now copy elements (len - 1) elements that are remaining
		boolean isright = true;
		for (int i = 1; i < len; i++, isright = !isright) {
			if (isright) {
				// find first uncopied element in second parent
				while (copied[secondpar.field[scurs]] == mark) scurs = (scurs + 1) % len;
				
				// copy element
				child.field[r] = secondpar.field[scurs];
				copied[child.field[r]] = mark;
				r = (r + 1) % len;
				scurs = (scurs + 1) % len;
			} else {
				// find first uncopied element in first parent
				while (copied[firstpar.field[fcurs]] == mark) if (--fcurs < 0) fcurs += len;
				
				// copy element
				child.field[l] = firstpar.field[fcurs];
				copied[child.field[l]] = mark;
				if (--l < 0) l += len;
				if (--fcurs < 0) fcurs += len;
			}
		}
	}

}














