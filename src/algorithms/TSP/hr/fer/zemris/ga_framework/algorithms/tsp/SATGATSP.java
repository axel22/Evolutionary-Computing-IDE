package hr.fer.zemris.ga_framework.algorithms.tsp;

import hr.fer.zemris.ga_framework.model.AlgorithmTerminatedException;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IParameterDialog;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.IValueRenderer;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.model.impl.ObjectList;
import hr.fer.zemris.ga_framework.model.impl.SimpleAlgorithm;
import hr.fer.zemris.ga_framework.model.impl.criteriums.GreaterThanIntegerCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.IntervalRealCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.PositiveIntegerCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.PositiveRealCriterium;
import hr.fer.zemris.ga_framework.model.impl.parameters.PrimitiveParameter;
import hr.fer.zemris.ga_framework.model.impl.parameters.SerializableParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;




public class SATGATSP extends SimpleAlgorithm implements IZoomSettable {
	
	/* static fields */
	private static final int INFO_SEND_PERIOD = 10000;
	private static final List<IParameter> PARAMS = new ArrayList<IParameter>();
	private static final List<IParameter> RETVALS = new ArrayList<IParameter>();
	private static final Map<String, Object> DEFAULTVALS = new HashMap<String, Object>();
	private static final Map<Class<? extends ISerializable>, Class<? extends IParameterDialog>> EDITORS = 
		new HashMap<Class<? extends ISerializable>, Class<? extends IParameterDialog>>();
	
	static {
		// criteriums
		GreaterThanIntegerCriterium gt2 = new GreaterThanIntegerCriterium(2);
		PositiveIntegerCriterium positiveInt = new PositiveIntegerCriterium();
		IntervalRealCriterium bet0and1 = new IntervalRealCriterium(0.0, 1.0);
		PositiveRealCriterium positiveReal = new PositiveRealCriterium();
		List<Object> crossovers = new ArrayList<Object>();
		crossovers.add("Order");
		crossovers.add("Edge");
		crossovers.add("Cycle");
		crossovers.add("PMX");
		crossovers.add("GSX");
		crossovers.add("1-PPP");
		List<Object> mutations = new ArrayList<Object>();
		mutations.add("Swap");
		mutations.add("Scramble");
		mutations.add("Inversion");
		mutations.add("Insert");
		mutations.add("Shift");
		mutations.add("2 opt");
		mutations.add("GShift");
		List<Object> selfadas = new ArrayList<Object>();
		selfadas.add("None");
		selfadas.add("Subsegment length");
		selfadas.add("Mutation probability");
		selfadas.add("Mutation operator");
		
		// init params
		PARAMS.add(new SerializableParameter("Problem definition", "List of cities and their locations.", CityTable.class));
		PARAMS.add(new PrimitiveParameter("Population size", "Number of individuals in the population.", ParameterTypes.INTEGER, gt2));
		PARAMS.add(new PrimitiveParameter("Tournament size", "Number of individuals selected for the tournament. Entering a value greater than population size will always result in using population size as tournament size.", ParameterTypes.INTEGER, positiveInt));
		PARAMS.add(new PrimitiveParameter("Pressure", "Number of individuals eliminated in each tournament. Entering a value greater or equal to tournament size will always result in one surviving individual.", ParameterTypes.INTEGER, positiveInt));
		PARAMS.add(new PrimitiveParameter("Crossover operator", "Operator used for crossover.", ParameterTypes.STRING, crossovers));
		PARAMS.add(new PrimitiveParameter("Mutation operator", "Operator used for mutation.", ParameterTypes.STRING, mutations));
		PARAMS.add(new SerializableParameter("Mutation operator list", "Operator used for self-adaptive mutation.", ObjectList.class));
		PARAMS.add(new PrimitiveParameter("Mutation probability", "Probability that mutation will occur on a newly created offspring.", ParameterTypes.REAL, bet0and1));
		PARAMS.add(new PrimitiveParameter("Self-adaptivity", "What form of self-adaptivity does the algorithm use.", ParameterTypes.STRING, selfadas));
		PARAMS.add(new PrimitiveParameter("Iterations", "Maximum number of iterations (tournament selections) algorithm will perform.", ParameterTypes.INTEGER, positiveInt));
		PARAMS.add(new PrimitiveParameter("Expected cost", "Expected permutation cost. Once a solution with a cost less than the specified is found, " +
				"the algorithm will stop. If no expected cost is known, this may be set to 0.0.", ParameterTypes.REAL, positiveReal));
		
		// init default vals
		DEFAULTVALS.put("Problem definition", new CityTable());
		DEFAULTVALS.put("Population size", 200);
		DEFAULTVALS.put("Tournament size", 3);
		DEFAULTVALS.put("Pressure", 1);
		DEFAULTVALS.put("Crossover operator", "Order");
		DEFAULTVALS.put("Mutation operator", "Inversion");
		DEFAULTVALS.put("Mutation operator list", new ObjectList(ParameterTypes.STRING, mutations));
		DEFAULTVALS.put("Mutation probability", 0.25);
		DEFAULTVALS.put("Self-adaptivity", "None");
		DEFAULTVALS.put("Iterations", 400000);
		DEFAULTVALS.put("Expected cost", 0.0);
		
		// init retvals
		RETVALS.add(new PrimitiveParameter("City permutation", "Optimal city permutation found during the run.", ParameterTypes.STRING));
		RETVALS.add(new PrimitiveParameter("Cost", "The total cost of visiting all the cities in the optimal order.", ParameterTypes.REAL));
		RETVALS.add(new PrimitiveParameter("Iterations made", "Total number of iterations performed before reaching iteration limit or expected solution cost.", ParameterTypes.INTEGER));
		RETVALS.add(new PrimitiveParameter("Success measure", "Success of the run - 0.0 if expected cost wasn't reached, 1.0 if expected cost was reached.", ParameterTypes.REAL));
		
		// init editor map
		EDITORS.put(CityTable.class, CitySelectPane.class);
	}

