package hr.fer.zemris.ga_framework.algorithms.tsp;

import hr.fer.zemris.ga_framework.model.AlgorithmTerminatedException;
import hr.fer.zemris.ga_framework.model.IAlgorithm;
import hr.fer.zemris.ga_framework.model.ICriterium;
import hr.fer.zemris.ga_framework.model.IInfoListener;
import hr.fer.zemris.ga_framework.model.IParameter;
import hr.fer.zemris.ga_framework.model.IParameterDialog;
import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.IValue;
import hr.fer.zemris.ga_framework.model.IValueRenderer;
import hr.fer.zemris.ga_framework.model.ParameterTypes;
import hr.fer.zemris.ga_framework.model.impl.ObjectList;
import hr.fer.zemris.ga_framework.model.impl.Value;
import hr.fer.zemris.ga_framework.model.impl.criteriums.GreaterThanIntegerCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.IntervalRealCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.PositiveIntegerCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.PositiveRealCriterium;
import hr.fer.zemris.ga_framework.model.impl.parameters.ListParameter;
import hr.fer.zemris.ga_framework.model.impl.parameters.PrimitiveParameter;
import hr.fer.zemris.ga_framework.model.impl.parameters.SerializableParameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



public class AdaptiveTournamentTSP extends AbstractTSP {
	
	/* static fields */
	private static final int INFO_SEND_PERIOD = 6000;
	private static final int PERCENTAGE_SEND_PERIOD = 12000;
	private static final List<IParameter> PARAMETERS = new ArrayList<IParameter>();
	private static final Map<String, IParameter> PARAMETERMAP = new HashMap<String, IParameter>();
	private static final List<IParameter> RETURNVALUES = new ArrayList<IParameter>();
	private static final Map<String, IParameter> RETVALMAP = new HashMap<String, IParameter>();
	private static final Map<Class<? extends ISerializable>, Class<? extends IParameterDialog>> EDITORS = 
		new HashMap<Class<? extends ISerializable>, Class<? extends IParameterDialog>>();
	
