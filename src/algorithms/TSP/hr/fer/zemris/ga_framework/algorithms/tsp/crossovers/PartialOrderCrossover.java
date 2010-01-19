package hr.fer.zemris.ga_framework.algorithms.tsp.crossovers;

import hr.fer.zemris.ga_framework.algorithms.tsp.PartialPermutation;

import java.util.BitSet;
import java.util.Random;





public class PartialOrderCrossover {
	
	private Random rand;
	private BitSet alreadyInChild;
	private int[] invertedTable;
	
	public PartialOrderCrossover() {
		rand = new Random();
		alreadyInChild = new BitSet();
	}
	
	public void crossover(PartialPermutation p1, PartialPermutation p2, PartialPermutation child) {
		int len = p1.field.length;
		int maxelem = p1.maxelem;
		alreadyInChild.clear();
		if (invertedTable == null || invertedTable.length < maxelem) invertedTable = new int[maxelem];
		
		// build the lookup table and taken city set from the parents
		int citiesFromFirst = rand.nextInt(len);
		child.takencities.clear();
		int pos1 = rand.nextInt(len), i = 0;
		// add 'citiesFromFirst' elements from first parent
		for (; i < citiesFromFirst; i++, pos1 = (pos1 + 1) % len) {
			child.lookuptable[pos1] = p1.lookuptable[pos1];
			child.takencities.set(child.lookuptable[pos1]);
			invertedTable[p1.lookuptable[pos1]] = pos1;
		}
		int cpos = pos1, pos2 = pos1;
		// add the rest (those that you can) from the second parent
		for (int k = 0; k < len; k++, pos2 = (pos2 + 1) % len) {
			int par2city = p2.lookuptable[pos2];
			if (!child.takencities.get(par2city)) {
				child.lookuptable[cpos] = par2city;
				child.takencities.set(par2city);
				invertedTable[par2city] = cpos;
				
				cpos = (cpos + 1) % len;
				i++;
			}
			if (i == len) break;
		}
		// add the rest from the first parent
		for (; i < len; pos1 = (pos1 + 1) % len) {
			int par1city = p1.lookuptable[pos1];
			if (!child.takencities.get(par1city)) {
				child.lookuptable[cpos] = par1city;
				child.takencities.set(par1city);
				invertedTable[par1city] = cpos;
				
				cpos = (cpos + 1) % len;
				i++;
			}
		}
		
		// copy a segment from the first parent (only those cities on the child's list)
		pos1 = rand.nextInt(len);
		cpos = pos1;
		int segmentlen = rand.nextInt(len), copycount = 0;
		for (int k = 0; k < segmentlen; k++, pos1 = (pos1 + 1) % len) {
			int city = p1.lookuptable[p1.field[pos1]];
			if (child.takencities.get(city)) {
				int childindex = invertedTable[city];
				child.field[cpos] = childindex;
				alreadyInChild.set(city);
				
				cpos = (cpos + 1) % len;
				copycount++;
			}
		}
		
		// continue copying from the second parent (only those cities on the child's list)
		pos2 = pos1;
		for (int k = 0; k < len; k++, pos2 = (pos2 + 1) % len) {
			int city = p2.lookuptable[p2.field[pos2]];
			if (child.takencities.get(city) && !alreadyInChild.get(city)) {
				int childindex = invertedTable[city];
				child.field[cpos] = childindex;
				alreadyInChild.set(city);
				
				cpos = (cpos + 1) % len;
				copycount++;
			}
			if (copycount == len) break;
		}
		
		// copy remaining cities from the first parent (only those on child's list)
		for (; copycount < len; pos1 = (pos1 + 1) % len) {
			int city = p1.lookuptable[p1.field[pos1]];
			if (child.takencities.get(city) && !alreadyInChild.get(city)) {
				int childindex = invertedTable[city];
				child.field[cpos] = childindex;
				alreadyInChild.set(city);
				
				cpos = (cpos + 1) % len;
				copycount++;
			}
		}
	}
	
}














