package hr.fer.zemris.ga_framework.algorithms.tsp.crossovers;

import hr.fer.zemris.ga_framework.algorithms.tsp.ICrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;









public class CycleCrossover implements ICrossover {
	
	private int len;
	private int tag;
	private int[] taken, rev1stpar;

	public CycleCrossover(int permlength) {
		tag = 0;
		len = permlength;
		taken = new int[len];
		rev1stpar = new int[len];
	}

	public void crossover(Permutation firstpar, Permutation secondpar, Permutation child) {
		// update tag, recreate field(s) if necessary
		if (++tag == 0) {
			taken = new int[len];
		}
		
		// construct reversed table for the second parent
		for (int i = 0; i < len; i++) {
			rev1stpar[firstpar.field[i]] = i;
		}
		
		// go element by element and copy cycles
		int from = 1;
		for (int i = 0; i < len; i++) {
			if (taken[i] == tag) continue;
			
			// switch parent from which elements will be taken
			from = (from == 1) ? 0 : 1;
			
			// if the element hasn't been copied, start copying the cycle
			int pos = i;
			Permutation currParent = (from == 0) ? firstpar : secondpar;
			do {
				// copy element, tag it as taken
				child.field[pos] = currParent.field[pos];
				taken[pos] = tag;
				
				// move to next position
				pos = rev1stpar[secondpar.field[pos]];
			} while (pos != i);
		}
	}

}














