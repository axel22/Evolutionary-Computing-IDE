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
import hr.fer.zemris.ga_framework.model.impl.Value;
import hr.fer.zemris.ga_framework.model.impl.criteriums.GreaterThanIntegerCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.IntervalRealCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.PositiveIntegerCriterium;
import hr.fer.zemris.ga_framework.model.impl.criteriums.PositiveRealCriterium;
import hr.fer.zemris.ga_framework.model.impl.parameters.PrimitiveParameter;
import hr.fer.zemris.ga_framework.model.impl.parameters.SerializableParameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GenerationalTSP extends AbstractTSP {
	
	/* static fields */
	private static final double GOLDBERG_C_VALUE = 2.0;
	private static final int SEND_INFO_CONSTANT = 200;
	private static final List<IParameter> PARAMETERS = new ArrayList<IParameter>();
	private static final Map<String, IParameter> PARAMETER_MAP = new HashMap<String, IParameter>();
	private static final List<IParameter> RETURN_VALUES = new ArrayList<IParameter>();
	private static final Map<String, IParameter> RETVALMAP = new HashMap<String, IParameter>();
	private static final Map<Class<? extends ISerializable>, Class<? extends IParameterDialog>> EDITORS = 
		new HashMap<Class<? extends ISerializable>, Class<? extends IParameterDialog>>();
	
	static {
		List<Object> sel_types = new ArrayList<Object>();
		sel_types.add("Selection");
		sel_types.add("Elimination");
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
		ICriterium between0and1Real = new IntervalRealCriterium(0.0, 1.0);
		ICriterium positiveInt = new PositiveIntegerCriterium();
		ICriterium positiveReal = new PositiveRealCriterium();
		ICriterium greaterThan2 = new GreaterThanIntegerCriterium(2);
		
		// init parameters
		PARAMETERS.add(new SerializableParameter("Problem definition", "A list of cities and their mutual distances.", CityTable.class));
		PARAMETERS.add(new PrimitiveParameter("Population size", "Number of individuals in each generation.", ParameterTypes.INTEGER, greaterThan2));
		PARAMETERS.add(new PrimitiveParameter("Selection type", "Selection model - states whether individuals are chosen or eliminated for the next generation",
				ParameterTypes.STRING, sel_types));
		PARAMETERS.add(new PrimitiveParameter("Elitism", "Determines whether or not the best individual in the population is always kept.", ParameterTypes.BOOLEAN));
		PARAMETERS.add(new PrimitiveParameter("Pressure", "Ratio of the population replaced in each generation. At least one individual will always remain, and at least one individual will be replaced.", ParameterTypes.REAL, between0and1Real));
		PARAMETERS.add(new PrimitiveParameter("Crossover operator", "Operator used for crossover.", ParameterTypes.STRING, crossovers));
		PARAMETERS.add(new PrimitiveParameter("Mutation operator", "Operator used for mutation.", ParameterTypes.STRING, mutations));
		PARAMETERS.add(new PrimitiveParameter("Mutation probability", "Probability that mutation is applied.", ParameterTypes.REAL, between0and1Real));
		PARAMETERS.add(new PrimitiveParameter("Generations", "Maximum number of generations to perform.", ParameterTypes.INTEGER, positiveInt));
		PARAMETERS.add(new PrimitiveParameter("Expected cost", "Expected permutation cost. Once a solution with a cost less than the specified is found, the algorithm " +
				"will stop. If no expected cost is known, this may be set to 0.0.", ParameterTypes.REAL, positiveReal));
		
		for (IParameter p : PARAMETERS) {
			PARAMETER_MAP.put(p.getName(), p);
		}
		
		// init return values
		RETURN_VALUES.add(new PrimitiveParameter("City permutation", "Optimal city permutation found during the run.", ParameterTypes.STRING));
		RETURN_VALUES.add(new PrimitiveParameter("Cost", "The total cost of visiting all the cities in the optimal order.", ParameterTypes.REAL));
		RETURN_VALUES.add(new PrimitiveParameter("Total generations", "Total number of iterations performed before reaching iteration limit or expected solution cost.", ParameterTypes.INTEGER));
		RETURN_VALUES.add(new PrimitiveParameter("Success measure", "Success of the run - 0.0 if expected cost isn't reached, and 1.0 if it is.", ParameterTypes.REAL));
		
		for (IParameter p : RETURN_VALUES) {
			RETVALMAP.put(p.getName(), p);
		}
		
		// init editor map
		EDITORS.put(CityTable.class, CitySelectPane.class);
	}
	
	/* private fields */
	private volatile boolean terminate;
	private volatile boolean running;
	
	/* ctors */
	
	public GenerationalTSP() {
		terminate = false;
		running = false;
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

	public Map<String, IValue> getDefaultValues() {
		Map<String, IValue> map = new HashMap<String, IValue>();
		
		map.put(PARAMETERS.get(0).getName(), new Value(new CityTable(1, 620, 330), PARAMETERS.get(0)));
		map.put(PARAMETERS.get(1).getName(), new Value(200, PARAMETERS.get(1)));
		map.put(PARAMETERS.get(2).getName(), new Value("Selection", PARAMETERS.get(2)));
		map.put(PARAMETERS.get(3).getName(), new Value(true, PARAMETERS.get(3)));
		map.put(PARAMETERS.get(4).getName(), new Value(0.45, PARAMETERS.get(4)));
		map.put(PARAMETERS.get(5).getName(), new Value("Order", PARAMETERS.get(5)));
		map.put(PARAMETERS.get(6).getName(), new Value("Inversion", PARAMETERS.get(6)));
		map.put(PARAMETERS.get(7).getName(), new Value(0.05, PARAMETERS.get(7)));
		map.put(PARAMETERS.get(8).getName(), new Value(5000, PARAMETERS.get(8)));
		map.put(PARAMETERS.get(9).getName(), new Value(0.0, PARAMETERS.get(9)));
		
		return map;
	}

	public String getDescription() {
		return "Implementation of the generational genetic algorithm for the Travelling salesman problem. " +
			"Given the list of cities and their mutual distances, finds the optimal round-trip in terms of total " +
			"distance. The algorithm can use elimination or generational selection. " +
			"It offers a wide choice of crossover and mutation operators.";
	}

	public Map<Class<? extends ISerializable>, Class<? extends IParameterDialog>> getEditors() {
		return EDITORS;
	}

	public String getExtensiveInfo() {
		InputStream stream = this.getClass().getResourceAsStream("GenerationalTSP.html");
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

	public List<String> getLiterature() {
		List<String> lst = new ArrayList<String>();
		
		lst.add("A. Eiben, J. Smith: <i>Introduction to Evolutionary Computing</i>, 2003.");
		lst.add("H. Sengoku, I. Yoshihara: <i>A Fast TSP Solver Using GA on JAVA</i>, 1998.");
		
		return lst;
	}

	public String getName() {
		return "Generational TSP GA";
	}

	public IParameter getParameter(String name) {
		return PARAMETER_MAP.get(name);
	}

	public List<IParameter> getParameters() {
		return PARAMETERS;
	}

	public IParameter getReturnValue(String name) {
		return RETVALMAP.get(name);
	}

	public List<IParameter> getReturnValues() {
		return RETURN_VALUES;
	}

	public void haltAlgorithm() {
		terminate = true;
	}

	public boolean isNative() {
		return false;
	}

	public boolean isPausable() {
		return false;
	}

	public boolean isPaused() {
		return false;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isSaveable() {
		return false;
	}

	public void load(String s) {
		throw new UnsupportedOperationException("This algorithm does not support loading.");
	}

	public IAlgorithm newInstance() {
		return new GenerationalTSP();
	}

	public Map<String, IValue> runAlgorithm(Map<String, IValue> values, IInfoListener listener) {
		Map<String, IValue> retval = null;
		
		try {
			terminate = false;
			running = true;
			retval = new HashMap<String, IValue>();
			
			// extract values from parameter map
			final CityTable cities = values.get("Problem definition").value();
			Integer popsize = values.get("Population size").value();
			String seltype = values.get("Selection type").value();
			String crossovername = values.get("Crossover operator").value();
			String mutationname = values.get("Mutation operator").value();
			Double mutprob = values.get("Mutation probability").value();
			Integer generations = values.get("Generations").value();
			Double expectedcost = values.get("Expected cost").value();
			Boolean elitism = values.get("Elitism").value();
			Double pressure = values.get("Pressure").value();
			temptable = cities;
			
			// initialize
			int modified_send_info_per = SEND_INFO_CONSTANT + cities.getCityNum() / 3;
			boolean isSelection = seltype.equals("Selection");
			int numcities = cities.getCityNum();
			int poolsize = (int)(popsize * (1 - pressure));
			if (poolsize < 1) poolsize = 1;
			if (poolsize >= popsize) poolsize = popsize - 1;
			double[] roulettewheel = new double[popsize];
			Permutation[] population = new Permutation[popsize];
			Permutation[] matingpool = new Permutation[poolsize];
			Random rand = new Random();
			Permutation bestperm = null, beforeperm = null;
			double lowestcost = Double.MAX_VALUE, highestcost = Double.MIN_VALUE, mean = 0.0;
			ICrossover crossover = TSPUtility.createCrossover(numcities, crossovername);
			IMutation mutation = TSPUtility.createMutation(numcities, mutationname, cities);
			
			// initialize and evaluate population
			for (int i = 0; i < popsize; i++) {
				Permutation p = new Permutation(numcities, true);
				p.cost = fitness(p);
				population[i] = p;
				mean += p.cost;
				
				if (p.cost < lowestcost) {
					bestperm = p;
					lowestcost = p.cost;
				}
				if (p.cost > highestcost) {
					highestcost = p.cost;
				}
			}
			mean = mean / popsize;
			
			// send info about the best solution
			if (listener != null) {
				listener.println("ALGORITHM STARTING.");
				listener.setProperty("Mating pool size", String.valueOf(poolsize));
				sendInfoAboutBest(listener, bestperm, lowestcost);
				sendInfoPercentage(listener, 0.0);
				initCanvas(listener, cities);
				listener.println("Zoom factor used: " + getZoomFactor());
			}
			
			// now iterate
			int generationCount = 0;
			for (; generationCount < generations && !terminate && lowestcost > expectedcost; generationCount++) {
				// create wheel - find standard deviation and then build roulette wheel, using
				// Goldberg's sigma scaling
				double stdev = 0.0;
				for (int i = 0; i < popsize; i++) {
					double q = population[i].cost - mean;
					stdev += q * q;
				}
				stdev /= popsize;
				stdev = Math.sqrt(stdev);
				
				// check whether it's time to send info (if this is needed)
				if (listener != null && generationCount % modified_send_info_per == 0) {
					if (!bestperm.equals(beforeperm)) {
						sendInfoAboutBest(listener, bestperm, lowestcost);
						redrawImage(listener, bestperm, cities, "");
						beforeperm = bestperm;
					}
					sendInfoPercentage(listener, 1.0 * generationCount / generations);
					listener.setProperty("Maximum cost", String.valueOf(highestcost));
					listener.setProperty("Fitness mean", String.valueOf(mean));
					listener.setProperty("Fitness st. deviation", String.valueOf(stdev));
				}
				
				double totalval = 0.0, last = 0.0;
				if (isSelection) {
					// selection used for next generation
					double bias = mean + GOLDBERG_C_VALUE * stdev;
					for (int i = 0; i < popsize; i++) {
						double quality = bias - population[i].cost;
						if (quality < 0.0) quality = 0.0;
						last = (roulettewheel[i] = last + quality);
						totalval += quality;
					}
				} else for (int i = 0; i < popsize; i++) {
					// elimination used for next generation
					double bias = mean - GOLDBERG_C_VALUE * stdev;
					double poverty = population[i].cost - bias;
					if (poverty < 0.0) poverty = 0.0;
					roulettewheel[i] = poverty;
					totalval += poverty;
				}
				
				// depending on selection type, select or eliminate individuals
				// and be sure to keep track of the new best individual here, depending
				// on selection type and whether elitism is used
				if (isSelection) {
					// perform SUS algorithm and place individuals from the mating pool back to population
					int current = 0, i = 0;
					double r = rand.nextDouble() * totalval / poolsize;
					while (current < poolsize) {
						while (r <= roulettewheel[i] && current < poolsize) {
							matingpool[current] = new Permutation(population[i]);
							r += totalval / poolsize;
							current++;
						}
						i++;
					}
					
					boolean newBestFound = false;
					if (!elitism) lowestcost = Double.MAX_VALUE;
					for (int k = 0; k < poolsize; k++) {
						population[k] = matingpool[k];
						if (population[k].cost <= lowestcost) {
							newBestFound = true;
							bestperm = population[k];
							lowestcost = population[k].cost;
						}
					}
					if (!newBestFound && elitism) {
						// ensure that the best solution remains in the population
						population[rand.nextInt(poolsize)] = bestperm;
					}
				} else {
					// perform roulette wheel elimination (do it poolsize times)
					int current = 0, lastelem = popsize - 1;
					while (current < poolsize) {
						double r = rand.nextDouble() * totalval;
						int i = 0;
						while (r > roulettewheel[i]) {
							r -= roulettewheel[i];
							i++;
						}
						totalval -= roulettewheel[i];
						population[i] = population[lastelem];
						roulettewheel[i] = roulettewheel[lastelem];
						lastelem--;
						current++;
					}
					
					// find the new best solution
					boolean newBestFound = false;
					if (!elitism) lowestcost = Double.MAX_VALUE;
					for (int k = 0; k < poolsize; k++) {
						if (population[k].cost <= lowestcost) {
							newBestFound = true;
							bestperm = population[k];
							lowestcost = population[k].cost;
						}
					}
					if (!newBestFound && elitism) {
						// ensure that the best solution stays in the population
						population[rand.nextInt(poolsize)] = bestperm;
					}
				}
				
				// perform crossovers and mutations, evaluating new individuals, tracking the best individual
				for (int i = poolsize; i < popsize; i++) {
					// choose two random parents from the mating pool
					Permutation par1 = population[rand.nextInt(poolsize)];
					Permutation par2 = population[rand.nextInt(poolsize)];
					
					// perform crossover to create offspring
					Permutation child = Permutation.createEmptyPermutation(numcities);
					crossover.crossover(par1, par2, child);
					population[i] = child;
					
					// perform mutation
					if (rand.nextDouble() < mutprob) mutation.mutate(child, -1);
					
					// evaluate child
					child.cost = fitness(child);
					
					// check the quality of the offspring
					if (child.cost < lowestcost) {
						bestperm = child;
						lowestcost = child.cost;
					}
				}
				
				// calculate mean fitness
				highestcost = Double.MIN_VALUE;
				mean = 0;
				for (int i = 0; i < popsize; i++) {
					mean += population[i].cost;

					if (population[i].cost > highestcost) {
						highestcost = population[i].cost;
					}
				}
				mean /= popsize;
			}
			
			// prepare return values
			retval.put(RETURN_VALUES.get(0).getName(), new Value(bestperm, RETURN_VALUES.get(0)));
			retval.put(RETURN_VALUES.get(1).getName(), new Value(lowestcost, RETURN_VALUES.get(1)));
			retval.put(RETURN_VALUES.get(2).getName(), new Value(generationCount, RETURN_VALUES.get(2)));
			retval.put(RETURN_VALUES.get(3).getName(), new Value((lowestcost <= expectedcost) ? 1.0 : 0.0,
					RETURN_VALUES.get(3)));
			
			// send info before ending job
			if (listener != null && !bestperm.equals(beforeperm)) {
				sendInfoAboutBest(listener, bestperm, lowestcost);
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
		
		return retval;
	}

	public String save() {
		throw new UnsupportedOperationException("This algorithm does not support saving.");
	}

	public void setPaused(boolean on) {
		throw new UnsupportedOperationException("This algorithm does not support pausing.");
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














