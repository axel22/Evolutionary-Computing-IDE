package hr.fer.zemris.ga_framework.algorithms.tsp.crossovers;

import hr.fer.zemris.ga_framework.algorithms.tsp.ICrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

import java.util.Random;








public class PartiallyMappedCrossover implements ICrossover {
	
	private Random rand;
	private int[] copied;
	private int[] taken;
	private int[] reversed2ndpar;
	private int tag;

	public PartiallyMappedCrossover(int permlength) {
		rand = new Random();
		copied = new int[permlength];
		taken = new int[permlength];
		reversed2ndpar = new int[permlength];
		tag = 0;
	}

	public void crossover(Permutation firstpar, Permutation secondpar, Permutation child) {
		// reserve new tag
		if (++tag == 0) {
			copied = new int[copied.length];
			taken = new int[taken.length];
			tag = 1;
		}
		
		int len = taken.length;
		
		// choose two crossover points at random - the space between them is the 'segment'
		int start = rand.nextInt(len), end = rand.nextInt(len);
//		System.out.println(start + ", " + end);
		
		// copy segment from the first parent into the offspring, and tag them as copied
		for (int i = start;; i = (i + 1) % len) {
			// copy element at position 'i'
			child.field[i] = firstpar.field[i];
			
			// position 'i' in offspring is taken
			taken[i] = tag;
			
			// element at position 'i' has been copied
			copied[child.field[i]] = tag;
			
			if (i == end) break;
		}
		
		// create a reversed table of the second parent
		for (int i = 0; i < len; i++) {
			reversed2ndpar[secondpar.field[i]] = i;
		}
		
		// for each yet uncopied segment element 'i' of the second parent
		for (int i = start;; i = (i + 1) % len) {
			// is it uncopied?
			int elem = secondpar.field[i];
			int elementAt = i;
			if (copied[elem] != tag) while (true) {
					// check which element 'j' has been copied in it's place in the offspring
					int chelem = child.field[elementAt];
					
					// find that element in the second parent
					int secparpos = reversed2ndpar[chelem];
					
					if (taken[secparpos] != tag) {
						// place 'i' on the position occupied by 'j' in the 
						// parent if that position is free in the offspring
						child.field[secparpos] = elem;
						taken[secparpos] = tag;
						copied[elem] = tag;
						break;
					} else {
						// if it's not free, repeat the process from that position on
						elementAt = secparpos;
					}
			}
			
			if (i == end) break;
		}
		
		// copy the remaining elements of the second parent
		for (int i = (end + 1) % len, copypos = i;; i = (i + 1) % len) {
			int elem = secondpar.field[i];
			if (copied[elem] != tag) {
				// find free position
				while (taken[copypos] == tag) copypos = (copypos + 1) % len;
				
				// copy - maintaining taken and copied fields is no longer required
				child.field[copypos] = elem;
				copypos = (copypos + 1) % len;
			}
			
			if (i == start) break;
		}
	}

}