	/* private fields */
	private CityTable citytab;
	private double zoomFactor;

	/* ctors */
	
	public SATGATSP() {
		super(PARAMS, RETVALS, DEFAULTVALS);
	}

	/* methods */
	
	protected double fitness(Permutation p) {
		double sum = 0;
		for (int i = 1; i < p.field.length; i++) {
			sum += citytab.get(p.field[i - 1], p.field[i]);
		}
		sum += citytab.get(p.field[p.field.length - 1], p.field[0]);
		return sum;
	}
	
	private List<IMutation> getMutOps(ObjectList mutopnames) {
		List<IMutation> lst = new ArrayList<IMutation>();
		
		for (Object o : mutopnames) {
			String s = (String) o;
			lst.add(TSPUtility.createMutation(citytab.getCityNum(), s, citytab));
		}
		
		return lst;
	}

	@Override
	protected void runImplementation(Map<String, IValue> values, IInfoListener listener, Map<String, Object> retvals) {
		// extract parameter values
		citytab = values.get("Problem definition").value();
		Integer popsize = values.get("Population size").value();
		Integer toursize = values.get("Tournament size").value();
		if (toursize > popsize) toursize = popsize;
		Integer pressure = values.get("Pressure").value();
		if (pressure >= toursize) pressure = toursize - 1;
		String xname = values.get("Crossover operator").value();
		String mname = values.get("Mutation operator").value();
		ObjectList mutopnames = values.get("Mutation operator list").value();
		Double mutprob = values.get("Mutation probability").value();
		String selfadaptive = values.get("Self-adaptivity").value();
		Integer iterations = values.get("Iterations").value();
		Double expectedcost = values.get("Expected cost").value();
		
		
		// initialize
		Random rand = new Random();
		int permlen = citytab.getCityNum();
		ICrossover crossover = TSPUtility.createCrossover(permlen, xname);
		IMutation mutation = TSPUtility.createMutation(permlen, mname, citytab);
		List<IMutation> mutoplst = getMutOps(mutopnames);
		if (mutoplst.isEmpty()) mutoplst.add(mutation);
		SAPermutation bestperm = null;
		double bestval = Double.MAX_VALUE;
		SAPermutation[] tournament = new SAPermutation[toursize];
		Comparator<SAPermutation> permComp = new Comparator<SAPermutation>() {
			public int compare(SAPermutation p1, SAPermutation p2) {
				if (p1.cost < p2.cost) return -1;
				if (p1.cost > p2.cost) return 1;
				return 0;
			}
		};
		if (listener != null) TSPUtility.initCanvas(listener, citytab, this);
		int sacode = -1;
		if (selfadaptive.equals("None")) sacode = 0;
		if (selfadaptive.equals("Subsegment length")) sacode = 1;
		if (selfadaptive.equals("Mutation probability")) sacode = 2;
		if (selfadaptive.equals("Mutation operator")) sacode = 3;
		
		// create population
		SAPermutation[] population = new SAPermutation[popsize];
		for (int i = 0; i < population.length; i++) {
			population[i] = new SAPermutation(permlen, true);
			population[i].cost = fitness(population[i]);
			population[i].mutation = mutoplst.get(0);
			if (bestval > population[i].cost) {
				bestval = population[i].cost;
				bestperm = population[i];
			}
		}
		
		// run loop
		int itercounter = 0;
		while (bestval > expectedcost && itercounter++ < iterations) {
			if (terminate) throw new AlgorithmTerminatedException();
			
			// select a tournament
			for (int i = 0; i < tournament.length; i++) {
				int rpos = rand.nextInt(population.length - i);
				tournament[i] = population[rpos];
				population[rpos] = population[population.length - 1 - i];
			}
			
			// destroy the worst in the tournament
			// use reproduction to create new ones
			Arrays.sort(tournament, permComp);
			for (int i = toursize - pressure; i < toursize; i++) {
				SAPermutation p1 = tournament[rand.nextInt(toursize - pressure)];
				SAPermutation p2 = tournament[rand.nextInt(toursize - pressure)];
				crossover.crossover(p1, p2, tournament[i]);
				if (tournament[i] == bestperm) bestperm = tournament[0];
				
				switch (sacode) {
				case 0:
					if (rand.nextDouble() < mutprob) mutation.mutate(tournament[i], -1);
					break;
				case 1:
					mutateSelfAdaptively1(tournament[i], mutation, rand, permlen, mutprob, mutoplst);
					break;
				case 2:
					mutateSelfAdaptively2(tournament[i], mutation, rand, permlen, mutprob, mutoplst);
					break;
				case 3:
					mutateSelfAdaptively3(tournament[i], mutation, rand, permlen, mutprob, mutoplst);
					break;
				}
				
				tournament[i].cost = fitness(tournament[i]);
				if (tournament[i].cost <= bestval) {
					bestval = tournament[i].cost;
					bestperm = tournament[i];
				}
			}
			
			// return them to population
			for (int i = 0, pi = popsize - toursize; i < tournament.length; i++, pi++) {
				population[pi] = tournament[i];
			}
			
			// report info
			if (itercounter % INFO_SEND_PERIOD == 0) sendInfo(listener, itercounter, iterations, bestperm, population, mutoplst);
		}
		
		// report info
		sendInfo(listener, itercounter, iterations, bestperm, population, mutoplst);
		
		// prepare results
		retvals.put("City permutation", bestperm.toString());
		retvals.put("Cost", bestval);
		retvals.put("Iterations made", itercounter);
		retvals.put("Success measure", bestval <= expectedcost ? 1.0 : 0.0);
	}

