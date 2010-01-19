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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



public class TournamentTSP extends AbstractTSP {
	
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
		mutations.add("2 opt");
		mutations.add("GShift");
		
		// init params
		PARAMETERS.add(new SerializableParameter("Problem definition", "A list of cities and respective distances between them.", CityTable.class));
		PARAMETERS.add(new PrimitiveParameter("Population size", "Size of the population.", ParameterTypes.INTEGER, greaterThan2));
		PARAMETERS.add(new PrimitiveParameter("Tournament size", "Number of individuals selected for the tournament.", ParameterTypes.INTEGER, positiveInt));
		PARAMETERS.add(new PrimitiveParameter("Pressure", "Number of individuals eliminated in each tournament. Entering a value greater or equal to tournament size will always result in one surviving individual.", ParameterTypes.INTEGER, positiveInt));
		PARAMETERS.add(new PrimitiveParameter("Crossover operator", "Operator used for crossover.", ParameterTypes.STRING, crossovers));
		PARAMETERS.add(new PrimitiveParameter("Mutation operator", "Operator used for mutation.", ParameterTypes.STRING, mutations));
		PARAMETERS.add(new PrimitiveParameter("Mutation probability", "Probability that mutation will occur on a newly created offspring.", ParameterTypes.REAL, between0and1));
		PARAMETERS.add(new PrimitiveParameter("Iterations", "Maximum number of iterations (tournament selections) algorithm will perform.", ParameterTypes.INTEGER, positiveInt));
		PARAMETERS.add(new PrimitiveParameter("Expected cost", "Expected permutation cost. Once a solution with a cost less than the specified is found, " +
				"the algorithm will stop. If no expected cost is known, this may be set to 0.0.", ParameterTypes.REAL, positiveReal));
		
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
	
	public TournamentTSP() {
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
		
		return map;
	}

	public String getDescription() {
		return "Implementation of the Travelling salesman problem genetic algorithm. Given the list " +
				"of cities and their mutual distances, finds " +
				"the permutation of the cities so that the sum of the distances " +
				"between the neighbouring cities within the permutation is minimal. " +
				"The algorithm uses tournament elimination, with a wide choice crossover " +
				"and mutation operators.";
	}

	public String getExtensiveInfo() {
		InputStream stream = this.getClass().getResourceAsStream("TournamentTSP.html");
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
		return "Tournament TSP GA";
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
			Permutation[] population = new Permutation[popsize];
			Permutation[] tournament = new Permutation[toursize];
			temptable = cities;
			
			// initialize and evaluate population, keep track of the best
			double bestval = Double.MAX_VALUE;
			Permutation bestperm = null, beforeperm = null;
			for (int i = 0; i < popsize; i++) {
				population[i] = new Permutation(permlength, true);
				population[i].cost = fitness(population[i]);
				if (population[i].cost < bestval) {
					bestval = population[i].cost;
					beforeperm = bestperm;
					bestperm = population[i];
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
					
					// perform mutation with some probability
					if (rand.nextDouble() < mutprob) {
						mutation.mutate(child, -1);
					}
					
					// evaluate offspring, keeping track of the best
					child.cost = fitness(child);
					if (child.cost < bestval) {
						bestval = child.cost;
						beforeperm = bestperm;
						bestperm = child;
					}
					
					// put child to tournament
					tournament[j] = child;
				}
				
				// add offspring and tournament members back to population
				for (int i = 0; i < toursize; i++) {
					population[popsize - i - 1] = tournament[i];
				}
				
				// send info if that is required
				if (listener != null && (counter % modified_send_info_per == 0)) {
					if (!bestperm.equals(beforeperm)) {
						sendInfoAboutBest(listener, bestperm, bestval);
						redrawImage(listener, bestperm, cities, "");
						beforeperm = bestperm;
					}
				}
				if (listener != null && (counter % PERCENTAGE_SEND_PERIOD == 0)) {
					sendInfoPercentage(listener, counter * 1.0 / iterations);
				}
			}
			
			// prepare return values
			retmap.put(RETURNVALUES.get(0).getName(), new Value(bestperm.toString(), RETURNVALUES.get(0)));
			retmap.put(RETURNVALUES.get(1).getName(), new Value(bestperm.cost, RETURNVALUES.get(1)));
			retmap.put(RETURNVALUES.get(2).getName(), new Value(counter, RETURNVALUES.get(2)));
			retmap.put(RETURNVALUES.get(3).getName(), new Value((bestperm.cost <= expectedcost) ? 1.0 : 0.0, RETURNVALUES.get(3)));
			
			// send info if that is required
			if (listener != null && !bestperm.equals(beforeperm)) {
				sendInfoAboutBest(listener, bestperm, bestval);
				redrawImage(listener, bestperm, cities, "");
				beforeperm = bestperm;
				listener.println("ALGORITHM ENDING.");
			}
		} finally {
			running = false;
			if (terminate) {
//				System.out.println("Someone terminated the algorithm.");
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
//		System.out.println(Arrays.toString(Thread.getAllStackTraces().get(Thread.currentThread())));
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
		return new TournamentTSP();
	}

	public List<String> getLiterature() {
		List<String> lst = new ArrayList<String>();
		
		lst.add("A. Eiben, J. Smith: <i>Introduction to Evolutionary Computing</i>, 2003.");
		lst.add("H. Sengoku, I. Yoshihara: <i>A Fast TSP Solver Using GA on JAVA</i>, 1998.");
		
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