	static {
		ICriterium positiveInt = new PositiveIntegerCriterium();
		ICriterium positiveReal = new PositiveRealCriterium();
		ICriterium greaterThan2 = new GreaterThanIntegerCriterium(2);
		ICriterium greaterThan1 = new GreaterThanIntegerCriterium(1);
		ICriterium between0and1 = new IntervalRealCriterium(0.0, 1.0);
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
		mutations.add("MultiShift");
		mutations.add("SwapSeg");
		mutations.add("2-inversion");
		mutations.add("2 opt");
		mutations.add("GShift");
		List<Object> adaptiveMechs = new ArrayList<Object>();
		adaptiveMechs.add("None");
		adaptiveMechs.add("Mutation probability varying");
		adaptiveMechs.add("Adaptive operator cycling");
		adaptiveMechs.add("Adaptive operator cycling 2");
		adaptiveMechs.add("Mutation operator statistics");
		adaptiveMechs.add("Population migrations");
		
		// init params
		PARAMETERS.add(new SerializableParameter("Problem definition", "A list of cities and respective distances between them.", CityTable.class));
		PARAMETERS.add(new PrimitiveParameter("Population size", "Size of the population.", ParameterTypes.INTEGER, greaterThan2));
		PARAMETERS.add(new PrimitiveParameter("Tournament size", "Number of individuals selected for the tournament.", ParameterTypes.INTEGER, positiveInt));
		PARAMETERS.add(new PrimitiveParameter("Pressure", "Number of individuals eliminated in each tournament. Entering a value greater or equal to tournament size will always result in one surviving individual.", ParameterTypes.INTEGER, positiveInt));
		PARAMETERS.add(new PrimitiveParameter("Crossover operator", "Operator used for crossover.", ParameterTypes.STRING, crossovers));
		PARAMETERS.add(new PrimitiveParameter("Mutation operator", "First operator used for mutation.", ParameterTypes.STRING, mutations));
		PARAMETERS.add(new PrimitiveParameter("Mutation probability", "Starting probability that mutation will occur on a newly created offspring.", ParameterTypes.REAL, between0and1));
		PARAMETERS.add(new PrimitiveParameter("Iterations", "Maximum number of iterations (tournament selections) algorithm will perform.", ParameterTypes.INTEGER, positiveInt));
		PARAMETERS.add(new PrimitiveParameter("Expected cost", "Expected permutation cost. Once a solution with a cost less than the specified is found, " +
				"the algorithm will stop. If no expected cost is known, this may be set to 0.0.", ParameterTypes.REAL, positiveReal));
		PARAMETERS.add(new PrimitiveParameter("Adaptation mechanism", "Mechanism used for parameter adaptation.", ParameterTypes.STRING, adaptiveMechs));
		PARAMETERS.add(new PrimitiveParameter("Adaptation period", "Number of iterations that must pass without an improvement in fitness, before a different mutation operator is chosen, mutation probability changed, etc. (depends on adaptation mechanism).", ParameterTypes.INTEGER, positiveInt));
		PARAMETERS.add(new ListParameter("Mutation operators", "A list of mutation operators that will be switched between. If empty, no operator switching will be used.", ParameterTypes.STRING));
		PARAMETERS.add(new PrimitiveParameter("Mutation probability increase", "An increase in mutation probability if no fitness improvement happens.", ParameterTypes.REAL, positiveReal));
		PARAMETERS.add(new PrimitiveParameter("Iterations per population", "Number of iterations to perform before switching to the next population.", ParameterTypes.INTEGER, greaterThan1));
		PARAMETERS.add(new PrimitiveParameter("Migration percentage", "Percentage of the population to exchange during migrations.", ParameterTypes.REAL, between0and1));
		
		for (IParameter p : PARAMETERS) {
			PARAMETERMAP.put(p.getName(), p);
		}
		
		// init return values
		RETURNVALUES.add(new PrimitiveParameter("City permutation", "Optimal city permutation found during the run.", ParameterTypes.STRING));
		RETURNVALUES.add(new PrimitiveParameter("Cost", "The total cost of visiting all the cities in the optimal order.", ParameterTypes.REAL));
		RETURNVALUES.add(new PrimitiveParameter("Iterations made", "Total number of iterations performed before reaching iteration limit or expected solution cost.", ParameterTypes.INTEGER));
		RETURNVALUES.add(new PrimitiveParameter("Success measure", "Success of the run - 0.0 if expected cost isn't reached, and 1.0 if it is.", ParameterTypes.REAL));
		
		for (IParameter p : RETURNVALUES) {
			RETVALMAP.put(p.getName(), p);
		}
		
		// init editor map
		EDITORS.put(CityTable.class, CitySelectPane.class);
	}
	
	/* private fields */
	private volatile boolean running, terminate;
	
	/* ctors */
	
	public AdaptiveTournamentTSP() {
		running = false;
		terminate = false;
	}
	
	/* methods */

	public boolean doesReturnInfoDuringRun() {
		return true;
	}

	public Map<String, IValue> getDefaultValues() {
		Map<String, IValue> map = new HashMap<String, IValue>();
		
		map.put(PARAMETERS.get(0).getName(), new Value(new CityTable(1, 620, 330), PARAMETERS.get(0)));
		map.put(PARAMETERS.get(1).getName(), new Value(200, PARAMETERS.get(1)));
		map.put(PARAMETERS.get(2).getName(), new Value(3, PARAMETERS.get(2)));
		map.put(PARAMETERS.get(3).getName(), new Value(1, PARAMETERS.get(3)));
		map.put(PARAMETERS.get(4).getName(), new Value("Order", PARAMETERS.get(4)));
		map.put(PARAMETERS.get(5).getName(), new Value("Inversion", PARAMETERS.get(5)));
		map.put(PARAMETERS.get(6).getName(), new Value(0.25, PARAMETERS.get(6)));
		map.put(PARAMETERS.get(7).getName(), new Value(400000, PARAMETERS.get(7)));
		map.put(PARAMETERS.get(8).getName(), new Value(0.0, PARAMETERS.get(8)));
		map.put(PARAMETERS.get(9).getName(), new Value("None", PARAMETERS.get(9)));
		map.put(PARAMETERS.get(10).getName(), new Value(25000, PARAMETERS.get(10)));
		map.put(PARAMETERS.get(11).getName(), new Value(new ObjectList(ParameterTypes.STRING, PARAMETERS.get(5).getAllowed()), PARAMETERS.get(11)));
		map.put(PARAMETERS.get(12).getName(), new Value(0.025, PARAMETERS.get(12)));
		map.put(PARAMETERS.get(13).getName(), new Value(50000, PARAMETERS.get(13)));
		map.put(PARAMETERS.get(14).getName(), new Value(0.05, PARAMETERS.get(14)));
		
		return map;
	}

