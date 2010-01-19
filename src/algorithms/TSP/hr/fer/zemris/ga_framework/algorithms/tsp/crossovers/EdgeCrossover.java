package hr.fer.zemris.ga_framework.algorithms.tsp.crossovers;

import hr.fer.zemris.ga_framework.algorithms.tsp.ICrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;

import java.util.Random;


/**
 * Given two parents, performs edge crossover, and returns child.
 * Length must be greater than 1.
 * 
 * @author Axel
 *
 */
@Deprecated
public class EdgeCrossover implements ICrossover {
	
	/* private */
	private Random rand;
	private int length;
	private int[][] edgetable;
	private int[] free;
	
	/* ctors */
	public EdgeCrossover(int permutationlength) {
		if (permutationlength < 2) throw new IllegalArgumentException("Length must be greater than 1.");
		length = permutationlength;
		edgetable = new int[length][4];
		free = new int[length];
		rand = new Random(50);
	}
	
	/* methods */
	public void crossover(Permutation firstpar, Permutation secondpar, Permutation ch) {
		int[] child = ch.field;
		
		// build edge table
		for (int i = 0; i < length; i++) free[i] = i;
		for (int i = 0; i < length; i++) {
			// first parent
			int posOfElem = findPosFor(firstpar.field, i);
			edgetable[i][1] = firstpar.field[(posOfElem + 1) % length];
			posOfElem--;
			if (posOfElem < 0) posOfElem += length;
			edgetable[i][0] = firstpar.field[posOfElem];

			// second parent
			posOfElem = findPosFor(secondpar.field, i);
			edgetable[i][3] = secondpar.field[(posOfElem + 1) % length];
			posOfElem--;
			if (posOfElem < 0) posOfElem += length;
			edgetable[i][2] = secondpar.field[posOfElem];
		}
		
		int elemdone = 0;
		int left = rand.nextInt(length);
		int right = (left + 1) % length;
		boolean wasLastElement = false;
		while (!wasLastElement) {
			// select random element from free elements
			int randpos = rand.nextInt(length - elemdone);
			int element = free[randpos];
			free[randpos] = free[length - elemdone - 1];
			elemdone++;
			
			// delete it's occurences from edge table (not it)
			removeFromEdgeTable(element);
			
			// add element to new solution
			child[right] = element;
			
			// check for end condition
			if (left == right) break;
			
			// try to find right neighbour
			int currentend = right;
			right = (right + 1) % length;
			boolean isonright = true;
			while (true) {
				// select neighbour with most occurences in parents
				int most = 0, mostpos = -1;
				int currentelement = child[currentend];
				for (int i = 0; i < 4; i++) {
					if (edgetable[currentelement][i] == -1) continue;
					int curr = 1;
					for (int j = i + 1; j < 4; j++) {
						if (edgetable[currentelement][j] == -1) continue;
						if (edgetable[currentelement][j] == edgetable[currentelement][i]) curr++;
					}
					if (curr > most) {
						mostpos = i;
						most = curr;
					} else if (curr == most && rand.nextBoolean()) {
						// notice that distribution is not quite uniform here,
						// but this *can* be justified
						mostpos = i;
						most = curr;
					}
				}
				if (mostpos != -1) {
					// a neighbour has been found
					int mostelem = edgetable[currentelement][mostpos];
					
					// delete element from free list
					int elempos = -1;
					for (int i = 0; i < (length - elemdone); i++) {
						if (free[i] == mostelem) {
							elempos = i;
							free[i] = free[length - elemdone - 1];
							elemdone++;
							break;
						}
					}
					if (elempos == -1) throw new IllegalStateException("Should exist in free table.");
					
					// delete it's occurences from edge table
					removeFromEdgeTable(mostelem);
					
					// end condition achieved?
					wasLastElement = right == left;
					
					// add neighbour to permutation
					if (isonright) {
						child[right] = mostelem;
						currentend = right;
						right = (right + 1) % length;
					} else {
						child[left] = mostelem;
						currentend = left;
						left = left - 1;
						if (left < 0) left = left + length;
					}
					
					if (wasLastElement) break;
				} else {
					// no neighbour has been found and put tu permutation
					if (isonright) {
						// if no neighbour was found on the right, try the left side
						currentend = (left + 1) % length;
						isonright = false;
					} else {
						// no neighbour on either side, get random element again
						break;
					}
				}
			}
		}
	}
	
	private int findPosFor(int[] field, int elem) {
		for (int i = 0; i < field.length; i++) {
			if (field[i] == elem) return i;
		}
		return -1;
	}

	private final void removeFromEdgeTable(int element) {
		for (int i = 0, toremove = 4; i < length && toremove > 0; i++) {
			for (int j = 0; j < 4; j++) {
				if (edgetable[i][j] == element) {
					edgetable[i][j] = -1;
					toremove--;
				}
			}
		}
	}

}














