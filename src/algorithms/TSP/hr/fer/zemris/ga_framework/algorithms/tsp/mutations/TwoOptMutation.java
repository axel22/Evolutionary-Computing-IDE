package hr.fer.zemris.ga_framework.algorithms.tsp.mutations;

import hr.fer.zemris.ga_framework.algorithms.tsp.CityTable;
import hr.fer.zemris.ga_framework.algorithms.tsp.IMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

import java.util.Random;

public class TwoOptMutation implements IMutation {

	/* static fields */

	/* private fields */
	private Random rand;
	private CityTable cities;
	private int[] tf;
	private int len;

	/* ctors */
	
	public TwoOptMutation(int permlen, CityTable citytab) {
		rand = new Random();
		cities = citytab;
		len = permlen;
		tf = new int[len];
	}

	/* methods */
	
	@SuppressWarnings("unused")
	private double fitness(Permutation p) {
		double sum = 0;
		for (int i = 1; i < p.field.length; i++) {
			sum += cities.get(p.field[i - 1], p.field[i]);
		}
		sum += cities.get(p.field[p.field.length - 1], p.field[0]);
		return sum;
	}
	
	public void mutate(Permutation tomutate, int sl) {
		int a, b, c, d;
		
		// pick an edge
		a = rand.nextInt(len);
		b = (a + 1) % len;
		double ab = cities.get(tomutate.field[a], tomutate.field[b]), cd, ac, bd;
		
		// now pick a random edge, and search for
		// an edge to perform a 2 opt
		int start = rand.nextInt(len);
		for (c = (start + 1) % len; c != start; c = d) {
			d = (c + 1) % len;
			if (c == a) continue;
			cd = cities.get(tomutate.field[c], tomutate.field[d]);
			ac = cities.get(tomutate.field[a], tomutate.field[c]);
			bd = cities.get(tomutate.field[b], tomutate.field[d]);
			if ((ab + cd) > (ac + bd)) {
				// found edge for 2 opt
				// invert everything between b and c
//				System.out.println("Ok.");
//				System.out.println("For " + tomutate);
//				System.out.println("a,b = " + a + "," + b + "; c,d = " + c + "," + d);
//				System.out.println(fitness(tomutate));
				for (int j = b, k = c;; j = (j + 1) % len) {
					tf[k] = tomutate.field[j];
					if (--k < 0) k += len;
					if (j == c) break;
				}
				for (int j = b;; j = (j + 1) % len) {
					tomutate.field[j] = tf[j];
					if (j == c) break;
				}
//				System.out.println(fitness(tomutate));
				return;
			}
		}
		
		// could not find an edge for 2 opt - random swap
//		System.out.println("Jes' swappin'.");
		tomutate.swap(rand.nextInt(len), rand.nextInt(len));
	}

}














