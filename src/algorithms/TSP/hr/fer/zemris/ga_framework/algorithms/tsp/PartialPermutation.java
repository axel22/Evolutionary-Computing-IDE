package hr.fer.zemris.ga_framework.algorithms.tsp;

import java.util.BitSet;
import java.util.Random;



public class PartialPermutation extends Permutation {

	/* static fields */

	/* fields */
	public int maxelem;
	public int[] lookuptable;
	public int[] collisions;
	public BitSet takencities;

	/* ctors */
	
	protected PartialPermutation() {
		super();
	}
	
	public PartialPermutation(int maxelements, int len, boolean randomize) {
		super(len, randomize);
		
		lookuptable = new int[len];
		collisions = new int[maxelements];
		takencities = new BitSet(maxelements);
		maxelem = maxelements;
		
		if (randomize) {
			Random rand = new Random();
			
			int[] cities = new int[maxelem];
			for (int i = 0; i < maxelem; i++) cities[i] = i;
			for (int i = 0, left = maxelem; i < len; i++) {
				int r = rand.nextInt(left);
				int city = cities[r];
				cities[r] = cities[--left];
				
				lookuptable[i] = city;
				takencities.set(city);
			}
		}
	}

	/* methods */
	
	public boolean isPermutationValid() {
		BitSet in = new BitSet(field.length);
		for (int i = 0; i < field.length; i++) {
			if (in.get(field[i])) return false;
			in.set(field[i]);
		}
		return true;
	}
	
	public boolean exchangeCityForCity(int oldcity, int newcity) {
		if (!takencities.get(oldcity)) return false;
		
		// find oldcity
		int i = 0;
		for (; i < lookuptable.length; i++) {
			if (lookuptable[i] == oldcity) break; 
		}
		
		// update lookuptable
		lookuptable[i] = newcity;
		
		// update takencities bitset
		takencities.set(oldcity, false);
		takencities.set(newcity, true);
		
		// update collisions
		collisions[oldcity] = 0;
		
		// the field does not need to be updated!
		
		return true;
	}
	
	public void exchangeRandomCity(Random rand) {
		if (maxelem == lookuptable.length) return;
		
		// create roulette wheel based on number of collisions
		double[] rwheel = new double[lookuptable.length];
		double total = 0.0;
		for (int i = 0; i < lookuptable.length; i++) {
			int city = lookuptable[i];
			rwheel[i] = 1 + collisions[city];
			total += rwheel[i];
		}
		
		// roulette wheel choose city to eliminate
		double r = rand.nextDouble() * total;
		int spinner = 0;
		while (r > rwheel[spinner] && spinner < rwheel.length) {
			r -= rwheel[spinner];
			spinner++;
		}
		int ctoelim = lookuptable[spinner];
		
		// choose some not yet taken city
		int ctoadd = rand.nextInt(maxelem);
		while (takencities.get(ctoadd)) ctoadd = (ctoadd + 1) % maxelem;
		
		// update lookuptable
		lookuptable[spinner] = ctoadd;
		
		// update takencities bitset
		takencities.set(ctoelim, false);
		takencities.set(ctoadd, true);
		
		// update collisions
		collisions[ctoelim] = 0;
		
		// the field does not need to be updated!
	}
	
	/**
	 * Lengths and maximum number of elements must conform.
	 */
	public static PartialPermutation createEmptyFromParents(PartialPermutation p1, PartialPermutation p2) {
		PartialPermutation child = new PartialPermutation();
		
		child.maxelem = p1.maxelem;
		child.field = new int[p1.field.length];
		child.lookuptable = new int[p1.lookuptable.length];
		child.collisions = new int[p1.maxelem];
		child.takencities = new BitSet(p1.maxelem);
		
		return child;
	}
	
	public static PartialPermutation createEmpty(int maxelem, int len) {
		PartialPermutation child = new PartialPermutation();
		
		child.maxelem = maxelem;
		child.field = new int[len];
		child.lookuptable = new int[len];
		child.collisions = new int[maxelem];
		child.takencities = new BitSet(maxelem);
		
		return child;
	}
	
	public static PartialPermutation createNull() {
		PartialPermutation child = new PartialPermutation();
		
		return child;
	}

}