	public String getDescription() {
		return "Adaptive genetic algorithm for solving the Travelling salesman problem. Given the list " +
				"of cities and their mutual distances, this algorithm finds " +
				"the permutation of the cities so that the sum of the distances " +
				"between the neighbouring cities within the permutation is minimal. " +
				"The algorithm uses tournament elimination, with a wide choice of crossover " +
				"and mutation operators. It also implements a variety of adaptive mechanisms.";
	}

	public String getExtensiveInfo() {
		InputStream stream = this.getClass().getResourceAsStream("AdaptiveTournamentTSP.html");
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		StringBuffer buffer = new StringBuffer();
		String line;
		try {
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e) {
			return "Could not load info from file.";
		}
		return buffer.toString();
	}

	public String getName() {
		return "Adaptive Tournament TSP GA";
	}

	public List<String> getAuthors() {
		List<String> lst = new ArrayList<String>();
		lst.add("Aleksandar Prokopec");
		return lst;
	}
	
	public IParameter getParameter(String name) {
		return PARAMETERMAP.get(name);
	}

	public List<IParameter> getParameters() {
		return PARAMETERS;
	}

	public List<IParameter> getReturnValues() {
		return RETURNVALUES;
	}

	public Map<String, IValue> runAlgorithm(Map<String, IValue> values, IInfoListener listener) {
		Map<String, IValue> retmap = null;
		
		try {
			terminate = false;
			running = true;
			retmap = new HashMap<String, IValue>();
			
			// extract values from parameter-value-map
			Integer iterations = values.get("Iterations").value();
			Double expectedcost = values.get("Expected cost").value();
			Integer popsize = values.get("Population size").value();
			Integer toursize = values.get("Tournament size").value();
			Integer toeliminate = values.get("Pressure").value();
			String crossoverop = values.get("Crossover operator").value();
			String mutationop = values.get("Mutation operator").value();
			if (toeliminate >= toursize) toeliminate = toursize - 1;
			Double mutprob = values.get("Mutation probability").value();
			final CityTable cities = values.get("Problem definition").value();
			ObjectList mutcycle_objlist = values.get("Mutation operators").value();
			ArrayList<Object> mutcycle = mutcycle_objlist.getOrdinaryListCopy();
			Integer adaptperiod = values.get("Adaptation period").value();
			String adaptscheme = values.get("Adaptation mechanism").value();
			Double mutincreaserate = values.get("Mutation probability increase").value();
			Integer itersPerPop = values.get("Iterations per population").value();
			Double migrationPercentage = values.get("Migration percentage").value();
			int permlength = cities.getCityNum();
			
			// initialize
			int modified_send_info_per = INFO_SEND_PERIOD + 10 * cities.getCityNum();
			Comparator<Permutation> comparePerm = new Comparator<Permutation>() {
				public int compare(Permutation p1, Permutation p2) {
					if (p1.cost > p2.cost) return 1;
					if (p1.cost < p2.cost) return -1;
					return 0;
				}
			};
			final Random rand = new Random();
			ICrossover crossover = TSPUtility.createCrossover(permlength, crossoverop);
			IMutation mutation = TSPUtility.createMutation(permlength, mutationop, cities);
			Permutation[] population = null;
			Permutation[] tournament = new Permutation[toursize];
			temptable = cities;
			
			// initialize adaptation mechanisms
			int adaptcode = 0;
			if (adaptscheme.equals("Mutation operator statistics")) {
				adaptcode = 1;
			} else if (adaptscheme.equals("Mutation probability varying")) {
				adaptcode = 2;
			} else if (adaptscheme.equals("Adaptive operator cycling")) {
				adaptcode = 3;
			} else if (adaptscheme.equals("Population migrations")) {
				adaptcode = 4;
			} else if (adaptscheme.equals("Adaptive operator cycling 2")) {
				adaptcode = 5;
			}
			int currentMutOpIndex = -1, totalMutOperators = mutcycle.size();
			int itersSinceImprovement = 0;
			if (mutcycle.size() == 0) mutcycle.add(mutationop);
			List<IMutation> mutOpsCycle = new ArrayList<IMutation>();
			for (Object mutopname : mutcycle) {
				mutOpsCycle.add(TSPUtility.createMutation(permlength, (String) mutopname, cities));
			}
			double startingMutProb = mutprob;
			int successfulMutations = 0, totalMutations = 0, adaptationCounter = 0;
			int[] successmut = null, totalmut = null;
			double[] mutopprobs = null;
			if (adaptcode == 1) {
				if (totalMutOperators > 0) {
					successmut = new int[totalMutOperators];
					totalmut = new int[totalMutOperators];
					mutopprobs = new double[totalMutOperators];
					for (int i = 0; i < totalMutOperators; i++) {
						mutopprobs[i] = 1.0 / totalMutOperators;
					}
					currentMutOpIndex = 0;
					mutation = mutOpsCycle.get(currentMutOpIndex);
				}
			}
			List<Permutation[]> populations = null;
			List<Permutation> bestperms = null, beforeperms = null;
			List<Double> bestvals = null;
			Permutation[] migrationpool = null;
			int currentPop = 0;
			Integer totalPops = mutOpsCycle.size();
			if (adaptcode == 4) {
				populations = new ArrayList<Permutation[]>();
				bestperms = new ArrayList<Permutation>();
				beforeperms = new ArrayList<Permutation>();
				bestvals = new ArrayList<Double>();
				migrationpool = new Permutation[(int)(popsize * totalPops * migrationPercentage + totalPops + 1)];
				mutation = mutOpsCycle.get(0);
			}
			int popToMigrate = (int) (popsize * migrationPercentage);
			if (popToMigrate <= 0) popToMigrate = 1;
			if (popToMigrate > popsize) popToMigrate = popsize;
			boolean mustRedraw = false;
			int migrationCounter = 0, iterationsSinceLastMigration = 0;
			
			// initialize and evaluate population, keep track of the best
			double bestval = Double.MAX_VALUE;
			Permutation bestperm = null, beforeperm = null;
			switch (adaptcode) {
			case 4:
				for (int popindex = 0; popindex < totalPops; popindex++) {
					bestval = Double.MAX_VALUE;
					bestperm = null;
					beforeperm = null;
					population = new Permutation[popsize];
					for (int i = 0; i < popsize; i++) {
						population[i] = new Permutation(permlength, true);
						population[i].cost = fitness(population[i]);
						if (population[i].cost < bestval) {
							bestval = population[i].cost;
							beforeperm = bestperm;
							bestperm = population[i];
						}
					}
					populations.add(population);
					bestperms.add(bestperm);
					beforeperms.add(beforeperm);
					bestvals.add(bestval);
				}
				population = populations.get(0);
				bestperm = bestperms.get(0);
				beforeperm = beforeperms.get(0);
				bestval = bestvals.get(0);
				break;
			default:
				population = new Permutation[popsize];
				for (int i = 0; i < popsize; i++) {
					population[i] = new Permutation(permlength, true);
					population[i].cost = fitness(population[i]);
					if (population[i].cost < bestval) {
						bestval = population[i].cost;
						beforeperm = bestperm;
						bestperm = population[i];
					}
				}
			}
			
			// send info if that's required
			if (listener != null) {
				listener.println("ALGORITHM STARTING.");
				sendInfoAboutBest(listener, bestperm, bestval);
				initCanvas(listener, cities);
				listener.println("Zoom factor used: " + getZoomFactor());
			}
			
			// main loop of the genetic algorithm
			int counter = 0;
			for (; !terminate && counter < iterations && bestval > expectedcost; counter++) {
				// select tournament size individuals, put worst to end
				for (int i = 0; i < toursize; i++) {
					int randpos = rand.nextInt(popsize - i);
					tournament[i] = population[randpos];
					population[randpos] = population[popsize - i - 1];
				}
				
				// sort tournament elements
				Arrays.sort(tournament, comparePerm);
				
				// eliminate toeliminate worst individuals among them
				for (int parentnum = toursize - toeliminate, j = parentnum; j < toursize; j++) {
					// select parents from the remaining individuals
					Permutation firstparent = tournament[rand.nextInt(parentnum)];
					Permutation secondparent = tournament[rand.nextInt(parentnum)];
					
					// create offspring
					Permutation child = Permutation.createEmptyPermutation(permlength);
					crossover.crossover(firstparent, secondparent, child);
					
					// perform mutation with some probability and evaluate child
					double fitnessBeforeMutation = 0.0;
					switch (adaptcode) {
					case 1:
						int randomMutOpIndex = 0;
						if (rand.nextDouble() < mutprob) {
							double rop = rand.nextDouble();
							while (randomMutOpIndex < totalMutOperators && rop > mutopprobs[randomMutOpIndex]) {
								rop -= mutopprobs[randomMutOpIndex];
								randomMutOpIndex++;
							}
							if (randomMutOpIndex >= totalMutOperators) randomMutOpIndex = totalMutOperators - 1;
							IMutation randomMutation = mutOpsCycle.get(randomMutOpIndex);
							totalmut[randomMutOpIndex]++;
							
							fitnessBeforeMutation = fitness(child);
							randomMutation.mutate(child, -1);
						}
						child.cost = fitness(child);
						if (child.cost < fitnessBeforeMutation) successmut[randomMutOpIndex]++;
						break;
					case 3:
					case 5:
						if (rand.nextDouble() < mutprob) {
							fitnessBeforeMutation = fitness(child);
							mutation.mutate(child, -1);
							totalMutations++;
						}
						child.cost = fitness(child);
						if (child.cost < fitnessBeforeMutation) successfulMutations++;
						break;
					default:
						if (rand.nextDouble() < mutprob) mutation.mutate(child, -1);
						child.cost = fitness(child);
					}
					
					// keep track of the best
					if (child.cost < bestval) {
						bestval = child.cost;
						beforeperm = bestperm;
						bestperm = child;
						itersSinceImprovement = 0;
					}
					
					// put child to tournament
					tournament[j] = child;
				}
				
				// add offspring and tournament members back to population
				for (int i = 0; i < toursize; i++) {
					population[popsize - i - 1] = tournament[i];
				}
				
				// perform adaptation
				switch (adaptcode) {
				case 1:
					// mutation operator statistics
					if (totalMutOperators > 0) {
						if (adaptationCounter > adaptperiod) {
							// reanalyze operator probabilities
							double total = 0.0;
							for (int i = 0; i < totalMutOperators; i++) {
								if (totalmut[i] != 0) mutopprobs[i] = 1.0 * successmut[i] / totalmut[i];
								mutopprobs[i] += (rand.nextDouble() - 0.5) * 0.02;
								if (mutopprobs[i] < 0) mutopprobs[i] = 0;
								total += mutopprobs[i];
							}
							if (total > 0.0) for (int i = 0; i < totalMutOperators; i++) mutopprobs[i] /= total;
							
							adaptationCounter = 0;
						} else {
							adaptationCounter++;
						}
					}
					break;
				case 2:
					// mutation probability varying
					if (itersSinceImprovement > adaptperiod) {
						mutprob += mutincreaserate;
						if (mutprob > 1.0) mutprob = 1.0;
						itersSinceImprovement = 0;
					} else if (itersSinceImprovement == 0) {
						mutprob -= mutincreaserate * 8;
						if (mutprob < startingMutProb) mutprob = startingMutProb;
					}
					break;
				case 3:
					// adaptive operator cycling
					if ((totalMutations != 0 && totalMutOperators > 0) && adaptationCounter > adaptperiod) {
						double succMutRatio = 1.0 * successfulMutations / totalMutations;
						if (rand.nextDouble() < (Math.cos(succMutRatio * Math.PI) / 2 + 0.5)) {
							currentMutOpIndex = (currentMutOpIndex + 1) % totalMutOperators;
							mutation = mutOpsCycle.get(currentMutOpIndex);
						}
						
						totalMutations = 0;
						successfulMutations = 0;
						adaptationCounter = 0;
						itersSinceImprovement = 0;
					} else {
						adaptationCounter++;
					}
					break;
				case 4:
					// population migrating
					if (adaptationCounter > itersPerPop) {
						// save parameters in current population
						bestperms.set(currentPop, bestperm);
						beforeperms.set(currentPop, beforeperm);
						bestvals.set(currentPop, bestval);
						
						// switch population currently being updated
						currentPop = (currentPop + 1) % totalPops;
						population = populations.get(currentPop);
						bestperm = bestperms.get(currentPop);
						beforeperm = beforeperms.get(currentPop);
						bestval = bestvals.get(currentPop);
						
						// change mutation operator
						mutation = mutOpsCycle.get(currentPop);
						
						// set redraw flag
						mustRedraw = true;
						
						// reset iterations since improvement
						adaptationCounter = 0;
					} else {
						adaptationCounter++;
					}
					
					if (iterationsSinceLastMigration > adaptperiod) {
						// exchange a part of the population
						// fill the migration pool
						int migpoolindex = 0;
						for (int popindex = 0; popindex < totalPops; popindex++) {
							int left = popsize;
							Permutation[] currentPopulation = populations.get(popindex);
							for (int i = 0; i < popToMigrate; i++) {
								// extract a member
								int randnum = rand.nextInt(left);
								Permutation p = currentPopulation[randnum];
								currentPopulation[randnum] = currentPopulation[--left];
								
								// put it to migration pool
								migrationpool[migpoolindex++] = p;
							}
						}
						
						// divide the pool between the populations evenly
						for (int popindex = 0; popindex < totalPops; popindex++) {
							int lastfree = popsize - popToMigrate;
							Permutation[] currentPopulation = populations.get(popindex);
							for (int i = 0; i < popToMigrate; i++) {
								// select a random member from the pool
								int randnum = rand.nextInt(migpoolindex);
								Permutation p = migrationpool[randnum];
								migrationpool[randnum] = migrationpool[--migpoolindex];
								
								// put it into the current population
								currentPopulation[lastfree++] = p;
							}
						}
						
						migrationCounter++;
						iterationsSinceLastMigration = 0;
					} else {
						iterationsSinceLastMigration++;
					}
					break;
				case 5:
					// adaptive operator cycling 2
					if ((totalMutations != 0 && totalMutOperators > 0) && itersSinceImprovement > adaptperiod) {
						double succMutRatio = 1.0 * successfulMutations / totalMutations;
						if (rand.nextDouble() < (Math.cos(succMutRatio * Math.PI) / 2 + 0.5)) {
							currentMutOpIndex = (currentMutOpIndex + 1) % totalMutOperators;
							mutation = mutOpsCycle.get(currentMutOpIndex);
						}
						
						totalMutations = 0;
						successfulMutations = 0;
						adaptationCounter = 0;
						itersSinceImprovement = 0;
					} else {
						adaptationCounter++;
					}
					break;
				}
				
				// update various counters
				itersSinceImprovement++;
				
				// send info if that is required
				if (listener != null && (counter % modified_send_info_per == 0)) {
					// redraw
					switch (adaptcode) {
					case 4:
						if (!bestperm.equals(beforeperm) || mustRedraw) {
							sendInfoAboutBest(listener, bestperm, bestval);
							redrawImage(listener, bestperm, cities, "Evolving population " + (currentPop + 1) + ", " + mutcycle.get(currentPop) + " mutation");
							beforeperm = bestperm;
							mustRedraw = false;
						}
						break;
					default:
						if (!bestperm.equals(beforeperm)) {
							sendInfoAboutBest(listener, bestperm, bestval);
							redrawImage(listener, bestperm, cities, "");
							beforeperm = bestperm;
						}
					}
					
					// set listener properties
					switch (adaptcode) {
					case 1:
						for (int i = 0; i < totalMutOperators; i++) {
							listener.setProperty(mutcycle.get(i) + " probability", String.valueOf(mutopprobs[i]));
						}
						break;
					case 2:
						listener.setProperty("Mutation probability", String.valueOf(mutprob));
						break;
					case 3:
					case 5:
						listener.setProperty("Mutation operator", (currentMutOpIndex > -1) ? (String) mutcycle.get(currentMutOpIndex) : mutationop);
						if (totalMutations != 0) listener.setProperty("Successful mutations ratio", String.valueOf(1.0 * successfulMutations / totalMutations));
						break;
					case 4:
						listener.setProperty("Evolving population", (currentPop + 1) + "/" + totalPops);
						listener.setProperty("Solutions per migration", String.valueOf(popToMigrate));
						listener.setProperty("Total migrations", String.valueOf(migrationCounter));
						listener.setProperty("Population mutation operator", (String) mutcycle.get(currentPop));
						break;
					}
				}
				if (listener != null && (counter % PERCENTAGE_SEND_PERIOD == 0)) {
					sendInfoPercentage(listener, counter * 1.0 / iterations);
				}
			}
			
			// perform adaptive operations at the end
			switch (adaptcode) {
			case 4:
				// save current info before searching
				bestperms.set(currentPop, bestperm);
				beforeperms.set(currentPop, beforeperm);
				bestvals.set(currentPop, bestval);
				
				double bval = Double.MAX_VALUE;
				for (int i = 0; i < totalPops; i++) {
					if (bestvals.get(i) < bval) {
						bval = bestvals.get(i);
						bestval = bval;
						bestperm = bestperms.get(i);
					}
				}
				break;
			}
			
			// prepare return values
			retmap.put(RETURNVALUES.get(0).getName(), new Value(bestperm.toString(), RETURNVALUES.get(0)));
			retmap.put(RETURNVALUES.get(1).getName(), new Value(bestperm.cost, RETURNVALUES.get(1)));
			retmap.put(RETURNVALUES.get(2).getName(), new Value(counter, RETURNVALUES.get(2)));
			retmap.put(RETURNVALUES.get(3).getName(), new Value((bestperm.cost <= expectedcost) ? 1.0 : 0.0, RETURNVALUES.get(3)));
			
			// send info if that is required
			if (listener != null) {
				sendInfoAboutBest(listener, bestperm, bestval);
				redrawImage(listener, bestperm, cities, "");
				beforeperm = bestperm;
				listener.println("ALGORITHM ENDING.");
			}
		} finally {
			running = false;
			if (terminate) {
				throw new AlgorithmTerminatedException();
			}
		}
		
		return retmap;
	}
	
