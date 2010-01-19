package hr.fer.zemris.ga_framework.algorithms.tsp;

import java.util.Arrays;
import java.util.Random;



public class Permutation {

	/* static fields */

	/* private fields */
	public int[] field;
	public double cost;
	
	/* ctors */
	
	protected Permutation() {
	}
	
	protected Permutation(int length) {
		field = new int[length];
	}
	
	public Permutation(int length, boolean random) {
		field = new int[length];
		for (int i = 0; i < length; i++) {
			field[i] = i;
		}
		
		if (random) {
			Random rand = new Random();
			int[] permfield =  new int[length];
			for (int i = 0; i < length; i++) {
				int randpos = rand.nextInt(length - i);
				permfield[i] = field[randpos];
				field[randpos] = field[length - i - 1];
			}
			field = permfield;
		}
	}
	
	public Permutation(Permutation other) {
		if (other.field != null) field = Arrays.copyOf(other.field, other.field.length);
		else field = null;
		cost = other.cost;
	}

	/* methods */
	
	public static Permutation createEmptyPermutation(int length) {
		return new Permutation(length);
	}
	
	public static Permutation createNullPermutation() {
		return new Permutation();
	}

	/**
	 * Swaps the specified elements of the permutation.
	 * 
	 * @throws 
	 */
	public void swap(int a, int b) {
		int tmp = field[a];
		field[a] = field[b];
		field[b] = tmp;
	}
	
	public void invert(int first, int second) {
		int elems;
		if (second >= first) elems = second - first + 1;
		else elems = second + field.length - first + 1;
		
		elems /= 2;
		
		for (int i = 0, curs = first, back = second; i < elems; i++) {
			int tmp = field[back];
			field[back] = field[curs];
			field[curs] = tmp;
			
			curs = (curs + 1) % field.length;
			if (--back < 0) back += field.length;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Permutation)) return false;
		Permutation that = (Permutation)o;
		return Arrays.equals(this.field, that.field);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(field);
	}

	@Override
	public String toString() {
		return Arrays.toString(field);
	}
	
}














