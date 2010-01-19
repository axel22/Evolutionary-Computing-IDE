package hr.fer.zemris.ga_framework.algorithms.tsp.mutations;

import hr.fer.zemris.ga_framework.algorithms.tsp.IMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

import java.util.Random;

public class SwapSegmentMutation implements IMutation {

	private int len;
	private Random rand;
	private int[] tmparr;
	
	public SwapSegmentMutation(int length) {
		len = length;
		rand = new Random();
		tmparr = new int[len / 2 + 1];
	}
	
	public void mutate(Permutation tomutate, int sl) {
		// select two non-intersecting segments
		int seglen = 0;
		if (len <= 3) seglen = 1;
		else {
			if (sl == -1) seglen = rand.nextInt(len / 2) + 1;
			else {
				if (sl < 1) sl = 1;
				else if (sl > len / 2) sl = len / 2;
			}
		}
		int start1 = rand.nextInt(len);
		int start2 = 0;
		if (len <= 3) start2 = (start1 + 1) % len;
		else start2 = (start1 + seglen + rand.nextInt(len - seglen - 1)) % len;
		
		// swap them
		for (int pos = start1, i = 0; i < seglen; i++, pos = (pos + 1) % len) {
			tmparr[i] = tomutate.field[pos];
		}
		for (int p = start1, q = start2, i = 0; i < seglen; i++, p = (p + 1) % len, q = (q + 1) % len) {
			tomutate.field[p] = tomutate.field[q];
		}
		for (int pos = start2, i = 0; i < seglen; i++, pos = (pos + 1) % len) {
			tomutate.field[pos] = tmparr[i];
		}
	}
	
}














