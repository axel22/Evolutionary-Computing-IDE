package hr.fer.zemris.ga_framework.algorithms.tsp.mutations;

import hr.fer.zemris.ga_framework.algorithms.tsp.IMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

import java.util.Random;


public class InsertMutation implements IMutation {

	/* static fields */

	/* private fields */
	private Random rand;
	private int len;
	
	/* ctors */
	
	public InsertMutation(int permlength) {
		rand = new java.util.Random();
		len = permlength;
	}

	/* methods */
	
	public void mutate(Permutation tomutate, int sl) {
		// choose 2 points within the permutation
		int first = rand.nextInt(len);
		int second = 0;
		if (sl == -1) {
			second = rand.nextInt(tomutate.field.length);
		} else {
			if (sl < 1) sl = 1;
			else if (sl >= tomutate.field.length) sl = tomutate.field.length - 1;
			second = (first + rand.nextInt(sl)) % tomutate.field.length;
		}
		
		// random scramble of elements between points
		// copy them to another array
//		System.out.println("From " + first + " to " + second);
		int atsecond = tomutate.field[second];
		if (first == second) return;
		int i = second;
		for (;;) {
			int next = i - 1;
			if (next < 0) next += len;
			if (next == first) break;
			tomutate.field[i] = tomutate.field[next];
			i = next;
		}
		tomutate.field[i] = atsecond;
	}

}














