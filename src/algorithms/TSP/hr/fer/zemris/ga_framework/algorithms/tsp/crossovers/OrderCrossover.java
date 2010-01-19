package hr.fer.zemris.ga_framework.algorithms.tsp.crossovers;

import hr.fer.zemris.ga_framework.algorithms.tsp.ICrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

import java.util.Random;

public class OrderCrossover implements ICrossover {
	
	/* static fields */

	/* private fields */
	private Random rand;
	private int len;
	private int[] copied;
	private int mark;

	/* ctors */
	
	public OrderCrossover(int permlength) {
		len = permlength;
		rand = new Random();
		copied = new int[permlength];
		mark = 0;
	}

	/* methods */
	
	public void crossover(Permutation firstpar, Permutation secondpar, Permutation child) {	
		// choose 2 random points
		int start = rand.nextInt(len);
		int end = rand.nextInt(len);
		if (start == end) end = (end + 1) % len;
//		System.out.println(start + " -> " + end);
		
		// copy between from first parent
		if (++mark == 0) copied = new int[len];
		for (int i = start; i != end; i = (i + 1) % len) {
			child.field[i] = firstpar.field[i];
			copied[child.field[i]] = mark;
		}
		
		// copy rest from second parent
		for (int i = 0, curs = end, chpos = end; i < len; i++, curs = (curs + 1) % len) {
			if (copied[secondpar.field[curs]] != mark) {
				child.field[chpos] = secondpar.field[curs];
				chpos = (chpos + 1) % len;
			}
		}
	}

}














