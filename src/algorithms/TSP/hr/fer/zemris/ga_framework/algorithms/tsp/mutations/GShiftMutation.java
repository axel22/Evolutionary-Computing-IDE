package hr.fer.zemris.ga_framework.algorithms.tsp.mutations;

import hr.fer.zemris.ga_framework.algorithms.tsp.CityTable;
import hr.fer.zemris.ga_framework.algorithms.tsp.Permutation;


public class GShiftMutation extends ShiftMutation {

	/* static fields */

	/* private fields */
	private CityTable cities;

	/* ctors */

	public GShiftMutation(int permlength, CityTable cityTable) {
		super(permlength);
		
		cities = cityTable; 
	}

	/* methods */

	public void mutate(Permutation tomutate) {
		// select an interval [from, to]
		int from = rand.nextInt(len);
		int intervlen = rand.nextInt(len - 1);
		int to = (from + intervlen) % len;

		// select the destination outside the interval
		int dest = -1;
		
		// first try to find a city that's closer to 'from' than it's current neighbour
		boolean shouldInverse = false, withinInterval = false;
		int pos = (to + 1 + rand.nextInt(len - intervlen - 1)) % len;
		int prev = from - 1;
		if (prev < 0) prev += len;
		int targetcity = tomutate.field[from];
		double currcost = cities.get(targetcity, tomutate.field[prev]);
		for (int i = 0; i < len; i++, pos = (pos + 1) % len) {
			if (pos == from) withinInterval = true;
			if (withinInterval) {
				if (pos == to) withinInterval = false;
				continue;
			}
			
			double cost = cities.get(targetcity, tomutate.field[pos]);
			if (cost <= currcost && pos != prev) {
				dest = (pos + 1) % len;
				break;
			}
		}
		
		// try to find the same city for the 'to' if you haven't found it for 'from'
		if (dest == -1) {
			pos = (to + 1 + rand.nextInt(len - intervlen - 1)) % len;
			int next = (to + 1) % len;
			targetcity = tomutate.field[to];
			currcost = cities.get(targetcity, tomutate.field[next]);
			withinInterval = false;
			for (int i = 0; i < len; i++, pos = (pos + 1) % len) {
				if (pos == from) withinInterval = true;
				if (withinInterval) {
					if (pos == to) withinInterval = false;
					continue;
				}
				
				double cost = cities.get(targetcity, tomutate.field[pos]);
				if (cost <= currcost && pos != next) {
					dest = (pos + 1) % len;
					shouldInverse = true;
					break;
				}
			}
		}
		
		// if you cannot find such a city, pick a random destination
		if (dest == -1) dest = (to + rand.nextInt(len - intervlen - 1) + 1) % len;

		// copy the interval in question
		int ppos = dest;
		if (shouldInverse) {
			for (int i = 0, gpos = from; i <= intervlen; i++, gpos = (gpos + 1) % len,
			ppos = (ppos + 1) % len) {
				xcharr[ppos] = tomutate.field[gpos];
			}
		} else {
			for (int i = 0, gpos = to; i <= intervlen; i++, ppos = (ppos + 1) % len) {
				xcharr[ppos] = tomutate.field[gpos];
				if (--gpos < 0) gpos += len;
			}
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