	private void mutateSelfAdaptively3(SAPermutation permutation, IMutation mutation, Random rand, int permlen, Double mutprob, List<IMutation> moplst) {
		if (rand.nextDouble() < mutprob) {
			permutation.mutateMutation(rand, moplst);
			
			permutation.mutation.mutate(permutation, -1);
		}
	}

	private void mutateSelfAdaptively1(SAPermutation permutation, IMutation mutation, Random rand, int permlen, double mutprob, List<IMutation> moplst) {
		if (rand.nextDouble() < mutprob) {
			// first mutate sa parameters
			permutation.mutateAdaptiveParams(rand);
			
			// calculate subsegment length
			int sublen = (int)(rand.nextGaussian() * permutation.sldev + permutation.slmid);
			if (sublen < 1) sublen = 1;
			if (sublen > permlen) sublen = permlen;
			
			// now mutate permutation itself
			mutation.mutate(permutation, sublen);
		}
	}
	
	private void mutateSelfAdaptively2(SAPermutation permutation, IMutation mutation, Random rand, int permlen, double mutprob, List<IMutation> moplst) {
		// first mutate sa parameters
		permutation.mutateAdaptiveParams(rand);
		
		// now mutate permutation itself
		if (rand.nextDouble() < permutation.mutprob) mutation.mutate(permutation, -1);
	}

