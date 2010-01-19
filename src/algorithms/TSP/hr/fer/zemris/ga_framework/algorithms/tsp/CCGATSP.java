package hr.fer.zemris.ga_framework.algorithms.tsp;

import hr.fer.zemris.ga_framework.algorithms.tsp.crossovers.PartialOrderCrossover;
import hr.fer.zemris.ga_framework.model.ICanvas;
import hr.fer.zemris.ga_framework.model.ICriterium;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IPainter;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IParameterDialog;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.IValueRenderer;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.model.impl.SimpleAlgorithm;
import hr.fer.zemris.ga_framework.model.impl.criteriums.GreaterThanIntegerCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.IntervalRealCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.PositiveIntegerCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.PositiveRealCriterium;
import hr.fer.zemris.ga_framework.model.impl.parameters.PrimitiveParameter;
import hr.fer.zemris.ga_framework.model.impl.parameters.SerializableParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CCGATSP extends SimpleAlgorithm {
	
	/* static */
	private static final Map<String, Object> DEFAULTPARAMVALS = new HashMap<String, Object>();
	private static final List<IParameter> RETURNVALUES = new ArrayList<IParameter>();
	private static final List<IParameter> PARAMETERS = new ArrayList<IParameter>();
	private static final Map<Class<? extends ISerializable>, Class<? extends IParameterDialog>> EDITORS = new HashMap<Class<? extends ISerializable>, Class<? extends IParameterDialog>>();
	private static final int NOT_MANY_ELEMS = 6;
	private static final int INFO_SEND_PERIOD = 35;
	private static final int SPECIES_TO_DRAW = 9;
	private static final int SPECIES_PER_ROW = 3;
	
	static {
		// init criteriums and allowed sets
		ICriterium gt2int = new GreaterThanIntegerCriterium(2);
		ICriterium bet01real = new IntervalRealCriterium(0, 1);
		List<Object> allowedMutations = new ArrayList<Object>();
		allowedMutations.add("Swap");
		allowedMutations.add("Scramble");
		allowedMutations.add("Insert");
		allowedMutations.add("Shift");
		allowedMutations.add("Inversion");
		
		// init params
		PARAMETERS.add(new SerializableParameter("Problem definition", "A list of cities and respective distances between them.", CityTable.class));
		PARAMETERS.add(new PrimitiveParameter("Population size", "Population size in each species.", ParameterTypes.INTEGER, gt2int));
		PARAMETERS.add(new PrimitiveParameter("Pressure", "Elimination pressure - percent of individuals to be eliminated each generation in each species. At least one individual shall always be removed, and not all will be removed.", ParameterTypes.REAL, bet01real));
		PARAMETERS.add(new PrimitiveParameter("Generations", "Number of generations to perform.", ParameterTypes.INTEGER, new PositiveIntegerCriterium()));
		PARAMETERS.add(new PrimitiveParameter("Subgenerations", "Number of generations to perform before moving on to the next species.", ParameterTypes.INTEGER, new PositiveIntegerCriterium()));
		PARAMETERS.add(new PrimitiveParameter("Mutation probability", "Probability that a mutation will occur on a newly created offspring.", ParameterTypes.REAL, bet01real));
		PARAMETERS.add(new PrimitiveParameter("Mutation operator", "Operator used for mutation of newly created offspring.", ParameterTypes.STRING, allowedMutations));
		PARAMETERS.add(new PrimitiveParameter("Species number", "A maximum number of species.", ParameterTypes.INTEGER, new PositiveIntegerCriterium()));
		PARAMETERS.add(new PrimitiveParameter("Representatives", "The number of representatives each species must offer for evaluating fitness. A value greater than population size is defaulted to the size.", ParameterTypes.INTEGER, new PositiveIntegerCriterium()));
		PARAMETERS.add(new PrimitiveParameter("Punishment multiplier", "How many times the punishment for city collision gets multiplied before being applied.", ParameterTypes.REAL));
		PARAMETERS.add(new PrimitiveParameter("CX probability multiplier", "Multiplier for probability that a permutation will choose a different city for it's city list. This probability is cumulative with each collision and it's age.", ParameterTypes.REAL, new PositiveRealCriterium()));
		PARAMETERS.add(new PrimitiveParameter("Adaptation period", "Period (in generations) between adapting the city lists in populations.", ParameterTypes.INTEGER, new PositiveIntegerCriterium()));
		PARAMETERS.add(new PrimitiveParameter("Expected cost", "The expected cost of the permutation to be found (0.0 if the cost is not known).", ParameterTypes.REAL));
		
		// init default values
		DEFAULTPARAMVALS.put("Problem definition", new CityTable());
		DEFAULTPARAMVALS.put("Population size", 40);
		DEFAULTPARAMVALS.put("Pressure", 0.3);
		DEFAULTPARAMVALS.put("Generations", 10000);
		DEFAULTPARAMVALS.put("Subgenerations", 10);
		DEFAULTPARAMVALS.put("Mutation probability", 0.03);
		DEFAULTPARAMVALS.put("Mutation operator", "Inversion");
		DEFAULTPARAMVALS.put("Species number", 3);
		DEFAULTPARAMVALS.put("Representatives", 6);
		DEFAULTPARAMVALS.put("Punishment multiplier", 0.05);
		DEFAULTPARAMVALS.put("CX probability multiplier", 0.2);
		DEFAULTPARAMVALS.put("Adaptation period", 10);
		DEFAULTPARAMVALS.put("Expected cost", 0.0);
		
		// init return values
		RETURNVALUES.add(new PrimitiveParameter("City permutation", "Optimal city permutation found during the run.", ParameterTypes.STRING));
		RETURNVALUES.add(new PrimitiveParameter("Cost", "The total cost of visiting all the cities in the optimal order.", ParameterTypes.REAL));
		RETURNVALUES.add(new PrimitiveParameter("Generations made", "Total number of generations performed before reaching iteration limit or expected solution cost.", ParameterTypes.INTEGER));
		RETURNVALUES.add(new PrimitiveParameter("Success measure", "Success of the run - 0.0 if there have been collisions, and 1.0 if there haven't been any collisions and expected cost has been reached.", ParameterTypes.REAL));
		RETURNVALUES.add(new PrimitiveParameter("Collisions", "Total number of collisions (cities appearing twice within the permutation).", ParameterTypes.INTEGER));
		
		// init editors
		EDITORS.put(CityTable.class, CitySelectPane.class);
	}

	/* private */
	private BitSet collisioned;
	private Random rand;
	private double punishment;
	private CircularQueue<Integer> speciesDrawQueue;
	
	/* ctors */

	public CCGATSP() {
		super(PARAMETERS, RETURNVALUES, DEFAULTPARAMVALS);
	}
	
	/* methods */

	public boolean doesReturnInfoDuringRun() {
		return true;
	}

	public List<String> getAuthors() {
		List<String> lst = new ArrayList<String>();
		
		lst.add("Aleksandar Prokopec");
		
		return lst;
	}

	public String getDescription() {
		return "Cooperative coevolutionary genetic algorithm works by decomposing the problem into " +
				"several subproblems, which are then solved separately by more species, each responsible " +
				"for one subproblem. Species interact by sharing fitness. This particular implementation solves the Travelling salesman problem.";
	}

	public Map<Class<? extends ISerializable>, Class<? extends IParameterDialog>> getEditors() {
		return EDITORS;
	}

	public String getExtensiveInfo() {
		return "This algorithm is inspired by De Jong's and Potter's work, and tries to use cooperative coevolution for permutation " +
				"type problems, namely the TSP. It works by dividing the cities into groups, where each group belongs to a certain " +
				"species (subpopulation). Individuals from species S are then evaluated by selecting the best representatives from the " +
				"other species, connecting them to form a complete permutation, and then calculating the path length. If there are repeating cities " +
				"in this complete permutation, the individual is punished by increasing it's cost. This punishment is proportional " +
				"to the average cost in the population in question.<br/>" +
				"It also uses a slightly modified order crossover, which takes into account that two parents may not in fact have " +
				"the same cities in their city list. It uses generational elimination and standard mutation operators.<br/>" + 
				"Every now and then, city exchange mutation is performed. This mutation changes a city in a permutation's city " +
				"list with another city. Permutations that have more collisions, and older collisions, also have a greater chance " +
				"of exchanging cities. In addition, this mutation favours cities with more collisions and older collisions, and these are " +
				"selected more often.<br/>" + 
				"Unfortunately, this experiment has failed (populations do not actually converge in most cases) and will thus not be described " +
				"in detail here.";
	}
	public List<String> getLiterature() {
		List<String> lst = new ArrayList<String>();
		
		lst.add("A. Eiben, J. Smith: <i>Introduction to Evolutionary Computing</i>, 2003.");
		lst.add("M. A. Potter, K. A. De Jong: <i>A Cooperative Coevolutionary Approach to Function Optimization</i>, 1994.");
		lst.add("M. A. Potter, K. A. De Jong: <i>Cooperative Coevolution: An Architecture for Evolving Coadapted Subcomponents</i>, 2000.");
		
		return lst;
	}

	public String getName() {
		return "Cooperative coevolutionary TSP GA";
	}

	public boolean isNative() {
		return false;
	}

	@Override
	protected void runImplementation(Map<String, IValue> values, IInfoListener listener, Map<String, Object> retvals) {
		// extract parameters
		CityTable cities = values.get("Problem definition").value();
		Double pressure = values.get("Pressure").value();
		Integer generations = values.get("Generations").value();
		Integer subgenerations = values.get("Subgenerations").value();
		Integer popsize = values.get("Population size").value();
		Integer speciesNumber = values.get("Species number").value();
		Integer cperspec = cities.getCityNum() / speciesNumber + 1;
		if (cperspec > cities.getCityNum()) cperspec = cities.getCityNum();
		Integer repsnum = values.get("Representatives").value();
		if (repsnum > popsize) repsnum = popsize;
		Double mutprob = values.get("Mutation probability").value();
		String mutopname = values.get("Mutation operator").value();
		Double punmult = values.get("Punishment multiplier").value();
		Double cxprob = 1.0 / cities.getCityNum();
		Double cxpmult = values.get("CX probability multiplier").value();
		Integer adaptperiod = values.get("Adaptation period").value();
		Double expcost = values.get("Expected cost").value();
		
		// initialize misc
		int lastSendGeneration = 0;
		if (listener != null) listener.useCanvas(true);
		boolean firsttimedraw = true;
		IMutation lmutation = TSPUtility.createMutation(cperspec, mutopname, cities);
		IMutation smutation = null;
		int generationCounter = 0;
		int numToEliminate = (int)(pressure * popsize);
		if (numToEliminate <= 0) numToEliminate = 1;
		if (numToEliminate >= popsize) numToEliminate = popsize - 1;
		if (cperspec > cities.getCityNum()) cperspec = cities.getCityNum();
		int totalspecies = 0;
		collisioned = new BitSet(cities.getCityNum());
		rand = new Random();
		double[] rouletteWheel = new double[popsize];
		PartialOrderCrossover crossover = new PartialOrderCrossover();
		double maxpunish = (cities.getHeight() > cities.getWidth()) ? (cities.getHeight() / 2) : (cities.getWidth() / 2);
		punishment = maxpunish;
		
		// initialize species
		List<Specie> species = new ArrayList<Specie>();
		for (int totalcities = cities.getCityNum(); totalcities > 0;) {
			int citiesInThisSpecie = 0;
			if (totalcities >= cperspec) citiesInThisSpecie = cperspec;
			else {
				citiesInThisSpecie = totalcities;
				smutation = TSPUtility.createMutation(totalcities, mutopname, cities);
			}
			
			species.add(new Specie(citiesInThisSpecie, popsize, cities.getCityNum()));
			totalcities -= citiesInThisSpecie;
			totalspecies++;
		}
		speciesDrawQueue = new CircularQueue<Integer>((SPECIES_TO_DRAW < totalspecies) ? (SPECIES_TO_DRAW) : (totalspecies));
		
		// select representatives in each species
		List<PartialPermutation[]> representativeList = new ArrayList<PartialPermutation[]>();
		for (int i = 0; i < totalspecies; i++) {
			PartialPermutation[] reps = new PartialPermutation[repsnum];
			species.get(i).selectRandomRepresentatives(reps);
			representativeList.add(reps);
		}
		
		// repeat until termination condition
		for (; !terminate && generationCounter < generations; generationCounter += subgenerations) {
			// for each specie - perform GA, a couple of times
			for (int i = 0; i < totalspecies; i++) {
				for (int subgencounter = 0; subgencounter < subgenerations; subgencounter++) {
					Specie spec = species.get(i);
					PartialPermutation[] population = spec.getPopulation();
					
					// evaluate all individuals (punish for each collision and remember them)
					double mincost = Double.MAX_VALUE, minrealcost = Double.MAX_VALUE;
					double avg = 0.0;
					int bestpos = -1;
					for (int k = 0; k < population.length; k++) {
						double realcost = assignFitnessAndCollisions(i, repsnum, population[k], representativeList, cities);
						double cost = population[k].cost;
						if (cost < mincost) {
							mincost = cost;
							bestpos = k;
						}
						if (realcost < minrealcost) {
							minrealcost = realcost;
						}
						avg += realcost;
					}
					avg /= population.length;
					punishment = avg * punmult;
					spec.setAverageCost(avg);
					if (minrealcost < spec.getObservedMinimum()) {
						spec.resetTimeSinceImprovement();
						spec.setObservedMinimum(minrealcost);
					} else {
						spec.incTimeSinceImprovement();
					}
					
					// place best permutation on the beginning of the population
					PartialPermutation tmp = population[0];
					population[0] = population[bestpos];
					population[bestpos] = tmp;
	
					// build roulette wheel
					double roultotal = 0.0;
					for (int k = 0; k < population.length; k++) {
						double delta = population[k].cost - mincost;
						if (k == 0) delta = 0;
						rouletteWheel[k] = delta;
						roultotal += delta;
					}
					
					// perform one generation of GA
					// roulette wheel eliminate worst individuals
					int left = population.length;
					for (int k = 0; k < numToEliminate; k++) {
						double r = rand.nextDouble() * roultotal;
						int spinner = 0;
						while ((r > rouletteWheel[spinner] && spinner < left) || spinner == 0) {
							r -= rouletteWheel[spinner];
							spinner++;
						}
						
						// eliminate spinner-th individual
						rouletteWheel[spinner] = rouletteWheel[--left];
						population[spinner] = population[left];
					}
					
					// create offspring and mutate with a certain probability, evaluate them
					for (int k = left; k < population.length; k++) {
						// select 2 parents
						PartialPermutation first = population[rand.nextInt(left)];
						PartialPermutation second = population[rand.nextInt(left)];
						
						// crossover
						PartialPermutation child = PartialPermutation.createEmptyFromParents(first, second);
						crossover.crossover(first, second, child);
						
						// mutate with certain probability
						if (rand.nextDouble() < mutprob) {
							if (first.field.length == cperspec) lmutation.mutate(child, -1);
							else smutation.mutate(child, -1);
						}
						
						assignFitnessAndCollisions(i, repsnum, child, representativeList, cities);
						population[k] = child;
					}
					
					// choose representatives
					chooseRepresentatives(representativeList.get(i), population);
					
					// after a 'long time', perform adaptation
					if (spec.getTimeSinceImprovement() > adaptperiod && generationCounter != 0) {
						// apply city list mutation to each individual (calculate probability for this) when adaptation period elapses
						// DO NOT EXCHANGE CITIES IN ELITE MEMBER!! (k starts with 1)
						for (int k = 1; k < population.length; k++) {
							PartialPermutation p = population[k];
							double prob = cxprob;
							for (int j = 0; j < p.collisions.length; j++) {
								prob += p.collisions[j] * cxprob;
							}
							prob *= cxpmult;
							
							if (rand.nextDouble() < prob) {
								// mutate
								p.exchangeRandomCity(rand);
								
								// reevaluate if mutation occured
								assignFitnessAndCollisions(i, repsnum, p, representativeList, cities);
							}
						}
						
						spec.incTotalAdapts();
					}
				}
			}
			
			// send info if needed
			if (listener != null && (generationCounter - lastSendGeneration) > INFO_SEND_PERIOD) {
				lastSendGeneration = generationCounter;
				
				sendDrawInfo(listener, representativeList, cities, firsttimedraw);
				if (firsttimedraw) firsttimedraw = false;
				listener.setPercentage(1.0 * generationCounter / generations);
				listener.setProperty("Punishment", String.valueOf(punishment));
				listener.setProperty("Basic CX probability", String.valueOf(cxprob));
				listener.setProperty("CX probability multiplier", String.valueOf(cxprob));
				for (int p = 0; p < species.size(); p++) {
					listener.setProperty("Species " + p + " observed min", String.valueOf(species.get(p).getObservedMinimum()));
				}
				for (int p = 0; p < species.size(); p++) {
					listener.setProperty("Species " + p + " adaptations", String.valueOf(species.get(p).getTotalAdapts()));
				}
				for (int p = 0; p < species.size(); p++) {
					listener.setProperty("Species " + p + " avg. cost", String.valueOf(species.get(p).getAverageCost()));
				}
			}
		}
		
		// fill return value map
		// find best permutation in each species and combine them to form a complete permutation
		Permutation best = new Permutation(cities.getCityNum(), false);
		int pos = 0;
		for (Specie spec : species) {
			PartialPermutation p = findBest(spec.getPopulation());
			for (int i = 0; i < p.field.length; i++) {
				best.field[pos++] = p.lookuptable[p.field[i]];
			}
		}
		double cost = 0.0;
		BitSet taken = new BitSet(cities.getCityNum());
		int numberOfCollisions = 0;
		for (int i = 1; i < best.field.length; i++) {
			cost += cities.get(best.field[i - 1], best.field[i]);
			if (taken.get(best.field[i])) numberOfCollisions++;
			taken.set(best.field[i]);
		}
		cost += cities.get(best.field[best.field.length - 1], best.field[0]);
		if (taken.get(best.field[0])) numberOfCollisions++;
		
		retvals.put("City permutation", best.toString());
		retvals.put("Cost", cost);
		retvals.put("Generations made", generationCounter);
		retvals.put("Success measure", (numberOfCollisions == 0 && cost <= expcost) ? (1.0) : (0.0));
		retvals.put("Collisions", numberOfCollisions);
	}

	private PartialPermutation findBest(PartialPermutation[] population) {
		double mincost = Double.MAX_VALUE;
		PartialPermutation p = null;
		for (int i = 0; i < population.length; i++) {
			if (population[i].cost < mincost) {
				mincost = population[i].cost;
				p = population[i];
			}
		}
		return p;
	}

	private void chooseRepresentatives(PartialPermutation[] reps, PartialPermutation[] population) {
//		int bestreps = reps.length / 2 + 1;
		int bestreps = reps.length;
		Comparator<PartialPermutation> compi = new Comparator<PartialPermutation>() {
			public int compare(PartialPermutation o1, PartialPermutation o2) {
				if (o1.cost < o2.cost) return -1;
				if (o1.cost > o2.cost) return 1;
				return 0;
			}
		};
		
		if (bestreps > NOT_MANY_ELEMS) {
			Arrays.sort(population, compi);
			
			int i;
			for (i = 0; i < bestreps; i++) {
				reps[i] = population[i];
			}
			for (; i < reps.length; i++) {
				int r = rand.nextInt(population.length);
				reps[i] = population[r];
			}
		} else {
			PartialPermutation[] tmp = new PartialPermutation[bestreps];
			
			for (int i = 0; i < bestreps; i++) {
				tmp[i] = population[i];
			}
			Arrays.sort(tmp, compi);
			for (int i = bestreps; i < population.length; i++) {
				if (population[i].cost < tmp[bestreps - 1].cost) {
					tmp[bestreps - 1] = population[i];
					Arrays.sort(tmp, compi);
				}
			}
			
			int i;
			for (i = 0; i < bestreps; i++) reps[i] = tmp[i];
			for (; i < reps.length; i++) reps[i] = population[rand.nextInt(population.length)];
		}
	}
	
	private double assignFitnessAndCollisions(int speciesIndex, int samplingTimes, PartialPermutation partialPermutation, List<PartialPermutation[]> representativeList, CityTable cities) {
		// choose a random representative from each of the species
		// go from city to city, sum the distances, track collisions and assign penalties for collisions
		collisioned.clear();
		Arrays.fill(partialPermutation.collisions, 0);
		
		double sum = 0.0, sumWithPunish = 0.0;
		for (int scount = 0; scount < samplingTimes; scount++) {
			int first = -1, last = -1, index = -1;
			for (PartialPermutation[] reps : representativeList) {
				index++;
				
				// choose a random representative (or actual permutation if current species is in question)
				// and sum the distances
				if (index == speciesIndex) {
					PartialPermutation perm = partialPermutation;
					
					// save first city ever if such hasn't been saved
					if (first == -1) first = perm.lookuptable[perm.field[0]];
					
					// sum distance from the last city to the current city
					int lastcity = perm.lookuptable[perm.field[0]];
					if (last != -1) {
						int current = lastcity;
						sum += cities.get(last, current);
					}
					
					// sum distances, don't track collisions
					for (int i = 1; i < perm.field.length; i++) {
						int currentcity = perm.lookuptable[perm.field[i]];
						sum += cities.get(lastcity, currentcity);
						
						lastcity = currentcity;
					}
					
					// save last city
					last = lastcity;
				} else {
					PartialPermutation perm = reps[rand.nextInt(reps.length)];
					
					// save first city ever if such hasn't been saved
					if (first == -1) first = perm.lookuptable[perm.field[0]];
					
					// sum distance from the last city to the current city
					int lastcity = perm.lookuptable[perm.field[0]];
					if (last != -1) {
						int current = lastcity;
						sum += cities.get(last, current);
					}
					
					// track collision with first city in this permutation
					collisioned.set(lastcity);
					
					// sum distances, track collisions
					for (int i = 1; i < perm.field.length; i++) {
						int currentcity = perm.lookuptable[perm.field[i]];
						sum += cities.get(lastcity, currentcity);
						
						lastcity = currentcity;
						
						// track collision
						collisioned.set(currentcity);
					}
					
					// save last city
					last = lastcity;
				}
			}
			
			// add distance from last city to the very first city
			sum += cities.get(first, last);
			
			// assign collisions and apply punishment
			sumWithPunish += sum;
			for (int i = 0; i < partialPermutation.collisions.length; i++) {
				if (collisioned.get(i) && partialPermutation.takencities.get(i)) {
					partialPermutation.collisions[i]++;
					
					sumWithPunish += partialPermutation.collisions[i] * punishment;
				}
			}
		}
		
		// set sum to cost
		partialPermutation.cost = sumWithPunish / samplingTimes;
		
		return sum / samplingTimes;
	}
	
	private void sendDrawInfo(IInfoListener listener, final List<PartialPermutation[]> repsList, final CityTable cities, final boolean firsttime) {
		// update list of species that will be drawn this time
		Integer last = speciesDrawQueue.last();
		if (last == null) for (int i = 0; i < speciesDrawQueue.capacity(); i++) {
			speciesDrawQueue.add(i);
		} else {
			int current = (last + 1) % repsList.size();
			speciesDrawQueue.add(current);
		}
		
		// draw the best individual in each of the species, noting collisions as red dots
		final int numcities = cities.getCityNum();
		listener.paint(new IPainter() {
			public void paint(ICanvas canvas) {
				canvas.clearCanvas();
				
				if (firsttime) canvas.setCanvasSize(640, 640);
				int i = 0, row = 0, column = 0;
				BitSet colls = new BitSet(numcities);
				for (Integer speciesIndex : speciesDrawQueue) {
					// draw this permutation
					PartialPermutation best = repsList.get(speciesIndex)[0];
					
					// find collisions
					colls.clear();
					for (int k = 0; k < best.lookuptable.length; k++) {
						int city = best.lookuptable[k];
						for (int j = 0; j < repsList.size(); j++) {
							if (j == speciesIndex) continue;
							if (repsList.get(j)[0].takencities.get(city)) colls.set(city);
						}
					}
					
					drawPermutation(canvas, cities, best, colls,
							column * cities.getWidth() / SPECIES_PER_ROW,
							row * (cities.getHeight() / SPECIES_PER_ROW + 10),
							1.0 / SPECIES_PER_ROW, 1.0 / SPECIES_PER_ROW,
							speciesIndex);
					
					column = (column + 1) % SPECIES_PER_ROW;
					if (column == 0) row++;
					i++;
				}
				
				canvas.flip();
			}
		});
	}
	
	public void drawPermutation(ICanvas canvas, CityTable cities, PartialPermutation p,
			BitSet notvalid, int xoff, int yoff, double xsc, double ysc, int specIndex) {
		boolean firstcity = true;
		double xlast = 0.0, ylast = 0.0;
		for (int i = 0; i < p.field.length; i++) {
			int city = p.lookuptable[p.field[i]];
			double x = cities.getCityX(city) * xsc + xoff;
			double y = cities.getCityY(city) * ysc + yoff;
			
			// connect with previous
			canvas.setDrawColor(160, 160, 160);
			if (firstcity) firstcity = false;
			else {
				canvas.drawLine((int)x, (int)y, (int)xlast, (int)ylast);
			}
			
			// draw city dot
			if (notvalid.get(city)) canvas.setFillColor(225, 0, 0);
			else canvas.setFillColor(100, 100, 100);
			canvas.fillOval((int)x - 2, (int)y - 2, 5, 5);
			
			xlast = x;
			ylast = y;
		}
		
		canvas.setDrawColor(200, 200, 200);
		canvas.drawText("Species " + specIndex, xoff, yoff, false);
	}

	public Map<Class<? extends ISerializable>, Class<? extends IValueRenderer>> getRenderers() {
		return null;
	}

}














