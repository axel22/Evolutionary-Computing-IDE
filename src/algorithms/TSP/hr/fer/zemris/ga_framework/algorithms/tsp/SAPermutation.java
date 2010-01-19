package hr.fer.zemris.ga_framework.algorithms.tsp;

import java.util.List;
import java.util.Random;




public class SAPermutation extends Permutation {

	/* static fields */

	/* fields */
	public double slmid, sldev;
	public double mutprob;
	public IMutation mutation;

	/* ctors */
	
	public SAPermutation() {
	}
	
	public SAPermutation(int length) {
		super(length);
		slmid = 1;
		sldev = 1;
		mutprob = 0.25;
		mutation = null;
	}
	
	public SAPermutation(int length, boolean random)  {
		super(length, random);
		Random r = new Random();
		if (length > 1) {
			slmid = r.nextInt(length - 1) / 4 + 1;
			sldev = r.nextDouble() * (length / 16);
		} else {
			slmid = 1;
			sldev = 1;
		}
		mutprob = 1.0;
		mutation = null;
	}
	
	public SAPermutation(SAPermutation other) {
		super(other);
		slmid = other.slmid;
		sldev = other.sldev;
		mutprob = other.mutprob;
		mutation = null;
	}

	/* methods */
	
	public void mutateAdaptiveParams(Random rand) {
		// subsegment length
		slmid = slmid + rand.nextGaussian() * field.length / 16;
		if (slmid < 1) slmid = 1;
		else if (slmid >= field.length) slmid = field.length;
		sldev = sldev + rand.nextGaussian() * field.length / 16;
		if (sldev < 0.001) sldev = 0.001;
		else if (sldev >= (field.length * 4)) sldev = field.length;
		
		// mutation probability
		mutprob = mutprob + rand.nextGaussian() * 0.01;
		if (mutprob < 0.0) mutprob = 0.0;
		else if (mutprob > 1.0) mutprob = 1.0;
	}
	
	public void mutateMutation(Random rand, List<IMutation> moplst) {
		if (moplst.isEmpty()) return;
		
		if (rand.nextDouble() < 0.05) mutation = moplst.get(rand.nextInt(moplst.size()));
	}
	
}