	public Map<Class<? extends ISerializable>, Class<? extends IParameterDialog>> getEditors() {
		return EDITORS;
	}

	public boolean isPausable() {
		return false;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isSaveable() {
		return false;
	}

	public void setPaused(boolean on) {
		throw new UnsupportedOperationException("Pause not available.");
	}

	public void load(String s) {
		throw new UnsupportedOperationException("Load not available.");
	}

	public String save() {
		throw new UnsupportedOperationException("Save not available.");
	}

	public void haltAlgorithm() {
		terminate = true;
	}

	public boolean isPaused() {
		return false;
	}
	
	public boolean isNative() {
		return false;
	}

	public IParameter getReturnValue(String name) {
		return RETVALMAP.get(name);
	}

	public IAlgorithm newInstance() {
		return new AdaptiveTournamentTSP();
	}

	public List<String> getLiterature() {
		List<String> lst = new ArrayList<String>();
		
		lst.add("A. Eiben, J. Smith: <i>Introduction to Evolutionary Computing</i>, 2003.");
		lst.add("H. Sengoku, I. Yoshihara: <i>A Fast TSP Solver Using GA on JAVA</i>, 1998.");
		lst.add("D. Ashlock: <i>Evolutionary Computation for Modeling and Optimization</i>, 2005.");
		
		return lst;
	}

	public List<IParameter> getRunProperties() {
		return null;
	}

	public void setRunProperty(String key, Object value) {
	}

	public Map<String, IValue> getDefaultRunProperties() {
		return null;
	}

	public Map<Class<? extends ISerializable>, Class<? extends IValueRenderer>> getRenderers() {
		return null;
	}
	
}














