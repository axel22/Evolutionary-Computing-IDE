package hr.fer.zemris.ga_framework.algorithms.tsp.crossovers;

import hr.fer.zemris.ga_framework.algorithms.tsp.ICrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

import java.util.Random;

public class OnePointPartialPreservationCrossover implements ICrossover {

	private int len;
	private Random rand;
	private int tag;
	private int[] added;
	
	public OnePointPartialPreservationCrossover(int permlength) {
		len = permlength;
		rand = new Random();
		added = new int[len];
		tag = 1;
	}
	
	public void crossover(Permutation firstpar, Permutation secondpar, Permutation child) {
		// choose a crossover point
		int cpoint = rand.nextInt(len);

		// reset added field and update tag
		if (tag == -1) {
			tag = 1;
			added = new int[len];
		} else tag++;
		
		// copy first parent into the child (up to crossover point)
		int cpos = rand.nextInt(len);
		for (int pos = 0; pos <= cpoint; pos++) {
			added[firstpar.field[pos]] = tag;
			child.field[cpos] = firstpar.field[pos];
			cpos = (cpos + 1) % len;
		}
		
		// copy the rest of the elements from the first parent
		// in the order in which they appear in the second parent
		for (int i = 0; i < len; i++) {
			if (added[secondpar.field[i]] != tag) {
				child.field[cpos] = secondpar.field[i];
				cpos = (cpos + 1) % len;
			}
		}
	}
	
}














