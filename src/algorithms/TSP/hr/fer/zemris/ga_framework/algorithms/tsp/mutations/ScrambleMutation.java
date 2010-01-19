package hr.fer.zemris.ga_framework.algorithms.tsp.mutations;

import hr.fer.zemris.ga_framework.algorithms.tsp.IMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

import java.util.Random;


public class ScrambleMutation implements IMutation {

	/* static fields */

	/* private fields */
	private Random rand;
	private int[] tfield;
	
	/* ctors */
	
	public ScrambleMutation(int permlength) {
		rand = new java.util.Random();
		tfield = new int[permlength];
	}

	/* methods */
	
	public void mutate(Permutation tomutate, int sl) {
		// choose 2 points within the permutation
		int first = rand.nextInt(tfield.length);
		int second = 0;
		if (sl == -1) {
			second = rand.nextInt(tfield.length);
		} else {
			if (sl < 1) sl = 1;
			else if (sl >= tfield.length) sl = tfield.length - 1;
			second = (first + rand.nextInt(sl)) % tfield.length;
		}
		int elems;
		if (second >= first) elems = second - first + 1;
		else elems = second + tfield.length - first + 1;
		
		// random scramble of elements between points
		// copy them to another array
//		System.out.println("from " + first + " to " + second);
//		System.out.println(tomutate);
		for (int i = 0, curs = first; i < elems; i++, curs = (curs + 1) % tfield.length) {
			tfield[i] = tomutate.field[curs];
		}
//		System.out.println(Arrays.toString(tfield));
		// return them in random order
		for (int i = 0, curs = first; i < elems; i++, curs = (curs + 1) % tfield.length) {
			int randpos = rand.nextInt(elems - i);
			tomutate.field[curs] = tfield[randpos];
			tfield[randpos] = tfield[elems - i - 1];
		}
	}

}














