package hr.fer.zemris.ga_framework.algorithms.tsp.mutations;

import hr.fer.zemris.ga_framework.algorithms.tsp.IMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

import java.util.Random;

public class ShiftMutation implements IMutation {

	/* static fields */

	/* private fields */
	protected int len;
	protected Random rand;
	protected int[] xcharr;

	/* ctors */

	public ShiftMutation(int permlength) {
		len = permlength;
		rand = new Random();
		xcharr = new int[permlength];
	}

	/* methods */

	public void mutate(Permutation tomutate, int sl) {
		// select an interval [from, to]
		int from = rand.nextInt(len);
		int intervlen = 0;
		if (sl == -1) {
			intervlen = rand.nextInt(len - 1);
		} else {
			if (sl >= len) sl = len - 1;
			intervlen = sl;
		}
		int to = (from + intervlen) % len;

		// select the destination outside the interval
		int dest = (to + rand.nextInt(len - intervlen - 1) + 1) % len;
//		int from = 1, intervlen = 5, to = 6, dest = 8;
//		System.out.println("[from, to] = " + from + ", " + to + "; dest = " + dest);

		// copy the interval in question
		int ppos = dest;
		for (int i = 0, gpos = from; i <= intervlen; i++, gpos = (gpos + 1) % len,
		ppos = (ppos + 1) % len) {
			xcharr[ppos] = tomutate.field[gpos];
		}
		
		// copy elements between the interval and the destination
		for (int gpos = (to + 1) % len; gpos != dest; gpos = (gpos + 1) % len) {
			xcharr[gpos] = tomutate.field[gpos];
		}
		
		// copy elements from the destination to the interval start
		for (int gpos = dest; gpos != from; gpos = (gpos + 1) % len, ppos = (ppos + 1) % len) {
			xcharr[ppos] = tomutate.field[gpos];
		}
		
		// exchange arrays
		int[] tmp = tomutate.field;
		tomutate.field = xcharr;
		xcharr = tmp;
	}

}