	private void sendInfo(IInfoListener listener, int itercounter, int totaliter, SAPermutation bestperm, SAPermutation[] population, List<IMutation> moplst) {
		if (listener == null) return;
		
		// properties
		listener.setProperty("Best permutation", bestperm.toString());
		listener.setProperty("Lowest cost", String.valueOf(bestperm.cost));
		double mid = 0.0, dev = 0.0, mut = 0.0, mut_d = 0.0;
		for (int i = 0; i < population.length; i++) {
			mid += population[i].slmid;
			dev += population[i].sldev;
			mut += population[i].mutprob;
		}
		mid /= population.length;
		dev /= population.length;
		mut /= population.length;
		for (int i = 0; i < population.length; i++) {
			double d = population[i].mutprob - mut;
			d *= d;
			mut_d += d;
		}
		mut_d /= population.length;
		mut_d = Math.sqrt(mut_d);
		listener.setProperty("Subsegment length mid (avg)", String.valueOf(mid));
		listener.setProperty("Subsegment length deviation (avg)", String.valueOf(dev));
		listener.setProperty("Mutation probability (avg)", String.valueOf(mut));
		listener.setProperty("Mutation probability (dev)", String.valueOf(mut_d));
		int[] howmany = new int[moplst.size()];
		for (int i = 0; i < population.length; i++) {
			howmany[moplst.indexOf(population[i].mutation)]++;
		}
		for (int i = 0; i < howmany.length; i++) {
			listener.setProperty(moplst.get(i).getClass().getSimpleName(), String.valueOf(howmany[i]));
		}
		
		// percentage and image
		if (itercounter % (2 * INFO_SEND_PERIOD) == 0) {
			listener.setPercentage(1.0 * itercounter / totaliter);
			TSPUtility.redrawImage(listener, bestperm, citytab, "", zoomFactor);
		}
	}

	public boolean doesReturnInfoDuringRun() {
		return true;
	}

	public List<String> getAuthors() {
		List<String> lst = new ArrayList<String>();
		lst.add("Aleksandar Prokopec");
		return lst;
	}

	public String getDescription() {
		return "This is a self-adaptive variant of the tournament genetic algorithm " +
				"that solves the travelling salesman problem. Most mutation operators work " +
				"on subsegments of a permutation that have a certain length. This length is " +
				"a self-adaptive parameter here.";
	}

	public Map<Class<? extends ISerializable>, Class<? extends IParameterDialog>> getEditors() {
		return EDITORS;
	}

	public String getExtensiveInfo() {
		return "No extensive info is available.";
	}

	public List<String> getLiterature() {
		List<String> lst = new ArrayList<String>();
		lst.add("A. Eiben, J. Smith: <i>Introduction to Evolutionary Computing</i>, 2003.");
		return lst;
	}

	public String getName() {
		return "Self Adaptive Tournament TSP GA";
	}

	public boolean isNative() {
		return false;
	}

	public void setZoomFactor(double zoomf) {
		zoomFactor = zoomf;
	}

	public Map<Class<? extends ISerializable>, Class<? extends IValueRenderer>> getRenderers() {
		return null;
	}

}














