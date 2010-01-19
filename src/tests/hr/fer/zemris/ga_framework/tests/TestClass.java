package hr.fer.zemris.ga_framework.tests;

import hr.fer.zemris.ga_framework.algorithms.tsp.ICrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.IMutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.PartialPermutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;
import hr.fer.zemris.ga_framework.algorithms.tsp.crossovers.EdgeCrossover2;
import hr.fer.zemris.ga_framework.algorithms.tsp.crossovers.PartialOrderCrossover;
import hr.fer.zemris.ga_framework.algorithms.tsp.mutations.SwapSegmentMutation;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.model.impl.ObjectList;

import java.util.ArrayList;
import java.util.List;








public class TestClass {

	public static void main(String[] args) {
		for (int i = 0; i < 2500; i++)
		testCrossover();
	}
	
	public static void testPOX() {
		for (int i = 0; i < 50000; i++) {
			PartialOrderCrossover crossover = new PartialOrderCrossover();
			PartialPermutation p1 = new PartialPermutation(20, 10, true);
			PartialPermutation p2 = new PartialPermutation(20, 10, true);
			PartialPermutation child = PartialPermutation.createEmpty(20, 10);
			crossover.crossover(p1, p2, child);
			if (!child.isPermutationValid()) throw new RuntimeException("Not valid: " + child);
		}
	}
	
	public static void testMutation() {
		IMutation mut = new SwapSegmentMutation(9);
		Permutation p = new Permutation(9, false);
		p.field = new int[] {8,0,1,2,4,5,3,7,6};
		System.out.println(p);
		mut.mutate(p, -1);
		System.out.println(p);
	}
	
	public static void testCrossover() {
		ICrossover xover = new EdgeCrossover2(9);
		ICrossover xover2 = new EdgeCrossover2(9);
		Permutation p1 = new Permutation(9, true);
		Permutation p2 = new Permutation(9, true);
//		p1.field = new int[] {0,1,2,3,4,5,6,7,8};
//		p2.field = new int[] {8,2,6,7,1,5,4,0,3};
//		p1.field = new int[] {8,0,1,2,4,5,3,7,6};
//		p2.field = new int[] {1,8,3,0,5,4,2,7,6};
//		System.out.println(p1);
//		System.out.println(p2);
		Permutation c1 = Permutation.createEmptyPermutation(9);
		Permutation c2 = Permutation.createEmptyPermutation(9);
		xover.crossover(p1, p2, c1);
		xover2.crossover(p1, p2, c2);
		if (!c1.equals(c2)) {
			System.out.println(p1);
			System.out.println(p2);
			System.out.println(c1);
			System.out.println(c2);
			xover2 = new EdgeCrossover2(9);
			c2 = Permutation.createEmptyPermutation(9);
			xover2.crossover(p1, p2, c2);
			throw new IllegalStateException();
		}
	}
	
	public static void testObjList() {
		List<Object> allowed = new ArrayList<Object>();
		allowed.add("bla");
		allowed.add("dtd");
		allowed.add("\"");
		allowed.add(" ");
		ObjectList olst = new ObjectList(ParameterTypes.STRING, allowed);
		
		olst.add("bla");
		olst.add("dtd");
		olst.add("\"");
		
		String s = olst.serialize();
		System.out.println(s);
		olst = (ObjectList) olst.deserialize(s);
		
		for (Object o : olst) System.out.println("'" + o + "'");
		for (Object o : olst.getAllowedObjects()) System.out.println("'" + o + "'");
	}

}














